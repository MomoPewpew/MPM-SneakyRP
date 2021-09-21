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
	public static final Float maxOffset = 1.0F;
	public static final Float maxRotation = (float)Math.toRadians(360.0F);
	public static final Float maxDuration = 1.0F;


	public GuiCustomScroll scroll;
	public boolean initiating = false;

	public static Emote emoteData = null;
	public static String emoteName = "";
	public static int emotePart = Emote.HEAD;
	public static int emoteIsRotate = 1;
	public static int emoteSection = 0;
	public static int emoteSelected = -1;

	public static boolean emoteIsChangedFromServer = true;

	public static Emote.PartCommand clipboardCommand = null;
	public static int clipboardUsage = 0;
	public static File autosaveFile = null;

	public static void loadAutosave() {
		ByteBuf buffer = Unpooled.buffer();
		try {
			if(autosaveFile.exists()) {
				buffer.writeBytes(new FileInputStream(autosaveFile), (int)autosaveFile.length());

				emoteData = Emote.readEmote(buffer);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			buffer.release();
			if(emoteData == null) {
				emoteData = new Emote();
			}
		}
	}
	public static void writeAutosave() {

		ByteBuf filedata = Unpooled.buffer();
		try {
			FileOutputStream out = new FileOutputStream(autosaveFile);
			Emote.writeEmote(filedata, emoteData);
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


		if(autosaveFile == null) {
			File dir = null;
			autosaveFile = new File(dir, "moreplayermodels" + File.separator + "emoteData-autosave.dat");
			loadAutosave();
		}
		this.playerdata.startPreview(emoteData.clone());
		this.playerdata.previewRepetitions = emoteSection == 1 ? 10 : 3;
	}

	public static void loadNewEmote(Emote newEmote) {
		emoteData = newEmote;
		emotePart = Emote.HEAD;
		emoteSection = 0;
		emoteIsRotate = 1;
		emoteSelected = -1;
		emoteIsChangedFromServer = false;

		if(autosaveFile == null) {
			File dir = null;
			autosaveFile = new File(dir, "moreplayermodels" + File.separator + "emoteData-autosave.dat");
		}
		//NOTE: does not reset the clipboardCommand
		// clipboardCommand = null;
		// writeAutosave();
	}



	public void onEmoteChange(boolean doUpdatePreview) {
		emoteIsChangedFromServer = true;

		if(doUpdatePreview) this.playerdata.startPreview(emoteData.clone());
		this.playerdata.previewRepetitions = emoteSection == 1 ? 10 : 3;
		writeAutosave();
	}

	public static final String getCommandName(Emote.PartCommand command, int isRotate) {
		if(command.consoleCommand == null) {
			if(isRotate == 1) {
				return String.format(java.util.Locale.US, "%.0f, %.0f, %.0f", Math.toDegrees(command.x), Math.toDegrees(command.y), Math.toDegrees(command.z));
			} else {
				return String.format(java.util.Locale.US, "%.2f, %.2f, %.2f", command.x, command.y, command.z);
			}
		} else if(command.consoleCommand.length() == 0) {
			return "empty";
		} else {
			return command.consoleCommand;
		}
	}
	public static final String getValueAsString(float value, int isRotate) {
		if(isRotate == 1) {
			return String.format(java.util.Locale.US, "%.0f", Math.toDegrees(value));
		} else {
			return String.format(java.util.Locale.US, "%.2f", value);
		}
	}

	public static final float sliderClamp(float value, float max) {
		return Math.max(0.0F, Math.min(1.0F, (value + max)/(max*2.0F)));
	}


	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();
		int meta_i = 2*emotePart + emoteIsRotate;
		int section_i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate + emoteSection;

		ArrayList<Emote.PartCommand> sectionList = emoteData.commands.get(section_i);

		//start at top left of menu
		int x = this.guiLeft;
		int y = this.guiTop + 45;
		this.addButton(new GuiNpcButton(606, x, y, 100, 20, "gui." + Emote.BODY_PARTS[emotePart]));
		y += 22;
		this.addButton(new GuiNpcButton(605, x,      y, 50, 20, "gui.rotate"));
		this.addButton(new GuiNpcButton(604, x + 50, y, 50, 20, "gui.offset"));
		if(emoteIsRotate == 1) {
			this.getButton(605).enabled = false;
		} else {
			this.getButton(604).enabled = false;
		}
		y += 22;
		if(emoteSection == 0) {
			this.addButton(new GuiNpcButton(602, x, y, 100, 20, "gui.intro"));
		} else if(emoteSection == 1) {
			this.addButton(new GuiNpcButton(602, x, y, 100, 20, "gui.loop"));
		} else if(emoteSection == 2) {
			this.addButton(new GuiNpcButton(602, x, y, 100, 20, "gui.outro"));
		}
		y += 22;

		if(this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0, false);
		}

		ArrayList<String> commandDisplayNames = new ArrayList<String>();
		this.scroll.colorlist = new ArrayList<Integer>();
		if(emoteData.partUsages[meta_i] > 0) {
			this.scroll.colorlist.add(16777215);
			commandDisplayNames.add("Part Usage");
			if(sectionList != null) {
				for(int i = 0; i < sectionList.size(); i++) {
					Emote.PartCommand command = sectionList.get(i);

					if(command.disabled) {
						this.scroll.colorlist.add(8421504);
					} else {
						this.scroll.colorlist.add(16777215);
					}
					commandDisplayNames.add(getCommandName(command, emoteIsRotate));
				}
			}
		} else {
			this.scroll.colorlist.add(8421504);
			commandDisplayNames.add("Not Used");
		}


		this.scroll.selected = emoteSelected;
		this.scroll.setUnsortedList(commandDisplayNames);
		this.scroll.guiLeft = x;
		this.scroll.guiTop = y;
		this.scroll.setSize(100, this.ySize - 52 - 3*22);
		this.addScroll(this.scroll);

		//////////////////
		// - + copy paste duplicate
		// < > disabled
		// x
		// y
		// z
		// duration
		// easing
		//////////////////
		y = this.guiTop + 45;
		x += this.scroll.xSize + 2;
		this.addButton(new GuiNpcButton(607, x, y, 20, 20, "+"));

		if(emoteSelected >= 1) {
			Emote.PartCommand command = sectionList.get(emoteSelected - 1);

			this.addButton(new GuiNpcButton(608, x + 22,  y, 20, 20, "-"));
			this.addButton(new GuiNpcButton(609, x + 44,  y, 40, 20, "gui.copy"));
			this.addButton(new GuiNpcButton(610, x + 86,  y, 40, 20, "gui.paste"));
			this.addButton(new GuiNpcButton(611, x + 128, y, 65, 20, "gui.duplicate"));
			y += 22;

			this.addButton(new GuiNpcButton(612, x,      y, 20, 20, "gui.moveup"));
			this.addButton(new GuiNpcButton(613, x + 22, y, 20, 20, "gui.movedown"));
			if(emoteSelected - 1 == 0) {
				this.getButton(612).enabled = false;
			}
			if(emoteSelected - 1 == sectionList.size() - 1) {
				this.getButton(613).enabled = false;
			}
			this.addButton(new GuiNpcButton(614, x + 128, y, 65, 20, command.disabled ? "gui.disabled" : "gui.enabled"));
			if(command.consoleCommand == null) {
				this.addButton(new GuiNpcButton(625, x + 44, y, 82, 20, "gui.type.keyframe"));
				y += 22;

				if(emoteIsRotate == 0) {
					this.addTextField(new GuiNpcTextField(615, this, x + 155, y + 1, 36, 18, getValueAsString(command.x, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 615, x, y, 152, 20, sliderClamp(command.x, maxOffset)));
					this.getSlider(615).displayString = "X";
					y += 22;

					this.addTextField(new GuiNpcTextField(616, this, x + 155, y + 1, 36, 18, getValueAsString(command.y, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 616, x, y, 152, 20, sliderClamp(command.y, maxOffset)));
					this.getSlider(616).displayString = "Y";
					y += 22;

					this.addTextField(new GuiNpcTextField(617, this, x + 155, y + 1, 36, 18, getValueAsString(command.z, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 617, x, y, 152, 20, sliderClamp(command.z, maxOffset)));
					this.getSlider(617).displayString = "Z";
					y += 22;
				} else {
					this.addTextField(new GuiNpcTextField(618, this, x + 155, y + 1, 36, 18, getValueAsString(command.x, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 618, x, y, 152, 20, sliderClamp(command.x, maxRotation)));
					this.getSlider(618).displayString = "X";

					y += 22;
					this.addTextField(new GuiNpcTextField(619, this, x + 155, y + 1, 36, 18, getValueAsString(command.y, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 619, x, y, 152, 20, sliderClamp(command.y, maxRotation)));
					this.getSlider(619).displayString = "Y";

					y += 22;
					this.addTextField(new GuiNpcTextField(620, this, x + 155, y + 1, 36, 18, getValueAsString(command.z, emoteIsRotate)));
					this.addSlider(new GuiNpcSlider(this, 620, x, y, 152, 20, sliderClamp(command.z, maxRotation)));
					this.getSlider(620).displayString = "Z";
					y += 22;
				}

				this.addTextField(new GuiNpcTextField(621, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", command.duration)));
				this.addSlider(new GuiNpcSlider(this, 621, x, y, 152, 20, Math.min(1.0f, Math.max(0.0f, command.duration/maxDuration))));
				this.getSlider(621).displayString = "Duration";
				y += 22;

				this.addLabel(new GuiNpcLabel(622, "gui.easing", x + 2,  y + 6, 16777215));
				this.addTextField(new GuiNpcTextField(623, this, x + 40, y + 1, 151, 18, TweenUtils.easings[command.easing].toString()));
				y += 22;
			} else {
				this.addButton(new GuiNpcButton(625, x + 44, y, 82, 20, "gui.type.command"));
				y += 22;

				this.addLabel(new GuiNpcLabel(626, "gui.command", x + 2,  y + 6, 16777215));
				this.addTextField(new GuiNpcTextField(626, this, x + 48, y + 1, 143, 18, command.consoleCommand));
				y += 22;
			}
			this.addLabel(new GuiNpcLabel(627, "gui.timestamp", x + 2,  y + 6, 16777215));
			float timestamp = 0.0f;
			for(int i = 0; i <= emoteSelected - 1; i += 1) {
				Emote.PartCommand c = sectionList.get(i);
				if(c.consoleCommand == null) {
					timestamp += c.duration;
				}
			}
			this.addLabel(new GuiNpcLabel(628, String.format(java.util.Locale.US, "%.2f", timestamp), x + 53,  y + 6, 16777215));

		} else if(emoteSelected == 0 && emoteData.partUsages[meta_i] > 0) {//editing part usage
			this.addButton(new GuiNpcButton(609, x + 44,  y, 40, 20, "gui.copy"));
			this.addButton(new GuiNpcButton(610, x + 86,  y, 40, 20, "gui.paste"));
			y += 22;
			int usage = emoteData.partUsages[meta_i];

			this.addLabel(new GuiNpcLabel(630, "gui.usageflag.loop_only_stops_at_boundary", x + 2, y + 6, 16777215));
			this.addButton(new GuiNpcButton(630, x + 160, y, 25, 20, (usage&Emote.FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY) > 0 ? "gui.yes" : "gui.no"));
			y += 22;

			this.addLabel(new GuiNpcLabel(631, "gui.usageflag.loop_pauses_when_still", x + 2, y + 6, 16777215));
			this.addButton(new GuiNpcButton(631, x + 160, y, 25, 20, (usage&Emote.FLAG_LOOP_PAUSES_WHEN_STILL) > 0 ? "gui.yes" : "gui.no"));
			y += 22;

			this.addLabel(new GuiNpcLabel(632, "gui.usageflag.outro_plays_when_still", x + 2, y + 6, 16777215));
			this.addButton(new GuiNpcButton(632, x + 160, y, 25, 20, (usage&Emote.FLAG_OUTRO_PLAYS_WHEN_STILL) > 0 ? "gui.yes" : "gui.no"));
			y += 22;

			boolean draw_walkstyles;
			if((usage&Emote.FLAG_LOOP_PAUSES_WHEN_STILL) > 0) {
				this.getButton(632).enabled = false;
				draw_walkstyles = true;
			} else if((usage&Emote.FLAG_OUTRO_PLAYS_WHEN_STILL) > 0) {
				this.getButton(631).enabled = false;
				draw_walkstyles = true;
			} else {
				draw_walkstyles = false;
			}
			if(draw_walkstyles) {
				this.addLabel(new GuiNpcLabel(633, "gui.usageflag.loop_only_pauses_at_boundary", x + 2, y + 6, 16777215));
				this.addButton(new GuiNpcButton(633, x + 160, y, 25, 20, (usage&Emote.FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY) > 0 ? "gui.yes" : "gui.no"));
				y += 22;

				this.addLabel(new GuiNpcLabel(634, "gui.usageflag.invert_movement", x + 2, y + 6, 16777215));
				this.addButton(new GuiNpcButton(634, x + 160, y, 25, 20, (usage&Emote.FLAG_INVERT_MOVEMENT) > 0 ? "gui.yes" : "gui.no"));
				y += 22;
			}

			if(emoteIsRotate == 1) {
				this.addLabel(new GuiNpcLabel(635, "gui.usageflag.follows_head_rotation", x + 2, y + 6, 16777215));
				this.addButton(new GuiNpcButton(635, x + 160, y, 25, 20, (usage&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0 ? "gui.yes" : "gui.no"));
				y += 22;
			} else if(emotePart == Emote.MODEL) {
				this.addLabel(new GuiNpcLabel(635, "gui.usageflag.camera_follows_model_offset", x + 2, y + 6, 16777215));
				this.addButton(new GuiNpcButton(635, x + 160, y, 25, 20, (usage&Emote.FLAG_CAMERA_FOLLOWS_MODEL_OFFSET) > 0 ? "gui.yes" : "gui.no"));
				y += 22;
			}
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if(this.initiating) return;
		int meta_i = 2*emotePart + emoteIsRotate;
		int section_i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate + emoteSection;

		ArrayList<Emote.PartCommand> sectionList = emoteData.commands.get(section_i);

		if(btn.id == 607) {//add anim command
			Emote.PartCommand command = new Emote.PartCommand();
			if(sectionList == null) {
				sectionList = new ArrayList<Emote.PartCommand>();
				emoteData.commands.set(section_i, sectionList);
				emoteData.partUsages[meta_i] |= Emote.FLAG_USED;
			}
			emoteSelected = sectionList.size() + 1;
			sectionList.add(command);
			onEmoteChange(true);
			this.initGui();
		} else if(btn.id == 602) {//change section commands
			emoteSection = (emoteSection + 1)%3;
			emoteSelected = -1;
			this.playerdata.previewRepetitions = emoteSection == 1 ? 10 : 3;
			this.initGui();
		} else if(btn.id == 604) {//goto offset commands
			emoteIsRotate = 0;
			emoteSelected = -1;
			this.initGui();
		} else if(btn.id == 605) {//goto rotate commands
			emoteIsRotate = 1;
			emoteSelected = -1;
			this.initGui();
		} else if(btn.id == 606) {//change and goto part commands
			emotePart = (emotePart + 1)%Emote.BODY_PARTS.length;
			emoteSelected = -1;
			this.initGui();
		} else if(emoteSelected == 0 && emoteData.partUsages[meta_i] > 0) {
			if(btn.id == 609) {//copy anim command
				clipboardUsage = emoteData.partUsages[meta_i];
			} else if(btn.id == 610) {//paste anim command
				if(clipboardUsage > 0) {
					emoteData.partUsages[meta_i] = clipboardUsage;
					onEmoteChange(true);
					this.initGui();
				}
			} else if(btn.id == 630) {//FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY
				emoteData.partUsages[meta_i] ^= Emote.FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY;
				onEmoteChange(false);
				this.initGui();
			} else if(btn.id == 631) {//FLAG_LOOP_PAUSES_WHEN_STILL
				emoteData.partUsages[meta_i] ^= Emote.FLAG_LOOP_PAUSES_WHEN_STILL;
				onEmoteChange(false);
				this.initGui();
			} else if(btn.id == 632) {//FLAG_OUTRO_PLAYS_WHEN_STILL
				emoteData.partUsages[meta_i] ^= Emote.FLAG_OUTRO_PLAYS_WHEN_STILL;
				onEmoteChange(false);
				this.initGui();
			} else if(btn.id == 633) {//FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY
				emoteData.partUsages[meta_i] ^= Emote.FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY;
				onEmoteChange(false);
				this.initGui();
			} else if(btn.id == 634) {//FLAG_INVERT_MOVEMENT
				emoteData.partUsages[meta_i] ^= Emote.FLAG_INVERT_MOVEMENT;
				onEmoteChange(false);
				this.initGui();
			} else if(btn.id == 635) {//FLAG_FOLLOWS_HEAD_ROTATION/FLAG_CAMERA_FOLLOWS_MODEL_OFFSET
				emoteData.partUsages[meta_i] ^= Emote.FLAG_FOLLOWS_HEAD_ROTATION;
				onEmoteChange(true);
				this.initGui();
			}
		} else if(sectionList != null && emoteSelected >= 1) {
			int selectedCommand = emoteSelected - 1;
			Emote.PartCommand command = sectionList.get(selectedCommand);

			if(btn.id == 608) {//remove anim command
				sectionList.remove(selectedCommand);
				if(selectedCommand == sectionList.size()) emoteSelected -= 1;
				if(sectionList.size() == 0) {
					emoteData.commands.set(section_i, null);
					emoteSelected = -1;
					int i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate;
					if(emoteData.commands.get(i) == null && emoteData.commands.get(i + 1) == null && emoteData.commands.get(i + 2) == null) {
						emoteData.partUsages[meta_i] = 0;
					}
				}
				onEmoteChange(true);
				this.initGui();
			} else if(btn.id == 609) {//copy anim command
				clipboardCommand = command.clone();
			} else if(btn.id == 610) {//paste anim command
				if(clipboardCommand != null) {
					sectionList.set(selectedCommand, clipboardCommand.clone());
					onEmoteChange(true);
					this.initGui();
				}
			} else if(btn.id == 611) {//duplicate anim command
				sectionList.add(selectedCommand, command.clone());
				emoteSelected += 1;
				onEmoteChange(true);
				this.initGui();
			} else if(btn.id == 612) {//move anim command up
				if(selectedCommand > 0) {
					sectionList.set(selectedCommand, sectionList.get(selectedCommand - 1));
					sectionList.set(selectedCommand - 1, command);
					emoteSelected -= 1;
					onEmoteChange(true);
					this.initGui();
				}
			} else if(btn.id == 613) {//move anim command down
				if(selectedCommand < sectionList.size() - 1) {
					sectionList.set(selectedCommand, sectionList.get(selectedCommand + 1));
					sectionList.set(selectedCommand + 1, command);
					emoteSelected += 1;
					onEmoteChange(true);
					this.initGui();
				}
			} else if(btn.id == 614) {//toggle disable anim command
				command.disabled = !command.disabled;
				onEmoteChange(true);
				this.initGui();
			} else if(btn.id == 625) {//switch to console command editor
				if(command.consoleCommand == null) {
					command.consoleCommand = "";
				} else {
					command.consoleCommand = null;
				}
				onEmoteChange(true);

				this.initGui();
			}
		}
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);
		if(this.initiating) return;
		int meta_i = 2*emotePart + emoteIsRotate;
		int section_i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate + emoteSection;

		ArrayList<Emote.PartCommand> sectionList = emoteData.commands.get(section_i);

		if(sectionList != null && emoteSelected >= 1) {
			Emote.PartCommand command = sectionList.get(emoteSelected - 1);

			if(615 <= slider.id && slider.id <= 620 || slider.id == 621) {
				Float value = 0.0F;
				String text = "";

				if(615 <= slider.id && slider.id <= 617) {//set offset
					value = ((slider.sliderValue - 0.5F) * (maxOffset * 2.0F));

					if(slider.id == 615) {
						command.x = value;
					} else if(slider.id == 616) {
						command.y = value;
					} else if(slider.id == 617) {
						command.z = value;
					}

					text = getValueAsString(value, 0);
				} else if(618 <= slider.id && slider.id <= 620) {//set rotate
					value = ((slider.sliderValue - 0.5F) * (maxRotation * 2.0F));

					if(slider.id == 618) {
						command.x = value;
					} else if(slider.id == 619) {
						command.y = value;
					} else if(slider.id == 620) {
						command.z = value;
					}

					text = getValueAsString(value, 1);
				} else if(slider.id == 621) {//set duration
					value = (slider.sliderValue)*(maxDuration);

					command.duration = value;

					text = getValueAsString(value, 0);
				}

				onEmoteChange(false);

				GuiNpcTextField textField = this.getTextField(slider.id);
				textField.setSelectionPos(0);
				textField.setCursorPositionZero();
				textField.setText(text);
			}
		}
	}
	@Override
	public void mouseReleased(GuiNpcSlider slider) {
		super.mouseReleased(slider);
		this.playerdata.startPreview(emoteData.clone());
		this.playerdata.previewRepetitions = emoteSection == 1 ? 10 : 3;
		this.initGui();
	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		int preSelected = emoteSelected;

		int meta_i = 2*emotePart + emoteIsRotate;
		int section_i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate + emoteSection;

		ArrayList<Emote.PartCommand> sectionList = emoteData.commands.get(section_i);

		if(sectionList != null) {
			if(0 <= this.scroll.selected && this.scroll.selected <= sectionList.size()) {
				emoteSelected = this.scroll.selected;
			}
		} else if(this.scroll.selected == 0 && emoteData.partUsages[meta_i] > 0) {
			emoteSelected = 0;
		}
		if(preSelected != emoteSelected) this.initGui();
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
		int meta_i = 2*emotePart + emoteIsRotate;
		int section_i = Emote.SECTION_LIST_COUNT*emotePart + 3*emoteIsRotate + emoteSection;

		ArrayList<Emote.PartCommand> sectionList = emoteData.commands.get(section_i);

		if(sectionList != null && emoteSelected >= 1) {
			Emote.PartCommand command = sectionList.get(emoteSelected - 1);

			if(615 <= textField.id && textField.id <= 620 || textField.id == 621) {
				Float value = null;
				try {
					value = Float.parseFloat(textField.getText().replace(',', '.'));
				} catch (NumberFormatException e) {
					this.initGui();
					return;
				}


				if(615 <= textField.id && textField.id <= 617) {//set offset
					value = Math.min(Emote.maxCoordRange, Math.max(-Emote.maxCoordRange, value));

					if(textField.id == 615) {
						command.x = value;
					} else if(textField.id == 616) {
						command.y = value;
					} else if(textField.id == 617) {
						command.z = value;
					}
				} else if(618 <= textField.id && textField.id <= 620) {//set rotate
					value = (float)Math.toRadians(value);
					value = Math.min(Emote.maxCoordRange, Math.max(-Emote.maxCoordRange, value));

					if(textField.id == 618) {
						command.x = value;
					} else if(textField.id == 619) {
						command.y = value;
					} else if(textField.id == 620) {
						command.z = value;
					}
				} else if(textField.id == 621) {//set duration
					value = Math.min(Emote.maxDuration, Math.max(0.0f, value));

					command.duration = value;
				}

				onEmoteChange(true);
				this.initGui();
			} else if(textField.id == 623) {//change and set easing
				int easing = TweenUtils.parseEasingToEnum(textField.getText());
				if(easing >= 0) {
					command.easing = easing;
					onEmoteChange(true);
				}

				this.initGui();
			} else if(textField.id == 626) {//edit command text
				command.consoleCommand = textField.getText();
				onEmoteChange(false);
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
