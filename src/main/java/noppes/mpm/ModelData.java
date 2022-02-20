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
import net.minecraft.client.entity.AbstractClientPlayer;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;

import noppes.mpm.LogWriter;
import noppes.mpm.client.Client;
import noppes.mpm.client.gui.GuiCreationSkinLoad;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.constants.EnumPackets;
import noppes.mpm.util.PixelmonHelper;
import aurelienribon.tweenengine.*;

public class ModelData extends ModelDataShared implements ICapabilityProvider {
	public static ExecutorService saveExecutor = Executors.newFixedThreadPool(1);
	@CapabilityInject(ModelData.class)
	public static Capability MODELDATA_CAPABILITY = null;
	public boolean resourceInit = false;
	public boolean resourceLoaded = false;
	public boolean armsLoaded = false;
	public Object textureObject = null;
	public ItemStack backItem;
	public short soundType;
	public float prePosX = 0.0f;
	public float prePosZ = 0.0f;
	// public float prePosY;
	public EntityPlayer player;
	public PropGroup propBase;
	public List<PropGroup> propGroups;

	public static final byte SECTION_NONE = 0;
	public static final byte SECTION_INTRO = 1;
	public static final byte SECTION_LOOP = 2;
	public static final byte SECTION_OUTRO = 3;
	public static final byte SECTION_PREVIEW_PAUSE = 4;

	public static final float RATE_MIN = .5f;
	public static final float PREVIEW_PAUSE_TIME = .5f;//NOTE: must be >0
	public static final float MOVEMENT_SCALING = .8f;

	//this is data to track server emotes
	public ArrayList<ArrayList<Emote.PartCommand>> emoteCommands = Emote.createCommandsList();
	public int[] emotePartUsages = new int[2*Emote.PART_COUNT];
	public final float[] emoteSpeeds = new float[2*Emote.PART_COUNT];
	public final byte[] emoteCommandSections = new byte[2*Emote.PART_COUNT];
	public final int[] emoteCommandIndices = new int[2*Emote.PART_COUNT];
	public final float[] emoteCommandTimes = new float[2*Emote.PART_COUNT];
	public final float[] emoteMovements = new float[Emote.STATE_COUNT];
	public final float[] emoteStates = new float[Emote.STATE_COUNT];

	public long emoteLastTime = 0;
	public boolean emoteIsPlaying = false;
	public float emoteMovementRate = 0.0f;

	public Emote queuedEmote;
	public boolean queuedOutroAllFirst;
	public float queuedSpeed;

	//this is to track client-side emote previews
	public ArrayList<ArrayList<Emote.PartCommand>> previewCommands = null;
	public int[] previewPartUsages = null;
	public byte[] previewCommandSections = null;
	public int[] previewCommandIndices = null;
	public float[] previewCommandTimes = null;
	public float[] previewMovements = null;
	public float[] previewStates = null;

	public int previewRepetitions = 4;
	public long previewLastTime = 0;
	public boolean previewIsPlaying = false;


	//the following arrays will point to the emote, the preview, or null, depending on which takes precedence for current animation
	public float[] animStates = null;
	public int[] animPartUsages = null;


	public ModelData() {
		this.backItem = null;
		this.soundType = 0;
		this.player = null;

		this.propBase = new PropGroup(this.player);
		this.propGroups = new ArrayList<PropGroup>();
	}

	@Override
	public synchronized NBTTagCompound writeToNBT() {
		NBTTagCompound compound = super.writeToNBT();
		compound.setShort("SoundType", this.soundType);
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
		Boolean prevModelType = new Boolean(this.slim);

		super.readFromNBT(compound);
		this.soundType = compound.getShort("SoundType");
		if (this.player != null) {
			this.player.refreshDisplayName();
			if (this.entityClass == null) {
				this.player.getEntityData().removeTag("MPMModel");
			} else {
				this.player.getEntityData().setString("MPMModel", this.entityClass.getCanonicalName());
			}
		}

		if (!prevUrl.equals(this.url)) {
			this.resourceInit = false;
			this.resourceLoaded = false;
		}

		if (this.player != null) {
			if (this.player.worldObj.isRemote && !prevModelType.equals(this.slim)) {
				this.reloadSkinType();
			}
		}

		this.propsFromNBT(compound);
	}

	public void reloadSkinType() {
		NetworkPlayerInfo playerInfo = null;

		if (this.player != null) {
			playerInfo = (NetworkPlayerInfo)ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, (AbstractClientPlayer) this.player, 0);
		}

		if (playerInfo == null) return;

		String type;

