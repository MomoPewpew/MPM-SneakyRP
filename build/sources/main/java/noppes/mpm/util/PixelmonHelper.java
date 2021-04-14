package noppes.mpm.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
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
          if (Enabled) {
               try {
                    Class c = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.Entity2Client");
                    getPixelmonModel = c.getMethod("getModel");
                    modelSetupClass = Class.forName("com.pixelmonmod.pixelmon.client.models.PixelmonModelSmd");
                    modelSetupMethod = modelSetupClass.getMethod("setupForRender", c);
               } catch (Exception var1) {
                    LogWriter.except(var1);
                    Enabled = false;
               }

          }
     }

     public static List getPixelmonList() {
          List list = new ArrayList();
          if (!Enabled) {
               return list;
          } else {
               try {
                    Class c = Class.forName("com.pixelmonmod.pixelmon.enums.EnumPokemonModel");
                    Object[] array = c.getEnumConstants();
                    Object[] var3 = array;
                    int var4 = array.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                         Object ob = var3[var5];
                         list.add(ob.toString());
                    }
               } catch (Exception var7) {
                    LogManager.getLogger().error("getPixelmonList", var7);
               }

               return list;
          }
     }

     public static boolean isPixelmon(Entity entity) {
          return !Enabled ? false : EntityList.getEntityString(entity).contains("Pixelmon");
     }

     public static Object getModel(EntityLivingBase entity) {
          try {
               return getPixelmonModel.invoke(entity);
          } catch (Exception var2) {
               LogManager.getLogger().error("getModel", var2);
               return null;
          }
     }

     public static void setupModel(EntityLivingBase entity, Object model) {
          try {
               if (modelSetupClass.isAssignableFrom(model.getClass())) {
                    modelSetupMethod.invoke(model, entity);
               }
          } catch (Exception var3) {
               LogManager.getLogger().error("setupModel", var3);
          }

     }

     public static String getName(EntityLivingBase entity) {
          if (Enabled && isPixelmon(entity)) {
               try {
                    Method m = entity.getClass().getMethod("getName");
                    return m.invoke(entity).toString();
               } catch (Exception var2) {
                    LogManager.getLogger().error("getName", var2);
                    return "";
               }
          } else {
               return "";
          }
     }

     public static void debug(EntityLivingBase entity) {
          if (Enabled && isPixelmon(entity)) {
               try {
                    Method m = entity.getClass().getMethod("getModel");
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString((String)m.invoke(entity)));
               } catch (Exception var2) {
                    var2.printStackTrace();
               }

          }
     }
}
