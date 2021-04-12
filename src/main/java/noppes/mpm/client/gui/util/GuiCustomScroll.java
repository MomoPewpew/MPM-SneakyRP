package noppes.mpm.client.gui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.util.NaturalOrderComparator;
import org.lwjgl.input.Mouse;

public class GuiCustomScroll extends GuiScreen {
  public static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/misc.png");

  private List<String> list;

  public int id;

  public int guiLeft = 0;

  public int guiTop = 0;

  private int xSize;

  private int ySize;

  public int selected;

  private HashSet<String> selectedList;

  private int hover;

  private int listHeight;

  private int scrollY;

  private int maxScrollY;

  private int scrollHeight;

  private boolean isScrolling;

  private boolean multipleSelection = false;

  private ICustomScrollListener listener;

  private boolean isSorted = true;

  public boolean visible = true;

  private boolean selectable = true;

  private int lastClickedItem;

  private long lastClickedTime = 0L;

  public GuiCustomScroll(GuiScreen parent, int id) {
    this.field_146294_l = 176;
    this.field_146295_m = 166;
    this.xSize = 176;
    this.ySize = 159;
    this.selected = -1;
    this.hover = -1;
    this.selectedList = new HashSet<>();
    this.listHeight = 0;
    this.scrollY = 0;
    this.scrollHeight = 0;
    this.isScrolling = false;
    if (parent instanceof ICustomScrollListener)
      this.listener = (ICustomScrollListener)parent;
    this.list = new ArrayList<>();
    this.id = id;
  }

  public GuiCustomScroll(GuiScreen parent, int id, boolean multipleSelection) {
    this(parent, id);
    this.multipleSelection = multipleSelection;
  }

  public void setSize(int x, int y) {
    this.ySize = y;
    this.xSize = x;
    this.listHeight = 14 * this.list.size();
    if (this.listHeight > 0) {
      this.scrollHeight = (int)((this.ySize - 8) / this.listHeight * (this.ySize - 8));
    } else {
      this.scrollHeight = Integer.MAX_VALUE;
    }
    this.maxScrollY = this.listHeight - this.ySize - 8 - 1;
  }

  public void drawScreen(int i, int j, float f, int mouseScrolled) {
    if (!this.visible)
      return;
    func_73733_a(this.guiLeft, this.guiTop, this.xSize + this.guiLeft, this.ySize + this.guiTop, -1072689136, -804253680);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    this.field_146297_k.field_71446_o.func_110577_a(resource);
    if (this.scrollHeight < this.ySize - 8)
      drawScrollBar();
    GlStateManager.func_179094_E();
    GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.func_179121_F();
    GlStateManager.func_179094_E();
    GlStateManager.func_179109_b(this.guiLeft, this.guiTop, 0.0F);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    if (this.selectable)
      this.hover = getMouseOver(i, j);
    drawItems();
    GlStateManager.func_179121_F();
    if (this.scrollHeight < this.ySize - 8) {
      i -= this.guiLeft;
      j -= this.guiTop;
      if (Mouse.isButtonDown(0)) {
        if (i >= this.xSize - 11 && i < this.xSize - 6 && j >= 4 && j < this.ySize)
          this.isScrolling = true;
      } else {
        this.isScrolling = false;
      }
      if (this.isScrolling) {
        this.scrollY = (j - 8) * this.listHeight / (this.ySize - 8) - this.scrollHeight;
        if (this.scrollY < 0)
          this.scrollY = 0;
        if (this.scrollY > this.maxScrollY)
          this.scrollY = this.maxScrollY;
      }
      if (mouseScrolled != 0) {
        this.scrollY += (mouseScrolled > 0) ? -14 : 14;
        if (this.scrollY > this.maxScrollY)
          this.scrollY = this.maxScrollY;
        if (this.scrollY < 0)
          this.scrollY = 0;
      }
    }
  }

  public boolean mouseInOption(int i, int j, int k) {
    int l = 4;
    int i1 = 14 * k + 4 - this.scrollY;
    return (i >= l - 1 && i < l + this.xSize - 11 && j >= i1 - 1 && j < i1 + 8);
  }

  protected void drawItems() {
    for (int i = 0; i < this.list.size(); i++) {
      int j = 4;
      int k = 14 * i + 4 - this.scrollY;
      if (k >= 4 && k + 12 < this.ySize) {
        int xOffset = (this.scrollHeight < this.ySize - 8) ? 0 : 10;
        String displayString = I18n.func_74838_a(this.list.get(i));
        String text = "";
        float maxWidth = (this.xSize + xOffset - 8) * 0.8F;
        if (this.field_146289_q.func_78256_a(displayString) > maxWidth) {
          for (int h = 0; h < displayString.length(); h++) {
            char c = displayString.charAt(h);
            text = text + c;
            if (this.field_146289_q.func_78256_a(text) > maxWidth)
              break;
          }
          if (displayString.length() > text.length())
            text = text + "...";
        } else {
          text = displayString;
        }
        if ((this.multipleSelection && this.selectedList.contains(text)) || (!this.multipleSelection && this.selected == i)) {
          func_73728_b(j - 2, k - 4, k + 10, -1);
          func_73728_b(j + this.xSize - 18 + xOffset, k - 4, k + 10, -1);
          func_73730_a(j - 2, j + this.xSize - 18 + xOffset, k - 3, -1);
          func_73730_a(j - 2, j + this.xSize - 18 + xOffset, k + 10, -1);
          this.field_146289_q.func_78276_b(text, j, k, 16777215);
        } else if (i == this.hover) {
          this.field_146289_q.func_78276_b(text, j, k, 65280);
        } else {
          this.field_146289_q.func_78276_b(text, j, k, 16777215);
        }
      }
    }
  }

