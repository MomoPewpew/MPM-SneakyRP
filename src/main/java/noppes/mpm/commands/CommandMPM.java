package noppes.mpm.commands;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.ModelData;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class CommandMPM extends MpmCommandInterface {
  private HashMap<String, Class<? extends EntityLivingBase>> entities = new HashMap<>();

  private List<String> sub = Arrays.asList(new String[] { "url", "name", "entity", "scale", "animation", "sendmodel" });

  public CommandMPM() {
    for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
      String name = ent.getName();
      try {
        Class<? extends Entity> c = ent.getEntityClass();
        if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(new Class[] { World.class }) != null && !Modifier.isAbstract(c.getModifiers()))
          this.entities.put(name.toLowerCase(), c.asSubclass(EntityLivingBase.class));
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (Exception exception) {}
    }
    this.entities.put("clear", null);
  }

  public String func_71517_b() {
    return "mpm";
  }

  public void func_184881_a(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
    EntityPlayerMP entityPlayerMP;
    EntityPlayer entityPlayer1;
    if (args.length < 1)
      throw new CommandException("Not enough arguments given", new Object[0]);
    String type = args[0].toLowerCase();
    if (!this.sub.contains(type))
      throw new CommandException("Unknown subcommand", new Object[0]);
    args = Arrays.<String>copyOfRange(args, 1, args.length);
    EntityPlayer player = null;
    if (args.length > 1 && isPlayerOp(icommandsender))
      try {
        entityPlayerMP = func_184888_a(server, icommandsender, args[0]);
        args = Arrays.<String>copyOfRange(args, 1, args.length);
      } catch (PlayerNotFoundException playerNotFoundException) {}
    if (entityPlayerMP == null && icommandsender instanceof EntityPlayer)
      entityPlayer1 = (EntityPlayer)icommandsender;
    if (entityPlayer1 == null)
      throw new PlayerNotFoundException("commands.generic.player.notFound", new Object[] { icommandsender });
    ModelData data = ModelData.get(entityPlayer1);
    if (type.equals("url")) {
      url(entityPlayer1, args, data);
    } else if (type.equals("scale")) {
      scale(entityPlayer1, args, data);
    } else if (type.equals("name")) {
      name(entityPlayer1, args, data);
    } else if (type.equals("entity")) {
      entity(entityPlayer1, args, data);
    } else if (type.equals("animation")) {
      animation(entityPlayer1, args, data);
    } else if (type.equals("sendmodel")) {
      sendmodel(server, entityPlayer1, args, data);
    }
  }

  private void animation(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
    if (args.length <= 0)
      throw new WrongUsageException("/mpm animation [@p] <animation>", new Object[0]);
    String type = args[0];
    EnumAnimation animation = null;
    for (EnumAnimation ani : EnumAnimation.values()) {
      if (ani.name().equalsIgnoreCase(type)) {
        animation = ani;
        break;
      }
    }
    if (animation == null)
      throw new WrongUsageException("Unknown animation " + type, new Object[0]);
    if (data.animation == animation)
      animation = EnumAnimation.NONE;
    Server.sendAssociatedData((Entity)player, EnumPackets.ANIMATION, new Object[] { player.getUniqueID(), animation });
    data.setAnimation(animation);
  }

  private void entity(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
    if (args.length <= 0)
      throw new WrongUsageException("/mpm entity [@p] <entity> (to go back to default /mpm entity [@p] clear)", new Object[0]);
    String arg = args[0].toLowerCase();
    if (!this.entities.containsKey(arg))
      throw new WrongUsageException("Unknown entity: " + args[0], new Object[0]);
    data.setEntityClass(this.entities.get(arg));
    int i = 1;
    if (args.length > i)
      while (i < args.length) {
        EntityLivingBase entity = data.getEntity(player);
        String[] split = args[i].split(":");
        if (split.length == 2)
          data.setExtra(entity, split[0], split[1]);
        i++;
      }
    Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.getUniqueID(), data.writeToNBT() });
  }

  private void name(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
    if (args.length <= 0)
      throw new WrongUsageException("/mpm name [@p] <name>", new Object[0]);
    if (args.length > 1 && args[0].startsWith("&")) {
      data.displayFormat = args[0].replace('&', Character.toChars(167)[0]);
      args = Arrays.<String>copyOfRange(args, 1, args.length);
    }
    data.displayName = args[0];
    for (int i = 1; i < args.length; i++)
      data.displayName += " " + args[i];
    data.displayName = data.displayName.replace('&', Character.toChars(167)[0]);
    if (data.displayName.equalsIgnoreCase("clear"))
      data.displayName = "";
    player.refreshDisplayName();
    Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.getUniqueID(), data.writeToNBT() });
  }

  private void url(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
    if (args.length <= 0)
      throw new WrongUsageException("/mpm url [@p] <url> (to go back to default /mpm url [@p] clear)", new Object[0]);
    String url = args[0];
    for (int i = 1; i < args.length; i++)
      url = url + " " + args[i];
    if (url.equalsIgnoreCase("clear"))
      url = "";
    data.url = url;
    Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.getUniqueID(), data.writeToNBT() });
  }

  private void sendmodel(MinecraftServer server, EntityPlayer fromPlayer, String[] args, ModelData fromData) throws WrongUsageException {
    EntityPlayerMP entityPlayerMP;
    if (args.length < 1)
      throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)", new Object[0]);
    EntityPlayer toPlayer = null;
    ModelData toData = null;
    try {
      entityPlayerMP = func_184888_a(server, (ICommandSender)fromPlayer, args[0]);
    } catch (CommandException commandException) {}
    if (entityPlayerMP == null || entityPlayerMP == fromPlayer) {
      if (args[0].equalsIgnoreCase("clear")) {
        fromData = new ModelData();
      } else {
        throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)", new Object[0]);
      }
    } else {
      toData = ModelData.get((EntityPlayer)entityPlayerMP);
    }
    NBTTagCompound compound = fromData.writeToNBT();
    toData.readFromNBT(compound);
    toData.save();
    Server.sendAssociatedData((Entity)entityPlayerMP, EnumPackets.SEND_PLAYER_DATA, new Object[] { entityPlayerMP.getUniqueID(), compound });
  }

  private void scale(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
    try {
      if (args.length == 1) {
        Scale scale = Scale.Parse(args[0]);
        data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.getUniqueID(), data.writeToNBT() });
      } else if (args.length == 4) {
        Scale scale = Scale.Parse(args[0]);
        data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        scale = Scale.Parse(args[1]);
        data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        scale = Scale.Parse(args[2]);
        data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        scale = Scale.Parse(args[3]);
        data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
        Server.sendAssociatedData((Entity)player, EnumPackets.SEND_PLAYER_DATA, new Object[] { player.getUniqueID(), data.writeToNBT() });
      } else {
        throw new WrongUsageException("/mpm scale [@p] [head x,y,z] [body x,y,z] [arms x,y,z] [legs x,y,z]. Examples: /mpm scale @p 1, /mpm scale @p 1 1 1 1, /mpm scale 1,1,1 1,1,1 1,1,1 1,1,1", new Object[0]);
      }
    } catch (NumberFormatException ex) {
      throw new WrongUsageException("None number given", new Object[0]);
    }
  }

  public String func_71518_a(ICommandSender sender) {
    return "/mpm <url/model/scale/name/animation> [@p]";
  }

  public int func_82362_a() {
    return 2;
  }

  public List func_184883_a(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
    if (args.length == 1)
      return CommandBase.func_175762_a(args, this.sub);
    if (args.length >= 2) {
      String type = args[0].toLowerCase();
      List<String> list = new ArrayList<>();
      if (args.length == 2)
        list.addAll(Arrays.asList(server.getPlayerList().func_72369_d()));
      if (type.equals("model"))
        list.addAll(this.entities.keySet());
      if (type.equals("animation"))
        for (EnumAnimation ani : EnumAnimation.values())
          list.add(ani.name().toLowerCase());
      return CommandBase.func_175762_a(args, list);
    }
    return super.func_184883_a(server, par1, args, pos);
  }

  static class Scale {
    float scaleX;

    float scaleY;

    float scaleZ;

    private static Scale Parse(String s) throws NumberFormatException {
      Scale scale = new Scale();
      if (s.contains(",")) {
        String[] split = s.split(",");
        if (split.length != 3)
          throw new NumberFormatException("Not enough args given");
        scale.scaleX = Float.parseFloat(split[0]);
        scale.scaleY = Float.parseFloat(split[1]);
        scale.scaleZ = Float.parseFloat(split[2]);
      } else {
        scale.scaleZ = scale.scaleY = scale.scaleX = Float.parseFloat(s);
      }
      return scale;
    }
  }
}
