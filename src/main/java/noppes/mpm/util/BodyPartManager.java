package noppes.mpm.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class BodyPartManager {

	private static Map<EntityLivingBase, ArrayList<Field>> partMap;
	static {
		partMap = new HashMap<EntityLivingBase, ArrayList<Field>>();
	}

	private static void putFields(EntityLivingBase entity) {
		Minecraft mc = Minecraft.getMinecraft();
		ModelBase model = (((RenderLivingBase) mc.getRenderManager().getEntityRenderObject(entity)).getMainModel());

		ArrayList<Field> renderers = new ArrayList<Field>();
		Field[] fields;

		fields = model.getClass().getFields();

		for (Field field : fields) {
			if (field.getType() == ModelRenderer.class && !renderers.contains(field)) {
				field.setAccessible(true);
				renderers.add(field);
			}
		}

		fields = model.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.getType() == ModelRenderer.class && !renderers.contains(field)) {
				field.setAccessible(true);
				renderers.add(field);
			}
		}

		partMap.put(entity, renderers);
	}

	private static ArrayList<Field> getFields(EntityLivingBase entity) {
		if (!partMap.containsKey(entity)) putFields(entity);

		return partMap.get(entity);
	}

	public static ModelRenderer getRenderer(EntityLivingBase entity, int index) {
		if (index < 0) return null;

		Minecraft mc = Minecraft.getMinecraft();
		ModelBase model = (((RenderLivingBase) mc.getRenderManager().getEntityRenderObject(entity)).getMainModel());

		ArrayList<Field> fields = getFields(entity);

		if (index > fields.size() - 1) return null;

		Field field = fields.get(index);

		try {
			return (ModelRenderer) field.get(model);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	public static String[] partNumberArray(EntityLivingBase entity) {
		ArrayList<String> parts = new ArrayList<String>();

		for (int i = 0 ; i < getFields(entity).size() ; i++) {
			parts.add(Integer.toString(i));
		}

		String[] partsArray = new String[parts.size()];
		partsArray = parts.toArray(partsArray);

		return partsArray;
	}

	public static String[] partNumberArrayWithModel(EntityLivingBase entity) {
		String[] partsArray = partNumberArray(entity);

		String[] partsArrayWithModel = new String[partsArray.length + 1];
		partsArrayWithModel[0] = "Model";

		for (int i = 0 ; i < partsArray.length ; i++) {
			partsArrayWithModel[i + 1] = partsArray[i];
		}

		return partsArrayWithModel;
	}

	public static String[] bipedPartNames = new String[]{
		"gui.model",
		"gui.lefthand",
		"gui.righthand",
		"gui.head",
		"gui.body",
		"gui.leftfoot",
		"gui.rightfoot",
	};

	public static int convertBipedPartToPartIndex(int index) {
		int ret = 0;

		switch(index) {
		case 2:
			ret = 7;
			break;
		case -1:
			ret = -1;
			break;
		case 3:
			ret = 9;
			break;
		case 0:
			ret = 11;
			break;
		case 1:
			ret = 10;
			break;
		case 4:
			ret = 13;
			break;
		case 5:
			ret = 12;
			break;
		}

		return ret;
	}

	public static int convertPartIndexToBipedPart(int index) {
		int ret = 0;

		switch(index) {
		case 7:
			ret = 2;
			break;
		case -1:
			ret = -1;
			break;
		case 9:
			ret = 3;
			break;
		case 11:
			ret = 0;
			break;
		case 10:
			ret = 1;
			break;
		case 13:
			ret = 4;
			break;
		case 12:
			ret = 5;
			break;
		}

		return ret;
	}
}
