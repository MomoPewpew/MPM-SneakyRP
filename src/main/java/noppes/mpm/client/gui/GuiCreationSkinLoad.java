package noppes.mpm.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.Client;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumPackets;

public class GuiCreationSkinLoad extends GuiCreationScreenInterface implements ICustomScrollListener, ITextfieldListener {
	private GuiCustomScroll scroll;
	private static int selected;
	public static GuiCreationSkinLoad GuiSkinLoad = new GuiCreationSkinLoad();
	private Boolean initiating = false;
	private static String searchString;
	private static ArrayList<String> list;

	public GuiCreationSkinLoad() {
		this.playerdata = ModelData.get(this.getPlayer());
		this.active = 400;
		this.xOffset = 140;
		searchString = "";
		list = new ArrayList<String>();
	}

	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		Client.sendData(EnumPackets.SKIN_FILENAME_UPDATE);

		if (this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0);
		}

		list = new ArrayList<String>();
		Integer y = MorePlayerModels.fileNamesSkins.size();

		for(int n = 0; n < y; ++n) {
			if (MorePlayerModels.fileNamesSkins.get(n).contains(searchString.toLowerCase()))
			list.add(MorePlayerModels.fileNamesSkins.get(n));
		}

		this.addTextField(new GuiNpcTextField(401, this, this.guiLeft + 1, this.guiTop + 46, 98, 16, searchString.equals("") ? "Search" : searchString));

		this.scroll.selected = selected;
		this.scroll.setUnsortedList(list);
		this.scroll.guiLeft = this.guiLeft;
		this.scroll.guiTop = this.guiTop + 45 + 22;
		this.scroll.setSize(100, this.ySize - 52 - 22);
		this.addScroll(this.scroll);

		int guiOffsetX = this.guiLeft + this.scroll.xSize + 2;

		y = this.guiTop + 44;

		this.addButton(new GuiNpcButton(402, guiOffsetX, y, 50, 20, "gui.refresh"));

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);

		if (btn.id == 402) {
			this.initGui();
		}
	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		if (this.scroll.selected < 0) return;

		selected = this.scroll.selected;

		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("skinName", list.get(selected));

		Client.sendData(EnumPackets.UPDATE_PLAYER_DATA_CLIENT, compound);
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

		if (textField.id >= 401) {
			searchString = textField.getText();
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id >= 401) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField textField) {
		// TODO Auto-generated method stub

	}
}
