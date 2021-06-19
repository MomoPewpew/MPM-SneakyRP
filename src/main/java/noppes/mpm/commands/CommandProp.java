package noppes.mpm.commands;

import java.util.List;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import noppes.mpm.ModelData;
import noppes.mpm.client.gui.GuiCreationProps;
import noppes.mpm.client.gui.GuiMPM;
import noppes.mpm.client.gui.util.GuiNPCInterface;

public class CommandProp extends MpmCommandInterface {

	private final List<String> listBodyParts = Lists.newArrayList(
	     "hat",
		 "head",
		 "model",
		 "body",
		 "torso",
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
	     "reset"
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

	@Override
	public String getCommandName() {
		return "prop";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
		if(icommandsender instanceof EntityPlayerMP == false)
			return;

		if (args.length == 0 || (args.length > 0 && guiStrings.contains(args[0]))) {

			GuiMPM guiMPM = new GuiMPM();
			Minecraft.getMinecraft().displayGuiScreen(guiMPM);
			try {
				guiMPM.setSubGui((GuiNPCInterface)GuiCreationProps.GuiProps.getClass().newInstance());
			} catch (IllegalAccessException | InstantiationException e) {

			}
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) icommandsender;
		ModelData data = ModelData.get(player);

		String bodyPartString = (args.length > 1) ? args[1].toLowerCase().replace("_", "").replace("-", "") : "lefthand";

		if (args.length > 0 && clearStrings.contains(args[0])) {
			data.clearProps();
			return;
		} else if (args.length > 0 && undoStrings.contains(args[0])) {
			Integer index = ((args.length > 1) ? Integer.valueOf(args[1]) - 1 : data.propItemStack.size() - 1);

			data.removeProp(index);
			return;
		}

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

		data.addProp(propItemStack, bodyPartName, propScaleX, propScaleY, propScaleZ, propOffsetX, propOffsetY, propOffsetZ, propRotateX, propRotateY, propRotateZ);
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/prop [<itemname>] [<bodypart>] [<scaleX>] [<scaleY>] [<scaleZ>] [<offsetX>] [<offsetY>] [<offsetZ>] [<rotateX>] [<rotateY>] [<rotateZ>]";
	}

}