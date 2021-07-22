package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import noppes.mpm.ModelData;
import noppes.mpm.Prop;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropPicker extends GuiCreationScreenInterface implements ITextfieldListener {
    private Boolean initiating = false;
    private static Prop prop;

    public GuiCreationPropPicker(Prop propArg) {
         this.active = -1;
         this.xOffset = 140;
         prop = propArg;
    }

    @Override
    public void initGui() {
    	 this.initiating = true;
         super.initGui();



         this.initiating = false;
    }

    @Override
    protected void actionPerformed(GuiButton btn) {

    }

	@Override
	public void unFocused(GuiNpcTextField var1) {

	}

	@Override
	public void focused(GuiNpcTextField var1) {

	}
}