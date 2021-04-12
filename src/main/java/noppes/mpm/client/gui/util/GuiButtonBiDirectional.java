package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBiDirectional extends GuiNpcButton {
  public static final ResourceLocation resource = new ResourceLocation("moreplayermodels:textures/gui/arrowbuttons.png");

  public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current) {
    super(id, x, y, width, height, arr, current);
  }

  public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    if (!this.field_146125_m)
      return;
    boolean disabled = (!this.field_146124_l || this.display.length <= 1);
    boolean hover = (!disabled && mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
    boolean hoverL = (!disabled && mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + 11 && mouseY < this.field_146129_i + this.field_146121_g);
    boolean hoverR = (!disabled && !hoverL && mouseX >= this.field_146128_h + this.field_146120_f - 11 && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    mc.func_110434_K().func_110577_a(resource);
    func_73729_b(this.field_146128_h, this.field_146129_i, 0, disabled ? 20 : (hoverL ? 40 : 0), 11, 20);
    func_73729_b(this.field_146128_h + this.field_146120_f - 11, this.field_146129_i, 11, disabled ? 20 : (hoverR ? 40 : 0), 11, 20);
    int l = 16777215;
    if (this.packedFGColour != 0) {
      l = this.packedFGColour;
    } else if (!this.field_146124_l || disabled) {
      l = 10526880;
    } else if (hover) {
      l = 16777120;
    }
    String text = "";
    float maxWidth = (this.field_146120_f - 36);
    if (mc.field_71466_p.func_78256_a(this.field_146126_j) > maxWidth) {
      for (int h = 0; h < this.field_146126_j.length(); h++) {
        char c = this.field_146126_j.charAt(h);
        text = text + c;
        if (mc.field_71466_p.func_78256_a(text) > maxWidth)
          break;
      }
      text = text + "...";
    } else {
      text = this.field_146126_j;
    }
    if (hover)
      text = "+ text;
    func_73732_a(mc.field_71466_p, text, this.field_146128_h + this.field_146120_f / 2, this.field_146129_i + (this.field_146121_g - 8) / 2, l);
  }

  public boolean func_146116_c(Minecraft minecraft, int mouseX, int mouseY) {
    int value = getValue();
    boolean bo = super.func_146116_c(minecraft, mouseX, mouseY);
    if (bo && this.display != null && this.display.length != 0) {
      boolean hoverL = (mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + 11 && mouseY < this.field_146129_i + this.field_146121_g);
      boolean hoverR = (!hoverL && mouseX >= this.field_146128_h + 11 && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
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
