package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import noppes.mpm.Prop;
import noppes.mpm.Prop.EnumType;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.constants.EnumParts;

public class LayerProp extends LayerInterface {

	public LayerProp(RenderPlayer render) {
		super(render);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		for (int i = 0; i < this.playerdata.propBase.props.size(); i++) {
			renderProp(this.playerdata.propBase.props.get(i), par7);
		}

		for (int i = 0; i < this.playerdata.propGroups.size(); i++) {
			if (this.playerdata.propGroups.get(i).hide)
				continue;

			for (int j = 0; j < this.playerdata.propGroups.get(i).props.size(); j++) {
				renderProp(this.playerdata.propGroups.get(i).props.get(j), par7);
			}
		}
     }

	private void renderProp(Prop prop, float par7) {
		if (prop.type == EnumType.ITEM) {
			renderItemProp(prop, par7);
		} else if (prop.type == EnumType.PARTICLE) {
			renderParticleProp(prop, par7);
		}
	}

	private void renderItemProp(Prop prop, float par7) {
		Boolean propHide = prop.hide;

		if (propHide == false) {
			Minecraft minecraft = Minecraft.getMinecraft();

			ModelRenderer propBodyPart = null;
			ModelRenderer motherRenderer = new ModelRenderer(this.model);
			ModelRenderer propRenderer = new ModelRenderer(this.model);
			motherRenderer.addChild(propRenderer);

			ItemStack propItemStack = prop.itemStack;
			Float propScaleX = prop.scaleX;
			Float propScaleY = prop.scaleY;
			Float propScaleZ = prop.scaleZ;
			Float propOffsetX = -prop.offsetX;
			Float propOffsetY = prop.offsetY;
			Float propOffsetZ = prop.offsetZ;
			Float propRotateX = -prop.rotateX;
			Float propRotateY = prop.rotateY;
			Float propRotateZ = prop.rotateZ;
			Boolean propMatchScaling = prop.matchScaling;

			Float partModifierX = 0.0F;
			Float partModifierY = 0.0F;
			Float partModifierZ = 0.0F;
			EnumParts enumPart = null;

    		 switch(prop.bodyPartName) {
    		     case "hat":
	    		 case "head":
	    			 propBodyPart = this.model.bipedHead;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.HEAD;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY + 0.50F) * this.playerdata.getPartConfig(enumPart).scaleY + 0.20F;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "model":
    			 	 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "body":
	    		 case "torso":
	    			 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "back":
	    			 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.3F) * this.playerdata.getPartConfig(enumPart).scaleY;
					 propOffsetZ = (propOffsetZ - 0.15F) * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "arm":
	    		 case "armleft":
	    		 case "leftarm":
	    			 propBodyPart = this.model.bipedLeftArm;
	    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

	    			 enumPart = EnumParts.ARM_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    		 case "hand":
	    		 case "handleft":
	    		 case "lefthand":
	    			 propBodyPart = this.model.bipedLeftArm;
	    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

	    			 enumPart = EnumParts.ARM_LEFT;

					 propOffsetX = (propOffsetX - 0.0625F) * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "armright":
	    		 case "rightarm":
	    			 propBodyPart = this.model.bipedRightArm;
	    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

	    			 enumPart = EnumParts.ARM_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "handright":
	    		 case "righthand":
	    			 propBodyPart = this.model.bipedRightArm;
	    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

	    			 enumPart = EnumParts.ARM_RIGHT;

					 propOffsetX = (propOffsetX + 0.0625F) * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "leg":
	    		 case "legleft":
	    		 case "leftleg":
	    			 propBodyPart = this.model.bipedLeftLeg;
	    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

	    			 enumPart = EnumParts.LEG_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "foot":
	    		 case "footleft":
	    		 case "leftfoot":
	    			 propBodyPart = this.model.bipedLeftLeg;
	    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

	    			 enumPart = EnumParts.LEG_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "legright":
	    		 case "rightleg":
	    			 propBodyPart = this.model.bipedRightLeg;
	    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

	    			 enumPart = EnumParts.LEG_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "footright":
	    		 case "rightfoot":
	    			 propBodyPart = this.model.bipedRightLeg;
	    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

	    			 enumPart = EnumParts.LEG_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
    		 }

    		 if (propBodyPart == this.model.bipedHead && this.playerdata.player == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0 && !(minecraft.currentScreen instanceof GuiNPCInterface))
			 	return;

			 if (this.player.isSneaking()) {
				 if (propBodyPart == this.model.bipedLeftLeg || propBodyPart == this.model.bipedRightLeg) {
    				 partModifierY += 0.1875F;
    				 partModifierZ -= 0.25F;
				 } else if (propBodyPart == this.model.bipedHead) {
    				 partModifierY -= 0.0625F;
				 }
			 }

    		 Float propOffsetXCorrected;
    		 Float propOffsetYCorrected;
    		 Float propOffsetZCorrected;

