package noppes.mpm;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.constants.EnumParts;

public class ModelDataShared {
  public ModelPartConfig arm1 = new ModelPartConfig();

  public ModelPartConfig arm2 = new ModelPartConfig();

  public ModelPartConfig body = new ModelPartConfig();

  public ModelPartConfig leg1 = new ModelPartConfig();

  public ModelPartConfig leg2 = new ModelPartConfig();

  public ModelPartConfig head = new ModelPartConfig();

  protected ModelPartData legParts = new ModelPartData("legs");

  public ModelEyeData eyes = new ModelEyeData();

  public Class<? extends EntityLivingBase> entityClass;

  protected EntityLivingBase entity;

  public NBTTagCompound extra = new NBTTagCompound();

  protected HashMap<EnumParts, ModelPartData> parts = new HashMap<>();

  public int wingMode = 0;

  public String url = "";

  public String displayName = "";

  public String displayFormat = "";

  public NBTTagCompound writeToNBT() {
    NBTTagCompound compound = new NBTTagCompound();
    if (this.entityClass != null)
      compound.func_74778_a("EntityClass", this.entityClass.getCanonicalName());
    compound.func_74782_a("ArmsConfig", (NBTBase)this.arm1.writeToNBT());
    compound.func_74782_a("BodyConfig", (NBTBase)this.body.writeToNBT());
    compound.func_74782_a("LegsConfig", (NBTBase)this.leg1.writeToNBT());
    compound.func_74782_a("HeadConfig", (NBTBase)this.head.writeToNBT());
    compound.func_74782_a("LegParts", (NBTBase)this.legParts.writeToNBT());
    compound.func_74782_a("Eyes", (NBTBase)this.eyes.writeToNBT());
    compound.func_74757_a("EyesEnabled", this.eyes.isEnabled());
    compound.func_74782_a("ExtraData", (NBTBase)this.extra);
    compound.func_74768_a("WingMode", this.wingMode);
    compound.func_74778_a("CustomSkinUrl", this.url);
    compound.func_74778_a("DisplayName", this.displayName);
    compound.func_74778_a("DisplayDisplayFormat", this.displayFormat);
    NBTTagList list = new NBTTagList();
    for (EnumParts e : this.parts.keySet()) {
      NBTTagCompound item = ((ModelPartData)this.parts.get(e)).writeToNBT();
      item.func_74778_a("PartName", e.name);
      list.func_74742_a((NBTBase)item);
    }
    compound.func_74782_a("Parts", (NBTBase)list);
    return compound;
  }

  public void readFromNBT(NBTTagCompound compound) {
    setEntityClass(compound.func_74779_i("EntityClass"));
    this.arm1.readFromNBT(compound.func_74775_l("ArmsConfig"));
    this.body.readFromNBT(compound.func_74775_l("BodyConfig"));
    this.leg1.readFromNBT(compound.func_74775_l("LegsConfig"));
    this.head.readFromNBT(compound.func_74775_l("HeadConfig"));
    this.legParts.readFromNBT(compound.func_74775_l("LegParts"));
    if (compound.func_74764_b("Eyes"))
      this.eyes.readFromNBT(compound.func_74775_l("Eyes"));
    this.extra = compound.func_74775_l("ExtraData");
    this.wingMode = compound.func_74762_e("WingMode");
    this.url = compound.func_74779_i("CustomSkinUrl");
    this.displayName = compound.func_74779_i("DisplayName");
    this.displayFormat = compound.func_74779_i("DisplayDisplayFormat");
    HashMap<EnumParts, ModelPartData> parts = new HashMap<>();
    NBTTagList list = compound.func_150295_c("Parts", 10);
    for (int i = 0; i < list.func_74745_c(); i++) {
      NBTTagCompound item = list.func_150305_b(i);
      String name = item.func_74779_i("PartName");
      ModelPartData part = new ModelPartData(name);
      part.readFromNBT(item);
      EnumParts e = EnumParts.FromName(name);
      if (e != null)
        parts.put(e, part);
    }
    this.parts = parts;
    updateTransate();
  }

