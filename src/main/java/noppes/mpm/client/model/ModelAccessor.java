/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:50 (GMT)]
 */
package noppes.mpm.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import noppes.mpm.client.model.ModelBipedAlt;
import noppes.mpm.client.model.ModelPlayerAlt;
import aurelienribon.tweenengine.TweenAccessor;
// import noppes.mpm.LogWriter;

import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class ModelAccessor implements TweenAccessor<ModelPlayerAlt> {

	public static final ModelAccessor INSTANCE = new ModelAccessor();

	public static final int ROT_X = 0;
	public static final int ROT_Y = 1;
	public static final int ROT_Z = 2;
	public static final int OFF_X = 3;
	public static final int OFF_Y = 4;
	public static final int OFF_Z = 5;

	public static final int MODEL_PROPS = 6;
	public static final int BODY_PARTS = 7;
	public static final int STATE_COUNT = MODEL_PROPS * BODY_PARTS;

	public static final int HEAD = 0;
	public static final int BODY = MODEL_PROPS;
	public static final int RIGHT_ARM = 2 * MODEL_PROPS;
	public static final int LEFT_ARM = 3 * MODEL_PROPS;
	public static final int RIGHT_LEG = 4 * MODEL_PROPS;
	public static final int LEFT_LEG = 5 * MODEL_PROPS;
	public static final int MODEL = 6 * MODEL_PROPS;

	public static final int HEAD_X = HEAD + ROT_X;
	public static final int HEAD_Y = HEAD + ROT_Y;
	public static final int HEAD_Z = HEAD + ROT_Z;
	public static final int BODY_X = BODY + ROT_X;
	public static final int BODY_Y = BODY + ROT_Y;
	public static final int BODY_Z = BODY + ROT_Z;
	public static final int RIGHT_ARM_X = RIGHT_ARM + ROT_X;
	public static final int RIGHT_ARM_Y = RIGHT_ARM + ROT_Y;
	public static final int RIGHT_ARM_Z = RIGHT_ARM + ROT_Z;
	public static final int LEFT_ARM_X = LEFT_ARM + ROT_X;
	public static final int LEFT_ARM_Y = LEFT_ARM + ROT_Y;
	public static final int LEFT_ARM_Z = LEFT_ARM + ROT_Z;
	public static final int RIGHT_LEG_X = RIGHT_LEG + ROT_X;
	public static final int RIGHT_LEG_Y = RIGHT_LEG + ROT_Y;
	public static final int RIGHT_LEG_Z = RIGHT_LEG + ROT_Z;
	public static final int LEFT_LEG_X = LEFT_LEG + ROT_X;
	public static final int LEFT_LEG_Y = LEFT_LEG + ROT_Y;
	public static final int LEFT_LEG_Z = LEFT_LEG + ROT_Z;

	public static final int MODEL_X = MODEL + ROT_X;
	public static final int MODEL_Y = MODEL + ROT_Y;
	public static final int MODEL_Z = MODEL + ROT_Z;

	public static final int HEAD_OFF_X = HEAD + OFF_X;
	public static final int HEAD_OFF_Y = HEAD + OFF_Y;
	public static final int HEAD_OFF_Z = HEAD + OFF_Z;
	public static final int BODY_OFF_X = BODY + OFF_X;
	public static final int BODY_OFF_Y = BODY + OFF_Y;
	public static final int BODY_OFF_Z = BODY + OFF_Z;
	public static final int RIGHT_ARM_OFF_X = RIGHT_ARM + OFF_X;
	public static final int RIGHT_ARM_OFF_Y = RIGHT_ARM + OFF_Y;
	public static final int RIGHT_ARM_OFF_Z = RIGHT_ARM + OFF_Z;
	public static final int LEFT_ARM_OFF_X = LEFT_ARM + OFF_X;
	public static final int LEFT_ARM_OFF_Y = LEFT_ARM + OFF_Y;
	public static final int LEFT_ARM_OFF_Z = LEFT_ARM + OFF_Z;
	public static final int RIGHT_LEG_OFF_X = RIGHT_LEG + OFF_X;
	public static final int RIGHT_LEG_OFF_Y = RIGHT_LEG + OFF_Y;
	public static final int RIGHT_LEG_OFF_Z = RIGHT_LEG + OFF_Z;
	public static final int LEFT_LEG_OFF_X = LEFT_LEG + OFF_X;
	public static final int LEFT_LEG_OFF_Y = LEFT_LEG + OFF_Y;
	public static final int LEFT_LEG_OFF_Z = LEFT_LEG + OFF_Z;

	public static final int MODEL_OFF_X = MODEL + OFF_X;
	public static final int MODEL_OFF_Y = MODEL + OFF_Y;
	public static final int MODEL_OFF_Z = MODEL + OFF_Z;

	// private static final Map<ModelPlayerAlt, float[]> MODEL_VALUES = new WeakHashMap<>();

	// public static void preRenderStep(EntityPlayer player) {
	// 	ModelPlayerAlt model;
	// 	{//get player model
	// 		Minecraft mc = Minecraft.getMinecraft();
	// 		RenderManager manager = mc.getRenderManager();
	// 		RenderPlayer render = manager.getSkinMap().get(((AbstractClientPlayer) player).getSkinType());
	// 		model = render.getMainModel();
	// 	}

	// 	float[] modelValues = MODEL_VALUES.get(model);
	// 	if (modelValues == null) return;

	// 	float offsetX = (modelValues[MODEL_OFF_X] * ModelBipedAlt.getPartConfigScale((Entity) player, 30, 4));
	// 	float offsetY = (modelValues[MODEL_OFF_Y] * ModelBipedAlt.getPartConfigScale((Entity) player, 30, 4));
	// 	float offsetZ = (modelValues[MODEL_OFF_Z] * ModelBipedAlt.getPartConfigScale((Entity) player, 30, 4));
	// 	float rotX = modelValues[MODEL_X];
	// 	float rotY = modelValues[MODEL_Y];
	// 	float rotZ = modelValues[MODEL_Z];

	// 	float height = player.height;

	// 	GlStateManager.translate(0, height / 2, 0);

	// 	GlStateManager.translate(offsetX, offsetY, offsetZ);

	// 	if (rotY != 0)
	// 		GlStateManager.rotate(rotY * 180 / (float)Math.PI, 0, 1, 0);
	// 	if (rotX != 0)
	// 		GlStateManager.rotate(rotX * 180 / (float)Math.PI, 1, 0, 0);
	// 	if (rotZ != 0)
	// 		GlStateManager.rotate(rotZ * 180 / (float)Math.PI, 0, 0, 1);

	// 	GlStateManager.translate(0, -height / 2, 0);
	// }


	public static ModelRenderer getEarsModel(ModelPlayer model) {
		return model.boxList.get(model.boxList.indexOf(model.bipedLeftArm) - 2);
	}

	@Override
	public int getValues(ModelPlayerAlt target, int tweenType, float[] returnValues, Entity entity) {
		// int axis = tweenType % MODEL_PROPS;
		// int bodyPart = tweenType - axis;

		returnValues[0] = target.states[tweenType];
		return 1;

		// if (bodyPart == MODEL) {
		// 	if (!MODEL_VALUES.containsKey(target)) {
		// 		returnValues[0] = 0;
		// 		return 1;
		// 	}

		// 	float[] values = MODEL_VALUES.get(target);
		// 	returnValues[0] = values[axis];
		// 	return 1;
		// }

		// ModelRenderer model = getBodyPart(target, bodyPart);
		// if(model == null)
		// 	return 0;

		// switch(axis) {
		// 	case ROT_X:
		// 		returnValues[0] = model.rotateAngleX; break;
		// 	case ROT_Y:
		// 		returnValues[0] = model.rotateAngleY; break;
		// 	case ROT_Z:
		// 		returnValues[0] = model.rotateAngleZ; break;
		// 	case OFF_X:
		// 		returnValues[0] = (model.offsetX / ModelBipedAlt.getPartConfigScale(entity, BODY, OFF_Y)); break;
		// 	case OFF_Y:
		// 		returnValues[0] = (model.offsetY / ModelBipedAlt.getPartConfigScale(entity, BODY, OFF_Y)); break;
		// 	case OFF_Z:
		// 		returnValues[0] = (model.offsetZ / ModelBipedAlt.getPartConfigScale(entity, BODY, OFF_Y)); break;
		// }
		// return 1;
	}

	private ModelRenderer getBodyPart(ModelPlayerAlt model, int part) {
		switch(part) {
			case HEAD : return model.bipedHead;
			case BODY : return model.bipedBody;
			case RIGHT_ARM : return model.bipedRightArm;
			case LEFT_ARM : return model.bipedLeftArm;
			case RIGHT_LEG : return model.bipedRightLeg;
			case LEFT_LEG : return model.bipedLeftLeg;
		}
		return null;
	}

	@Override
	public void setValues(ModelPlayerAlt target, int tweenType, float[] newValues, Entity entity) {
        // LogWriter.warn("ics " + tweenType + " - " + newValues[0]);
		int axis = tweenType % MODEL_PROPS;
		int bodyPart = tweenType - axis;

		target.states[tweenType] = newValues[0];

		if (bodyPart == MODEL) {
			// float val = newValues[0];
			// switch(axis) {
			// 	case ROT_X:
			// 		target.modelRotateX = val; break;
			// 	case ROT_Y:
			// 		target.modelRotateY = val; break;
			// 	case ROT_Z:
			// 		target.modelRotateZ = val; break;
			// 	case OFF_X:
			// 		target.modelOffsetX = (val); break;
			// 	case OFF_Y:
			// 		target.modelOffsetY = (val); break;
			// 	case OFF_Z:
			// 		target.modelOffsetZ = (val); break;
			// }
			target.doAnimModel = true;
		} else {
			ModelRenderer model = getBodyPart(target, bodyPart);
			messWithModel(target, model, axis, newValues[0], bodyPart, entity);
		}
		// target.animStates[tweenType] = newValues[0];

		// LogWriter.warn("sesd " + tweenType + " - " + newValues[0]);
	}

	private void messWithModel(ModelPlayerAlt biped, ModelRenderer part, int axis, float val, int bodyPart, Entity entity) {
		setPartAxis(part, axis, val, bodyPart, entity);

		// if(biped instanceof ModelPlayer)
		messWithPlayerModel((ModelPlayer) biped, part, axis, val, bodyPart, entity);
	}

	private void messWithPlayerModel(ModelPlayer biped, ModelRenderer part, int axis, float val, int bodyPart, Entity entity) {
		if(part == biped.bipedHead) {
			setPartAxis(biped.bipedHeadwear, axis, val, bodyPart, entity);
			setPartOffset(getEarsModel(biped), axis, val);
		} else if(part == biped.bipedLeftArm)
			setPartAxis(biped.bipedLeftArmwear, axis, val, bodyPart, entity);
		else if(part == biped.bipedRightArm)
			setPartAxis(biped.bipedRightArmwear, axis, val, bodyPart, entity);
		else if(part == biped.bipedLeftLeg)
			setPartAxis(biped.bipedLeftLegwear, axis, val, bodyPart, entity);
		else if(part == biped.bipedRightLeg)
			setPartAxis(biped.bipedRightLegwear, axis, val, bodyPart, entity);
		else if(part == biped.bipedBody)
			setPartAxis(biped.bipedBodyWear, axis, val, bodyPart, entity);
	}

	private void setPartOffset(ModelRenderer part, int axis, float val) {
		if(part == null)
			return;

		switch(axis) {
			case OFF_X:
				part.offsetX = val; break;
			case OFF_Y:
				part.offsetY = val; break;
			case OFF_Z:
				part.offsetZ = val; break;
		}
	}

	private void setPartAxis(ModelRenderer part, int axis, float val, int bodyPart, Entity entity) {
		if(part == null)
			return;

		switch(axis) {
			case ROT_X:
				part.rotateAngleX = val; break;
			case ROT_Y:
				part.rotateAngleY = val; break;
			case ROT_Z:
				part.rotateAngleZ = val; break;
			case OFF_X:
				part.offsetX = (val); break;
			case OFF_Y:
				part.offsetY = (val); break;
			case OFF_Z:
				part.offsetZ = (val); break;
		}
	}
}
