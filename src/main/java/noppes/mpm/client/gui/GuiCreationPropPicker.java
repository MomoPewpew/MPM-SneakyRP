package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationPropPicker extends GuiCreationScreenInterface implements ITextfieldListener {
    private Boolean initiating = false;
    private static Prop prop;
    private static List<ItemStack> itemStacks;
    private int tab;
    private String searchString;
    private static final Integer rowAmount = 10;
    private static final Integer columnAmount = 10;


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

         for (int row = 0; row < Integer.max(10, (int) Math.ceil((((itemStacks.size() - tab * rowAmount * columnAmount)) / 10))); row++) {
        	 for (int column = 0; column < Integer.max(10, (itemStacks.size() - tab * rowAmount * columnAmount - row * rowAmount)); column++) {
        		 itemButton(itemStacks.get(tab * rowAmount * columnAmount + row * rowAmount + column), row, column);
             }
         }

         this.initiating = false;
    }

    private static void itemButton(ItemStack itemStack, Integer row, Integer column) {
    	ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		IBakedModel bakedModeltemp = itemModelMesher.getItemModel(itemStack);
		IBakedModel bakedModel = bakedModeltemp.getOverrides().handleItemState(bakedModeltemp, itemStack, null, null);

		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(column, row, 150.0F);
			GlStateManager.scale(16F, -16F, 16F);
			bakedModel = ForgeHooksClient.handleCameraTransforms(bakedModel, TransformType.GUI, false);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			Minecraft minecraft = Minecraft.getMinecraft();
			RenderItem renderItem = minecraft.getRenderItem();
			//renderItem.renderModel(bakedModel, itemStack);
			renderItem.renderItem(itemStack, bakedModel);
		}
		GlStateManager.popMatrix();
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