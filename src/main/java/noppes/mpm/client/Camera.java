package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class Camera {
	public boolean enabled = false;
	public boolean closeupenabled = false;
	public float cameraYaw = 0.0F;
	public float cameraPitch = 0.0F;
	public float playerYaw = 0.0F;
	public float playerPitch = 0.0F;
	public float cameraDistance = 4.0F;

	public void update(boolean start) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity view = mc.getRenderViewEntity();
		if (mc.gameSettings.thirdPersonView != 1) {
			if (this.enabled) {
				this.reset();
			}
		} else if (this.enabled && view != null) {
			this.updateCamera();
			if (start) {
				view.rotationYaw = view.prevRotationYaw = this.cameraYaw;
				view.rotationPitch = view.prevRotationPitch = this.cameraPitch;
			} else {
				view.rotationYaw = mc.thePlayer.rotationYaw - this.cameraYaw + this.playerYaw;
				view.prevRotationYaw = mc.thePlayer.prevRotationYaw - this.cameraYaw + this.playerYaw;
				view.rotationPitch = -this.playerPitch;
				view.prevRotationPitch = -this.playerPitch;
			}

		}
	}

	private void updateCamera() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.inGameHasFocus) {
			float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			double dx = (double)((float)Mouse.getDX() * f1) * 0.15D;
			double dy = (double)((float)Mouse.getDY() * f1) * -0.15D;

			this.cameraYaw = (float)((double)this.cameraYaw + dx);
			this.cameraPitch = (float)((double)this.cameraPitch + dy);
			this.cameraPitch = MathHelper.clamp_float(this.cameraPitch, -90.0F, 90.0F);

			if (mc.gameSettings.keyBindAttack.isKeyDown() || mc.gameSettings.keyBindUseItem.isKeyDown()) {
				this.playerYaw = this.cameraYaw;
				this.playerPitch = -this.cameraPitch;
			} else {
				/*                    if (this.closeupenabled
				&& ((((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) < 90) || (((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) > 270)))
				this.closeupenabled = false;*/

				if (this.closeupenabled) {
					if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()
					&& ((((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) > 135) && (((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) < 225)))
					this.playerYaw = this.cameraYaw + 180.0F;

					this.playerPitch = this.cameraPitch;
				} else {
					if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) {
						if ((((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) < 45) || (((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) > 315)) {
							this.playerYaw = this.cameraYaw;
							this.playerPitch = -this.cameraPitch;
						} else if ((((Math.abs((this.cameraYaw - mc.thePlayer.renderYawOffset) % 360))) < 90) || (Math.abs(mc.thePlayer.rotationYaw - mc.thePlayer.renderYawOffset) > 270)) {
							this.playerPitch = -this.cameraPitch;;
						}
					}
				}
			}
		}
	}

	public void reset() {
		this.enabled = false;
		this.closeupenabled = false;
		this.cameraYaw = 0.0F;
		this.cameraPitch = 0.0F;
		this.playerYaw = 0.0F;
		this.playerPitch = 0.0F;
		this.cameraDistance = 4.0F;
	}

	public void enabled() {
		if (!this.enabled) {
			Minecraft mc = Minecraft.getMinecraft();
			this.cameraYaw = this.playerYaw = mc.thePlayer.rotationYaw;
			this.cameraPitch = mc.thePlayer.rotationPitch;
			this.playerPitch = -this.cameraPitch;
		}

		this.enabled = true;
	}

	public void closeupenabled() {
		if (!(Math.tan((this.cameraYaw - this.playerYaw) * (Math.PI / 360.0F)) > Math.tan(135.0F * Math.PI / 360.0F))
		&& !(Math.tan((this.cameraYaw - this.playerYaw) * (Math.PI / 360.0F)) < Math.tan(-135.0F * Math.PI / 360.0F)))
		this.cameraYaw = this.playerYaw + 180.0F;

		this.closeupenabled = true;
	}

	public void closeupdisabled() {
		this.cameraYaw = this.playerYaw;
		this.closeupenabled = false;
	}
}
