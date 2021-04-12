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

  @Override
  public void initGui() {
    super.initGui();
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
    Set<String> keys = compound.getKeySet();
    for (String name : keys) {
      if (isIgnored(name))
        continue;
      NBTBase base = compound.getTag(name);
      if (name.equals("Age")) {
        data.put("Child", new GuiTypeBoolean("Child", entity.isChild()));
        continue;
      }
      if (name.equals("Color") && base.getId() == 1) {
        data.put("Color", new GuiTypeByte("Color", compound.getByte("Color")));
        continue;
      }
      if (base.getId() == 1) {
        byte b = ((NBTTagByte)base).getByte();
        if (b != 0 && b != 1)
          continue;
        if (this.playerdata.extra.hasKey(name))
          b = this.playerdata.extra.getByte(name);
        data.put(name, new GuiTypeBoolean(name, (b == 1)));
      }
    }
    if (PixelmonHelper.isPixelmon((Entity)entity))
      data.put("Model", new GuiTypePixelmon("Model"));
    if (EntityList.getEntityString((Entity)entity).equals("tgvstyle.Dog"))
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
    (new EntityFakeLiving(entity.worldObj)).writeEntityToNBT(fake);
    NBTTagCompound compound = new NBTTagCompound();
    try {
      entity.writeEntityToNBT(compound);
    } catch (Throwable throwable) {}
    Set<String> keys = fake.getKeySet();
    for (String name : keys)
      compound.removeTag(name);
    return compound;
  }

  @Override
  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    if (scroll.id == 0) {
      initGui();
    } else if (this.selected != null) {
      this.selected.scrollClicked(i, j, k, scroll);
    }
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    super.actionPerformed(btn);
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

    @Override
    public void initGui() {
      GuiCreationExtra.this.addButton((GuiNpcButton)new GuiNpcButtonYesNo(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 50, 60, 20, this.bo));
    }

    @Override
    public void actionPerformed(GuiButton button) {
      if (button.id != 11)
        return;
      this.bo = ((GuiNpcButtonYesNo)button).getBoolean();
      if (this.name.equals("Child")) {
        GuiCreationExtra.this.playerdata.extra.setInteger("Age", this.bo ? -24000 : 0);
        GuiCreationExtra.this.playerdata.clearEntity();
      } else {
        GuiCreationExtra.this.playerdata.extra.setBoolean(this.name, this.bo);
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

    @Override
    public void initGui() {
      GuiCreationExtra.this.addButton((GuiNpcButton)new GuiButtonBiDirectional(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[] {
              "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
              "10", "11", "12", "13", "14", "15" }, this.b));
    }

    @Override
    public void actionPerformed(GuiButton button) {
      if (button.id != 11)
        return;
      GuiCreationExtra.this.playerdata.extra.setByte(this.name, (byte)((GuiNpcButton)button).getValue());
      GuiCreationExtra.this.playerdata.clearEntity();
    }
  }

  class GuiTypePixelmon extends GuiType {
    public GuiTypePixelmon(String name) {
      super(name);
    }

    @Override
    public void initGui() {
      GuiCustomScroll scroll = new GuiCustomScroll((GuiScreen)GuiCreationExtra.this, 1);
      scroll.setSize(120, 200);
      scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
      scroll.guiTop = GuiCreationExtra.this.guiTop + 50;
      GuiCreationExtra.this.addScroll(scroll);
      scroll.setList(PixelmonHelper.getPixelmonList());
      scroll.setSelected(PixelmonHelper.getName(GuiCreationExtra.this.entity));
    }

    @Override
    public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
      String name = scroll.getSelected();
      GuiCreationExtra.this.playerdata.clearEntity();
      GuiCreationExtra.this.playerdata.extra.setString("Name", name);
    }
  }

  class GuiTypeDoggyStyle extends GuiType {
    public GuiTypeDoggyStyle(String name) {
      super(name);
    }

    @Override
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

    @Override
    public void actionPerformed(GuiButton button) {
      if (button.id != 11)
        return;
      int breed = ((GuiNpcButton)button).getValue();
      EntityLivingBase entity = GuiCreationExtra.this.playerdata.getEntity((EntityPlayer)GuiCreationExtra.this.mc.thePlayer);
      GuiCreationExtra.this.playerdata.setExtra(entity, "breed", ((GuiNpcButton)button).getValue() + "");
      GuiCreationExtra.this.playerdata.clearEntity();
    }
  }

  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
