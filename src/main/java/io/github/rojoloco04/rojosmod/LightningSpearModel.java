package io.github.rojoloco04.rojosmod;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/**
 * 3D model for the lightning spear in flight.
 * Geometry exported from Blockbench (lightning_spear.bbmodel), 32x32 UV texture.
 * Extends Model<Unit> (no animation state needed — static projectile model).
 */
public class LightningSpearModel extends Model<Unit> {

    // Layer location used to register and look up this model's baked form
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
        Identifier.fromNamespaceAndPath(rojosmod.MODID, "lightning_spear"), "main"
    );

    public LightningSpearModel(ModelPart root) {
        // entityCutoutNoCull: renders with alpha cutout and no back-face culling,
        // same render type used by TridentModel
        super(root, RenderTypes::entityCutoutNoCull);
    }

    /**
     * Defines the geometry for baking. Called once at startup via RegisterLayerDefinitions.
     * The structure here matches the Blockbench export exactly.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Main body: shaft + crossguard + tip
        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
            CubeListBuilder.create()
                .texOffs(6, 6).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))   // shaft
                .texOffs(7, 19).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))  // crossguard
                .texOffs(10, 25).addBox(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)) // tip base
                .texOffs(8, 1).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),  // tip point
            PartPose.ZERO);

        // Tip side pieces, rotated 90° around Y because I copied the tip point
        bb_main.addOrReplaceChild("cube_r1",
            CubeListBuilder.create()
                .texOffs(15, 1).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, 0.0F, -3.0F, 0.0F, 1.5708F, 0.0F));

        bb_main.addOrReplaceChild("cube_r2",
            CubeListBuilder.create()
                .texOffs(1, 1).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 0.0F, -3.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
