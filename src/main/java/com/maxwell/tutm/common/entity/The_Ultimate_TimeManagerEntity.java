package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.common.network.UpdateBossBarPacket;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AutoRegisterEntity(
        name = "the_ultimate_time_manager",
        width = 0.6f, height = 2.2f,
        renderer = "com.maxwell.tutm.client.renderer.The_Ultimate_Time_ManagerRenderer"
)
public class The_Ultimate_TimeManagerEntity extends Monster {
    public static final Set<The_Ultimate_TimeManagerEntity> ACTIVE_BOSSES = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final EntityDataAccessor<Boolean> IS_ALLY = SynchedEntityData.defineId(The_Ultimate_TimeManagerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SECOND_FORM = SynchedEntityData.defineId(The_Ultimate_TimeManagerEntity.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    private final Set<UUID> trackingPlayers = new HashSet<>();
    private float lastHealth;
    private boolean lastSecondFormState;
    private boolean hasUsedSupernova = false;
    private int teleportCooldown = 0;
    private int timeStopCooldown = 200;
    private long lastRealWorldTime = 0;
    public void absoluteRealTimeTick(long currentRealTime) {
        if (currentRealTime - this.lastRealWorldTime < 50) {
            return;
        }
        this.lastRealWorldTime = currentRealTime;

        if (!this.isRemoved()) {
            // Note: super.tick() already calls aiStep() via baseTick()
            super.tick();
            if (this.level().isClientSide) {
                this.xo = this.getX();
                this.yo = this.getY();
                this.zo = this.getZ();
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();
            }
        }
    }
    public The_Ultimate_TimeManagerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lastHealth = -1.0F;
        this.lastSecondFormState = false;
        this.setNoGravity(true);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.FLYING_SPEED, 0.6D);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // スーパーノヴァのチャージ中は完全無敵（ラストスタンドの絶対保証）
        if (this.currentAttackState == AttackState.SUPERNOVA) {
            return false;
        }

        // スーパーノヴァをまだ使っていない場合、致死ダメージを食いしばって強制発動する
        if (!hasUsedSupernova) {
            if (this.getHealth() - amount <= 1.0f) {
                this.setHealth(1.0f);
                this.currentAttackState = AttackState.SUPERNOVA;
                this.stateTimer = 0;
                this.hasUsedSupernova = true;
                return false;
            }
        }

        return super.hurt(source, amount);
    }

