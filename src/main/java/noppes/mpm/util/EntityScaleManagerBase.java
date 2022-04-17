package noppes.mpm.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public class EntityScaleManagerBase {
	public static String getName(EntityLivingBase entity) {
		String name = entity.getClass().getCanonicalName();

		Minecraft mc = Minecraft.getMinecraft();
		ModelBase model = (((RenderLivingBase) mc.getRenderManager().getEntityRenderObject(entity)).getMainModel());

		if (model.isChild) {
			name += "_child";
		} else {
			name += "_adult";
		}

		return name;
	}
}
