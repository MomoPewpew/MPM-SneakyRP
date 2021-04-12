package noppes.mpm.client.gui.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiNPCInterface extends GuiScreen {
  public EntityPlayerSP player;

  public boolean drawDefaultBackground = true;

  private HashMap<Integer, GuiNpcButton> buttons = new HashMap<>();

  private HashMap<Integer, GuiNpcTextField> textfields = new HashMap<>();

  private HashMap<Integer, GuiNpcLabel> labels = new HashMap<>();

  private HashMap<Integer, GuiCustomScroll> scrolls = new HashMap<>();

  private HashMap<Integer, GuiNpcSlider> sliders = new HashMap<>();

  private HashMap<Integer, GuiScreen> extra = new HashMap<>();

  protected ResourceLocation background = null;

  public boolean closeOnEsc = false;

  public int guiLeft;

  public int guiTop;

  public int xSize;

  public int ySize;

  private GuiNPCInterface subgui;

  public GuiNPCInterface parent;

  public int mouseX;

  public int mouseY;

  public GuiNPCInterface() {
    this.player = (Minecraft.getMinecraft()).thePlayer;
    this.xSize = 200;
    this.ySize = 222;
  }

  public void setBackground(String texture) {
    this.background = new ResourceLocation("moreplayermodels", "textures/gui/" + texture);
  }

  public ResourceLocation getResource(String texture) {
    return new ResourceLocation("moreplayermodels", "textures/gui/" + texture);
  }

  @Override
  public void initGui() {
    super.initGui();
    GuiNpcTextField.unfocus();
    if (this.subgui != null) {
      this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
      this.subgui.initGui();
    }
    this.guiLeft = (this.width - this.xSize) / 2;
    this.guiTop = (this.height - this.ySize) / 2;
    this.buttonList.clear();
    this.labels.clear();
    this.textfields.clear();
    this.buttons.clear();
    this.scrolls.clear();
    this.sliders.clear();
    Keyboard.enableRepeatEvents(true);
  }

  @Override
  public void updateScreen() {
    if (this.subgui != null) {
      this.subgui.updateScreen();
    } else {
      for (GuiNpcTextField tf : this.textfields.values()) {
        if (tf.enabled)
          tf.updateCursorCounter();
      }
      super.updateScreen();
    }
  }

  @Override
  public void mouseClicked(int i, int j, int k) throws IOException {
    if (this.subgui != null) {
      this.subgui.mouseClicked(i, j, k);
    } else {
      for (GuiNpcTextField tf : new ArrayList(this.textfields.values())) {
        if (tf.enabled)
          tf.mouseClicked(i, j, k);
      }
      if (k == 0)
        for (GuiCustomScroll scroll : new ArrayList(this.scrolls.values()))
          scroll.mouseClicked(i, j, k);
      mouseEvent(i, j, k);
      super.mouseClicked(i, j, k);
    }
  }

  public void mouseEvent(int i, int j, int k) {}

  @Override
  protected void actionPerformed(GuiButton guibutton) {
    if (this.subgui != null) {
      this.subgui.buttonEvent(guibutton);
    } else {
      buttonEvent(guibutton);
    }
  }

  public void buttonEvent(GuiButton guibutton) {}

  @Override
  public void keyTyped(char c, int i) {
    if (this.subgui != null) {
      this.subgui.keyTyped(c, i);
    } else {
      for (GuiNpcTextField tf : this.textfields.values())
        tf.func_146201_a(c, i);
      if (this.closeOnEsc && (i == 1 || (!GuiNpcTextField.isActive() && isInventoryKey(i))))
        close();
    }
  }

  public void onGuiClosed() {
    GuiNpcTextField.unfocus();
  }

  public final void close() {
    if (this.parent != null) {
      this.parent.closeSubGui(this);
    } else {
      displayGuiScreen((GuiScreen)null);
      this.mc.setIngameFocus();
    }
    save();
  }

  public void addButton(GuiNpcButton button) {
    this.buttons.put(Integer.valueOf(button.id), button);
    this.buttonList.add(button);
  }

  public GuiNpcButton getButton(int i) {
    return this.buttons.get(Integer.valueOf(i));
  }

  public void addTextField(GuiNpcTextField tf) {
    this.textfields.put(Integer.valueOf(tf.field_175208_g), tf);
  }

  public GuiNpcTextField getTextField(int i) {
    return this.textfields.get(Integer.valueOf(i));
  }

  public void addLabel(GuiNpcLabel label) {
    this.labels.put(Integer.valueOf(label.id), label);
  }

  public GuiNpcLabel getLabel(int i) {
    return this.labels.get(Integer.valueOf(i));
  }

  public void addSlider(GuiNpcSlider slider) {
    this.sliders.put(Integer.valueOf(slider.id), slider);
    this.buttonList.add(slider);
  }

  public GuiNpcSlider getSlider(int i) {
    return this.sliders.get(Integer.valueOf(i));
  }

  public void addScroll(GuiCustomScroll scroll) {
    scroll.setWorldAndResolution(this.mc, 350, 250);
    this.scrolls.put(Integer.valueOf(scroll.id), scroll);
  }

  public GuiCustomScroll getScroll(int id) {
    return this.scrolls.get(Integer.valueOf(id));
  }

  public abstract void save();

  @Override
  public void drawScreen(int i, int j, float f) {
    this.mouseX = i;
    this.mouseY = j;
    if (this.subgui == null || this.subgui.drawSubGuiBackground()) {
      if (this.drawDefaultBackground)
        drawDefaultBackground();
      if (this.background != null && this.mc.renderEngine  != null) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine .bindTexture(this.background);
        if (this.xSize > 256) {
          drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 250, this.ySize);
          drawTexturedModalRect(this.guiLeft + 250, this.guiTop, 256 - this.xSize - 250, 0, this.xSize - 250, this.ySize);
        } else {
          drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        }
      }
      for (GuiNpcLabel label : this.labels.values())
        label.drawLabel(this, this.fontRendererObj);
      for (GuiNpcTextField tf : this.textfields.values())
        tf.func_146194_f();
      for (GuiCustomScroll scroll : this.scrolls.values())
        scroll.drawScreen(i, j, f, hasSubGui() ? 0 : Mouse.getDWheel());
      for (GuiScreen gui : this.extra.values())
        gui.drawScreen(i, j, f);
      super.drawScreen(i, j, f);
    }
    if (this.subgui != null) {
      GlStateManager.translate(0.0F, 0.0F, 260.0F);
      this.subgui.drawScreen(i, j, f);
      GlStateManager.translate(0.0F, 0.0F, -260.0F);
    }
  }

  public boolean drawSubGuiBackground() {
    return true;
  }

  public FontRenderer getFontRenderer() {
    return this.fontRendererObj;
  }

  public void elementClicked() {
    if (this.subgui != null)
      this.subgui.elementClicked();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  public void doubleClicked() {}

  public boolean isInventoryKey(int i) {
    return (i == this.mc.gameSettings.keyBindInventory.getKeyCode());
  }

  @Override
  public void drawDefaultBackground() {
    super.drawDefaultBackground();
  }

  public void displayGuiScreen(GuiScreen gui) {
    this.mc.displayGuiScreen(gui);
  }

  public void setSubGui(GuiNPCInterface gui) {
    this.subgui = gui;
    this.subgui.parent = this;
    this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
    initGui();
  }

  public void closeSubGui(GuiNPCInterface gui) {
    this.subgui = null;
    if (this instanceof ISubGuiListener)
      ((ISubGuiListener)this).subGuiClosed(gui);
    initGui();
  }

  public boolean hasSubGui() {
    return (this.subgui != null);
  }

  public GuiNPCInterface getSubGui() {
    if (hasSubGui() && this.subgui.hasSubGui())
      return this.subgui.getSubGui();
    return this.subgui;
  }

  public void drawNpc(EntityLivingBase npc, int x, int y, float zoomed, int rotation) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate((this.guiLeft + x), (this.guiTop + y), 50.0F);
    float scale = 1.0F;
    if (npc.height  > 2.4D)
      scale = 2.0F / npc.height ;
    if (npc instanceof net.minecraft.entity.player.EntityPlayer);
    GlStateManager.scale(-60.0F * scale * zoomed, 60.0F * scale * zoomed, 60.0F * scale * zoomed);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    float f2 = npc.renderYawOffset;
    float f3 = npc.rotationYaw;
    float f4 = npc.rotationPitch;
    float f7 = npc.rotationYawHead;
    float f5 = (this.guiLeft + x) - this.mouseX;
    float f6 = (this.guiTop + y) - 100.0F * scale * zoomed - this.mouseY;
    GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-((float)Math.atan((f6 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    npc.renderYawOffset = rotation;
    npc.rotationYaw = (float)Math.atan((f5 / 80.0F)) * 40.0F + rotation;
    npc.rotationPitch = -((float)Math.atan((f6 / 40.0F))) * 20.0F;
    npc.rotationYawHead = npc.rotationYaw;
    (this.mc.getRenderManager()).playerViewY  = 180.0F;
    this.mc.getRenderManager().doRenderEntity((Entity)npc, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    npc.prevRenderYawOffset = npc.renderYawOffset = f2;
    npc.prevRotationYaw = npc.rotationYaw = f3;
    npc.prevRotationPitch = npc.rotationPitch = f4;
    npc.prevRotationYawHead = npc.rotationYawHead = f7;
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }

  public void openLink(String link) {
    try {
      Class<?> oclass = Class.forName("java.awt.Desktop");
      Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(link) });
    } catch (Throwable throwable) {}
  }
}
