package noppes.mpm.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.model.Model2DRenderer;
import noppes.mpm.client.model.ModelPlaneRenderer;
import noppes.mpm.client.model.ModelWings;
import noppes.mpm.constants.EnumParts;

public class LayerBody extends LayerInterface implements LayerPreRender  {
	private Model2DRenderer wing;
	private Model2DRenderer wing2;
	private ModelWings wing3 = new ModelWings();
	private Model2DRenderer breasts;
	private ModelRenderer breasts2;
	private ModelRenderer breasts3;
	private ModelPlaneRenderer skirt;
	private Model2DRenderer fin;

	public LayerBody(RenderPlayer render) {
		super(render);
	}

	@Override
	protected void createParts() {
		this.wing = new Model2DRenderer(this.model, 56.0F, 16.0F, 8, 16);
		this.wing.setRotationPoint(-2.0F, 2.5F, 1.0F);
		this.wing.setRotationOffset(-8.0F, 14.0F, 0.0F);
		this.setRotation(this.wing, 0.7141593F, 0.5235988F, 0.5090659F);
		this.wing2 = new Model2DRenderer(this.model, 56.0F, 16.0F, 8, 16);
		this.wing2.setRotationPoint(-1.0F, 2.5F, 2.0F);
		this.wing2.setRotationOffset(-8.0F, 11.0F, -0.5F);
		this.breasts = new Model2DRenderer(this.model, 20.0F, 22.0F, 8, 3);
		this.breasts.setRotationPoint(-3.6F, 5.2F, -3.0F);
		this.breasts.setScale(0.17F, 0.19F);
		this.breasts.setThickness(1.0F);
		this.breasts2 = new ModelRenderer(this.model);
		Model2DRenderer bottom = new Model2DRenderer(this.model, 20.0F, 22.0F, 8, 4);
		bottom.setRotationPoint(-3.6F, 5.0F, -3.1F);
		bottom.setScale(0.225F, 0.2F);
		bottom.setThickness(2.0F);
		bottom.rotateAngleX = -0.31415927F;
		this.breasts2.addChild(bottom);
		this.breasts3 = new ModelRenderer(this.model);
		Model2DRenderer right = new Model2DRenderer(this.model, 20.0F, 23.0F, 3, 2);
		right.setRotationPoint(-3.8F, 5.3F, -3.6F);
		right.setScale(0.12F, 0.14F);
		right.setThickness(1.75F);
		this.breasts3.addChild(right);
		Model2DRenderer right2 = new Model2DRenderer(this.model, 20.0F, 22.0F, 3, 1);
		right2.setRotationPoint(-3.79F, 4.1F, -3.14F);
		right2.setScale(0.06F, 0.07F);
		right2.setThickness(1.75F);
		right2.rotateAngleX = 0.34906584F;
		this.breasts3.addChild(right2);
		Model2DRenderer right3 = new Model2DRenderer(this.model, 20.0F, 24.0F, 3, 1);
		right3.setRotationPoint(-3.79F, 5.3F, -3.6F);
		right3.setScale(0.06F, 0.07F);
		right3.setThickness(1.75F);
		right3.rotateAngleX = -0.34906584F;
		this.breasts3.addChild(right3);
		Model2DRenderer right4 = new Model2DRenderer(this.model, 21.0F, 23.0F, 1, 2);
		right4.setRotationPoint(-1.8F, 5.3F, -3.14F);
		right4.setScale(0.12F, 0.14F);
		right4.setThickness(1.75F);
		right4.rotateAngleY = 0.34906584F;
		this.breasts3.addChild(right4);
		Model2DRenderer left = new Model2DRenderer(this.model, 25.0F, 23.0F, 3, 2);
		left.setRotationPoint(0.8F, 5.3F, -3.6F);
		left.setScale(0.12F, 0.14F);
		left.setThickness(1.75F);
		this.breasts3.addChild(left);
		Model2DRenderer left2 = new Model2DRenderer(this.model, 25.0F, 22.0F, 3, 1);
		left2.setRotationPoint(0.81F, 4.1F, -3.18F);
		left2.setScale(0.06F, 0.07F);
		left2.setThickness(1.75F);
		left2.rotateAngleX = 0.34906584F;
		this.breasts3.addChild(left2);
		Model2DRenderer left3 = new Model2DRenderer(this.model, 25.0F, 24.0F, 3, 1);
		left3.setRotationPoint(0.81F, 5.3F, -3.6F);
		left3.setScale(0.06F, 0.07F);
		left3.setThickness(1.75F);
		left3.rotateAngleX = -0.34906584F;
		this.breasts3.addChild(left3);
		Model2DRenderer left4 = new Model2DRenderer(this.model, 24.0F, 23.0F, 1, 2);
		left4.setRotationPoint(0.8F, 5.3F, -3.6F);
		left4.setScale(0.12F, 0.14F);
		left4.setThickness(1.75F);
		left4.rotateAngleY = -0.34906584F;
		this.breasts3.addChild(left4);
		this.skirt = new ModelPlaneRenderer(this.model, 58, 18);
		this.skirt.addSidePlane(0.0F, 0.0F, 0.0F, 9, 2);
		ModelPlaneRenderer part1 = new ModelPlaneRenderer(this.model, 58, 18);
		part1.addSidePlane(2.0F, 0.0F, 0.0F, 9, 2);
		part1.rotateAngleY = -1.5707964F;
		this.skirt.addChild(part1);
		this.skirt.setRotationPoint(2.4F, 8.8F, 0.0F);
		this.setRotation(this.skirt, 0.3F, -0.2F, -0.2F);
		this.fin = new Model2DRenderer(this.model, 56.0F, 20.0F, 8, 12);
		this.fin.setRotationPoint(-0.5F, 12.0F, 10.0F);
		this.fin.setScale(0.74F);
		this.fin.rotateAngleY = 1.5707964F;
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {

		GlStateManager.translate(this.model.bipedBody.offsetX, this.model.bipedBody.offsetY, this.model.bipedBody.offsetZ);

		this.model.bipedBody.postRender(0.0625F);
		this.renderSkirt(par7);
		this.renderWings(par7);
		this.renderFin(par7);
		this.renderBreasts(par7);

		GlStateManager.translate(-this.model.bipedBody.offsetX, -this.model.bipedBody.offsetY, -this.model.bipedBody.offsetZ);
	}

	private void renderWings(float par7) {
		ModelPartData data = this.playerdata.getPartData(EnumParts.WINGS);
		if (data != null) {
			ItemStack itemstack = this.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (itemstack.getItem() == Items.ELYTRA && this.playerdata.wingMode == 2)
			return;

			this.preRender(data);
			GlStateManager.pushMatrix();
			if (data.type >= 0 && data.type <= 2) {
				this.wing.render(par7);
				GlStateManager.scale(-1.0F, 1.0F, 1.0F);
				this.wing.render(par7);
			}

			if (data.type == 3) {
				this.wing2.render(par7);
				GlStateManager.scale(-1.0F, 1.0F, 1.0F);
				this.wing2.render(par7);
			}

			if (data.type == 4) {
				this.wing3.render(this.player, this.player.limbSwing, this.player.limbSwingAmount, (float)this.player.ticksExisted, 0.0F, 0.0F, par7);
			}

			GlStateManager.popMatrix();
		}
	}

	private void renderSkirt(float par7) {
		ModelPartData data = this.playerdata.getPartData(EnumParts.SKIRT);
		if (data != null && !this.playerdata.hidePants) {
			this.preRender(data);
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.7F, 1.04F, 1.6F);

			for(int i = 0; i < 10; ++i) {
				GlStateManager.rotate(36.0F, 0.0F, 1.0F, 0.0F);
				this.skirt.render(par7);
			}

			GlStateManager.popMatrix();
		}
	}

