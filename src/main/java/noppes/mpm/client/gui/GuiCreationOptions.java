package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.mpm.client.gui.select.GuiTextureSelection;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationOptions extends GuiCreationScreenInterface implements ISliderListener, ITextfieldListener {
	private static final float maxModelOffset = 2.0F;
	private static final float minModelOffset = 0.0F;

	public GuiCreationOptions() {
		this.xOffset = 150;
	}

	@Override
	public void initGui() {
		super.initGui();
		int y = this.guiTop + 50;
		this.addButton(new GuiNpcButton(9, this.guiLeft + 58, y, 80, 20, new String[]{"gui.default", "config.humanfemale", "config.humanmale", "config.goblinmale"}, this.playerdata.soundType));
		this.addLabel(new GuiNpcLabel(5, "config.sounds", this.guiLeft, y + 5, 16777215));
		int var10005 = this.guiLeft + 60;
		y += 23;
		this.addTextField(new GuiNpcTextField(52, this, var10005, y, 200, 20, this.playerdata.url));
		this.addLabel(new GuiNpcLabel(52, "config.skinurl", this.guiLeft, y + 5, 16777215));
		y += 23;
		this.addTextField(new GuiNpcTextField(54, this, var10005, y, 200, 20, this.playerdata.displayName));
		this.addLabel(new GuiNpcLabel(54, "gui.name", this.guiLeft, y + 5, 16777215));
		//this.addButton(new GuiNpcButton(10, this.guiLeft + 262, y, 80, 20, "gui.select"));
		y += 23;
		this.addLabel(new GuiNpcLabel(11, "part.arms", this.guiLeft, y + 5, 16777215));
		this.addButton(new GuiNpcButton(11, this.guiLeft + 58, y, 80, 20, new String[]{"gui.default", "gui.slim"}, this.playerdata.slim ? 1 : 0));
		y += 23;
		int x = this.guiLeft;
		this.addLabel(new GuiNpcLabel(53, "gui.offset", x, y + 5, 16777215));
		this.addTextField(new GuiNpcTextField(53, this, x + 161, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.modelOffsetY)));
		this.addSlider(new GuiNpcSlider(this, 53, x + 58, y, 100, 20, ((this.playerdata.modelOffsetY - minModelOffset) / (maxModelOffset - minModelOffset))));
		this.getSlider(53).displayString = "Y";
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if (btn instanceof GuiNpcButton) {
			GuiNpcButton button = (GuiNpcButton)btn;
			if (button.id == 9) {
				this.playerdata.soundType = (short)button.getValue();
			}

			if (button.id == 10) {
				this.setSubGui(new GuiTextureSelection(this.playerdata));
			}

			if (button.id == 11) {
				this.playerdata.slim = button.getValue() == 1;
				if (this.getPlayer() != null) this.playerdata.reloadSkinType();
			}

		}
	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (textField.id == 52) {
			this.playerdata.url = textField.getText();
			this.playerdata.resourceInit = false;
			this.playerdata.resourceLoaded = false;
		} else if (textField.id == 53) {
			Float value = null;
			try {
				value = Float.parseFloat(textField.getText().replace(',', '.'));
			} catch (NumberFormatException e) {
				return;
			}

			Float sliderValue = 0.0F;

			sliderValue = (value - minModelOffset) / (maxModelOffset - minModelOffset);

			this.playerdata.modelOffsetY = value;

			textField.setCursorPositionZero();
			textField.setSelectionPos(0);
			this.getSlider(textField.id).sliderValue = sliderValue;
		} else if (textField.id == 54) {
			this.playerdata.displayName = textField.getText();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (textField.id ==53) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField textField) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);

		if (slider.id == 53) {
			Float value = 0.0F;
			String text = "";

			value = ((slider.sliderValue * (maxModelOffset - minModelOffset)) + minModelOffset);

			this.playerdata.modelOffsetY = value;

			text = String.format(java.util.Locale.US,"%.2f", value);

			this.getTextField(slider.id).setText(text);
		}
	}
}
