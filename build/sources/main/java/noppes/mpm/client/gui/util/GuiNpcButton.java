package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;

public class GuiNpcButton extends GuiButton {
     public boolean shown;
     protected String[] display;
     private int displayValue;
     public int id;

     public GuiNpcButton(int i, int j, int k, String s) {
          super(i, j, k, I18n.translateToLocal(s));
          this.shown = true;
          this.displayValue = 0;
          this.id = i;
     }

     public GuiNpcButton(int i, int j, int k, String[] display, int val) {
          this(i, j, k, display[val]);
          this.display = display;
          this.displayValue = val;
     }

     public GuiNpcButton(int i, int j, int k, int l, int m, String string) {
          super(i, j, k, l, m, I18n.translateToLocal(string));
          this.shown = true;
          this.displayValue = 0;
          this.id = i;
     }

     public GuiNpcButton(int i, int j, int k, int l, int m, String[] display, int val) {
          this(i, j, k, l, m, display[val % display.length]);
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
          if (this.shown)
               return;
		   func_191745_a(minecraft, i, j, partialTicks);
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
