package noppes.mpm.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.mpm.ModelData;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;

public class GuiPresetSave extends GuiNPCInterface {
  private ModelData data;

  private GuiScreen parent;

  public GuiPresetSave(GuiScreen parent, ModelData data) {
    this.data = data;
    this.parent = parent;
    this.xSize = 200;
    this.drawDefaultBackground = true;
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    addTextField(new GuiNpcTextField(0, (GuiScreen)this, this.guiLeft, this.guiTop + 70, 200, 20, ""));
    addButton(new GuiNpcButton(0, this.guiLeft, this.guiTop + 100, 98, 20, "Save"));
    addButton(new GuiNpcButton(1, this.guiLeft + 100, this.guiTop + 100, 98, 20, "Cancel"));
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    GuiNpcButton button = (GuiNpcButton)btn;
    if (button.field_146127_k == 0) {
      String name = getTextField(0).func_146179_b().trim();
      if (name.isEmpty())
        return;
      Preset preset = new Preset();
      preset.name = name;
      preset.data = this.data.copy();
      PresetController.instance.addPreset(preset);
    }
    close();
  }

  public void save() {}
}
