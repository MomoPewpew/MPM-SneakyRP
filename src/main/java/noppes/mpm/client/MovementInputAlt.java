package noppes.mpm.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import noppes.mpm.client.Camera;

@SideOnly(Side.CLIENT)
public class MovementInputAlt extends MovementInput {
	private final GameSettings gameSettings;
	private final Camera camera;

    public MovementInputAlt(GameSettings gameSettings, Camera camera)
    {
        this.gameSettings = gameSettings;
        this.camera = camera;
    }

    @Override
    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.field_192832_b = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown() && !this.gameSettings.keyBindRight.isKeyDown()) {
        	if (!this.gameSettings.keyBindLeft.isKeyDown()) {
        		move(0.0F);
        	} else {
        		move(315.0F);
        	}
        } else if (this.gameSettings.keyBindLeft.isKeyDown()) {
        	if (!this.gameSettings.keyBindBack.isKeyDown()) {
        		move(270.0F);
        	} else {
        		move(225.0F);
        	}
        } else if (this.gameSettings.keyBindBack.isKeyDown()) {
        	if (!this.gameSettings.keyBindRight.isKeyDown()) {
        		move(180.0F);
        	} else {
        		move(135.0F);
        	}
        } else if (this.gameSettings.keyBindRight.isKeyDown()) {
        	if (!this.gameSettings.keyBindForward.isKeyDown()) {
        		move(90.0F);
        	} else {
        		move(45.0F);
        	}
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.field_192832_b = (float)((double)this.field_192832_b * 0.3D);
        }
    }

    private void move(Float angleDirection) {
    	Float rotateAngle = camera.playerYaw - (camera.cameraYaw + angleDirection + (camera.closeupenabled ? 180 : 0));

    	// reduce the angle
    	rotateAngle =  rotateAngle % 360;

    	// force it to be the positive remainder, so that 0 <= angle < 360
    	rotateAngle = (rotateAngle + 360) % 360;

    	// force into the minimum absolute value residue class, so that -180 < angle <= 180
    	if (rotateAngle > 180)
    		rotateAngle -= 360;

    	rotateAngle = Math.max(-20, Math.min(20, (rotateAngle)));

    	camera.playerYaw -= rotateAngle;
    	camera.playerPitch = 0.0F;
    	++this.field_192832_b;
    }

}
