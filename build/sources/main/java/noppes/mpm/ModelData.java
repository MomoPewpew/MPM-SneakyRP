package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.util.PixelmonHelper;

public class ModelData extends ModelDataShared implements ICapabilityProvider {
  public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);

  @CapabilityInject(ModelData.class)
  public static Capability<ModelData> MODELDATA_CAPABILITY = null;

  public boolean resourceInit = false;

  public boolean resourceLoaded = false;

  public boolean webapiActive = false;

  public boolean webapiInit = false;

  public Object textureObject = null;

  public ItemStack backItem = ItemStack.field_190927_a;

  public int inLove = 0;

  public int animationTime = -1;

  public EnumAnimation animation = EnumAnimation.NONE;

  public EnumAnimation prevAnimation = EnumAnimation.NONE;

  public int animationStart = 0;

  public short soundType = 0;

  public double prevPosX;

  public double prevPosY;

  public double prevPosZ;

  public EntityPlayer player = null;

  public long lastEdited = System.currentTimeMillis();

  public UUID analyticsUUID = UUID.randomUUID();

  public synchronized NBTTagCompound writeToNBT() {
    NBTTagCompound compound = super.writeToNBT();
    compound.setShort("SoundType", this.soundType);
    compound.setInteger("Animation", this.animation.ordinal());
    compound.setLong("LastEdited", this.lastEdited);
    return compound;
  }

  public synchronized void readFromNBT(NBTTagCompound compound) {
    String prevUrl = this.url;
    super.readFromNBT(compound);
    this.soundType = compound.getShort("SoundType");
    this.lastEdited = compound.getLong("LastEdited");
    if (this.player != null) {
      this.player.refreshDisplayName();
      if (this.entityClass == null) {
        this.player.getEntityData().removeTag("MPMModel");
      } else {
        this.player.getEntityData().setString("MPMModel", this.entityClass.getCanonicalName());
      }
    }
    setAnimation(compound.getInteger("Animation"));
    if (!prevUrl.equals(this.url)) {
      this.resourceInit = false;
      this.resourceLoaded = false;
    }
  }

  public void setAnimation(int i) {
    if (i < (EnumAnimation.values()).length) {
      this.animation = EnumAnimation.values()[i];
    } else {
      this.animation = EnumAnimation.NONE;
    }
    setAnimation(this.animation);
  }

  public void setAnimation(EnumAnimation ani) {
    this.animationTime = -1;
    this.animation = ani;
    this.lastEdited = System.currentTimeMillis();
    if (this.animation == EnumAnimation.WAVING)
      this.animationTime = 80;
    if (this.animation == EnumAnimation.YES || this.animation == EnumAnimation.NO)
      this.animationTime = 60;
    if (this.player == null || ani == EnumAnimation.NONE) {
      this.animationStart = -1;
    } else {
      this.animationStart = this.player.ticksExisted;
    }
  }

  public EntityLivingBase getEntity(EntityPlayer player) {
    if (this.entityClass == null)
      return null;
    if (this.entity == null)
      try {
        this.entity = this.entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { player.worldObj });
        if (PixelmonHelper.isPixelmon((Entity)this.entity) && player.worldObj.isRemote && !this.extra.hasKey("Name"))
          this.extra.setString("Name", "Abra");
        this.entity.func_70037_a(this.extra);
        this.entity.func_184224_h(true);
        this.entity.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(player.func_110138_aP());
        if (this.entity instanceof EntityLiving) {
          EntityLiving living = (EntityLiving)this.entity;
          living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.getHeldItemMainhand());
          living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, player.getHeldItemOffhand());
          living.setItemStackToSlot(EntityEquipmentSlot.HEAD, player.inventory.armorItemInSlot(3));
          living.setItemStackToSlot(EntityEquipmentSlot.CHEST, player.inventory.armorItemInSlot(2));
          living.setItemStackToSlot(EntityEquipmentSlot.LEGS, player.inventory.armorItemInSlot(1));
          living.setItemStackToSlot(EntityEquipmentSlot.FEET, player.inventory.armorItemInSlot(0));
        }
      } catch (Exception exception) {}
    return this.entity;
  }

  public ModelData copy() {
    ModelData data = new ModelData();
    data.readFromNBT(writeToNBT());
    data.resourceLoaded = this.resourceLoaded;
    data.player = this.player;
    return data;
  }

  public boolean isSleeping() {
    return isSleeping(this.animation);
  }

  private boolean isSleeping(EnumAnimation animation) {
    return (animation == EnumAnimation.SLEEPING_EAST || animation == EnumAnimation.SLEEPING_NORTH || animation == EnumAnimation.SLEEPING_SOUTH || animation == EnumAnimation.SLEEPING_WEST);
  }

  public boolean animationEquals(EnumAnimation animation2) {
    return (animation2 == this.animation || (isSleeping() && isSleeping(animation2)));
  }

  public float getOffsetCamera(EntityPlayer player) {
    if (!MorePlayerModels.EnablePOV)
      return 0.0F;
    float offset = -offsetY();
    if (this.animation == EnumAnimation.SITTING)
      offset += 0.5F - getLegsY();
    if (isSleeping())
      offset = 1.18F;
    if (this.animation == EnumAnimation.CRAWLING)
      offset = 0.8F;
    if (offset < -0.2F && isBlocked(player))
      offset = -0.2F;
    return offset;
  }

  private boolean isBlocked(EntityPlayer player) {
    return !player.worldObj.isAirBlock((new BlockPos((Entity)player)).up(2));
  }

  public void setExtra(EntityLivingBase entity, String key, String value) {
    key = key.toLowerCase();
    if (key.equals("breed") && EntityList.getEntityString((Entity)entity).equals("tgvstyle.Dog"))
      try {
        Method method = entity.getClass().getMethod("getBreedID", new Class[0]);
        Enum breed = (Enum)method.invoke(entity, new Object[0]);
        method = entity.getClass().getMethod("setBreedID", new Class[] { breed.getClass() });
        method.invoke(entity, new Object[] { ((Enum[])breed.getClass().getEnumConstants())[Integer.parseInt(value)] });
        NBTTagCompound comp = new NBTTagCompound();
        entity.writeEntityToNBT(comp);
        this.extra.setString("EntityData21", comp.getString("EntityData21"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    if (key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon((Entity)entity))
      this.extra.setString("Name", value);
    clearEntity();
  }

  public void save() {
    if (this.player == null)
      return;
    EntityPlayer player = this.player;
    saveExecutor.submit(() -> {
          try {
            String filename = player.getUniqueID().toString().toLowerCase();
            if (filename.isEmpty())
              filename = "noplayername";
            filename = filename + ".dat";
            File file = new File(MorePlayerModels.dir, filename + "_new");
            File file1 = new File(MorePlayerModels.dir, filename + "_old");
            File file2 = new File(MorePlayerModels.dir, filename);
            CompressedStreamTools.writeCompressed(writeToNBT(), new FileOutputStream(file));
            if (file1.exists())
              file1.delete();
            file2.renameTo(file1);
            if (file2.exists())
              file2.delete();
            file.renameTo(file2);
            if (file.exists())
              file.delete();
          } catch (Exception e) {
            LogWriter.except(e);
          }
        });
  }

  public static ModelData get(EntityPlayer player) {
    ModelData data = (ModelData)player.getCapability(MODELDATA_CAPABILITY, null);
    if (data.player == null) {
      data.player = player;
      NBTTagCompound compound = loadPlayerData(player.getUniqueID());
      if (compound != null)
        data.readFromNBT(compound);
    }
    return data;
  }

  private static NBTTagCompound loadPlayerData(UUID id) {
    String filename = id.toString();
    if (filename.isEmpty())
      filename = "noplayername";
    filename = filename + ".dat";
    try {
      File file = new File(MorePlayerModels.dir, filename);
      if (!file.exists())
        return null;
      return CompressedStreamTools.readCompressed(new FileInputStream(file));
    } catch (Exception e) {
      LogWriter.except(e);
      try {
        File file = new File(MorePlayerModels.dir, filename + "_old");
        if (!file.exists())
          return null;
        return CompressedStreamTools.readCompressed(new FileInputStream(file));
      } catch (Exception exception) {
        LogWriter.except(exception);
        return null;
      }
    }
  }
  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return (capability == MODELDATA_CAPABILITY);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (hasCapability(capability, facing))
      return (T)this;
    return null;
  }

  public void update() {}
}
