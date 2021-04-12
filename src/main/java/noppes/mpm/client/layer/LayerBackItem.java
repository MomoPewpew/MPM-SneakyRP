package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noppes.mpm.MorePlayerModels;

public class LayerBackItem extends LayerInterface {
  public LayerBackItem(RenderPlayer render) {
    super(render);
  }

  public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
    Minecraft minecraft = Minecraft.getMinecraft();
    ItemStack itemstack = this.playerdata.backItem;
    if (!MorePlayerModels.EnableBackItem || itemstack.func_190926_b() || ItemStack.func_77989_b(itemstack, this.player.inventory.func_70448_g()))
      return;
    Item item = itemstack.func_77973_b();
    if (item instanceof net.minecraft.item.ItemBlock)
      return;
    this.model.field_78115_e.func_78794_c(par7);
    GlStateManager.func_179137_b(0.0D, 0.36D, 0.14D);
    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
    if (item instanceof net.minecraft.item.ItemSword)
      GlStateManager.rotate(180.0F, -1.0F, 0.0F, 0.0F);
    IBakedModel model = minecraft.func_175599_af().func_175037_a().func_178089_a(itemstack);
    ItemTransformVec3f p_175034_1_ = (model.func_177552_f()).field_188037_l;
    GlStateManager.translate(p_175034_1_.field_178363_d.x + ItemCameraTransforms.field_181696_h, p_175034_1_.field_178363_d.y + ItemCameraTransforms.field_181697_i, p_175034_1_.field_178363_d.z + ItemCameraTransforms.field_181698_j);
    minecraft.func_175597_ag().func_178099_a((EntityLivingBase)this.player, itemstack, ItemCameraTransforms.TransformType.NONE);
  }

  public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {}
}
