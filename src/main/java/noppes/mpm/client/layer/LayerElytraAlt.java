package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.constants.EnumParts;

public class LayerElytraAlt implements LayerRenderer<AbstractClientPlayer> {

	private final RenderPlayer playerRenderer;

	public LayerElytraAlt(RenderPlayer playerRendererIn)
	{
		this.playerRenderer = playerRendererIn;
	}

	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
	    if (player instanceof EntityPlayer) {
	        ModelData data = ModelData.get(player);
	        if (data.getPartData(EnumParts.WINGS) != null && data.wingMode == 1)
	          return;
	      }
	      doRenderLayer(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		// TODO Auto-generated method stub
		return false;
	}
}

/*public class LayerElytraAlt extends LayerElytra {

  public LayerElytraAlt(RenderPlayer renderPlayerIn) {
    super((RenderLivingBase)renderPlayerIn);
  }

  @Override
  public void doRenderLayer(EntityLivingBase entityLiving, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    if (entityLiving instanceof EntityPlayer) {
      ModelData data = ModelData.get((EntityPlayer)entityLiving);
      if (data.getPartData(EnumParts.WINGS) != null && data.wingMode == 1)
        return;
    }
    super.doRenderLayer(entityLiving, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
  }
}*/
