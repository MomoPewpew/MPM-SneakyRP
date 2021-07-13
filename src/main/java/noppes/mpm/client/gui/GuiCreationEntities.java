package noppes.mpm.client.gui;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import co.runed.multicharacter.addons.mpm.MPMUtil;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {
     public HashMap data = new HashMap();
     private List list;
     private GuiCustomScroll scroll;
     private boolean resetToSelected = true;

     public GuiCreationEntities() {
          Iterator var1 = ForgeRegistries.ENTITIES.getValues().iterator();

          while(var1.hasNext()) {
               EntityEntry ent = (EntityEntry)var1.next();
               String name = ent.getName();

               try {
                    Class c = ent.getEntityClass();
                    if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
                         this.data.put(name, c.asSubclass(EntityLivingBase.class));
                    }
               } catch (SecurityException var5) {
                    var5.printStackTrace();
               } catch (Exception var6) {
               }
          }

          this.list = new ArrayList(this.data.keySet());
          this.list.add(I18n.translateToLocal("gui.player"));
          Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);
          this.active = 1;
          this.xOffset = 60;
     }

     @Override
     public void initGui() {
          super.initGui();
          this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "gui.resettoplayer"));
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0);
               this.scroll.setUnsortedList(this.list);
          }

          this.scroll.guiLeft = this.guiLeft;
          this.scroll.guiTop = this.guiTop + 68;
          this.scroll.setSize(100, this.ySize - 70);
          String selected = I18n.translateToLocal("gui.player");
          if (this.entity != null) {
               Iterator var2 = this.data.entrySet().iterator();

               while(var2.hasNext()) {
                    Entry en = (Entry)var2.next();
                    if (((Class)en.getValue()).toString().equals(this.entity.getClass().toString())) {
                         selected = (String)en.getKey();
                    }
               }
          }

          this.scroll.setSelected(selected);
          if (this.resetToSelected) {
               this.scroll.scrollTo(this.scroll.getSelected());
               this.resetToSelected = false;
          }

          this.addScroll(this.scroll);
     }

     @Override
     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          if (btn.id == 10) {
               this.playerdata.setEntityClass((Class)null);
               this.resetToSelected = true;
               this.initGui();
          }

     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          this.playerdata.setEntityClass((Class)this.data.get(scroll.getSelected()));
          this.initGui();
     }

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
		// TODO Auto-generated method stub
		
	}
}
