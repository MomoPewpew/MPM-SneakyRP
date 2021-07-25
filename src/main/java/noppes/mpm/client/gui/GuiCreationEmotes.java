package noppes.mpm.client.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
// import net.minecraft.nbt.NBTTagCompound;

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

import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenUtils;

public class GuiCreationEmotes extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener, ITextfieldListener {
	public final List<String> bodyParts = Arrays.asList(
	"lefthand", "righthand", "head", "body", "leftfoot", "rightfoot"
	);
	public static final Float maxOffset = 2.0F;
	public static final Float maxRotation = 360.0F;
	public static final Float maxDuration = 5.0F;
	public static PartCommand clipboard_command = null;
	// public static GuiCreationEmotes instance = new GuiCreationEmotes();

	public class PartCommand {
		public Float x = 0.0F;
		public Float y = 0.0F;
		public Float z = 0.0F;
		public Float duration = 1.0F;
		public TweenEquation easing = Linear.INOUT;
		public boolean disabled = false;

		public PartCommand clone() {
			PartCommand command = new PartCommand();
			command.x = x;
			command.y = y;
			command.z = z;
			command.duration = duration;
			command.easing = easing;
			command.disabled = disabled;
			return command;
		}
	}
	public class PartCommands {
		public ArrayList<PartCommand> intro_offset = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> intro_rotate = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> loop_offset = new ArrayList<PartCommand>();
		public ArrayList<PartCommand> loop_rotate = new ArrayList<PartCommand>();

		public ArrayList<PartCommand> getCommandList(boolean isintro, boolean isoffset) {//in c I could do this trivially and efficiently with a union
			if(isintro) {
				if(isoffset) {
					return this.intro_offset;
				} else {
					return this.intro_rotate;
				}
			} else {
				if(isoffset) {
					return this.loop_offset;
				} else {
					return this.loop_rotate;
				}
			}
		}
	}
	public class Emote {
		public HashMap<String, PartCommands> commands = new HashMap<String, PartCommands>();
	}


	private GuiCustomScroll scroll;
	public boolean initiating = false;
	public Emote cur_emote;
	public String cur_part = "head";
	public boolean iseditingintro = true;
	public boolean iseditingoffset = true;
	public int selected_i = -1;


	public GuiCreationEmotes() {
		this.playerdata = ModelData.get(this.getPlayer());
		this.active = 500;
		this.xOffset = 140;

		this.cur_emote = new Emote();
	}

