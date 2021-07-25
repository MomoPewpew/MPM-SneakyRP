package noppes.mpm.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PropGroup;
import noppes.mpm.client.Client;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumPackets;

public class GuiCreationPropLoad extends GuiCreationScreenInterface implements ICustomScrollListener, ITextfieldListener {
     private GuiCustomScroll scroll;
     private static int selected;
     public static GuiCreationPropLoad GuiPropLoad = new GuiCreationPropLoad();
     private Boolean initiating = false;
     private static String searchString;
     private static ArrayList<String> list;
     private static ArrayList<String> currentPropGroupNames;

     public GuiCreationPropLoad() {
   	  	 this.playerdata = ModelData.get(this.getPlayer());
         this.active = 450;
         this.xOffset = 140;
         searchString = "";
         list = new ArrayList<String>();
         currentPropGroupNames = new ArrayList<String>();
    }

     @Override
     public void initGui() {
          this.initiating = true;
          super.initGui();

          Client.sendData(EnumPackets.PROPGROUPS_FILENAME_UPDATE);

          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0, true);
          }

          currentPropGroupNames = new ArrayList<String>();

          if (this.playerdata.propGroups.size() > 0) {
              for (PropGroup propGroup : this.playerdata.propGroups) {
            	  currentPropGroupNames.add(propGroup.name.toLowerCase());
              }
          }

          list = new ArrayList<String>();
          Integer y = MorePlayerModels.fileNamesPropGroups.size();//TODO: fix crash here on fresh server

          for(int n = 0; n < y; ++n) {
        	  if (MorePlayerModels.fileNamesPropGroups.get(n).contains(searchString.toLowerCase())) {
        		  list.add(MorePlayerModels.fileNamesPropGroups.get(n));

        		  if (currentPropGroupNames.contains(MorePlayerModels.fileNamesPropGroups.get(n)))
        			  this.scroll.getSelectedList().add(MorePlayerModels.fileNamesPropGroups.get(n));
        	  }
          }

          this.addTextField(new GuiNpcTextField(451, this, this.guiLeft + 1, this.guiTop + 46, 98, 16, searchString.equals("") ? "Search" : searchString));

          this.scroll.selected = selected;
          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 67;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          int guiOffsetX = this.guiLeft + this.scroll.xSize + 2;

          y = this.guiTop + 44;

          this.addButton(new GuiNpcButton(452, guiOffsetX, y, 50, 20, "gui.refresh"));

          this.initiating = false;
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);

          if (btn.id == 452) {
        	  this.initGui();
          }
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
 		 if (this.initiating || this.scroll.getHover() < 0) return;

    	 selected = this.scroll.getHover();

         if (this.scroll.getSelectedList().contains(this.scroll.getList().get(selected))) {
        	 NBTTagCompound compound = new NBTTagCompound();
        	 compound.setString("propName", list.get(selected));

        	 Client.sendData(EnumPackets.PROPGROUP_LOAD_CLIENT, compound);
         } else {
        	 this.playerdata.removePropGroupByName(list.get(selected));
         }
     }

 	@Override
 	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
 	}

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }

     static {
    	 selected = -1;
    }

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id >= 451) {
			searchString = textField.getText();
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id >= 451) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}
}
