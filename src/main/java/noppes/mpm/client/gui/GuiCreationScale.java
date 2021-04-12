package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationScale extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener {
  private GuiCustomScroll scroll;

  private List<EnumParts> data = new ArrayList<>();

  private static EnumParts selected = EnumParts.HEAD;

  public GuiCreationScale() {
    this.active = 3;
    this.xOffset = 140;
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    if (this.scroll == null)
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
    ArrayList<String> list = new ArrayList();
    EnumParts[] parts = { EnumParts.HEAD, EnumParts.BODY, EnumParts.ARM_LEFT, EnumParts.ARM_RIGHT, EnumParts.LEG_LEFT, EnumParts.LEG_RIGHT };
    this.data.clear();
    for (EnumParts part : parts) {
      if (part == EnumParts.ARM_RIGHT) {
        ModelPartConfig modelPartConfig = this.playerdata.getPartConfig(EnumParts.ARM_LEFT);
        if (!modelPartConfig.notShared)
          continue;
      }
      if (part == EnumParts.LEG_RIGHT) {
        ModelPartConfig modelPartConfig = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
        if (!modelPartConfig.notShared)
          continue;
      }
      this.data.add(part);
      list.add(I18n.func_74838_a("part." + part.name));
      continue;
    }
    this.scroll.setUnsortedList(list);
    this.scroll.setSelected(I18n.func_74838_a("part." + selected.name));
    this.scroll.guiLeft = this.guiLeft;
    this.scroll.guiTop = this.guiTop + 46;
    this.scroll.setSize(100, this.ySize - 74);
    addScroll(this.scroll);
    ModelPartConfig config = this.playerdata.getPartConfig(selected);
    int y = this.guiTop + 65;
    addLabel(new GuiNpcLabel(10, "scale.width", this.guiLeft + 102, y + 5, 16777215));
    addSlider(new GuiNpcSlider((GuiScreen)this, 10, this.guiLeft + 150, y, 100, 20, config.scaleX - 0.5F));
    y += 22;
    addLabel(new GuiNpcLabel(11, "scale.height", this.guiLeft + 102, y + 5, 16777215));
    addSlider(new GuiNpcSlider((GuiScreen)this, 11, this.guiLeft + 150, y, 100, 20, config.scaleY - 0.5F));
    y += 22;
    addLabel(new GuiNpcLabel(12, "scale.depth", this.guiLeft + 102, y + 5, 16777215));
    addSlider(new GuiNpcSlider((GuiScreen)this, 12, this.guiLeft + 150, y, 100, 20, config.scaleZ - 0.5F));
    if (selected == EnumParts.ARM_LEFT || selected == EnumParts.LEG_LEFT) {
      y += 22;
      addLabel(new GuiNpcLabel(13, "scale.shared", this.guiLeft + 102, y + 5, 16777215));
      addButton(new GuiNpcButton(13, this.guiLeft + 150, y, 50, 20, new String[] { "gui.no", "gui.yes" }, config.notShared ? 0 : 1));
    }
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (btn.field_146127_k == 13) {
      boolean bo = (((GuiNpcButton)btn).getValue() == 0);
      (this.playerdata.getPartConfig(selected)).notShared = bo;
      func_73866_w_();
    }
  }

  public void mouseDragged(GuiNpcSlider slider) {
    super.mouseDragged(slider);
    if (slider.field_146127_k >= 10 && slider.field_146127_k <= 12) {
      int percent = (int)(50.0F + slider.sliderValue * 100.0F);
      slider.setString(percent + "%");
      ModelPartConfig config = this.playerdata.getPartConfig(selected);
      if (slider.field_146127_k == 10)
        config.scaleX = slider.sliderValue + 0.5F;
      if (slider.field_146127_k == 11)
        config.scaleY = slider.sliderValue + 0.5F;
      if (slider.field_146127_k == 12)
        config.scaleZ = slider.sliderValue + 0.5F;
      updateTransate();
    }
  }

  private void updateTransate() {
    for (EnumParts part : EnumParts.values()) {
      ModelPartConfig config = this.playerdata.getPartConfig(part);
      if (config != null)
        if (part == EnumParts.HEAD) {
          config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
        } else if (part == EnumParts.ARM_LEFT) {
          ModelPartConfig body = this.playerdata.getPartConfig(EnumParts.BODY);
          float x = (1.0F - body.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
          float y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
          config.setTranslate(-x, y, 0.0F);
          if (!config.notShared) {
            ModelPartConfig arm = this.playerdata.getPartConfig(EnumParts.ARM_RIGHT);
            arm.copyValues(config);
          }
        } else if (part == EnumParts.ARM_RIGHT) {
          ModelPartConfig body = this.playerdata.getPartConfig(EnumParts.BODY);
          float x = (1.0F - body.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
          float y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
          config.setTranslate(x, y, 0.0F);
        } else if (part == EnumParts.LEG_LEFT) {
          config.setTranslate(config.scaleX * 0.125F - 0.113F, this.playerdata.getLegsY(), 0.0F);
          if (!config.notShared) {
            ModelPartConfig leg = this.playerdata.getPartConfig(EnumParts.LEG_RIGHT);
            leg.copyValues(config);
          }
        } else if (part == EnumParts.LEG_RIGHT) {
          config.setTranslate((1.0F - config.scaleX) * 0.125F, this.playerdata.getLegsY(), 0.0F);
        } else if (part == EnumParts.BODY) {
          config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
        }
    }
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    if (scroll.selected >= 0) {
      selected = this.data.get(scroll.selected);
      func_73866_w_();
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
