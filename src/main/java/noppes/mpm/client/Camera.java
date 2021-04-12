package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class Camera {
  public boolean enabled = false;

  public float cameraYaw = 0.0F;

  public float cameraPitch = 0.0F;

  public float playerYaw = 0.0F;

  public float playerPitch = 0.0F;

  public float cameraDistance = 4.0F;

  public void update(boolean start) {
    Minecraft mc = Minecraft.getMinecraft();
    Entity view = mc.func_175606_aa();
    if (mc.field_71474_y.field_74320_O != 1) {
      if (this.enabled)
        reset();
      return;
    }
    if (!this.enabled || view == null)
      return;
    updateCamera();
    if (start) {
      view.field_70177_z = view.field_70126_B = this.cameraYaw;
      view.field_70125_A = view.field_70127_C = this.cameraPitch;
    } else {
      view.field_70177_z = mc.thePlayer.field_70177_z - this.cameraYaw + this.playerYaw;
      view.field_70126_B = mc.thePlayer.field_70126_B - this.cameraYaw + this.playerYaw;
      view.field_70125_A = -this.playerPitch;
      view.field_70127_C = -this.playerPitch;
    }
  }

  private void updateCamera() {
    Minecraft mc = Minecraft.getMinecraft();
    if (!mc.field_71415_G)
      return;
    float f = mc.field_71474_y.field_74341_c * 0.6F + 0.2F;
    float f1 = f * f * f * 8.0F;
    double dx = (Mouse.getDX() * f1) * 0.15D;
    double dy = (Mouse.getDY() * f1) * 0.15D;
    if (ClientProxy.Camera.func_151470_d()) {
      this.cameraYaw = (float)(this.cameraYaw + dx);
      this.cameraPitch = (float)(this.cameraPitch + dy);
      this.cameraPitch = MathHelper.func_76131_a(this.cameraPitch, -90.0F, 90.0F);
    } else {
      this.playerYaw = (float)(this.playerYaw + dx);
      this.playerPitch = (float)(this.playerPitch + dy);
      this.playerPitch = MathHelper.func_76131_a(this.playerPitch, -90.0F, 90.0F);
    }
  }

  public void reset() {
    this.enabled = false;
    this.cameraYaw = 0.0F;
    this.cameraPitch = 0.0F;
    this.playerYaw = 0.0F;
    this.playerPitch = 0.0F;
    this.cameraDistance = 4.0F;
  }

  public void enabled() {
    Minecraft mc = Minecraft.getMinecraft();
    if (!this.enabled) {
      this.cameraYaw = this.playerYaw = mc.thePlayer.field_70177_z;
      this.cameraPitch = mc.thePlayer.field_70125_A;
      this.playerPitch = -this.cameraPitch;
    }
    this.enabled = true;
  }
}
