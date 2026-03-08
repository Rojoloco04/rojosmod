package io.github.rojoloco04.rojosmod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(rojosmod.MODID)
public class rojosmod {

    public static final String MODID = "rojosmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    // Blocks
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", p -> p.mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Items
    public static final DeferredItem<Item> SNACK = ITEMS.registerSimpleItem(
        "snack",
        p -> p.food(
            new FoodProperties.Builder()
                .alwaysEdible()
                .nutrition(4)
                .saturationModifier(2f)
                .build()
        )
    );

    public static final ToolMaterial COPPER_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_STONE_TOOL,
        200,
        5f,
        1.5f,
        20,
        Tags.Items.INGOTS_COPPER
    );

    public static final DeferredHolder<EntityType<?>, EntityType<LightningSpearEntity>> LIGHTNING_SPEAR_ENTITY =
        ENTITY_TYPES.register("lightning_spear", id ->
            EntityType.Builder.<LightningSpearEntity>of(LightningSpearEntity::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .build(ResourceKey.create(Registries.ENTITY_TYPE, id))
        );

    public static final DeferredItem<LightningSpearItem> LIGHTNING_SPEAR = ITEMS.registerItem(
        "lightning_spear",
        p -> new LightningSpearItem(
            p.spear(COPPER_MATERIAL, 0.85f, 0.82f, 0.65f, 4.0f, 9.0f, 8.25f, 5.1f, 12.5f, 4.6f)
        )
    );

    // Creative Tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ROJOSMOD_TAB = CREATIVE_MODE_TABS.register(
        "rojosmod_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.rojosmod"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> LIGHTNING_SPEAR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(LIGHTNING_SPEAR.get());
                output.accept(SNACK.get());
                // Add new items here
            })
            .build()
    );

    public rojosmod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Adds the example block item to the vanilla Building Blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
