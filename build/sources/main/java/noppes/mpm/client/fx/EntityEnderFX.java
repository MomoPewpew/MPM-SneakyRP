package noppes.mpm.client.fx;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.particle.ParticlePortal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.ClientProxy;

public class EntityEnderFX extends ParticlePortal{

    private float portalParticleScale;
    private int particleNumber;
    private AbstractClientPlayer player;
    private static final ResourceLocation resource = new ResourceLocation("textures/particle/particles.png");
    private final ResourceLocation location;
    private boolean move = true;
    private float startX = 0, startY = 0, startZ = 0;

	public EntityEnderFX(AbstractClientPlayer player, double par2, double par4,
			double par6, double par8, double par10, double par12, ModelPartData data) {
		super(player.worldObj, par2, par4, par6, par8, par10, par12);

		this.player = player;
		particleNumber = player.getRNG().nextInt(2);
        portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;

        particleRed = (data.color >> 16 & 255) / 255f;
        particleGreen = (data.color >> 8  & 255) / 255f;
        particleBlue = (data.color & 255) / 255f;

        if(player.getRNG().nextInt(3) == 1){
        	move = false;
            this.startX = (float) player.posX;
            this.startY = (float) player.posY;
            this.startZ = (float) player.posZ;
        }

        if(data.playerTexture)
        	location = player.getLocationSkin();
        else
        	location = data.getResource();
	}

	@Override
    public void renderParticle(BufferBuilder renderer, Entity entity, float par2, float par3, float par4, float par5, float par6, float par7){

		if(move){
			startX = (float)(player.prevPosX + (player.posX - player.prevPosX) * (double)par2);
			startY = (float)(player.prevPosY + (player.posY - player.prevPosY) * (double)par2);
			startZ = (float)(player.prevPosZ + (player.posZ - player.prevPosZ) * (double)par2);
		}
    	Tessellator tessellator = Tessellator.getInstance();
    	tessellator.draw();
        float scale = ((float)particleAge + par2) / (float)particleMaxAge;
        scale = 1.0F - scale;
        scale *= scale;
        scale = 1.0F - scale;
        particleScale = portalParticleScale * scale;

    	ClientProxy.bindTexture(location);

        float f = 0.875f;
        float f1 = f + 0.125f;
        float f2 = 0.75f - (particleNumber * 0.25f);
        float f3 = f2 + 0.25f;
        float f4 = 0.1F * particleScale;
        float f5 = (float)(((prevPosX + (posX - prevPosX) * (double)par2) - interpPosX) + startX);
        float f6 = (float)(((prevPosY + (posY - prevPosY) * (double)par2) - interpPosY) + startY);
        float f7 = (float)(((prevPosZ + (posZ - prevPosZ) * (double)par2) - interpPosZ) + startZ);

        GlStateManager.color(1, 1, 1, 1.0F);
		renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        //tessellator.setBrightness(240);
        //tessellator.setColorOpaque_F(1, 1, 1);
        renderer.pos(f5 - par3 * f4 - par6 * f4, f6 - par4 * f4, f7 - par5 * f4 - par7 * f4).tex(f1, f3).color(particleRed, particleGreen, particleBlue, 1).endVertex();
        renderer.pos((f5 - par3 * f4) + par6 * f4, f6 + par4 * f4, (f7 - par5 * f4) + par7 * f4).tex(f1, f2).color(particleRed, particleGreen, particleBlue, 1).endVertex();
        renderer.pos(f5 + par3 * f4 + par6 * f4, f6 + par4 * f4, f7 + par5 * f4 + par7 * f4).tex(f, f2).color(particleRed, particleGreen, particleBlue, 1).endVertex();
        renderer.pos((f5 + par3 * f4) - par6 * f4, f6 - par4 * f4, (f7 + par5 * f4) - par7 * f4).tex(f, f3).color(particleRed, particleGreen, particleBlue, 1).endVertex();

        tessellator.draw();
    	ClientProxy.bindTexture(resource);
		renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    }

	@Override
    public int getFXLayer(){
    	return 0;
    }
}
