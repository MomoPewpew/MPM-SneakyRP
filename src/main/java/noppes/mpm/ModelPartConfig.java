package noppes.mpm;

import net.minecraft.nbt.NBTTagCompound;

public class ModelPartConfig {
  public float scaleX = 1.0F, scaleY = 1.0F, scaleZ = 1.0F;

  public float transX = 0.0F;

  public float transY = 0.0F;

  public float transZ = 0.0F;

  public boolean notShared = false;

  public NBTTagCompound writeToNBT() {
    NBTTagCompound compound = new NBTTagCompound();
    compound.func_74776_a("ScaleX", this.scaleX);
    compound.func_74776_a("ScaleY", this.scaleY);
    compound.func_74776_a("ScaleZ", this.scaleZ);
    compound.func_74776_a("TransX", this.transX);
    compound.func_74776_a("TransY", this.transY);
    compound.func_74776_a("TransZ", this.transZ);
    compound.func_74757_a("NotShared", this.notShared);
    return compound;
  }

  public void readFromNBT(NBTTagCompound compound) {
    this.scaleX = checkValue(compound.func_74760_g("ScaleX"), 0.5F, 1.5F);
    this.scaleY = checkValue(compound.func_74760_g("ScaleY"), 0.5F, 1.5F);
    this.scaleZ = checkValue(compound.func_74760_g("ScaleZ"), 0.5F, 1.5F);
    this.transX = checkValue(compound.func_74760_g("TransX"), -1.0F, 1.0F);
    this.transY = checkValue(compound.func_74760_g("TransY"), -1.0F, 1.0F);
    this.transZ = checkValue(compound.func_74760_g("TransZ"), -1.0F, 1.0F);
    this.notShared = compound.func_74767_n("NotShared");
  }

  public String toString() {
    return "ScaleX: " + this.scaleX + " - ScaleY: " + this.scaleY + " - ScaleZ: " + this.scaleZ;
  }

  public void setScale(float x, float y, float z) {
    this.scaleX = x;
    this.scaleY = y;
    this.scaleZ = z;
  }

  public void setScale(float x, float y) {
    this.scaleZ = this.scaleX = x;
    this.scaleY = y;
  }

  public float checkValue(float given, float min, float max) {
    if (given < min)
      return min;
    if (given > max)
      return max;
    return given;
  }

  public void setTranslate(float transX, float transY, float transZ) {
    this.transX = transX;
    this.transY = transY;
    this.transZ = transZ;
  }

  public void copyValues(ModelPartConfig config) {
    this.scaleX = config.scaleX;
    this.scaleY = config.scaleY;
    this.scaleZ = config.scaleZ;
    this.transX = config.transX;
    this.transY = config.transY;
    this.transZ = config.transZ;
  }
}
