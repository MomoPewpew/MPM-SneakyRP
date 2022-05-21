package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.elytradev.architecture.common.shape.Shape;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
import noppes.mpm.client.gui.util.GuiItemStackButton;
import noppes.mpm.client.gui.util.GuiNpcButton;

public class GuiCreationPropShapePicker extends GuiCreationScreenInterface {
	private Boolean initiating = false;
	private static Prop prop;
	private static final Integer rowAmount = 7;
	private static final Integer columnAmount = 14;
	private static PropGroup propGroupOld;
	private static int selectedOld;
	private static List<String> list = new ArrayList<String>();

	public GuiCreationPropShapePicker(Prop propArg, PropGroup propGroup, int selected, String propStringOld) {
		this.active = -1;
		this.xOffset = 140;
		prop = propArg;
		propGroupOld = propGroup;
		selectedOld = selected;
		this.closeOnInventory = false;

		list.clear();
		list.add(propStringOld);
		for (Shape shape : Shape.values()) {
			list.add("shape:" + shape.toString().toLowerCase() + ":" + propStringOld);
		}
	}

	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		Iterator<String> var1 = list.iterator();
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();

		while(var1.hasNext()) {
			String ent = var1.next();

			itemStacks.add(Prop.parseItemStack(ent));
		}

		this.addButton(new GuiNpcButton(902, this.guiLeft + 182, this.guiTop + 210, 45, 20, "gui.confirm"));

		for (int row = 0; row < Integer.min(rowAmount, (int) Math.ceil((((double) itemStacks.size())) / columnAmount)); row++) {
			for (int column = 0; column < Integer.min(columnAmount, (itemStacks.size() - row * columnAmount)); column++) {

				ItemStack itemStack = itemStacks.get(row * columnAmount + column);

				this.addButton(new GuiItemStackButton((1000 + row * columnAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, "", itemStack));
			}
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		if (this.initiating) return;
		super.actionPerformed(btn);

		if (btn.id >= 1000) {
			prop.propString = list.get(btn.id - 1000);
			prop.parsePropString(prop.propString);
		} else if (btn.id == 902) {
			this.openGui(new GuiCreationProps(propGroupOld, selectedOld));
		}
	}
}
