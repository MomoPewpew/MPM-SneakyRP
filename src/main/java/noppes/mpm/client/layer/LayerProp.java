package noppes.mpm.client.layer;

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
import noppes.mpm.MorePlayerModels;

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
				Float propScaleX = this.playerdata.propScaleX.get(i);
				Float propScaleY = this.playerdata.propScaleY.get(i);
				Float propScaleZ = this.playerdata.propScaleZ.get(i);
				Float propOffsetX = this.playerdata.propOffsetX.get(i);
				Float propOffsetY = this.playerdata.propOffsetY.get(i);
				Float propOffsetZ = this.playerdata.propOffsetZ.get(i);
				Float propRotateX = this.playerdata.propRotateX.get(i);
				Float propRotateY = this.playerdata.propRotateY.get(i);
				Float propRotateZ = this.playerdata.propRotateZ.get(i);

	    		 switch(this.playerdata.propBodyPartName.get(i)) {
	    		     case "hat":
		    		 case "head":
		    			 propBodyPart = this.model.bipedHead;
		    			 break;
		    		 case "model":
	    			 	 propBodyPart = this.model.bipedBodyWear;
		    		 case "body":
		    		 case "torso":
		    			 propBodyPart = this.model.bipedBody;
		    			 break;
		    		 case "arm":
		    		 case "hand":
		    		 case "armleft":
		    		 case "handleft":
		    		 case "leftarm":
		    		 case "lefthand":
		    			 propBodyPart = this.model.bipedLeftArm;
		    			 break;
		    		 case "armright":
		    		 case "handright":
		    		 case "rightarm":
		    		 case "righthand":
		    			 propBodyPart = this.model.bipedRightArm;
		    			 break;
		    		 case "leg":
		    		 case "foot":
		    		 case "legleft":
		    		 case "footlef":
		    		 case "leftleg":
		    		 case "leftfoot":
		    			 propBodyPart = this.model.bipedLeftLeg;
		    			 break;
		    		 case "legright":
		    		 case "footright":
		    		 case "rightleg":
		    		 case "rightfoot":
		    			 propBodyPart = this.model.bipedRightLeg;
		    			 break;
	    		 }

	    		Float propOffsetXCorrected = (float) (propOffsetX - (propOffsetY * Math.sin(propBodyPart.rotateAngleZ)));
	    		Float propOffsetYCorrected = (float) (propOffsetY * Math.cos(propBodyPart.rotateAngleX) * Math.cos(propBodyPart.rotateAngleZ));
	    		Float propOffsetZCorrected = (float) (propOffsetZ + (propOffsetY * Math.sin(propBodyPart.rotateAngleX)));

				GlStateManager.translate((propBodyPart.offsetX - propOffsetXCorrected), (propBodyPart.offsetY - propOffsetYCorrected), (propBodyPart.offsetZ - propOffsetZCorrected));
				GlStateManager.rotate(propRotateX, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(propRotateY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(propRotateZ, 0.0F, 0.0F, 1.0F);
				GlStateManager.pushMatrix();

				propBodyPart.postRender(par7);

				IBakedModel model = minecraft.getRenderItem().getItemModelMesher().getItemModel(propItemStack);
				ItemTransformVec3f transformVec = model.getItemCameraTransforms().thirdperson_right;
				GlStateManager.scale((-propScaleX * (transformVec.scale.x + ItemCameraTransforms.offsetScaleX)), (-propScaleY * (transformVec.scale.y + ItemCameraTransforms.offsetScaleY)), (propScaleZ * (transformVec.scale.z + ItemCameraTransforms.offsetScaleZ)));
				minecraft.getItemRenderer().renderItem(this.player, propItemStack, TransformType.NONE);

				GlStateManager.popMatrix();;
				GlStateManager.rotate(-propRotateZ, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(-propRotateY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-propRotateX, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(-(propBodyPart.offsetX - propOffsetXCorrected), -(propBodyPart.offsetY - propOffsetYCorrected), -(propBodyPart.offsetZ - propOffsetZCorrected));
	          }
		}
     }

     @Override
     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
     }
}
