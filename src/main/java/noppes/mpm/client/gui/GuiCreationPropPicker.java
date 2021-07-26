package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.Prop;
import noppes.mpm.client.gui.util.GuiItemStackButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropPicker extends GuiCreationScreenInterface implements ITextfieldListener {
    private Boolean initiating = false;
    private static Prop prop;
    private static List<ItemStack> itemStacks;
    private static int tab;
    private static String searchString;
    private static final Integer rowAmount = 7;
    private static final Integer columnAmount = 14;
    private final Integer yTop = this.guiTop;
    private final Integer yBot = yTop + 70;
    private final Integer xLeft = this.guiLeft;
    private final Integer xRight = xLeft + 140;
    private static Set<Entry<Integer, Item>> mappings;

    public GuiCreationPropPicker(Prop propArg) {
         this.active = -1;
         this.xOffset = 140;
         prop = propArg;
         tab = 0;
         searchString = "";

         itemStacks = new ArrayList<ItemStack>();

         List<Item> list = ForgeRegistries.ITEMS.getValues();

         TreeMap<Integer, Item> map = new TreeMap<>();
         for (Item item : list) {
             map.put(item.getIdFromItem(item), item);
         }
         mappings = map.entrySet();
    }

    @Override
    public void initGui() {
    	 this.initiating = true;
         super.initGui();

         List<Item> list = new ArrayList<Item>();

         for(Entry<Integer, Item> mapping : mappings){
    		 list.add(mapping.getValue());
         }

         Iterator<Item> var1 = list.iterator();
         itemStacks = new ArrayList<ItemStack>();

         while(var1.hasNext()) {
             Item ent = (Item)var1.next();

             ItemStack itemStack = new ItemStack(ent);

             if ((!searchString.startsWith("@") && itemStack.getDisplayName().toLowerCase().contains(searchString))
    				 || (searchString.startsWith("@") && itemStack.getItem().getRegistryName().toString().contains(new String(searchString).replace("@", "")))) {
            	 itemStacks.add(new ItemStack(ent));
             }

             for (short i = 1; i < 1; i++) {
	           	  itemStack = new ItemStack(ent, 1, i);

	           	  if (itemStack.getDisplayName().equals(new ItemStack(ent).getDisplayName())) {
	           		  break;
	           	  } else if ((!searchString.startsWith("@") && itemStack.getDisplayName().toLowerCase().contains(searchString))
        				 || (searchString.startsWith("@") && itemStack.getItem().getRegistryName().toString().contains(new String(searchString).replace("@", "")))) {
	           		  itemStacks.add(itemStack);
	           	  }
     		 }
        }

         for (int row = 0; row < Integer.min(rowAmount, (int) Math.ceil(((itemStacks.size() - tab * rowAmount * columnAmount)) / 10)); row++) {
        	 for (int column = 0; column < Integer.min(columnAmount, (itemStacks.size() - tab * rowAmount * columnAmount - row * rowAmount)); column++) {

        		 ItemStack itemStack = itemStacks.get(tab * rowAmount * columnAmount + row * rowAmount + column);

        		 this.addButton(new GuiItemStackButton((1000 + tab * rowAmount * columnAmount + row * rowAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, "", itemStack));
             }
         }

         this.addTextField(new GuiNpcTextField(901, this, this.guiLeft + 50, this.guiTop + 200, 200, 16, searchString.equals("") ? "Search" : searchString));


         this.initiating = false;
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
		if (this.initiating) return;
        super.actionPerformed(btn);

        if (btn.id >= 1000) {
        	ItemStack itemStack = itemStacks.get(btn.id - 1000);
        	prop.propString = itemStack.getItem().getRegistryName().toString() + ":" + itemStack.getItemDamage();
        	prop.parsePropString(prop.propString);
        }
    }

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id == 901) {
			searchString = new String(textField.getText()).toLowerCase();
			this.initGui();
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id == 901) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}
	}
}