	public void resetAndReplaceEmote(Emote emote) {
		this.cur_emote = emote;
		cur_part = "head";
		iseditingintro = true;
		iseditingoffset = true;
		selected_i = -1;
		//NOTE: does not reset the clipboard_command
		// clipboard_command = null;
	}


	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		PartCommands cur_part_commands = this.cur_emote.commands.get(this.cur_part);
		ArrayList<PartCommand> cur_command_list = null;
		if(cur_part_commands != null) {
			cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);
		}

		//start at top left of menu
		int x = this.guiLeft;
		int y = this.guiTop + 45;
		this.addButton(new GuiNpcButton(101, x,      y, 50, 20, "gui.intro"));
		this.addButton(new GuiNpcButton(102, x + 50, y, 50, 20, "gui.loop"));
		if(this.iseditingintro) {
			this.getButton(101).enabled = false;
		} else {
			this.getButton(102).enabled = false;
		}
		y += 22;
		this.addButton(new GuiNpcButton(103, x,      y, 50, 20, "gui.offset"));
		this.addButton(new GuiNpcButton(104, x + 50, y, 50, 20, "gui.rotate"));
		if(this.iseditingoffset) {
			this.getButton(103).enabled = false;
		} else {
			this.getButton(104).enabled = false;
		}
		y += 22;
		this.addButton(new GuiNpcButton(105, x, y, 100, 20, "gui." + this.cur_part));
		y += 22;


		if(this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0, false);
		}

		ArrayList<String> command_display_names = new ArrayList<String>();
		this.scroll.colorlist = new ArrayList<Integer>();
		if(cur_part_commands != null) {
			for(int i = 0; i < cur_command_list.size(); i++) {
				PartCommand command = cur_command_list.get(i);

				if(command.disabled) {
        		   this.scroll.colorlist.add(8421504);
				} else {
        		   this.scroll.colorlist.add(16777215);
				}
				String str;
				if(this.iseditingoffset) {
					str = String.format(java.util.Locale.US, "%.2f, %.2f, %.2f", command.x, command.y, command.z);
				} else {
					str = String.format(java.util.Locale.US, "%.1f, %.1f, %.1f", command.x, command.y, command.z);
				}
				command_display_names.add(str);
			}
		} else {
			this.scroll.colorlist.add(8421504);
			command_display_names.add("None");
		}

		this.scroll.selected = this.selected_i;
		this.scroll.setUnsortedList(command_display_names);
		this.scroll.guiLeft = x;
		this.scroll.guiTop = y - 1;
		this.scroll.setSize(100, this.ySize - 74);
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
		this.addButton(new GuiNpcButton(110, x, y, 20, 20, "+"));

		if(cur_part_commands != null && this.selected_i >= 0) {
			PartCommand cur_command = cur_command_list.get(this.selected_i);

			this.addButton(new GuiNpcButton(111, x + 22,  y, 20, 20, "-"));
			this.addButton(new GuiNpcButton(112, x + 44,  y, 40, 20, "gui.copy"));
			this.addButton(new GuiNpcButton(113, x + 86,  y, 40, 20, "gui.paste"));
			this.addButton(new GuiNpcButton(114, x + 128, y, 60, 20, "gui.duplicate"));
			y += 22;

			this.addButton(new GuiNpcButton(115, x,      y, 20, 20, "gui.moveup"));
			this.addButton(new GuiNpcButton(116, x + 22, y, 20, 20, "gui.movedown"));
			if(this.selected_i == 0) {
				this.getButton(115).enabled = false;
			} else if(this.selected_i == cur_command_list.size() - 1) {
				this.getButton(116).enabled = false;
			}
			this.addButton(new GuiNpcButton(133, x + 44, y, 82, 20, cur_command.disabled ? "gui.enable" : "gui.disable"));

			y += 22;

			if(this.iseditingoffset) {
				this.addTextField(new GuiNpcTextField(120, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.x)));
				this.addSlider(new GuiNpcSlider(this, 120, x, y, 152, 20, ((cur_command.x + maxOffset) / (maxOffset * 2.0F))));
				this.getSlider(120).displayString = "X";

				y += 22;
				this.addTextField(new GuiNpcTextField(121, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.y)));
				this.addSlider(new GuiNpcSlider(this, 121, x, y, 152, 20, ((cur_command.y + maxOffset) / (maxOffset * 2.0F))));
				this.getSlider(121).displayString = "Y";

				y += 22;
				this.addTextField(new GuiNpcTextField(122, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.z)));
				this.addSlider(new GuiNpcSlider(this, 122, x, y, 152, 20, ((cur_command.z + maxOffset) / (maxOffset * 2.0F))));
				this.getSlider(122).displayString = "Z";

			} else {
				this.addTextField(new GuiNpcTextField(123, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.1f", cur_command.x)));
				this.addSlider(new GuiNpcSlider(this, 123, x, y, 152, 20, ((cur_command.x + maxRotation) / (maxRotation * 2.0F))));
				this.getSlider(123).displayString = "X";

				y += 22;
				this.addTextField(new GuiNpcTextField(124, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.1f", cur_command.y)));
				this.addSlider(new GuiNpcSlider(this, 124, x, y, 152, 20, ((cur_command.y + maxRotation) / (maxRotation * 2.0F))));
				this.getSlider(124).displayString = "Y";

				y += 22;
				this.addTextField(new GuiNpcTextField(125, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.1f", cur_command.z)));
				this.addSlider(new GuiNpcSlider(this, 125, x, y, 152, 20, ((cur_command.z + maxRotation) / (maxRotation * 2.0F))));
				this.getSlider(125).displayString = "Z";
			}
			y += 22;

			this.addTextField(new GuiNpcTextField(130, this, x + 155, y + 1, 36, 18, String.format(java.util.Locale.US, "%.2f", cur_command.duration)));
			this.addSlider(new GuiNpcSlider(this, 130, x, y, 152, 20, ((cur_command.duration) / (maxDuration))));
			this.getSlider(130).displayString = "Duration";
			y += 22;

			this.addLabel(new GuiNpcLabel(132, "gui.easing", x,      y + 5, 16777215));
			this.addTextField(new GuiNpcTextField(131, this, x + 33, y, 185, 20, cur_command.easing.toString().toLowerCase()));
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if(this.initiating) return;

		PartCommands cur_part_commands = this.cur_emote.commands.get(this.cur_part);
		ArrayList<PartCommand> cur_command_list = null;
		if(cur_part_commands != null) {
			cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);
		}

		if(btn.id == 110) {//add anim command
			PartCommand command = new PartCommand();
			if(cur_part_commands == null) {
				cur_part_commands = new PartCommands();
				cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);
				this.cur_emote.commands.put(this.cur_part, cur_part_commands);
			}
			this.selected_i = cur_command_list.size();
			cur_command_list.add(command);
			this.initGui();
		} else if(btn.id == 101) {//goto intro commands
			this.iseditingintro = true;
			this.selected_i = -1;
			this.initGui();
		} else if(btn.id == 102) {//goto loop commands
			this.iseditingintro = false;
			this.selected_i = -1;
			this.initGui();
		} else if(btn.id == 103) {//goto offset commands
			this.iseditingoffset = true;
			this.selected_i = -1;
			this.initGui();
		} else if(btn.id == 104) {//goto rotate commands
			this.iseditingoffset = false;
			this.selected_i = -1;
			this.initGui();
		} else if(btn.id == 105) {//change and goto part commands
			for(int i = 0; i < bodyParts.size(); i++) {
				if(bodyParts.get(i).equals(this.cur_part)) {
					this.cur_part = bodyParts.get((i + 1)%bodyParts.size());
					break;
				}
			}
			this.initGui();
		} else if(cur_part_commands != null && this.selected_i >= 0) {
			PartCommand cur_command = cur_command_list.get(this.selected_i);

			if(btn.id == 111) {//remove anim command
				cur_command_list.remove(this.selected_i);
				if(this.selected_i == cur_command_list.size()) this.selected_i--;
				if(cur_command_list.size() == 0) this.cur_emote.commands.remove(this.cur_part);
				this.initGui();
			} else if(btn.id == 112) {//copy anim command
				this.clipboard_command = cur_command.clone();
			} else if(btn.id == 113) {//paste anim command
				cur_command_list.set(this.selected_i, this.clipboard_command.clone());
				this.initGui();
			} else if(btn.id == 114) {//duplicate anim command
				cur_command_list.add(this.selected_i++, cur_command.clone());
				this.initGui();
			} else if(btn.id == 115) {//move anim command up
				if(this.selected_i > 0) {
					cur_command_list.set(this.selected_i, cur_command_list.get(this.selected_i - 1));
					cur_command_list.set(this.selected_i - 1, cur_command);
					this.selected_i -= 1;
					this.initGui();
				}
			} else if(btn.id == 116) {//move anim command down
				if(this.selected_i < cur_command_list.size() - 1) {
					cur_command_list.set(this.selected_i, cur_command_list.get(this.selected_i + 1));
					cur_command_list.set(this.selected_i + 1, cur_command);
					this.selected_i += 1;
					this.initGui();
				}
			} else if(btn.id == 133) {//toggle disable anim command
				cur_command.disabled = !cur_command.disabled;
				this.initGui();
			}
		}
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);
		if(this.initiating) return;

		PartCommands cur_part_commands = this.cur_emote.commands.get(this.cur_part);

		if(cur_part_commands != null && this.selected_i >= 0) {
			ArrayList<PartCommand> cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);
			PartCommand cur_command = cur_command_list.get(this.selected_i);

			if(120 <= slider.id && slider.id <= 125 || slider.id == 130) {
				Float value = 0.0F;
				String text = "";

				if(120 <= slider.id && slider.id <= 122) {//set offset
					value = ((slider.sliderValue - 0.5F) * (maxOffset * 2.0F));

					if(slider.id == 120) {
						cur_command.x = value;
					} else if(slider.id == 121) {
						cur_command.y = value;
					} else if(slider.id == 122) {
						cur_command.z = value;
					}

					text = String.format(java.util.Locale.US, "%.2f", value);
				} else if(123 <= slider.id && slider.id <= 125) {//set rotate
					value = ((slider.sliderValue - 0.5F) * (maxRotation * 2.0F));

					if(slider.id == 123) {
						cur_command.x = value;
					} else if(slider.id == 124) {
						cur_command.y = value;
					} else if(slider.id == 125) {
						cur_command.z = value;
					}

					text = String.format(java.util.Locale.US, "%.1f", value);
				} else if(slider.id == 130) {//set duration
					value = ((slider.sliderValue) * (maxDuration));

					cur_command.duration = value;

					text = String.format(java.util.Locale.US, "%.2f", value);
				}

				this.getTextField(slider.id).setText(text);

				if(slider.id != 130) {//update command name in scroll
					ArrayList<String> command_display_names = new ArrayList<String>();
					for(int i = 0; i < cur_command_list.size(); i++) {
						PartCommand command = cur_command_list.get(i);

						String str;
						if(this.iseditingoffset) {
							str = String.format(java.util.Locale.US, "%.2f, %.2f, %.2f", command.x, command.y, command.z);
						} else {
							str = String.format(java.util.Locale.US, "%.1f, %.1f, %.1f", command.x, command.y, command.z);
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
		int pre_selected = this.selected_i;

		PartCommands cur_part_commands = this.cur_emote.commands.get(this.cur_part);
		if(cur_part_commands != null) {
			ArrayList<PartCommand> cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);

			if(0 <= this.scroll.selected && this.scroll.selected < cur_command_list.size()) {
				this.selected_i = this.scroll.selected;
			}
		}
		if(pre_selected != this.scroll.selected) this.initGui();
	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
	// 	// if(scroll.selected >= propGroupAmount) {
	// 	// 	this.openGui(new GuiCreationPropRename(selected - propGroupAmount));
	// 	// } else if(scroll.selected >= 0) {
	// 	// 	this.openGui(new GuiCreationEmotes(selectedPropGroup));
	// 	// }
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {

	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if(this.initiating) return;

		PartCommands cur_part_commands = this.cur_emote.commands.get(this.cur_part);

		if(cur_part_commands != null && this.selected_i >= 0) {
			ArrayList<PartCommand> cur_command_list = cur_part_commands.getCommandList(this.iseditingintro, this.iseditingoffset);
			PartCommand cur_command = cur_command_list.get(this.selected_i);

			if(120 <= textField.id && textField.id <= 125 || textField.id == 130) {
				Float value = null;
				try {
					value = Float.parseFloat(textField.getText().replace(',', '.'));
				} catch (NumberFormatException e) {
					return;
				}

				Float sliderValue = 0.0F;

				if(120 <= textField.id && textField.id <= 122) {//set offset
					sliderValue = (value + maxOffset) / (maxOffset * 2.0F);

					if(textField.id == 120) {
						cur_command.x = value;
					} else if(textField.id == 113) {
						cur_command.y = value;
					} else if(textField.id == 114) {
						cur_command.z = value;
					}
				} else if(123 <= textField.id && textField.id <= 125) {//set rotate
					sliderValue = (value + maxRotation) / (maxRotation * 2.0F);

					if(textField.id == 115) {
						cur_command.x = value;
					} else if(textField.id == 116) {
						cur_command.y = value;
					} else if(textField.id == 117) {
						cur_command.z = value;
					}
				} else if(textField.id == 130) {//set duration
					sliderValue = (value) / (maxDuration);

					cur_command.duration = value;
				}

				// textField.setCursorPositionZero();
				// textField.setSelectionPos(0);
				// this.getSlider(textField.id).sliderValue = sliderValue;
				this.initGui();
			} else if(textField.id == 131) {//change and set easing
				TweenEquation easing = TweenUtils.parseEasing(textField.getText());
				if(easing != null) {
					cur_command.easing = easing;
				}

				this.initGui();
			}
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if(this.initiating) return;

		if(120 <= textField.id && textField.id <= 125 || textField.id == 130 || textField.id == 131) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}
}
