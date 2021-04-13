package noppes.mpm.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiModelColor extends GuiNPCInterface implements ITextfieldListener {
  private GuiScreen parent;

  private static final ResourceLocation colorPicker = new ResourceLocation("moreplayermodels:textures/gui/color.png");

  private static final ResourceLocation colorgui = new ResourceLocation("moreplayermodels:textures/gui/color_gui.png");

  private int colorX;

  private int colorY;

  private GuiNpcTextField textfield;

  public int color;

  private ColorCallback callback;

  public GuiModelColor(GuiScreen parent, int color, ColorCallback callback) {
    this.parent = parent;
    this.callback = callback;
    this.ySize = 230;
    this.closeOnEsc = false;
    this.background = colorgui;
    this.color = color;
  }

  public void initGui() {
    super.initGui();
    this.colorX = this.guiLeft + 4;
    this.colorY = this.guiTop + 50;
    addTextField(this.textfield = new GuiNpcTextField(0, (GuiScreen)this, this.guiLeft + 35, this.guiTop + 25, 60, 20, getColor()));
    addButton(new GuiNpcButton(66, this.guiLeft + 107, this.guiTop + 8, 20, 20, "X"));
    this.textfield.setTextColor(this.color);
  }

  protected void actionPerformed(GuiButton guibutton) {
    if (guibutton.id == 66)
      close();
  }

  public void keyTyped(char c, int i) {
    String prev = this.textfield.getText();
    super.keyTyped(c, i);
    String newText = this.textfield.getText();
    if (newText.equals(prev))
      return;
    try {
      this.color = Integer.parseInt(this.textfield.getText(), 16);
      this.callback.color(this.color);
      this.textfield.setTextColor(this.color);
    } catch (NumberFormatException e) {
      this.textfield.setText(prev);
    }
  }

  public void drawScreen(int par1, int par2, float par3) {
    super.drawScreen(par1, par2, par3);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(colorPicker);
    drawTexturedModalRect(this.colorX, this.colorY, 0, 0, 120, 120);
  }

  public void mouseClicked(int i, int j, int k) throws IOException {
    super.mouseClicked(i, j, k);
    if (i < this.colorX || i > this.colorX + 120 || j < this.colorY || j > this.colorY + 120)
      return;
    InputStream stream = null;
    try {
      IResource resource = this.mc.getResourceManager().getResource(colorPicker);
      BufferedImage bufferedimage = ImageIO.read(stream = resource.getInputStream());
      int color = bufferedimage.getRGB((i - this.guiLeft - 4) * 4, (j - this.guiTop - 50) * 4) & 0xFFFFFF;
      if (color != 0) {
        this.color = color;
        this.callback.color(color);
        this.textfield.setTextColor(color);
        this.textfield.setText(getColor());
      }
    } catch (IOException iOException) {

    } finally {
      if (stream != null)
        try {
          stream.close();
        } catch (IOException iOException) {}
    }
  }

  public void unFocused(GuiNpcTextField textfield) {
    try {
      this.color = Integer.parseInt(textfield.getText(), 16);
    } catch (NumberFormatException e) {
      this.color = 0;
    }
    this.callback.color(this.color);
    textfield.setTextColor(this.color);
  }

  public String getColor() {
    String str = Integer.toHexString(this.color);
    while (str.length() < 6)
      str = "0" + str;
    return str;
  }

  public void save() {}

  public static interface ColorCallback {
    void color(int param1Int);
  }
}
