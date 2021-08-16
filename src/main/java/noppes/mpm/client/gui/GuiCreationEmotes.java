package noppes.mpm.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.client.gui.GuiButton;

import noppes.mpm.ModelData;
import noppes.mpm.Emote;
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

import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenUtils;

public class GuiCreationEmotes extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener, ITextfieldListener {
	public static final Float maxOffset = 2.0F;
	public static final Float maxRotation = (float)Math.toRadians(360.0F);
	public static final Float maxDuration = 2.0F;


	public GuiCustomScroll scroll;
	public boolean initiating = false;

	public static Emote curEmote = null;
	public static String curEmoteName = "";
	public static int curPart = Emote.HEAD;
	public static boolean iseditingintro = true;
	public static boolean iseditingoffset = false;
	public static boolean ischangedfromserver = true;
	// public static boolean isloadingnewemote = false;
	public static int selected = -1;
	public static Emote.PartCommand clipboardCommand = null;
	public static File autosaveFile;

	public static void load() {
		File dir = null;
		autosaveFile = new File(dir, "moreplayermodels" + File.separator + "emote-autosave.dat");

		ByteBuf buffer = Unpooled.buffer();
		try {
			if(autosaveFile.exists()) {
				buffer.writeBytes(new FileInputStream(autosaveFile), (int)autosaveFile.length());

				curEmote = Emote.readEmote(buffer);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			buffer.release();
			if(curEmote == null) {
				curEmote = new Emote();
			}
		}
	}
	public static void writeAutosave() {

		ByteBuf filedata = Unpooled.buffer();
		try {
			FileOutputStream out = new FileOutputStream(autosaveFile);
			Emote.writeEmote(filedata, curEmote);
			byte[] rawdata = filedata.array();
			out.write(rawdata);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			filedata.release();
		}
	}

	public GuiCreationEmotes() {
		this.playerdata = ModelData.get(this.getPlayer());
		this.active = 601;
		this.xOffset = 140;

		load();//should this be here? maybe loading once on init would be better
		this.playerdata.startPreviewEmote(curEmote, this.getPlayer(), iseditingintro);
	}

	public static void resetAndReplaceEmote(Emote emote) {
		curEmote = emote;
		curPart = Emote.HEAD;
		iseditingintro = true;
		iseditingoffset = false;
		selected = -1;
		//NOTE: does not reset the clipboardCommand
		// clipboardCommand = null;
		writeAutosave();
	}


	public static ArrayList<Emote.PartCommand> getCommandList(Emote emote, int partId, boolean intro, boolean offset) {
		return emote.commands.get(4*curPart + (intro?0:2) + (offset?0:1));
	}
	public static void setCommandList(Emote emote, int partId, boolean intro, boolean offset, ArrayList<Emote.PartCommand> s) {
		emote.commands.set(4*curPart + (intro?0:2) + (offset?0:1), s);
	}

	public void onEmoteChange() {
		ischangedfromserver = true;
		this.playerdata.startPreviewEmote(curEmote, this.getPlayer(), iseditingintro);
		writeAutosave();
	}


	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		ArrayList<Emote.PartCommand> cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);

		//start at top left of menu
		int x = this.guiLeft;
		int y = this.guiTop + 45;
		this.addButton(new GuiNpcButton(602, x,      y, 50, 20, "gui.intro"));
		this.addButton(new GuiNpcButton(603, x + 50, y, 50, 20, "gui.loop"));
		if(iseditingintro) {
			this.getButton(602).enabled = false;
		} else {
			this.getButton(603).enabled = false;
		}
		y += 22;
		this.addButton(new GuiNpcButton(605, x,      y, 50, 20, "gui.rotate"));
		this.addButton(new GuiNpcButton(604, x + 50, y, 50, 20, "gui.offset"));
		if(iseditingoffset) {
			this.getButton(604).enabled = false;
		} else {
			this.getButton(605).enabled = false;
		}
		y += 22;
		this.addButton(new GuiNpcButton(606, x, y, 100, 20, "gui." + Emote.BODY_PARTS[curPart]));
		y += 22;


