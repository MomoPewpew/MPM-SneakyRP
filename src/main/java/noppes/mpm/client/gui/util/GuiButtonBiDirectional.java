package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBiDirectional extends GuiNpcButton {
  public static final ResourceLocation resource = new ResourceLocation("moreplayermodels:textures/gui/arrowbuttons.png");

  public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current) {
    super(id, x, y, width, height, arr, current);
  }

  public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    if (!this.visible)
      return;
    boolean disabled = (!this.enabled || this.display.length <= 1);
    boolean hover = (!disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
    boolean hoverL = (!disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height);
    boolean hoverR = (!disabled && !hoverL && mouseX >= this.xPosition + this.width - 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.func_110434_K().bindTexture(resource);
    drawTexturedModalRect(this.xPosition, this.yPosition, 0, disabled ? 20 : (hoverL ? 40 : 0), 11, 20);
    drawTexturedModalRect(this.xPosition + this.width - 11, this.yPosition, 11, disabled ? 20 : (hoverR ? 40 : 0), 11, 20);
    int l = 16777215;
    if (this.packedFGColour != 0) {
      l = this.packedFGColour;
    } else if (!this.enabled || disabled) {
      l = 10526880;
    } else if (hover) {
      l = 16777120;
    }
    String text = "";
    float maxWidth = (this.width - 36);
    if (mc.fontRendererObj.getStringWidth(this.field_146126_j) > maxWidth) {
      for (int h = 0; h < this.field_146126_j.length(); h++) {
        char c = this.field_146126_j.charAt(h);
        text = text + c;
        if (mc.fontRendererObj.getStringWidth(text) > maxWidth)
          break;
      }
      text = text + "...";
    } else {
      text = this.field_146126_j;
    }
    if (hover)
      text = "+ text;
    func_73732_a(mc.fontRendererObj, text, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
  }

  public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
    int value = getValue();
    boolean bo = super.mousePressed(minecraft, mouseX, mouseY);
    if (bo && this.display != null && this.display.length != 0) {
      boolean hoverL = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height);
      boolean hoverR = (!hoverL && mouseX >= this.xPosition + 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
      if (hoverR)
        value = (value + 1) % this.display.length;
      if (hoverL) {
        if (value <= 0)
          value = this.display.length;
        value--;
      }
      setDisplay(value);
    }
    return bo;
  }
}
