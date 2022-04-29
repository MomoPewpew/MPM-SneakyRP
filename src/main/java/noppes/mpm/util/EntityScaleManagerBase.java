package noppes.mpm.util;

import noppes.mpm.ModelData;

public class EntityScaleManagerBase {
	public static String getName(ModelData data) {
		String name = data.getEntityClass().getCanonicalName();

		if (isChild(data)) {
			name += "_child";
		} else {
			name += "_adult";
		}

		return name;
	}

	public static Boolean isChild(ModelData data) {
		return (data.extra.getInteger("Age") < 0);
	}
}
