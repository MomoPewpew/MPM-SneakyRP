package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class GuiItemStackButton extends GuiNpcButton {
     public boolean shown;
     protected String[] display;
     private int displayValue;
     public int id;
     public ItemStack itemStack;
     protected RenderItem itemRender;
     protected FontRenderer fontRendererObj;

     public GuiItemStackButton(int i, int j, int k, String s, ItemStack itemStackArg) {
          super(i, j, k, I18n.translateToLocal(s));
          this.shown = true;
          this.displayValue = 0;
          this.id = i;
          this.itemStack = itemStackArg;
          this.itemRender = Minecraft.getMinecraft().getRenderItem();
          this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
     }

     public GuiItemStackButton(int i, int j, int k, String[] display, int val, ItemStack itemStackArg) {
          this(i, j, k, display[val], itemStackArg);
          this.display = display;
          this.displayValue = val;
     }

     public GuiItemStackButton(int i, int j, int k, int l, int m, String string, ItemStack itemStackArg) {
          super(i, j, k, l, m, I18n.translateToLocal(string));
          this.shown = true;
          this.displayValue = 0;
          this.id = i;
          this.itemStack = itemStackArg;
          this.itemRender = Minecraft.getMinecraft().getRenderItem();
          this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
     }

     public GuiItemStackButton(int i, int j, int k, int l, int m, String[] display, int val, ItemStack itemStackArg) {
          this(i, j, k, l, m, display[val % display.length], itemStackArg);
          this.display = display;
          this.displayValue = val % display.length;
     }

     public void setDisplayText(String text) {
          this.displayString = I18n.translateToLocal(text);
     }

     public int getValue() {
          return this.displayValue;
     }

     @Override
     public void func_191745_a(Minecraft minecraft, int i, int j, float partialTicks) {
          if (!shown)
               return;

          RenderHelper.disableStandardItemLighting();
          RenderHelper.enableGUIStandardItemLighting();
          this.zLevel = 100.0F;
          this.itemRender.zLevel = 100.0F;
          GlStateManager.enableRescaleNormal();
          this.itemRender.renderItemAndEffectIntoGUI(this.itemStack, this.xPosition, this.yPosition);
          this.itemRender.renderItemOverlays(this.fontRendererObj, this.itemStack, this.xPosition, this.yPosition);
          this.itemRender.zLevel = 0.0F;
          this.zLevel = 0.0F;
          RenderHelper.enableStandardItemLighting();
     }

     @Override
     public boolean mousePressed(Minecraft minecraft, int i, int j) {
          boolean bo = super.mousePressed(minecraft, i, j);
          if (bo && this.display != null) {
               this.displayValue = (this.displayValue + 1) % this.display.length;
               this.setDisplayText(this.display[this.displayValue]);
          }

          return bo;
     }

     public void setDisplay(int value) {
          this.displayValue = value;
          this.setDisplayText(this.display[value]);
     }
}
