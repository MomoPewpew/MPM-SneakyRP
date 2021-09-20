package noppes.mpm;

import java.util.UUID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import noppes.mpm.constants.EnumPackets;

public class ServerEventHandler {
	private static final ResourceLocation female_death = new ResourceLocation("moreplayermodels:human.female.death");
	private static final ResourceLocation female_hurt = new ResourceLocation("moreplayermodels:human.female.hurt");
	private static final ResourceLocation female_attack = new ResourceLocation("moreplayermodels:human.female.attack");
	private static final ResourceLocation male_death = new ResourceLocation("moreplayermodels:human.male.death");
	private static final ResourceLocation male_hurt = new ResourceLocation("moreplayermodels:human.male.hurt");
	private static final ResourceLocation male_attack = new ResourceLocation("moreplayermodels:human.male.attack");
	private static final ResourceLocation goblin_death = new ResourceLocation("moreplayermodels:goblin.male.death");
	private static final ResourceLocation goblin_hurt = new ResourceLocation("moreplayermodels:goblin.male.hurt");
	private static final ResourceLocation goblin_attack = new ResourceLocation("moreplayermodels:goblin.male.attack");
	private static final ResourceLocation key = new ResourceLocation("moreplayermodels", "modeldata");

	@SubscribeEvent
	public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
		if (event.getEntity() instanceof EntityPlayer && event.getSound() != null && event.getSound() == SoundEvents.ENTITY_PLAYER_HURT) {
			EntityPlayer player = (EntityPlayer)event.getEntity();
			ModelData data = ModelData.get(player);
			if (data != null) {
				if (data.soundType != 0) {
					ResourceLocation sound = null;
					if (player.getHealth() > 1.0F && !player.isDead) {
						if (data.soundType == 1) {
							sound = female_hurt;
						} else if (data.soundType == 2) {
							sound = male_hurt;
						} else if (data.soundType == 3) {
							sound = goblin_hurt;
						}
					} else if (data.soundType == 1) {
						sound = female_death;
					} else if (data.soundType == 2) {
						sound = male_death;
					} else if (data.soundType == 3) {
						sound = goblin_death;
					}

					if (sound != null) {
						event.setSound(new SoundEvent(sound));
					}

				}
			}
		}
	}

	@SubscribeEvent
	public void chat(ServerChatEvent event) {
		Server.sendToAll(event.getPlayer().getServer(), EnumPackets.CHAT_EVENT, event.getPlayer().getUniqueID(), event.getMessage());
		ModelData data = ModelData.get(event.getPlayer());
		if (!data.displayFormat.isEmpty() && event.getComponent() instanceof TextComponentTranslation) {
			Object[] obs = ((TextComponentTranslation)event.getComponent()).getFormatArgs();
			if (obs.length > 0) {
				ITextComponent comp = (ITextComponent)obs[0];
				if (comp.getSiblings().size() > 0 && comp.getSiblings().get(0) instanceof TextComponentString) {
					TextComponentString com = (TextComponentString)comp.getSiblings().get(0);
					if (com.getUnformattedText().equals(event.getPlayer().getDisplayName().getUnformattedText())) {
						comp.getSiblings().remove(0);
						comp.getSiblings().add(0, new TextComponentString(data.displayFormat + event.getPlayer().getDisplayName().getUnformattedText()));
					}
				}
			}
		}

	}

	@SubscribeEvent
	public void onAttack(LivingAttackEvent event) {
		if (!event.getEntityLiving().worldObj.isRemote && event.getAmount() >= 1.0F && event.getSource().damageType.equals("player") && event.getSource().getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.getSource().getSourceOfDamage();
			boolean flag = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && player.getRidingEntity() == null;
			if (flag && event.getEntityLiving().getHealth() >= 0.0F && (float)player.hurtResistantTime <= (float)player.maxHurtResistantTime / 2.0F) {
				ModelData data = ModelData.get(player);
				if (data != null) {
					String sound = "";
					if (data.soundType == 1) {
						sound = "moreplayermodels:human.female.attack";
					} else if (data.soundType == 2) {
						sound = "moreplayermodels:human.male.attack";
					} else {
						if (data.soundType != 3) {
							return;
						}

						sound = "moreplayermodels:goblin.male.attack";
					}

					float pitch = (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.2F + 1.0F;
					player.worldObj.playSound(player, player.getPosition(), new SoundEvent(new ResourceLocation(sound)), SoundCategory.PLAYERS, 0.9876543F, pitch);
				}
			}
		}
	}

	@SubscribeEvent
	public void playerTracking(StartTracking event) {//player target has moved close enough to player player to start needing their model data
		if (event.getTarget() instanceof EntityPlayer) {
			EntityPlayer target = (EntityPlayer)event.getTarget();
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
			ModelData data = ModelData.get(target);
			Server.sendDelayedData(player, EnumPackets.SEND_PLAYER_DATA, 100, target.getUniqueID(), data.writeToNBT());
			ItemStack back = (ItemStack)player.inventory.mainInventory.get(0);
			if (back != null) {
				Server.sendDelayedData(player, EnumPackets.BACK_ITEM_UPDATE, 100, target.getUniqueID(), back.writeToNBT(new NBTTagCompound()));
			} else {
				Server.sendDelayedData(player, EnumPackets.BACK_ITEM_REMOVE, 100, target.getUniqueID());
			}
			//transfer emotes
			//TODO: write a custom, optimized serverside emote update procedure
			data.updateEmote();
			if(data.emoteIsPlaying) {
				ByteBuf sendBuffer = Unpooled.buffer();
				try {
					sendBuffer.writeInt(EnumPackets.EMOTE_DATA.ordinal());

					UUID uuid = target.getUniqueID();
					sendBuffer.writeLong(uuid.getMostSignificantBits());
					sendBuffer.writeLong(uuid.getLeastSignificantBits());

					Emote.writeEmoteV2(sendBuffer, data.emoteCommands, data.emotePartUsages);
					for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) sendBuffer.writeFloat(data.emoteSpeeds[i]);
					for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) sendBuffer.writeInt((data.emoteCommandIndices[i] << 2)|(data.emoteCommandSections[i]));
					for(int i = 0; i < 2*Emote.PART_COUNT; i += 1) sendBuffer.writeFloat(data.emoteCommandTimes[i]);

					Server.sendDelayedData(player, 100, sendBuffer);
				} catch(Exception e) {//I do not like exceptions
					sendBuffer.release();
				}
			}
		}
	}

	@SubscribeEvent
	public void onNameSet(NameFormat event) {
		ModelData data = ModelData.get(event.getEntityPlayer());
		if (!data.displayName.isEmpty()) {
			event.setDisplayname(data.displayName);
		}
	}

	@SubscribeEvent
	public void attach(AttachCapabilitiesEvent event) {
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(key, new ModelData());
		}
	}
}
