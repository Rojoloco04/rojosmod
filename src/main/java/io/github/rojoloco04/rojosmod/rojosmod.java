package io.github.rojoloco04.rojosmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(rojosmod.MODID)
public class rojosmod {

    public static final String MODID = "rojosmod";

    // Deferred registers — populated before the mod constructor runs
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    // The projectile entity — MISC category, small hitbox
    public static final DeferredHolder<EntityType<?>, EntityType<LightningSpearEntity>> LIGHTNING_SPEAR_ENTITY =
        ENTITY_TYPES.register("lightning_spear", id ->
            EntityType.Builder.<LightningSpearEntity>of(LightningSpearEntity::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, id))
        );

    // The item — properties (durability, attributes, lore, etc.) are built in LightningSpearItem
    public static final DeferredItem<LightningSpearItem> LIGHTNING_SPEAR = ITEMS.registerItem(
        "lightning_spear",
        p -> new LightningSpearItem(LightningSpearItem.buildProperties(p))
    );

    // Creative tab containing all mod items
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ROJOSMOD_TAB = CREATIVE_MODE_TABS.register(
        "rojosmod_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.rojosmod"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> LIGHTNING_SPEAR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(LIGHTNING_SPEAR.get());
            })
            .build()
    );

    // mod constructor
    public rojosmod(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
    }
}
