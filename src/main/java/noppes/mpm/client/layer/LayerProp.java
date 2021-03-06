package noppes.mpm.client.layer;

import org.lwjgl.util.vector.Vector3f;

import com.elytradev.architecture.client.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.model.animation.Animation;
import noppes.mpm.Emote;
import noppes.mpm.Prop;
import noppes.mpm.Prop.EnumType;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.util.EntityScaleManagerClient;

public class LayerProp extends LayerInterface {

	Minecraft minecraft;


	public LayerProp(RenderPlayer render) {
		super(render);
		this.minecraft = Minecraft.getMinecraft();
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		for (int i = 0; i < this.playerdata.propBase.props.size(); i++) {
			renderProp(this.playerdata.propBase.props.get(i), par7);
		}

		for (int i = 0; i < this.playerdata.propGroups.size(); i++) {
			if (this.playerdata.propGroups.get(i).hide)
			continue;

			for (int j = 0; j < this.playerdata.propGroups.get(i).props.size(); j++) {
				renderProp(this.playerdata.propGroups.get(i).props.get(j), par7);
			}
		}
	}

	private void renderProp(Prop prop, float par7) {
		if (prop.refreshCache || (this.playerdata.player == minecraft.thePlayer && minecraft.currentScreen instanceof GuiNPCInterface)) prop.refreshCache(this.player);

		if (prop.type == EnumType.ITEM) {
			renderItemProp(prop, par7);
		} else if (prop.type == EnumType.PARTICLE) {
			renderParticleProp(prop, par7);
		}
	}

