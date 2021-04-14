package noppes.mpm.client.gui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.client.EntityFakeLiving;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.util.PixelmonHelper;

public class GuiCreationExtra extends GuiCreationScreenInterface implements ICustomScrollListener {
     private final String[] ignoredTags = new String[]{"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
     private final String[] booleanTags = new String[0];
     private GuiCustomScroll scroll;
     private Map data = new HashMap();
     private GuiCreationExtra.GuiType selected;

     public GuiCreationExtra() {
          this.active = 2;
     }

     @Override
     public void initGui() {
          super.initGui();
          if (this.entity == null) {
               this.openGui(new GuiCreationParts());
          } else {
               if (this.scroll == null) {
                    this.data = this.getData(this.entity);
                    this.scroll = new GuiCustomScroll(this, 0);
                    List list = new ArrayList(this.data.keySet());
                    this.scroll.setList(list);
                    if (list.isEmpty()) {
                         return;
                    }

                    this.scroll.setSelected((String)list.get(0));
               }

               this.selected = (GuiCreationExtra.GuiType)this.data.get(this.scroll.getSelected());
               if (this.selected != null) {
                    this.scroll.guiLeft = this.guiLeft;
                    this.scroll.guiTop = this.guiTop + 46;
                    this.scroll.setSize(100, this.ySize - 74);
                    this.addScroll(this.scroll);
                    this.selected.initGui();
               }
          }
     }

     public Map getData(EntityLivingBase entity) {
          Map data = new HashMap();
          NBTTagCompound compound = this.getExtras(entity);
          Set keys = compound.getKeySet();
          Iterator var5 = keys.iterator();

          while(true) {
               while(true) {
                    String name;
                    do {
                         if (!var5.hasNext()) {
                              if (PixelmonHelper.isPixelmon(entity)) {
                                   data.put("Model", new GuiCreationExtra.GuiTypePixelmon("Model"));
                              }

                              if (EntityList.getEntityString(entity).equals("tgvstyle.Dog")) {
                                   data.put("Breed", new GuiCreationExtra.GuiTypeDoggyStyle("Breed"));
                              }

                              return data;
                         }

                         name = (String)var5.next();
                    } while(this.isIgnored(name));

                    NBTBase base = compound.getTag(name);
                    if (name.equals("Age")) {
                         data.put("Child", new GuiCreationExtra.GuiTypeBoolean("Child", entity.isChild()));
                    } else if (name.equals("Color") && base.getId() == 1) {
                         data.put("Color", new GuiCreationExtra.GuiTypeByte("Color", compound.getByte("Color")));
                    } else if (base.getId() == 1) {
                         byte b = ((NBTTagByte)base).getByte();
                         if (b == 0 || b == 1) {
                              if (this.playerdata.extra.hasKey(name)) {
                                   b = this.playerdata.extra.getByte(name);
                              }

                              data.put(name, new GuiCreationExtra.GuiTypeBoolean(name, b == 1));
                         }
                    }
               }
          }
     }

     private boolean isIgnored(String tag) {
          String[] var2 = this.ignoredTags;
          int var3 = var2.length;

          for(int var4 = 0; var4 < var3; ++var4) {
               String s = var2[var4];
               if (s.equals(tag)) {
                    return true;
               }
          }

          return false;
     }

     private NBTTagCompound getExtras(EntityLivingBase entity) {
          NBTTagCompound fake = new NBTTagCompound();
          (new EntityFakeLiving(entity.worldObj)).writeEntityToNBT(fake);
          NBTTagCompound compound = new NBTTagCompound();

          try {
               entity.writeEntityToNBT(compound);
          } catch (Throwable var7) {
          }

          Set keys = fake.getKeySet();
          Iterator var5 = keys.iterator();

          while(var5.hasNext()) {
               String name = (String)var5.next();
               compound.removeTag(name);
          }

          return compound;
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          if (scroll.id == 0) {
               this.initGui();
          } else if (this.selected != null) {
               this.selected.scrollClicked(i, j, k, scroll);
          }

     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (this.selected != null) {
               this.selected.actionPerformed(btn);
          }

     }

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }

