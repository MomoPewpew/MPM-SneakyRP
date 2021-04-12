package noppes.mpm.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

public class ConfigLoader {
  private boolean updateFile = false;

  private File dir;

  private String fileName;

  private Class<?> configClass;

  private LinkedList<Field> configFields;

  public ConfigLoader(Class<?> clss, File dir, String fileName) {
    if (!dir.exists())
      dir.mkdir();
    this.dir = dir;
    this.configClass = clss;
    this.configFields = new LinkedList<>();
    this.fileName = fileName + ".cfg";
    Field[] fields = this.configClass.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent((Class)ConfigProp.class))
        this.configFields.add(field);
    }
  }

  public void loadConfig() {
    try {
      File configFile = new File(this.dir, this.fileName);
      HashMap<String, Field> types = new HashMap<>();
      for (Field field : this.configFields) {
        ConfigProp prop = field.<ConfigProp>getAnnotation(ConfigProp.class);
        types.put(!prop.name().isEmpty() ? prop.name() : field.getName(), field);
      }
      if (configFile.exists()) {
        HashMap<String, Object> properties = parseConfig(configFile, types);
        for (String prop : properties.keySet()) {
          Field field = types.get(prop);
          Object obj = properties.get(prop);
          if (!obj.equals(field.get((Object)null)))
            field.set((Object)null, obj);
        }
        for (String type : types.keySet()) {
          if (!properties.containsKey(type))
            this.updateFile = true;
        }
      } else {
        this.updateFile = true;
      }
    } catch (Exception e) {
      this.updateFile = true;
      System.err.println(e.getMessage());
    }
    if (this.updateFile)
      updateConfig();
    this.updateFile = false;
  }

  private HashMap<String, Object> parseConfig(File file, HashMap<String, Field> types) throws Exception {
    HashMap<String, Object> config = new HashMap<>();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String strLine;
    while ((strLine = reader.readLine()) != null) {
      if (strLine.startsWith("#") || strLine.length() == 0)
        continue;
      int index = strLine.indexOf("=");
      if (index <= 0 || index == strLine.length()) {
        this.updateFile = true;
        continue;
      }
      String name = strLine.substring(0, index);
      String prop = strLine.substring(index + 1);
      if (!types.containsKey(name)) {
        this.updateFile = true;
        continue;
      }
      Object obj = null;
      Class<?> class2 = ((Field)types.get(name)).getType();
      if (class2.isAssignableFrom(String.class)) {
        obj = prop;
      } else if (class2.isAssignableFrom(int.class)) {
        obj = Integer.valueOf(Integer.parseInt(prop));
      } else if (class2.isAssignableFrom(short.class)) {
        obj = Short.valueOf(Short.parseShort(prop));
      } else if (class2.isAssignableFrom(byte.class)) {
        obj = Byte.valueOf(Byte.parseByte(prop));
      } else if (class2.isAssignableFrom(boolean.class)) {
        obj = Boolean.valueOf(Boolean.parseBoolean(prop));
      } else if (class2.isAssignableFrom(float.class)) {
        obj = Float.valueOf(Float.parseFloat(prop));
      } else if (class2.isAssignableFrom(double.class)) {
        obj = Double.valueOf(Double.parseDouble(prop));
      }
      if (obj != null)
        config.put(name, obj);
    }
    reader.close();
    return config;
  }

  public void updateConfig() {
    File file = new File(this.dir, this.fileName);
    try {
      if (!file.exists())
        file.createNewFile();
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      for (Field field : this.configFields) {
        ConfigProp prop = field.<ConfigProp>getAnnotation(ConfigProp.class);
        if (prop.info().length() != 0)
          out.write("#" + prop.info() + System.getProperty("line.separator"));
        String name = !prop.name().isEmpty() ? prop.name() : field.getName();
        try {
          out.write(name + "=" + field.get((Object)null).toString() + System.getProperty("line.separator"));
          out.write(System.getProperty("line.separator"));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