		if(this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0, false);
		}

		ArrayList<String> command_display_names = new ArrayList<String>();
		this.scroll.colorlist = new ArrayList<Integer>();
		if(curEmote.partIsUsed(curPart)) {
			if(cur_command_list != null) {
				for(int i = 0; i < cur_command_list.size(); i++) {
					Emote.PartCommand command = cur_command_list.get(i);

					if(command.disabled) {
						this.scroll.colorlist.add(8421504);
					} else {
						this.scroll.colorlist.add(16777215);
					}
					String str;
					if(iseditingoffset) {
						str = String.format(java.util.Locale.US, "%.2f, %.2f, %.2f", command.x, command.y, command.z);
					} else {
						str = String.format(java.util.Locale.US, "%.0f, %.0f, %.0f", Math.toDegrees(command.x), Math.toDegrees(command.y), Math.toDegrees(command.z));
					}
					command_display_names.add(str);
				}
			} else {
				this.scroll.colorlist.add(8421504);
				command_display_names.add("None");
			}
		} else {
			this.scroll.colorlist.add(8421504);
			command_display_names.add("Not Added");
		}


		this.scroll.selected = selected;
		this.scroll.setUnsortedList(command_display_names);
		this.scroll.guiLeft = x;
		this.scroll.guiTop = y;
		this.scroll.setSize(100, this.ySize - 52 - 3*22);
		this.addScroll(this.scroll);

		//////////////////
		// - + copy paste duplicate
		// /\ \/ easing (disabled?)
		// duration
		// x
		// y
		// z
		//////////////////
		y = this.guiTop + 45;
		x += this.scroll.xSize + 2;
		this.addButton(new GuiNpcButton(607, x, y, 20, 20, "+"));

		if(cur_command_list != null && selected >= 0) {
			Emote.PartCommand cur_command = cur_command_list.get(selected);

			this.addButton(new GuiNpcButton(608, x + 22,  y, 20, 20, "-"));
			this.addButton(new GuiNpcButton(609, x + 44,  y, 40, 20, "gui.copy"));
			this.addButton(new GuiNpcButton(610, x + 86,  y, 40, 20, "gui.paste"));
			this.addButton(new GuiNpcButton(611, x + 128, y, 65, 20, "gui.duplicate"));
			y += 22;

			this.addButton(new GuiNpcButton(612, x,      y, 20, 20, "gui.moveup"));
			this.addButton(new GuiNpcButton(613, x + 22, y, 20, 20, "gui.movedown"));
			if(selected == 0) {
				this.getButton(612).enabled = false;
			}
			if(selected == cur_command_list.size() - 1) {
				this.getButton(613).enabled = false;
			}
			this.addButton(new GuiNpcButton(614, x + 44, y, 82, 20, cur_command.disabled ? "gui.enable" : "gui.disable"));

			y += 22;

			if(iseditingoffset) {
				this.addTextField(new GuiNpcTextField(615, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.x)));
				this.addSlider(new GuiNpcSlider(this, 615, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, ((cur_command.x) + maxOffset) / (maxOffset * 2.0F)))));
				this.getSlider(615).displayString = "X";

				y += 22;
				this.addTextField(new GuiNpcTextField(616, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.y)));
				this.addSlider(new GuiNpcSlider(this, 616, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, (cur_command.y + maxOffset) / (maxOffset * 2.0F)))));
				this.getSlider(616).displayString = "Y";

				y += 22;
				this.addTextField(new GuiNpcTextField(617, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.z)));
				this.addSlider(new GuiNpcSlider(this, 617, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, (cur_command.z + maxOffset) / (maxOffset * 2.0F)))));
				this.getSlider(617).displayString = "Z";

			} else {
				this.addTextField(new GuiNpcTextField(618, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.0f", Math.toDegrees(cur_command.x))));
				this.addSlider(new GuiNpcSlider(this, 618, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, (cur_command.x + maxRotation) / (maxRotation * 2.0F)))));
				this.getSlider(618).displayString = "X";

				y += 22;
				this.addTextField(new GuiNpcTextField(619, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.0f", Math.toDegrees(cur_command.y))));
				this.addSlider(new GuiNpcSlider(this, 619, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, (cur_command.y + maxRotation) / (maxRotation * 2.0F)))));
				this.getSlider(619).displayString = "Y";

				y += 22;
				this.addTextField(new GuiNpcTextField(620, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.0f", Math.toDegrees(cur_command.z))));
				this.addSlider(new GuiNpcSlider(this, 620, x, y, 152, 20, Math.max(0.0F, Math.min(1.0F, (cur_command.z + maxRotation) / (maxRotation * 2.0F)))));
				this.getSlider(620).displayString = "Z";
			}
			y += 22;

			this.addTextField(new GuiNpcTextField(621, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.duration)));
			this.addSlider(new GuiNpcSlider(this, 621, x, y, 152, 20, ((cur_command.duration) / (maxDuration))));
			this.getSlider(621).displayString = "Duration";
			y += 22;

			this.addLabel(new GuiNpcLabel(622, "gui.easing", x + 2,  y + 5, 16777215));
			this.addTextField(new GuiNpcTextField(623, this, x + 40, y + 1, 151, 18, TweenUtils.easings[cur_command.easing].toString()));
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if(this.initiating) return;

		ArrayList<Emote.PartCommand> cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);

		if(btn.id == 607) {//add anim command
			Emote.PartCommand command = new Emote.PartCommand();
			if(cur_command_list == null) {
				setCommandList(curEmote, curPart, iseditingintro, iseditingoffset, new ArrayList<Emote.PartCommand>());
				cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);
			}
			selected = cur_command_list.size();
			cur_command_list.add(command);
			onEmoteChange();
			this.initGui();
		} else if(btn.id == 602) {//goto intro commands
			iseditingintro = true;
			this.playerdata.startPreviewEmote(curEmote, this.getPlayer(), iseditingintro);
			selected = -1;
			this.initGui();
		} else if(btn.id == 603) {//goto loop commands
			iseditingintro = false;
			this.playerdata.startPreviewEmote(curEmote, this.getPlayer(), iseditingintro);
			selected = -1;
			this.initGui();
		} else if(btn.id == 604) {//goto offset commands
			iseditingoffset = true;
			selected = -1;
			this.initGui();
		} else if(btn.id == 605) {//goto rotate commands
			iseditingoffset = false;
			selected = -1;
			this.initGui();
		} else if(btn.id == 606) {//change and goto part commands
			curPart = (curPart + 1)%Emote.BODY_PARTS.length;
			selected = -1;
			this.initGui();
		} else if(cur_command_list != null && selected >= 0) {
			Emote.PartCommand cur_command = cur_command_list.get(selected);

			if(btn.id == 608) {//remove anim command
				cur_command_list.remove(selected);
				if(selected == cur_command_list.size()) selected--;
				if(cur_command_list.size() == 0) {
					setCommandList(curEmote, curPart, iseditingintro, iseditingoffset, null);
				}
				onEmoteChange();
				this.initGui();
			} else if(btn.id == 609) {//copy anim command
				clipboardCommand = cur_command.clone();
			} else if(btn.id == 610) {//paste anim command
				if(clipboardCommand != null) {
					cur_command_list.set(selected, clipboardCommand.clone());
					onEmoteChange();
					this.initGui();
				}
			} else if(btn.id == 611) {//duplicate anim command
				cur_command_list.add(selected++, cur_command.clone());
				onEmoteChange();
				this.initGui();
			} else if(btn.id == 612) {//move anim command up
				if(selected > 0) {
					cur_command_list.set(selected, cur_command_list.get(selected - 1));
					cur_command_list.set(selected - 1, cur_command);
					selected -= 1;
					onEmoteChange();
					this.initGui();
				}
			} else if(btn.id == 613) {//move anim command down
				if(selected < cur_command_list.size() - 1) {
					cur_command_list.set(selected, cur_command_list.get(selected + 1));
					cur_command_list.set(selected + 1, cur_command);
					selected += 1;
					onEmoteChange();
					this.initGui();
				}
			} else if(btn.id == 614) {//toggle disable anim command
				cur_command.disabled = !cur_command.disabled;
				onEmoteChange();
				this.initGui();
			}
		}
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);
		if(this.initiating) return;

		ArrayList<Emote.PartCommand> cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);

		if(cur_command_list != null && selected >= 0) {
			Emote.PartCommand cur_command = cur_command_list.get(selected);

			if(615 <= slider.id && slider.id <= 620 || slider.id == 621) {
				Float value = 0.0F;
				String text = "";

				boolean hasChanged = false;
				if(615 <= slider.id && slider.id <= 617) {//set offset
					value = ((slider.sliderValue - 0.5F) * (maxOffset * 2.0F));

					if(slider.id == 615) {
						hasChanged = Math.abs(cur_command.x - value) > .0001;
						cur_command.x = value;
					} else if(slider.id == 616) {
						hasChanged = Math.abs(cur_command.y - value) > .0001;
						cur_command.y = value;
					} else if(slider.id == 617) {
						hasChanged = Math.abs(cur_command.z - value) > .0001;
						cur_command.z = value;
					}

					text = String.format(java.util.Locale.US, "%.2f", value);
				} else if(618 <= slider.id && slider.id <= 620) {//set rotate
					value = ((slider.sliderValue - 0.5F) * (maxRotation * 2.0F));

					if(slider.id == 618) {
						hasChanged = Math.abs(cur_command.x - value) > .0001;
						cur_command.x = value;
					} else if(slider.id == 619) {
						hasChanged = Math.abs(cur_command.y - value) > .0001;
						cur_command.y = value;
					} else if(slider.id == 620) {
						hasChanged = Math.abs(cur_command.z - value) > .0001;
						cur_command.z = value;
					}

					text = String.format(java.util.Locale.US, "%.0f", Math.toDegrees(value));
				} else if(slider.id == 621) {//set duration
					value = Math.max(Emote.minDuration, (slider.sliderValue) * (maxDuration));

					hasChanged = Math.abs(cur_command.duration - value) > .0001;
					cur_command.duration = value;

					text = String.format(java.util.Locale.US, "%.2f", value);
				}

				if(hasChanged) onEmoteChange();
				GuiNpcTextField textField = this.getTextField(slider.id);
				textField.setSelectionPos(0);
				textField.setCursorPositionZero();
				textField.setText(text);

				if(slider.id != 621) {//update command name in scroll
					ArrayList<String> command_display_names = new ArrayList<String>();
					for(int i = 0; i < cur_command_list.size(); i++) {
						Emote.PartCommand command = cur_command_list.get(i);

						String str;
						if(iseditingoffset) {
							str = String.format(java.util.Locale.US, "%.2f, %.2f, %.2f", command.x, command.y, command.z);
						} else {
							str = String.format(java.util.Locale.US, "%.0f, %.0f, %.0f", Math.toDegrees(command.x), Math.toDegrees(command.y), Math.toDegrees(command.z));
						}
						command_display_names.add(str);
					}
					this.scroll.setUnsortedList(command_display_names);
				}
			}
		}
	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		int pre_selected = selected;

		ArrayList<Emote.PartCommand> cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);
		if(cur_command_list != null) {

			if(0 <= this.scroll.selected && this.scroll.selected < cur_command_list.size()) {
				selected = this.scroll.selected;
			}
		}
		if(pre_selected != this.scroll.selected) this.initGui();
	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {

	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if(this.initiating) return;

		ArrayList<Emote.PartCommand> cur_command_list = getCommandList(curEmote, curPart, iseditingintro, iseditingoffset);

		if(cur_command_list != null && selected >= 0) {
			Emote.PartCommand cur_command = cur_command_list.get(selected);

			if(615 <= textField.id && textField.id <= 620 || textField.id == 621) {
				Float value = null;
				try {
					value = Float.parseFloat(textField.getText().replace(',', '.'));
				} catch (NumberFormatException e) {
					return;
				}

				Float sliderValue = 0.0F;

				if(615 <= textField.id && textField.id <= 617) {//set offset
					value = Math.min(Emote.maxOffset, Math.max(-Emote.maxOffset, value));
					sliderValue = (value + maxOffset) / (maxOffset * 2.0F);

					if(textField.id == 615) {
						cur_command.x = value;
					} else if(textField.id == 616) {
						cur_command.y = value;
					} else if(textField.id == 617) {
						cur_command.z = value;
					}
				} else if(618 <= textField.id && textField.id <= 620) {//set rotate
					value = (float)Math.toRadians(value);
					value = Math.min(Emote.maxRotate, Math.max(-Emote.maxRotate, value));
					sliderValue = (value + maxRotation) / (maxRotation * 2.0F);

					if(textField.id == 618) {
						cur_command.x = value;
					} else if(textField.id == 619) {
						cur_command.y = value;
					} else if(textField.id == 620) {
						cur_command.z = value;
					}
				} else if(textField.id == 621) {//set duration
					value = Math.min(Emote.maxDuration, Math.max(Emote.minDuration, value));
					sliderValue = (value) / (maxDuration);

					cur_command.duration = value;
				}

				// textField.setCursorPositionZero();
				// textField.setSelectionPos(0);
				// this.getSlider(textField.id).sliderValue = sliderValue;
				onEmoteChange();
				this.initGui();
			} else if(textField.id == 623) {//change and set easing
				int easing = TweenUtils.parseEasingToEnum(textField.getText());
				if(easing >= 0) {
					cur_command.easing = easing;
					onEmoteChange();
				}

				this.initGui();
			}
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if(this.initiating) return;

		if(615 <= textField.id && textField.id <= 620 || textField.id == 621 || textField.id == 623) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}


	@Override
	public void textboxKeyTyped(GuiNpcTextField var1) {

	}
}
