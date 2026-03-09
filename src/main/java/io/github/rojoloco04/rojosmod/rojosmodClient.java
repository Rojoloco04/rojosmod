package io.github.rojoloco04.rojosmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// Client-only entry point — runs only on the physical client side
@Mod(value = rojosmod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = rojosmod.MODID, value = Dist.CLIENT)
public class rojosmodClient {

    // Register the model layer so the baked model can be looked up by name at render time
    @SubscribeEvent
    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LightningSpearModel.LAYER, LightningSpearModel::createBodyLayer);
    }

    // Bind LightningSpearRenderer to the lightning_spear entity type
    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(rojosmod.LIGHTNING_SPEAR_ENTITY.get(), LightningSpearRenderer::new);
    }
}