  public String getSelected() {
    if (this.selected == -1 || this.selected >= this.list.size())
      return null;
    return this.list.get(this.selected);
  }

  private int getMouseOver(int i, int j) {
    i -= this.guiLeft;
    j -= this.guiTop;
    if (i >= 4 && i < this.xSize - 4 && j >= 4 && j < this.ySize)
      for (int j1 = 0; j1 < this.list.size(); ) {
        if (!mouseInOption(i, j, j1)) {
          j1++;
          continue;
        }
        return j1;
      }
    return -1;
  }

  public void func_73864_a(int i, int j, int k) {
    if (k != 0 || this.hover < 0)
      return;
    if (this.multipleSelection) {
      if (this.selectedList.contains(this.list.get(this.hover))) {
        this.selectedList.remove(this.list.get(this.hover));
      } else {
        this.selectedList.add(this.list.get(this.hover));
      }
    } else {
      if (this.hover >= 0)
        this.selected = this.hover;
      this.hover = -1;
    }
    if (this.listener != null) {
      long time = System.currentTimeMillis();
      this.listener.scrollClicked(i, j, k, this);
      if (this.selected >= 0 && this.selected == this.lastClickedItem && time - this.lastClickedTime < 500L)
        this.listener.scrollDoubleClicked(this.list.get(this.selected), this);
      this.lastClickedTime = time;
      this.lastClickedItem = this.selected;
    }
  }

  private void drawScrollBar() {
    int i = this.guiLeft + this.xSize - 9;
    int j = this.guiTop + (int)(this.scrollY / this.listHeight * (this.ySize - 8)) + 4;
    int k = j;
    func_73729_b(i, k, this.xSize, 9, 5, 1);
    for (; ++k < j + this.scrollHeight - 1; k++)
      func_73729_b(i, k, this.xSize, 10, 5, 1);
    func_73729_b(i, k, this.xSize, 11, 5, 1);
  }

  public boolean hasSelected() {
    return (this.selected >= 0);
  }

  public void setList(List<String> list) {
    if (isSameList(list))
      return;
    this.isSorted = true;
    this.scrollY = 0;
    Collections.sort(list, (Comparator<? super String>)new NaturalOrderComparator());
    this.list = list;
    setSize(this.xSize, this.ySize);
  }

  public void setUnsortedList(List<String> list) {
    if (isSameList(list))
      return;
    this.isSorted = false;
    this.scrollY = 0;
    this.list = list;
    setSize(this.xSize, this.ySize);
  }

  private boolean isSameList(List<String> list) {
    if (this.list.size() != list.size())
      return false;
    for (String s : this.list) {
      if (!list.contains(s))
        return false;
    }
    return true;
  }

  public void replace(String old, String name) {
    String select = getSelected();
    this.list.remove(old);
    this.list.add(name);
    if (this.isSorted)
      Collections.sort(this.list, (Comparator<? super String>)new NaturalOrderComparator());
    if (old.equals(select))
      select = name;
    this.selected = this.list.indexOf(select);
    setSize(this.xSize, this.ySize);
  }

  public void setSelected(String name) {
    this.selected = this.list.indexOf(name);
  }

  public void clear() {
    this.list = new ArrayList<>();
    this.selected = -1;
    this.scrollY = 0;
    setSize(this.xSize, this.ySize);
  }

  public List<String> getList() {
    return this.list;
  }

  public HashSet<String> getSelectedList() {
    return this.selectedList;
  }

  public void setSelectedList(HashSet<String> selectedList) {
    this.selectedList = selectedList;
  }

  public GuiCustomScroll setUnselectable() {
    this.selectable = false;
    return this;
  }

  public void scrollTo(String name) {
    int i = this.list.indexOf(name);
    if (i < 0 || this.scrollHeight >= this.ySize - 8)
      return;
    int pos = (int)(1.0F * i / this.list.size() * this.listHeight);
    if (pos > this.maxScrollY)
      pos = this.maxScrollY;
    this.scrollY = pos;
  }

  public boolean isMouseOver(int x, int y) {
    return (x >= this.guiLeft && x <= this.guiLeft + this.xSize && y >= this.guiTop && y <= this.guiTop + this.ySize);
  }
}
