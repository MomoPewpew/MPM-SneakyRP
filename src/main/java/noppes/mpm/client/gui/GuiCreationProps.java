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
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationProps extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener {
     private GuiCustomScroll scroll;
     private List data = new ArrayList();
     private static int selected;
     private static int sliders = 106;

     public GuiCreationProps() {
          this.active = 100;
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
          Integer y = playerdata.propItemStack.size();

          for(int n = 0; n < y; ++n) {
               String itemStack = playerdata.propItemStack.get(n).getDisplayName();
               this.data.add(itemStack);
               list.add(itemStack);
          }

          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 46;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          y = this.guiTop + 45;
          this.addButton(new GuiNpcButton(101, this.guiLeft + 112, y, 72, 20, "gui.addprop"));

          if (selected >= 0) {
        	  this.addButton(new GuiNpcButton(102, this.guiLeft + 186, y, 72, 20, "gui.deleteprop"));
        	  this.addButton(new GuiNpcButton(102, this.guiLeft + 260, y, 72, 20, "gui.copycommand"));
        	  y += 22;
              this.addLabel(new GuiNpcLabel(104, "gui.name", this.guiLeft + 112, y + 5, 16777215));
              this.addTextField(new GuiNpcTextField(104, this, this.guiLeft + 150, y, 200, 20, this.playerdata.propItemStack.get(selected).getItem().getRegistryName().toString()));
        	  y += 22;
        	  this.addLabel(new GuiNpcLabel(105, "gui.bodypart", this.guiLeft + 112, y + 5, 16777215));
              this.addButton(new GuiNpcButton(105, this.guiLeft + 150, y, 70, 20, new String[]{"gui.lefthand", "gui.righthand", "gui.head", "gui.body", "gui.leftfoot", "gui.rightfoot", "gui.model"}, 0));
              y += 22;
              this.addButton(new GuiNpcButton(106, this.guiLeft + 112, y, 42, 20, "gui.scale"));
              this.addButton(new GuiNpcButton(107, this.guiLeft + 156, y, 42, 20, "gui.offset"));
              this.addButton(new GuiNpcButton(108, this.guiLeft + 200, y, 42, 20, "gui.rotate"));
              y += 22;
              if (sliders == 106) {
                  this.addLabel(new GuiNpcLabel(109, "gui.x", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 109, this.guiLeft + 150, y, 100, 20, (this.playerdata.propScaleX.get(selected) / 4.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(110, "gui.y", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 110, this.guiLeft + 150, y, 100, 20, (this.playerdata.propScaleY.get(selected) / 4.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(111, "gui.z", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 111, this.guiLeft + 150, y, 100, 20, (this.playerdata.propScaleZ.get(selected) / 4.0F)));
              } else if (sliders == 107) {
                  this.addLabel(new GuiNpcLabel(112, "gui.x", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 112, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propOffsetX.get(selected) + 2.0F) / 4.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(113, "gui.y", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 113, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propOffsetY.get(selected) + 2.0F) / 4.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(114, "gui.z", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 114, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propOffsetZ.get(selected) + 2.0F) / 4.0F)));
              } else if (sliders == 108) {
                  this.addLabel(new GuiNpcLabel(115, "gui.x", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 115, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propRotateX.get(selected) + 180.0F) / 360.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(116, "gui.y", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 116, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propRotateY.get(selected) + 180.0F) / 360.0F)));
                  y += 22;
                  this.addLabel(new GuiNpcLabel(117, "gui.z", this.guiLeft + 127, y + 5, 16777215));
                  this.addSlider(new GuiNpcSlider(this, 117, this.guiLeft + 150, y, 100, 20, ((this.playerdata.propRotateZ.get(selected) + 180.0F) / 360.0F)));
              }

              this.getButton(sliders).enabled = false;
          }
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 101) {
        	   playerdata.addProp(new ItemStack(Blocks.CRAFTING_TABLE), "lefthand", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
               selected = playerdata.propItemStack.size();
               this.initGui();
          } else if (btn.id == 102) {
       	   	  playerdata.removeProp(selected);
              selected -= 1;
              this.initGui();
         } else if (btn.id == 106) {
        	 this.sliders = 106;
             this.initGui();
         } else if (btn.id == 107) {
        	 this.sliders = 107;
             this.initGui();
         } else if (btn.id == 108) {
        	 this.sliders = 108;
             this.initGui();
         }
     }

     @Override
     public void mouseDragged(GuiNpcSlider slider) {
          super.mouseDragged(slider);
          if (slider.id >= 109 && slider.id <= 117) {
        	  if (slider.id >= 109 && slider.id <= 111) {
                  Float value = (slider.sliderValue * 4.0F);
                  slider.setString(String.format(java.util.Locale.US,"%.2f", value));

                  if (slider.id == 109) {
                	  this.playerdata.propScaleX.set(selected, value);
                  } else if (slider.id == 110) {
                	  this.playerdata.propScaleY.set(selected, value);
                  } else if (slider.id == 111) {
                	  this.playerdata.propScaleZ.set(selected, value);
                  }
        	  }

        	  if (slider.id >= 112 && slider.id <= 114) {
        		  Float value = ((slider.sliderValue - 0.5F) * 4.0F);
                  slider.setString(String.format(java.util.Locale.US,"%.2f", value));

                  if (slider.id == 112) {
                	  this.playerdata.propOffsetX.set(selected, value);
                  } else if (slider.id == 113) {
                	  this.playerdata.propOffsetY.set(selected, value);
                  } else if (slider.id == 114) {
                	  this.playerdata.propOffsetZ.set(selected, value);
                  }
        	  }

        	  if (slider.id >= 115 && slider.id <= 117) {
        		  Float value = ((slider.sliderValue - 0.5F) * 360.0F);
                  slider.setString(String.format(java.util.Locale.US,"%.0f", value));

                  if (slider.id == 115) {
                	  this.playerdata.propRotateX.set(selected, value);
                  } else if (slider.id == 116) {
                	  this.playerdata.propRotateY.set(selected, value);
                  } else if (slider.id == 117) {
                	  this.playerdata.propRotateZ.set(selected, value);
                  }
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
