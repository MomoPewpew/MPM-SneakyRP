package noppes.mpm.commands;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandProp extends MpmCommandInterface {

	private final List<String> clearStrings = new ArrayList<String>(Arrays.asList(
		"clear",
		"reset",
		"cl"
	));

	private final List<String> deleteStrings = new ArrayList<String>(Arrays.asList(
		"undo",
		"remove",
		"delete",
		"del",
		"rem"
	));

	private final List<String> guiStrings = new ArrayList<String>(Arrays.asList(
		"gui",
		"interface",
		"ui",
		"options"
	));

	private final List<String> giveStrings = new ArrayList<String>(Arrays.asList(
		"give"
	));

	private final List<String> nameStrings = new ArrayList<String>(Arrays.asList(
		"label",
		"name"
	));

	private final List<String> hideStrings = new ArrayList<String>(Arrays.asList(
		"hide"
	));

	private final List<String> showStrings = new ArrayList<String>(Arrays.asList(
		"show"
	));

	private final List<String> toggleStrings = new ArrayList<String>(Arrays.asList(
		"toggle"
	));

	private final List<String> groupStrings = new ArrayList<String>(Arrays.asList(
		"group"
	));

	@Override
	public String getCommandName() {
		return "prop";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(icommandsender instanceof EntityPlayerMP == false)
		return;

		if (args.length == 0 || (args.length > 0 && guiStrings.contains(args[0]))) {
			Server.sendData((EntityPlayerMP) icommandsender, EnumPackets.PROP_GUI_OPEN);
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		ModelData data = ModelData.get(player);

		String bodyPartString = (args.length > 1) ? args[1].toLowerCase().replace("_", "") : "0";

		if (args.length > 0) {
			if (clearStrings.contains(args[0])) {
				data.clearPropsServer();
				return;
			} else if (deleteStrings.contains(args[0])) {
				if (args.length == 1) {
					data.propBase.removePropServer(data.propBase.props.size() - 1);
				} else {
					data.propBase.removePropServerByName(args[1]);
					data.removePropGroupServerByName(args[1]);
				}
				return;
			} else if (giveStrings.contains(args[0])) {
				EntityPlayerMP target = null;

				if (args.length > 2) {
					target = getPlayer(server, icommandsender, args[2]);
				}

				if (args.length == 0) {
					giveProp(target, (data.propBase.props.size() - 1), (EntityPlayerMP) icommandsender);
				} else if (args.length > 1) {
					for (int i = 0; i < data.propBase.props.size(); i++) {
						if (data.propBase.props.get(i).name.toLowerCase().equals(args[1].toLowerCase())) {
							giveProp(target, i, (EntityPlayerMP) icommandsender);
						}
					}

					for (int i = 0; i < data.propGroups.size(); i++) {
						if (data.propGroups.get(i).name.toLowerCase().equals(args[1].toLowerCase())) {
							givePropGroup(target, i, (EntityPlayerMP) icommandsender);
						}
					}
				}
				return;
			} else if (hideStrings.contains(args[0])) {
				if (args.length == 1) {
					data.propBase.hidePropServer(data.propBase.props.size() - 1);
				} else {
					data.propBase.hidePropServerByName(args[1]);
					data.hidePropGroupServerByName(args[1]);
				}
				return;
			} else if (showStrings.contains(args[0])) {
				if (args.length == 1) {
					data.propBase.showPropServer(data.propBase.props.size() - 1);
				} else {
					data.propBase.showPropServerByName(args[1]);
					data.showPropGroupServerByName(args[1]);
				}
				return;
			} else if (toggleStrings.contains(args[0])) {
				if (args.length == 1) {
					data.propBase.togglePropServer(data.propBase.props.size() - 1);
				} else {
					data.propBase.togglePropServerByName(args[1]);
					data.togglePropGroupServerByName(args[1]);
				}
				return;
			}

			if (args.length > 1) {
				if (nameStrings.contains(args[0])) {
					data.propBase.namePropServer(args[1]);
					return;
				} else if (groupStrings.contains(args[0])) {
					String filename = args[1] + ".dat";
					File file;

					File dir = null;
					dir = new File(dir, MorePlayerModels.assetRootFolder + File.separator + "propGroups");

					if (!dir.exists()) {
						return;
					}

					NBTTagCompound compound = new NBTTagCompound();

					try {
						file = new File(dir, filename);
						compound = !file.exists() ? null : CompressedStreamTools.readCompressed(new FileInputStream(file));

						PropGroup propGroup = new PropGroup((EntityPlayer) icommandsender);
						propGroup.readFromNBT(compound);

						data.addPropGroupServer(propGroup);
					} catch (Exception var4) {
						LogWriter.except(var4);
					}

					return;
				}
			}
		}

		Prop prop = null;

		String propString = (args.length > 0) ? args[0] : "minecraft:stained_glass:2";

		if (!propString.startsWith("particle:")) {
			String bodyPartName = bodyPartString;
			Float propScaleX = (args.length > 2) ? Float.valueOf(args[2]) : 1.0F;
			Float propScaleY = (args.length > 3) ? Float.valueOf(args[3]) : propScaleX;
			Float propScaleZ = (args.length > 4) ? Float.valueOf(args[4]) : propScaleX;
			Float propOffsetX = (args.length > 5) ? Float.valueOf(args[5]) : 0.0F;
			Float propOffsetY = (args.length > 6) ? Float.valueOf(args[6]) : 0.0F;
			Float propOffsetZ = (args.length > 7) ? Float.valueOf(args[7]) : 0.0F;
			Float propRotateX = (args.length > 8) ? Float.valueOf(args[8]) : 0.0F;
			Float propRotateY = (args.length > 9) ? Float.valueOf(args[9]) : 0.0F;
			Float propRotateZ = (args.length > 10) ? Float.valueOf(args[10]) : 0.0F;
			Boolean propMatchScaling = (args.length > 11) ? parseBoolean(args[11]) : true;
			Boolean propHide = (args.length > 12) ? parseBoolean(args[12]) : false;
			String propName = (args.length > 13) ? args[13] : "NONAME";
			Float ppOffsetX = (args.length > 14) ? Float.valueOf(args[14]) : 0.0F;
			Float ppOffsetY = (args.length > 15) ? Float.valueOf(args[15]) : 0.0F;
			Float ppOffsetZ = (args.length > 16) ? Float.valueOf(args[16]) : 0.0F;
			Boolean sheathProp = (args.length > 17) ? parseBoolean(args[17]) : false;

			prop = new Prop(propString, bodyPartName,
			propScaleX, propScaleY, propScaleZ,
			propOffsetX, propOffsetY, propOffsetZ,
			propRotateX, propRotateY, propRotateZ,
			propMatchScaling, propHide, propName,
			ppOffsetX, ppOffsetY, ppOffsetZ,
			sheathProp);
		} else {
			String bodyPartName = bodyPartString;
			Float propMotionScatter = (args.length > 2) ? Float.valueOf(args[2]) : 0.0F;
			Float propFrequency = (args.length > 3) ? Float.valueOf(args[3]) : 1.0F;
			int propAmount = (args.length > 4) ? Integer.valueOf(args[4]) : 1;
			Float propOffsetX = (args.length > 5) ? Float.valueOf(args[5]) : 0.0F;
			Float propOffsetY = (args.length > 6) ? Float.valueOf(args[6]) : 0.0F;
			Float propOffsetZ = (args.length > 7) ? Float.valueOf(args[7]) : 0.0F;
			Float propPitch = (args.length > 8) ? Float.valueOf(args[8]) : 0.0F;
			Float propYaw = (args.length > 9) ? Float.valueOf(args[9]) : 0.0F;
			Double propSpeed = (args.length > 10) ? Double.valueOf(args[10]) : 0.0F;
			Boolean propHide = (args.length > 11) ? parseBoolean(args[11]) : false;
			String propName = (args.length > 12) ? args[12] : "NONAME";
			Boolean lockrotation = (args.length > 13) ? parseBoolean(args[13]) : false;

			prop = new Prop(propString, bodyPartName,
			propMotionScatter, propFrequency, propAmount,
			propOffsetX, propOffsetY, propOffsetZ,
			propPitch, propYaw, propSpeed,
			propHide, propName, lockrotation);
		}


		if (!prop.parsePropString(propString))
		return;

		data.propBase.addPropServer(prop);
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/prop [<itemname>] [<bodypart>] [<scaleX>] [<scaleY>] [<scaleZ>] [<offsetX>] [<offsetY>] [<offsetZ>] [<rotateX>] [<rotateY>] [<rotateZ>] [Bodypart scaling <TRUE/FALSE>] [Hidden <TRUE/FALSE>] [<Name>]";
	}

	private static EntityPlayerMP getClosestPlayer(final EntityPlayerMP player) {
		EntityPlayerMP closest = null;
		double closestDistance = 0;

		for (EntityPlayerMP entity : player.getEntityWorld().getEntities(EntityPlayerMP.class, EntitySelectors.NOT_SPECTATING)) {
			if (entity == player || !(entity instanceof EntityPlayerMP)) {
				continue;
			}

			double distance = entity.getPosition().distanceSq(player.getPosition());
			if ((closest == null || distance < closestDistance) && Math.sqrt(distance) <= 3) {
				closest = entity;
				closestDistance = distance;
			}
		}

		return closest;
	}

	public static void giveProp(EntityPlayerMP target, Integer index, EntityPlayerMP sender) {
		ModelData data = ModelData.get(sender);

		if (index == null) index = data.propBase.props.size() - 1;
		if (target == null) target = getClosestPlayer(sender);

		if (data != null && target != null && index >= 0) {
			ModelData targetData = ModelData.get(target);

			targetData.propBase.addPropServer(data.propBase.props.get(index));

			data.propBase.hidePropServer(index);
		}
	}

	public static void givePropGroup(EntityPlayerMP target, Integer index, EntityPlayerMP sender) {
		ModelData data = ModelData.get(sender);

		if (index == null) index = data.propGroups.size() - 1;
		if (target == null) target = getClosestPlayer(sender);

		if (data != null && target != null && index >= 0) {
			ModelData targetData = ModelData.get(target);

			targetData.addPropGroupServer(data.propGroups.get(index));

			data.hidePropGroupServer(index);
		}
	}

}
