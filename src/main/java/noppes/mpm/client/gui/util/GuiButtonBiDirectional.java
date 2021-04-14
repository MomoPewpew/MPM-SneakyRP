package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBiDirectional extends GuiNpcButton {
     public static final ResourceLocation resource = new ResourceLocation("moreplayermodels:textures/gui/arrowbuttons.png");

     public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current) {
          super(id, x, y, width, height, arr, current);
     }

     @Override
     public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
          if (this.visible) {
               boolean disabled = !this.enabled || this.display.length <= 1;
               boolean hover = !disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
               boolean hoverL = !disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height;
               boolean hoverR = !disabled && !hoverL && mouseX >= this.xPosition + this.width - 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               mc.getTextureManager().bindTexture(resource);
               this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, disabled ? 20 : (hoverL ? 40 : 0), 11, 20);
               this.drawTexturedModalRect(this.xPosition + this.width - 11, this.yPosition, 11, disabled ? 20 : (hoverR ? 40 : 0), 11, 20);
               int l = 16777215;
               if (this.packedFGColour != 0) {
                    l = this.packedFGColour;
               } else if (this.enabled && !disabled) {
                    if (hover) {
                         l = 16777120;
                    }
               } else {
                    l = 10526880;
               }

               String text = "";
               float maxWidth = (float)(this.width - 36);
               if ((float)mc.fontRendererObj.getStringWidth(this.displayString) > maxWidth) {
                    for(int h = 0; h < this.displayString.length(); ++h) {
                         char c = this.displayString.charAt(h);
                         text = text + c;
                         if ((float)mc.fontRendererObj.getStringWidth(text) > maxWidth) {
                              break;
                         }
                    }

                    text = text + "...";
               } else {
                    text = this.displayString;
               }

               if (hover) {
                    text = "§n" + text;
               }

               this.drawCenteredString(mc.fontRendererObj, text, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
          }
     }

     @Override
     public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
          int value = this.getValue();
          boolean bo = super.mousePressed(minecraft, mouseX, mouseY);
          if (bo && this.display != null && this.display.length != 0) {
               boolean hoverL = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height;
               boolean hoverR = !hoverL && mouseX >= this.xPosition + 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
               if (hoverR) {
                    value = (value + 1) % this.display.length;
               }

               if (hoverL) {
                    if (value <= 0) {
                         value = this.display.length;
                    }

                    --value;
               }

               this.setDisplay(value);
          }

          return bo;
     }
}
