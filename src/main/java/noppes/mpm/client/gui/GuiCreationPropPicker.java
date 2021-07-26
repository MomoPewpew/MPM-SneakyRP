package noppes.mpm.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.Prop;
import noppes.mpm.client.gui.util.GuiItemStackButton;
import noppes.mpm.client.gui.util.GuiNpcButton;
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
         Iterator<Item> var1 = ForgeRegistries.ITEMS.getValues().iterator();

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

/*         for (ItemStack itemStack : itemStacks) {
        	 this.getPlayer().addChatMessage(new TextComponentTranslation(itemStack.getDisplayName()));
         }*/

         for (int row = 0; row < Integer.min(rowAmount, (int) Math.ceil(((itemStacks.size() - tab * rowAmount * columnAmount)) / 10)); row++) {
        	 for (int column = 0; column < Integer.min(columnAmount, (itemStacks.size() - tab * rowAmount * columnAmount - row * rowAmount)); column++) {
        		 //itemButton(itemStacks.get(tab * rowAmount * columnAmount + row * rowAmount + column), row, column);
        		 this.addButton(new GuiItemStackButton((1000 + tab * rowAmount * columnAmount + row * rowAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, "", itemStacks.get(tab * rowAmount * columnAmount + row * rowAmount + column)));
             }
         }

         this.initiating = false;
    }

    private void itemButton(ItemStack itemStack, Integer row, Integer column) {

		//this.addButton(new GuiNpcButton((1000 + tab * rowAmount * columnAmount + row * rowAmount + column), this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, ""));

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row, 20, 20, 28, 32);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        this.itemRender.renderItemAndEffectIntoGUI(itemStack, this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, this.guiLeft + 20 * column, this.guiTop + 46 + 20 * row);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
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