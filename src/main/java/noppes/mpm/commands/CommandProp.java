package noppes.mpm.commands;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import noppes.mpm.ModelData;
import noppes.mpm.Prop;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class CommandProp extends MpmCommandInterface {

	private final List<String> listBodyParts = Lists.newArrayList(
	     "hat",
		 "head",
		 "model",
		 "body",
		 "torso",
		 "back",
		 "arm",
		 "hand",
		 "armleft",
		 "handleft",
		 "leftarm",
		 "lefthand",
		 "armright",
		 "handright",
		 "rightarm",
		 "righthand",
		 "leg",
		 "foot",
		 "legleft",
		 "footleft",
		 "leftleg",
		 "leftfoot",
		 "legright",
		 "footright",
		 "rightleg",
		 "rightfoot"
	);

	private final List<String> clearStrings = Lists.newArrayList(
	     "clear",
	     "reset",
	     "cl"
	);

	private final List<String> undoStrings = Lists.newArrayList(
	     "undo",
	     "remove",
	     "delete",
	     "del",
	     "rem"
	);

	private final List<String> guiStrings = Lists.newArrayList(
	     "gui",
	     "interface",
	     "ui",
	     "options"
	);

	private final List<String> giveStrings = Lists.newArrayList(
	     "give"
	);

	private final List<String> labelStrings = Lists.newArrayList(
		     "label"
		);

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

		if (args.length > 0 && giveStrings.contains(args[0])) {
			EntityPlayerMP target = null;
			Integer index = null;

			if (args.length > 1) {
				if (args[1].matches("^\\d+$")) {
					index = Integer.parseInt(args[1]) - 1;
				} else {
					target = getPlayer(server, icommandsender, args[1]);
				}
			}
			if (args.length > 2) {
				if (args[2].matches("^\\d+$")) {
					index = Integer.parseInt(args[2]) - 1;
				} else {
					target = getPlayer(server, icommandsender, args[2]);
				}
			}

			giveProp(target, index, (EntityPlayerMP) icommandsender);
			return;
		}

		if (args.length > 1 && labelStrings.contains(args[0])) {
			data.labelPropServer(args[1]);
			return;
		}

		String bodyPartString = (args.length > 1) ? args[1].toLowerCase().replace("_", "").replace("-", "") : "lefthand";

		if (args.length > 0 && clearStrings.contains(args[0])) {
			data.clearPropsServer();
			return;
		} else if (args.length > 0 && undoStrings.contains(args[0])) {
			if (args.length == 1 || args[1].matches("^\\d+$")) {
				Integer index = ((args.length > 1) ? Integer.valueOf(args[1]) - 1 : data.props.size() - 1);

				data.removePropServer(index);
			} else {
				data.removeLabelServer(args[1]);
			}
			return;
		}

		String propString = (args.length > 0) ? args[0] : "crafting_table";
		ItemStack propItemStack = (args.length > 0) ? new ItemStack(getItemByText(icommandsender, args[0])) : new ItemStack(Blocks.CRAFTING_TABLE);
		String bodyPartName = (listBodyParts.contains(bodyPartString)) ? bodyPartString : "lefthand";
		Float propScaleX = (args.length > 2) ? Float.valueOf(args[2]) : 1.0F;
		Float propScaleY = (args.length > 3) ? Float.valueOf(args[3]) : propScaleX;
		Float propScaleZ = (args.length > 4) ? Float.valueOf(args[4]) : propScaleX;
		Float propOffsetX = (args.length > 5) ? Float.valueOf(args[5]) : 0.0F;
		Float propOffsetY = (args.length > 6) ? Float.valueOf(args[6]) : 0.0F;
		Float propOffsetZ = (args.length > 7) ? Float.valueOf(args[7]) : 0.0F;
		Float propRotateX = (args.length > 8) ? Float.valueOf(args[8]) : 0.0F;
		Float propRotateY = (args.length > 9) ? Float.valueOf(args[9]) : 0.0F;
		Float propRotateZ = (args.length > 10) ? Float.valueOf(args[10]) : 0.0F;
		Boolean propMatchScaling = (args.length > 11) ? parseBoolean(args[11]) : false;
		Boolean propHide = (args.length > 12) ? parseBoolean(args[12]) : false;

		data.addPropServer(propString, propItemStack, bodyPartName,
				propScaleX, propScaleY, propScaleZ,
				propOffsetX, propOffsetY, propOffsetZ,
				propRotateX, propRotateY, propRotateZ,
				propMatchScaling, propHide);
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/prop [<itemname>] [<bodypart>] [<scaleX>] [<scaleY>] [<scaleZ>] [<offsetX>] [<offsetY>] [<offsetZ>] [<rotateX>] [<rotateY>] [<rotateZ>]";
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

		if (index == null) index = data.props.size() - 1;
		if (target == null) target = getClosestPlayer(sender);

		if (data != null && target != null && index >= 0) {
			ModelData targetData = ModelData.get(target);

			Prop prop = data.props.get(index);

			targetData.addPropServer(prop.propString, prop.itemStack, prop.bodyPartName,
					prop.scaleX, prop.scaleY, prop.scaleZ,
					prop.offsetX, prop.offsetY, prop.offsetZ,
					prop.rotateX, prop.rotateY, prop.rotateZ,
					prop.matchScaling, prop.hide);

			data.hidePropServer(index);
		}
	}

}