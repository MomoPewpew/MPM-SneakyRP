package noppes.mpm.commands;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
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
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumPackets;

public class CommandMPM extends MpmCommandInterface {
	private HashMap entities = new HashMap();
	private List sub = Arrays.asList("url", "name", "entity", "scale", "animation", "sendmodel", "enableentity", "disableentity");

	public CommandMPM() {
		Iterator var1 = ForgeRegistries.ENTITIES.getValues().iterator();

		while(var1.hasNext()) {
			EntityEntry ent = (EntityEntry)var1.next();
			String name = ent.getName();

			try {
				Class c = ent.getEntityClass();
				if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers())) {
					this.entities.put(name.toLowerCase(), c.asSubclass(EntityLivingBase.class));
				}
			} catch (SecurityException var5) {
				var5.printStackTrace();
			} catch (Exception var6) {
			}
		}

		this.entities.put("clear", (Object)null);
	}

	@Override
	public String getCommandName() {
		return "mpm";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new CommandException("Not enough arguments given", new Object[0]);
		} else {
			String type = args[0].toLowerCase();
			if (!this.sub.contains(type)) {
				throw new CommandException("Unknown subcommand", new Object[0]);
			} else {
				args = (String[])Arrays.copyOfRange(args, 1, args.length);
				EntityPlayer player = null;
				if (args.length > 1 && this.isPlayerOp(icommandsender)) {
					try {
						player = getPlayer(server, icommandsender, args[0]);
						args = (String[])Arrays.copyOfRange(args, 1, args.length);
					} catch (PlayerNotFoundException var7) {
					}
				}

				if (player == null && icommandsender instanceof EntityPlayer) {
					player = (EntityPlayer)icommandsender;
				}

				if (player == null) {
					throw new PlayerNotFoundException("commands.generic.player.notFound", new Object[]{icommandsender});
				} else {
					ModelData data = ModelData.get((EntityPlayer)player);
					if (type.equals("url")) {
						this.url((EntityPlayer)player, args, data);
					} else if (type.equals("scale")) {
						this.scale((EntityPlayer)player, args, data);
					} else if (type.equals("name")) {
						this.name((EntityPlayer)player, args, data);
					} else if (type.equals("entity")) {
						this.entity((EntityPlayer)player, args, data);
					} else if (type.equals("animation")) {
						this.animation((EntityPlayer)player, args, data);
					} else if (type.equals("sendmodel")) {
						this.sendmodel(server, (EntityPlayer)player, args, data);
					} else if (type.equals("enableentity")) {
						Server.sendDelayedData((EntityPlayerMP) icommandsender, EnumPackets.ENTITIES_ENABLE, 100);
						if (MorePlayerModels.playersEntityDenied.contains(((EntityPlayer) icommandsender).getUniqueID()))
						MorePlayerModels.playersEntityDenied.remove(((EntityPlayer) icommandsender).getUniqueID());
					} else if (type.equals("disableentity")) {
						Server.sendDelayedData((EntityPlayerMP) icommandsender, EnumPackets.ENTITIES_DISABLE, 100);
						if (!MorePlayerModels.playersEntityDenied.contains(((EntityPlayer) icommandsender).getUniqueID()))
						MorePlayerModels.playersEntityDenied.add(((EntityPlayer) icommandsender).getUniqueID());
					}
				}
			}
		}
	}

	private void animation(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
		if (args.length <= 0) {
			throw new WrongUsageException("/mpm animation [@p] <animation>", new Object[0]);
		} else {
			String type = args[0];
			EnumAnimation animation = null;
			EnumAnimation[] var6 = EnumAnimation.values();
			int var7 = var6.length;

			for(int var8 = 0; var8 < var7; ++var8) {
				EnumAnimation ani = var6[var8];
				if (ani.name().equalsIgnoreCase(type)) {
					animation = ani;
					break;
				}
			}

			if (animation == null) {
				throw new WrongUsageException("Unknown animation " + type, new Object[0]);
			} else {
				if (data.animation == animation) {
					animation = EnumAnimation.NONE;
				}

				Server.sendAssociatedData(player, EnumPackets.ANIMATION, player.getUniqueID(), animation);
				data.setAnimation(animation);
			}
		}
	}

	private void entity(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
		if (args.length <= 0) {
			throw new WrongUsageException("/mpm entity [@p] <entity> (to go back to default /mpm entity [@p] clear)", new Object[0]);
		} else {
			String arg = args[0].toLowerCase();
			if (!this.entities.containsKey(arg)) {
				throw new WrongUsageException("Unknown entity: " + args[0], new Object[0]);
			} else {
				data.setEntityClass((Class)this.entities.get(arg));
				int i = 1;
				if (args.length > i) {
					for(; i < args.length; ++i) {
						EntityLivingBase entity = data.getEntity(player);
						String[] split = args[i].split(":");
						if (split.length == 2) {
							data.setExtra(entity, split[0], split[1]);
						}
					}
				}

				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}
		}
	}

	private void name(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
		if (args.length <= 0) {
			throw new WrongUsageException("/mpm name [@p] <name>", new Object[0]);
		} else {
			if (args.length > 1 && args[0].startsWith("&")) {
				data.displayFormat = args[0].replace('&', Character.toChars(167)[0]);
				args = (String[])Arrays.copyOfRange(args, 1, args.length);
			}

			data.displayName = args[0];

			for(int i = 1; i < args.length; ++i) {
				data.displayName = data.displayName + " " + args[i];
			}

			data.displayName = data.displayName.replace('&', Character.toChars(167)[0]);
			if (data.displayName.equalsIgnoreCase("clear")) {
				data.displayName = "";
			}

			player.refreshDisplayName();
			Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
		}
	}

	private void url(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
		if (args.length <= 0) {
			throw new WrongUsageException("/mpm url [@p] <url> (to go back to default /mpm url [@p] clear)", new Object[0]);
		} else {
			String url = args[0];

			for(int i = 1; i < args.length; ++i) {
				url = url + " " + args[i];
			}

			if (url.equalsIgnoreCase("clear")) {
				url = "";
			}

			data.url = url;
			Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
		}
	}

	private void sendmodel(MinecraftServer server, EntityPlayer fromPlayer, String[] args, ModelData fromData) throws WrongUsageException {
		if (args.length < 1) {
			throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)", new Object[0]);
		} else {
			EntityPlayer toPlayer = null;
			ModelData toData = null;

			try {
				toPlayer = getPlayer(server, fromPlayer, args[0]);
			} catch (CommandException var8) {
			}

			if (toPlayer != null && toPlayer != fromPlayer) {
				toData = ModelData.get(toPlayer);
			} else {
				if (!args[0].equalsIgnoreCase("clear")) {
					throw new WrongUsageException("/mpm sendmodel [@from_player] <@to_player> (to go back to default /mpm sendmodel [@p] clear)", new Object[0]);
				}

				fromData = new ModelData();
			}

			NBTTagCompound compound = fromData.writeToNBT();
			toData.readFromNBT(compound);
			toData.save();
			Server.sendAssociatedData(toPlayer, EnumPackets.SEND_PLAYER_DATA, toPlayer.getUniqueID(), compound);
		}
	}

	private void scale(EntityPlayer player, String[] args, ModelData data) throws WrongUsageException {
		try {
			CommandMPM.Scale scale;
			if (args.length == 1) {
				scale = CommandMPM.Scale.Parse(args[0]);
				data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			} else {
				if (args.length != 4) {
					throw new WrongUsageException("/mpm scale [@p] [head x,y,z] [body x,y,z] [arms x,y,z] [legs x,y,z]. Examples: /mpm scale @p 1, /mpm scale @p 1 1 1 1, /mpm scale 1,1,1 1,1,1 1,1,1 1,1,1", new Object[0]);
				}

				scale = CommandMPM.Scale.Parse(args[0]);
				data.head.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				scale = CommandMPM.Scale.Parse(args[1]);
				data.body.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				scale = CommandMPM.Scale.Parse(args[2]);
				data.arm1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.arm2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				scale = CommandMPM.Scale.Parse(args[3]);
				data.leg1.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				data.leg2.setScale(scale.scaleX, scale.scaleY, scale.scaleZ);
				Server.sendAssociatedData(player, EnumPackets.SEND_PLAYER_DATA, player.getUniqueID(), data.writeToNBT());
			}

		} catch (NumberFormatException var5) {
			throw new WrongUsageException("None number given", new Object[0]);
		}
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/mpm <url/model/scale/name/animation> [@p]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List getTabCompletionOptions(MinecraftServer server, ICommandSender par1, String[] args, BlockPos pos) {
		if (args.length == 1)
		return CommandBase.getListOfStringsMatchingLastWord(args, this.sub);
		if (args.length >= 2) {
			String type = args[0].toLowerCase();
			List<String> list = new ArrayList<>();
			if (args.length == 2)
			list.addAll(Arrays.asList(server.getPlayerList().getAllUsernames()));
			if (type.equals("model"))
			list.addAll(this.entities.keySet());
			if (type.equals("animation"))
			for (EnumAnimation ani : EnumAnimation.values())
			list.add(ani.name().toLowerCase());
			return CommandBase.getListOfStringsMatchingLastWord(args, list);
		}
		return super.getTabCompletionOptions(server, par1, args, pos);
	}

	static class Scale {
		float scaleX;
		float scaleY;
		float scaleZ;

		private static CommandMPM.Scale Parse(String s) throws NumberFormatException {
			CommandMPM.Scale scale = new CommandMPM.Scale();
			if (s.contains(",")) {
				String[] split = s.split(",");
				if (split.length != 3) {
					throw new NumberFormatException("Not enough args given");
				}

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
