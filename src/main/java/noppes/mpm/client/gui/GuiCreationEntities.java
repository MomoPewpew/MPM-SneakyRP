package noppes.mpm.client.gui;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {
  public HashMap<String, Class<? extends EntityLivingBase>> data = new HashMap<>();

  private List<String> list;

  private GuiCustomScroll scroll;

  private boolean resetToSelected = true;

  public GuiCreationEntities() {
    for (EntityEntry ent : ForgeRegistries.ENTITIES.getValues()) {
      String name = ent.getName();
      try {
        Class<? extends Entity> c = ent.getEntityClass();
        if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(new Class[] { World.class }) != null && !Modifier.isAbstract(c.getModifiers()) &&
          Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof net.minecraft.client.renderer.entity.RenderLivingBase)
          this.data.put(name, c.asSubclass(EntityLivingBase.class));
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (Exception exception) {}
    }
    this.list = new ArrayList<>(this.data.keySet());
    this.list.add(I18n.translateToLocal("gui.player"));
    Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);
    this.active = 1;
    this.xOffset = 60;
  }

  @Override
  public void initGui() {
    super.initGui();
    addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "gui.resettoplayer"));
    if (this.scroll == null) {
      this.scroll = new GuiCustomScroll((GuiScreen)this, 0);
      this.scroll.setUnsortedList(this.list);
    }
    this.scroll.guiLeft = this.guiLeft;
    this.scroll.guiTop = this.guiTop + 68;
    this.scroll.setSize(100, this.ySize - 70);
    String selected = I18n.translateToLocal("gui.player");
    if (this.entity != null)
      for (Map.Entry<String, Class<? extends EntityLivingBase>> en : this.data.entrySet()) {
        if (((Class)en.getValue()).toString().equals(this.entity.getClass().toString()))
          selected = en.getKey();
      }
    this.scroll.setSelected(selected);
    if (this.resetToSelected) {
      this.scroll.scrollTo(this.scroll.getSelected());
      this.resetToSelected = false;
    }
    addScroll(this.scroll);
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    super.actionPerformed(btn);
    if (btn.id == 10) {
      this.playerdata.setEntityClass(null);
      this.resetToSelected = true;
      initGui();
    }
  }

  @Override
  public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
    this.playerdata.setEntityClass(this.data.get(scroll.getSelected()));
    initGui();
  }

  @Override
  public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {}
}
