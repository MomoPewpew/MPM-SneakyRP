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

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.field_192832_b;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.field_192832_b;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            //++this.moveStrafe;
        	camera.playerYaw = (float)((double)camera.playerYaw - 10.0F);
        	if (camera.playerYaw < -180.0F)
        		camera.playerYaw = camera.playerYaw + 360.0F;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            //--this.moveStrafe;
        	camera.playerYaw = (float)((double)camera.playerYaw + 10.0F);
        	if (camera.playerYaw > 180.0F)
        		camera.playerYaw = camera.playerYaw - 360.0F;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.field_192832_b = (float)((double)this.field_192832_b * 0.3D);
        }
    }

}
