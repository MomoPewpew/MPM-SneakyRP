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

public class EntityEnderFX extends ParticlePortal {
  private float portalParticleScale;

  private int particleNumber;

  private AbstractClientPlayer player;

  private static final ResourceLocation resource = new ResourceLocation("textures/particle/particles.png");

  private final ResourceLocation location;

  private boolean move = true;

  private float startX = 0.0F, startY = 0.0F, startZ = 0.0F;

  public EntityEnderFX(AbstractClientPlayer player, double partialTicks, double rotationY, double rotationXY, double par8, double par10, double par12, ModelPartData data) {
    super(player.worldObj, partialTicks, rotationY, rotationXY, par8, par10, par12);
    this.player = player;
    this.particleNumber = player.getRNG().nextInt(2);
    this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
    this.particleRed = (data.color >> 16 & 0xFF) / 255.0F;
    this.particleGreen = (data.color >> 8 & 0xFF) / 255.0F;
    this.particleBlue = (data.color & 0xFF) / 255.0F;
    if (player.getRNG().nextInt(3) == 1) {
      this.move = false;
      this.startX = (float)player.posX;
      this.startY = (float)player.posY;
      this.startZ = (float)player.posZ;
    }
    if (data.playerTexture) {
      this.location = player.getLocationSkin();
    } else {
      this.location = data.getResource();
    }
  }

  @Override
  public void renderParticle(BufferBuilder renderer, Entity entity, float partialTicks, float rotationX, float rotationY, float rotationZ, float rotationXY, float rotationXZ) {
    if (this.move) {
      this.startX = (float)(this.player.posX + (this.player.posX - this.player.posX) * partialTicks);
      this.startY = (float)(this.player.posY + (this.player.posY - this.player.posY) * partialTicks);
      this.startZ = (float)(this.player.posZ  + (this.player.posZ - this.player.posZ ) * partialTicks);
    }
    Tessellator tessellator = Tessellator.getInstance();
    tessellator.draw();
    float scale = (this.particleAge + partialTicks) / this.particleMaxAge;
    scale = 1.0F - scale;
    scale *= scale;
    scale = 1.0F - scale;
    this.particleScale = this.portalParticleScale * scale;
    ClientProxy.bindTexture(this.location);
    float f = 0.875F;
    float f1 = f + 0.125F;
    float f2 = 0.75F - this.particleNumber * 0.25F;
    float f3 = f2 + 0.25F;
    float f4 = 0.1F * this.particleScale;
    float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX + this.startX);
    float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY + this.startY);
    float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ + this.startZ);
    int i = getBrightnessForRender(partialTicks);
    int j = i >> 16 & 0xFFFF;
    int k = i & 0xFFFF;
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    renderer.pos((f5 - rotationX * f4 - rotationXY * f4), (f6 - rotationY * f4), (f7 - rotationZ * f4 - rotationXZ * f4)).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    renderer.pos((f5 - rotationX * f4 + rotationXY * f4), (f6 + rotationY * f4), (f7 - rotationZ * f4 + rotationXZ * f4)).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    renderer.pos((f5 + rotationX * f4 + rotationXY * f4), (f6 + rotationY * f4), (f7 + rotationZ * f4 + rotationXZ * f4)).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    renderer.pos((f5 + rotationX * f4 - rotationXY * f4), (f6 - rotationY * f4), (f7 + rotationZ * f4 - rotationXZ * f4)).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    tessellator.draw();
    ClientProxy.bindTexture(resource);
    renderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
  }

  @Override
  public int getFXLayer() {
    return 0;
  }
}