	private void renderFin(float par7) {
		ModelPartData data = this.playerdata.getPartData(EnumParts.FIN);
		if (data != null) {
			this.preRender(data);
			this.fin.render(par7);
		}
	}

	private void renderBreasts(float par7) {
		ModelPartData data = this.playerdata.getPartData(EnumParts.BREASTS);
		if (data != null) {
			data.playerTexture = true;
			this.preRender(data);
			if (data.type == 0) {
				this.breasts.render(par7);
			}

			if (data.type == 1) {
				this.breasts2.render(par7);
			}

			if (data.type == 2) {
				this.breasts3.render(par7);
			}

		}
	}

	@Override
	public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
		this.wing.rotateAngleX = 0.7141593F;
		this.wing.rotateAngleZ = 0.5090659F;
		this.wing2.rotateAngleY = 0.8F;
		float motion = Math.abs(MathHelper.sin(par1 * 0.033F + 3.1415927F) * 0.4F) * par2;
		Model2DRenderer var10000;
		if (this.player.worldObj.isAirBlock(this.player.getPosition())) {
			float speed = 0.55F + 0.5F * motion;
			float y = MathHelper.sin(par3 * 0.35F);
			var10000 = this.wing;
			var10000.rotateAngleZ += y * 0.5F * speed;
			var10000 = this.wing;
			var10000.rotateAngleX += y * 0.5F * speed;
			var10000 = this.wing2;
			var10000.rotateAngleY += y * 0.5F * speed;
		} else {
			var10000 = this.wing;
			var10000.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
			var10000 = this.wing;
			var10000.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
			var10000 = this.wing2;
			var10000.rotateAngleY += MathHelper.sin(par3 * 0.07F) * 0.44F;
		}

		this.setRotation(this.skirt, 0.3F, -0.2F, -0.2F);
		ModelPlaneRenderer var10 = this.skirt;
		var10.rotateAngleX += this.model.bipedLeftArm.rotateAngleX * 0.04F;
		var10 = this.skirt;
		var10.rotateAngleZ += this.model.bipedLeftArm.rotateAngleX * 0.06F;
		var10 = this.skirt;
		var10.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.04F - 0.05F;
	}

	@Override
	public void preRender(AbstractClientPlayer player) {
		this.player = player;
		this.playerdata = ModelData.get(player);

		ModelPartData data = this.playerdata.getOrCreatePart(EnumParts.BODY);
		this.model.bipedBody.isHidden = this.model.bipedBodyWear.isHidden = data == null || data.type != 0;

		if (this.playerdata.hideShirt) {
			this.model.bipedBodyWear.showModel = false;
		}
	}
}
