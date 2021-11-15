package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.mpm.client.gui.select.GuiTextureSelection;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationOptions extends GuiCreationScreenInterface implements ITextfieldListener {
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
		//this.addButton(new GuiNpcButton(10, this.guiLeft + 262, y, 80, 20, "gui.select"));
		y += 23;
		this.addLabel(new GuiNpcLabel(5, "part.arms", this.guiLeft, y + 5, 16777215));
		this.addButton(new GuiNpcButton(11, this.guiLeft + 58, y, 80, 20, new String[]{"gui.default", "gui.slim"}, this.playerdata.slim ? 1 : 0));
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
	public void unFocused(GuiNpcTextField guiNpcTextField) {
		this.playerdata.url = guiNpcTextField.getText();
		this.playerdata.resourceInit = false;
		this.playerdata.resourceLoaded = false;
	}

	@Override
	public void focused(GuiNpcTextField var1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField textField) {
		// TODO Auto-generated method stub

	}
}
