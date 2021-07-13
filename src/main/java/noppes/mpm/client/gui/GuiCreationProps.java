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
import noppes.mpm.Prop;
import noppes.mpm.client.Client;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumPackets;

public class GuiCreationProps extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener, ITextfieldListener {
     private GuiCustomScroll scroll;
     private static int selected;
     private static int sliders = 106;
     private final List<String> bodyParts = Arrays.asList("lefthand", "righthand", "head", "body", "leftfoot", "rightfoot", "model");
     private static String propString;
     public static GuiCreationProps GuiProps = new GuiCreationProps();
     private static boolean newProp = false;
     private static final Float maxScale = 5.0F;
     private static final Float maxOffset = 2.0F;
     private static final Float maxRotation = 180.0F;
     private Boolean initiating = false;
     private final Integer guiOffsetX = this.guiLeft + 158;

     public GuiCreationProps() {
    	  this.playerdata = ModelData.get(this.getPlayer());
          this.active = 100;
          this.xOffset = 140;
          selected = this.playerdata.props.size() - 1;
          if (selected >= 0)
        	  newProp = true;
     }

     @Override
     public void initGui() {
          this.initiating = true;
          super.initGui();
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0, false, true);
          }

          ArrayList<String> list = new ArrayList<String>();
          Integer y = this.playerdata.props.size();
          this.scroll.colorlist = new ArrayList<Integer>();

          for(int n = 0; n < y; ++n) {
        	   if (playerdata.props.get(n).name.equals("NONAME")) {
                   list.add(playerdata.props.get(n).itemStack.getDisplayName());
        	   } else {
                   list.add(playerdata.props.get(n).name);
        	   }

        	   if (this.playerdata.props.get(n).hide == true) {
        		   this.scroll.colorlist.add(8421504);
        	   } else {
        		   this.scroll.colorlist.add(16777215);
        	   }
          }

          this.scroll.selected = selected;
          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 46;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          if (newProp) {
        	  this.scroll.selected = selected = this.playerdata.props.size() - 1;
        	  propString = this.playerdata.props.get(selected).propString;
        	  newProp = false;
          }

          y = this.guiTop + 45;
          this.addButton(new GuiNpcButton(101, this.guiOffsetX, y, 20, 20, "+"));
          if (selected >= 0) {
        	  Prop prop = this.playerdata.props.get(selected);

        	  this.addButton(new GuiNpcButton(102, this.guiOffsetX + 22, y, 20, 20, "-"));
        	  this.addButton(new GuiNpcButton(119, this.guiOffsetX + 44, y, 54, 20, "gui.duplicate"));
        	  this.addButton(new GuiNpcButton(120, this.guiOffsetX + 100, y, 35, 20, "gui.give"));
        	  this.addButton(new GuiNpcButton(103, this.guiOffsetX + 136, y, 84, 20, "gui.copycommand"));
        	  y += 22;
              this.addLabel(new GuiNpcLabel(104, "gui.name", this.guiOffsetX, y + 5, 16777215));
              this.addTextField(new GuiNpcTextField(104, this, this.guiOffsetX + 33, y, 185, 20, propString));
        	  y += 22;
        	  this.addLabel(new GuiNpcLabel(105, "gui.bodypart", this.guiOffsetX, y + 5, 16777215));
              this.addButton(new GuiNpcButton(105, this.guiOffsetX + 32, y, 69, 20, new String[]{"gui.lefthand", "gui.righthand", "gui.head", "gui.body", "gui.leftfoot", "gui.rightfoot", "gui.model"},
            		  bodyParts.contains(prop.bodyPartName) ? bodyParts.indexOf(prop.bodyPartName) : 0));
              this.addButton(new GuiNpcButton(121, this.guiOffsetX + 102, y, 50, 20, new String[]{"gui.shown", "gui.hidden"}, playerdata.props.get(selected).hide ? 1 : 0));
              y += 22;
              this.addButton(new GuiNpcButton(106, this.guiOffsetX, y, 49, 20, "gui.scale"));
              this.addButton(new GuiNpcButton(107, this.guiOffsetX + 50, y, 50, 20, "gui.offset"));
              this.addButton(new GuiNpcButton(108, this.guiOffsetX + 102, y, 50, 20, "gui.rotate"));
              y += 22;
              if (sliders == 106) {
                  this.addTextField(new GuiNpcTextField(109, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).scaleX)));
                  this.addSlider(new GuiNpcSlider(this, 109, this.guiOffsetX, y, 152, 20, (this.playerdata.props.get(selected).scaleX / maxScale)));
                  this.getSlider(109).displayString = "X";
                  y += 22;
                  this.addTextField(new GuiNpcTextField(110, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).scaleY)));
                  this.addSlider(new GuiNpcSlider(this, 110, this.guiOffsetX, y, 152, 20, (this.playerdata.props.get(selected).scaleY / maxScale)));
                  this.getSlider(110).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(111, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).scaleZ)));
                  this.addSlider(new GuiNpcSlider(this, 111, this.guiOffsetX, y, 152, 20, (this.playerdata.props.get(selected).scaleZ / maxScale)));
                  this.getSlider(111).displayString = "Z";
              } else if (sliders == 107) {
                  this.addTextField(new GuiNpcTextField(112, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).offsetX)));
                  this.addSlider(new GuiNpcSlider(this, 112, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).offsetX + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(112).displayString = "X";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(113, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).offsetY)));
                  this.addSlider(new GuiNpcSlider(this, 113, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).offsetY + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(113).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(114, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.props.get(selected).offsetZ)));
                  this.addSlider(new GuiNpcSlider(this, 114, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).offsetZ + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(114).displayString = "Z";
              } else if (sliders == 108) {
                  this.addTextField(new GuiNpcTextField(115, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", this.playerdata.props.get(selected).rotateX)));
                  this.addSlider(new GuiNpcSlider(this, 115, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).rotateX + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(115).displayString = "X";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(116, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", this.playerdata.props.get(selected).rotateY)));
                  this.addSlider(new GuiNpcSlider(this, 116, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).rotateY + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(116).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(117, this, this.guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", this.playerdata.props.get(selected).rotateZ)));
                  this.addSlider(new GuiNpcSlider(this, 117, this.guiOffsetX, y, 152, 20, ((this.playerdata.props.get(selected).rotateZ + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(117).displayString = "Z";
              }

              this.getButton(sliders).enabled = false;

              y += 22;
        	  this.addLabel(new GuiNpcLabel(118, "gui.matchscaling", this.guiOffsetX, y + 5, 16777215));
              this.addButton(new GuiNpcButton(118, this.guiOffsetX + 98, y, 55, 20, new String[]{"gui.false", "gui.true"}, playerdata.props.get(selected).matchScaling ? 1 : 0));
          }

          this.initiating = false;
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 101) {
        	   this.playerdata.props.add(new Prop("minecraft:crafting_table", new ItemStack(Blocks.CRAFTING_TABLE), "lefthand", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false, false, "NONAME"));
               newProp = true;
               this.initGui();
          } else if (btn.id == 102) {
       	   	  this.playerdata.props.remove(selected);
       	   	  if (selected == this.playerdata.props.size()) selected -= 1;
       	   	  if (selected >= 0) propString = this.playerdata.props.get(selected).propString;
              this.initGui();
         } else if (btn.id == 103) {
        	 String command = this.playerdata.props.get(selected).getCommand();
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
        	 this.playerdata.props.get(selected).bodyPartName = this.bodyParts.get(((GuiNpcButton)btn).getValue());
             this.initGui();
         } else if (btn.id == 118) {
        	 this.playerdata.props.get(selected).matchScaling = ((GuiNpcButton)btn).getValue() == 1 ? true : false;
             this.initGui();
         } else if (btn.id == 121) {
        	 this.playerdata.props.get(selected).hide = ((GuiNpcButton)btn).getValue() == 1 ? true : false;
             this.initGui();
         } else if (btn.id == 119) {
        	 Prop prop = this.playerdata.props.get(selected);
        	 this.playerdata.props.add(new Prop(prop.propString, prop.itemStack, prop.bodyPartName,
				 prop.scaleX, prop.scaleY, prop.scaleZ,
				 prop.offsetX, prop.offsetY, prop.offsetZ,
				 prop.rotateX, prop.rotateY, prop.rotateZ,
				 prop.matchScaling, prop.hide, prop.name));
             this.initGui();
         } else if (btn.id == 120) {
             this.playerdata.propSyncClient();
       		 Client.sendData(EnumPackets.PROP_GIVE, selected);

             this.initGui();
         }
     }

     @Override
     public void mouseDragged(GuiNpcSlider slider) {
          super.mouseDragged(slider);
          if (this.initiating) return;

          if (slider.id >= 109 && slider.id <= 117) {
              Float value = 0.0F;
              String text = "";

        	  if (slider.id >= 109 && slider.id <= 111) {
                  value = (slider.sliderValue * maxScale);

                  if (slider.id == 109) {
                	  this.playerdata.props.get(selected).scaleX = value;
                  } else if (slider.id == 110) {
                	  this.playerdata.props.get(selected).scaleY = value;
                  } else if (slider.id == 111) {
                	  this.playerdata.props.get(selected).scaleZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.2f", value);
        	  } else if (slider.id >= 112 && slider.id <= 114) {
        		  value = ((slider.sliderValue - 0.5F) * (maxOffset * 2.0F));

                  if (slider.id == 112) {
                	  this.playerdata.props.get(selected).offsetX = value;
                  } else if (slider.id == 113) {
                	  this.playerdata.props.get(selected).offsetY = value;
                  } else if (slider.id == 114) {
                	  this.playerdata.props.get(selected).offsetZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.2f", value);
        	  } else if (slider.id >= 115 && slider.id <= 117) {
        		  value = ((slider.sliderValue - 0.5F) * (maxRotation * 2.0F));

                  if (slider.id == 115) {
                	  this.playerdata.props.get(selected).rotateX = value;
                  } else if (slider.id == 116) {
                	  this.playerdata.props.get(selected).rotateY = value;
                  } else if (slider.id == 117) {
                	  this.playerdata.props.get(selected).rotateZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.1f", value);
        	  }

        	  this.getTextField(slider.id).setText(text);
          }
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          if (scroll.selected >= 0) {
              selected = scroll.selected;
              propString = playerdata.props.get(selected).propString;

               this.initGui();
          }
     }

 	@Override
 	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
        if (scroll.selected >= 0) {

        }
 	}

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
         if (scroll.selected >= 0) {
        	 playerdata.props.get(selected).hide = !playerdata.props.get(selected).hide;

             this.initGui();
         }

     }

     static {
    	 selected = -1;
    }

	@Override
	public void unFocused(GuiNpcTextField textField) {

		if (this.initiating) return;

		if (textField.id == 104) {
			propString = textField.getText();

			try {
				this.playerdata.props.get(selected).itemStack = new ItemStack(CommandBase.getItemByText(this.getPlayer(), propString));
				this.playerdata.props.get(selected).propString = propString;
				this.initGui();
			} catch (NumberInvalidException e) {

			}
		}

		if (textField.id >= 109 && textField.id <= 117) {
			Float value = null;
			try {
			    value = Float.parseFloat(textField.getText().replace(',', '.'));
			} catch (NumberFormatException e) {
			    return;
			}

			Float sliderValue = 0.0F;

			if (textField.id >= 109 && textField.id <= 111) {
				sliderValue = value / maxScale;

	            if (textField.id == 109) {
              	  this.playerdata.props.get(selected).scaleX = value;
	            } else if (textField.id == 110) {
              	  this.playerdata.props.get(selected).scaleY = value;
	            } else if (textField.id == 111) {
              	  this.playerdata.props.get(selected).scaleZ = value;
	            }
			} else if (textField.id >= 112 && textField.id <= 114) {
				sliderValue = (value + maxOffset) / (maxOffset * 2.0F);

				if (textField.id == 112) {
              	  this.playerdata.props.get(selected).offsetX = value;
	            } else if (textField.id == 113) {
              	  this.playerdata.props.get(selected).offsetY = value;
	            } else if (textField.id == 114) {
              	  this.playerdata.props.get(selected).offsetZ = value;
	            }
			} else if (textField.id >= 115 && textField.id <= 117) {
				sliderValue = (value + maxRotation) / (maxRotation * 2.0F);

	            if (textField.id == 115) {
              	  this.playerdata.props.get(selected).rotateX = value;
	            } else if (textField.id == 116) {
	              	  this.playerdata.props.get(selected).rotateY = value;
	            } else if (textField.id == 117) {
	              	  this.playerdata.props.get(selected).rotateZ = value;
	            }
			}

			textField.setCursorPositionZero();
			textField.setSelectionPos(0);
			this.getSlider(textField.id).sliderValue = sliderValue;
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {

		if (this.initiating) return;

		if (textField.id >= 109 && textField.id <= 117) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}
}
