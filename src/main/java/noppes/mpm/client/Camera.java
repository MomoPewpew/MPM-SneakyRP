package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Map;

import org.lwjgl.input.Mouse;

public class Camera {
     public boolean enabled = false;
     public float cameraYaw = 0.0F;
     public float cameraPitch = 0.0F;
     public float playerYaw = 0.0F;
     public float playerPitch = 0.0F;
     public float cameraDistance = 4.0F;
     private static Field KEYBIND_ARRAY = null;

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
               double dy = (double)((float)Mouse.getDY() * f1) * 0.15D;

                this.cameraYaw = (float)((double)this.cameraYaw + dx);
                this.cameraPitch = (float)((double)this.cameraPitch + dy);
                this.cameraPitch = MathHelper.clamp_float(this.cameraPitch, -90.0F, 90.0F);

                if (this.cameraYaw < -180.0F) {
                	this.cameraYaw = this.cameraYaw + 360.0F;
                } else if (this.cameraYaw > 180.0F) {
                	this.cameraYaw = this.cameraYaw - 360.0F;
                }

                if ((this.cameraYaw > (this.playerYaw + 90.0F)) || (this.cameraYaw < (this.playerYaw - 90.0F))) {
                	this.playerPitch = this.cameraPitch;
                } else {
                	this.playerPitch = -this.cameraPitch;
                }
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
               this.cameraYaw = this.playerYaw = mc.thePlayer.rotationYaw;
               this.cameraPitch = mc.thePlayer.rotationPitch;
               this.playerPitch = -this.cameraPitch;
          }

          this.enabled = true;

          if (!(mc.thePlayer.movementInput instanceof MovementInputAlt))
        	  mc.thePlayer.movementInput = new MovementInputAlt(mc.gameSettings, this);
     }
}
