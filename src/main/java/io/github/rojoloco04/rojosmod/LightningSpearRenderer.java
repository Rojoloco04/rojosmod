package io.github.rojoloco04.rojosmod;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class LightningSpearRenderer extends ArrowRenderer<LightningSpearEntity, ArrowRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        rojosmod.MODID, "textures/item/lightning_spear.png"
    );

    public LightningSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState state) {
        return TEXTURE;
    }
}
