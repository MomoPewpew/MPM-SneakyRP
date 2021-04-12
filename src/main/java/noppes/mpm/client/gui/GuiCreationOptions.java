package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.mpm.client.gui.select.GuiTextureSelection;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationOptions extends GuiCreationScreenInterface implements ITextfieldListener {
  public void func_73866_w_() {
    super.func_73866_w_();
    int y = this.guiTop + 50;
    addButton(new GuiNpcButton(9, this.guiLeft + 58, y, 80, 20, new String[] { "gui.default", "config.humanfemale", "config.humanmale", "config.goblinmale" }, this.playerdata.soundType));
    addLabel(new GuiNpcLabel(5, "config.sounds", this.guiLeft, y + 5, 16777215));
    y += 23;
    addTextField(new GuiNpcTextField(52, (GuiScreen)this, this.guiLeft + 60, y, 200, 20, this.playerdata.url));
    addLabel(new GuiNpcLabel(52, "config.skinurl", this.guiLeft, y + 5, 16777215));
    addButton(new GuiNpcButton(10, this.guiLeft + 262, y, 80, 20, "gui.select"));
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (!(btn instanceof GuiNpcButton))
      return;
    GuiNpcButton button = (GuiNpcButton)btn;
    if (button.field_146127_k == 9)
      this.playerdata.soundType = (short)button.getValue();
    if (button.field_146127_k == 10)
      setSubGui((GuiNPCInterface)new GuiTextureSelection(this.playerdata));
  }

  public void unFocused(GuiNpcTextField guiNpcTextField) {
    this.playerdata.url = guiNpcTextField.func_146179_b();
    this.playerdata.resourceInit = false;
    this.playerdata.resourceLoaded = false;
  }
}
