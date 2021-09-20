package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.constants.EnumParts;
import noppes.mpm.LogWriter;


public class ModelPlayerAlt extends ModelPlayer {
	private ModelRenderer cape;
	private ModelRenderer dmhead;
	private ModelData playerdata;
	private Map map = new HashMap();

	public long emoteId = 0;


	public ModelPlayerAlt(float scale, boolean bo) {
		super(scale, bo);
		this.dmhead = new ModelScaleRenderer(this, 24, 0, EnumParts.HEAD);
		this.dmhead.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
		this.cape = new ModelScaleRenderer(this, 0, 0, EnumParts.BODY);
		this.cape.setTextureSize(64, 32);
		this.cape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);
		ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.dmhead, 6);
		ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.cape, 5);
		this.bipedLeftArm = this.createScale(this.bipedLeftArm, EnumParts.ARM_LEFT);
		this.bipedRightArm = this.createScale(this.bipedRightArm, EnumParts.ARM_RIGHT);
		this.bipedLeftArmwear = this.createScale(this.bipedLeftArmwear, EnumParts.ARM_LEFT);
		this.bipedRightArmwear = this.createScale(this.bipedRightArmwear, EnumParts.ARM_RIGHT);
		this.bipedLeftLeg = this.createScale(this.bipedLeftLeg, EnumParts.LEG_LEFT);
		this.bipedRightLeg = this.createScale(this.bipedRightLeg, EnumParts.LEG_RIGHT);
		this.bipedLeftLegwear = this.createScale(this.bipedLeftLegwear, EnumParts.LEG_LEFT);
		this.bipedRightLegwear = this.createScale(this.bipedRightLegwear, EnumParts.LEG_RIGHT);
		this.bipedHead = this.createScale(this.bipedHead, EnumParts.HEAD);
		this.bipedHeadwear = this.createScale(this.bipedHeadwear, EnumParts.HEAD);
		this.bipedBody = this.createScale(this.bipedBody, EnumParts.BODY);
		this.bipedBodyWear = this.createScale(this.bipedBodyWear, EnumParts.BODY);
	}

	private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part) {
		int textureX = (Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2);
		int textureY = (Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3);
		ModelScaleRenderer model = new ModelScaleRenderer(this, textureX, textureY, part);
		model.textureHeight = renderer.textureHeight;
		model.textureWidth = renderer.textureWidth;
		model.childModels = renderer.childModels;
		model.cubeList = renderer.cubeList;
		copyModelAngles(renderer, model);
		List list = (List)this.map.get(part);
		if (list == null) {
			this.map.put(part, list = new ArrayList());
		}

		((List)list).add(model);
		return model;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float ageInTicks, float par4, float par5, float par6, Entity entity) {
		EntityPlayer player = (EntityPlayer)entity;
		this.playerdata = ModelData.get(player);
		Iterator var9 = this.map.keySet().iterator();

		while(var9.hasNext()) {
			EnumParts part = (EnumParts)var9.next();
			ModelPartConfig config = this.playerdata.getPartConfig(part);

			ModelScaleRenderer model;
			for(Iterator var12 = ((List)this.map.get(part)).iterator(); var12.hasNext(); model.config = config) {
				model = (ModelScaleRenderer)var12.next();
			}
		}


		ModelData.resetModelPlayerForEmote(this);

		super.setRotationAngles(par1, par2, ageInTicks, par4, par5, par6, entity);

		this.playerdata.updateAnim();
		this.playerdata.animModelPlayer(this, par4, par5);
	}

	@Override
	public ModelRenderer getRandomModelBox(Random random) {
		switch(random.nextInt(5)) {
			case 0:
			return this.bipedHead;
			case 1:
			return this.bipedBody;
			case 2:
			return this.bipedLeftArm;
			case 3:
			return this.bipedRightArm;
			case 4:
			return this.bipedLeftLeg;
			case 5:
			return this.bipedRightLeg;
			default:
			return this.bipedHead;
		}
	}
}