    private enum AttackState {
        IDLE, LASER_BURST, DIVINE_WAVE, HOMING_SHOT, SUPERNOVA, CHRONOS_GEAR
    }
    private AttackState currentAttackState = AttackState.IDLE;
    private int stateTimer = 0;
    private int laserCount = 0;

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            super.aiStep();
            return;
        }
        float healthPercent = this.getHealth() / this.getMaxHealth();
        int factor = 1;
        if (healthPercent <= 0.25F) factor = 4;
        else if (healthPercent <= 0.50F) factor = 3;
        else if (healthPercent <= 0.75F) factor = 2;
        BossTimeManager.setAccelFactor(factor);

        timeStopCooldown--;
        if (!BossTimeManager.isTimeStopped() && timeStopCooldown <= 0 && this.getTarget() != null) {
            if (this.random.nextFloat() < 0.05F) {
                BossTimeManager.requestBossMode(
                        (ServerLevel) this.level(),
                        isSecondForm() ? BossTimeMode.ABSOLUTE_STOP : BossTimeMode.STOPPED,
                        200,
                        1
                );
                timeStopCooldown = ModConfig.BOSS_TIME_STOP_COOLDOWN.get();
            }
        }

        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            Player nearestPlayer = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 64.0D, false);
            if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                this.setTarget(nearestPlayer);
            }
        }

        this.setNoGravity(true);
        LivingEntity target = this.getTarget();
        
        // 常に定期的にホーミングレーザー（魔法弾）を撃つ（スーパーノヴァ時以外）
        if (target != null && currentAttackState != AttackState.SUPERNOVA) {
            if (this.tickCount % 80 == 0) { // 4秒ごとに発射
                TemporalHomingEntity ball = new TemporalHomingEntity(this.level(), this, target);
                this.level().addFreshEntity(ball);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.ILLUSIONER_CAST_SPELL, net.minecraft.sounds.SoundSource.HOSTILE, 0.8F, 1.5F);
            }
        }
        
        // フライバフの付与（周囲のプレイヤー）
        for (UUID uuid : this.trackingPlayers) {
            Player p = this.level().getPlayerByUUID(uuid);
            if (p != null) {
                if (!p.isCreative() && !p.isSpectator() && !p.getAbilities().mayfly) {
                    p.getAbilities().mayfly = true;
                    p.onUpdateAbilities();
                }
            }
        }
        
        if (target != null) {
            handleFlightMovement(target);
            updateAttackSequence(target);
        } else {
            double hoverY = Math.sin(this.tickCount * 0.1) * 0.02;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5).add(0, hoverY, 0));
            currentAttackState = AttackState.IDLE;
        }
        super.aiStep();
    }


    private void updateAttackSequence(LivingEntity target) {
        stateTimer++;
        
        // HPが微量(2%以下)になったら強制的にスーパーノヴァモードへ突入（ラストスタンド）
        if (!hasUsedSupernova && this.getHealth() <= Math.max(10.0f, this.getMaxHealth() * 0.02f)) {
            currentAttackState = AttackState.SUPERNOVA;
            stateTimer = 0;
            hasUsedSupernova = true;
            return;
        }

        switch (currentAttackState) {
            case IDLE -> {
                if (stateTimer > 40) {
                    currentAttackState = AttackState.LASER_BURST;
                    stateTimer = 0;
                    laserCount = 0;
                }
            }
            case LASER_BURST -> {
                int fireRate = isSecondForm() ? 10 : 15;
                if (stateTimer % fireRate == 0) {
                    shootConstantLaser(target);
                    laserCount++;
                    if (laserCount >= 5) {
                        currentAttackState = AttackState.DIVINE_WAVE;
                        stateTimer = 0;
                    }
                }
            }
            case DIVINE_WAVE -> {
                if (stateTimer == 10) {
                    DivineWaveEntity wave = new DivineWaveEntity(this.level(), this);
                    this.level().addFreshEntity(wave);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.WITHER_SHOOT, net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.5F);
                }
                if (stateTimer > 40) {
                    currentAttackState = AttackState.HOMING_SHOT;
                    stateTimer = 0;
                }
            }
            case HOMING_SHOT -> {
                if (stateTimer % 15 == 0) {
                    TemporalHomingEntity ball = new TemporalHomingEntity(this.level(), this, target);
                    this.level().addFreshEntity(ball);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.ILLUSIONER_CAST_SPELL, net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.2F);
                    if (stateTimer >= 45) {
                        currentAttackState = AttackState.CHRONOS_GEAR; // 通常ローテは歯車へ直行
                        stateTimer = 0;
                    }
                }
            }
            case SUPERNOVA -> {
                if (stateTimer == 1) {
                    // スーパーノヴァ召喚
                    ChronosSupernovaEntity supernova = new ChronosSupernovaEntity(this.level(), this, this.getX(), this.getY() + 2.0, this.getZ());
                    this.level().addFreshEntity(supernova);
                    // 警告音（ウィザーのスポーン音などのド派手な音）
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.WITHER_SPAWN, net.minecraft.sounds.SoundSource.HOSTILE, 3.0F, 0.5F);
                }
                // チャージ中はボスが激しく回転しつつ静止する
                this.setYRot(this.getYRot() + 20.0f);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();

                if (stateTimer >= ChronosSupernovaEntity.CHARGE_TIME + 20) { // 爆発後少し待ってから次へ
                    currentAttackState = AttackState.CHRONOS_GEAR;
                    stateTimer = 0;
                }
            }

            case CHRONOS_GEAR -> {
                if (stateTimer == 1) {
                    int gearCount = isSecondForm() ? 6 : 4;
                    for (int i = 0; i < gearCount; i++) {
                        double angle = i * (Math.PI * 2.0 / gearCount);
                        ChronosGearEntity gear = new ChronosGearEntity(this.level(), this, this.position().add(Math.cos(angle)*4, 1.5, Math.sin(angle)*4));
                        this.level().addFreshEntity(gear);
                    }
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), net.minecraft.sounds.SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 1.5F);
                }
                if (stateTimer > 60) {
                    currentAttackState = AttackState.IDLE;
                    stateTimer = 0;
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        if (isSecondForm()) {
            return Component.translatable("entity.tutm.the_ultimate_time_manager.phase2");
        }
        return Component.translatable("entity.tutm.the_ultimate_time_manager");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SECOND_FORM, false);
        this.entityData.define(IS_ALLY, false); 
    }
    public boolean isSecondForm() {
        return this.entityData.get(IS_SECOND_FORM);
    }

    public void setSecondForm(boolean secondForm) {
        this.entityData.set(IS_SECOND_FORM, secondForm);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // 念のためのtickデスポーン判定（ボスバーから外れたが何らかの理由で残っている場合）
            // ※召喚直後にすぐ消えないように100tick（5秒）の猶予を設ける
            if (this.tickCount > 100 && this.trackingPlayers.isEmpty()) {
                this.discard();
                return;
            }
            // 200ブロック以内にプレイヤーが1人もいない場合（ディメンション移動や遠距離逃亡）、ボスは完全に消滅する
            boolean hasNearby = false;
            for (Player p : this.level().players()) {
                if (p.distanceTo(this) < 200.0) {
                    hasNearby = true;
                    break;
                }
            }
            if (!hasNearby) {
                this.discard();
                return;
            }
        }

        if (this.level().isClientSide()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
        boolean currentForm = this.isSecondForm();
        if (!currentForm && this.getHealth() <= this.getMaxHealth() * 0.5F) {
            this.setSecondForm(true);
            currentForm = true;
        }
        if (this.getHealth() != this.lastHealth || currentForm != this.lastSecondFormState) {
            this.lastHealth = this.getHealth();
            this.lastSecondFormState = currentForm;
            this.broadcastBossBarPacket(true);
        }
        LivingEntity target = this.getTarget();
        if (target != null && !this.level().isClientSide) {
            // スーパーノヴァ中は強制的な視点追従を行わない（超高速回転するため）
            if (this.currentAttackState != AttackState.SUPERNOVA) {
                this.lookAt(target, 15.0F, 15.0F); 
                float yaw = Mth.wrapDegrees(this.getYRot());
                this.setYRot(yaw);
                this.yHeadRot = yaw;
                this.setYBodyRot(yaw);
                this.yRotO = yaw;
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide) {
            ACTIVE_BOSSES.add(this);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(ModConfig.BOSS_MAX_HEALTH.get());
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ModConfig.BOSS_ATTACK_DAMAGE.get());
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ModConfig.BOSS_MOVEMENT_SPEED.get());
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(ModConfig.BOSS_FOLLOW_RANGE.get());
            this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(ModConfig.BOSS_FLYING_SPEED.get());
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!this.level().isClientSide) {
            ACTIVE_BOSSES.remove(this);
        }
    }
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!this.level().isClientSide) {
            this.trackingPlayers.add(player.getUUID());
            sendBossBarPacket(player, true);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.level().isClientSide) {
            this.trackingPlayers.remove(player.getUUID());
            sendBossBarPacket(player, false);
            // プレイヤーが誰も自分を見ていないかつ召喚から5秒以上経過している場合は即座にデスポーンする
            if (this.tickCount > 100 && this.trackingPlayers.isEmpty()) {
                this.discard();
            }
        }
    }


    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            broadcastBossBarPacket(false);
            for (UUID uuid : this.trackingPlayers) {
                Player p = this.level().getPlayerByUUID(uuid);
                if (p != null && !p.isCreative() && !p.isSpectator()) {
                    p.getAbilities().mayfly = false;
                    p.getAbilities().flying = false;
                    p.onUpdateAbilities();
                }
            }
        }
    }


    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        this.spawnAtLocation(new ItemStack(ModItems.INFINITE_TIME_CLOCK.get()));
        this.spawnAtLocation(new ItemStack(ModItems.LUNAR_CHRONO_CLOCK.get()));
    }

    private void sendBossBarPacket(ServerPlayer player, boolean shouldDisplay) {
        UpdateBossBarPacket packet;
        if (shouldDisplay) {
            packet = new UpdateBossBarPacket(true, this.getHealth(), this.getMaxHealth(), this.getDisplayName(), this.isSecondForm());
        } else {
            packet = new UpdateBossBarPacket(false, 0, 1, null, false);
        }
        TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public void setHealth(float pHealth) {
        super.setHealth(pHealth);
    }

    private void broadcastBossBarPacket(boolean shouldDisplay) {
        if (this.level().isClientSide) return;
        UpdateBossBarPacket packet;
        if (shouldDisplay) {
            packet = new UpdateBossBarPacket(true, this.getHealth(), this.getMaxHealth(), this.getDisplayName(), this.isSecondForm());
        } else {
            packet = new UpdateBossBarPacket(false, 0, 1, null, false);
        }
        for (UUID uuid : this.trackingPlayers) {
            ServerPlayer player = (ServerPlayer) this.level().getPlayerByUUID(uuid);
            if (player != null) {
                TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    @Override
    public void registerGoals() {
            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    private void handleFlightMovement(LivingEntity target) {
        // 溜めや攻撃中はピタリと止まる
        if (currentAttackState == AttackState.LASER_BURST) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.3, 0.3));
            return;
        } else if (currentAttackState == AttackState.SUPERNOVA) {
            // スーパーノヴァ中は絶対に動かないように完全に速度を0にする
            this.setDeltaMovement(0, 0, 0);
            return;
        }

        // ウィザー風AI: ターゲットとの適切な距離を保ちつつ正面を取る
        double distance = this.distanceTo(target);
        double desiredDistance = isSecondForm() ? 10.0 : 14.0;
        
        Vec3 targetPos = target.position().add(0, target.getEyeHeight() + 3.0, 0); // ターゲットの少し上を目指す
        Vec3 direction = targetPos.subtract(this.position()).normalize();

        // 速度を下げ、ぴったり張り付かない「ギリギリ」の速度に調整
        double speed = isSecondForm() ? 0.15 : 0.08;

        if (distance > desiredDistance + 3.0) {
            // 近づく
            this.setDeltaMovement(this.getDeltaMovement().add(direction.scale(speed)));
        } else if (distance < desiredDistance - 3.0) {
            // 離れる（後退する速度はさらに遅め）
            this.setDeltaMovement(this.getDeltaMovement().add(direction.scale(-speed * 0.5)));
        } else {
            // ホバリング・僅かな左右揺れ
            double strafe = Math.sin(this.tickCount * 0.05) * speed * 0.4;
            Vec3 rightDir = direction.yRot((float)Math.PI / 2);
            this.setDeltaMovement(this.getDeltaMovement().add(rightDir.scale(strafe)));
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(0.85, 0.85, 0.85));
        
        // ターゲットをジンワリと追従
        this.lookAt(target, 15.0F, 15.0F);


        teleportCooldown--;
        if (teleportCooldown <= 0) {
            if (distance > 32.0) {
                this.moveTo(target.getX(), target.getY() + 6.0, target.getZ());
                teleportCooldown = 100;
            } else if (distance < 5.0) {
                Vec3 behind = target.getLookAngle().reverse().scale(10);
                this.moveTo(target.getX() + behind.x, target.getY() + 5.0, target.getZ() + behind.z);
                teleportCooldown = 60;
            }
        }
    }

    private void shootConstantLaser(LivingEntity target) {
        Vec3 targetPos = target.getBoundingBox().getCenter();
        double distance = this.distanceTo(target);
        Vec3 targetVel = target.getDeltaMovement();

        double leadTime = distance * 0.4;
        Vec3 predictedPos = targetPos.add(targetVel.scale(leadTime));

        Vec3 lookDir = this.getLookAngle();
        Vec3 spawnPos = this.getEyePosition().subtract(lookDir.scale(5.0));

        int totalLasers = isSecondForm() ? 6 : 4;

        for (int i = 0; i < totalLasers; i++) {
            TemporalLaserEntity laser;

            if (i == 0) {
                laser = new TemporalLaserEntity(this.level(), this, spawnPos, targetPos, 0);
                laser.setTrackingTarget(target); 
            } else {
                double spread = isSecondForm() ? 2.5 : 1.5;
                Vec3 spreadPos = predictedPos.add(
                        (random.nextDouble() - 0.5) * spread * (distance * 0.1),
                        (random.nextDouble() - 0.5) * spread * (distance * 0.1),
                        (random.nextDouble() - 0.5) * spread * (distance * 0.1)
                );
                laser = new TemporalLaserEntity(this.level(), this, spawnPos, spreadPos, 0);
            }

            this.level().addFreshEntity(laser);
        }
    }
}