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
    this.field_178724_i = createScale(this.field_178724_i, EnumParts.ARM_LEFT);
    this.field_178723_h = createScale(this.field_178723_h, EnumParts.ARM_RIGHT);
    this.field_178722_k = createScale(this.field_178722_k, EnumParts.LEG_LEFT);
    this.field_178721_j = createScale(this.field_178721_j, EnumParts.LEG_RIGHT);
    this.bipedHead = createScale(this.bipedHead, EnumParts.HEAD);
    this.bipedHeadwear = createScale(this.bipedHeadwear, EnumParts.HEAD);
    this.field_78115_e = createScale(this.field_78115_e, EnumParts.BODY);
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
    this.field_178721_j.field_78807_k = ((data.getPartData(EnumParts.LEGS)).type != 0);
    if (!this.field_78093_q)
      this.field_78093_q = (data.animation == EnumAnimation.SITTING);
    if (this.field_78117_n && (data.animation == EnumAnimation.CRAWLING || data.isSleeping()))
      this.field_78117_n = false;
    this.field_78115_e.field_78800_c = this.field_78115_e.field_78797_d = this.field_78115_e.field_78798_e = 0.0F;
    this.field_78115_e.rotateAngleX = this.field_78115_e.rotateAngleY = this.field_78115_e.rotateAngleZ = 0.0F;
    this.bipedHead.rotateAngleX = 0.0F;
    this.bipedHead.rotateAngleZ = 0.0F;
    this.bipedHead.field_78800_c = 0.0F;
    this.bipedHead.field_78797_d = 0.0F;
    this.bipedHead.field_78798_e = 0.0F;
    this.field_178722_k.rotateAngleX = 0.0F;
    this.field_178722_k.rotateAngleY = 0.0F;
    this.field_178722_k.rotateAngleZ = 0.0F;
    this.field_178721_j.rotateAngleX = 0.0F;
    this.field_178721_j.rotateAngleY = 0.0F;
    this.field_178721_j.rotateAngleZ = 0.0F;
    this.field_178724_i.field_78800_c = 0.0F;
    this.field_178724_i.field_78797_d = 2.0F;
    this.field_178724_i.field_78798_e = 0.0F;
    this.field_178723_h.field_78800_c = 0.0F;
    this.field_178723_h.field_78797_d = 2.0F;
    this.field_178723_h.field_78798_e = 0.0F;
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
    } else if (this.field_78117_n) {
      this.field_78115_e.rotateAngleX = 0.5F / (data.getPartConfig(EnumParts.BODY)).scaleY;
    }
  }
}
