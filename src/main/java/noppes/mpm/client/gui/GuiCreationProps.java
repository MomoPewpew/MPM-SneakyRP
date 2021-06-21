package noppes.mpm.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import noppes.mpm.ModelData;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationProps extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener, ITextfieldListener {
     private GuiCustomScroll scroll;
     private static int selected;
     private static int sliders = 106;
     private final List<String> bodyParts = Arrays.asList("lefthand", "righthand", "head", "body", "leftfoot", "rightfoot", "model");
     private static String propName;
     public static GuiCreationProps GuiProps = new GuiCreationProps();
     private static boolean newProp = false;

     public GuiCreationProps() {
    	  this.playerdata = ModelData.get(this.getPlayer());
          this.active = 100;
          this.xOffset = 140;
          this.selected = this.playerdata.propItemStack.size() - 1;
     }

     @Override
     public void initGui() {
          super.initGui();
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0);
          }

          ArrayList<String> list = new ArrayList<String>();
          Integer y = this.playerdata.propItemStack.size();

          for(int n = 0; n < y; ++n) {
               String itemStack = playerdata.propItemStack.get(n).getDisplayName();
               list.add(itemStack);
          }

          this.scroll.selected = this.selected;
          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 46;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          if (newProp) {
        	  this.scroll.selected = selected = this.playerdata.propItemStack.size() - 1;
        	  propName = this.playerdata.propItemStack.get(selected).getItem().getRegistryName().toString();
        	  newProp = false;
          }

          y = this.guiTop + 45;
          this.addButton(new GuiNpcButton(101, this.guiLeft + 112, y, 72, 20, "gui.addprop"));
          if (selected >= 0) {
        	  this.addButton(new GuiNpcButton(102, this.guiLeft + 186, y, 72, 20, "gui.deleteprop"));
        	  this.addButton(new GuiNpcButton(103, this.guiLeft + 260, y, 72, 20, "gui.copycommand"));
        	  y += 22;
              this.addLabel(new GuiNpcLabel(104, "gui.name", this.guiLeft + 112, y + 5, 16777215));
              this.addTextField(new GuiNpcTextField(104, this, this.guiLeft + 145, y, 185, 20, propName));
        	  y += 22;
        	  this.addLabel(new GuiNpcLabel(105, "gui.bodypart", this.guiLeft + 112, y + 5, 16777215));
              this.addButton(new GuiNpcButton(105, this.guiLeft + 152, y, 70, 20, new String[]{"gui.lefthand", "gui.righthand", "gui.head", "gui.body", "gui.leftfoot", "gui.rightfoot", "gui.model"},
            		  bodyParts.contains(this.playerdata.propBodyPartName.get(selected)) ? bodyParts.indexOf(this.playerdata.propBodyPartName.get(selected)) : 0));
              y += 22;
              this.addButton(new GuiNpcButton(106, this.guiLeft + 112, y, 50, 20, "gui.scale"));
              this.addButton(new GuiNpcButton(107, this.guiLeft + 163, y, 50, 20, "gui.offset"));
              this.addButton(new GuiNpcButton(108, this.guiLeft + 214, y, 50, 20, "gui.rotate"));
              y += 22;
              if (sliders == 106) {
                  this.addSlider(new GuiNpcSlider(this, 109, this.guiLeft + 112, y, 152, 20, (this.playerdata.propScaleX.get(selected) / 5.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 110, this.guiLeft + 112, y, 152, 20, (this.playerdata.propScaleY.get(selected) / 5.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 111, this.guiLeft + 112, y, 152, 20, (this.playerdata.propScaleZ.get(selected) / 5.0F)));
              } else if (sliders == 107) {
                  this.addSlider(new GuiNpcSlider(this, 112, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propOffsetX.get(selected) + 2.0F) / 4.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 113, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propOffsetY.get(selected) + 2.0F) / 4.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 114, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propOffsetZ.get(selected) + 2.0F) / 4.0F)));
              } else if (sliders == 108) {
                  this.addSlider(new GuiNpcSlider(this, 115, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propRotateX.get(selected) + 180.0F) / 360.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 116, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propRotateY.get(selected) + 180.0F) / 360.0F)));
                  y += 22;
                  this.addSlider(new GuiNpcSlider(this, 117, this.guiLeft + 112, y, 152, 20, ((this.playerdata.propRotateZ.get(selected) + 180.0F) / 360.0F)));
              }

              this.getButton(sliders).enabled = false;
          }
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 101) {
        	   this.playerdata.addPropClient(new ItemStack(Blocks.CRAFTING_TABLE), "lefthand", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
               newProp = true;
               this.initGui();
          } else if (btn.id == 102) {
       	   	  this.playerdata.removePropClient(selected);
       	   	  if (selected == this.playerdata.propItemStack.size()) selected -= 1;
       	   	  if (selected >= 0) propName = this.playerdata.propItemStack.get(selected).getItem().getRegistryName().toString();
              this.initGui();
         } else if (btn.id == 103) {
        	 String command = "/prop " +
        			 this.playerdata.propItemStack.get(selected).getItem().getRegistryName().toString() + " " + this.playerdata.propBodyPartName.get(selected) + " " +
        			 this.playerdata.propScaleX.get(selected) + " " + this.playerdata.propScaleY.get(selected) + " " + this.playerdata.propScaleZ.get(selected) + " " +
        			 this.playerdata.propOffsetX.get(selected) + " " + this.playerdata.propOffsetY.get(selected) + " " + this.playerdata.propOffsetZ.get(selected) + " " +
        			 this.playerdata.propRotateX.get(selected) + " " + this.playerdata.propRotateY.get(selected) + " " + this.playerdata.propRotateZ.get(selected);
        	 StringSelection selection = new StringSelection(command);
        	 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	 clipboard.setContents(selection, selection);
         } else if (btn.id == 106) {
        	 sliders = 106;
             this.initGui();
         } else if (btn.id == 107) {
        	 sliders = 107;
             this.initGui();
         } else if (btn.id == 108) {
        	 sliders = 108;
             this.initGui();
         } else if (btn.id == 105) {
        	 this.playerdata.propBodyPartName.set(selected, this.bodyParts.get(((GuiNpcButton)btn).getValue()));
             this.initGui();
         }
     }

     @Override
     public void mouseDragged(GuiNpcSlider slider) {
          super.mouseDragged(slider);
          if (slider.id >= 109 && slider.id <= 117) {
              String axis = null;
              Float value;

        	  if (slider.id >= 109 && slider.id <= 111) {
                  value = (slider.sliderValue * 5.0F);

                  if (slider.id == 109) {
                	  this.playerdata.propScaleX.set(selected, value);
                	  axis = "X : ";
                  } else if (slider.id == 110) {
                	  this.playerdata.propScaleY.set(selected, value);
                	  axis = "Y : ";
                  } else if (slider.id == 111) {
                	  this.playerdata.propScaleZ.set(selected, value);
                	  axis = "Z : ";
                  }

                  slider.setString(axis + String.format(java.util.Locale.US,"%.2f", value));
        	  }

        	  if (slider.id >= 112 && slider.id <= 114) {
        		  value = ((slider.sliderValue - 0.5F) * 4.0F);

                  if (slider.id == 112) {
                	  this.playerdata.propOffsetX.set(selected, value);
                	  axis = "X : ";
                  } else if (slider.id == 113) {
                	  this.playerdata.propOffsetY.set(selected, value);
                	  axis = "Y : ";
                  } else if (slider.id == 114) {
                	  this.playerdata.propOffsetZ.set(selected, value);
                	  axis = "Z : ";
                  }

                  slider.setString(axis + String.format(java.util.Locale.US,"%.2f", value));
        	  }

        	  if (slider.id >= 115 && slider.id <= 117) {
        		  value = ((slider.sliderValue - 0.5F) * 360.0F);

                  if (slider.id == 115) {
                	  this.playerdata.propRotateX.set(selected, value);
                	  axis = "X : ";
                  } else if (slider.id == 116) {
                	  this.playerdata.propRotateY.set(selected, value);
                	  axis = "Y : ";
                  } else if (slider.id == 117) {
                	  this.playerdata.propRotateZ.set(selected, value);
                	  axis = "Z : ";
                  }

                  slider.setString(axis + String.format(java.util.Locale.US,"%.1f", value));
        	  }
          }
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          if (scroll.selected >= 0) {
               selected = scroll.selected;
               propName = this.playerdata.propItemStack.get(selected).getItem().getRegistryName().toString();
               this.initGui();
          }
     }

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }

     static {
    	 selected = -1;
    }

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (textField.id == 104) {
			propName = textField.getText();

			try {
				this.playerdata.propItemStack.set(selected, new ItemStack(CommandBase.getItemByText(this.getPlayer(), textField.getText())));
			} catch (NumberInvalidException e) {

			}
		}
	}
}