	private void renderItemProp(Prop prop, float par7) {
		if ((!prop.hide && prop.propBodyPart != null) &&!(prop.sheathProp && (prop.itemStack.getItem().equals(this.player.getHeldItemMainhand().getItem()) || prop.itemStack.getItem().equals(this.player.getHeldItemOffhand().getItem())))) {
			ModelRenderer motherRenderer = null;
			ModelRenderer propRenderer = null;

			float sneakModifierY = 0.0F;
			float sneakModifierZ = 0.0F;

			float partXrotation = 0.0F;
			float partYrotation = 0.0F;

			EntityLivingBase entity = this.playerdata.getEntity(this.player);
			if (entity == null) {
				if (prop.partIndex > 5) return;

				motherRenderer = new ModelRenderer(this.model);
				propRenderer = new ModelRenderer(this.model);

				if (prop.partIndex == 5 && this.playerdata.player == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0 && !(minecraft.currentScreen instanceof GuiNPCInterface))
					return;

				if (this.player.isSneaking()) {
					switch(prop.partIndex) {
						case 2:
						case 3:
							sneakModifierY = 0.1875F;
							sneakModifierZ = -0.25F;
							break;
						case 5:
							sneakModifierY = -0.0625F;
							break;
					}
				}

				if (prop.partIndex >= 0) {
					partXrotation = prop.propBodyPart.rotateAngleX;
					partYrotation = prop.propBodyPart.rotateAngleY;
				}
			} else {
				ModelBase entityModel = (((RenderLivingBase) minecraft.getRenderManager().getEntityRenderObject(entity)).getMainModel());

				motherRenderer = new ModelRenderer(entityModel);
				propRenderer = new ModelRenderer(entityModel);

				if (this.player.isSneaking()) {
					if (entityModel instanceof ModelBiped) {
						sneakModifierY = 0.40F;
					} else {
						sneakModifierY = 0.20F;
					}
				}

				Float yRender = (float) Math.toRadians(entity.prevRenderYawOffset + ((entity.renderYawOffset - entity.prevRenderYawOffset) * Animation.getPartialTickTime()));

				if (prop.partIndex >= 0) {
					partXrotation = (float) (Math.PI + prop.propBodyPart.rotateAngleX);
					partYrotation = (float) (-prop.propBodyPart.rotateAngleY - yRender);
				} else {
					partXrotation = (float) (Math.PI);
					partYrotation = (float) (-prop.propBodyPart.rotateAngleY - yRender);
				}

				if (prop.propBodyPart != null) {
					prop.partModifierX = (float) (-(((Math.cos(yRender) * prop.propBodyPart.rotationPointX) + (Math.sin(yRender) * prop.propBodyPart.rotationPointZ)) / 16)) * this.playerdata.entityScaleX * EntityScaleManagerClient.getScaleMult(this.playerdata);
					prop.partModifierZ = (float) ((((Math.cos(-yRender) * prop.propBodyPart.rotationPointZ) + (Math.sin(-yRender) * prop.propBodyPart.rotationPointX)) / 16)) * this.playerdata.entityScaleX * EntityScaleManagerClient.getScaleMult(this.playerdata);
				}
			}

			motherRenderer.addChild(propRenderer);

			float propOffsetXCorrected;
			float propOffsetYCorrected;
			float propOffsetZCorrected;

			if (prop.partIndex < 0) {
				propOffsetXCorrected = -prop.propOffsetX;
				propOffsetYCorrected = prop.propOffsetY;
				propOffsetZCorrected = prop.propOffsetZ;

				if (entity != null) {
					motherRenderer.rotateAngleX = partXrotation;
					motherRenderer.rotateAngleY = partYrotation;
				}
			} else {
				//Calculate prop offset
				float anglePrev;
				float hyp;
				//Apply pitch
				if (prop.propOffsetZ == 0) {
					if (prop.propOffsetY <= 0) {
						anglePrev = 0.0F;
						hyp = prop.propOffsetY;
					} else {
						anglePrev = (float) Math.PI;
						hyp = -prop.propOffsetY;
					}
				} else {
					anglePrev = (float) Math.atan2(prop.propOffsetZ, prop.propOffsetY);
					hyp = (float) (prop.propOffsetZ / Math.sin(anglePrev));
				}

				float Zpitch = (float) (Math.sin(anglePrev + partXrotation) * hyp);
				float Ypitch = (float) (Math.cos(anglePrev + partXrotation) * hyp);

				//Apply yaw
				if (prop.propOffsetX == 0) {
					if (Zpitch >= 0) {
						anglePrev = 0.0F;
						hyp = Zpitch;
					} else {
						anglePrev = (float) Math.PI;
						hyp = -Zpitch;
					}
				} else {
					anglePrev = (float) Math.atan2(-prop.propOffsetX, Zpitch);
					hyp = (float) (-prop.propOffsetX / Math.sin(anglePrev));
				}

				float Xyaw = (float) (Math.sin(anglePrev + partYrotation) * hyp);
				propOffsetZCorrected = (float) (Math.cos(anglePrev + partYrotation) * hyp);

				//Apply roll
				if (Xyaw > -0.0001 && Xyaw < 0.0001) {
					if (Ypitch <= 0) {
						anglePrev = 0.0F;
						hyp = Ypitch;
					} else {
						anglePrev = (float) Math.PI;
						hyp = -Ypitch;
					}
				} else {
					anglePrev = (float) Math.atan2(Xyaw, Ypitch);
					hyp = (float) (Xyaw / Math.sin(anglePrev));
				}

				propOffsetXCorrected = (float) (Math.sin(anglePrev - prop.propBodyPart.rotateAngleZ) * hyp);
				propOffsetYCorrected = (float) (Math.cos(anglePrev - prop.propBodyPart.rotateAngleZ) * hyp);

				motherRenderer.rotateAngleX = partXrotation;
				motherRenderer.rotateAngleY = partYrotation;
				motherRenderer.rotateAngleZ = prop.propBodyPart.rotateAngleZ;
			}

			GlStateManager.pushMatrix();

			GlStateManager.translate((prop.propBodyPart.offsetX - propOffsetXCorrected - prop.partModifierX + prop.propPpOffsetX),
					(prop.propBodyPart.offsetY - propOffsetYCorrected - prop.partModifierY - prop.propPpOffsetY - sneakModifierY),
					(prop.propBodyPart.offsetZ - propOffsetZCorrected - prop.partModifierZ - prop.propPpOffsetZ - sneakModifierZ));
			motherRenderer.postRender(par7);

			GlStateManager.rotate(-prop.rotateX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(prop.rotateY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(prop.rotateZ, 0.0F, 0.0F, 1.0F);
			propRenderer.postRender(par7);

			IBakedModel iModel = null;
			ItemTransformVec3f transformVec;
			if (prop.propString.startsWith("shape:")) {
				iModel = ClientProxy.RENDERING_MANAGER.getItemBakedModel();
				transformVec = new ItemTransformVec3f(new Vector3f(75.0F, -45.0F, 0.0F), new Vector3f(0.0F, 0.15625F, 0.0F), new Vector3f(0.375F, 0.375F, 0.375F));
			} else {
				iModel = minecraft.getRenderItem().getItemModelMesher().getItemModel(prop.itemStack);
				transformVec = iModel.getItemCameraTransforms().thirdperson_right;
			}

			GlStateManager.scale((-prop.propScaleX * (transformVec.scale.x + ItemCameraTransforms.offsetScaleX)), (-prop.propScaleY * (transformVec.scale.y + ItemCameraTransforms.offsetScaleY)), (prop.propScaleZ * (transformVec.scale.z + ItemCameraTransforms.offsetScaleZ)));
			minecraft.getItemRenderer().renderItem(this.player, prop.itemStack, TransformType.NONE);

			GlStateManager.popMatrix();

			motherRenderer.childModels.remove(propRenderer);
			this.model.boxList.remove(propRenderer);
			this.model.boxList.remove(motherRenderer);
		}
	}

	private void renderParticleProp(Prop prop, float par7) {
		Boolean propHide = prop.hide;

		if (propHide == false) {
			if (System.currentTimeMillis() - prop.lastPlayed >= 1000 / prop.frequency) {
				float sneakModifierY = 0.0F;
				float sneakModifierZ = 0.0F;

				if (prop.partIndex == 5 && this.playerdata.player == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0 && !(minecraft.currentScreen instanceof GuiNPCInterface))
					return;

				EntityLivingBase entity = this.playerdata.getEntity(this.player);

				if (this.player.isSneaking()) {
					if (entity == null) {
						switch(prop.partIndex) {
							case 2:
							case 3:
								sneakModifierY = -0.125F;
								sneakModifierZ = -0.25F;
								break;
							case 5:
								sneakModifierY = -0.375F;
								break;
							case 0:
							case 1:
								sneakModifierY = -0.375F;
								break;
							case 4:
								sneakModifierY = -0.375F;
								break;
						}
					}
				}

				float propOffsetXCorrected;
				float propOffsetYCorrected;
				float propOffsetZCorrected;

				if (prop.partIndex < 0) {
					propOffsetXCorrected = -prop.propOffsetX - prop.propBodyPart.offsetX - (this.playerdata.animStates == null ? 0.0F : this.playerdata.animStates[Emote.AXIS_COUNT*Emote.MODEL + Emote.OFF_X]);
					propOffsetYCorrected = prop.propOffsetY;
					propOffsetZCorrected = prop.propOffsetZ - prop.propBodyPart.offsetZ - (this.playerdata.animStates == null ? 0.0F : this.playerdata.animStates[Emote.AXIS_COUNT*Emote.MODEL + Emote.OFF_Z]);
				} else {
					//Calculate prop offset
					float anglePrev;
					float hyp;
					//Apply pitch
					if (prop.propOffsetZ == 0) {
						if (prop.propOffsetY <= 0) {
							anglePrev = 0.0F;
							hyp = prop.propOffsetY;
						} else {
							anglePrev = (float) Math.PI;
							hyp = -prop.propOffsetY;
						}
					} else {
						anglePrev = (float) Math.atan2(prop.propOffsetZ, prop.propOffsetY);
						hyp = (float) (prop.propOffsetZ / Math.sin(anglePrev));
					}

					float Zpitch = (float) (Math.sin(anglePrev + prop.propBodyPart.rotateAngleX) * hyp);
					float Ypitch = (float) (Math.cos(anglePrev + prop.propBodyPart.rotateAngleX) * hyp);

					//Apply yaw
					if (prop.propOffsetX == 0) {
						if (Zpitch >= 0) {
							anglePrev = 0.0F;
							hyp = Zpitch;
						} else {
							anglePrev = (float) Math.PI;
							hyp = -Zpitch;
						}
					} else {
						anglePrev = (float) Math.atan2(-prop.propOffsetX, Zpitch);
						hyp = (float) (-prop.propOffsetX / Math.sin(anglePrev));
					}

					float Xyaw = (float) (Math.sin(anglePrev + prop.propBodyPart.rotateAngleY) * hyp);
					propOffsetZCorrected = (float) (Math.cos(anglePrev + prop.propBodyPart.rotateAngleY) * hyp) - prop.propBodyPart.offsetZ - (this.playerdata.animStates == null ? 0.0F : this.playerdata.animStates[Emote.AXIS_COUNT*Emote.MODEL + Emote.OFF_Z]);

					//Apply roll
					if (Xyaw > -0.0001 && Xyaw < 0.0001) {
						if (Ypitch <= 0) {
							anglePrev = 0.0F;
							hyp = Ypitch;
						} else {
							anglePrev = (float) Math.PI;
							hyp = -Ypitch;
						}
					} else {
						anglePrev = (float) Math.atan2(Xyaw, Ypitch);
						hyp = (float) (Xyaw / Math.sin(anglePrev));
					}

					propOffsetXCorrected = (float) (Math.sin(anglePrev - prop.propBodyPart.rotateAngleZ) * hyp) - prop.propBodyPart.offsetX - (this.playerdata.animStates == null ? 0.0F : this.playerdata.animStates[Emote.AXIS_COUNT*Emote.MODEL + Emote.OFF_X]);
					propOffsetYCorrected = (float) (Math.cos(anglePrev - prop.propBodyPart.rotateAngleZ) * hyp);
				}

				//Adjust for model yaw
				float propOffsetXCorrected2 = (float) (propOffsetXCorrected * Math.cos(Math.toRadians(-this.player.renderYawOffset)) + (2 * propOffsetZCorrected + sneakModifierZ) * Math.sin(Math.toRadians(this.player.renderYawOffset)));
				float propOffsetZCorrected2 = (float) ((propOffsetZCorrected + sneakModifierZ) * Math.cos(Math.toRadians(-this.player.renderYawOffset)) + propOffsetXCorrected * Math.sin(Math.toRadians(-this.player.renderYawOffset)));

				float partModifierXCorrected = 0;
				float partModifierZCorrected = 0;
				if (entity != null) {

					Float yRender = (float) Math.toRadians(entity.prevRenderYawOffset + ((entity.renderYawOffset - entity.prevRenderYawOffset) * Animation.getPartialTickTime()));

					if (prop.propBodyPart != null) {
						partModifierXCorrected = (float) -((((Math.cos(yRender) * prop.propBodyPart.rotationPointX) + (Math.sin(yRender) * prop.propBodyPart.rotationPointZ)) / 16)) * this.playerdata.entityScaleX * EntityScaleManagerClient.getScaleMult(this.playerdata);
						partModifierZCorrected = (float) -((((Math.cos(-yRender) * prop.propBodyPart.rotationPointZ) + (Math.sin(-yRender) * prop.propBodyPart.rotationPointX)) / 16)) * this.playerdata.entityScaleX * EntityScaleManagerClient.getScaleMult(this.playerdata);
					}
				} else {
					partModifierXCorrected = (float) (prop.partModifierX * Math.cos(Math.toRadians(-this.player.renderYawOffset)) + propOffsetZCorrected * Math.sin(Math.toRadians(-this.player.renderYawOffset)));
					partModifierZCorrected = (float) (prop.partModifierZ * Math.cos(Math.toRadians(-this.player.renderYawOffset)) + prop.partModifierX * Math.sin(Math.toRadians(-this.player.renderYawOffset)));
				}

				for (int i = 0 ; i < prop.amount ; i++) {
					Double propMotionPitch = Math.toRadians((prop.pitch + (2 * prop.scatter * Math.random()) - prop.scatter));
					Double propMotionYaw = Math.toRadians((prop.yaw + (2 * prop.scatter * Math.random()) - prop.scatter));

					Double propMotionXCorrected = null;
					Double propMotionYCorrected = null;
					Double propMotionZCorrected = null;
					if (prop.partIndex < 0 || prop.lockrotation) {
						//Calculate particle motion
						//Apply pitch
						propMotionYCorrected = prop.speed * Math.cos(propMotionPitch);
						Double propMotionZPitch = prop.speed * Math.sin(propMotionPitch);

						//Apply yaw
						propMotionZCorrected = propMotionZPitch * Math.cos(-propMotionYaw - Math.toRadians(this.player.renderYawOffset));
						propMotionXCorrected = propMotionZPitch * Math.sin(-propMotionYaw - Math.toRadians(this.player.renderYawOffset));
					} else {
						float anglePrev;
						Double hyp;
						//Apply pitch
						Double Zpitch = (Math.sin(prop.propBodyPart.rotateAngleX + propMotionPitch) * prop.speed);
						Double Ypitch = (Math.cos(prop.propBodyPart.rotateAngleX + propMotionPitch) * prop.speed);

						//Apply yaw
						Double Xyaw = (Math.sin(-prop.propBodyPart.rotateAngleY - Math.toRadians(this.player.renderYawOffset) + propMotionYaw) * Zpitch);
						propMotionZCorrected = (Math.cos(-prop.propBodyPart.rotateAngleY - Math.toRadians(this.player.renderYawOffset) + propMotionYaw) * Zpitch);

						//Apply roll
						if (Xyaw > -0.0001 && Xyaw < 0.0001) {
							if (Ypitch <= 0) {
								anglePrev = 0.0F;
								hyp = Ypitch;
							} else {
								anglePrev = (float) Math.PI;
								hyp = -Ypitch;
							}
						} else {
							anglePrev = (float) Math.atan2(Xyaw, Ypitch);
							hyp = (Xyaw / Math.sin(anglePrev));
						}

						propMotionXCorrected = (Math.sin(anglePrev + prop.propBodyPart.rotateAngleZ) * hyp);
						propMotionYCorrected = (Math.cos(anglePrev + prop.propBodyPart.rotateAngleZ) * hyp);
					}

					this.player.worldObj.spawnParticle(prop.particleType,
						this.player.posX - propOffsetXCorrected2 - partModifierXCorrected,
						this.player.posY + propOffsetYCorrected + prop.partModifierY - prop.propBodyPart.offsetY + sneakModifierY - (this.playerdata.animStates == null ? 0.0F : this.playerdata.animStates[Emote.AXIS_COUNT*Emote.MODEL + Emote.OFF_Y]) + this.playerdata.modelOffsetY,
						this.player.posZ + propOffsetZCorrected2 + partModifierZCorrected,
						propMotionXCorrected, propMotionYCorrected, propMotionZCorrected);
				}

				prop.lastPlayed = System.currentTimeMillis();
			}
		}
	}


	@Override
	public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
	}
}
