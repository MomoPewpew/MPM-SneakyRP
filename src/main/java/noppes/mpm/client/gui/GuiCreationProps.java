package noppes.mpm.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.ModelData;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
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
     private static PropGroup propGroup;
     private static PropGroup selectedPropGroup = null;
     private static List<Prop> props;
     private static int propGroupAmount;
     private static Prop prop = null;

     public GuiCreationProps() {
   	  	 this.playerdata = ModelData.get(this.getPlayer());
         this.active = 100;
         this.xOffset = 140;

         propGroupAmount = this.playerdata.propGroups.size();

         propGroup = this.playerdata.propBase;
         props = propGroup.props;
         if (selected >= 0) {
          	newProp = true;
         }
    }

     public GuiCreationProps(PropGroup propGroupArg) {
   	  	 this.playerdata = ModelData.get(this.getPlayer());
         this.active = -1;
         this.xOffset = 140;

         propGroupAmount = 0;

         propGroup = propGroupArg;
         props = propGroup.props;
         if (selected >= 0) {
          	newProp = true;
         }
     }

     @Override
     public void initGui() {
          this.initiating = true;
          super.initGui();
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0, false, true);
          }

          ArrayList<String> list = new ArrayList<String>();
          Integer y = props.size();
          this.scroll.colorlist = new ArrayList<Integer>();

          for(int n = 0; n < propGroupAmount; ++n) {
          		list.add(this.playerdata.propGroups.get(n).name);

	       	   if (this.playerdata.propGroups.get(n).hide == true) {
	       		   this.scroll.colorlist.add(8421504);
	       	   } else {
	       		   this.scroll.colorlist.add(255);
	       	   }
          }

          for(int n = 0; n < y; ++n) {
        	   if (props.get(n).name.equals("NONAME")) {
                   list.add(props.get(n).itemStack.getDisplayName());
        	   } else {
                   list.add(props.get(n).name);
        	   }

        	   if (props.get(n).hide == true) {
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

          int guiOffsetX = this.guiLeft + this.scroll.xSize + 2;

          if (newProp) {
        	  this.scroll.selected = selected = props.size() + propGroupAmount - 1;
        	  if (selected >= propGroupAmount) {
            	  prop = props.get(selected - propGroupAmount);
            	  propString = prop.propString;
        	  }
        	  newProp = false;
          }

          y = this.guiTop + 45;
          this.addButton(new GuiNpcButton(101, guiOffsetX, y, 20, 20, "+"));
          if (selected >= propGroupAmount) {
        	  this.addButton(new GuiNpcButton(102, guiOffsetX + 22, y, 20, 20, "-"));
        	  this.addButton(new GuiNpcButton(119, guiOffsetX + 44, y, 54, 20, "gui.duplicate"));
        	  this.addButton(new GuiNpcButton(120, guiOffsetX + 100, y, 35, 20, "gui.give"));
        	  this.addButton(new GuiNpcButton(103, guiOffsetX + 136, y, 84, 20, "gui.copycode"));
        	  y += 22;
              this.addLabel(new GuiNpcLabel(104, "gui.prop", guiOffsetX, y + 5, 16777215));
              this.addTextField(new GuiNpcTextField(104, this, guiOffsetX + 33, y + 1, 145, 18, propString));
              //this.addButton(new GuiNpcButton(123, guiOffsetX + 180, y, 40, 20, "gui.picker"));
        	  y += 22;
        	  this.addLabel(new GuiNpcLabel(105, "gui.bodypart", guiOffsetX, y + 5, 16777215));
              this.addButton(new GuiNpcButton(105, guiOffsetX + 32, y, 69, 20, new String[]{"gui.lefthand", "gui.righthand", "gui.head", "gui.body", "gui.leftfoot", "gui.rightfoot", "gui.model"},
            		  bodyParts.contains(prop.bodyPartName) ? bodyParts.indexOf(prop.bodyPartName) : 0));
              this.addButton(new GuiNpcButton(121, guiOffsetX + 102, y, 50, 20, new String[]{"gui.shown", "gui.hidden"}, prop.hide ? 1 : 0));
              y += 22;
              this.addButton(new GuiNpcButton(106, guiOffsetX, y, 49, 20, "gui.scale"));
              this.addButton(new GuiNpcButton(107, guiOffsetX + 50, y, 50, 20, "gui.offset"));
              this.addButton(new GuiNpcButton(108, guiOffsetX + 102, y, 50, 20, "gui.rotate"));
              y += 22;
              if (sliders == 106) {
                  this.addTextField(new GuiNpcTextField(109, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.scaleX)));
                  this.addSlider(new GuiNpcSlider(this, 109, guiOffsetX, y, 152, 20, (prop.scaleX / maxScale)));
                  this.getSlider(109).displayString = "X";
                  y += 22;
                  this.addTextField(new GuiNpcTextField(110, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.scaleY)));
                  this.addSlider(new GuiNpcSlider(this, 110, guiOffsetX, y, 152, 20, (prop.scaleY / maxScale)));
                  this.getSlider(110).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(111, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.scaleZ)));
                  this.addSlider(new GuiNpcSlider(this, 111, guiOffsetX, y, 152, 20, (prop.scaleZ / maxScale)));
                  this.getSlider(111).displayString = "Z";
              } else if (sliders == 107) {
                  this.addTextField(new GuiNpcTextField(112, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.offsetX)));
                  this.addSlider(new GuiNpcSlider(this, 112, guiOffsetX, y, 152, 20, ((prop.offsetX + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(112).displayString = "X";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(113, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.offsetY)));
                  this.addSlider(new GuiNpcSlider(this, 113, guiOffsetX, y, 152, 20, ((prop.offsetY + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(113).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(114, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", prop.offsetZ)));
                  this.addSlider(new GuiNpcSlider(this, 114, guiOffsetX, y, 152, 20, ((prop.offsetZ + maxOffset) / (maxOffset * 2.0F))));
                  this.getSlider(114).displayString = "Z";
              } else if (sliders == 108) {
                  this.addTextField(new GuiNpcTextField(115, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", prop.rotateX)));
                  this.addSlider(new GuiNpcSlider(this, 115, guiOffsetX, y, 152, 20, ((prop.rotateX + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(115).displayString = "X";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(116, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", prop.rotateY)));
                  this.addSlider(new GuiNpcSlider(this, 116, guiOffsetX, y, 152, 20, ((prop.rotateY + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(116).displayString = "Y";

                  y += 22;
                  this.addTextField(new GuiNpcTextField(117, this, guiOffsetX + 155, y + 1, 36, 18, String.format(java.util.Locale.US,"%.1f", prop.rotateZ)));
                  this.addSlider(new GuiNpcSlider(this, 117, guiOffsetX, y, 152, 20, ((prop.rotateZ + maxRotation) / (maxRotation * 2.0F))));
                  this.getSlider(117).displayString = "Z";
              }

              this.getButton(sliders).enabled = false;

              y += 22;
        	  this.addLabel(new GuiNpcLabel(118, "gui.matchscaling", guiOffsetX, y + 5, 16777215));
              this.addButton(new GuiNpcButton(118, guiOffsetX + 98, y, 55, 20, new String[]{"gui.false", "gui.true"}, prop.matchScaling ? 1 : 0));
              y += 22;
              this.addButton(new GuiNpcButton(122, guiOffsetX, y, 152, 20, "gui.propgroup"));
          } else if (selected >= 0) {
        	  selectedPropGroup = this.playerdata.propGroups.get(selected);
          	  this.addButton(new GuiNpcButton(302, guiOffsetX + 22, y, 20, 20, "-"));
        	  this.addButton(new GuiNpcButton(305, guiOffsetX + 44, y, 54, 20, "gui.duplicate"));
        	  this.addButton(new GuiNpcButton(306, guiOffsetX + 100, y, 35, 20, "gui.give"));
        	  this.addButton(new GuiNpcButton(309, guiOffsetX + 136, y, 84, 20, "gui.copycode"));
        	  y += 22;
              this.addLabel(new GuiNpcLabel(303, "gui.name", guiOffsetX, y + 5, 16777215));
              this.addTextField(new GuiNpcTextField(303, this, guiOffsetX + 33, y, 185, 20, selectedPropGroup.name));
        	  y += 22;
              this.addButton(new GuiNpcButton(307, guiOffsetX, y, 100, 20, "gui.browse"));
              this.addButton(new GuiNpcButton(308, guiOffsetX + 102, y, 50, 20, new String[]{"gui.shown", "gui.hidden"}, selectedPropGroup.hide ? 1 : 0));
          }

          this.initiating = false;
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 101) {
        	   props.add(new Prop("minecraft:stained_glass:2", new ItemStack(Blocks.STAINED_GLASS, 1, 2), "lefthand", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false, false, "NONAME"));
               newProp = true;
               this.initGui();
          } else if (btn.id == 102) {
       	   	  props.remove(selected - propGroupAmount);
       	   	  if (selected == props.size() + propGroupAmount) selected -= 1;
       	   	  if (selected >= propGroupAmount) {
       	   		  prop = props.get(selected - propGroupAmount);
       	   		  propString = prop.propString;
       	   	  }
              this.initGui();
         } else if (btn.id == 103) {
        	 String command = prop.getCommand();
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
        	 prop.bodyPartName = this.bodyParts.get(((GuiNpcButton)btn).getValue());
             this.initGui();
         } else if (btn.id == 118) {
        	 prop.matchScaling = ((GuiNpcButton)btn).getValue() == 1 ? true : false;
             this.initGui();
         } else if (btn.id == 121) {
        	 prop.hide = ((GuiNpcButton)btn).getValue() == 1 ? true : false;
             this.initGui();
         } else if (btn.id == 119) {
        	 Prop propTemp = new Prop();
        	 propTemp.readFromNBT(prop.writeToNBT());
        	 props.add(propTemp);
        	 newProp = true;
             this.initGui();
         } else if (btn.id == 120) {
             this.playerdata.syncPropsClient();
       		 Client.sendData(EnumPackets.PROP_GIVE, selected - propGroupAmount);

             this.initGui();
         } else if (btn.id == 122) {
        	 this.openGui(new GuiCreationPropGroups(selected - propGroupAmount, propGroup));
         } else if (btn.id == 302) {
        	 this.playerdata.propGroups.remove(selected);
        	 propGroupAmount -= 1;

      	   	  if (selected == props.size() + propGroupAmount) selected -= 1;
      	   	  if (selected >= propGroupAmount) {
       	   		  prop = props.get(selected - propGroupAmount);
       	   		  propString = prop.propString;
       	   	  }

        	 this.initGui();
         } else if (btn.id == 305) {
        	 PropGroup propGroupTemp = new PropGroup(this.getPlayer());
        	 propGroupTemp.readFromNBT(selectedPropGroup.writeToNBT());
        	 this.playerdata.propGroups.add(propGroupTemp);
        	 propGroupAmount += 1;
        	 selected = propGroupAmount - 1;

        	 this.initGui();
         } else if (btn.id == 306) {
             this.playerdata.syncPropsClient();
        	 Client.sendData(EnumPackets.PROPGROUP_GIVE, selected);

             this.initGui();
         } else if (btn.id == 307) {
        	 this.openGui(new GuiCreationProps(selectedPropGroup));
         } else if (btn.id == 308) {
        	 selectedPropGroup.hide = ((GuiNpcButton)btn).getValue() == 1 ? true : false;
             this.initGui();
         } else if (btn.id == 309) {
        	 String uuid = UUID.randomUUID().toString();
         	 String command = "/prop group " + uuid;

        	 StringSelection selection = new StringSelection(command);
        	 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        	 clipboard.setContents(selection, selection);

        	 NBTTagCompound compound = new NBTTagCompound();
        	 compound.setTag("propGroup", selectedPropGroup.writeToNBT());
        	 compound.setString("uuid", uuid);

        	 Client.sendData(EnumPackets.PROPGROUP_SAVE, compound);
         } else if (btn.id == 123) {
        	 this.openGui(new GuiCreationPropPicker(prop));
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
                	  prop.scaleX = value;
                  } else if (slider.id == 110) {
                	  prop.scaleY = value;
                  } else if (slider.id == 111) {
                	  prop.scaleZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.2f", value);
        	  } else if (slider.id >= 112 && slider.id <= 114) {
        		  value = ((slider.sliderValue - 0.5F) * (maxOffset * 2.0F));

                  if (slider.id == 112) {
                	  prop.offsetX = value;
                  } else if (slider.id == 113) {
                	  prop.offsetY = value;
                  } else if (slider.id == 114) {
                	  prop.offsetZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.2f", value);
        	  } else if (slider.id >= 115 && slider.id <= 117) {
        		  value = ((slider.sliderValue - 0.5F) * (maxRotation * 2.0F));

                  if (slider.id == 115) {
                	  prop.rotateX = value;
                  } else if (slider.id == 116) {
                	  prop.rotateY = value;
                  } else if (slider.id == 117) {
                	  prop.rotateZ = value;
                  }

                  text = String.format(java.util.Locale.US,"%.1f", value);
        	  }

        	  this.getTextField(slider.id).setText(text);
          }
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          if (this.scroll.selected >= 0) {
              selected = this.scroll.selected;

              if (selected >= propGroupAmount) {
                  prop = props.get(selected - propGroupAmount);
                  propString = prop.propString;
              }

               this.initGui();
          }
     }

 	@Override
 	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
        if (scroll.selected >= propGroupAmount) {
        	this.openGui(new GuiCreationPropRename(prop));
        } else if (scroll.selected >= 0) {
        	this.openGui(new GuiCreationProps(selectedPropGroup));
        }
 	}

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
         if (scroll.selected >= propGroupAmount) {
        	 prop.hide = !prop.hide;

             this.initGui();
         } else if (scroll.selected >= 0) {
        	 this.playerdata.propGroups.get(selected).hide = !this.playerdata.propGroups.get(selected).hide;

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

			if (prop.parsePropString(propString)) {
				prop.propString = propString;
				this.initGui();
			}
		} else if (textField.id == 303) {
			if (textField.getText().equals("")) return;

			selectedPropGroup.name = textField.getText();
			this.initGui();
		} else if (textField.id >= 109 && textField.id <= 117) {
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
              	  prop.scaleX = value;
	            } else if (textField.id == 110) {
              	  prop.scaleY = value;
	            } else if (textField.id == 111) {
              	  prop.scaleZ = value;
	            }
			} else if (textField.id >= 112 && textField.id <= 114) {
				sliderValue = (value + maxOffset) / (maxOffset * 2.0F);

				if (textField.id == 112) {
              	  prop.offsetX = value;
	            } else if (textField.id == 113) {
              	  prop.offsetY = value;
	            } else if (textField.id == 114) {
              	  prop.offsetZ = value;
	            }
			} else if (textField.id >= 115 && textField.id <= 117) {
				sliderValue = (value + maxRotation) / (maxRotation * 2.0F);

	            if (textField.id == 115) {
              	  prop.rotateX = value;
	            } else if (textField.id == 116) {
	              	  prop.rotateY = value;
	            } else if (textField.id == 117) {
	              	  prop.rotateZ = value;
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
