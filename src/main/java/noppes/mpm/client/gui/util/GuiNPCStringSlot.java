package noppes.mpm.client.gui.util;

import java.util.HashSet;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

public class GuiNPCStringSlot extends GuiSlot {
  private Vector<String> list;

  public String selected;

  public HashSet<String> selectedList;

  private boolean multiSelect;

  private GuiScreen parent;

  private GuiListActionListener listener;

  public int size;

  private long prevTime;

  public GuiNPCStringSlot(Vector<String> list, GuiScreen parent, boolean multiSelect, int size) {
    super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 64, size);
    this.prevTime = 0L;
    this.selectedList = new HashSet<>();
    this.parent = parent;
    this.list = list;
    this.multiSelect = multiSelect;
    this.size = size;
    if (parent instanceof GuiListActionListener)
      this.listener = (GuiListActionListener)parent;
  }

  @Override
  protected int getSize() {
    return this.list.size();
  }

  @Override
  protected void elementClicked(int i, boolean flag, int var3, int var4) {
    long time = System.currentTimeMillis();
    if (this.listener != null && this.selected != null && this.selected.equals(this.list.get(i)) && time - this.prevTime < 400L)
      this.listener.doubleClicked();
    this.selected = this.list.get(i);
    if (this.selectedList.contains(this.selected)) {
      this.selectedList.remove(this.selected);
    } else {
      this.selectedList.add(this.selected);
    }
    if (this.listener != null)
      this.listener.elementClicked();
    this.prevTime = time;
  }

  @Override
  protected boolean isSelected(int i) {
    if (!this.multiSelect) {
      if (this.selected == null)
        return false;
      return this.selected.equals(this.list.get(i));
    }
    return this.selectedList.contains(this.list.get(i));
  }

  @Override
  protected int getContentHeight() {
    return this.list.size() * this.size;
  }

  @Override
  protected void drawBackground() {
    this.parent.drawDefaultBackground();
  }

  public void clear() {
    this.list.clear();
  }

  public void setList(Vector<String> list) {
    this.list = list;
  }

  protected void func_192637_a(int i, int j, int k, int p_180791_4_, int p_180791_5_, int p_180791_6_, float partialTicks) {
    if (i >= this.list.size())
      return;
    String s = this.list.get(i);
    this.parent.func_73731_b((Minecraft.getMinecraft()).fontRendererObj, s, j + 50, k + 3, 16777215);
  }
}
