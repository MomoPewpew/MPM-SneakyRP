package noppes.mpm.client.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.PropGroup;
import noppes.mpm.client.Client;
import noppes.mpm.Server;
import noppes.mpm.Emote;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumPackets;

public class GuiCreationEmoteLoad extends GuiCreationScreenInterface implements ICustomScrollListener, ITextfieldListener {
	public static String searchString = "";
	public static int selected = -1;
	public static ArrayList<String> scrollList = null;//readonly
	public static boolean hasCachedEmoteFileNamesChanged = true;
	public static boolean isrequestingremoval = false;
	public static boolean isrequestingreset = false;
	public static ArrayList<String> cachedEmoteFileNames = new ArrayList<String>();

	public GuiCustomScroll scroll;
	public boolean initiating = false;

	public GuiCreationEmoteLoad() {
		this.playerdata = ModelData.get(this.getPlayer());
		this.active = 651;
		this.xOffset = 140;

		Client.sendData(EnumPackets.EMOTE_FILENAME_UPDATE);
		isrequestingremoval = false;
		isrequestingreset = false;
	}

	public static String getSelectedEmoteName(int selected) {
		String str = scrollList.get(selected);
		if(str.contains(" (vault)")) {
			str = str.substring(0, str.length() - 8);
		}
		return str;
	}

	public static void _updateScrollList() {
		String emoteName = "";
		if(selected >= 0) emoteName = getSelectedEmoteName(selected);
		ArrayList<String> fileNamesEmotes = cachedEmoteFileNames;

		if(searchString.equals("")) {
			scrollList = fileNamesEmotes;
		} else {
			ArrayList<String> serverList = new ArrayList<String>();
			for(int i = 0; i < fileNamesEmotes.size(); i++) {
				String str = fileNamesEmotes.get(i);
				if(str.contains(searchString.toLowerCase())) {
					serverList.add(str);
				}
			}
			scrollList = serverList;
		}
		//search for the currently selected emote and update it's index
		if(selected >= 0) {
			selected = -1;
			isrequestingremoval = false;
			for(int i = 0; i < scrollList.size(); i++) {
				if(emoteName.equals(getSelectedEmoteName(i))) {
					selected = i;
					break;
				}
			}
		}
	}

	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		if(this.scroll == null) this.scroll = new GuiCustomScroll(this, 0, false);
		if(scrollList == null || hasCachedEmoteFileNamesChanged) {
			_updateScrollList();
		}
		hasCachedEmoteFileNamesChanged = false;

		int x = this.guiLeft;
		int y = this.guiTop + 45;

		this.addTextField(new GuiNpcTextField(652, this, x + 1, y + 1, 98, 16, searchString.equals("") ? "Search" : searchString));
		y += 22;

		this.scroll.selected = selected;
		this.scroll.setUnsortedList(scrollList);
		this.scroll.guiLeft = x;
		this.scroll.guiTop = y;
		this.scroll.setSize(100, this.ySize - 52 - 22);
		this.addScroll(this.scroll);

		x += this.scroll.xSize + 2;

		y = this.guiTop + 44;

		this.addButton(new GuiNpcButton(653, x, y, 50, 20, "gui.refresh"));
		if(selected >= 0) {
			this.addButton(new GuiNpcButton(654, x + 52, y, 50, 20, "gui.load"));
			this.addButton(new GuiNpcButton(655, x + 104, y, 50, 20, "gui.remove"));
			String emoteName = getSelectedEmoteName(selected);
			y += 22;
			if(GuiCreationEmotes.emoteName.equals(emoteName) && !GuiCreationEmotes.emoteIsChangedFromServer) {
				this.getButton(654).enabled = false;
			}
			if(isrequestingremoval) {
				this.addButton(new GuiNpcButton(657, x + 52, y, 102, 20, "gui.confirmremove"));
				this.getButton(655).enabled = false;
			}
		} else {
			y += 22;
		}

		y += 22;
		this.addLabel(new GuiNpcLabel(655, "gui.currentlyediting", x,  y + 5, 16777215));
		y += 22;

		this.addButton(new GuiNpcButton(656, x + 143, y, 50, 20, "gui.save"));
		if(GuiCreationEmotes.emoteName.equals("") || !GuiCreationEmotes.emoteIsChangedFromServer) {
			this.getButton(656).enabled = false;
		}
		this.addTextField(new GuiNpcTextField(657, this, x, y + 1, 140, 18, GuiCreationEmotes.emoteName));
		y += 22;
		this.addButton(new GuiNpcButton(658, x + 143, y, 50, 20, "gui.reset"));
		if (isrequestingreset) {
			this.addButton(new GuiNpcButton(659, x + 143 - 102 - 2, y, 102, 20, "gui.confirmreset"));
			this.getButton(658).enabled = false;
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);

		if(btn.id == 653) {//refresh
			//maybe this needs more code to properly sync with the server
			Client.sendData(EnumPackets.EMOTE_FILENAME_UPDATE);
			this.initGui();
		} else if(btn.id == 656) {//save local emote as server emote
			if(GuiCreationEmotes.emoteData != null) {
				ByteBuf buffer = Unpooled.buffer();
				buffer.writeInt(EnumPackets.EMOTE_SAVE.ordinal());
				Server.writeString(buffer, GuiCreationEmotes.emoteName);
				Emote.writeEmote(buffer, GuiCreationEmotes.emoteData);
				Client.sendData(buffer);
				GuiCreationEmotes.emoteIsChangedFromServer = false;//if this packet is dropped it could cause problems since the emote wasn't actually saved
				this.initGui();
			}
		} else if(btn.id == 658) {//request reset local emote
			isrequestingreset = true;
			this.initGui();
		} else if(btn.id == 659) {//reset local emote
			isrequestingreset = false;
			GuiCreationEmotes.loadNewEmote(new Emote());
			GuiCreationEmotes.emoteName = "";
			this.initGui();
			this.playerdata.endPreview();
		} else if(selected >= 0) {//refresh
			//NOTE: the emoteName may have been desynced with the server, minor inconvenience
			String emoteName = getSelectedEmoteName(selected);
			if(btn.id == 654) {//load
				Client.sendData(EnumPackets.EMOTE_LOAD, emoteName);
				GuiCreationEmotes.emoteName = emoteName;
				this.initGui();
			} else if(btn.id == 655) {//request remove
				isrequestingremoval = true;
				Client.sendData(EnumPackets.EMOTE_FILENAME_UPDATE);
				this.initGui();
			} else if(btn.id == 657) {//remove
				isrequestingremoval = false;
				selected = -1;
				Client.sendData(EnumPackets.EMOTE_REMOVE, emoteName);
				this.initGui();
			}
		}
	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		if (this.initiating) return;
		if(0 <= this.scroll.selected && this.scroll.selected < scrollList.size()) {
			if(selected != this.scroll.selected) {
				selected = this.scroll.selected;
				isrequestingremoval = false;
			}
		} else {
			selected = -1;
			isrequestingremoval = false;
		}
		this.initGui();
	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
	}


	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if(textField.id == 652) {
			if(!textField.getText().equals("Search")) {
				searchString = MorePlayerModels.validateFileName(textField.getText());
				if(searchString == null) searchString = "";
				_updateScrollList();
			}
			this.initGui();
		} else if(textField.id == 657) {
			String str = MorePlayerModels.validateFileName(textField.getText());
			if(str != null && !str.equals(GuiCreationEmotes.emoteName)) {
				GuiCreationEmotes.emoteName = str;
				GuiCreationEmotes.emoteIsChangedFromServer = true;
			}
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id == 652 || textField.id == 657) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField var1) {

	}
}
