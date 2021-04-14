package noppes.mpm.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;

public class PresetController {
     public HashMap presets = new HashMap();
     private File dir;
     public static PresetController instance;
     public String selected = "Default";

     public PresetController(File dir) {
          instance = this;
          this.dir = dir;
     }

     public Preset getPreset(String username) {
          if (this.presets.isEmpty()) {
               this.load();
          }

          return username != null && !username.isEmpty() ? (Preset)this.presets.get(username.toLowerCase()) : null;
     }

     public void load() {
          NBTTagCompound compound = this.loadPreset();
          HashMap presets = new HashMap();
          if (compound != null) {
               if (compound.hasKey("PresetSelected")) {
                    this.selected = compound.getString("PresetSelected");
               }

               NBTTagList list = compound.getTagList("Presets", 10);

               for(int i = 0; i < list.tagCount(); ++i) {
                    NBTTagCompound comp = list.getCompoundTagAt(i);
                    Preset preset = new Preset();
                    preset.readFromNBT(comp);
                    presets.put(preset.name.toLowerCase(), preset);
               }
          }

          if (presets.isEmpty()) {
               Preset preset = new Preset();
               preset.data = ModelData.get(Minecraft.getMinecraft().player);
               preset.name = "Default";
               preset.menu = true;
               presets.put("default", preset);
               ModelData data = new ModelData();
               preset = new Preset();
               preset.name = "Normal";
               preset.data = data;
               preset.menu = true;
               presets.put("normal", preset);
          }

          this.presets = presets;
     }

     private NBTTagCompound loadPreset() {
          String filename = "presets.dat";

          File file;
          try {
               file = new File(this.dir, filename);
               return !file.exists() ? null : CompressedStreamTools.readCompressed(new FileInputStream(file));
          } catch (Exception var4) {
               LogWriter.except(var4);

               try {
                    file = new File(this.dir, filename + "_old");
                    return !file.exists() ? null : CompressedStreamTools.readCompressed(new FileInputStream(file));
               } catch (Exception var3) {
                    LogWriter.except(var3);
                    return null;
               }
          }
     }

     public void save() {
          NBTTagCompound compound = new NBTTagCompound();
          NBTTagList list = new NBTTagList();
          Iterator var3 = this.presets.values().iterator();

          while(var3.hasNext()) {
               Preset preset = (Preset)var3.next();
               list.appendTag(preset.writeToNBT());
          }

          compound.setTag("Presets", list);
          compound.setString("PresetSelected", this.selected);
          this.savePreset(compound);
     }

     private void savePreset(NBTTagCompound compound) {
          String filename = "presets.dat";

          try {
               File file = new File(this.dir, filename + "_new");
               File file1 = new File(this.dir, filename + "_old");
               File file2 = new File(this.dir, filename);
               CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
               if (file1.exists()) {
                    file1.delete();
               }

               file2.renameTo(file1);
               if (file2.exists()) {
                    file2.delete();
               }

               file.renameTo(file2);
               if (file.exists()) {
                    file.delete();
               }
          } catch (Exception var6) {
               LogWriter.except(var6);
               var6.printStackTrace();
          }

     }

     public void addPreset(Preset preset) {
          this.presets.put(preset.name.toLowerCase(), preset);
          this.save();
     }

     public void removePreset(String preset) {
          if (preset != null) {
               this.presets.remove(preset.toLowerCase());
               this.save();
          }
     }
}
