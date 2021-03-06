package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.model.part.head.ModelHeadwear;

public class LayerHeadwear extends LayerInterface implements LayerPreRender {
	private ModelHeadwear headwear;

	public LayerHeadwear(RenderPlayer render) {
		super(render);
	}

	@Override
	protected void createParts() {
		this.headwear = new ModelHeadwear(this.model);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		if (MorePlayerModels.HeadWearType == 1 && !this.model.bipedHead.isHidden && this.model.bipedHead.showModel && !this.playerdata.hideHat) {
			GlStateManager.translate(this.model.bipedHead.offsetX, this.model.bipedHead.offsetY, this.model.bipedHead.offsetZ);

			GlStateManager.color(1.0F, 1.0F, 1.0F);
			ClientProxy.bindTexture(this.player.getLocationSkin());
			if (this.player.hurtTime > 0 || this.player.deathTime > 0) {
				GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
			}

			this.model.bipedHead.postRender(par7);
			this.headwear.render(par7);

			GlStateManager.translate(-this.model.bipedHead.offsetX, -this.model.bipedHead.offsetY, -this.model.bipedHead.offsetZ);
		}
	}

	@Override
	public void rotate(float par2, float par3, float par4, float par5, float par6, float par7) {
	}

	@Override
	public void preRender(AbstractClientPlayer player) {
		this.player = player;
		this.playerdata = ModelData.get(player);

		this.model.bipedHeadwear.isHidden = MorePlayerModels.HeadWearType == 1;
		this.headwear.config = null;

		if (this.playerdata.hideHat) {
			this.model.bipedHeadwear.showModel = false;
		}
	}
}
