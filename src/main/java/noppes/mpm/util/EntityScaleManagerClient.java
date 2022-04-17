package noppes.mpm.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import noppes.mpm.client.Client;
import noppes.mpm.constants.EnumPackets;

public class EntityScaleManagerClient {
	private static Map<String, Float> entityMap;
	static {
		entityMap = new HashMap<String, Float>();
	}

	public static Float getScaleMult(EntityLivingBase entity) {
		Float mult;
		String name = entity.getClass().getCanonicalName();
		if (entity.isChild()) {
			name += "_child";
		} else {
			name += "_adult";
		}

		if (entityMap.containsKey(name)) {
			mult = entityMap.get(name);
		} else {
			if (entity.isChild()) {
				mult = 0.5F;
			} else {
				mult = 1.0F;
			}
			Client.sendData(EnumPackets.ENTITY_SCALE_MULT, name);
		}

		return mult;
	}

	public static void setScaleMult(String name, Float mult) {
		entityMap.put(name, mult);
	}
}
