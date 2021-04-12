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
    super(0, 0, 0, new ItemStack(Items.field_151144_bL, 1, 3));
    this.field_146126_j = I18n.func_74838_a("menu.mpm");
  }

  public void onTabClicked() {
    Minecraft.func_71410_x().func_152344_a(() -> {
          Minecraft mc = Minecraft.func_71410_x();
          mc.func_147108_a((GuiScreen)new GuiMPM());
        });
  }

  public boolean shouldAddToList() {
    return true;
  }

  public void func_191745_a(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
    if (!this.field_146125_m) {
      super.func_191745_a(minecraft, mouseX, mouseY, partialTicks);
      return;
    }
    this.renderStack = ItemStack.field_190927_a;
    if (this.field_146124_l) {
      Minecraft mc = Minecraft.func_71410_x();
      boolean hovered = (mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g);
      if (hovered) {
        int x = mouseX + mc.field_71466_p.func_78256_a(this.field_146126_j);
        GlStateManager.func_179109_b(x, (this.field_146129_i + 2), 0.0F);
        drawHoveringText(Arrays.asList(new String[] { this.field_146126_j }, ), 0, 0, mc.field_71466_p);
        GlStateManager.func_179109_b(-x, -(this.field_146129_i + 2), 0.0F);
      }
    }
    super.func_191745_a(minecraft, mouseX, mouseY, partialTicks);
    GlStateManager.func_179094_E();
    GlStateManager.func_179109_b((this.field_146128_h + 14), this.field_146129_i + 22.0F, 150.0F);
    GlStateManager.func_179152_a(20.0F, 20.0F, 20.0F);
    ClientProxy.bindTexture(minecraft.field_71439_g.func_110306_p());
    GlStateManager.func_179142_g();
    GlStateManager.func_179114_b(135.0F, -1.0F, 1.0F, -1.0F);
    RenderHelper.func_74519_b();
    GlStateManager.func_179114_b(-135.0F, -1.0F, 1.0F, -1.0F);
    biped.field_78116_c.field_78795_f = 0.7F;
    biped.field_78116_c.field_78796_g = -0.7853982F;
    biped.field_78116_c.field_78808_h = -0.5F;
    biped.field_78116_c.func_78785_a(0.064F);
    biped.field_178720_f.func_78785_a(0.0625F);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.func_179121_F();
    RenderHelper.func_74518_a();
    GlStateManager.func_179101_C();
    GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
    GlStateManager.func_179090_x();
    GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
  }

  protected void drawHoveringText(List<String> list, int x, int y, FontRenderer font) {
    if (list.isEmpty())
      return;
    GlStateManager.func_179101_C();
    RenderHelper.func_74518_a();
    GlStateManager.func_179140_f();
    GlStateManager.func_179097_i();
    int k = 0;
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      String s = iterator.next();
      int l = font.func_78256_a(s);
      if (l > k)
        k = l;
    }
    int j2 = x + 12;
    int k2 = y - 12;
    int i1 = 8;
    if (list.size() > 1)
      i1 += 2 + (list.size() - 1) * 10;
    if (j2 + k > this.field_146120_f)
      j2 -= 28 + k;
    if (k2 + i1 + 6 > this.field_146121_g)
      k2 = this.field_146121_g - i1 - 6;
    this.field_73735_i = 300.0F;
    this.itemRender.field_77023_b = 300.0F;
    int j1 = -267386864;
    func_73733_a(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
    func_73733_a(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
    func_73733_a(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
    func_73733_a(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
    func_73733_a(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
    int k1 = 1347420415;
    int l1 = (k1 & 0xFEFEFE) >> 1 | k1 & 0xFF000000;
    func_73733_a(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
    func_73733_a(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
    func_73733_a(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
    func_73733_a(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
    for (int i2 = 0; i2 < list.size(); i2++) {
      String s1 = list.get(i2);
      font.func_175063_a(s1, j2, k2, -1);
      if (i2 == 0)
        k2 += 2;
      k2 += 10;
    }
    this.field_73735_i = 0.0F;
    this.itemRender.field_77023_b = 0.0F;
    GlStateManager.func_179145_e();
    GlStateManager.func_179126_j();
    RenderHelper.func_74519_b();
    GlStateManager.func_179091_B();
  }
}
