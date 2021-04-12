package noppes.mpm.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import noppes.mpm.LogWriter;
import org.apache.logging.log4j.LogManager;

public class PixelmonHelper {
  public static boolean Enabled = false;

  private static Method getPixelmonModel = null;

  private static Class modelSetupClass;

  private static Method modelSetupMethod;

  public static void load() {
    Enabled = Loader.isModLoaded("pixelmon");
    if (!Enabled)
      return;
    try {
      Class<?> c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client");
      getPixelmonModel = c.getMethod("getModel", new Class[0]);
      modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
      modelSetupMethod = modelSetupClass.getMethod("setupForRender", new Class[] { c });
    } catch (Exception e) {
      LogWriter.except(e);
      Enabled = false;
    }
  }

  public static List<String> getPixelmonList() {
    List<String> list = new ArrayList<>();
    if (!Enabled)
      return list;
    try {
      Class<?> c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemonModel");
      Object[] array = c.getEnumConstants();
      for (Object ob : array)
        list.add(ob.toString());
    } catch (Exception e) {
      LogManager.getLogger().error("getPixelmonList", e);
    }
    return list;
  }

  public static boolean isPixelmon(Entity entity) {
    if (!Enabled)
      return false;
    return EntityList.func_75621_b(entity).contains("Pixelmon");
  }

  public static Object getModel(EntityLivingBase entity) {
    try {
      return getPixelmonModel.invoke(entity, new Object[0]);
    } catch (Exception e) {
      LogManager.getLogger().error("getModel", e);
      return null;
    }
  }

  public static void setupModel(EntityLivingBase entity, Object model) {
    try {
      if (modelSetupClass.isAssignableFrom(model.getClass()))
        modelSetupMethod.invoke(model, new Object[] { entity });
    } catch (Exception e) {
      LogManager.getLogger().error("setupModel", e);
    }
  }

  public static String getName(EntityLivingBase entity) {
    if (!Enabled || !isPixelmon((Entity)entity))
      return "";
    try {
      Method m = entity.getClass().getMethod("getName", new Class[0]);
      return m.invoke(entity, new Object[0]).toString();
    } catch (Exception e) {
      LogManager.getLogger().error("getName", e);
      return "";
    }
  }

  public static void debug(EntityLivingBase entity) {
    if (!Enabled || !isPixelmon((Entity)entity))
      return;
    try {
      Method m = entity.getClass().getMethod("getModel", new Class[0]);
      (Minecraft.func_71410_x()).field_71439_g.func_145747_a((ITextComponent)new TextComponentString((String)m.invoke(entity, new Object[0])));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
