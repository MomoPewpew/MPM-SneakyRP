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
    compound.func_74777_a("SoundType", this.soundType);
    compound.func_74768_a("Animation", this.animation.ordinal());
    compound.func_74772_a("LastEdited", this.lastEdited);
    return compound;
  }

  public synchronized void readFromNBT(NBTTagCompound compound) {
    String prevUrl = this.url;
    super.readFromNBT(compound);
    this.soundType = compound.func_74765_d("SoundType");
    this.lastEdited = compound.func_74763_f("LastEdited");
    if (this.player != null) {
      this.player.refreshDisplayName();
      if (this.entityClass == null) {
        this.player.getEntityData().func_82580_o("MPMModel");
      } else {
        this.player.getEntityData().func_74778_a("MPMModel", this.entityClass.getCanonicalName());
      }
    }
    setAnimation(compound.func_74762_e("Animation"));
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
      this.animationStart = this.player.field_70173_aa;
    }
  }

  public EntityLivingBase getEntity(EntityPlayer player) {
    if (this.entityClass == null)
      return null;
    if (this.entity == null)
      try {
        this.entity = this.entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { player.field_70170_p });
        if (PixelmonHelper.isPixelmon((Entity)this.entity) && player.field_70170_p.field_72995_K && !this.extra.func_74764_b("Name"))
          this.extra.func_74778_a("Name", "Abra");
        this.entity.func_70037_a(this.extra);
        this.entity.func_184224_h(true);
        this.entity.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(player.func_110138_aP());
        if (this.entity instanceof EntityLiving) {
          EntityLiving living = (EntityLiving)this.entity;
          living.func_184201_a(EntityEquipmentSlot.MAINHAND, player.func_184614_ca());
          living.func_184201_a(EntityEquipmentSlot.OFFHAND, player.func_184592_cb());
          living.func_184201_a(EntityEquipmentSlot.HEAD, player.field_71071_by.func_70440_f(3));
          living.func_184201_a(EntityEquipmentSlot.CHEST, player.field_71071_by.func_70440_f(2));
          living.func_184201_a(EntityEquipmentSlot.LEGS, player.field_71071_by.func_70440_f(1));
          living.func_184201_a(EntityEquipmentSlot.FEET, player.field_71071_by.func_70440_f(0));
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
    return !player.field_70170_p.func_175623_d((new BlockPos((Entity)player)).func_177981_b(2));
  }

  public void setExtra(EntityLivingBase entity, String key, String value) {
    key = key.toLowerCase();
    if (key.equals("breed") && EntityList.func_75621_b((Entity)entity).equals("tgvstyle.Dog"))
      try {
        Method method = entity.getClass().getMethod("getBreedID", new Class[0]);
        Enum breed = (Enum)method.invoke(entity, new Object[0]);
        method = entity.getClass().getMethod("setBreedID", new Class[] { breed.getClass() });
        method.invoke(entity, new Object[] { ((Enum[])breed.getClass().getEnumConstants())[Integer.parseInt(value)] });
        NBTTagCompound comp = new NBTTagCompound();
        entity.func_70014_b(comp);
        this.extra.func_74778_a("EntityData21", comp.func_74779_i("EntityData21"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    if (key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon((Entity)entity))
      this.extra.func_74778_a("Name", value);
    clearEntity();
  }

  public void save() {
    if (this.player == null)
      return;
    EntityPlayer player = this.player;
    saveExecutor.submit(() -> {
          try {
            String filename = player.func_110124_au().toString().toLowerCase();
            if (filename.isEmpty())
              filename = "noplayername";
            filename = filename + ".dat";
            File file = new File(MorePlayerModels.dir, filename + "_new");
            File file1 = new File(MorePlayerModels.dir, filename + "_old");
            File file2 = new File(MorePlayerModels.dir, filename);
            CompressedStreamTools.func_74799_a(writeToNBT(), new FileOutputStream(file));
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
      NBTTagCompound compound = loadPlayerData(player.func_110124_au());
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
      return CompressedStreamTools.func_74796_a(new FileInputStream(file));
    } catch (Exception e) {
      LogWriter.except(e);
      try {
        File file = new File(MorePlayerModels.dir, filename + "_old");
        if (!file.exists())
          return null;
        return CompressedStreamTools.func_74796_a(new FileInputStream(file));
      } catch (Exception exception) {
        LogWriter.except(exception);
        return null;
      }
    }
  }

  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return (capability == MODELDATA_CAPABILITY);
  }

  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (hasCapability(capability, facing))
      return (T)this;
    return null;
  }

  public void update() {}
}
