package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.model.animation.AniBow;
import noppes.mpm.client.model.animation.AniCrawling;
import noppes.mpm.client.model.animation.AniDancing;
import noppes.mpm.client.model.animation.AniHug;
import noppes.mpm.client.model.animation.AniNo;
import noppes.mpm.client.model.animation.AniPoint;
import noppes.mpm.client.model.animation.AniWaving;
import noppes.mpm.client.model.animation.AniYes;
import noppes.mpm.constants.EnumAnimation;
import noppes.mpm.constants.EnumParts;

public class ModelPlayerAlt extends ModelPlayer {
  private ModelRenderer cape;

  private ModelRenderer dmhead;

  private ModelData playerdata;

  private Map<EnumParts, List<ModelScaleRenderer>> map = new HashMap<>();

  public ModelPlayerAlt(float scale, boolean bo) {
    super(scale, bo);
    this.dmhead = new ModelScaleRenderer((ModelBase)this, 24, 0, EnumParts.HEAD);
    this.dmhead.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
    this.cape = new ModelScaleRenderer((ModelBase)this, 0, 0, EnumParts.BODY);
    this.cape.setTextureSize(64, 32);
    this.cape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);
    ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.dmhead, 6);
    ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.cape, 5);
    this.bipedLeftArm = createScale(this.bipedLeftArm, EnumParts.ARM_LEFT);
    this.bipedRightArm = createScale(this.bipedRightArm, EnumParts.ARM_RIGHT);
    this.bipedLeftArmwear = createScale(this.bipedLeftArmwear, EnumParts.ARM_LEFT);
    this.bipedRightArmwear = createScale(this.bipedRightArmwear, EnumParts.ARM_RIGHT);
    this.bipedLeftLeg = createScale(this.bipedLeftLeg, EnumParts.LEG_LEFT);
    this.bipedRightLeg = createScale(this.bipedRightLeg, EnumParts.LEG_RIGHT);
    this.bipedLeftLegwear = createScale(this.bipedLeftLegwear, EnumParts.LEG_LEFT);
    this.bipedRightLegwear = createScale(this.bipedRightLegwear, EnumParts.LEG_RIGHT);
    this.bipedHead = createScale(this.bipedHead, EnumParts.HEAD);
    this.bipedHeadwear = createScale(this.bipedHeadwear, EnumParts.HEAD);
    this.bipedBody = createScale(this.bipedBody, EnumParts.BODY);
    this.bipedBodyWear = createScale(this.bipedBodyWear, EnumParts.BODY);
  }

  private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part) {
    int textureX = ((Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2)).intValue();
    int textureY = ((Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3)).intValue();
    ModelScaleRenderer model = new ModelScaleRenderer((ModelBase)this, textureX, textureY, part);
    model.textureHeight = renderer.textureHeight;
    model.textureWidth = renderer.textureWidth;
    model.childModels = renderer.childModels;
    model.cubeList = renderer.cubeList;
    copyModelAngles(renderer, model);
    List<ModelScaleRenderer> list = this.map.get(part);
    if (list == null)
      this.map.put(part, list = new ArrayList<>());
    list.add(model);
    return model;
  }

  @Override
  public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
    EntityPlayer player = (EntityPlayer)entity;
    this.playerdata = ModelData.get(player);
    if (this.playerdata.isSleeping()) {
      GlStateManager.translate(0.0F, 1.14F, 0.0F);
      GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, -0.8F, 0.0F);
    } else if (this.playerdata.animation == EnumAnimation.CRAWLING) {
      GlStateManager.translate(0.0F, (12.0F - this.playerdata.getBodyY() * 4.0F) * par6, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, ((this.isSneak ? -6.0F : -3.0F) - this.playerdata.getBodyY() * 10.0F) * par6);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
    }
    for (EnumParts part : this.map.keySet()) {
      ModelPartConfig config = this.playerdata.getPartConfig(part);
      for (ModelScaleRenderer model : this.map.get(part))
        model.config = config;
    }
    if (!this.isRiding)
      this.isRiding = (this.playerdata.animation == EnumAnimation.SITTING);
    if (this.isSneak && (this.playerdata.animation == EnumAnimation.CRAWLING || this.playerdata.isSleeping()))
      this.isSneak = false;
    this.bipedBody.rotationPointX = this.bipedBody.rotationPointY = this.bipedBody.rotationPointZ = 0.0F;
    this.bipedBody.rotateAngleX = this.bipedBody.rotateAngleY = this.bipedBody.rotateAngleZ = 0.0F;
    this.bipedHead.rotateAngleX = 0.0F;
    this.bipedHead.rotateAngleZ = 0.0F;
    this.bipedHead.rotationPointX = 0.0F;
    this.bipedHead.rotationPointY = 0.0F;
    this.bipedHead.rotationPointZ = 0.0F;
    this.bipedLeftLeg.rotateAngleX = 0.0F;
    this.bipedLeftLeg.rotateAngleY = 0.0F;
    this.bipedLeftLeg.rotateAngleZ = 0.0F;
    this.bipedRightLeg.rotateAngleX = 0.0F;
    this.bipedRightLeg.rotateAngleY = 0.0F;
    this.bipedRightLeg.rotateAngleZ = 0.0F;
    this.bipedLeftArm.rotationPointX = 0.0F;
    this.bipedLeftArm.rotationPointY = 2.0F;
    this.bipedLeftArm.rotationPointZ = 0.0F;
    this.bipedRightArm.rotationPointX = 0.0F;
    this.bipedRightArm.rotationPointY = 2.0F;
    this.bipedRightArm.rotationPointZ = 0.0F;
    super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
    if (this.playerdata.isSleeping() || player.isPlayerSleeping()) {
      if (this.bipedHead.rotateAngleX < 0.0F) {
        this.bipedHead.rotateAngleX = 0.0F;
        this.bipedHeadwear.rotateAngleX = 0.0F;
      }
    } else if (this.playerdata.animation == EnumAnimation.CRY) {
      this.bipedHead.rotateAngleX = 0.7F;
    } else if (this.playerdata.animation == EnumAnimation.HUG) {
      AniHug.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this);
    } else if (this.playerdata.animation == EnumAnimation.CRAWLING) {
      AniCrawling.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this);
    } else if (this.playerdata.animation == EnumAnimation.WAVING) {
      AniWaving.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this);
    } else if (this.playerdata.animation == EnumAnimation.DANCING) {
      AniDancing.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this);
    } else if (this.playerdata.animation == EnumAnimation.BOW) {
      AniBow.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this, this.playerdata);
    } else if (this.playerdata.animation == EnumAnimation.YES) {
      AniYes.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this, this.playerdata);
    } else if (this.playerdata.animation == EnumAnimation.NO) {
      AniNo.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this, this.playerdata);
    } else if (this.playerdata.animation == EnumAnimation.POINT) {
      AniPoint.setRotationAngles(par1, par2, par3, par4, par5, par6, entity, (ModelBiped)this);
    } else if (this.isSneak) {
      this.bipedBody.rotateAngleX = 0.5F / (this.playerdata.getPartConfig(EnumParts.BODY)).scaleY;
    }
    copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
    copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
    copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
    copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
    copyModelAngles(this.bipedBody, this.bipedBodyWear);
    copyModelAngles(this.bipedHead, this.bipedHeadwear);
  }

  @Override
  public ModelRenderer getRandomModelBox(Random random) {
    switch (random.nextInt(5)) {
      case 0:
        return this.bipedHead;
      case 1:
        return this.bipedBody;
      case 2:
        return this.bipedLeftArm;
      case 3:
        return this.bipedRightArm;
      case 4:
        return this.bipedLeftLeg;
      case 5:
        return this.bipedRightLeg;
    }
    return this.bipedHead;
  }
}