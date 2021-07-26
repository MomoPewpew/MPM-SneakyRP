package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.Prop;
import noppes.mpm.client.gui.util.GuiItemStackButton;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropPicker extends GuiCreationScreenInterface implements ITextfieldListener {
    private Boolean initiating = false;
    private static Prop prop;
    private static List<ItemStack> itemStacks;
    private int tab;
    private String searchString;
    private static final Integer rowAmount = 7;
    private static final Integer columnAmount = 14;
    private final Integer yTop = this.guiTop;
    private final Integer yBot = yTop + 70;
    private final Integer xLeft = this.guiLeft;
    private final Integer xRight = xLeft + 140;

    public GuiCreationPropPicker(Prop propArg) {
         this.active = -1;
         this.xOffset = 140;
         prop = propArg;
         tab = 0;

         itemStacks = new ArrayList<ItemStack>();

         List<Item> list = ForgeRegistries.ITEMS.getValues();

         TreeMap<Integer, Item> map = new TreeMap<>();
         for (Item item : list) {
             map.put(item.getIdFromItem(item), item);
         }
         Set<Entry<Integer, Item>> mappings = map.entrySet();

         list = new ArrayList<Item>();

         for(Entry<Integer, Item> mapping : mappings){
        	 if (mapping.getKey() > 0)
        		 list.add(mapping.getValue());
         }

         Iterator<Item> var1 = list.iterator();

         while(var1.hasNext()) {
             Item ent = (Item)var1.next();

             itemStacks.add(new ItemStack(ent));

             for (short i = 1; i < 1; i++) {
	           	  ItemStack itemStack = new ItemStack(ent, 1, i);

	           	  if (itemStack.getDisplayName().equals(new ItemStack(ent).getDisplayName())) {
	           		  break;
	           	  } else {
	           		  itemStacks.add(itemStack);
	           	  }
     		 }
        }
    }

    @Override
    public void initGui() {
    	 this.initiating = true;
         super.initGui();

         for (int row = 0; row < Integer.min(rowAmount, (int) Math.ceil(((itemStacks.size() - tab * rowAmount * columnAmount)) / 10)); row++) {
        	 for (int column = 0; column < Integer.min(columnAmount, (itemStacks.size() - tab * rowAmount * columnAmount - row * rowAmount)); column++) {
        		 this.addButton(new GuiItemStackButton((1000 + tab * rowAmount * columnAmount + row * rowAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, "", itemStacks.get(tab * rowAmount * columnAmount + row * rowAmount + column)));
             }
         }

         this.initiating = false;
    }

    @Override
    protected void actionPerformed(GuiButton btn) {

    }

	@Override
	public void unFocused(GuiNpcTextField var1) {

	}

	@Override
	public void focused(GuiNpcTextField var1) {

	}
}