  private void updateTransate() {
    for (EnumParts part : EnumParts.values()) {
      ModelPartConfig config = getPartConfig(part);
      if (config != null)
        if (part == EnumParts.HEAD) {
          config.setTranslate(0.0F, getBodyY(), 0.0F);
        } else if (part == EnumParts.ARM_LEFT) {
          ModelPartConfig body = getPartConfig(EnumParts.BODY);
          float x = (1.0F - body.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
          float y = getBodyY() + (1.0F - config.scaleY) * -0.1F;
          config.setTranslate(-x, y, 0.0F);
          if (!config.notShared) {
            ModelPartConfig arm = getPartConfig(EnumParts.ARM_RIGHT);
            arm.copyValues(config);
          }
        } else if (part == EnumParts.ARM_RIGHT) {
          ModelPartConfig body = getPartConfig(EnumParts.BODY);
          float x = (1.0F - body.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
          float y = getBodyY() + (1.0F - config.scaleY) * -0.1F;
          config.setTranslate(x, y, 0.0F);
        } else if (part == EnumParts.LEG_LEFT) {
          config.setTranslate(config.scaleX * 0.125F - 0.113F, getLegsY(), 0.0F);
          if (!config.notShared) {
            ModelPartConfig leg = getPartConfig(EnumParts.LEG_RIGHT);
            leg.copyValues(config);
          }
        } else if (part == EnumParts.LEG_RIGHT) {
          config.setTranslate((1.0F - config.scaleX) * 0.125F, getLegsY(), 0.0F);
        } else if (part == EnumParts.BODY) {
          config.setTranslate(0.0F, getBodyY(), 0.0F);
        }
    }
  }

  private void setEntityClass(String string) {
    this.entityClass = null;
    this.entity = null;
    for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
      try {
        Class<? extends Entity> c = ent.getEntityClass();
        if (c.getCanonicalName().equals(string) && EntityLivingBase.class.isAssignableFrom(c)) {
          this.entityClass = c.asSubclass(EntityLivingBase.class);
          break;
        }
      } catch (Exception exception) {}
    }
  }

  public void setEntityClass(Class<? extends EntityLivingBase> entityClass) {
    this.entityClass = entityClass;
    this.entity = null;
    this.extra = new NBTTagCompound();
  }

  public Class<? extends EntityLivingBase> getEntityClass() {
    return this.entityClass;
  }

  public float offsetY() {
    if (this.entity == null)
      return -getBodyY();
    return this.entity.field_70131_O - 1.8F;
  }

  public void clearEntity() {
    this.entity = null;
  }

  public ModelPartData getPartData(EnumParts type) {
    if (type == EnumParts.LEGS)
      return this.legParts;
    if (type == EnumParts.EYES)
      return this.eyes;
    return this.parts.get(type);
  }

  public ModelPartConfig getPartConfig(EnumParts type) {
    if (type == EnumParts.BODY)
      return this.body;
    if (type == EnumParts.ARM_LEFT)
      return this.arm1;
    if (type == EnumParts.ARM_RIGHT)
      return this.arm2;
    if (type == EnumParts.LEG_LEFT)
      return this.leg1;
    if (type == EnumParts.LEG_RIGHT)
      return this.leg2;
    return this.head;
  }

  public void removePart(EnumParts type) {
    this.parts.remove(type);
  }

  public ModelPartData getOrCreatePart(EnumParts type) {
    if (type == null)
      return null;
    if (type == EnumParts.EYES)
      return this.eyes;
    ModelPartData part = getPartData(type);
    if (part == null)
      this.parts.put(type, part = new ModelPartData(type.name));
    return part;
  }

  public float getBodyY() {
    return (1.0F - this.body.scaleY) * 0.75F + getLegsY();
  }

  public float getLegsY() {
    ModelPartConfig legs = this.leg1;
    if (this.leg2.notShared && this.leg2.scaleY > this.leg1.scaleY)
      legs = this.leg2;
    return (1.0F - legs.scaleY) * 0.75F;
  }
}
