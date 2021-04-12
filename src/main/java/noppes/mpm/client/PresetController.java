package noppes.mpm.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.mpm.LogWriter;
import noppes.mpm.ModelData;

public class PresetController {
  public HashMap<String, Preset> presets = new HashMap<>();

  private File dir;

  public static PresetController instance;

  public String selected = "Default";

  public PresetController(File dir) {
    instance = this;
    this.dir = dir;
  }

  public Preset getPreset(String username) {
    if (this.presets.isEmpty())
      load();
    if (username == null || username.isEmpty())
      return null;
    return this.presets.get(username.toLowerCase());
  }

  public void load() {
    NBTTagCompound compound = loadPreset();
    HashMap<String, Preset> presets = new HashMap<>();
    if (compound != null) {
      if (compound.func_74764_b("PresetSelected"))
        this.selected = compound.func_74779_i("PresetSelected");
      NBTTagList list = compound.func_150295_c("Presets", 10);
      for (int i = 0; i < list.func_74745_c(); i++) {
        NBTTagCompound comp = list.func_150305_b(i);
        Preset preset = new Preset();
        preset.readFromNBT(comp);
        presets.put(preset.name.toLowerCase(), preset);
      }
    }
    if (presets.isEmpty()) {
      Preset preset = new Preset();
      preset.data = ModelData.get((EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
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
    try {
      File file = new File(this.dir, filename);
      if (!file.exists())
        return null;
      return CompressedStreamTools.func_74796_a(new FileInputStream(file));
    } catch (Exception e) {
      LogWriter.except(e);
      try {
        File file = new File(this.dir, filename + "_old");
        if (!file.exists())
          return null;
        return CompressedStreamTools.func_74796_a(new FileInputStream(file));
      } catch (Exception exception) {
        LogWriter.except(exception);
        return null;
      }
    }
  }

  public void save() {
    NBTTagCompound compound = new NBTTagCompound();
    NBTTagList list = new NBTTagList();
    for (Preset preset : this.presets.values())
      list.func_74742_a((NBTBase)preset.writeToNBT());
    compound.func_74782_a("Presets", (NBTBase)list);
    compound.func_74778_a("PresetSelected", this.selected);
    savePreset(compound);
  }

  private void savePreset(NBTTagCompound compound) {
    String filename = "presets.dat";
    try {
      File file = new File(this.dir, filename + "_new");
      File file1 = new File(this.dir, filename + "_old");
      File file2 = new File(this.dir, filename);
      CompressedStreamTools.func_74799_a(compound, new FileOutputStream(file));
      if (file1.exists())
        file1.delete();
      file2.renameTo(file1);
      if (file2.exists())
        file2.delete();
      file.renameTo(file2);
      if (file.exists())
        file.delete();
    } catch (Exception e) {
      LogWriter.except(e);
      e.printStackTrace();
    }
  }

  public void addPreset(Preset preset) {
    this.presets.put(preset.name.toLowerCase(), preset);
    save();
  }

  public void removePreset(String preset) {
    if (preset == null)
      return;
    this.presets.remove(preset.toLowerCase());
    save();
  }
}
