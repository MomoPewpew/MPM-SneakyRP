package noppes.mpm.client.gui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.client.EntityFakeLiving;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.util.PixelmonHelper;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener {
  private final String[] ignoredTags = new String[] { "CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced" };

  private final String[] booleanTags = new String[0];

  private GuiCustomScroll scroll;

  private Map<String, GuiType> data = new HashMap<>();

  private GuiType selected;

  public GuiCreationExtra() {
    this.active = 2;
  }

  public void func_73866_w_() {
    super.func_73866_w_();
    if (this.entity == null) {
      openGui(new GuiCreationParts());
      return;
    }
    if (this.scroll == null) {
      this.data = getData(this.entity);
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
      List<String> list = new ArrayList<>(this.data.keySet());
      this.scroll.setList(list);
      if (list.isEmpty())
        return;
      this.scroll.setSelected(list.get(0));
    }
    this.selected = this.data.get(this.scroll.getSelected());
    if (this.selected == null)
      return;
    this.scroll.guiLeft = this.guiLeft;
    this.scroll.guiTop = this.guiTop + 46;
    this.scroll.setSize(100, this.ySize - 74);
    addScroll(this.scroll);
    this.selected.initGui();
  }

  public Map<String, GuiType> getData(EntityLivingBase entity) {
    Map<String, GuiType> data = new HashMap<>();
    NBTTagCompound compound = getExtras(entity);
    Set<String> keys = compound.func_150296_c();
    for (String name : keys) {
      if (isIgnored(name))
        continue;
      NBTBase base = compound.func_74781_a(name);
      if (name.equals("Age")) {
        data.put("Child", new GuiTypeBoolean("Child", entity.func_70631_g_()));
        continue;
      }
      if (name.equals("Color") && base.func_74732_a() == 1) {
        data.put("Color", new GuiTypeByte("Color", compound.func_74771_c("Color")));
        continue;
      }
      if (base.func_74732_a() == 1) {
        byte b = ((NBTTagByte)base).func_150290_f();
        if (b != 0 && b != 1)
          continue;
        if (this.playerdata.extra.func_74764_b(name))
          b = this.playerdata.extra.func_74771_c(name);
        data.put(name, new GuiTypeBoolean(name, (b == 1)));
      }
    }
    if (PixelmonHelper.isPixelmon((Entity)entity))
      data.put("Model", new GuiTypePixelmon("Model"));
    if (EntityList.func_75621_b((Entity)entity).equals("tgvstyle.Dog"))
      data.put("Breed", new GuiTypeDoggyStyle("Breed"));
    return data;
  }

  private boolean isIgnored(String tag) {
    for (String s : this.ignoredTags) {
      if (s.equals(tag))
        return true;
    }
    return false;
  }

  private NBTTagCompound getExtras(EntityLivingBase entity) {
    NBTTagCompound fake = new NBTTagCompound();
    (new EntityFakeLiving(entity.field_70170_p)).func_70014_b(fake);
    NBTTagCompound compound = new NBTTagCompound();
    try {
      entity.func_70014_b(compound);
    } catch (Throwable throwable) {}
    Set<String> keys = fake.func_150296_c();
    for (String name : keys)
      compound.func_82580_o(name);
    return compound;
  }

  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    if (scroll.id == 0) {
      func_73866_w_();
    } else if (this.selected != null) {
      this.selected.scrollClicked(i, j, k, scroll);
    }
  }

  protected void func_146284_a(GuiButton btn) {
    super.func_146284_a(btn);
    if (this.selected != null)
      this.selected.actionPerformed(btn);
  }

  abstract class GuiType {
    public String name;

    public GuiType(String name) {
      this.name = name;
    }

    public void initGui() {}

    public void actionPerformed(GuiButton button) {}

    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {}
  }

  class GuiTypeBoolean extends GuiType {
    private boolean bo;

    public GuiTypeBoolean(String name, boolean bo) {
      super(name);
      this.bo = bo;
    }

    public void initGui() {
      GuiCreationExtra.this.addButton((GuiNpcButton)new GuiNpcButtonYesNo(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 50, 60, 20, this.bo));
    }

    public void actionPerformed(GuiButton button) {
      if (button.field_146127_k != 11)
        return;
      this.bo = ((GuiNpcButtonYesNo)button).getBoolean();
      if (this.name.equals("Child")) {
        GuiCreationExtra.this.playerdata.extra.func_74768_a("Age", this.bo ? -24000 : 0);
        GuiCreationExtra.this.playerdata.clearEntity();
      } else {
        GuiCreationExtra.this.playerdata.extra.func_74757_a(this.name, this.bo);
        GuiCreationExtra.this.playerdata.clearEntity();
      }
    }
  }

  class GuiTypeByte extends GuiType {
    private byte b;

    public GuiTypeByte(String name, byte b) {
      super(name);
      this.b = b;
    }

    public void initGui() {
      GuiCreationExtra.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[] {
              "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
              "10", "11", "12", "13", "14", "15" }, this.b));
    }

    public void actionPerformed(GuiButton button) {
      if (button.field_146127_k != 11)
        return;
      GuiCreationExtra.this.playerdata.extra.func_74774_a(this.name, (byte)((GuiNpcButton)button).getValue());
      GuiCreationExtra.this.playerdata.clearEntity();
    }
  }

  class GuiTypePixelmon extends GuiType {
    public GuiTypePixelmon(String name) {
      super(name);
    }

    public void initGui() {
      GuiCustomScroll scroll = new GuiCustomScroll((GuiScreen)GuiCreationExtra.this, 1);
      scroll.setSize(120, 200);
      scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
      scroll.guiTop = GuiCreationExtra.this.guiTop + 50;
      GuiCreationExtra.this.addScroll(scroll);
      scroll.setList(PixelmonHelper.getPixelmonList());
      scroll.setSelected(PixelmonHelper.getName(GuiCreationExtra.this.entity));
    }

    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      String name = scroll.getSelected();
      GuiCreationExtra.this.playerdata.clearEntity();
      GuiCreationExtra.this.playerdata.extra.func_74778_a("Name", name);
    }
  }

  class GuiTypeDoggyStyle extends GuiType {
    public GuiTypeDoggyStyle(String name) {
      super(name);
    }

    public void initGui() {
      Enum breed = null;
      try {
        Method method = GuiCreationExtra.this.entity.getClass().getMethod("getBreedID", new Class[0]);
        breed = (Enum)method.invoke(GuiCreationExtra.this.entity, new Object[0]);
      } catch (Exception exception) {}
      GuiCreationExtra.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[] {
              "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
              "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
              "20", "21", "22", "23", "24", "25", "26" }, breed.ordinal()));
    }

    public void actionPerformed(GuiButton button) {
      if (button.field_146127_k != 11)
        return;
      int breed = ((GuiNpcButton)button).getValue();
      EntityLivingBase entity = GuiCreationExtra.this.playerdata.getEntity((EntityPlayer)GuiCreationExtra.this.field_146297_k.field_71439_g);
      GuiCreationExtra.this.playerdata.setExtra(entity, "breed", ((GuiNpcButton)button).getValue() + "");
      GuiCreationExtra.this.playerdata.clearEntity();
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
