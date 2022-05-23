package noppes.mpm;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.constants.EnumPackets;

public class ModelEyeData extends ModelPartData {
	public boolean glint = true;
	private Random r = new Random();
	public int browThickness = 4;
	public int eyePos = 1;
	public int skinColor = 11830381;
	public int browColor = 5982516;
	public long blinkStart = 0L;

	public ModelEyeData() {
		super("eyes");
		this.type = -1;
		this.color = 4210943;
	}

	@Override
	public NBTTagCompound writeToNBT() {
		NBTTagCompound compound = super.writeToNBT();
		compound.setBoolean("Glint", this.glint);
		compound.setInteger("SkinColor", this.skinColor);
		compound.setInteger("BrowColor", this.browColor);
		compound.setInteger("PositionY", this.eyePos);
		compound.setInteger("BrowThickness", this.browThickness);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.glint = compound.getBoolean("Glint");
		this.skinColor = compound.getInteger("SkinColor");
		this.browColor = compound.getInteger("BrowColor");
		this.eyePos = compound.getInteger("PositionY");
		this.browThickness = compound.getInteger("BrowThickness");
	}

	public boolean isEnabled() {
		return this.type >= 0;
	}

	public void update(EntityPlayer player) {
		if (this.isEnabled() && player.isEntityAlive()) {
			if (this.blinkStart < 0L) {
				++this.blinkStart;
			} else if (this.blinkStart == 0L) {
				if (this.r.nextInt(140) == 1) {
					this.blinkStart = System.currentTimeMillis();
					if (player != null && player.isServerWorld()) {
						Server.sendAssociatedData(player, EnumPackets.EYE_BLINK, player.getUniqueID());
					}
				}
			} else if (System.currentTimeMillis() - this.blinkStart > 300L) {
				this.blinkStart = -20L;
			}

		}
	}

	public boolean equals(ModelEyeData eye2) {
		return ((this.color == eye2.color) && (this.type == eye2.type) && (this.glint == eye2.glint) && (this.browThickness == eye2.browThickness) && (this.eyePos == eye2.eyePos) && (this.skinColor == eye2.skinColor) && (this.browColor == eye2.browColor));
	}

	public void clone(ModelEyeData eye2) {
		this.color = eye2.color;
		this.type = eye2.type;
		this.glint = eye2.glint;
		this.browThickness = eye2.browThickness;
		this.eyePos = eye2.eyePos;
		this.skinColor = eye2.skinColor;
		this.browColor = eye2.browColor;
	}
}
