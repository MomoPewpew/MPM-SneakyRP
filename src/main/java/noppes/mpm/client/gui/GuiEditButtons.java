package noppes.mpm.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import org.lwjgl.input.Keyboard;

public class GuiEditButtons extends GuiNPCInterface {
  private final String[] animations = new String[] { "gui.none", "animation.sleep", "animation.crawl", "animation.hug", "animation.sit", "animation.dance", "animation.wave", "animation.wag", "animation.bow", "animation.cry" };

  public GuiEditButtons() {
    this.closeOnEsc = true;
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    int y = this.guiTop + 20;
    addLabel(new GuiNpcLabel(0, "message.animationmessage1", this.guiLeft, y, 16777215));
    addLabel(new GuiNpcLabel(6, "message.animationmessage2", this.guiLeft, y + 11, 16777215));
    getLabel(0).center(this.xSize);
    getLabel(6).center(this.xSize);
    y += 22;
    addButton(1, y, "MPM 1", MorePlayerModels.button1);
    y += 22;
    addButton(2, y, "MPM 2", MorePlayerModels.button2);
    y += 22;
    addButton(3, y, "MPM 3", MorePlayerModels.button3);
    y += 22;
    addButton(4, y, "MPM 4", MorePlayerModels.button4);
    y += 22;
    addButton(5, y, "MPM 5", MorePlayerModels.button5);
  }

  private void addButton(int id, int y, String title, int value) {
    for (KeyBinding key : (Minecraft.getMinecraft()).field_71474_y.field_74324_K) {
      if (key.func_151464_g().equals(title)) {
        title = title + " (" + Keyboard.getKeyName(key.func_151463_i()) + ")";
        break;
      }
    }
    value = getValue(value);
    addButton(new GuiNpcButton(id, this.guiLeft + 50, y, 70, 20, this.animations, value));
    addLabel(new GuiNpcLabel(id, title, this.guiLeft, y + 5, 16777215));
  }

  private int getValue(int i) {
    if (i == 0)
      return 0;
    if (i >= 1 && i <= 4)
      return 1;
    return i - 3;
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    GuiNpcButton button = (GuiNpcButton)btn;
    if (button.id == 1) {
      MorePlayerModels.button1 = getValue(button);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 2) {
      MorePlayerModels.button2 = getValue(button);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 3) {
      MorePlayerModels.button3 = getValue(button);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 4) {
      MorePlayerModels.button4 = getValue(button);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 5) {
      MorePlayerModels.button5 = getValue(button);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 66)
      close();
  }

  private int getValue(GuiNpcButton button) {
    int value = button.getValue();
    if (value <= 1)
      return value;
    return value + 3;
  }

  public void save() {}
}
