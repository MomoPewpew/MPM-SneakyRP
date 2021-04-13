package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiNpcTextField extends GuiTextField {
  public boolean enabled = true;

  public boolean inMenu = true;

  public boolean numbersOnly = false;

  private ITextfieldListener listener;

  public int id;

  public int min = 0;

  public int max = Integer.MAX_VALUE;

  public int def = 0;

  private static GuiNpcTextField activeTextfield = null;

  private final int[] allowedSpecialChars = new int[] { 14, 211, 203, 205 };

  public GuiNpcTextField(int id, GuiScreen parent, int i, int j, int k, int l, String s) {
    super(id, (Minecraft.getMinecraft()).fontRendererObj, i, j, k, l);
    setMaxStringLength(500);
    setText(s);
    this.id = id;
    if (parent instanceof ITextfieldListener)
      this.listener = (ITextfieldListener)parent;
  }


  public static boolean isActive() {
    return (activeTextfield != null);
  }

  private boolean charAllowed(char c, int i) {
    if (!this.numbersOnly || Character.isDigit(c))
      return true;
    for (int j : this.allowedSpecialChars) {
      if (j == i)
        return true;
    }
    return false;
  }

  @Override
  public boolean textboxKeyTyped(char c, int i) {
    if (!charAllowed(c, i))
      return false;
    return super.textboxKeyTyped(c, i);
  }

  public boolean isEmpty() {
    return (getText().trim().length() == 0);
  }

  public int getInteger() {
    return Integer.parseInt(getText());
  }

  public boolean isInteger() {
    try {
      Integer.parseInt(getText());
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  public boolean mouseClicked(int i, int j, int k) {
    boolean wasFocused = isFocused();
    boolean clicked = super.mouseClicked(i, j, k);
    if (wasFocused != isFocused() &&
      wasFocused)
      unFocused();
    if (isFocused())
      activeTextfield = this;
    return clicked;
  }

  public void unFocused() {
    if (this.numbersOnly)
      if (isEmpty() || !isInteger()) {
        setText(this.def + "");
      } else if (getInteger() < this.min) {
        setText(this.min + "");
      } else if (getInteger() > this.max) {
        setText(this.max + "");
      }
    if (this.listener != null)
      this.listener.unFocused(this);
    if (this == activeTextfield)
      activeTextfield = null;
  }

  @Override
  public void drawTextBox() {
    if (this.enabled)
      super.drawTextBox();
  }

  public void setMinMaxDefault(int i, int j, int k) {
    this.min = i;
    this.max = j;
    this.def = k;
  }

  public static void unfocus() {
    if (activeTextfield != null)
      activeTextfield.unFocused();
    activeTextfield = null;
  }
}
