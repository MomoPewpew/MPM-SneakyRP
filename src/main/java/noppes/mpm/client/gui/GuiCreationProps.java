package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationProps extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener {
     private GuiCustomScroll scroll;
     private List data = new ArrayList();
     private static int selected;
	 EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	 ModelData modelData = ModelData.get(player);

     public GuiCreationProps() {
          this.active = 3;
          this.xOffset = 140;
     }

     @Override
     public void initGui() {


          super.initGui();
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0);
          }

          ArrayList list = new ArrayList();
          this.data.clear();
          Integer y = modelData.propItemStack.size();

          for(int n = 0; n < y; ++n) {
               String itemStack = modelData.propItemStack.get(n).getDisplayName();
               this.data.add(itemStack);
               list.add(itemStack);
          }

          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 46;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          y = this.guiTop + 45;
          this.addButton(new GuiNpcButton(101, this.guiLeft + 124, y, 60, 20, "gui.addprop"));

          if (selected >= 0) {
        	  this.addButton(new GuiNpcButton(102, this.guiLeft + 186, y, 60, 20, "gui.deleteprop"));
          }
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 101) {
        	   modelData.addProp(new ItemStack(Blocks.CRAFTING_TABLE), "lefthand", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
               selected = modelData.propItemStack.size();
               this.initGui();
          } else if (btn.id == 102) {
       	   	  modelData.removeProp(selected);
              selected = modelData.propItemStack.size();
              this.initGui();
         }
     }

     @Override
     public void mouseDragged(GuiNpcSlider slider) {
          super.mouseDragged(slider);
          if (slider.id >= 10 && slider.id <= 12) {
               int percent = (int)(50.0F + slider.sliderValue * 100.0F);
               slider.setString(percent + "%");
               if (slider.id == 10) {

               }

               if (slider.id == 11) {

               }

               if (slider.id == 12) {

               }

               this.updateTransate();
          }

     }

     private void updateTransate() {
          EnumParts[] var1 = EnumParts.values();
          int var2 = var1.length;

          for(int var3 = 0; var3 < var2; ++var3) {
               EnumParts part = var1[var3];
               ModelPartConfig config = this.playerdata.getPartConfig(part);
               if (config != null) {
                    if (part == EnumParts.HEAD) {
                         config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
                    } else {
                         ModelPartConfig leg;
                         float x;
                         float y;
                         if (part == EnumParts.ARM_LEFT) {
                              leg = this.playerdata.getPartConfig(EnumParts.BODY);
                              x = (1.0F - leg.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
                              y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
                              config.setTranslate(-x, y, 0.0F);
                              if (!config.notShared) {
                                   ModelPartConfig arm = this.playerdata.getPartConfig(EnumParts.ARM_RIGHT);
                                   arm.copyValues(config);
                              }
                         } else if (part == EnumParts.ARM_RIGHT) {
                              leg = this.playerdata.getPartConfig(EnumParts.BODY);
                              x = (1.0F - leg.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
                              y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
                              config.setTranslate(x, y, 0.0F);
                         } else if (part == EnumParts.LEG_LEFT) {
                              config.setTranslate(config.scaleX * 0.125F - 0.113F, this.playerdata.getLegsY(), 0.0F);
                              if (!config.notShared) {
                                   leg = this.playerdata.getPartConfig(EnumParts.LEG_RIGHT);
                                   leg.copyValues(config);
                              }
                         } else if (part == EnumParts.LEG_RIGHT) {
                              config.setTranslate((1.0F - config.scaleX) * 0.125F, this.playerdata.getLegsY(), 0.0F);
                         } else if (part == EnumParts.BODY) {
                              config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
                         }
                    }
               }
          }

     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          if (scroll.selected >= 0) {
               selected = scroll.selected;
               this.initGui();
          }
     }

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }

     static {
         selected = -1;
    }
}
