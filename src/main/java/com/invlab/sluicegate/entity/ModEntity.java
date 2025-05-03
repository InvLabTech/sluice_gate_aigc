// ModEntities.java
package com.invlab.sluicegate.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.invlab.sluicegate.entity.SluiceGateEntity;

public class ModEntity {
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "sluice_gate");

    public static final RegistryObject<EntityType<SluiceGateEntity>> SLUICE_GATE = ENTITIES.register(
            "sluice_gate",
            () -> EntityType.Builder.<SluiceGateEntity>of(SluiceGateEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f) // Default size, will be adjusted
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build("sluice_gate:sluice_gate"));
}