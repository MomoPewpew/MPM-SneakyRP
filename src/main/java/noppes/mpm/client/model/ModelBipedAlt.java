package noppes.mpm.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
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

public class ModelBipedAlt extends ModelBiped {
  private Map<EnumParts, List<ModelScaleRenderer>> map = new HashMap<>();

  public ModelBipedAlt(float scale) {
    super(scale);
    this.bipedLeftArm = createScale(this.bipedLeftArm, EnumParts.ARM_LEFT);
    this.bipedRightArm = createScale(this.bipedRightArm, EnumParts.ARM_RIGHT);
    this.field_178722_k = createScale(this.field_178722_k, EnumParts.LEG_LEFT);
    this.bipedRightLeg = createScale(this.bipedRightLeg, EnumParts.LEG_RIGHT);
    this.bipedHead = createScale(this.bipedHead, EnumParts.HEAD);
    this.bipedHeadwear = createScale(this.bipedHeadwear, EnumParts.HEAD);
    this.bipedBody = createScale(this.bipedBody, EnumParts.BODY);
  }

  private ModelScaleRenderer createScale(ModelRenderer renderer, EnumParts part) {
    int textureX = ((Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 2)).intValue();
    int textureY = ((Integer)ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, renderer, 3)).intValue();
    ModelScaleRenderer model = new ModelScaleRenderer((ModelBase)this, textureX, textureY, part);
    model.field_78799_b = renderer.field_78799_b;
    model.field_78801_a = renderer.field_78801_a;
    model.field_78805_m = renderer.field_78805_m;
    model.field_78804_l = renderer.field_78804_l;
    func_178685_a(renderer, model);
    List<ModelScaleRenderer> list = this.map.get(part);
    if (list == null)
      this.map.put(part, list = new ArrayList<>());
    list.add(model);
    return model;
  }

  public void func_78087_a(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
    EntityPlayer player = (EntityPlayer)entity;
    ModelData data = ModelData.get(player);
    for (EnumParts part : this.map.keySet()) {
      ModelPartConfig config = data.getPartConfig(part);
      for (ModelScaleRenderer model : this.map.get(part))
        model.config = config;
    }
    this.bipedRightLeg.isHidden = ((data.getPartData(EnumParts.LEGS)).type != 0);
    if (!this.isRiding)
      this.isRiding = (data.animation == EnumAnimation.SITTING);
    if (this.isSneak && (data.animation == EnumAnimation.CRAWLING || data.isSleeping()))
      this.isSneak = false;
    this.bipedBody.rotationPointX = this.bipedBody.rotationPointY = this.bipedBody.rotationPointZ = 0.0F;
    this.bipedBody.rotateAngleX = this.bipedBody.rotateAngleY = this.bipedBody.rotateAngleZ = 0.0F;
    this.bipedHead.rotateAngleX = 0.0F;
    this.bipedHead.rotateAngleZ = 0.0F;
    this.bipedHead.rotationPointX = 0.0F;
    this.bipedHead.rotationPointY = 0.0F;
    this.bipedHead.rotationPointZ = 0.0F;
    this.field_178722_k.rotateAngleX = 0.0F;
    this.field_178722_k.rotateAngleY = 0.0F;
    this.field_178722_k.rotateAngleZ = 0.0F;
    this.bipedRightLeg.rotateAngleX = 0.0F;
    this.bipedRightLeg.rotateAngleY = 0.0F;
    this.bipedRightLeg.rotateAngleZ = 0.0F;
    this.bipedLeftArm.rotationPointX = 0.0F;
    this.bipedLeftArm.rotationPointY = 2.0F;
    this.bipedLeftArm.rotationPointZ = 0.0F;
    this.bipedRightArm.rotationPointX = 0.0F;
    this.bipedRightArm.rotationPointY = 2.0F;
    this.bipedRightArm.rotationPointZ = 0.0F;
    super.func_78087_a(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
    if (data.isSleeping() || player.isPlayerSleeping()) {
      if (this.bipedHead.rotateAngleX < 0.0F) {
        this.bipedHead.rotateAngleX = 0.0F;
        this.bipedHeadwear.rotateAngleX = 0.0F;
      }
    } else if (data.animation == EnumAnimation.CRY) {
      this.bipedHead.rotateAngleX = 0.7F;
    } else if (data.animation == EnumAnimation.HUG) {
      AniHug.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
    } else if (data.animation == EnumAnimation.CRAWLING) {
      AniCrawling.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
    } else if (data.animation == EnumAnimation.WAVING) {
      AniWaving.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
    } else if (data.animation == EnumAnimation.DANCING) {
      AniDancing.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
    } else if (data.animation == EnumAnimation.BOW) {
      AniBow.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
    } else if (data.animation == EnumAnimation.YES) {
      AniYes.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
    } else if (data.animation == EnumAnimation.NO) {
      AniNo.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this, data);
    } else if (data.animation == EnumAnimation.POINT) {
      AniPoint.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity, this);
    } else if (this.isSneak) {
      this.bipedBody.rotateAngleX = 0.5F / (data.getPartConfig(EnumParts.BODY)).scaleY;
    }
  }
}
