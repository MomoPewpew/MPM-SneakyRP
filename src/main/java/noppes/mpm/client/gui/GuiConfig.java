package noppes.mpm.client.gui;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.ClientProxy;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;

public class GuiConfig extends GuiNPCInterface {
  public void func_73866_w_() {
    super.func_73866_w_();
    int y = this.guiTop + 20;
    y += 32;
    addButton(new GuiNpcButton(46, this.guiLeft, y, 80, 20, "config.reloadskins"));
    addButton(new GuiNpcButton(51, this.guiLeft + 90, y, 80, 20, "config.editbuttons"));
    addButton(new GuiNpcButton(47, this.guiLeft + 90, y + 22, 50, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.EnablePOV ? 1 : 0));
    addLabel(new GuiNpcLabel(47, "config.pov", this.guiLeft, y + 27, 16777215));
    y += 22;
    addButton(new GuiNpcButton(48, this.guiLeft + 90 + 144, y, 60, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.EnableChatBubbles ? 1 : 0));
    addLabel(new GuiNpcLabel(48, "config.chatbubbles", this.guiLeft + 144, y + 5, 16777215));
    addButton(new GuiNpcButton(49, this.guiLeft + 90, y + 22, 50, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.EnableBackItem ? 1 : 0));
    addLabel(new GuiNpcLabel(49, "config.backitem", this.guiLeft, y + 27, 16777215));
    y += 22;
    addButton(new GuiNpcButton(50, this.guiLeft + 90 + 144, y, 50, 20, new String[] { "gui.no", "1", "2", "3", "4" }, MorePlayerModels.Tooltips));
    addLabel(new GuiNpcLabel(50, "config.tooltip", this.guiLeft + 144, y + 5, 16777215));
    addButton(new GuiNpcButton(57, this.guiLeft + 90 + 144, y + 22, 50, 20, new String[] { "gui.yes", "gui.no" }, MorePlayerModels.HidePlayerNames ? 1 : 0));
    addLabel(new GuiNpcLabel(57, "config.names", this.guiLeft + 144, y + 27, 16777215));
    y += 22;
    addButton(new GuiNpcButton(53, this.guiLeft + 90, y, 50, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.EnableParticles ? 1 : 0));
    addLabel(new GuiNpcLabel(53, "config.particles", this.guiLeft, y + 5, 16777215));
    addButton(new GuiNpcButton(56, this.guiLeft + 90 + 144, y + 22, 50, 20, new String[] { "gui.yes", "gui.no" }, MorePlayerModels.HideSelectionBox ? 1 : 0));
    addLabel(new GuiNpcLabel(56, "config.blockhighlight", this.guiLeft + 144, y + 27, 16777215));
    y += 22;
    addButton(new GuiNpcButton(54, this.guiLeft + 90, y, 50, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.HeadWearType));
    addLabel(new GuiNpcLabel(54, "config.solidheadlayer", this.guiLeft, y + 5, 16777215));
    y += 22;
    addButton(new GuiNpcButton(55, this.guiLeft + 90, y, 50, 20, new String[] { "gui.no", "gui.yes" }, MorePlayerModels.Compatibility ? 1 : 0));
    addLabel(new GuiNpcLabel(55, "config.compatibility", this.guiLeft, y + 5, 16777215));
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (!(btn instanceof GuiNpcButton))
      return;
    GuiNpcButton button = (GuiNpcButton)btn;
    if (button.id == 46) {
      List<EntityPlayer> players = this.field_146297_k.theWorld.field_73010_i;
      for (EntityPlayer player : players) {
        ModelData data = ModelData.get(player);
        data.resourceLoaded = false;
        data.resourceInit = false;
        data.webapiInit = false;
      }
    }
    if (button.id == 47) {
      MorePlayerModels.EnablePOV = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 48) {
      MorePlayerModels.EnableChatBubbles = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 49) {
      MorePlayerModels.EnableBackItem = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 50) {
      MorePlayerModels.Tooltips = button.getValue();
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 51)
      this.field_146297_k.displayGuiScreen((GuiScreen)new GuiEditButtons());
    if (button.id == 53) {
      MorePlayerModels.EnableParticles = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 54) {
      MorePlayerModels.HeadWearType = button.getValue();
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 55) {
      MorePlayerModels.Compatibility = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
      ClientProxy.fixModels(false);
    }
    if (button.id == 56) {
      MorePlayerModels.HideSelectionBox = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
    if (button.id == 57) {
      MorePlayerModels.HidePlayerNames = (button.getValue() == 1);
      MorePlayerModels.instance.configLoader.updateConfig();
    }
  }

  public void save() {}
}
