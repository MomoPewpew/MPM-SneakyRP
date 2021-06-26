package noppes.mpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import noppes.mpm.client.Client;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.PixelmonHelper;

public class ModelData extends ModelDataShared implements ICapabilityProvider {
     public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);
     @CapabilityInject(ModelData.class)
     public static Capability MODELDATA_CAPABILITY = null;
     public boolean resourceInit = false;
     public boolean resourceLoaded = false;
     public boolean webapiActive = false;
     public boolean webapiInit = false;
     public Object textureObject = null;
     public ItemStack backItem;
     public int inLove;
     public int animationTime;
     public EnumAnimation animation;
     public EnumAnimation prevAnimation;
     public int animationStart;
     public short soundType;
     public double prevPosX;
     public double prevPosY;
     public double prevPosZ;
     public EntityPlayer player;
     public long lastEdited;
     public UUID analyticsUUID;
     public List<Prop> props;

     public ModelData() {
          this.backItem = null;
          this.inLove = 0;
          this.animationTime = -1;
          this.animation = EnumAnimation.NONE;
          this.prevAnimation = EnumAnimation.NONE;
          this.animationStart = 0;
          this.soundType = 0;
          this.player = null;
          this.lastEdited = System.currentTimeMillis();
          this.analyticsUUID = UUID.randomUUID();

          this.props = new ArrayList();
     }

     @Override
     public synchronized NBTTagCompound writeToNBT() {
          NBTTagCompound compound = super.writeToNBT();
          compound.setShort("SoundType", this.soundType);
          compound.setInteger("Animation", this.animation.ordinal());
          compound.setLong("LastEdited", this.lastEdited);
          compound = this.propsToNBT(compound);
          return compound;
     }

     @Override
     public synchronized void readFromNBT(NBTTagCompound compound) {
          String prevUrl = new String(this.url);
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

          this.setAnimation(compound.getInteger("Animation"));
          if (!prevUrl.equals(this.url)) {
               this.resourceInit = false;
               this.resourceLoaded = false;
          }

          this.propsFromNBT(compound);
     }

     public void setAnimation(int i) {
          if (i < EnumAnimation.values().length) {
               this.animation = EnumAnimation.values()[i];
          } else {
               this.animation = EnumAnimation.NONE;
          }

          this.setAnimation(this.animation);
     }

     public void setAnimation(EnumAnimation ani) {
          this.animationTime = -1;
          this.animation = ani;
          this.lastEdited = System.currentTimeMillis();
          if (this.animation == EnumAnimation.WAVING) {
               this.animationTime = 80;
          }

          if (this.animation == EnumAnimation.YES || this.animation == EnumAnimation.NO) {
               this.animationTime = 60;
          }

          if (this.player != null && ani != EnumAnimation.NONE) {
               this.animationStart = this.player.ticksExisted;
          } else {
               this.animationStart = -1;
          }

     }

     public EntityLivingBase getEntity(EntityPlayer player) {
          if (this.entityClass == null) {
               return null;
          } else {
               if (this.entity == null) {
                    try {
                         this.entity = (EntityLivingBase)this.entityClass.getConstructor(World.class).newInstance(player.worldObj);
                         if (PixelmonHelper.isPixelmon(this.entity) && player.worldObj.isRemote && !this.extra.hasKey("Name")) {
                              this.extra.setString("Name", "Abra");
                         }

                         this.entity.readEntityFromNBT(this.extra);
                         this.entity.setEntityInvulnerable(true);
                         this.entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)player.getMaxHealth());
                         if (this.entity instanceof EntityLiving) {
                              EntityLiving living = (EntityLiving)this.entity;
                              living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, player.getHeldItemMainhand());
                              living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, player.getHeldItemOffhand());
                              living.setItemStackToSlot(EntityEquipmentSlot.HEAD, player.inventory.armorItemInSlot(3));
                              living.setItemStackToSlot(EntityEquipmentSlot.CHEST, player.inventory.armorItemInSlot(2));
                              living.setItemStackToSlot(EntityEquipmentSlot.LEGS, player.inventory.armorItemInSlot(1));
                              living.setItemStackToSlot(EntityEquipmentSlot.FEET, player.inventory.armorItemInSlot(0));
                         }
                    } catch (Exception var3) {
                    }
               }

               return this.entity;
          }
     }

     public ModelData copy() {
          ModelData data = new ModelData();
          data.readFromNBT(this.writeToNBT());
          data.resourceLoaded = this.resourceLoaded;
          data.player = this.player;
          return data;
     }

     public boolean isSleeping() {
          return this.isSleeping(this.animation);
     }

     private boolean isSleeping(EnumAnimation animation) {
          return animation == EnumAnimation.SLEEPING_EAST || animation == EnumAnimation.SLEEPING_NORTH || animation == EnumAnimation.SLEEPING_SOUTH || animation == EnumAnimation.SLEEPING_WEST;
     }

     public boolean animationEquals(EnumAnimation animation2) {
          return animation2 == this.animation || this.isSleeping() && this.isSleeping(animation2);
     }

     public float getOffsetCamera(EntityPlayer player) {
          if (!MorePlayerModels.EnablePOV) {
               return 0.0F;
          } else {
               float offset = -this.offsetY();
               if (this.animation == EnumAnimation.SITTING) {
                    offset += 0.5F - this.getLegsY();
               }

               if (this.isSleeping()) {
                    offset = 1.18F;
               }

               if (this.animation == EnumAnimation.CRAWLING) {
                    offset = 0.8F;
               }

               if (offset < -0.2F && this.isBlocked(player)) {
                    offset = -0.2F;
               }

               return offset;
          }
     }

     private boolean isBlocked(EntityPlayer player) {
          return !player.worldObj.isAirBlock((new BlockPos(player)).up(2));
     }

     public void setExtra(EntityLivingBase entity, String key, String value) {
          key = key.toLowerCase();
          if (key.equals("breed") && EntityList.getEntityString(entity).equals("tgvstyle.Dog")) {
               try {
                    Method method = entity.getClass().getMethod("getBreedID");
                    Enum breed = (Enum)method.invoke(entity);
                    method = entity.getClass().getMethod("setBreedID", breed.getClass());
                    method.invoke(entity, ((Enum[])breed.getClass().getEnumConstants())[Integer.parseInt(value)]);
                    NBTTagCompound comp = new NBTTagCompound();
                    entity.writeEntityToNBT(comp);
                    this.extra.setString("EntityData21", comp.getString("EntityData21"));
               } catch (Exception var7) {
                    var7.printStackTrace();
               }
          }

          if (key.equalsIgnoreCase("name") && PixelmonHelper.isPixelmon(entity)) {
               this.extra.setString("Name", value);
          }

          this.clearEntity();
     }

     public void save() {
          if (this.player != null) {
               EntityPlayer player = this.player;
               saveExecutor.submit(() -> {
                    try {
                         String filename = player.getUniqueID().toString().toLowerCase();
                         if (filename.isEmpty()) {
                              filename = "noplayername";
                         }

                         filename = filename + ".dat";
                         File file = new File(MorePlayerModels.dir, filename + "_new");
                         File file1 = new File(MorePlayerModels.dir, filename + "_old");
                         File file2 = new File(MorePlayerModels.dir, filename);
                         CompressedStreamTools.writeCompressed(this.writeToNBT(), new FileOutputStream(file));
                         if (file1.exists()) {
                              file1.delete();
                         }

                         file2.renameTo(file1);
                         if (file2.exists()) {
                              file2.delete();
                         }

                         file.renameTo(file2);
                         if (file.exists()) {
                              file.delete();
                         }
                    } catch (Exception var6) {
                         LogWriter.except(var6);
                    }

               });
          }
     }

     public static ModelData get(EntityPlayer player) {
          ModelData data = (ModelData)player.getCapability(MODELDATA_CAPABILITY, (EnumFacing)null);
          if (data.player == null) {
               data.player = player;
               NBTTagCompound compound = loadPlayerData(player.getUniqueID());
               if (compound != null) {
                    data.readFromNBT(compound);
               }
          }

          return data;
     }

     private static NBTTagCompound loadPlayerData(UUID id) {
          String filename = id.toString();
          if (filename.isEmpty()) {
               filename = "noplayername";
          }

          filename = filename + ".dat";

          File file;
          try {
               file = new File(MorePlayerModels.dir, filename);
               return !file.exists() ? null : CompressedStreamTools.readCompressed(new FileInputStream(file));
          } catch (Exception var4) {
               LogWriter.except(var4);

               try {
                    file = new File(MorePlayerModels.dir, filename + "_old");
                    return !file.exists() ? null : CompressedStreamTools.readCompressed(new FileInputStream(file));
               } catch (Exception var3) {
                    LogWriter.except(var3);
                    return null;
               }
          }
     }

     @Override
     public boolean hasCapability(Capability capability, EnumFacing facing) {
          return capability == MODELDATA_CAPABILITY;
     }

     @Override
     public Object getCapability(Capability capability, EnumFacing facing) {
          return this.hasCapability(capability, facing) ? this : null;
     }

     public void update() {
     }

     public void addPropClient(String propString, String bodyPartName,
		Float propScaleX, Float propScaleY, Float propScaleZ,
		Float propOffsetX, Float propOffsetY, Float propOffsetZ,
		Float propRotateX, Float propRotateY, Float propRotateZ,
		Boolean propMatchScaling, Boolean hide
		) {
    	 Prop prop = new Prop(propString, null, bodyPartName,
    			 propScaleX, propScaleY, propScaleZ,
    			 propOffsetX, propOffsetY, propOffsetZ,
    			 propRotateX, propRotateY, propRotateZ,
    			 propMatchScaling, hide);

 		Client.sendData(EnumPackets.PROP_ADD, prop.writeToNBT());
     }

     public void addPropServer(String propString, ItemStack propItemStack, String bodyPartName,
		Float propScaleX, Float propScaleY, Float propScaleZ,
		Float propOffsetX, Float propOffsetY, Float propOffsetZ,
		Float propRotateX, Float propRotateY, Float propRotateZ,
		Boolean propMatchScaling, Boolean hide
		) {
    	 Prop prop = new Prop(propString, propItemStack, bodyPartName,
    			 propScaleX, propScaleY, propScaleZ,
    			 propOffsetX, propOffsetY, propOffsetZ,
    			 propRotateX, propRotateY, propRotateZ,
    			 propMatchScaling, hide);

    	 this.props.add(prop);
    	 Server.sendAssociatedData(this.player, EnumPackets.PROP_ADD, this.player.getUniqueID(), prop.writeToNBT());
     }

     public void clearPropsClient() {
  		Client.sendData(EnumPackets.PROP_CLEAR);
 	}

     public void clearPropsServer() {
    	this.props.clear();
    	Server.sendAssociatedData(this.player, EnumPackets.PROP_CLEAR, this.player.getUniqueID());
 	}

     public void removePropClient(Integer index) {
   		Client.sendData(EnumPackets.PROP_REMOVE, index);
     }

     public void removePropServer(Integer index) {
    	 this.props.remove(index);
     	Server.sendAssociatedData(this.player, EnumPackets.PROP_REMOVE, this.player.getUniqueID(), index);
     }

     public void propSyncClient() {
        NBTTagCompound compound = new NBTTagCompound();
 		Client.sendData(EnumPackets.PROP_SYNC, this.propsToNBT(compound));
     }

     public void propSyncServer() {
         NBTTagCompound compound = new NBTTagCompound();
    	Server.sendAssociatedData(this.player, EnumPackets.PROP_SYNC, this.player.getUniqueID(), this.propsToNBT(compound));
     }

     public NBTTagCompound propsToNBT(NBTTagCompound compound) {
  		for (int i = 0; i < this.props.size(); i++) {
	    	 compound.setTag(("prop" + String.valueOf(i)), this.props.get(i).writeToNBT());
		}

		return compound;
     }

     public void propsFromNBT(NBTTagCompound compound) {
         List<Prop> propsTemp = new ArrayList<Prop>();

   		for (int i = 0; i < Integer.MAX_VALUE; i++) {
  			 NBTTagCompound tag = compound.getCompoundTag("prop" + String.valueOf(i));
  			 Prop prop = new Prop();
	    	 prop.readFromNBT(tag);

	    	 if (prop.propString != "") {
	    		 propsTemp.add(prop);
	    	 } else {
				 break;
	    	 }
		}

         this.props = new ArrayList(propsTemp);
     }

     public void hidePropServer (Integer index) {
    	 this.props.get(index).hide = true;
     	 Server.sendAssociatedData(this.player, EnumPackets.PROP_HIDE, this.player.getUniqueID(), index);
     }
}
