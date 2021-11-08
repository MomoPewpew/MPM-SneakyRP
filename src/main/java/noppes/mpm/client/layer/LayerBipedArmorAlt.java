package noppes.mpm.client.layer;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.mpm.ModelData;

@SideOnly(Side.CLIENT)
public class LayerBipedArmorAlt extends LayerArmorBase<ModelBiped> {

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
			model.bipedHead.showModel = true;
			model.bipedHeadwear.showModel = true;
			break;
		case CHEST:
			model.bipedBody.showModel = true;
			model.bipedRightArm.showModel = true;
			model.bipedLeftArm.showModel = true;
			break;
		case LEGS:
			model.bipedBody.showModel = true;
			model.bipedRightLeg.showModel = true;
			model.bipedLeftLeg.showModel = true;
			break;
		case FEET:
			model.bipedRightLeg.showModel = true;
			model.bipedLeftLeg.showModel = true;
			break;
		}
	}

	protected void setModelVisible(ModelBiped p_setModelVisible_1_) {
		p_setModelVisible_1_.setInvisible(false);
	}

	protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped model) {
		ModelData data = ModelData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(entity.getUniqueID()));

		switch (slot) {
		case HEAD:
			if (data.hideHat) return null;
			break;
		case CHEST:
			if (data.hideShirt) return null;
			break;
		case LEGS:
			if (data.hidePants) return null;
			break;
		case FEET:
			if (data.hidePants) return null;
			break;
		}

		return ForgeHooksClient.getArmorModel(entity, stack, slot, model);
	}
}
