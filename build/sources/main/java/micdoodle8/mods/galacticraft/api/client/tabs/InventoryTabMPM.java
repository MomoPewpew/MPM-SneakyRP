package micdoodle8.mods.galacticraft.api.client.tabs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
               mc.displayGuiScreen(new GuiMPM());
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
          } else {
               this.renderStack = null;
               if (this.enabled) {
                    Minecraft mc = Minecraft.getMinecraft();
                    boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    if (hovered) {
                         int x = mouseX + mc.fontRendererObj.getStringWidth(this.displayString);
                         GlStateManager.translate((float)x, (float)(this.yPosition + 2), 0.0F);
                         this.drawHoveringText(Arrays.asList(this.displayString), 0, 0, mc.fontRendererObj);
                         GlStateManager.translate((float)(-x), (float)(-(this.yPosition + 2)), 0.0F);
                    }
               }

               super.drawButton(minecraft, mouseX, mouseY, partialTicks);
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)(this.xPosition + 14), (float)this.yPosition + 22.0F, 150.0F);
               GlStateManager.scale(20.0F, 20.0F, 20.0F);
               ClientProxy.bindTexture(minecraft.thePlayer.getLocationSkin());
               GlStateManager.enableColorMaterial();
               GlStateManager.rotate(135.0F, -1.0F, 1.0F, -1.0F);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.rotate(-135.0F, -1.0F, 1.0F, -1.0F);
               biped.bipedHeadwear.rotateAngleX = biped.bipedHead.rotateAngleX = 0.7F;
               biped.bipedHeadwear.rotateAngleY = biped.bipedHead.rotateAngleY = -0.7853982F;
               biped.bipedHeadwear.rotateAngleZ = biped.bipedHead.rotateAngleZ = -0.5F;
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
     }

     protected void drawHoveringText(List list, int x, int y, FontRenderer font) {
          if (!list.isEmpty()) {
               GlStateManager.disableRescaleNormal();
               RenderHelper.disableStandardItemLighting();
               GlStateManager.disableLighting();
               GlStateManager.disableDepth();
               int k = 0;
               Iterator iterator = list.iterator();

               int k2;
               while(iterator.hasNext()) {
                    String s = (String)iterator.next();
                    k2 = font.getStringWidth(s);
                    if (k2 > k) {
                         k = k2;
                    }
               }

               int j2 = x + 12;
               k2 = y - 12;
               int i1 = 8;
               if (list.size() > 1) {
                    i1 += 2 + (list.size() - 1) * 10;
               }

               if (j2 + k > this.width) {
                    j2 -= 28 + k;
               }

               if (k2 + i1 + 6 > this.height) {
                    k2 = this.height - i1 - 6;
               }

               this.zLevel = 300.0F;
               this.itemRender.zLevel = 300.0F;
               int j1 = -267386864;
               this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
               this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
               this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
               this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
               this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
               int k1 = 1347420415;
               int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
               this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
               this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
               this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
               this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

               for(int i2 = 0; i2 < list.size(); ++i2) {
                    String s1 = (String)list.get(i2);
                    font.drawStringWithShadow(s1, (float)j2, (float)k2, -1);
                    if (i2 == 0) {
                         k2 += 2;
                    }

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
}
