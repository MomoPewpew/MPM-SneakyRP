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

import net.minecraft.client.Minecraft;
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
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

import noppes.mpm.client.Client;
import noppes.mpm.client.gui.GuiCreationSkinLoad;
import noppes.mpm.client.gui.util.GuiNPCInterface;
// import noppes.mpm.client.model.ModelAccessor;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.PixelmonHelper;
import aurelienribon.tweenengine.*;

public class ModelData extends ModelDataShared implements ICapabilityProvider {
	public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);
	@CapabilityInject(ModelData.class)
	public static Capability MODELDATA_CAPABILITY = null;
	public boolean resourceInit = false;
	public boolean resourceLoaded = false;
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
	public PropGroup propBase;
	public List<PropGroup> propGroups;

	//this is data to track server emotes that are synced between all players
	public Emote curEmote = null;
	public Timeline curEmoteTimeline = null;
	public long lastTime = 0;
	public float[] animStates = new float[Emote.STATE_COUNT];
	public float curEmoteSpeed = 1.0F;
	public long curEmoteTempId = 0;

	//and this is data to track clientside emote previews shown when creating emotes
	public Emote previewEmote = null;
	public Timeline previewTimeline = null;
	public long previewLastTime = 0;
	public float[] previewStates = new float[Emote.STATE_COUNT];
	public long previewTempId = 0;


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

		this.propBase = new PropGroup(this.player);
		this.propGroups = new ArrayList<PropGroup>();
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
		if (this.player != null) {
			if (this.player.worldObj.isRemote) {
				Minecraft mc = Minecraft.getMinecraft();
				if (this.player == mc.thePlayer && mc.currentScreen instanceof GuiNPCInterface) {
					if (((GuiNPCInterface) mc.currentScreen).hasSubGui() && !(((GuiNPCInterface) mc.currentScreen).getSubGui() instanceof GuiCreationSkinLoad)) {
						return;
					}
				}
			}
		}

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

	// public void update() {
		// }

		public void clearPropsServer() {
			this.propBase = new PropGroup(this.player);
			this.propGroups = new ArrayList<PropGroup>();
			Server.sendAssociatedData(this.player, EnumPackets.PROP_CLEAR, this.player.getUniqueID());
		}

		public void syncPropsClient() {
			NBTTagCompound compound = new NBTTagCompound();
			Client.sendData(EnumPackets.PROP_SYNC, this.propsToNBT(compound));
		}

		public void syncPropsServer() {
			NBTTagCompound compound = new NBTTagCompound();
			Server.sendAssociatedData(this.player, EnumPackets.PROP_SYNC, this.player.getUniqueID(), this.propsToNBT(compound));
		}

		public NBTTagCompound propsToNBT(NBTTagCompound compound) {

			compound.setTag("propBase", this.propBase.writeToNBT());

			for (int i = 0; i < this.propGroups.size(); i++) {
				compound.setTag(("propGroup" + String.valueOf(i)), this.propGroups.get(i).writeToNBT());
			}

			return compound;
		}

		public void propsFromNBT(NBTTagCompound compound) {

			this.propBase = new PropGroup(this.player);
			this.propBase.readFromNBT(compound.getCompoundTag("propBase"));

			this.propGroups = new ArrayList<PropGroup>();

			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				PropGroup propGroup = new PropGroup(this.player);
				propGroup.readFromNBT(compound.getCompoundTag("propGroup" + String.valueOf(i)));

				if (!propGroup.name.equals("")) {
					this.propGroups.add(propGroup);
				} else {
					break;
				}
			}
		}

		public void showPropGroupServerByName (String name) {
			for (int i = 0; i < this.propGroups.size(); i++) {
				if (this.propGroups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
					this.propGroups.get(i).hide = false;
					Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_SHOW, this.player.getUniqueID(), i);
				}
			}
		}

		public void hidePropGroupServer(int i) {
			this.propGroups.get(i).hide = true;
			Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_HIDE, this.player.getUniqueID(), i);
		}

		public void hidePropGroupServerByName (String name) {
			for (int i = 0; i < this.propGroups.size(); i++) {
				if (this.propGroups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
					this.propGroups.get(i).hide = true;
					Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_HIDE, this.player.getUniqueID(), i);
				}
			}
		}

		public void togglePropGroupServerByName (String name) {
			for (int i = 0; i < this.propGroups.size(); i++) {
				if (this.propGroups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
					if (this.propGroups.get(i).hide == true) {
						this.propGroups.get(i).hide = false;
						Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_SHOW, this.player.getUniqueID(), i);
					} else {
						this.propGroups.get(i).hide = true;
						Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_HIDE, this.player.getUniqueID(), i);
					}
				}
			}
		}

		public void removePropGroupByName (String name) {
			for (int i = 0; i < this.propGroups.size(); i++) {
				if (this.propGroups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
					this.propGroups.remove(i);
				}
			}
		}

		public void removePropGroupServerByName (String name) {
			for (int i = 0; i < this.propGroups.size(); i++) {
				if (this.propGroups.get(i).name.toLowerCase().equals(name.toLowerCase())) {
					this.propGroups.remove(i);
					Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_REMOVE, this.player.getUniqueID(), i);
				}
			}
		}

		public void addPropGroupServer(PropGroup propGroup) {
			NBTTagCompound compound = propGroup.writeToNBT();

			PropGroup propGroupTemp = new PropGroup(this.player);
			propGroupTemp.readFromNBT(compound);

			this.propGroups.add(propGroupTemp);

			Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_ADD, this.player.getUniqueID(), compound);
		}



		//Emotes


		public static class ModelAccessor implements TweenAccessor<float[]> {
			public static final ModelAccessor INSTANCE = new ModelAccessor();
			@Override
			public int getValues(float[] target, int tweenType, float[] returnValues, Entity entity) {
				returnValues[0] = target[tweenType];
				return 1;
			}

			@Override
			public void setValues(float[] target, int tweenType, float[] newValues, Entity entity) {
				target[tweenType] = newValues[0];
			}
		}
		static {
			Tween.registerAccessor(float[].class, ModelAccessor.INSTANCE);
		}

		public void startEmote(Emote emote, float speed, EntityPlayer player) {
			this.endCurEmote();
			Timeline timeline = createTimeline(emote, this.animStates);

			timeline.start(player);
			this.curEmote = emote;
			this.curEmoteTimeline = timeline;
			this.curEmoteSpeed = speed;
			this.lastTime = System.currentTimeMillis();
			this.curEmoteTempId = this.lastTime;
		}
		public void startPreviewEmote(Emote emote, EntityPlayer player, boolean isintro) {
			this.endPreviewEmote();

			Timeline timeline;
			if(isintro) {
				timeline = createPreviewTimeline(emote, this.previewStates, isintro);
			} else {
				timeline = createTimeline(emote, this.previewStates);
			}

			timeline.start(player);
			this.previewEmote = emote;
			this.previewTimeline = timeline;
			this.previewLastTime = System.currentTimeMillis();
			this.previewTempId = this.previewLastTime;
		}
		public static Timeline createTimeline(Emote emote, float[] states) {
			Timeline intro = null;
			Timeline loop = null;
			for(int partId = 0; partId < Emote.PART_COUNT; partId++) {
				Timeline t;
				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + Emote.INTRO_OFFSET), Emote.AXIS_COUNT*partId + Emote.OFF_X, states);
				if(t != null) {
					if(intro == null) intro = Timeline.createParallel();
					intro.push(t);
				}
				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + Emote.INTRO_ROTATE), Emote.AXIS_COUNT*partId + Emote.ROT_X, states);
				if(t != null) {
					if(intro == null) intro = Timeline.createParallel();
					intro.push(t);
				}

				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + Emote.LOOP_OFFSET), Emote.AXIS_COUNT*partId + Emote.OFF_X, states);
				if(t != null) {
					if(loop == null) loop = Timeline.createParallel();
					loop.push(t.repeat(60*60*24, 0));
				}
				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + Emote.LOOP_ROTATE), Emote.AXIS_COUNT*partId + Emote.ROT_X, states);
				if(t != null) {
					if(loop == null) loop = Timeline.createParallel();
					loop.push(t.repeat(60*60*24, 0));
				}
			}
			if(loop == null) {
				if(intro == null) {
					return Timeline.createParallel();
				} else {
					return intro;
				}
			} else if(intro == null) {
				return loop;
			} else {
				Timeline timeline = Timeline.createSequence();
				timeline.push(intro);
				timeline.push(loop);
				return timeline;
			}
		}
		public static Timeline createPreviewTimeline(Emote emote, float[] states, boolean isintro) {
			Timeline timeline = Timeline.createParallel();
			int a1 = (isintro ? Emote.INTRO_OFFSET : Emote.LOOP_OFFSET);
			int a2 = (isintro ? Emote.INTRO_ROTATE : Emote.LOOP_ROTATE);
			for(int partId = 0; partId < Emote.PART_COUNT; partId++) {
				Timeline t;
				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + a1), Emote.AXIS_COUNT*partId + Emote.OFF_X, states);
				if(t != null) {
					timeline.push(t);
				}
				t = createTimelineFromEmotePartCommandList(emote.commands.get(4*partId + a2), Emote.AXIS_COUNT*partId + Emote.ROT_X, states);
				if(t != null) {
					timeline.push(t);
				}
			}
			return timeline.repeat(60*60*24, isintro? 1.0F : 0);
		}
		public static Timeline createTimelineFromEmotePartCommandList(ArrayList<Emote.PartCommand> list, int tweenId, float[] states) {
			if(list == null) return null;
			float prex = Float.MAX_VALUE;
			float prey = Float.MAX_VALUE;
			float prez = Float.MAX_VALUE;
			int totalDisabled = 0;
			Timeline timeline = Timeline.createSequence();
			for(int i = 0; i < list.size(); i++) {
				Emote.PartCommand command = list.get(i);
				if(command.disabled) {
					totalDisabled += 1;
				} else {
					boolean isx = Math.abs(command.x - prex) > 0.0001F;
					boolean isy = Math.abs(command.y - prey) > 0.0001F;
					boolean isz = Math.abs(command.z - prez) > 0.0001F;
					prex = command.x;
					prey = command.y;
					prez = command.z;
					int total = (isx ? 1 : 0) + (isy ? 1 : 0) + (isz ? 1 : 0);
					if(total == 0) {
						timeline.pushPause(command.duration);
					} else if(total == 1) {
						Tween tween;
						//NOTE: this is highly specific to ModelAccessor
						if(isx) {
							tween = Tween.to(states, tweenId + 0, command.duration).target(command.x);
						} else if(isy) {
							tween = Tween.to(states, tweenId + 1, command.duration).target(command.y);
						} else {
							tween = Tween.to(states, tweenId + 2, command.duration).target(command.z);
						}
						tween.ease(TweenUtils.easings[command.easing]);
						timeline.push(tween);
					} else {
						Timeline par = Timeline.createParallel();
						TweenEquation eq = TweenUtils.easings[command.easing];
						if(isx) {
							Tween tween = Tween.to(states, tweenId + 0, command.duration).target(command.x);
							tween.ease(eq);
							par.push(tween);
						}
						if(isy) {
							Tween tween = Tween.to(states, tweenId + 1, command.duration).target(command.y);
							tween.ease(eq);
							par.push(tween);
						}
						if(isz) {
							Tween tween = Tween.to(states, tweenId + 2, command.duration).target(command.z);
							tween.ease(eq);
							par.push(tween);
						}
						timeline.push(par);
					}
				}
			}
			if(list.size() == totalDisabled) return null;
			return timeline;
		}

		public void endCurEmote() {
			if(this.curEmoteTempId > 0) {
				this.curEmoteTimeline.free();
				this.curEmoteTimeline = null;
				this.curEmote = null;
				this.curEmoteTempId = 0;
				for(int i = 0; i < Emote.STATE_COUNT; i++) {
					this.animStates[i] = 0.0F;
				}
			}
		}
		public void endPreviewEmote() {
			if(this.previewTempId > 0) {
				this.previewTimeline.free();
				this.previewTimeline = null;
				this.previewTempId = 0;
				for(int i = 0; i < Emote.STATE_COUNT; i++) {
					this.previewStates[i] = 0.0F;
				}
			}
		}

		public void updateAnim() {
			if(this.curEmoteTempId > 0) {
				long curTime = System.currentTimeMillis();
				float delta = (curTime - this.lastTime)/1000F;
				if(delta > .001) {
					this.curEmoteTimeline.update(this.curEmoteSpeed*delta, (Entity)this.player);
					this.lastTime = curTime;
					if(this.curEmoteTimeline.isFinished()) {
						this.endCurEmote();
					}
				}
			}
			if(this.previewTempId > 0) {
				long curTime = System.currentTimeMillis();
				float delta = (curTime - this.previewLastTime)/1000F;
				if(delta > .001) {
					this.previewTimeline.update(delta, (Entity)this.player);
					this.previewLastTime = curTime;
					if(this.previewTimeline.isFinished()) {
						this.endPreviewEmote();
					}
				}
			}
		}
		public long getPlayingEmoteId() {
			if(this.previewTempId > 0) {
				return this.previewTempId;
			} else {
				return this.curEmoteTempId;
			}
		}


		public static ModelRenderer getEarsModel(ModelPlayer model) {
			return model.boxList.get(model.boxList.indexOf(model.bipedLeftArm) - 2);
		}
		public void animModelPlayer(ModelPlayer biped) {
			if(this.previewTempId > 0) {
				animModelPlayerFromStates(biped, this.previewStates, this.previewEmote, this.player.height);
			} else if(this.curEmoteTempId > 0) {
				animModelPlayerFromStates(biped, this.animStates, this.curEmote, this.player.height);
			}
		}
		public void animModelBiped(ModelBiped biped) {
			if(this.previewTempId > 0) {
				animModelBipedFromStates(biped, this.previewStates, this.previewEmote);
			} else if(this.curEmoteTempId > 0) {
				animModelBipedFromStates(biped, this.animStates, this.curEmote);
			}
		}

		public static void animModelPlayerFromStates(ModelPlayer biped, float[] states, Emote emote, float playerHeight) {
			if(emote.partIsOffset(Emote.HEAD)) {
				setPartOffset(biped.bipedHead, Emote.HEAD, states);
				setPartOffset(biped.bipedHeadwear, Emote.HEAD, states);
				setPartOffset(getEarsModel(biped), Emote.HEAD, states);
			}
			if(emote.partIsRotate(Emote.HEAD)) {
				setPartRotate(biped.bipedHead, Emote.HEAD, states);
				setPartRotate(biped.bipedHeadwear, Emote.HEAD, states);
			}
			if(emote.partIsOffset(Emote.RIGHT_ARM)) {
				setPartOffset(biped.bipedRightArm, Emote.RIGHT_ARM, states);
				setPartOffset(biped.bipedRightArmwear, Emote.RIGHT_ARM, states);
			}
			if(emote.partIsRotate(Emote.RIGHT_ARM)) {
				setPartRotate(biped.bipedRightArm, Emote.RIGHT_ARM, states);
				setPartRotate(biped.bipedRightArmwear, Emote.RIGHT_ARM, states);
			}
			if(emote.partIsOffset(Emote.LEFT_ARM)) {
				setPartOffset(biped.bipedLeftArm, Emote.LEFT_ARM, states);
				setPartOffset(biped.bipedLeftArmwear, Emote.LEFT_ARM, states);
			}
			if(emote.partIsRotate(Emote.LEFT_ARM)) {
				setPartRotate(biped.bipedLeftArm, Emote.LEFT_ARM, states);
				setPartRotate(biped.bipedLeftArmwear, Emote.LEFT_ARM, states);
			}
			if(emote.partIsOffset(Emote.RIGHT_LEG)) {
				setPartOffset(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
				setPartOffset(biped.bipedRightLegwear, Emote.RIGHT_LEG, states);
			}
			if(emote.partIsRotate(Emote.RIGHT_LEG)) {
				setPartRotate(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
				setPartRotate(biped.bipedRightLegwear, Emote.RIGHT_LEG, states);
			}
			if(emote.partIsOffset(Emote.LEFT_LEG)) {
				setPartOffset(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
				setPartOffset(biped.bipedLeftLegwear, Emote.LEFT_LEG, states);
			}
			if(emote.partIsRotate(Emote.LEFT_LEG)) {
				setPartRotate(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
				setPartRotate(biped.bipedLeftLegwear, Emote.LEFT_LEG, states);
			}

			if(emote.partIsUsed(Emote.BODY)) {
				setPartAxis(biped.bipedBody, Emote.BODY, states);
				setPartAxis(biped.bipedBodyWear, Emote.BODY, states);
			}
			if(emote.partIsUsed(Emote.MODEL)) {
				float offsetX = (states[Emote.MODEL + Emote.OFF_X]);
				float offsetY = (states[Emote.MODEL + Emote.OFF_Y]);
				float offsetZ = (states[Emote.MODEL + Emote.OFF_Z]);
				float rotX = states[Emote.MODEL + Emote.ROT_X];
				float rotY = states[Emote.MODEL + Emote.ROT_Y];
				float rotZ = states[Emote.MODEL + Emote.ROT_Z];

				GlStateManager.translate(0, playerHeight / 2, 0);

				GlStateManager.translate(offsetX/playerHeight, offsetY/playerHeight, offsetZ/playerHeight);

				if (rotY != 0)
				GlStateManager.rotate(rotY * 90.0F/(float)Math.PI, 0, 1, 0);
				if (rotX != 0)
				GlStateManager.rotate(rotX * 90.0F/(float)Math.PI, 1, 0, 0);
				if (rotZ != 0)
				GlStateManager.rotate(rotZ * 90.0F/(float)Math.PI, 0, 0, 1);

				GlStateManager.translate(0, -playerHeight / 2, 0);
			}
		}
		public static void animModelBipedFromStates(ModelBiped biped, float[] states, Emote emote) {
			if(emote.partIsOffset(Emote.HEAD)) {
				setPartOffset(biped.bipedHead, Emote.HEAD, states);
			}
			if(emote.partIsRotate(Emote.HEAD)) {
				setPartRotate(biped.bipedHead, Emote.HEAD, states);
			}
			if(emote.partIsOffset(Emote.RIGHT_ARM)) {
				setPartOffset(biped.bipedRightArm, Emote.RIGHT_ARM, states);
			}
			if(emote.partIsRotate(Emote.RIGHT_ARM)) {
				setPartRotate(biped.bipedRightArm, Emote.RIGHT_ARM, states);
			}
			if(emote.partIsOffset(Emote.LEFT_ARM)) {
				setPartOffset(biped.bipedLeftArm, Emote.LEFT_ARM, states);
			}
			if(emote.partIsRotate(Emote.LEFT_ARM)) {
				setPartRotate(biped.bipedLeftArm, Emote.LEFT_ARM, states);
			}
			if(emote.partIsOffset(Emote.RIGHT_LEG)) {
				setPartOffset(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
			}
			if(emote.partIsRotate(Emote.RIGHT_LEG)) {
				setPartRotate(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
			}
			if(emote.partIsOffset(Emote.LEFT_LEG)) {
				setPartOffset(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
			}
			if(emote.partIsRotate(Emote.LEFT_LEG)) {
				setPartRotate(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
			}

			if(emote.partIsUsed(Emote.BODY)) {
				setPartAxis(biped.bipedBody, Emote.BODY, states);
			}
		}
		public static void setPartOffset(ModelRenderer part, int partId, float[] states) {
			partId *= 6;
			part.offsetX = states[partId + Emote.OFF_X];
			part.offsetY = states[partId + Emote.OFF_Y];
			part.offsetZ = states[partId + Emote.OFF_Z];
		}
		public static void setPartRotate(ModelRenderer part, int partId, float[] states) {
			partId *= 6;
			part.rotateAngleX = states[partId + Emote.ROT_X];
			part.rotateAngleY = states[partId + Emote.ROT_Y];
			part.rotateAngleZ = states[partId + Emote.ROT_Z];
		}
		public static void setPartAxis(ModelRenderer part, int partId, float[] states) {
			partId *= 6;
			part.offsetX = states[partId + Emote.OFF_X];
			part.offsetY = states[partId + Emote.OFF_Y];
			part.offsetZ = states[partId + Emote.OFF_Z];
			part.rotateAngleX = states[partId + Emote.ROT_X];
			part.rotateAngleY = states[partId + Emote.ROT_Y];
			part.rotateAngleZ = states[partId + Emote.ROT_Z];
		}

		public static void resetModelPlayerAfterEmote(ModelPlayer biped) {
			resetPart(biped.bipedHead);
			resetPart(biped.bipedHeadwear);
			resetPart(biped.bipedBody);
			resetPart(biped.bipedLeftArm);
			resetPart(biped.bipedRightArm);
			resetPart(biped.bipedLeftLeg);
			resetPart(biped.bipedRightLeg);
			resetPart(biped.bipedBodyWear);
			resetPart(biped.bipedLeftArmwear);
			resetPart(biped.bipedRightArmwear);
			resetPart(biped.bipedLeftLegwear);
			resetPart(biped.bipedRightLegwear);
			resetPart(getEarsModel(biped));
		}
		public static void resetModelBipedAfterEmote(ModelBiped biped) {
			resetPart(biped.bipedHead);
			resetPart(biped.bipedBody);
			resetPart(biped.bipedLeftArm);
			resetPart(biped.bipedRightArm);
			resetPart(biped.bipedLeftLeg);
			resetPart(biped.bipedRightLeg);
			// resetPart(getEarsModel(biped));
		}

		public static void resetPart(ModelRenderer part) {
			if(part != null)
			part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;
		}
	}
