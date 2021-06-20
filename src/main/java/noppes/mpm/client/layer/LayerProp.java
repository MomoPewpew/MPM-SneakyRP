package noppes.mpm.client.layer;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.constants.EnumParts;

public class LayerProp extends LayerInterface {

	public LayerProp(RenderPlayer render) {
		super(render);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		Minecraft minecraft = Minecraft.getMinecraft();

		for (int i = 0; i < this.playerdata.propItemStack.size(); i++) {
			ItemStack propItemStack = this.playerdata.propItemStack.get(i);

			if (propItemStack != null) {
				ModelRenderer propBodyPart = null;
				ModelRenderer motherRenderer = new ModelRenderer(this.model);
				ModelRenderer propRenderer = new ModelRenderer(this.model);
				motherRenderer.addChild(propRenderer);
				Float propScaleX = this.playerdata.propScaleX.get(i);
				Float propScaleY = this.playerdata.propScaleY.get(i);
				Float propScaleZ = this.playerdata.propScaleZ.get(i);
				Float propOffsetX = -this.playerdata.propOffsetX.get(i);
				Float propOffsetY = this.playerdata.propOffsetY.get(i);
				Float propOffsetZ = this.playerdata.propOffsetZ.get(i);
				Float propRotateX = -this.playerdata.propRotateX.get(i);
				Float propRotateY = this.playerdata.propRotateY.get(i);
				Float propRotateZ = this.playerdata.propRotateZ.get(i);

				Float partModifierX = 0.0F;
				Float partModifierY = 0.0F;
				Float partModifierZ = 0.0F;

	    		 switch(this.playerdata.propBodyPartName.get(i)) {
	    		     case "hat":
		    		 case "head":
		    			 propBodyPart = this.model.bipedHead;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.HEAD).scaleX;
    					 propOffsetY = (propOffsetY + 0.50F) * this.playerdata.getPartConfig(EnumParts.HEAD).scaleY + 0.20F;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.HEAD).scaleZ;
		    			 break;
		    		 case "model":
	    			 	 propBodyPart = this.model.bipedBody;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.BODY).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.BODY).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.BODY).scaleZ;
		    			 break;
		    		 case "body":
		    		 case "torso":
		    			 propBodyPart = this.model.bipedBody;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.BODY).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.BODY).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.BODY).scaleZ;
		    			 break;
		    		 case "back":
		    			 propBodyPart = this.model.bipedBody;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.BODY).scaleX;
    					 propOffsetY = (propOffsetY - 0.3F) * this.playerdata.getPartConfig(EnumParts.BODY).scaleY;
    					 propOffsetZ = (propOffsetY - 0.15F) * this.playerdata.getPartConfig(EnumParts.BODY).scaleZ;
		    			 break;
		    		 case "arm":
		    		 case "armleft":
		    		 case "leftarm":
		    			 propBodyPart = this.model.bipedLeftArm;
		    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleZ;
		    		 case "hand":
		    		 case "handleft":
		    		 case "lefthand":
		    			 propBodyPart = this.model.bipedLeftArm;
		    			 partModifierX = (-0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX);
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY);

    					 propOffsetX = (propOffsetX - 0.0625F) * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleX;
    					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.ARM_LEFT).scaleZ;
		    			 break;
		    		 case "armright":
		    		 case "rightarm":
		    			 propBodyPart = this.model.bipedRightArm;
		    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleZ;
		    			 break;
		    		 case "handright":
		    		 case "righthand":
		    			 propBodyPart = this.model.bipedRightArm;
		    			 partModifierX = (0.25F * this.playerdata.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * this.playerdata.getPartConfig(EnumParts.BODY).scaleY - 0.125  * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

    					 propOffsetX = (propOffsetX + 0.0625F) * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleX;
    					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.ARM_RIGHT).scaleZ;
		    			 break;
		    		 case "leg":
		    		 case "legleft":
		    		 case "leftleg":
		    			 propBodyPart = this.model.bipedLeftLeg;
		    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleZ;
		    			 break;
		    		 case "foot":
		    		 case "footleft":
		    		 case "leftfoot":
		    			 propBodyPart = this.model.bipedLeftLeg;
		    			 partModifierX = -0.125F * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleX;
    					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.LEG_LEFT).scaleZ;
		    			 break;
		    		 case "legright":
		    		 case "rightleg":
		    			 propBodyPart = this.model.bipedRightLeg;
		    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
		    			 propOffsetY = propOffsetY * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleZ;
		    			 break;
		    		 case "footright":
		    		 case "rightfoot":
		    			 propBodyPart = this.model.bipedRightLeg;
		    			 partModifierX = 0.125F * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
		    			 partModifierY = (float) (-1.5F + 0.75 * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

		    			 propOffsetX = propOffsetX * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
    					 propOffsetY = (propOffsetY - 0.7F) * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleY;
		    			 propOffsetZ = propOffsetZ * this.playerdata.getPartConfig(EnumParts.LEG_RIGHT).scaleZ;
		    			 break;
	    		 }

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
	    		Float propOffsetZCorrected = (float) (Math.cos(anglePrev + propBodyPart.rotateAngleY) * hyp);

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

	    		Float propOffsetXCorrected = (float) (Math.sin(anglePrev - propBodyPart.rotateAngleZ) * hyp);
	    		Float propOffsetYCorrected = (float) (Math.cos(anglePrev - propBodyPart.rotateAngleZ) * hyp);

	    		GlStateManager.pushMatrix();

	    		if (!this.playerdata.propBodyPartName.get(i).equals("model")) {
					motherRenderer.rotateAngleX = propBodyPart.rotateAngleX;
					motherRenderer.rotateAngleY = propBodyPart.rotateAngleY;
					motherRenderer.rotateAngleZ = propBodyPart.rotateAngleZ;
	    		}

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
     }

     @Override
     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
     }
}
