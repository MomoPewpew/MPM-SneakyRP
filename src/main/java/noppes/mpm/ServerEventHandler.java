package noppes.mpm;

import net.minecraft.entity.Entity;
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
import net.minecraftforge.event.entity.player.PlayerEvent;
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

  @SubscribeEvent
  public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event) {
    if (!(event.getEntity() instanceof EntityPlayer) || event.getSound() == null || event.getSound() != SoundEvents.field_187800_eb)
      return;
    EntityPlayer player = (EntityPlayer)event.getEntity();
    ModelData data = ModelData.get(player);
    if (data == null)
      return;
    if (data.soundType == 0)
      return;
    ResourceLocation sound = null;
    if (player.func_110143_aJ() <= 1.0F || player.field_70128_L) {
      if (data.soundType == 1) {
        sound = female_death;
      } else if (data.soundType == 2) {
        sound = male_death;
      } else if (data.soundType == 3) {
        sound = goblin_death;
      }
    } else if (data.soundType == 1) {
      sound = female_hurt;
    } else if (data.soundType == 2) {
      sound = male_hurt;
    } else if (data.soundType == 3) {
      sound = goblin_hurt;
    }
    if (sound != null)
      event.setSound(new SoundEvent(sound));
  }

  @SubscribeEvent
  public void chat(ServerChatEvent event) {
    Server.sendToAll(event.getPlayer().func_184102_h(), EnumPackets.CHAT_EVENT, new Object[] { event.getPlayer().func_110124_au(), event.getMessage() });
    ModelData data = ModelData.get((EntityPlayer)event.getPlayer());
    if (!data.displayFormat.isEmpty() && event.getComponent() instanceof TextComponentTranslation) {
      Object[] obs = ((TextComponentTranslation)event.getComponent()).func_150271_j();
      if (obs.length > 0) {
        ITextComponent comp = (ITextComponent)obs[0];
        if (comp.func_150253_a().size() > 0 && comp.func_150253_a().get(0) instanceof TextComponentString) {
          TextComponentString com = comp.func_150253_a().get(0);
          if (com.func_150260_c().equals(event.getPlayer().func_145748_c_().func_150260_c())) {
            comp.func_150253_a().remove(0);
            comp.func_150253_a().add(0, new TextComponentString(data.displayFormat + event.getPlayer().func_145748_c_().func_150260_c()));
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onAttack(LivingAttackEvent event) {
    if ((event.getEntityLiving()).field_70170_p.field_72995_K || event.getAmount() < 1.0F || !(event.getSource()).field_76373_n.equals("player") || !(event.getSource().func_76346_g() instanceof EntityPlayer))
      return;
    EntityPlayer player = (EntityPlayer)event.getSource().func_76346_g();
    boolean flag = (player.field_70143_R > 0.0F && !player.field_70122_E && !player.func_70617_f_() && !player.func_70090_H() && !player.func_70644_a(MobEffects.field_76440_q) && player.func_184187_bx() == null);
    if (!flag || event.getEntityLiving().func_110143_aJ() < 0.0F || player.field_70172_ad > player.field_70771_an / 2.0F)
      return;
    ModelData data = ModelData.get(player);
    if (data == null)
      return;
    String sound = "";
    if (data.soundType == 1) {
      sound = "moreplayermodels:human.female.attack";
    } else if (data.soundType == 2) {
      sound = "moreplayermodels:human.male.attack";
    } else if (data.soundType == 3) {
      sound = "moreplayermodels:goblin.male.attack";
    } else {
      return;
    }
    float pitch = (player.func_70681_au().nextFloat() - player.func_70681_au().nextFloat()) * 0.2F + 1.0F;
    player.field_70170_p.func_184133_a(player, player.func_180425_c(), new SoundEvent(new ResourceLocation(sound)), SoundCategory.PLAYERS, 0.9876543F, pitch);
  }

  @SubscribeEvent
  public void playerTracking(PlayerEvent.StartTracking event) {
    if (!(event.getTarget() instanceof EntityPlayer))
      return;
    EntityPlayer target = (EntityPlayer)event.getTarget();
    EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
    ModelData data = ModelData.get(target);
    Server.sendDelayedData(player, EnumPackets.SEND_PLAYER_DATA, 100, new Object[] { target.func_110124_au(), data.writeToNBT() });
    ItemStack back = (ItemStack)player.field_71071_by.field_70462_a.get(0);
    if (!back.func_190926_b()) {
      Server.sendDelayedData(player, EnumPackets.BACK_ITEM_UPDATE, 100, new Object[] { target.func_110124_au(), back.func_77955_b(new NBTTagCompound()) });
    } else {
      Server.sendDelayedData(player, EnumPackets.BACK_ITEM_REMOVE, 100, new Object[] { target.func_110124_au() });
    }
  }

  @SubscribeEvent
  public void onNameSet(PlayerEvent.NameFormat event) {
    ModelData data = ModelData.get(event.getEntityPlayer());
    if (!data.displayName.isEmpty())
      event.setDisplayname(data.displayName);
  }

  private static final ResourceLocation key = new ResourceLocation("moreplayermodels", "modeldata");

  @SubscribeEvent
  public void attach(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof EntityPlayer)
      event.addCapability(key, new ModelData());
  }
}
