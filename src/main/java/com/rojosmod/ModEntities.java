package com.rojosmod;

import com.rojosmod.entity.LightningSpearEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    // Same pattern as ModItems — a DeferredRegister for entity types.
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RojosMod.MODID);

    public static final RegistryObject<EntityType<LightningSpearEntity>> LIGHTNING_SPEAR =
            ENTITIES.register("lightning_spear", () ->
                    EntityType.Builder.<LightningSpearEntity>of(LightningSpearEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)           // hitbox size in world units
                            .clientTrackingRange(4)       // how many chunks away clients track this entity
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(RojosMod.MODID, "lightning_spear"))));
}