		if (this.slim) {
			type = "slim";
		} else {
			type = "default";
		}

		ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, type, 5);
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

			if(this.entity != null) {
				this.entity.setSilent(true);
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

	public float getOffsetCamera(EntityPlayer player) {
		if (!MorePlayerModels.EnablePOV) {
			return 0.0F;
		} else {
			float offset = -this.offsetY();
			if(this.animPartUsages != null && (this.animPartUsages[2*Emote.MODEL + 0]&Emote.FLAG_CAMERA_FOLLOWS_MODEL_OFFSET) > 0) {
				offset += this.animStates[6*Emote.MODEL + Emote.OFF_Y];
			}

			if (offset < -0.2F && this.isBlocked(player)) {
				offset = -0.2F;
			}

			offset -= this.modelOffsetY;

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
		if(data.player == null) data.loadPlayerData(player);
		return data;
	}

	public void loadPlayerData(EntityPlayer player) {
		this.player = player;
		UUID id = player.getUniqueID();
		String filename = id.toString();
		if (filename.isEmpty()) {
			filename = "noplayername";
		}

		filename = filename + ".dat";

		NBTTagCompound compound = null;
		File file;
		try {
			file = new File(MorePlayerModels.dir, filename);
			if(file.exists()) {
				compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
			}
		} catch (Exception var4) {
			LogWriter.except(var4);
		}
		if(compound != null) {
			this.readFromNBT(compound);
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

	public void showPropGroupServerByName(String name) {
		for (int i = this.propGroups.size() - 1; i >= 0; i--) {
			if (this.propGroups.get(i).name.toLowerCase().startsWith(name.toLowerCase())) {
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
		for (int i = this.propGroups.size() - 1; i >= 0; i--) {
			if (this.propGroups.get(i).name.toLowerCase().startsWith(name.toLowerCase())) {
				this.propGroups.get(i).hide = true;
				Server.sendAssociatedData(this.player, EnumPackets.PROPGROUP_HIDE, this.player.getUniqueID(), i);
			}
		}
	}

	public void togglePropGroupServerByName (String name) {
		for (int i = this.propGroups.size() - 1; i >= 0; i--) {
			if (this.propGroups.get(i).name.toLowerCase().startsWith(name.toLowerCase())) {
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
		for (int i = this.propGroups.size() - 1; i >= 0; i--) {
			if (this.propGroups.get(i).name.toLowerCase().startsWith(name.toLowerCase())) {
				this.propGroups.remove(i);
			}
		}
	}

	public void removePropGroupServerByName (String name) {
		for (int i = this.propGroups.size() - 1; i >= 0; i--) {
			if (this.propGroups.get(i).name.toLowerCase().startsWith(name.toLowerCase())) {
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


	////////////////////////////
	// Emotes


	public static final void runCommand(ModelData data, String str) {
		// send command or text to server. For the time being it is
		// not possible to execute client-only commands.
		if(data.player.worldObj.isRemote && !str.equals("")) {
		Minecraft mc = Minecraft.getMinecraft();
			if(data.player == mc.thePlayer) {
				mc.thePlayer.sendChatMessage(str);
			}
		}

	}

	public static final boolean updateEmoteStates(
		ModelData data,
		ArrayList<ArrayList<Emote.PartCommand>> commands,
		int[] partUsages,
		float[] partSpeeds,
		byte[] commandSections,
		int[] commandIndices,
		float[] commandTimes,
		float[] movements,
		float[] states,
		float movementRate,
		float delta
	) {
		boolean runsNextFrame = false;
		for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
			int partId = meta_i/2;
			int isRotate = meta_i%2;

			if(commandSections[meta_i] == SECTION_NONE) continue;

			int usage = partUsages[meta_i];
			float remainingDelta = partSpeeds[meta_i]*delta;
			float loopSpeedMult = 1.0f;

			boolean play_outro_at_loop_boundary = false;
			boolean pause_at_loop_boundary = false;
			float m = movementRate;
			if((usage&Emote.FLAG_INVERT_MOVEMENT) > 0) {
				m = Math.max(0.0f, 1.0f - m);
			}

			if((usage&Emote.ANIMFLAG_END_EMOTE) > 0) {
				if(commandSections[meta_i] != SECTION_OUTRO) {
					if((usage&Emote.FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY) > 0) {
						play_outro_at_loop_boundary = true;
					} else {
						fastForwardCommands(data, commands, partUsages, commandSections, commandIndices, commandTimes, meta_i, SECTION_OUTRO);
					}
				}
			} else if((usage&Emote.FLAG_OUTRO_PLAYS_WHEN_STILL) > 0) {
				if((usage&Emote.FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY) > 0) {
					if(m <= RATE_MIN) {
						play_outro_at_loop_boundary = true;
						loopSpeedMult = RATE_MIN;
					} else {
						loopSpeedMult = m;
					}
				} else {
					if(m <= RATE_MIN) {
						loopSpeedMult = RATE_MIN;
						fastForwardCommands(data, commands, partUsages, commandSections, commandIndices, commandTimes, meta_i, SECTION_OUTRO);
					} else {
						loopSpeedMult = m;
					}
				}
			} else if((usage&Emote.FLAG_LOOP_PAUSES_WHEN_STILL) > 0) {
				if((usage&Emote.FLAG_LOOP_ONLY_PAUSES_AT_BOUNDARY) > 0) {
					if(m <= RATE_MIN) {
						pause_at_loop_boundary = true;
						loopSpeedMult = RATE_MIN;
					} else {
						loopSpeedMult = m;
					}
				} else {
					loopSpeedMult = m;
				}
			}

			final int state_i = Emote.AXIS_COUNT*partId + 3*isRotate;
			final int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
			final int loop_i = intro_i + 1;
			final int outro_i = loop_i + 1;

			boolean hasLoopedThisFrame = false;
			boolean hasOutroLoopedThisFrame = false;
			while(remainingDelta > 0) {
				Emote.PartCommand commandToApply = null;
				float speedMultToApply = 1.0f;
				if(commandSections[meta_i] == SECTION_INTRO) {
					ArrayList<Emote.PartCommand> section = commands.get(intro_i);
					int section_size = section == null ? 0 : section.size();
					if(commandIndices[meta_i] < section_size) {
						commandToApply = section.get(commandIndices[meta_i]);
					} else {
						commandSections[meta_i] = SECTION_LOOP;
						commandIndices[meta_i] = 0;
					}
				}
				if(commandSections[meta_i] == SECTION_LOOP) {
					ArrayList<Emote.PartCommand> section = commands.get(loop_i);
					int section_size = section == null ? 0 : section.size();
					if(commandIndices[meta_i] >= section_size || (commandIndices[meta_i] == 0 && commandTimes[meta_i] <= 0)) {
						if(section_size == 0 || play_outro_at_loop_boundary) {
							commandSections[meta_i] = SECTION_OUTRO;
							commandIndices[meta_i] = 0;
						} else if(!hasLoopedThisFrame) {//loop (and prevent inf loop)
							hasLoopedThisFrame = true;
							if(!pause_at_loop_boundary) {
								commandToApply = section.get(0);
								speedMultToApply = loopSpeedMult;
							}
							commandIndices[meta_i] = 0;
						}
					} else {
						commandToApply = section.get(commandIndices[meta_i]);
						speedMultToApply = loopSpeedMult;
					}
				}
				if(commandSections[meta_i] == SECTION_OUTRO) {
					ArrayList<Emote.PartCommand> section = commands.get(outro_i);
					ArrayList<Emote.PartCommand> loop = commands.get(loop_i);
					int section_size = section == null ? 0 : section.size();
					if(commandIndices[meta_i] < section_size) {
						commandToApply = section.get(commandIndices[meta_i]);
					} else if((usage&Emote.FLAG_OUTRO_PLAYS_WHEN_STILL) > 0 && (usage&Emote.ANIMFLAG_END_EMOTE) == 0) {//restart for walkcycle
						if(m <= RATE_MIN) {//hold at outro end
							partUsages[meta_i] &= ~Emote.FLAG_USED;
						} else if(!hasOutroLoopedThisFrame) {//loop to intro (and prevent inf loop)
							hasOutroLoopedThisFrame = true;
							partUsages[meta_i] |= Emote.FLAG_USED;
							commandSections[meta_i] = SECTION_INTRO;
							commandIndices[meta_i] = 0;
							// states[state_i + Emote.OFF_X] = 0;
							// states[state_i + Emote.OFF_Y] = 0;
							// states[state_i + Emote.OFF_Z] = 0;

							continue;
						}
					} else {//finished
						setEmoteCommands(commands, partUsages, partSpeeds, commandSections, commandIndices, commandTimes, states, meta_i, null, 0);
					}
				}
				if(commandToApply == null) break;

				remainingDelta = applyCommand(data, commandTimes, movements, states, meta_i, state_i, commandToApply, remainingDelta, speedMultToApply);
				if(remainingDelta == 0) break;
				//command is finished, prepare next command
				commandIndices[meta_i] += 1;
				commandTimes[meta_i] = 0;
			}
			if(commandSections[meta_i] != SECTION_NONE) {
				runsNextFrame = true;
			}
		}
		return runsNextFrame;
	}
	public static final boolean updatePreviewStates(
		ArrayList<ArrayList<Emote.PartCommand>> commands,
		int[] partUsages,
		byte[] commandSections,
		int[] commandIndices,
		float[] commandTimes,
		float[] movements,
		float[] states,
		int previewRepetitions,
		float delta
	) {
		boolean runsNextFrame = false;
		boolean doOutro = false;

		for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
			final int partId = meta_i/2;
			final int isRotate = meta_i%2;

			if(commandSections[meta_i] == SECTION_NONE) continue;
			runsNextFrame = true;

			int usage = partUsages[meta_i];
			float remainingDelta = delta;

			boolean play_outro_at_loop_boundary = false;
			if((usage&Emote.ANIMFLAG_END_EMOTE) > 0) {
				if(commandSections[meta_i] < SECTION_OUTRO) {
					if((usage&Emote.FLAG_LOOP_ONLY_STOPS_AT_BOUNDARY) > 0) {
						play_outro_at_loop_boundary = true;
					} else {
						fastForwardCommands(null, commands, partUsages, commandSections, commandIndices, commandTimes, meta_i, SECTION_OUTRO);
					}
				}
			}
			if(remainingDelta <= 0) continue;

			final int state_i = Emote.AXIS_COUNT*partId + 3*isRotate;
			final int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
			final int loop_i = intro_i + 1;
			final int outro_i = loop_i + 1;

			boolean hasLoopedThisFrame = false;
			while(true) {
				Emote.PartCommand commandToApply = null;
				if(commandSections[meta_i] == SECTION_INTRO) {
					ArrayList<Emote.PartCommand> section = commands.get(intro_i);
					int section_size = section == null ? 0 : section.size();
					if(commandIndices[meta_i] < section_size) {
						commandToApply = section.get(commandIndices[meta_i]);
					} else {
						commandSections[meta_i] = SECTION_LOOP;
						commandIndices[meta_i] = 0;
					}
				}
				if(commandSections[meta_i] == SECTION_LOOP) {
					ArrayList<Emote.PartCommand> loop = commands.get(loop_i);
					ArrayList<Emote.PartCommand> intro = commands.get(intro_i);
					ArrayList<Emote.PartCommand> outro = commands.get(outro_i);
					int loop_size = loop == null ? 0 : loop.size();
					int intro_size = intro == null ? 0 : intro.size();
					int outro_size = outro == null ? 0 : outro.size();
					if(loop_size == 0 || (commandIndices[meta_i]%loop_size == 0 && commandTimes[meta_i] <= 0)) {
						if(loop_size == 0 || play_outro_at_loop_boundary) {
							commandSections[meta_i] = SECTION_OUTRO;
							commandIndices[meta_i] = 0;
						} else if(!hasLoopedThisFrame) {//loop (and prevent inf loop/crash)
							hasLoopedThisFrame = true;

							if(commandIndices[meta_i] >= previewRepetitions*loop_size && (intro_size > 0 || outro_size > 0)) {
								doOutro = true;
							}
							commandToApply = loop.get(0);
						}
					} else {
						commandToApply = loop.get(commandIndices[meta_i]%loop_size);
					}
				}
				if(commandSections[meta_i] == SECTION_OUTRO) {
					ArrayList<Emote.PartCommand> section = commands.get(outro_i);
					int section_size = section == null ? 0 : section.size();
					if(commandIndices[meta_i] < section_size) {
						commandToApply = section.get(commandIndices[meta_i]);
					} else {//pause
						commandSections[meta_i] = SECTION_PREVIEW_PAUSE;
						commandIndices[meta_i] = 0;
					}
				}
				if(commandSections[meta_i] == SECTION_PREVIEW_PAUSE) {
					if(commandTimes[meta_i] + remainingDelta > PREVIEW_PAUSE_TIME) {
						remainingDelta -= PREVIEW_PAUSE_TIME - commandTimes[meta_i];
						commandIndices[meta_i] = 1;
						commandTimes[meta_i] = PREVIEW_PAUSE_TIME;
					} else {
						commandTimes[meta_i] += remainingDelta;
						remainingDelta = 0;
					}
					break;
				}
				if(commandToApply == null) break;

				remainingDelta = applyCommand(null, commandTimes, movements, states, meta_i, state_i, commandToApply, remainingDelta, 1.0f);
				if(remainingDelta <= 0) break;
				//command is finished, prepare next command
				commandIndices[meta_i] += 1;
				commandTimes[meta_i] = 0;
			}
		}
		if(doOutro) {
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				if(commandSections[meta_i] == SECTION_NONE) continue;
				partUsages[meta_i] |= Emote.ANIMFLAG_END_EMOTE;
			}
		}
		if(runsNextFrame) {
			boolean endPreviewPause = true;
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				if(commandSections[meta_i] == SECTION_NONE) continue;
				if(commandSections[meta_i] != SECTION_PREVIEW_PAUSE || commandIndices[meta_i] == 0) {
					endPreviewPause = false;
					break;
				}
			}
			if(endPreviewPause) {
				for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
					if(commandSections[meta_i] == SECTION_NONE) continue;
					final int partId = meta_i/2;
					final int isRotate = meta_i%2;
					final int state_i = Emote.AXIS_COUNT*partId + 3*isRotate;
					partUsages[meta_i] &= ~Emote.ANIMFLAG_END_EMOTE;
					commandSections[meta_i] = SECTION_INTRO;
					commandIndices[meta_i] = 0;
					commandTimes[meta_i] = 0;
					states[state_i + Emote.OFF_X] = 0;
					states[state_i + Emote.OFF_Y] = 0;
					states[state_i + Emote.OFF_Z] = 0;
				}
			}
		}
		return runsNextFrame;
	}

	public static final float applyCommand(
		ModelData data,
		float[] commandTimes,
		float[] movements,
		float[] states,
		int meta_i,
		int state_i,
		Emote.PartCommand commandToApply,
		float delta,
		float speedMult
	) {
		//run the command and subtract from delta
		if(!commandToApply.disabled) {
			if(commandToApply.consoleCommand == null) {
				if(commandTimes[meta_i] == 0) {// NOTE: all commands must start at exactly 0 time for this to work
					movements[state_i + Emote.OFF_X] = states[state_i + Emote.OFF_X] - commandToApply.x;
					movements[state_i + Emote.OFF_Y] = states[state_i + Emote.OFF_Y] - commandToApply.y;
					movements[state_i + Emote.OFF_Z] = states[state_i + Emote.OFF_Z] - commandToApply.z;
				}
				float timeLeft = (commandToApply.duration - commandTimes[meta_i])/speedMult;
				if(delta > timeLeft) {
					delta -= timeLeft;

					states[state_i + Emote.OFF_X] = commandToApply.x;
					states[state_i + Emote.OFF_Y] = commandToApply.y;
					states[state_i + Emote.OFF_Z] = commandToApply.z;
				} else {
					float effectiveDelta = delta*speedMult;
					commandTimes[meta_i] += effectiveDelta;
					delta = 0;

					TweenEquation eq = TweenUtils.easings[commandToApply.easing];
					float t = 1 - eq.compute((commandTimes[meta_i] + effectiveDelta)/commandToApply.duration);
					states[state_i + Emote.OFF_X] = commandToApply.x + t*movements[state_i + Emote.OFF_X];
					states[state_i + Emote.OFF_Y] = commandToApply.y + t*movements[state_i + Emote.OFF_Y];
					states[state_i + Emote.OFF_Z] = commandToApply.z + t*movements[state_i + Emote.OFF_Z];
				}
			} else if(data != null) {
				runCommand(data, commandToApply.consoleCommand);
			}
		}
		return delta;
	}
	public static final void setEmoteCommands(
		ArrayList<ArrayList<Emote.PartCommand>> commands,
		int[] partUsages,
		float[] partSpeeds,
		byte[] commandSections,
		int[] commandIndices,
		float[] commandTimes,
		float[] states,
		int meta_i,
		Emote emote,
		float speed
	) {
		int partId = meta_i/2;
		int isRotate = meta_i%2;
		int state_i = Emote.AXIS_COUNT*partId + 3*isRotate;
		int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
		int loop_i = intro_i + 1;
		int outro_i = loop_i + 1;

		commandIndices[meta_i] = 0;
		commandTimes[meta_i] = 0;
		//reset this part
		if(partSpeeds != null) partSpeeds[meta_i] = speed;
		states[state_i + Emote.OFF_X] = 0;
		states[state_i + Emote.OFF_Y] = 0;
		states[state_i + Emote.OFF_Z] = 0;


		if(emote != null && emote.partUsages[meta_i] > 0) {
			partUsages[meta_i] = emote.partUsages[meta_i];
			commandSections[meta_i] = SECTION_INTRO;
			commands.set(intro_i, emote.commands.get(intro_i));
			commands.set(loop_i, emote.commands.get(loop_i));
			commands.set(outro_i, emote.commands.get(outro_i));
		} else {
			partUsages[meta_i] = 0;
			commandSections[meta_i] = SECTION_NONE;
			commands.set(intro_i, null);
			commands.set(loop_i, null);
			commands.set(outro_i, null);
		}
	}
	public static final void fastForwardCommands(
		ModelData data,
		ArrayList<ArrayList<Emote.PartCommand>> commands,
		int[] partUsages,
		byte[] commandSections,
		int[] commandIndices,
		float[] commandTimes,
		int meta_i,
		int desiredSection
	) {
		if(commandSections[meta_i] == desiredSection) return;

		int partId = meta_i/2;
		int isRotate = meta_i%2;
		int state_i = Emote.AXIS_COUNT*partId + 3*isRotate;
		int intro_i = Emote.SECTION_LIST_COUNT*partId + 3*isRotate;
		int loop_i = intro_i + 1;
		int outro_i = loop_i + 1;

		boolean runsNextFrame = false;

		commandTimes[meta_i] = 0;
		while(true) {
			Emote.PartCommand commandToApply = null;
			if(commandSections[meta_i] == SECTION_INTRO) {
				ArrayList<Emote.PartCommand> section = commands.get(intro_i);
				int section_size = section == null ? 0 : section.size();
				if(commandIndices[meta_i] < section_size) {
					commandToApply = section.get(commandIndices[meta_i]);
				} else {
					commandSections[meta_i] = SECTION_OUTRO;
					commandIndices[meta_i] = 0;
					if(desiredSection == SECTION_OUTRO) return;
				}
			}
			if(commandSections[meta_i] == SECTION_LOOP) {
				// ArrayList<Emote.PartCommand> section = commands.get(loop_i);
				// int section_size = section == null ? 0 : section.size();
				// if(commandIndices[meta_i] < section_size) {
				// 	commandToApply = section.get(commandIndices[meta_i]);
				// } else {
					commandSections[meta_i] = SECTION_OUTRO;
					commandIndices[meta_i] = 0;
					if(desiredSection == SECTION_OUTRO) return;
				// }
			}
			if(commandSections[meta_i] == SECTION_OUTRO) {
				ArrayList<Emote.PartCommand> section = commands.get(outro_i);
				ArrayList<Emote.PartCommand> loop = commands.get(loop_i);
				int section_size = section == null ? 0 : section.size();
				if(commandIndices[meta_i] < section_size) {
					commandToApply = section.get(commandIndices[meta_i]);
				} else {
					commandSections[meta_i] = SECTION_NONE;
					commandIndices[meta_i] = 0;
					if(desiredSection == SECTION_NONE) return;
				}
			}
			if(commandToApply == null) break;

			//run the command
			if(!commandToApply.disabled && commandToApply.consoleCommand != null) {
				runCommand(data, commandToApply.consoleCommand);
			}
			commandIndices[meta_i] += 1;
		}
	}
	public static final boolean doesEmoteConflict(int[] partUsages0, int[] partUsages1) {
		for(int partId = 0; partId < Emote.PART_COUNT; partId += 1) {
			for(int isRotate = 0; isRotate < 2; isRotate += 1) {
				final int meta_i = 2*partId + isRotate;
				if(partUsages0[meta_i] > 0 && partUsages1[meta_i] > 0) return true;
			}
		}
		return false;
	}

	public void updateAnim() {
		float updateCutoff = .001f;
		if(this.previewIsPlaying) {
			long curTime = System.currentTimeMillis();
			float delta = (curTime - this.previewLastTime)/1000F;
			if(delta > updateCutoff) {
				this.previewLastTime = curTime;
				this.previewIsPlaying = updatePreviewStates(this.previewCommands, this.previewPartUsages, this.previewCommandSections, this.previewCommandIndices, this.previewCommandTimes, this.previewMovements, this.previewStates, this.previewRepetitions, delta);
			}
			updateCutoff = 0.5f;
		}
		//NOTE: this is really dumb code to predict the player's movement speed
		float posX = (float)this.player.posX;
		float posZ = (float)this.player.posZ;
		float x = this.prePosX - posX;
		float z = this.prePosZ - posZ;
		this.prePosX = posX;
		this.prePosZ = posZ;
		if(x != 0.0F || z != 0.0F) {
			float movementRate = (x*x + z*z)*MOVEMENT_SCALING;
			if(movementRate < 1F) {
				// LogWriter.warn(movementRate);
				this.emoteMovementRate = movementRate;
			}
		}
		this.updateEmote(updateCutoff);
		if(this.previewIsPlaying) {
			this.animStates = this.previewStates;
			this.animPartUsages = this.previewPartUsages;
		} else if(this.emoteIsPlaying) {
			this.animStates = this.emoteStates;
			this.animPartUsages = this.emotePartUsages;
		} else {
			this.animStates = null;
			this.animPartUsages = null;
		}
	}

	public void updateEmote() {
		this.updateEmote(.001f);
	}
	public void updateEmote(float updateCutoff) {
		if(this.emoteIsPlaying) {
			long curTime = System.currentTimeMillis();
			float delta = (curTime - this.emoteLastTime)/1000F;
			if(delta > updateCutoff) {
				this.emoteLastTime = curTime;
				float movementRate = this.emoteMovementRate/delta;
				movementRate = 2*movementRate/(movementRate + 1.0f);

				this.emoteIsPlaying = updateEmoteStates(this, this.emoteCommands, this.emotePartUsages, this.emoteSpeeds, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, this.emoteMovements, this.emoteStates, movementRate, delta);
				//check if we can play the queued emote
				if(this.queuedEmote != null) {
					if(!this.emoteIsPlaying || (!this.queuedOutroAllFirst && !doesEmoteConflict(this.queuedEmote.partUsages, this.emotePartUsages))) {
						for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
							if(this.queuedEmote.partUsages[meta_i] > 0) {
								setEmoteCommands(this.emoteCommands, this.emotePartUsages, this.emoteSpeeds, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, this.emoteStates, meta_i, this.queuedEmote, this.queuedSpeed);
							}
						}
						this.emoteIsPlaying = true;
						this.queuedEmote = null;
					}
				}
			}
		}
	}

	public void startEmote(Emote emote, float speed, boolean cancel_if_conflicting, boolean outro_all_playing_first, boolean override_instead_of_outro) {
		boolean conflicts = this.emoteIsPlaying && doesEmoteConflict(emote.partUsages, this.emotePartUsages);
		//NOTE: this design can cause a minor desync bug if two emotes attempt to be queued at roughly the same time, a queue list would fix this, but I don't think the added complexity is worth it
		if(cancel_if_conflicting && (this.queuedEmote != null || conflicts)) return;

		if(override_instead_of_outro || !conflicts) {
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				if(outro_all_playing_first || emote.partUsages[meta_i] > 0) {
					fastForwardCommands(this, this.emoteCommands, this.emotePartUsages, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, meta_i, SECTION_NONE);
					setEmoteCommands(this.emoteCommands, this.emotePartUsages, this.emoteSpeeds, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, this.emoteStates, meta_i, emote, speed);
				}
			}
			if(!this.emoteIsPlaying) {
				this.emoteIsPlaying = true;
				this.emoteLastTime = System.currentTimeMillis();
			}
			if(!this.previewIsPlaying) {
				this.animStates = this.emoteStates;
				this.animPartUsages = this.emotePartUsages;
			}
		} else {
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				if(this.emotePartUsages[meta_i] > 0 && (outro_all_playing_first || emote.partUsages[meta_i] > 0)) {
					this.emotePartUsages[meta_i] |= Emote.ANIMFLAG_END_EMOTE;
				}
			}
			this.queuedEmote = emote;
			this.queuedOutroAllFirst = outro_all_playing_first;
			this.queuedSpeed = speed;
			this.emoteIsPlaying = true;
		}
	}
	public void endEmotes(boolean override_instead_of_outro) {
		if(this.emoteIsPlaying) {
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				if(override_instead_of_outro) {
					fastForwardCommands(this, this.emoteCommands, this.emotePartUsages, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, meta_i, SECTION_NONE);
					setEmoteCommands(this.emoteCommands, this.emotePartUsages, this.emoteSpeeds, this.emoteCommandSections, this.emoteCommandIndices, this.emoteCommandTimes, this.emoteStates, meta_i, null, 0);
				} else if(this.emotePartUsages[meta_i] > 0) {
					this.emotePartUsages[meta_i] |= Emote.ANIMFLAG_END_EMOTE;
				}
			}
			this.emoteIsPlaying = !override_instead_of_outro;
		}
		this.queuedEmote = null;
	}

	public void startPreview(Emote emote) {
		if(this.previewCommands == null) {
			this.previewCommands = Emote.createCommandsList();
			this.previewPartUsages = new int[2*Emote.PART_COUNT];
			this.previewCommandSections = new byte[2*Emote.PART_COUNT];
			this.previewCommandIndices = new int[2*Emote.PART_COUNT];
			this.previewCommandTimes = new float[2*Emote.PART_COUNT];
			this.previewMovements = new float[Emote.STATE_COUNT];
			this.previewStates = new float[Emote.STATE_COUNT];
		}
		for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
			setEmoteCommands(this.previewCommands, this.previewPartUsages, null, this.previewCommandSections, this.previewCommandIndices, this.previewCommandTimes, this.previewStates, meta_i, emote, 0);
		}
		this.previewIsPlaying = true;
		this.previewLastTime = System.currentTimeMillis();
		this.animStates = this.previewStates;
		this.animPartUsages = this.previewPartUsages;
	}
	public void endPreview() {
		if(this.previewIsPlaying) {
			for(int meta_i = 0; meta_i < 2*Emote.PART_COUNT; meta_i += 1) {
				setEmoteCommands(this.previewCommands, this.previewPartUsages, null, this.previewCommandSections, this.previewCommandIndices, this.previewCommandTimes, this.previewStates, meta_i, null, 0);
			}
			this.previewIsPlaying = false;
		}
	}

	public static ModelRenderer getEarsModel(ModelPlayer model) {
		return model.boxList.get(model.boxList.indexOf(model.bipedLeftArm) - 2);
	}
	public void animModelPlayer(ModelPlayer biped, float netHeadYaw, float headPitch) {
		if(this.animStates != null) animModelPlayerFromStates(this, biped, this.animStates, this.animPartUsages, this.player.height, netHeadYaw, headPitch);
	}
	public void animModelBiped(ModelBiped biped, float netHeadYaw, float headPitch) {
		if(this.animStates != null) animModelBipedFromStates(this, biped, this.animStates, this.animPartUsages, netHeadYaw, headPitch);
	}

	public static final void animModelPlayerFromStates(ModelData data, ModelPlayer biped, float[] states, int[] partUsages, float playerHeight, float netHeadYaw, float headPitch) {
		netHeadYaw /= 57.295776F;
		headPitch /= 57.295776F;
		ModelPartConfig config = data.body;
		if((partUsages[2*Emote.HEAD]&Emote.FLAG_USED) > 0) {
			setPartOffsetToTwo(biped.bipedHead, biped.bipedHeadwear, Emote.HEAD, states, config);
			setPartOffset(getEarsModel(biped), Emote.HEAD, states, config);
		}
		if((partUsages[2*Emote.HEAD + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.HEAD + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedHead, Emote.HEAD, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedHeadwear, Emote.HEAD, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedHead, Emote.HEAD, states);
				setPartRotate(biped.bipedHeadwear, Emote.HEAD, states);
			}
		}
		if((partUsages[2*Emote.RIGHT_ARM]&Emote.FLAG_USED) > 0) {
			setPartOffsetToTwo(biped.bipedRightArm, biped.bipedRightArmwear, Emote.RIGHT_ARM, states, config);
		}
		if((partUsages[2*Emote.RIGHT_ARM + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.RIGHT_ARM + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedRightArm, Emote.RIGHT_ARM, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedRightArmwear, Emote.RIGHT_ARM, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedRightArm, Emote.RIGHT_ARM, states);
				setPartRotate(biped.bipedRightArmwear, Emote.RIGHT_ARM, states);
			}
		}
		if((partUsages[2*Emote.LEFT_ARM]&Emote.FLAG_USED) > 0) {
			setPartOffsetToTwo(biped.bipedLeftArm, biped.bipedLeftArmwear, Emote.LEFT_ARM, states, config);
		}
		if((partUsages[2*Emote.LEFT_ARM + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.LEFT_ARM + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedLeftArm, Emote.LEFT_ARM, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedLeftArmwear, Emote.LEFT_ARM, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedLeftArm, Emote.LEFT_ARM, states);
				setPartRotate(biped.bipedLeftArmwear, Emote.LEFT_ARM, states);
			}
		}
		if((partUsages[2*Emote.RIGHT_LEG]&Emote.FLAG_USED) > 0) {
			setPartOffsetToTwo(biped.bipedRightLeg, biped.bipedRightLegwear, Emote.RIGHT_LEG, states, config);
		}
		if((partUsages[2*Emote.RIGHT_LEG + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.RIGHT_LEG + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedRightLeg, Emote.RIGHT_LEG, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedRightLegwear, Emote.RIGHT_LEG, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
				setPartRotate(biped.bipedRightLegwear, Emote.RIGHT_LEG, states);
			}
		}
		if((partUsages[2*Emote.LEFT_LEG]&Emote.FLAG_USED) > 0) {
			setPartOffsetToTwo(biped.bipedLeftLeg, biped.bipedLeftLegwear, Emote.LEFT_LEG, states, config);
		}
		if((partUsages[2*Emote.LEFT_LEG + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.LEFT_LEG + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedLeftLeg, Emote.LEFT_LEG, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedLeftLegwear, Emote.LEFT_LEG, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
				setPartRotate(biped.bipedLeftLegwear, Emote.LEFT_LEG, states);
			}
		}

		if((partUsages[2*Emote.BODY]&Emote.FLAG_USED) > 0 || (partUsages[2*Emote.BODY + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.BODY + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartOffsetToTwo(biped.bipedBody, biped.bipedBodyWear, Emote.BODY, states, config);
				setPartRotateBoundToHead(biped.bipedBody, Emote.BODY, states, netHeadYaw, headPitch);
				setPartRotateBoundToHead(biped.bipedBodyWear, Emote.BODY, states, netHeadYaw, headPitch);
			} else {
				setPartOffsetToTwo(biped.bipedBody, biped.bipedBodyWear, Emote.BODY, states, config);
				setPartRotate(biped.bipedBody, Emote.BODY, states);
				setPartRotate(biped.bipedBodyWear, Emote.BODY, states);
			}
		}
		if((partUsages[2*Emote.MODEL]&Emote.FLAG_USED) > 0 || (partUsages[2*Emote.MODEL + 1]&Emote.FLAG_USED) > 0) {
			int id = Emote.AXIS_COUNT*Emote.MODEL;
			float offsetX = states[id + Emote.OFF_X];
			float offsetY = states[id + Emote.OFF_Y];
			float offsetZ = states[id + Emote.OFF_Z];
			float rotX = states[id + Emote.ROT_X];
			float rotY = states[id + Emote.ROT_Y];
			float rotZ = states[id + Emote.ROT_Z];
			if((partUsages[2*Emote.MODEL + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				rotX += headPitch;
				rotY += netHeadYaw;
			}

			GlStateManager.translate(0, playerHeight / 2, 0);

			config = data.leg1;
			GlStateManager.translate(offsetX/playerHeight*config.scaleX, offsetY/playerHeight*config.scaleY, offsetZ/playerHeight*config.scaleZ);
			// GlStateManager.translate(offsetX/2, offsetY/2, offsetZ/2);

			if (rotY != 0) GlStateManager.rotate(rotY * 90.0F/(float)Math.PI, 0, 1, 0);
			if (rotX != 0) GlStateManager.rotate(rotX * 90.0F/(float)Math.PI, 1, 0, 0);
			if (rotZ != 0) GlStateManager.rotate(rotZ * 90.0F/(float)Math.PI, 0, 0, 1);

			GlStateManager.translate(0, -playerHeight / 2, 0);
		}
	}
	public static final void animModelBipedFromStates(ModelData data, ModelBiped biped, float[] states, int[] partUsages, float netHeadYaw, float headPitch) {
		netHeadYaw /= 57.295776F;
		headPitch /= 57.295776F;
		ModelPartConfig config = data.body;
		if((partUsages[2*Emote.HEAD]&Emote.FLAG_USED) > 0) {
			setPartOffset(biped.bipedHead, Emote.HEAD, states, config);
		}
		if((partUsages[2*Emote.HEAD + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.HEAD + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedHead, Emote.HEAD, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedHead, Emote.HEAD, states);
			}
		}
		if((partUsages[2*Emote.RIGHT_ARM]&Emote.FLAG_USED) > 0) {
			setPartOffset(biped.bipedRightArm, Emote.RIGHT_ARM, states, config);
		}
		if((partUsages[2*Emote.RIGHT_ARM + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.RIGHT_ARM + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedRightArm, Emote.RIGHT_ARM, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedRightArm, Emote.RIGHT_ARM, states);
			}
		}
		if((partUsages[2*Emote.LEFT_ARM]&Emote.FLAG_USED) > 0) {
			setPartOffset(biped.bipedLeftArm, Emote.LEFT_ARM, states, config);
		}
		if((partUsages[2*Emote.LEFT_ARM + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.LEFT_ARM + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedLeftArm, Emote.LEFT_ARM, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedLeftArm, Emote.LEFT_ARM, states);
			}
		}
		if((partUsages[2*Emote.RIGHT_LEG]&Emote.FLAG_USED) > 0) {
			setPartOffset(biped.bipedRightLeg, Emote.RIGHT_LEG, states, config);
		}
		if((partUsages[2*Emote.RIGHT_LEG + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.RIGHT_LEG + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedRightLeg, Emote.RIGHT_LEG, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedRightLeg, Emote.RIGHT_LEG, states);
			}
		}
		if((partUsages[2*Emote.LEFT_LEG]&Emote.FLAG_USED) > 0) {
			setPartOffset(biped.bipedLeftLeg, Emote.LEFT_LEG, states, config);
		}
		if((partUsages[2*Emote.LEFT_LEG + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.LEFT_LEG + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartRotateBoundToHead(biped.bipedLeftLeg, Emote.LEFT_LEG, states, netHeadYaw, headPitch);
			} else {
				setPartRotate(biped.bipedLeftLeg, Emote.LEFT_LEG, states);
			}
		}

		if((partUsages[2*Emote.BODY]&Emote.FLAG_USED) > 0 || (partUsages[2*Emote.BODY + 1]&Emote.FLAG_USED) > 0) {
			if((partUsages[2*Emote.BODY + 1]&Emote.FLAG_FOLLOWS_HEAD_ROTATION) > 0) {
				setPartOffset(biped.bipedBody, Emote.BODY, states, config);
				setPartRotateBoundToHead(biped.bipedBody, Emote.BODY, states, netHeadYaw, headPitch);
			} else {
				setPartOffset(biped.bipedBody, Emote.BODY, states, config);
				setPartRotate(biped.bipedBody, Emote.BODY, states);
			}
		}
	}
	public static final void setPartOffset(ModelRenderer part, int partId, float[] states, ModelPartConfig config) {
		partId *= 6;
		part.offsetX = states[partId + Emote.OFF_X]*config.scaleX;
		part.offsetY = states[partId + Emote.OFF_Y]*config.scaleY;
		part.offsetZ = states[partId + Emote.OFF_Z]*config.scaleZ;
	}
	public static final void setPartOffsetToTwo(ModelRenderer part0, ModelRenderer part1, int partId, float[] states, ModelPartConfig config) {
		partId *= 6;
		float offsetX = states[partId + Emote.OFF_X]*config.scaleX;
		float offsetY = states[partId + Emote.OFF_Y]*config.scaleY;
		float offsetZ = states[partId + Emote.OFF_Z]*config.scaleZ;
		part0.offsetX = offsetX;
		part0.offsetY = offsetY;
		part0.offsetZ = offsetZ;
		part1.offsetX = offsetX;
		part1.offsetY = offsetY;
		part1.offsetZ = offsetZ;
	}
	public static final void setPartRotate(ModelRenderer part, int partId, float[] states) {
		partId *= 6;
		part.rotateAngleX = states[partId + Emote.ROT_X];
		part.rotateAngleY = states[partId + Emote.ROT_Y];
		part.rotateAngleZ = states[partId + Emote.ROT_Z];
	}
	public static final void setPartRotateBoundToHead(ModelRenderer part, int partId, float[] states, float netHeadYaw, float headPitch) {
		partId *= 6;
		part.rotateAngleX = states[partId + Emote.ROT_X] + headPitch;
		part.rotateAngleY = states[partId + Emote.ROT_Y] + netHeadYaw;
		part.rotateAngleZ = states[partId + Emote.ROT_Z];
	}

	public static final void resetModelPlayerForEmote(ModelPlayer biped) {
		ModelRenderer part;
		part = biped.bipedHead;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedBody;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftArm;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightArm;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftLeg;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightLeg;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedHeadwear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedBodyWear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftArmwear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightArmwear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftLegwear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightLegwear;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = getEarsModel(biped);
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;
	}
	public static final void resetModelBipedForEmote(ModelBiped biped) {
		ModelRenderer part;

		part = biped.bipedHead;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedBody;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftArm;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightArm;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedLeftLeg;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;

		part = biped.bipedRightLeg;
		if(part != null) part.rotateAngleX = part.rotateAngleY = part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;
	}
}
