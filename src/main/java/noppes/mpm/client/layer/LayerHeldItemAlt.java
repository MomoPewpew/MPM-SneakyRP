package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.FMLCommonHandler;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.constants.EnumParts;

public class LayerHeldItemAlt extends LayerHeldItem {

	public LayerHeldItemAlt(RenderLivingBase<?> p_i46115_1_) {
		super(p_i46115_1_);
	}

	@Override
	public void doRenderLayer(EntityLivingBase entity, float p_doRenderLayer_2_, float p_doRenderLayer_3_, float p_doRenderLayer_4_, float p_doRenderLayer_5_, float p_doRenderLayer_6_, float p_doRenderLayer_7_, float p_doRenderLayer_8_) {
		boolean flag = (entity.getPrimaryHand() == EnumHandSide.RIGHT);
		ItemStack itemstack = flag ? entity.getHeldItemOffhand() : entity.getHeldItemMainhand();
		ItemStack itemstack1 = flag ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();

		if (itemstack != null || itemstack1 != null) {

			GlStateManager.pushMatrix();

			if ((this.livingEntityRenderer.getMainModel()).isChild) {

				float f = 0.5F;
				GlStateManager.translate(0.0F, 0.75F, 0.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
			}

			renderHeldItem(entity, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			renderHeldItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GlStateManager.popMatrix();
		}
	}

	private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType type, EnumHandSide side) {
		if (stack != null) {
			if (entity instanceof EntityPlayer) {
				ModelData data = ModelData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(entity.getUniqueID()));
				ModelPartData dataLeftArm = data.getOrCreatePart(EnumParts.ARM_LEFT);
				ModelPartData dataRightArm = data.getOrCreatePart(EnumParts.ARM_RIGHT);

				if (side == EnumHandSide.LEFT && dataLeftArm.type != 0) return;
				if (side == EnumHandSide.RIGHT && dataRightArm.type != 0) return;
			}

			GlStateManager.pushMatrix();

			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			func_191361_a(side);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = (side == EnumHandSide.LEFT);
			GlStateManager.translate((flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, type, flag);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected void func_191361_a(EnumHandSide side) {
		((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, side);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}