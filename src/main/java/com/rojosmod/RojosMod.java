package com.rojosmod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RojosMod.MODID)
public final class RojosMod {
    public static final String MODID = "rojosmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RojosMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        // Common setup hook
        FMLCommonSetupEvent.getBus(modBusGroup).addListener(this::commonSetup);

        // Config registration (keep if you still have Config.java)
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{}: common setup", MODID);
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("{}: client setup", MODID);
        }
    }
}
