package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class LayerEyes extends LayerInterface {
	public LayerEyes(RenderPlayer render) {
		super(render);
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		if (this.playerdata.eye1.isEnabled() || this.playerdata.eye2.isEnabled()) {
			GlStateManager.translate(this.model.bipedHead.offsetX, this.model.bipedHead.offsetY, this.model.bipedHead.offsetZ);
			GlStateManager.pushMatrix();
			this.model.bipedHead.postRender(0.0625F);
			GlStateManager.scale(par7, par7, -par7);
			GlStateManager.translate(0.0F, (float)((this.playerdata.eye1.type == 1 ? 1 : 2) - this.playerdata.eye1.eyePos), 0.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.shadeModel(7425);
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableCull();
			GlStateManager.disableAlpha();
			GlStateManager.depthMask(false);
			int i = this.player.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			Minecraft.getMinecraft().entityRenderer.func_191514_d(true);
			this.drawLeft();
			this.drawLeftBrow();
			GlStateManager.translate(0.0F, -((float)((this.playerdata.eye1.type == 1 ? 1 : 2) - this.playerdata.eye1.eyePos)), 0.0F);
			GlStateManager.translate(0.0F, (float)((this.playerdata.eye2.type == 1 ? 1 : 2) - this.playerdata.eye2.eyePos), 0.0F);
			this.drawRight();
			this.drawRightBrow();
			Minecraft.getMinecraft().entityRenderer.func_191514_d(false);
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(7424);
			GlStateManager.enableAlpha();
			GlStateManager.disableCull();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.translate(-this.model.bipedHead.offsetX, -this.model.bipedHead.offsetY, -this.model.bipedHead.offsetZ);
			GlStateManager.enableTexture2D();
		}
	}

	private void drawLeft() {
		if (this.playerdata.eye1.isEnabled()) {
			this.drawRect(3.0D, -5.0D, 1.0D, -4.0D, 16185078, 4.01D, false);
			this.drawRect(2.0D, -5.0D, 1.0D, -4.0D, this.playerdata.eye1.color, 4.011D, this.playerdata.eye1.type == 1);
			if (this.playerdata.eye1.glint && this.player.isEntityAlive()) {
				this.drawRect(1.5D, -4.9D, 1.9D, -4.5D, -1, 4.012D, false);
			}

			if (this.playerdata.eye1.type == 1) {
				this.drawRect(3.0D, -4.0D, 1.0D, -3.0D, 16777215, 4.01D, true);
				this.drawRect(2.0D, -4.0D, 1.0D, -3.0D, this.playerdata.eye1.color, 4.011D, false);
			}

		}
	}

	private void drawRight() {
		if (this.playerdata.eye2.isEnabled()) {
			this.drawRect(-3.0D, -5.0D, -1.0D, -4.0D, 16185078, 4.01D, false);
			this.drawRect(-2.0D, -5.0D, -1.0D, -4.0D, this.playerdata.eye2.color, 4.011D, this.playerdata.eye2.type == 1);
			if (this.playerdata.eye2.glint && this.player.isEntityAlive()) {
				this.drawRect(-1.5D, -4.9D, -1.1D, -4.5D, -1, 4.012D, false);
			}

			if (this.playerdata.eye2.type == 1) {
				this.drawRect(-3.0D, -4.0D, -1.0D, -3.0D, 16777215, 4.01D, true);
				this.drawRect(-2.0D, -4.0D, -1.0D, -3.0D, this.playerdata.eye2.color, 4.011D, false);
			}

		}
	}

	private void drawLeftBrow() {
		float offsetY = 0.0F;
		float f;
		if (this.playerdata.eye1.blinkStart > 0L && this.player.isEntityAlive()) {
			f = (float)(System.currentTimeMillis() - this.playerdata.eye1.blinkStart) / 150.0F;
			if (f > 1.0F) {
				f = 2.0F - f;
			}

			if (f < 0.0F) {
				this.playerdata.eye1.blinkStart = 0L;
				f = 0.0F;
			}

			if (this.playerdata.eye1.isEnabled()) {
				offsetY = (float)(this.playerdata.eye1.type == 1 ? 2 : 1) * f;
				this.drawRect(3.0D, -5.0D, 1.0D, (double)(-5.0F + offsetY), this.playerdata.eye1.skinColor, 4.013D, false);
			}
		}

		if (this.playerdata.eye1.browThickness > 0) {
			if (this.playerdata.eye1.isEnabled()) {
				f = (float)this.playerdata.eye1.browThickness / 10.0F;
				this.drawRect(1.0D, (double)(-5.0F), 3.0D, (double)(-5.0F - f), this.playerdata.eye1.browColor, 4.014D, false);
			}
		}

	}

	private void drawRightBrow() {
		float offsetY = 0.0F;
		float f;
		if (this.playerdata.eye1.blinkStart > 0L && this.player.isEntityAlive()) {
			f = (float)(System.currentTimeMillis() - this.playerdata.eye1.blinkStart) / 150.0F;
			if (f > 1.0F) {
				f = 2.0F - f;
			}

			if (f < 0.0F) {
				this.playerdata.eye1.blinkStart = 0L;
				f = 0.0F;
			}

			if (this.playerdata.eye2.isEnabled()) {
				offsetY = (float)(this.playerdata.eye2.type == 1 ? 2 : 1) * f;
				this.drawRect(-3.0D, -5.0D, -1.0D, (double)(-5.0F + offsetY), this.playerdata.eye2.skinColor, 4.013D, false);
			}
		}

		if (this.playerdata.eye2.browThickness > 0) {
			if (this.playerdata.eye2.isEnabled()) {
				f = (float)this.playerdata.eye2.browThickness / 10.0F;
				this.drawRect(-3.0D, (double)(-5.0F), -1.0D, (double)(-5.0F - f), this.playerdata.eye2.browColor, 4.014D, false);
			}
		}

	}

	public void drawRect(double x, double y, double x2, double y2, int color, double z, boolean darken) {
		double j1;
		if (x < x2) {
			j1 = x;
			x = x2;
			x2 = j1;
		}

		if (y < y2) {
			j1 = y;
			y = y2;
			y2 = j1;
		}

		float f1 = (float)(color >> 16 & 255) / 255.0F;
		float f2 = (float)(color >> 8 & 255) / 255.0F;
		float f3 = (float)(color & 255) / 255.0F;
		if (darken) {
			f1 *= 0.96F;
			f2 *= 0.96F;
			f3 *= 0.96F;
		}

		BufferBuilder tessellator = Tessellator.getInstance().getBuffer();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator.begin(7, DefaultVertexFormats.POSITION_COLOR);
		tessellator.pos(x, y, z).color(f1, f2, f3, 1.0F).endVertex();
		tessellator.pos(x, y2, z).color(f1, f2, f3, 1.0F).endVertex();
		tessellator.pos(x2, y2, z).color(f1, f2, f3, 1.0F).endVertex();
		tessellator.pos(x2, y, z).color(f1, f2, f3, 1.0F).endVertex();
		Tessellator.getInstance().draw();
	}

	@Override
	public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
	}
}
