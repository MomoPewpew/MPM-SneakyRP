package noppes.mpm.client.gui;

import noppes.mpm.ModelData;
import noppes.mpm.Prop;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropRename extends GuiCreationScreenInterface implements ITextfieldListener {
    private Boolean initiating = false;
    private static Prop prop;

    public GuiCreationPropRename(int index) {
         this.active = -1;
         this.xOffset = 140;
         prop = ModelData.get(this.getPlayer()).props.get(index);
    }

    @Override
    public void initGui() {
    	 this.initiating = true;
         super.initGui();

         Integer x = this.guiLeft + 102;
         Integer y = this.guiTop + 67;
         this.addLabel(new GuiNpcLabel(201, "gui.name", x, y + 5, 16777215));
         this.addTextField(new GuiNpcTextField(201, this, x + 33, y, 185, 20, prop.name));

         this.initiating = false;
    }

	@Override
	public void unFocused(GuiNpcTextField textField) {

		if (this.initiating) return;

		if (textField.id == 201) {

		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {}
}