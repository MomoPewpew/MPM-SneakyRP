package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabMPM;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelData;
import noppes.mpm.client.Client;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.constants.EnumPackets;

public class GuiMPM extends GuiNPCInterface implements ICustomScrollListener, ISubGuiListener {
  public static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/smallbg.png");

  public ModelData playerdata;

  protected NBTTagCompound original = new NBTTagCompound();

  private GuiCustomScroll scroll = null;

  public GuiMPM() {
    this.playerdata = ModelData.get((EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
    this.original = this.playerdata.writeToNBT();
    this.xSize = 182;
    this.ySize = 185;
    this.drawDefaultBackground = false;
    this.closeOnEsc = true;
    if (PresetController.instance.presets.isEmpty())
      PresetController.instance.load();
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    TabRegistry.updateTabValues(this.guiLeft + 2, this.guiTop + 8, InventoryTabMPM.class);
    TabRegistry.addTabsToList(this.field_146292_n);
    if (this.scroll == null) {
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
      this.scroll.setSize(80, 160);
    }
    List<String> list = new ArrayList<>();
    for (Preset preset : PresetController.instance.presets.values()) {
      if (preset.menu)
        list.add(preset.name);
    }
    this.scroll.setList(list);
    this.scroll.setSelected(PresetController.instance.selected);
    if (!this.scroll.hasSelected())
      this.scroll.selected = 0;
    this.scroll.guiLeft = this.guiLeft + 4;
    this.scroll.guiTop = this.guiTop + 14;
    addScroll(this.scroll);
    addButton(new GuiNpcButton(0, this.guiLeft + 4, this.guiTop + 176, 20, 20, "+"));
    addButton(new GuiNpcButton(1, this.guiLeft + 26, this.guiTop + 176, 20, 20, "-"));
    (getButton(1)).enabled = (this.scroll.getList().size() > 1);
    addButton(new GuiNpcButton(2, this.guiLeft + 48, this.guiTop + 176, 60, 20, "selectServer.edit"));
    addButton(new GuiNpcButton(3, this.guiLeft + 110, this.guiTop + 176, 68, 20, "gui.config"));
  }

  public void func_73863_a(int i, int j, float f) {
    func_146276_q_();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.renderEngine.bindTexture(resource);
    drawTexturedModalRect(this.guiLeft, this.guiTop + 8, 0, 0, this.xSize, 192);
    super.func_73863_a(i, j, f);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GuiInventory.func_147046_a(this.guiLeft + 130, this.guiTop + 130, 40, (this.guiLeft + 130 - i), (this.guiTop + 60 - j), (EntityLivingBase)this.player);
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    Preset preset = PresetController.instance.getPreset(scroll.getSelected());
    if (preset != null) {
      this.playerdata.readFromNBT(preset.data.writeToNBT());
      PresetController.instance.selected = preset.name;
    }
  }

  protected void func_146284_a(GuiButton button) {
    if (!(button instanceof GuiNpcButton))
      return;
    if (button.id == 0)
      setSubGui(new GuiCreationLoad());
    if (button.id == 1) {
      GuiYesNo gui = new GuiYesNo((result, id) -> {
            if (result) {
              PresetController.instance.removePreset(this.scroll.getSelected());
              this.scroll.getList().remove(this.scroll.getSelected());
              Preset preset = PresetController.instance.getPreset(this.scroll.getList().get(0));
              this.playerdata.readFromNBT(preset.data.writeToNBT());
              PresetController.instance.selected = preset.name;
            }
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)this);
          }"", I18n.translateToLocal("message.delete"), 0);
      this.field_146297_k.displayGuiScreen((GuiScreen)gui);
    }
    if (button.id == 2)
      try {
        setSubGui((GuiNPCInterface)GuiCreationScreenInterface.Gui.getClass().newInstance());
      } catch (InstantiationException instantiationException) {

      } catch (IllegalAccessException illegalAccessException) {}
    if (button.id == 3)
      setSubGui(new GuiConfig());
  }

  public void save() {
    NBTTagCompound newCompound = this.playerdata.writeToNBT();
    if (!this.original.equals(newCompound)) {
      this.playerdata.save();
      this.playerdata.lastEdited = System.currentTimeMillis();
      Client.sendData(EnumPackets.UPDATE_PLAYER_DATA, new Object[] { newCompound });
      this.original = newCompound;
    }
  }

  public void subGuiClosed(GuiNPCInterface subgui) {
    if (subgui instanceof GuiCreationScreenInterface) {
      Preset p = PresetController.instance.getPreset(getScroll(0).getSelected());
      if (p != null) {
        p.data = this.playerdata.copy();
        PresetController.instance.save();
      }
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
