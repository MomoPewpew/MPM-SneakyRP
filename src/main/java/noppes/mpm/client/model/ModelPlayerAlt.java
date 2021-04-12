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
    this.dmhead.func_78790_a(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
    this.cape = new ModelScaleRenderer((ModelBase)this, 0, 0, EnumParts.BODY);
    this.cape.func_78787_b(64, 32);
    this.cape.func_78790_a(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);
    ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.dmhead, 6);
    ObfuscationReflectionHelper.setPrivateValue(ModelPlayer.class, this, this.cape, 5);
    this.field_178724_i = createScale(this.field_178724_i, EnumParts.ARM_LEFT);
    this.field_178723_h = createScale(this.field_178723_h, EnumParts.ARM_RIGHT);
    this.field_178734_a = createScale(this.field_178734_a, EnumParts.ARM_LEFT);
    this.field_178732_b = createScale(this.field_178732_b, EnumParts.ARM_RIGHT);
    this.field_178722_k = createScale(this.field_178722_k, EnumParts.LEG_LEFT);
    this.field_178721_j = createScale(this.field_178721_j, EnumParts.LEG_RIGHT);
    this.field_178733_c = createScale(this.field_178733_c, EnumParts.LEG_LEFT);
    this.field_178731_d = createScale(this.field_178731_d, EnumParts.LEG_RIGHT);
    this.field_78116_c = createScale(this.field_78116_c, EnumParts.HEAD);
    this.field_178720_f = createScale(this.field_178720_f, EnumParts.HEAD);
    this.field_78115_e = createScale(this.field_78115_e, EnumParts.BODY);
    this.field_178730_v = createScale(this.field_178730_v, EnumParts.BODY);
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

  public void func_78087_a(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
    EntityPlayer player = (EntityPlayer)entity;
    this.playerdata = ModelData.get(player);
    if (this.playerdata.isSleeping()) {
      GlStateManager.func_179109_b(0.0F, 1.14F, 0.0F);
      GlStateManager.func_179114_b(45.0F, -1.0F, 0.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, -0.8F, 0.0F);
    } else if (this.playerdata.animation == EnumAnimation.CRAWLING) {
      GlStateManager.func_179109_b(0.0F, (12.0F - this.playerdata.getBodyY() * 4.0F) * par6, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.0F, ((this.field_78117_n ? -6.0F : -3.0F) - this.playerdata.getBodyY() * 10.0F) * par6);
      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
    }
    for (EnumParts part : this.map.keySet()) {
      ModelPartConfig config = this.playerdata.getPartConfig(part);
      for (ModelScaleRenderer model : this.map.get(part))
        model.config = config;
    }
    if (!this.field_78093_q)
      this.field_78093_q = (this.playerdata.animation == EnumAnimation.SITTING);
    if (this.field_78117_n && (this.playerdata.animation == EnumAnimation.CRAWLING || this.playerdata.isSleeping()))
      this.field_78117_n = false;
    this.field_78115_e.field_78800_c = this.field_78115_e.field_78797_d = this.field_78115_e.field_78798_e = 0.0F;
    this.field_78115_e.field_78795_f = this.field_78115_e.field_78796_g = this.field_78115_e.field_78808_h = 0.0F;
    this.field_78116_c.field_78795_f = 0.0F;
    this.field_78116_c.field_78808_h = 0.0F;
    this.field_78116_c.field_78800_c = 0.0F;
    this.field_78116_c.field_78797_d = 0.0F;
    this.field_78116_c.field_78798_e = 0.0F;
    this.field_178722_k.field_78795_f = 0.0F;
    this.field_178722_k.field_78796_g = 0.0F;
    this.field_178722_k.field_78808_h = 0.0F;
    this.field_178721_j.field_78795_f = 0.0F;
    this.field_178721_j.field_78796_g = 0.0F;
    this.field_178721_j.field_78808_h = 0.0F;
    this.field_178724_i.field_78800_c = 0.0F;
    this.field_178724_i.field_78797_d = 2.0F;
    this.field_178724_i.field_78798_e = 0.0F;
    this.field_178723_h.field_78800_c = 0.0F;
    this.field_178723_h.field_78797_d = 2.0F;
    this.field_178723_h.field_78798_e = 0.0F;
    super.func_78087_a(par1, par2, par3, par4, par5, par6, entity);
    if (this.playerdata.isSleeping() || player.func_70608_bn()) {
      if (this.field_78116_c.field_78795_f < 0.0F) {
        this.field_78116_c.field_78795_f = 0.0F;
        this.field_178720_f.field_78795_f = 0.0F;
      }
    } else if (this.playerdata.animation == EnumAnimation.CRY) {
      this.field_78116_c.field_78795_f = 0.7F;
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
    } else if (this.field_78117_n) {
      this.field_78115_e.field_78795_f = 0.5F / (this.playerdata.getPartConfig(EnumParts.BODY)).scaleY;
    }
    func_178685_a(this.field_178722_k, this.field_178733_c);
    func_178685_a(this.field_178721_j, this.field_178731_d);
    func_178685_a(this.field_178724_i, this.field_178734_a);
    func_178685_a(this.field_178723_h, this.field_178732_b);
    func_178685_a(this.field_78115_e, this.field_178730_v);
    func_178685_a(this.field_78116_c, this.field_178720_f);
  }

  public ModelRenderer func_85181_a(Random random) {
    switch (random.nextInt(5)) {
      case 0:
        return this.field_78116_c;
      case 1:
        return this.field_78115_e;
      case 2:
        return this.field_178724_i;
      case 3:
        return this.field_178723_h;
      case 4:
        return this.field_178722_k;
      case 5:
        return this.field_178721_j;
    }
    return this.field_78116_c;
  }
}
