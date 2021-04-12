package micdoodle8.mods.galacticraft.api.client.tabs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.gui.GuiMPM;

public class InventoryTabMPM extends AbstractTab {
  private static final ModelPlayer biped = new ModelPlayer(0.0F, true);

  public InventoryTabMPM() {
    super(0, 0, 0, new ItemStack(Items.SKULL, 1, 3));
    this.displayString = I18n.translateToLocal("menu.mpm");
  }

  @Override
  public void onTabClicked() {
    Minecraft.getMinecraft().addScheduledTask(() -> {
          Minecraft mc = Minecraft.getMinecraft();
          mc.displayGuiScreen((GuiScreen)new GuiMPM());
        });
  }

  @Override
  public boolean shouldAddToList() {
    return true;
  }

  @Override
  public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
    if (!this.visible) {
      super.drawButton(minecraft, mouseX, mouseY, partialTicks);
      return;
    }
    this.renderStack = ItemStack.field_190927_a;
    if (this.enabled) {
      Minecraft mc = Minecraft.getMinecraft();
      boolean hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
      if (hovered) {
        int x = mouseX + mc.fontRendererObj.getStringWidth(this.displayString);
        GlStateManager.translate(x, (this.yPosition + 2), 0.0F);
        drawHoveringText(Arrays.asList(new String[] { this.displayString }, ), 0, 0, mc.fontRendererObj);
        GlStateManager.translate(-x, -(this.yPosition + 2), 0.0F);
      }
    }
    super.drawButton(minecraft, mouseX, mouseY, partialTicks);
    GlStateManager.pushMatrix();
    GlStateManager.translate((this.xPosition + 14), this.yPosition + 22.0F, 150.0F);
    GlStateManager.translate(20.0F, 20.0F, 20.0F);
    ClientProxy.bindTexture(minecraft.thePlayer.getLocationSkin());
    GlStateManager.enableColorMaterial();
    GlStateManager.rotate(135.0F, -1.0F, 1.0F, -1.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, -1.0F, 1.0F, -1.0F);
	    biped.bipedHead.rotateAngleX = 0.7F;
	    biped.bipedHead.rotateAngleY = -0.7853982F;
	    biped.bipedHead.rotateAngleZ = -0.5F;
    biped.bipedHead.render(0.064F);
    biped.bipedHeadwear.render(0.0625F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }

  protected void drawHoveringText(List<String> list, int x, int y, FontRenderer font) {
    if (list.isEmpty())
      return;
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    int k = 0;
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      String s = iterator.next();
      int l = font.getStringWidth(s);
      if (l > k)
        k = l;
    }
    int j2 = x + 12;
    int k2 = y - 12;
    int i1 = 8;
    if (list.size() > 1)
      i1 += 2 + (list.size() - 1) * 10;
    if (j2 + k > this.width)
      j2 -= 28 + k;
    if (k2 + i1 + 6 > this.height)
      k2 = this.height - i1 - 6;
    this.zLevel = 300.0F;
    this.itemRender.zLevel = 300.0F;
    int j1 = -267386864;
    drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
    drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
    drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
    drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
    drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
    int k1 = 1347420415;
    int l1 = (k1 & 0xFEFEFE) >> 1 | k1 & 0xFF000000;
    drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
    drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
    drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
    drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
    for (int i2 = 0; i2 < list.size(); i2++) {
      String s1 = list.get(i2);
      font.drawStringWithShadow(s1, j2, k2, -1);
      if (i2 == 0)
        k2 += 2;
      k2 += 10;
    }
    this.zLevel = 0.0F;
    this.itemRender.zLevel = 0.0F;
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableRescaleNormal();
  }
}
