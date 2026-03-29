package com.maxwell.tutm.client;

import com.maxwell.tutm.client.gui.CustomBossBarManager;
import com.maxwell.tutm.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class BossMusicInstance extends AbstractTickableSoundInstance {
    public BossMusicInstance() {
        super(ModSounds.BOSS_MUSIC.get(), SoundSource.MUSIC, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.5F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (!CustomBossBarManager.getShouldDisplay()) {
            this.stop();
        }
    }
}