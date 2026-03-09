package io.github.rojoloco04.rojosmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/**
 * Renders the lightning spear entity in flight.
 * Reuses ThrownTridentRenderState (yRot, xRot, isFoil) since we need the same fields.
 */
public class LightningSpearRenderer extends EntityRenderer<LightningSpearEntity, ThrownTridentRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        rojosmod.MODID, "textures/entity/lightning_spear.png"
    );
    private final LightningSpearModel model;

    public LightningSpearRenderer(EntityRendererProvider.Context context) {
        super(context);
        // Bake the model geometry registered in LightningSpearModel.createBodyLayer()
        this.model = new LightningSpearModel(context.bakeLayer(LightningSpearModel.LAYER));
    }

    @Override
    public ThrownTridentRenderState createRenderState() {
        return new ThrownTridentRenderState();
    }

    @Override
    public void extractRenderState(LightningSpearEntity entity, ThrownTridentRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot(partialTick);
        state.xRot = entity.getXRot(partialTick);
        state.isFoil = false; // enchantment glint handled separately if needed
    }

    @Override
    public void submit(ThrownTridentRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        // +180° on Y: the Blockbench model tip points toward +Z, so flip it to face the direction of travel
        // No +90° on X (unlike ThrownTridentRenderer) because the model is already horizontal
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot + 180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));

        List<RenderType> renderTypes = ItemRenderer.getFoilRenderTypes(this.model.renderType(TEXTURE), false, state.isFoil);
        for (int i = 0; i < renderTypes.size(); i++) {
            collector.order(i).submitModel(
                this.model, Unit.INSTANCE, poseStack, renderTypes.get(i),
                state.lightCoords, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null
            );
        }

        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }
}
