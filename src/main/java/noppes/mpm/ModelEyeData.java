package noppes.mpm;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.constants.EnumPackets;

public class ModelEyeData extends ModelPartData {
  private Random r = new Random();

  public boolean glint = true;

  public int browThickness = 4;

  public int eyePos = 1;

  public int skinColor = 11830381;

  public int browColor = 5982516;

  public long blinkStart = 0L;

  public ModelEyeData() {
    super("eyes");
    this.type = -1;
    (new Integer[23])[0] = Integer.valueOf(8368696);
    (new Integer[23])[1] = Integer.valueOf(16247203);
    (new Integer[23])[2] = Integer.valueOf(10526975);
    (new Integer[23])[3] = Integer.valueOf(10987431);
    (new Integer[23])[4] = Integer.valueOf(10791096);
    (new Integer[23])[5] = Integer.valueOf(4210943);
    (new Integer[23])[6] = Integer.valueOf(14188339);
    (new Integer[23])[7] = Integer.valueOf(11685080);
    (new Integer[23])[8] = Integer.valueOf(6724056);
    (new Integer[23])[9] = Integer.valueOf(15066419);
    (new Integer[23])[10] =
      Integer.valueOf(8375321);
    (new Integer[23])[11] = Integer.valueOf(15892389);
    (new Integer[23])[12] = Integer.valueOf(10066329);
    (new Integer[23])[13] = Integer.valueOf(5013401);
    (new Integer[23])[14] = Integer.valueOf(8339378);
    (new Integer[23])[15] = Integer.valueOf(3361970);
    (new Integer[23])[16] = Integer.valueOf(6704179);
    (new Integer[23])[17] = Integer.valueOf(6717235);
    (new Integer[23])[18] = Integer.valueOf(10040115);
    (new Integer[23])[19] = Integer.valueOf(16445005);
    (new Integer[23])[20] = Integer.valueOf(6085589);
    (new Integer[23])[21] = Integer.valueOf(4882687);
    (new Integer[23])[22] = Integer.valueOf(55610);
    this.color = (new Integer[23])[this.r.nextInt(23)].intValue();
  }

  public NBTTagCompound writeToNBT() {
    NBTTagCompound compound = super.writeToNBT();
    compound.setBoolean("Glint", this.glint);
    compound.setInteger("SkinColor", this.skinColor);
    compound.setInteger("BrowColor", this.browColor);
    compound.setInteger("PositionY", this.eyePos);
    compound.setInteger("BrowThickness", this.browThickness);
    return compound;
  }

  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.glint = compound.getBoolean("Glint");
    this.skinColor = compound.getInteger("SkinColor");
    this.browColor = compound.getInteger("BrowColor");
    this.eyePos = compound.getInteger("PositionY");
    this.browThickness = compound.getInteger("BrowThickness");
  }

  public boolean isEnabled() {
    return (this.type >= 0);
  }

  public void update(EntityPlayer player) {
    if (!isEnabled() || !player.isEntityAlive())
      return;
    if (this.blinkStart < 0L) {
      this.blinkStart++;
    } else if (this.blinkStart == 0L) {
      if (this.r.nextInt(140) == 1) {
        this.blinkStart = System.currentTimeMillis();
        if (player != null && player.isServerWorld())
          Server.sendAssociatedData((Entity)player, EnumPackets.EYE_BLINK, new Object[] { player.getUniqueID() });
      }
    } else if (System.currentTimeMillis() - this.blinkStart > 300L) {
      this.blinkStart = -20L;
    }
  }
}
