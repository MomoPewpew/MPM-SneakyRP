package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelData;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;

public class GuiCreationLoad extends GuiNPCInterface implements ICustomScrollListener {
  private List<String> list = new ArrayList<>();

  private GuiCustomScroll scroll;

  private static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/smallbg.png");

  private ModelData playerdata;

  private NBTTagCompound original = new NBTTagCompound();

  private String selected = "Normal";

  private HashMap<String, Preset> presets = Preset.GetDefault();

  public GuiCreationLoad() {
    this.playerdata = ModelData.get((EntityPlayer)(Minecraft.func_71410_x()).field_71439_g);
    this.original = this.playerdata.writeToNBT();
    this.drawDefaultBackground = false;
    this.closeOnEsc = true;
    this.presets.putAll(PresetController.instance.presets);
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    if (this.scroll == null) {
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
      for (Preset preset : this.presets.values())
        this.list.add(preset.name);
      this.scroll.setList(this.list);
      this.scroll.setSelected(this.selected);
      this.scroll.scrollTo(this.selected);
    }
    this.scroll.guiLeft = this.guiLeft + 4;
    this.scroll.guiTop = this.guiTop + 33;
    this.scroll.setSize(100, 144);
    addScroll(this.scroll);
    addTextField(new GuiNpcTextField(0, (GuiScreen)this, this.guiLeft + 4, this.guiTop + 12, 172, 20, "gui.new"));
    addButton(new GuiNpcButton(10, this.guiLeft + 4, this.guiTop + this.ySize - 46, 86, 20, "gui.done"));
    addButton(new GuiNpcButton(11, this.guiLeft + 92, this.guiTop + this.ySize - 46, 86, 20, "gui.cancel"));
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (btn.field_146127_k == 10) {
      this.original = this.playerdata.writeToNBT();
      Preset p = new Preset();
      p.menu = true;
      String name = getTextField(0).func_146179_b();
      if (name.trim().isEmpty())
        name = I18n.func_74838_a("gui.new");
      p.name = name;
      p.data = this.playerdata.copy();
      PresetController.instance.selected = name;
      PresetController.instance.addPreset(p);
      close();
    }
    if (btn.field_146127_k == 11)
      close();
  }

  public void func_73863_a(int i, int j, float f) {
    func_146276_q_();
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(resource);
    func_73729_b(this.guiLeft, this.guiTop + 8, 0, 0, this.xSize, 192);
    super.func_73863_a(i, j, f);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    GuiInventory.func_147046_a(this.guiLeft + 144, this.guiTop + 140, 40, (this.guiLeft + 144 - i), (this.guiTop + 80 - j), (EntityLivingBase)this.player);
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    this.selected = scroll.getSelected();
    Preset preset = this.presets.get(this.selected.toLowerCase());
    if (preset != null) {
      this.playerdata.readFromNBT(preset.data.writeToNBT());
      func_73866_w_();
    }
  }

  public void save() {
    this.playerdata.readFromNBT(this.original);
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
