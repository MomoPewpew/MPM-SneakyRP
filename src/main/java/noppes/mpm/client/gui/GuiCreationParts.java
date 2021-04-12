package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelEyeData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiColorButton;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener {
  private GuiCustomScroll scroll;

  private GuiPart[] parts = new GuiPart[] {
      (new GuiPart(EnumParts.EARS))
      .setTypes(new String[] { "gui.none", "gui.normal", "ears.bunny" }), new GuiPartHorns(), new GuiPartHair(), (new GuiPart(EnumParts.MOHAWK))

      .setTypes(new String[] { "gui.none", "1", "2" }).noPlayerOptions(), new GuiPartSnout(), new GuiPartBeard(), (new GuiPart(EnumParts.FIN))

      .setTypes(new String[] { "gui.none", "fin.shark", "fin.reptile" }), (new GuiPart(EnumParts.BREASTS))
      .setTypes(new String[] { "gui.none", "1", "2", "3" }).noPlayerOptions(), new GuiPartWings(), new GuiPartClaws(),
      (new GuiPart(EnumParts.SKIRT))

      .setTypes(new String[] { "gui.none", "gui.normal" }), new GuiPartLegs(), new GuiPartTail(), new GuiPartEyes(), new GuiPartParticles() };

  private static int selected = 0;

  public GuiCreationParts() {
    this.active = 2;
    Arrays.sort(this.parts, (o1, o2) -> {
          String s1 = I18n.func_74838_a("part." + o1.part.name);
          String s2 = I18n.func_74838_a("part." + o2.part.name);
          return s1.compareToIgnoreCase(s2);
        });
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    if (this.entity != null) {
      openGui(new GuiCreationExtra());
      return;
    }
    if (this.scroll == null) {
      List<String> list = new ArrayList<>();
      for (GuiPart part : this.parts)
        list.add(I18n.func_74838_a("part." + part.part.name));
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
      this.scroll.setUnsortedList(list);
    }
    this.scroll.guiLeft = this.guiLeft;
    this.scroll.guiTop = this.guiTop + 46;
    this.scroll.setSize(100, this.ySize - 50);
    addScroll(this.scroll);
    if (this.parts[selected] != null) {
      this.scroll.setSelected(I18n.func_74838_a("part." + (this.parts[selected]).part.name));
      this.parts[selected].initGui();
    }
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (this.parts[selected] != null)
      this.parts[selected].actionPerformed(btn);
  }

  public void unFocused(GuiNpcTextField textfield) {
    if (textfield.field_175208_g == 23);
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    if (scroll.selected >= 0) {
      selected = scroll.selected;
      func_73866_w_();
    }
  }

  class GuiPart {
    EnumParts part;

    private int paterns = 0;

    protected String[] types = new String[] { "gui.none" };

    protected ModelPartData data;

    protected boolean hasPlayerOption = true;

    protected boolean noPlayerTypes = false;

    protected boolean canBeDeleted = true;

    public GuiPart(EnumParts part) {
      this.part = part;
      this.data = GuiCreationParts.this.playerdata.getPartData(part);
    }

    public int initGui() {
      this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
      int y = GuiCreationParts.this.guiTop + 50;
      if (this.data == null || !this.data.playerTexture || !this.noPlayerTypes) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(20, GuiCreationParts.this.guiLeft + 145, y, 100, 20, this.types, (this.data == null) ? 0 : (this.data.type + 1)));
        y += 25;
      }
      if (this.data != null && this.hasPlayerOption) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(21, "gui.playerskin", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiNpcButtonYesNo(21, GuiCreationParts.this.guiLeft + 170, y, this.data.playerTexture));
        y += 25;
      }
      if (this.data != null && !this.data.playerTexture) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(23, "gui.color", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiColorButton(23, GuiCreationParts.this.guiLeft + 170, y, this.data.color));
        y += 25;
      }
      return y;
    }

    protected void actionPerformed(GuiButton btn) {
      if (btn.field_146127_k == 20) {
        int i = ((GuiNpcButton)btn).getValue();
        if (i == 0 && this.canBeDeleted) {
          GuiCreationParts.this.playerdata.removePart(this.part);
        } else {
          this.data = GuiCreationParts.this.playerdata.getOrCreatePart(this.part);
          this.data.pattern = 0;
          this.data.setType(i - 1);
        }
        GuiCreationParts.this.func_73866_w_();
      }
      if (btn.field_146127_k == 22)
        this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
      if (btn.field_146127_k == 21) {
        this.data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
        GuiCreationParts.this.func_73866_w_();
      }
      if (btn.field_146127_k == 23)
        GuiCreationParts.this.setSubGui(new GuiModelColor((GuiScreen)GuiCreationParts.this, this.data.color, color -> this.data.color = color));
    }

    public GuiPart noPlayerOptions() {
      this.hasPlayerOption = false;
      return this;
    }

    public GuiPart noPlayerTypes() {
      this.noPlayerTypes = true;
      return this;
    }

    public GuiPart setTypes(String[] types) {
      this.types = types;
      return this;
    }
  }

  class GuiPartParticles extends GuiPart {
    public GuiPartParticles() {
      super(EnumParts.PARTICLES);
      this.types = new String[] { "gui.none", "1", "2" };
    }

    public int initGui() {
      int y = super.initGui();
      if (this.data == null)
        return y;
      return y;
    }
  }

  class GuiPartTail extends GuiPart {
    public GuiPartTail() {
      super(EnumParts.TAIL);
      this.types = new String[] { "gui.none", "part.tail", "tail.dragon", "tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.bird", "tail.fox" };
    }

    public int initGui() {
      this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
      this.hasPlayerOption = (this.data != null && (this.data.type == 0 || this.data.type == 1 || this.data.type == 6 || this.data.type == 7));
      int y = super.initGui();
      if (this.data != null && this.data.type == 0) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "1", "2" }, this.data.pattern));
      }
      return y;
    }
  }

  class GuiPartHorns extends GuiPart {
    public GuiPartHorns() {
      super(EnumParts.HORNS);
      this.types = new String[] { "gui.none", "horns.bull", "horns.antlers", "horns.antenna" };
    }

    public int initGui() {
      int y = super.initGui();
      if (this.data != null && this.data.type == 2) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "1", "2" }, this.data.pattern));
      }
      return y;
    }
  }

  class GuiPartHair extends GuiPart {
    public GuiPartHair() {
      super(EnumParts.HAIR);
      this.types = new String[] { "gui.none", "1", "2", "3", "4" };
      noPlayerTypes();
    }
  }

  class GuiPartSnout extends GuiPart {
    public GuiPartSnout() {
      super(EnumParts.SNOUT);
      this.types = new String[] { "gui.none", "snout.small", "snout.medium", "snout.large", "snout.bunny", "snout.beak" };
    }
  }

  class GuiPartBeard extends GuiPart {
    public GuiPartBeard() {
      super(EnumParts.BEARD);
      this.types = new String[] { "gui.none", "1", "2", "3", "4" };
      noPlayerTypes();
    }
  }

  class GuiPartEyes extends GuiPart {
    private ModelEyeData eyes;

    public GuiPartEyes() {
      super(EnumParts.EYES);
      this.types = new String[] { "gui.none", "1", "2" };
      noPlayerOptions();
      this.canBeDeleted = false;
      this.eyes = (ModelEyeData)this.data;
    }

    public int initGui() {
      int y = super.initGui();
      if (this.data != null && this.eyes.isEnabled()) {
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "gui.both", "gui.left", "gui.right" }, this.data.pattern));
        GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.draw", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        y += 25;
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(37, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { I18n.func_74838_a("gui.down") + "x2", "gui.down", "gui.normal", "gui.up" }, this.eyes.eyePos + 1));
        GuiCreationParts.this.addLabel(new GuiNpcLabel(37, "gui.position", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        y += 25;
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiNpcButtonYesNo(34, GuiCreationParts.this.guiLeft + 145, y, this.eyes.glint));
        GuiCreationParts.this.addLabel(new GuiNpcLabel(34, "eye.glint", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        y += 25;
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiColorButton(35, GuiCreationParts.this.guiLeft + 170, y, this.eyes.browColor));
        GuiCreationParts.this.addLabel(new GuiNpcLabel(35, "eye.brow", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(38, GuiCreationParts.this.guiLeft + 225, y, 50, 20, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8" }, this.eyes.browThickness));
        y += 25;
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiColorButton(36, GuiCreationParts.this.guiLeft + 170, y, this.eyes.skinColor));
        GuiCreationParts.this.addLabel(new GuiNpcLabel(36, "eye.lid", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
      }
      return y;
    }

    protected void actionPerformed(GuiButton btn) {
      if (btn.field_146127_k == 34)
        this.eyes.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
      if (btn.field_146127_k == 35)
        GuiCreationParts.this.setSubGui(new GuiModelColor((GuiScreen)GuiCreationParts.this, this.eyes.browColor, color -> this.eyes.browColor = color));
      if (btn.field_146127_k == 36)
        GuiCreationParts.this.setSubGui(new GuiModelColor((GuiScreen)GuiCreationParts.this, this.eyes.skinColor, color -> this.eyes.skinColor = color));
      if (btn.field_146127_k == 37)
        this.eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
      if (btn.field_146127_k == 38)
        this.eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
      super.actionPerformed(btn);
    }
  }

  class GuiPartWings extends GuiPart {
    public GuiPartWings() {
      super(EnumParts.WINGS);
      setTypes(new String[] { "gui.none", "1", "2", "3", "4", "5" });
    }

    public int initGui() {
      int y = super.initGui();
      if (this.data == null)
        return y;
      GuiCreationParts.this.addLabel(new GuiNpcLabel(24, I18n.func_74838_a("part.wings") + "/" + I18n.func_74838_a("item.elytra.name"), GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
      GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(34, GuiCreationParts.this.guiLeft + 185, y, 100, 20, new String[] { "gui.both", "part.wings", "item.elytra.name" }, this.data.pattern));
      return y;
    }

    protected void actionPerformed(GuiButton btn) {
      if (btn.field_146127_k == 34)
        GuiCreationParts.this.playerdata.wingMode = ((GuiButtonBiDirectional)btn).getValue();
      super.actionPerformed(btn);
    }
  }

  class GuiPartClaws extends GuiPart {
    public GuiPartClaws() {
      super(EnumParts.CLAWS);
      this.types = new String[] { "gui.none", "gui.show" };
    }

    public int initGui() {
      int y = super.initGui();
      if (this.data == null)
        return y;
      GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
      GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "gui.both", "gui.left", "gui.right" }, this.data.pattern));
      return y;
    }
  }

  class GuiPartLegs extends GuiPart {
    public GuiPartLegs() {
      super(EnumParts.LEGS);
      this.types = new String[] { "gui.none", "gui.normal", "legs.naga", "legs.spider", "legs.horse", "legs.mermaid", "legs.digitigrade" };
      this.canBeDeleted = false;
    }

    public int initGui() {
      this.hasPlayerOption = (this.data.type == 1 || this.data.type == 5);
      int y = super.initGui();
      if (this.data.type == 4) {
        GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
        GuiCreationParts.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[] { "1", "2" }, this.data.pattern));
      }
      return y;
    }

    protected void actionPerformed(GuiButton btn) {
      if (btn.field_146127_k == 20) {
        int i = ((GuiNpcButton)btn).getValue();
        if (i <= 1) {
          this.data.playerTexture = true;
        } else {
          this.data.playerTexture = false;
        }
      }
      if (btn.field_146127_k == 22)
        this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
      super.actionPerformed(btn);
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
