package noppes.mpm.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.util.MPMEntityUtil;
import org.lwjgl.input.Keyboard;

public abstract class GuiCreationScreenInterface extends GuiNPCInterface implements ISubGuiListener, ISliderListener {
  public static String Message = "";

  public EntityLivingBase entity;

  public int active = 0;

  private EntityPlayer player;

  public int xOffset = 0;

  public ModelData playerdata;

  public static GuiCreationScreenInterface Gui = new GuiCreationParts();

  private static float rotation = 0.5F;

  public GuiCreationScreenInterface() {
    this.playerdata = ModelData.get((EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
    this.xSize = 400;
    this.ySize = 240;
    this.xOffset = 140;
    this.player = (EntityPlayer)(Minecraft.getMinecraft()).thePlayer;
    this.closeOnEsc = true;
  }

  @Override
  public void initGui() {
    super.initGui();
    this.entity = this.playerdata.getEntity((EntityPlayer)this.mc.thePlayer);
    Keyboard.enableRepeatEvents(true);
    addButton(new GuiNpcButton(0, this.guiLeft, this.guiTop, 60, 20, "gui.options"));
    addButton(new GuiNpcButton(1, this.guiLeft + 62, this.guiTop, 60, 20, "gui.entity"));
    if (this.entity == null) {
      addButton(new GuiNpcButton(2, this.guiLeft, this.guiTop + 23, 60, 20, "gui.parts"));
    } else {
      GuiCreationExtra gui = new GuiCreationExtra();
      gui.playerdata = this.playerdata;
      if (!gui.getData(this.entity).isEmpty()) {
        addButton(new GuiNpcButton(2, this.guiLeft, this.guiTop + 23, 60, 20, "gui.extra"));
      } else if (this.active == 2) {
        openGui(new GuiCreationEntities());
        return;
      }
    }
    if (this.entity == null)
      addButton(new GuiNpcButton(3, this.guiLeft + 62, this.guiTop + 23, 60, 20, "gui.scale"));
    (getButton(this.active)).enabled = false;
    addButton(new GuiNpcButton(66, this.guiLeft + this.xSize - 20, this.guiTop, 20, 20, "X"));
    addLabel(new GuiNpcLabel(0, Message, this.guiLeft + 120, this.guiTop + this.ySize - 10, 16711680));
    getLabel(0).center(this.xSize - 120);
    addSlider(new GuiNpcSlider((GuiScreen)this, 500, this.guiLeft + this.xOffset + 142, this.guiTop + 210, 120, 20, rotation));
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    super.actionPerformed(btn);
    if (btn.id == 0)
      openGui(new GuiCreationOptions());
    if (btn.id == 1)
      openGui(new GuiCreationEntities());
    if (btn.id == 2)
      if (this.entity == null) {
        openGui(new GuiCreationParts());
      } else {
        openGui(new GuiCreationExtra());
      }
    if (btn.id == 3)
      openGui(new GuiCreationScale());
    if (btn.id == 66)
      close();
  }

  public void drawScreen(int x, int y, float f) {
    EntityPlayer entityPlayer;
    super.drawScreen(x, y, f);
    this.entity = this.playerdata.getEntity((EntityPlayer)this.mc.thePlayer);
    EntityLivingBase entity = this.entity;
    if (entity == null) {
      entityPlayer = this.player;
    } else {
      MPMEntityUtil.Copy((EntityLivingBase)this.mc.thePlayer, (EntityLivingBase)this.player);
    }
    drawNpc((EntityLivingBase)entityPlayer, this.xOffset + 200, 200, 1.0F, (int)(rotation * 360.0F - 180.0F));
  }

  public void onGuiClosed() {
    super.onGuiClosed();
    Keyboard.enableRepeatEvents(false);
  }

  public void save() {}

  public boolean drawSubGuiBackground() {
    return true;
  }

  public void openGui(GuiNPCInterface gui) {
    this.parent.setSubGui(gui);
    if (gui instanceof GuiCreationScreenInterface)
      Gui = (GuiCreationScreenInterface)gui;
  }

  public void subGuiClosed(GuiNPCInterface subgui) {
    initGui();
  }

  public void mouseDragged(GuiNpcSlider slider) {
    if (slider.id == 500) {
      rotation = slider.sliderValue;
      slider.setString("" + (int)(rotation * 360.0F));
    }
  }

  public void mousePressed(GuiNpcSlider slider) {}

  public void mouseReleased(GuiNpcSlider slider) {}
}
