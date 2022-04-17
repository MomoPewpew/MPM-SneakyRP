package noppes.mpm.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.client.Client;
import noppes.mpm.constants.EnumPackets;

public class EntityScaleManagerClient extends EntityScaleManagerBase {
	private static Map<String, Float> entityMap;
	static {
		entityMap = new HashMap<String, Float>();
	}

	public static Float getScaleMult(EntityLivingBase entity) {
		Float mult;
		String name = getName(entity);

		if (entityMap.containsKey(name)) {
			mult = entityMap.get(name);
		} else {
			if (entity.isChild()) {
				mult = 0.5F;
			} else {
				mult = 1.0F;
			}

			entityMap.put(name, mult);

			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("name", name);
			Client.sendData(EnumPackets.ENTITY_SCALE_MULT, compound);
		}

		return mult;
	}

	public static void setScaleMult(String name, Float mult) {
		entityMap.put(name, mult);
	}
}
