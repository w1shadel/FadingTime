package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TUTM.MODID);
    public static final Map<RegistryObject<? extends EntityType<?>>, String> RENDERER_MAP = new HashMap<>();
    private static final Map<Class<?>, RegistryObject<? extends EntityType<?>>> CLASS_TO_TYPE = new HashMap<>();

    public static void autoRegister() {
        String annotationDescriptor = Type.getDescriptor(AutoRegisterEntity.class);
        ModFileScanData scanData = ModList.get().getModFileById(TUTM.MODID).getFile().getScanResult();
        scanData.getAnnotations().stream()
                .filter(data -> annotationDescriptor.equals(data.annotationType().getDescriptor()))
                .forEach(data -> {
                    try {
                        String entityClassName = data.clazz().getClassName();
                        Class<?> clazz = Class.forName(entityClassName);
                        AutoRegisterEntity config = clazz.getAnnotation(AutoRegisterEntity.class);
                        if (config == null) return;
                        RegistryObject<EntityType<?>> reg = ENTITIES.register(config.name(), () ->
                                EntityType.Builder.of((type, level) -> {
                                            try {
                                                return (Entity) clazz.getConstructor(EntityType.class, net.minecraft.world.level.Level.class)
                                                        .newInstance(type, level);
                                            } catch (Exception e) {
                                                throw new RuntimeException("Entity construction failed for " + entityClassName, e);
                                            }
                                        }, config.category())
                                        .sized(config.width(), config.height())
                                        .build(config.name())
                        );
                        CLASS_TO_TYPE.put(clazz, reg);
                        if (!config.renderer().isEmpty()) {
                            RENDERER_MAP.put(reg, config.renderer());
                        }

                    } catch (Exception e) {
                        TUTM.LOGGER.error("Failed to auto-register entity: " + data.clazz().getClassName(), e);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> get(Class<T> clazz) {
        RegistryObject<? extends EntityType<?>> reg = CLASS_TO_TYPE.get(clazz);
        if (reg == null) {
            throw new IllegalArgumentException("未登録のエンティティクラスです (サーバー起動時のスキャン失敗の可能性あり): " + clazz.getName());
        }
        return (EntityType<T>) reg.get();
    }
}