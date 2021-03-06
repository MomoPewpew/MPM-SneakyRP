package noppes.mpm.client.model.part.legs;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.mpm.ModelData;

public class ModelMermaidLegs2 extends ModelRenderer {
	ModelRenderer Tail1;
	ModelRenderer Tail2;
	ModelRenderer Tail3;
	ModelRenderer Tail4;
	ModelRenderer Tail5;
	ModelRenderer Tail6;
	ModelRenderer Tail7;
	ModelRenderer Tail8;

	public ModelMermaidLegs2(ModelBase base) {
		super(base);
		this.textureWidth = 64.0F;
		this.textureHeight = 32.0F;
		this.Tail1 = new ModelRenderer(base, 0, 18);
		this.Tail1.addBox(0.0F, 0.0F, 0.0F, 8, 6, 4);
		this.Tail1.setRotationPoint(-4.0F, 12.0F, -2.0F);
		this.setRotation(this.Tail1, 0.075F, 0.0F, 0.0F);
		this.Tail2 = new ModelRenderer(base, 0, 18);
		this.Tail2.addBox(0.0F, 0.0F, 0.0F, 6, 5, 3);
		this.Tail2.setRotationPoint(1.0F, 5.5F, 0.3F);
		this.setRotation(this.Tail2, 0.56F, 0.0F, 0.0F);
		this.Tail1.addChild(this.Tail2);
		this.Tail3 = new ModelRenderer(base, 0, 18);
		this.Tail3.addBox(0.0F, 0.0F, 0.0F, 5, 5, 2);
		this.Tail3.setRotationPoint(5.5F, 4.0F, 2.5F);
		this.setRotation(this.Tail3, -0.37818F, 3.141593F, 0.0F);
		this.Tail2.addChild(this.Tail3);
		this.Tail4 = new ModelRenderer(base, 0, 20);
		this.Tail4.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1);
		this.Tail4.setRotationPoint(0.5F, 4.5F, 0.5F);
		this.setRotation(this.Tail4, -0.1F, 0.0F, 0.0F);
		this.Tail3.addChild(this.Tail4);
		this.Tail5 = new ModelRenderer(base, 0, 20);
		this.Tail5.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		this.Tail5.setRotationPoint(-1.0F, 1.5F, 0.0F);
		this.setRotation(this.Tail5, 0.0F, 0.0F, 0.0F);
		this.Tail4.addChild(this.Tail5);
		this.Tail6 = new ModelRenderer(base, 0, 20);
		this.Tail6.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		this.Tail6.setRotationPoint(-2.0F, 3.0F, 0.0F);
		this.setRotation(this.Tail6, 0.0F, 0.0F, 0.0F);
		this.Tail4.addChild(this.Tail6);
		this.Tail7 = new ModelRenderer(base, 0, 20);
		this.Tail7.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		this.Tail7.setRotationPoint(4.0F, 1.5F, 0.0F);
		this.setRotation(this.Tail7, 0.0F, 0.0F, 0.0F);
		this.Tail4.addChild(this.Tail7);
		this.Tail8 = new ModelRenderer(base, 0, 20);
		this.Tail8.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		this.Tail8.setRotationPoint(5.0F, 3.0F, 0.0F);
		this.setRotation(this.Tail8, 0.0F, 0.0F, 0.0F);
		this.Tail4.addChild(this.Tail8);
	}

	@Override
	public void render(float f5) {
		if (!this.isHidden && this.showModel) {
			this.Tail1.render(f5);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity, ModelData data) {
		this.Tail1.setRotationPoint(-4.0F, 12.0F, -2.0F);
		float ani = MathHelper.sin(par1 * 0.6662F);
		if ((double)ani > 0.2D) {
			ani /= 3.0F;
		}

		this.Tail1.rotateAngleX = 0.2F - ani * 0.2F * par2;
		this.Tail2.rotateAngleX = 0.56F - ani * 0.24F * par2;
		this.Tail3.rotateAngleX = -0.4F + ani * 0.24F * par2;
		this.Tail4.rotateAngleX = -0.1F + ani * 0.1F * par2;
		if (entity.isSneaking()) {
			this.Tail1.setRotationPoint(-4.0F, 10.0F, 3.0F);
		}

	}
}
