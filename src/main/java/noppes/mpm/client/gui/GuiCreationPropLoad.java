package noppes.mpm.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
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

     public GuiCreationPropLoad() {
   	  	 this.playerdata = ModelData.get(this.getPlayer());
         this.active = 500;
         this.xOffset = 140;
         searchString = "";
         list = new ArrayList<String>();
    }

     @Override
     public void initGui() {
          this.initiating = true;
          super.initGui();

          Client.sendData(EnumPackets.PROPGROUPS_FILENAME_UPDATE);

          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0);
          }

          list = new ArrayList<String>();
          Integer y = MorePlayerModels.fileNamesPropGroups.size();

          for(int n = 0; n < y; ++n) {
        	  if (MorePlayerModels.fileNamesPropGroups.get(n).contains(searchString))
        		  list.add(MorePlayerModels.fileNamesPropGroups.get(n));
          }

          this.addTextField(new GuiNpcTextField(501, this, this.guiLeft + 1, this.guiTop + 46, 98, 16, searchString.equals("") ? "Search" : searchString));

          this.scroll.selected = selected;
          this.scroll.setUnsortedList(list);
          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 67;
          this.scroll.setSize(100, this.ySize - 74);
          this.addScroll(this.scroll);

          int guiOffsetX = this.guiLeft + this.scroll.xSize + 2;

          y = this.guiTop + 44;

          this.addButton(new GuiNpcButton(502, guiOffsetX, y, 50, 20, "gui.refresh"));

          this.initiating = false;
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);

          if (btn.id == 502) {
        	  this.initGui();
          }
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    	 if (scroll.selected < 0) return;

    	 selected = scroll.selected;

    	 NBTTagCompound compound = new NBTTagCompound();
    	 compound.setString("skinName", list.get(selected));

    	 Client.sendData(EnumPackets.UPDATE_PLAYER_DATA_CLIENT, compound);

    	 this.playerdata = ModelData.get(this.getPlayer());

    	 this.initGui();
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

		if (textField.id >= 501) {
			searchString = textField.getText();
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id >= 501) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}
}
