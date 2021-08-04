package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import noppes.mpm.Prop;
import noppes.mpm.PropGroup;
import noppes.mpm.client.gui.util.GuiItemStackButton;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropPicker extends GuiCreationScreenInterface implements ITextfieldListener {
	private Boolean initiating = false;
	private static Prop prop;
	private static int tab;
	private static String searchString;
	private static final Integer rowAmount = 7;
	private static final Integer columnAmount = 14;
	private static Set<Entry<Float, ItemStack>> mappings;
	private static PropGroup propGroupOld;
	private static int selectedOld;

	public GuiCreationPropPicker(Prop propArg, PropGroup propGroup, int selected) {
		this.active = -1;
		this.xOffset = 140;
		prop = propArg;
		propGroupOld = propGroup;
		selectedOld = selected;
		tab = 0;
		searchString = "";
		this.closeOnInventory = false;

		TreeMap<Float, ItemStack> map = new TreeMap<>();

		for (CreativeTabs creativeTab : CreativeTabs.CREATIVE_TAB_ARRAY) {
			if (creativeTab == CreativeTabs.INVENTORY) {
				continue;
			}
			NonNullList<ItemStack> creativeTabItemStacks = NonNullList.func_191196_a();
			try {
				creativeTab.displayAllRelevantItems(creativeTabItemStacks);
			} catch (RuntimeException | LinkageError e) {
			}
			for (ItemStack itemStack : creativeTabItemStacks) {
				if (itemStack == null) {
				} else if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
				} else {
					map.put((((float) (itemStack.getItem().getIdFromItem(itemStack.getItem()))) + (((float) itemStack.getItemDamage()) / 1000)), itemStack);
				}
			}
		}

		mappings = map.entrySet();
	}

	@Override
	public void initGui() {
		this.initiating = true;
		super.initGui();

		List<ItemStack> list = new ArrayList<ItemStack>();

		for(Entry<Float, ItemStack> mapping : mappings){
			list.add(mapping.getValue());
		}

		Iterator<ItemStack> var1 = list.iterator();
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();

		while(var1.hasNext()) {
			ItemStack ent = (ItemStack)var1.next();

			if (ent.getDisplayName().equals("Air")) continue;

			if ((!searchString.startsWith("@") && ent.getDisplayName().toLowerCase().contains(searchString))
			|| (searchString.startsWith("@") && ent.getItem().getRegistryName().toString().contains(new String(searchString).replace("@", "")))) {
				itemStacks.add(ent);
			}
		}

		this.addButton(new GuiNpcButton(903, this.guiLeft + 108, this.guiTop + 189, 20, 20, "<"));
		this.addLabel(new GuiNpcLabel(903, String.valueOf(tab), this.guiLeft + 136, this.guiTop + 195, 16777215));
		this.addButton(new GuiNpcButton(904, this.guiLeft + 148, this.guiTop + 189, 20, 20, ">"));

		if (tab == 0) this.getButton(903).enabled = false;
		if ((tab + 1) * 100 >= itemStacks.size()) this.getButton(904).enabled = false;

		this.addTextField(new GuiNpcTextField(901, this, this.guiLeft + 50, this.guiTop + 211, 130, 18, searchString.equals("") ? "Search" : searchString));
		this.addButton(new GuiNpcButton(902, this.guiLeft + 182, this.guiTop + 210, 45, 20, "gui.confirm"));

		for (int row = 0; row < Integer.min(rowAmount, (int) Math.ceil((((double) itemStacks.size() - tab * rowAmount * columnAmount)) / columnAmount)); row++) {
			for (int column = 0; column < Integer.min(columnAmount, (itemStacks.size() - tab * rowAmount * columnAmount - row * columnAmount)); column++) {

				ItemStack itemStack = itemStacks.get(tab * rowAmount * columnAmount + row * columnAmount + column);

				this.addButton(new GuiItemStackButton((1000 + tab * rowAmount * columnAmount + row * columnAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, "", itemStack));
			}
		}

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		if (this.initiating) return;
		super.actionPerformed(btn);

		if (btn.id >= 1000) {
			ItemStack itemStack = ((GuiItemStackButton) btn).itemStack;
			prop.propString = itemStack.getItem().getRegistryName().toString() + ":" + itemStack.getItemDamage();
			prop.parsePropString(prop.propString);
		} else if (btn.id == 903) {
			tab--;
			this.initGui();
		} else if (btn.id == 904) {
			tab++;
			this.initGui();
		} else if (btn.id == 902) {
			this.openGui(new GuiCreationProps(propGroupOld, selectedOld));
		}
	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id == 901) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id == 901) {
			searchString = new String(textField.getText()).toLowerCase();
			tab = 0;
			int i = textField.getCursorPosition();

			this.initGui();

			if (searchString.equals("")) this.getTextField(901).setText("");

			this.getTextField(901).setFocused(true);
			this.getTextField(901).setCursorPosition(i);
		}
	}
}
