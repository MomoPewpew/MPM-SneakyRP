package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiNpcTextField extends GuiTextField {
  public boolean enabled = true;

  public boolean inMenu = true;

  public boolean numbersOnly = false;

  private ITextfieldListener listener;

  public int field_175208_g;

  public int min = 0;

  public int max = Integer.MAX_VALUE;

  public int def = 0;

  private static GuiNpcTextField activeTextfield = null;

  private final int[] allowedSpecialChars = new int[] { 14, 211, 203, 205 };

  public GuiNpcTextField(int id, GuiScreen parent, int i, int j, int k, int l, String s) {
    super(id, (Minecraft.func_71410_x()).field_71466_p, i, j, k, l);
    func_146203_f(500);
    func_146180_a(s);
    this.field_175208_g = id;
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

  public boolean func_146201_a(char c, int i) {
    if (!charAllowed(c, i))
      return false;
    return super.func_146201_a(c, i);
  }

  public boolean isEmpty() {
    return (func_146179_b().trim().length() == 0);
  }

  public int getInteger() {
    return Integer.parseInt(func_146179_b());
  }

  public boolean isInteger() {
    try {
      Integer.parseInt(func_146179_b());
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public boolean func_146192_a(int i, int j, int k) {
    boolean wasFocused = func_146206_l();
    boolean clicked = super.func_146192_a(i, j, k);
    if (wasFocused != func_146206_l() &&
      wasFocused)
      unFocused();
    if (func_146206_l())
      activeTextfield = this;
    return clicked;
  }

  public void unFocused() {
    if (this.numbersOnly)
      if (isEmpty() || !isInteger()) {
        func_146180_a(this.def + "");
      } else if (getInteger() < this.min) {
        func_146180_a(this.min + "");
      } else if (getInteger() > this.max) {
        func_146180_a(this.max + "");
      }
    if (this.listener != null)
      this.listener.unFocused(this);
    if (this == activeTextfield)
      activeTextfield = null;
  }

  public void func_146194_f() {
    if (this.enabled)
      super.func_146194_f();
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