    		if (prop.bodyPartName.equals("model")) {
    			propOffsetXCorrected = propOffsetX;
    			propOffsetYCorrected = propOffsetY;
    			propOffsetZCorrected = propOffsetZ;
    		} else {
    			//Calculate prop offset
	    		Float anglePrev;
	    		Float hyp;
	    		//Apply pitch
	    		if (propOffsetZ == 0) {
	    			if (propOffsetY <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = propOffsetY;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -propOffsetY;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetZ, propOffsetY);
	    			hyp = (float) (propOffsetZ / Math.sin(anglePrev));
	    		}

	    		Float Zpitch = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleX) * hyp);
	    		Float Ypitch = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleX) * hyp);

	    		//Apply yaw
	    		if (propOffsetX == 0) {
	    			if (Zpitch >= 0) {
	    				anglePrev = 0.0F;
	    				hyp = Zpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -Zpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetX, Zpitch);
	    			hyp = (float) (propOffsetX / Math.sin(anglePrev));
	    		}

	    		Float Xyaw = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleY) * hyp);
	    		propOffsetZCorrected = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleY) * hyp);

	    		//Apply roll
	    		if (Xyaw > -0.0001 && Xyaw < 0.0001) {
	    			if (Ypitch <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = Ypitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
		    			hyp = -Ypitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(Xyaw, Ypitch);
		    		hyp = (float) (Xyaw / Math.sin(anglePrev));
	    		}

	    		propOffsetXCorrected = (float) (Math.sin(anglePrev - propBodyPart.rotateAngleZ) * hyp);
	    		propOffsetYCorrected = (float) (Math.cos(anglePrev - propBodyPart.rotateAngleZ) * hyp);

				motherRenderer.rotateAngleX = propBodyPart.rotateAngleX;
				motherRenderer.rotateAngleY = propBodyPart.rotateAngleY;
				motherRenderer.rotateAngleZ = propBodyPart.rotateAngleZ;
    		}

    		if (propMatchScaling == true) {
    			propScaleX = propScaleX * this.playerdata.getPartConfig(enumPart).scaleX;
    			propScaleY = propScaleY * this.playerdata.getPartConfig(enumPart).scaleY;
    			propScaleZ = propScaleZ * this.playerdata.getPartConfig(enumPart).scaleZ;
    		}

    		GlStateManager.pushMatrix();

			GlStateManager.translate((propBodyPart.offsetX - propOffsetXCorrected - partModifierX), (propBodyPart.offsetY - propOffsetYCorrected - partModifierY), (propBodyPart.offsetZ - propOffsetZCorrected - partModifierZ));
			motherRenderer.postRender(par7);

			GlStateManager.rotate(propRotateX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(propRotateY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(propRotateZ, 0.0F, 0.0F, 1.0F);
			propRenderer.postRender(par7);

			IBakedModel model = minecraft.getRenderItem().getItemModelMesher().getItemModel(propItemStack);
			ItemTransformVec3f transformVec = model.getItemCameraTransforms().thirdperson_right;
			GlStateManager.scale((-propScaleX * (transformVec.scale.x + ItemCameraTransforms.offsetScaleX)), (-propScaleY * (transformVec.scale.y + ItemCameraTransforms.offsetScaleY)), (propScaleZ * (transformVec.scale.z + ItemCameraTransforms.offsetScaleZ)));
			minecraft.getItemRenderer().renderItem(this.player, propItemStack, TransformType.NONE);

			GlStateManager.popMatrix();
        }
	}

	private void renderParticleProp(Prop prop, float par7) {
		Boolean propHide = prop.hide;

		if (propHide == false) {
			Minecraft minecraft = Minecraft.getMinecraft();

			ModelRenderer propBodyPart = null;

			EnumParticleTypes propParticleType = prop.particleType;
			Float propScaleX = prop.scaleX;
			Float propScaleY = prop.scaleY;
			Float propScaleZ = prop.scaleZ;
			Float propOffsetX = -prop.offsetX;
			Float propOffsetY = prop.offsetY;
			Float propOffsetZ = prop.offsetZ;
			Float propRotateX = -prop.rotateX;
			Float propRotateY = prop.rotateY;
			Float propRotateZ = prop.rotateZ;
			Boolean propMatchScaling = prop.matchScaling;

			Float partModifierX = 0.0F;
			Float partModifierY = 0.0F;
			Float partModifierZ = 0.0F;
			EnumParts enumPart = null;

    		 switch(prop.bodyPartName) {
    		     case "hat":
	    		 case "head":
	    			 propBodyPart = this.model.bipedHead;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.HEAD;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY + 0.50F) * this.playerdata.getPartConfig(enumPart).scaleY + 0.20F;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "model":
    			 	 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "body":
	    		 case "torso":
	    			 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "back":
	    			 propBodyPart = this.model.bipedBody;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

	    			 enumPart = EnumParts.BODY;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.3F) * this.playerdata.getPartConfig(enumPart).scaleY;
					 propOffsetZ = (propOffsetZ - 0.15F) * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "arm":
	    		 case "armleft":
	    		 case "leftarm":
	    			 propBodyPart = this.model.bipedLeftArm;
	    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

	    			 enumPart = EnumParts.ARM_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    		 case "hand":
	    		 case "handleft":
	    		 case "lefthand":
	    			 propBodyPart = this.model.bipedLeftArm;
	    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

	    			 enumPart = EnumParts.ARM_LEFT;

					 propOffsetX = (propOffsetX - 0.0625F) * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "armright":
	    		 case "rightarm":
	    			 propBodyPart = this.model.bipedRightArm;
	    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

	    			 enumPart = EnumParts.ARM_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "handright":
	    		 case "righthand":
	    			 propBodyPart = this.model.bipedRightArm;
	    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

	    			 enumPart = EnumParts.ARM_RIGHT;

					 propOffsetX = (propOffsetX + 0.0625F) * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "leg":
	    		 case "legleft":
	    		 case "leftleg":
	    			 propBodyPart = this.model.bipedLeftLeg;
	    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

	    			 enumPart = EnumParts.LEG_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "foot":
	    		 case "footleft":
	    		 case "leftfoot":
	    			 propBodyPart = this.model.bipedLeftLeg;
	    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

	    			 enumPart = EnumParts.LEG_LEFT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "legright":
	    		 case "rightleg":
	    			 propBodyPart = this.model.bipedRightLeg;
	    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

	    			 enumPart = EnumParts.LEG_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
	    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
	    		 case "footright":
	    		 case "rightfoot":
	    			 propBodyPart = this.model.bipedRightLeg;
	    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
	    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

	    			 enumPart = EnumParts.LEG_RIGHT;

	    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(enumPart).scaleX;
					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(enumPart).scaleY;
	    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(enumPart).scaleZ;
	    			 break;
    		 }

    		 if (propBodyPart == this.model.bipedHead && this.playerdata.player == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0 && !(minecraft.currentScreen instanceof GuiNPCInterface))
			 	return;

			 if (this.player.isSneaking()) {
				 if (propBodyPart == this.model.bipedLeftLeg || propBodyPart == this.model.bipedRightLeg) {
    				 partModifierY += 0.1875F;
    				 partModifierZ -= 0.25F;
				 } else if (propBodyPart == this.model.bipedHead) {
    				 partModifierY -= 0.0625F;
				 }
			 }

    		 Float propOffsetXCorrected;
    		 Float propOffsetYCorrected;
    		 Float propOffsetZCorrected;

    		if (prop.bodyPartName.equals("model")) {
    			propOffsetXCorrected = propOffsetX;
    			propOffsetYCorrected = propOffsetY;
    			propOffsetZCorrected = propOffsetZ;
    		} else {
    			//Calculate prop offset
	    		Float anglePrev;
	    		Float hyp;
	    		//Apply pitch
	    		if (propOffsetZ == 0) {
	    			if (propOffsetY <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = propOffsetY;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -propOffsetY;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetZ, propOffsetY);
	    			hyp = (float) (propOffsetZ / Math.sin(anglePrev));
	    		}

	    		Float Zpitch = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleX) * hyp);
	    		Float Ypitch = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleX) * hyp);

	    		//Apply yaw
	    		if (propOffsetX == 0) {
	    			if (Zpitch >= 0) {
	    				anglePrev = 0.0F;
	    				hyp = Zpitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
	    				hyp = -Zpitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(propOffsetX, Zpitch);
	    			hyp = (float) (propOffsetX / Math.sin(anglePrev));
	    		}

	    		Float Xyaw = (float) (Math.sin(anglePrev + propBodyPart.rotateAngleY) * hyp);
	    		propOffsetZCorrected = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleY) * hyp);

	    		//Apply roll
	    		if (Xyaw > -0.0001 && Xyaw < 0.0001) {
	    			if (Ypitch <= 0) {
	    				anglePrev = 0.0F;
		    			hyp = Ypitch;
	    			} else {
	    				anglePrev = (float) Math.PI;
		    			hyp = -Ypitch;
	    			}
	    		} else {
	    			anglePrev = (float) Math.atan2(Xyaw, Ypitch);
		    		hyp = (float) (Xyaw / Math.sin(anglePrev));
	    		}

	    		propOffsetXCorrected = (float) (Math.sin(anglePrev - propBodyPart.rotateAngleZ) * hyp);
	    		propOffsetYCorrected = (float) (Math.cos(anglePrev - propBodyPart.rotateAngleZ) * hyp);
    		}

    		if (propMatchScaling == true) {
    			propScaleX = propScaleX * this.playerdata.getPartConfig(enumPart).scaleX;
    			propScaleY = propScaleY * this.playerdata.getPartConfig(enumPart).scaleY;
    			propScaleZ = propScaleZ * this.playerdata.getPartConfig(enumPart).scaleZ;
    		}

            this.player.worldObj.spawnParticle(propParticleType,
            		this.player.posX, this.player.posY, this.player.posZ,
            		0, 0, 0);
        }
	}


     @Override
     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
     }
}
