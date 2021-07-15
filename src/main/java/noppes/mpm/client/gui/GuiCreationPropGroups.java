package noppes.mpm.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import noppes.mpm.ModelData;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropGroups extends GuiCreationScreenInterface implements ICustomScrollListener, ITextfieldListener {
    private GuiCustomScroll scroll;
    private static int selected;
    private static Prop prop = null;
    private static PropGroup sourceGroup = null;
    private static PropGroup selectedPropGroup = null;
    private static int propIndex;
    private Boolean initiating = false;
    private final int guiOffsetX = this.guiLeft + 158;
    private static boolean newPropGroup = false;

    public GuiCreationPropGroups(int index, PropGroup propGroupArg) {
  	  	 this.playerdata = ModelData.get(this.getPlayer());
         this.active = -1;
         this.xOffset = 140;

         propIndex = index;
         sourceGroup = propGroupArg;
         prop = sourceGroup.props.get(propIndex);
         selected = this.playerdata.propGroups.size() - 1;
         if (selected >= 0) {
        	 selectedPropGroup = this.playerdata.propGroups.get(selected);
         }
    }

    @Override
    public void initGui() {
         this.initiating = true;
         super.initGui();
         if (this.scroll == null) {
             this.scroll = new GuiCustomScroll(this, 0);
         }

         ArrayList<String> list = new ArrayList<String>();
         Integer y = this.playerdata.propGroups.size();

         for(int n = 0; n < y; ++n) {
        	 list.add(this.playerdata.propGroups.get(n).name);
        	 this.scroll.colorlist.add(255);
         }

        this.scroll.selected = selected;
        this.scroll.setUnsortedList(list);
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.ySize - 74);
        this.addScroll(this.scroll);

        if (newPropGroup) {
      	  this.scroll.selected = selected = this.playerdata.propGroups.size() - 1;
      	  selectedPropGroup = this.playerdata.propGroups.get(selected);
      	  newPropGroup = false;
        }

        y = this.guiTop + 45;
        this.addButton(new GuiNpcButton(301, this.guiOffsetX, y, 20, 20, "+"));
        if (selected >= 0) {
        	this.addButton(new GuiNpcButton(302, this.guiOffsetX + 22, y, 20, 20, "-"));
        	y += 22;
            this.addLabel(new GuiNpcLabel(303, "gui.name", this.guiOffsetX, y + 5, 16777215));
            this.addTextField(new GuiNpcTextField(303, this, this.guiOffsetX + 33, y, 185, 20, selectedPropGroup.name));
            y += 22;
            this.addButton(new GuiNpcButton(304, this.guiOffsetX, y, 100, 20, "gui.confirmmove"));
        }

        this.initiating = false;
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
         super.actionPerformed(btn);
         if (btn.id == 301) {
        	 PropGroup propGroupTemp = new PropGroup(this.getPlayer());
        	 propGroupTemp.name = "propGroup" + String.valueOf(this.playerdata.propGroups.size() + 1);
        	 this.playerdata.propGroups.add(propGroupTemp);
        	 newPropGroup = true;
        	 this.initGui();
         } else if (btn.id == 302) {
        	 this.playerdata.propGroups.remove(selected);
      	   	  if (selected == this.playerdata.propGroups.size()) selected -= 1;
      	   	  if (selected >= 0) {
      	   		  selectedPropGroup = this.playerdata.propGroups.get(selected);
      	   	  }
        	 this.initGui();
         } else if (btn.id == 304) {
        	 Prop propTemp = new Prop();
        	 propTemp.readFromNBT(prop.writeToNBT());
        	 selectedPropGroup.props.add(propTemp);

        	 sourceGroup.props.remove(propIndex);

        	 this.openGui(new GuiCreationProps());
         }
    }

	@Override
	public void scrollClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
        if (scroll.selected >= 0) {
            selected = scroll.selected;

            if (selected >= 0) {
            	selectedPropGroup = this.playerdata.propGroups.get(selected);
            }

             this.initGui();
        }

	}

	@Override
	public void scrollDoubleClicked(String var1, GuiCustomScroll var2) {

	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {

	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (this.initiating) return;


		if (textField.id == 303) {
			if (textField.getText().equals("")) return;

			selectedPropGroup.name = textField.getText();
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

	}
}