package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiNpcTextField extends GuiTextField {
     public boolean enabled = true;
     public boolean inMenu = true;
     public boolean numbersOnly = false;
     private ITextfieldListener listener;
     public int id;
     public int min = 0;
     public int max = Integer.MAX_VALUE;
     public int def = 0;
     private static GuiNpcTextField activeTextfield = null;
     private final int[] allowedSpecialChars = new int[]{14, 211, 203, 205};

     public GuiNpcTextField(int id, GuiScreen parent, int i, int j, int k, int l, String s) {
          super(id, Minecraft.getMinecraft().fontRenderer, i, j, k, l);
          this.setMaxStringLength(500);
          this.setText(s);
          this.id = id;
          if (parent instanceof ITextfieldListener) {
               this.listener = (ITextfieldListener)parent;
          }

     }

     public static boolean isActive() {
          return activeTextfield != null;
     }

     private boolean charAllowed(char c, int i) {
          if (this.numbersOnly && !Character.isDigit(c)) {
               int[] var3 = this.allowedSpecialChars;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                    int j = var3[var5];
                    if (j == i) {
                         return true;
                    }
               }

               return false;
          } else {
               return true;
          }
     }

     public boolean textboxKeyTyped(char c, int i) {
          return !this.charAllowed(c, i) ? false : super.textboxKeyTyped(c, i);
     }

     public boolean isEmpty() {
          return this.getText().trim().length() == 0;
     }

     public int getInteger() {
          return Integer.parseInt(this.getText());
     }

     public boolean isInteger() {
          try {
               Integer.parseInt(this.getText());
               return true;
          } catch (NumberFormatException var2) {
               return false;
          }
     }

     public boolean mouseClicked(int i, int j, int k) {
          boolean wasFocused = this.isFocused();
          boolean clicked = super.mouseClicked(i, j, k);
          if (wasFocused != this.isFocused() && wasFocused) {
               this.unFocused();
          }

          if (this.isFocused()) {
               activeTextfield = this;
          }

          return clicked;
     }

     public void unFocused() {
          if (this.numbersOnly) {
               if (!this.isEmpty() && this.isInteger()) {
                    if (this.getInteger() < this.min) {
                         this.setText(this.min + "");
                    } else if (this.getInteger() > this.max) {
                         this.setText(this.max + "");
                    }
               } else {
                    this.setText(this.def + "");
               }
          }

          if (this.listener != null) {
               this.listener.unFocused(this);
          }

          if (this == activeTextfield) {
               activeTextfield = null;
          }

     }

     public void drawTextBox() {
          if (this.enabled) {
               super.drawTextBox();
          }

     }

     public void setMinMaxDefault(int i, int j, int k) {
          this.min = i;
          this.max = j;
          this.def = k;
     }

     public static void unfocus() {
          if (activeTextfield != null) {
               activeTextfield.unFocused();
          }

          activeTextfield = null;
     }
}
