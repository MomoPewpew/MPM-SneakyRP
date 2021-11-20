package noppes.mpm.client.layer;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.mpm.ModelData;

@SideOnly(Side.CLIENT)
public class LayerBipedArmorAlt extends LayerBipedArmor {

	ModelData data = null;

	public LayerBipedArmorAlt(RenderLivingBase<?> render) {
		super(render);
	}

	protected void initArmor() {
		this.modelLeggings = new ModelBiped(0.5F);
		this.modelArmor = new ModelBiped(1.0F);
	}

	protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slot) {
		setModelVisible(model);
		switch (slot) {
		case HEAD:
			if (data.hideHat) {
				model.bipedHead.showModel = false;
				model.bipedHeadwear.showModel = false;
			} else {
				model.bipedHead.showModel = true;
				model.bipedHeadwear.showModel = true;
			}
			break;
		case CHEST:
			if (data.hideShirt) {
				model.bipedBody.showModel = false;
				model.bipedRightArm.showModel =false;
				model.bipedLeftArm.showModel = false;
			} else {
				model.bipedBody.showModel = true;
				model.bipedRightArm.showModel = true;
				model.bipedLeftArm.showModel = true;
			}
			break;
		case LEGS:
			if (data.hidePants) {
				model.bipedBody.showModel = false;
				model.bipedRightLeg.showModel = false;
				model.bipedLeftLeg.showModel = false;
			} else {
				model.bipedBody.showModel = true;
				model.bipedRightLeg.showModel = true;
				model.bipedLeftLeg.showModel = true;
			}
			break;
		case FEET:
			if (data.hidePants) {
				model.bipedRightLeg.showModel = false;
				model.bipedLeftLeg.showModel = false;
			} else {
				model.bipedRightLeg.showModel = true;
				model.bipedLeftLeg.showModel = true;
			}
			break;
		}
	}

	protected void setModelVisible(ModelBiped p_setModelVisible_1_) {
		p_setModelVisible_1_.setInvisible(false);
	}

	protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped model) {
		if (data == null) data = ModelData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(entity.getUniqueID()));
		return ForgeHooksClient.getArmorModel(entity, stack, slot, model);
	}
}
