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

  public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
    if (!this.playerdata.eyes.isEnabled())
      return;
    GlStateManager.pushMatrix();
    this.model.bipedHead.func_78794_c(0.0625F);
    GlStateManager.translate(par7, par7, -par7);
    GlStateManager.translate(0.0F, (((this.playerdata.eyes.type == 1) ? 1 : 2) - this.playerdata.eyes.eyePos), 0.0F);
    GlStateManager.enableRescaleNormal();
    GlStateManager.func_179103_j(7425);
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.func_179089_o();
    GlStateManager.func_179118_c();
    GlStateManager.depthMask(false);
    int i = this.player.func_70070_b();
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.func_77475_a(OpenGlHelper.lightmapTexUnit, j, k);
    (Minecraft.getMinecraft()).field_71460_t.func_191514_d(true);
    drawBrows();
    drawLeft();
    drawRight();
    (Minecraft.getMinecraft()).field_71460_t.func_191514_d(false);
    GlStateManager.depthMask(true);
    GlStateManager.disableBlend();
    GlStateManager.func_179103_j(7424);
    GlStateManager.func_179141_d();
    GlStateManager.func_179129_p();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
    GlStateManager.enableTexture2D();
  }

  private void drawLeft() {
    if (this.playerdata.eyes.pattern == 2)
      return;
    drawRect(3.0D, -5.0D, 1.0D, -4.0D, 16185078, 4.01D, false);
    drawRect(2.0D, -5.0D, 1.0D, -4.0D, this.playerdata.eyes.color, 4.011D, (this.playerdata.eyes.type == 1));
    if (this.playerdata.eyes.glint && this.player.isEntityAlive())
      drawRect(1.5D, -4.9D, 1.9D, -4.5D, -1, 4.012D, false);
    if (this.playerdata.eyes.type == 1) {
      drawRect(3.0D, -4.0D, 1.0D, -3.0D, 16777215, 4.01D, true);
      drawRect(2.0D, -4.0D, 1.0D, -3.0D, this.playerdata.eyes.color, 4.011D, false);
    }
  }

  private void drawRight() {
    if (this.playerdata.eyes.pattern == 1)
      return;
    drawRect(-3.0D, -5.0D, -1.0D, -4.0D, 16185078, 4.01D, false);
    drawRect(-2.0D, -5.0D, -1.0D, -4.0D, this.playerdata.eyes.color, 4.011D, (this.playerdata.eyes.type == 1));
    if (this.playerdata.eyes.glint && this.player.isEntityAlive())
      drawRect(-1.5D, -4.9D, -1.1D, -4.5D, -1, 4.012D, false);
    if (this.playerdata.eyes.type == 1) {
      drawRect(-3.0D, -4.0D, -1.0D, -3.0D, 16777215, 4.01D, true);
      drawRect(-2.0D, -4.0D, -1.0D, -3.0D, this.playerdata.eyes.color, 4.011D, false);
    }
  }

  private void drawBrows() {
    float offsetY = 0.0F;
    if (this.playerdata.eyes.blinkStart > 0L && this.player.isEntityAlive()) {
      float f = (float)(System.currentTimeMillis() - this.playerdata.eyes.blinkStart) / 150.0F;
      if (f > 1.0F)
        f = 2.0F - f;
      if (f < 0.0F) {
        this.playerdata.eyes.blinkStart = 0L;
        f = 0.0F;
      }
      offsetY = ((this.playerdata.eyes.type == 1) ? 2 : true) * f;
      drawRect(-3.0D, -5.0D, -1.0D, (-5.0F + offsetY), this.playerdata.eyes.skinColor, 4.013D, false);
      drawRect(3.0D, -5.0D, 1.0D, (-5.0F + offsetY), this.playerdata.eyes.skinColor, 4.013D, false);
    }
    if (this.playerdata.eyes.browThickness > 0) {
      float thickness = this.playerdata.eyes.browThickness / 10.0F;
      drawRect(-3.0D, (-5.0F + offsetY), -1.0D, (-5.0F - thickness + offsetY), this.playerdata.eyes.browColor, 4.014D, false);
      drawRect(1.0D, (-5.0F + offsetY), 3.0D, (-5.0F - thickness + offsetY), this.playerdata.eyes.browColor, 4.014D, false);
    }
  }

  public void drawRect(double x, double y, double x2, double y2, int color, double z, boolean darken) {
    if (x < x2) {
      double j1 = x;
      x = x2;
      x2 = j1;
    }
    if (y < y2) {
      double j1 = y;
      y = y2;
      y2 = j1;
    }
    float f1 = (color >> 16 & 0xFF) / 255.0F;
    float f2 = (color >> 8 & 0xFF) / 255.0F;
    float f3 = (color & 0xFF) / 255.0F;
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

  public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {}
}
