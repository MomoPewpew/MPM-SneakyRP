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
      compound.setString("EntityClass", this.entityClass.getCanonicalName());
    compound.setTag("ArmsConfig", (NBTBase)this.arm1.writeToNBT());
    compound.setTag("BodyConfig", (NBTBase)this.body.writeToNBT());
    compound.setTag("LegsConfig", (NBTBase)this.leg1.writeToNBT());
    compound.setTag("HeadConfig", (NBTBase)this.head.writeToNBT());
    compound.setTag("LegParts", (NBTBase)this.legParts.writeToNBT());
    compound.setTag("Eyes", (NBTBase)this.eyes.writeToNBT());
    compound.setBoolean("EyesEnabled", this.eyes.isEnabled());
    compound.setTag("ExtraData", (NBTBase)this.extra);
    compound.setInteger("WingMode", this.wingMode);
    compound.setString("CustomSkinUrl", this.url);
    compound.setString("DisplayName", this.displayName);
    compound.setString("DisplayDisplayFormat", this.displayFormat);
    NBTTagList list = new NBTTagList();
    for (EnumParts e : this.parts.keySet()) {
      NBTTagCompound item = ((ModelPartData)this.parts.get(e)).writeToNBT();
      item.setString("PartName", e.name);
      list.appendTag((NBTBase)item);
    }
    compound.setTag("Parts", (NBTBase)list);
    return compound;
  }

  public void readFromNBT(NBTTagCompound compound) {
    setEntityClass(compound.getString("EntityClass"));
    this.arm1.readFromNBT(compound.getCompoundTag("ArmsConfig"));
    this.body.readFromNBT(compound.getCompoundTag("BodyConfig"));
    this.leg1.readFromNBT(compound.getCompoundTag("LegsConfig"));
    this.head.readFromNBT(compound.getCompoundTag("HeadConfig"));
    this.legParts.readFromNBT(compound.getCompoundTag("LegParts"));
    if (compound.hasKey("Eyes"))
      this.eyes.readFromNBT(compound.getCompoundTag("Eyes"));
    this.extra = compound.getCompoundTag("ExtraData");
    this.wingMode = compound.getInteger("WingMode");
    this.url = compound.getString("CustomSkinUrl");
    this.displayName = compound.getString("DisplayName");
    this.displayFormat = compound.getString("DisplayDisplayFormat");
    HashMap<EnumParts, ModelPartData> parts = new HashMap<>();
    NBTTagList list = compound.getTagList("Parts", 10);
    for (int i = 0; i < list.tagCount(); i++) {
      NBTTagCompound item = list.getCompoundTagAt(i);
      String name = item.getString("PartName");
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
    return this.entity.height - 1.8F;
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
