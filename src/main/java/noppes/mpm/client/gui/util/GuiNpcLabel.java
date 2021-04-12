package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;

public class GuiNpcLabel {
  public String label;

  private int x;

  private int y;

  private int color = 4210752;

  public boolean enabled = true;

  public int id;

  public GuiNpcLabel(int id, String label, int x, int y, int color) {
    this.id = id;
    this.label = I18n.func_74838_a(label);
    this.x = x;
    this.y = y;
    this.color = color;
  }

  public void drawLabel(GuiScreen gui, FontRenderer fontRenderer) {
    if (this.enabled)
      fontRenderer.func_78276_b(this.label, this.x, this.y, this.color);
  }

  public void center(int width) {
    int size = (Minecraft.func_71410_x()).field_71466_p.func_78256_a(this.label);
    this.x += (width - size) / 2;
  }
}
