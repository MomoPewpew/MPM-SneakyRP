package noppes.mpm.util;

import net.minecraft.entity.EntityLivingBase;

public class EntityScaleManagerBase {
	public static String getName(EntityLivingBase entity) {
		String name = entity.getClass().getCanonicalName();
		if (entity.isChild()) {
			name += "_child";
		} else {
			name += "_adult";
		}

		return name;
	}
}
