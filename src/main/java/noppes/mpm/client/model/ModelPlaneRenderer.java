package noppes.mpm.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import noppes.mpm.constants.EnumPlanePosition;

public class ModelPlaneRenderer extends ModelRenderer {
	private int textureOffsetX;
	private int textureOffsetY;

	public ModelPlaneRenderer(ModelBase modelbase, int i, int j) {
		super(modelbase, i, j);
		this.textureOffsetX = i;
		this.textureOffsetY = j;
	}

	public void addBackPlane(float f, float f1, float f2, int i, int j) {
		this.addPlane(f, f1, f2, i, j, 0, 0.0F, EnumPlanePosition.BACK);
	}

	public void addSidePlane(float f, float f1, float f2, int j, int k) {
		this.addPlane(f, f1, f2, 0, j, k, 0.0F, EnumPlanePosition.LEFT);
	}

	public void addTopPlane(float f, float f1, float f2, int i, int k) {
		this.addPlane(f, f1, f2, i, 0, k, 0.0F, EnumPlanePosition.TOP);
	}

	public void addBackPlane(float f, float f1, float f2, int i, int j, float scale) {
		this.addPlane(f, f1, f2, i, j, 0, scale, EnumPlanePosition.BACK);
	}

	public void addSidePlane(float f, float f1, float f2, int j, int k, float scale) {
		this.addPlane(f, f1, f2, 0, j, k, scale, EnumPlanePosition.LEFT);
	}

	public void addTopPlane(float f, float f1, float f2, int i, int k, float scale) {
		this.addPlane(f, f1, f2, i, 0, k, scale, EnumPlanePosition.TOP);
	}

	public void addPlane(float par1, float par2, float par3, int par4, int par5, int par6, float f3, EnumPlanePosition pos) {
		this.cubeList.add(new ModelPlane(this, this.textureOffsetX, this.textureOffsetY, par1, par2, par3, par4, par5, par6, f3, pos));
	}
}