     class GuiTypeDoggyStyle extends GuiCreationExtra.GuiType {
          public GuiTypeDoggyStyle(String name) {
               super(name);
          }

          public void initGui() {
               Enum breed = null;

               try {
                    Method method = GuiCreationExtra.this.entity.getClass().getMethod("getBreedID");
                    breed = (Enum)method.invoke(GuiCreationExtra.this.entity);
               } catch (Exception var3) {
               }

               GuiCreationExtra.this.addButton(new GuiButtonBiDirectional(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"}, breed.ordinal()));
          }

          public void actionPerformed(GuiButton button) {
               if (button.id == 11) {
                    int breed = ((GuiNpcButton)button).getValue();
                    EntityLivingBase entity = GuiCreationExtra.this.playerdata.getEntity(GuiCreationExtra.this.mc.thePlayer);
                    GuiCreationExtra.this.playerdata.setExtra(entity, "breed", ((GuiNpcButton)button).getValue() + "");
                    GuiCreationExtra.this.playerdata.clearEntity();
               }
          }
     }

     class GuiTypePixelmon extends GuiCreationExtra.GuiType {
          public GuiTypePixelmon(String name) {
               super(name);
          }

          public void initGui() {
               GuiCustomScroll scroll = new GuiCustomScroll(GuiCreationExtra.this, 1);
               scroll.setSize(120, 200);
               scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
               scroll.guiTop = GuiCreationExtra.this.guiTop + 50;
               GuiCreationExtra.this.addScroll(scroll);
               scroll.setList(PixelmonHelper.getPixelmonList());
               scroll.setSelected(PixelmonHelper.getName(GuiCreationExtra.this.entity));
          }

          public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
               String name = scroll.getSelected();
               GuiCreationExtra.this.playerdata.clearEntity();
               GuiCreationExtra.this.playerdata.extra.setString("Name", name);
          }
     }

     class GuiTypeByte extends GuiCreationExtra.GuiType {
          private byte b;

          public GuiTypeByte(String name, byte b) {
               super(name);
               this.b = b;
          }

          public void initGui() {
               GuiCreationExtra.this.addButton(new GuiButtonBiDirectional(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, this.b));
          }

          public void actionPerformed(GuiButton button) {
               if (button.id == 11) {
                    GuiCreationExtra.this.playerdata.extra.setByte(this.name, (byte)((GuiNpcButton)button).getValue());
                    GuiCreationExtra.this.playerdata.clearEntity();
               }
          }
     }

     class GuiTypeBoolean extends GuiCreationExtra.GuiType {
          private boolean bo;

          public GuiTypeBoolean(String name, boolean bo) {
               super(name);
               this.bo = bo;
          }

          public void initGui() {
               GuiCreationExtra.this.addButton(new GuiNpcButtonYesNo(11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 50, 60, 20, this.bo));
          }

          public void actionPerformed(GuiButton button) {
               if (button.id == 11) {
                    this.bo = ((GuiNpcButtonYesNo)button).getBoolean();
                    if (this.name.equals("Child")) {
                         GuiCreationExtra.this.playerdata.extra.setInteger("Age", this.bo ? -24000 : 0);
                         GuiCreationExtra.this.playerdata.clearEntity();
                    } else {
                         GuiCreationExtra.this.playerdata.extra.setBoolean(this.name, this.bo);
                         GuiCreationExtra.this.playerdata.clearEntity();
                    }

               }
          }
     }

     abstract class GuiType {
          public String name;

          public GuiType(String name) {
               this.name = name;
          }

          public void initGui() {
          }

          public void actionPerformed(GuiButton button) {
          }

          public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          }
     }
}
