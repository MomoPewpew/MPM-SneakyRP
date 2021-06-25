package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelData;
import noppes.mpm.client.Client;
import noppes.mpm.client.Preset;
import noppes.mpm.client.PresetController;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.constants.EnumPackets;

public class GuiMPM extends GuiNPCInterface implements ICustomScrollListener, ISubGuiListener {
     public static final ResourceLocation resource = new ResourceLocation("moreplayermodels", "textures/gui/smallbg.png");
     public ModelData playerdata;
     protected NBTTagCompound original = new NBTTagCompound();
     private GuiCustomScroll scroll = null;

     public GuiMPM() {
          this.playerdata = ModelData.get(Minecraft.getMinecraft().thePlayer);
          this.original = this.playerdata.writeToNBT();
          this.xSize = 182;
          this.ySize = 185;
          this.drawDefaultBackground = false;
          this.closeOnEsc = true;
          if (PresetController.instance.presets.isEmpty()) {
               PresetController.instance.load();
          }

     }

     @Override
     public void initGui() {
          super.initGui();
          if (this.scroll == null) {
               this.scroll = new GuiCustomScroll(this, 0);
               this.scroll.setSize(80, 160);
          }

          List list = new ArrayList();
          Iterator var2 = PresetController.instance.presets.values().iterator();

          while(var2.hasNext()) {
               Preset preset = (Preset)var2.next();
               if (preset.menu) {
                    list.add(preset.name);
               }
          }

          this.scroll.setList(list);
          this.scroll.setSelected(PresetController.instance.selected);
          if (!this.scroll.hasSelected()) {
               this.scroll.selected = 0;
          }

          this.scroll.guiLeft = this.guiLeft + 4;
          this.scroll.guiTop = this.guiTop + 14;
          this.addScroll(this.scroll);
          this.addButton(new GuiNpcButton(0, this.guiLeft + 4, this.guiTop + 176, 20, 20, "+"));
          this.addButton(new GuiNpcButton(1, this.guiLeft + 26, this.guiTop + 176, 20, 20, "-"));
          this.getButton(1).enabled = this.scroll.getList().size() > 1;
          this.addButton(new GuiNpcButton(2, this.guiLeft + 48, this.guiTop + 176, 60, 20, "selectServer.edit"));
          this.addButton(new GuiNpcButton(3, this.guiLeft + 110, this.guiTop + 176, 68, 20, "gui.config"));
     }

     @Override
     public void drawScreen(int i, int j, float f) {
          this.drawDefaultBackground();
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          this.mc.renderEngine.bindTexture(resource);
          this.drawTexturedModalRect(this.guiLeft, this.guiTop + 8, 0, 0, this.xSize, 192);
          super.drawScreen(i, j, f);
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          GuiInventory.drawEntityOnScreen(this.guiLeft + 130, this.guiTop + 130, 40, (float)(this.guiLeft + 130 - i), (float)(this.guiTop + 60 - j), this.player);
     }

     @Override
     public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
          Preset preset = PresetController.instance.getPreset(scroll.getSelected());
          if (preset != null) {
               this.playerdata.readFromNBT(preset.data.writeToNBT());
               PresetController.instance.selected = preset.name;
          }

     }

     @Override
     protected void actionPerformed(GuiButton button) {
          if (button instanceof GuiNpcButton) {
               if (button.id == 0) {
                    this.setSubGui(new GuiCreationLoad());
               }

               if (button.id == 1) {
                    GuiYesNo gui = new GuiYesNo((result, id) -> {
                         if (result) {
                              PresetController.instance.removePreset(this.scroll.getSelected());
                              this.scroll.getList().remove(this.scroll.getSelected());
                              Preset preset = PresetController.instance.getPreset((String)this.scroll.getList().get(0));
                              this.playerdata.readFromNBT(preset.data.writeToNBT());
                              PresetController.instance.selected = preset.name;
                         }

                         Minecraft.getMinecraft().displayGuiScreen(this);
                    }, "", I18n.translateToLocal("message.delete"), 0);
                    this.mc.displayGuiScreen(gui);
               }

               if (button.id == 2) {
                    try {
                         this.setSubGui((GuiNPCInterface)GuiCreationScreenInterface.Gui.getClass().newInstance());
                    } catch (InstantiationException var3) {
                    } catch (IllegalAccessException var4) {
                    }
               }

               if (button.id == 3) {
                    this.setSubGui(new GuiConfig());
               }

          }
     }

     @Override
     public void save() {
          NBTTagCompound newCompound = this.playerdata.writeToNBT();
          if (!this.original.equals(newCompound)) {
               this.playerdata.save();
               this.playerdata.lastEdited = System.currentTimeMillis();
               Client.sendData(EnumPackets.UPDATE_PLAYER_DATA, newCompound);
               this.original = newCompound;
          }
     }

     @Override
     public void subGuiClosed(GuiNPCInterface subgui) {
          if (subgui instanceof GuiCreationScreenInterface) {
               Preset p = PresetController.instance.getPreset(this.getScroll(0).getSelected());
               if (p != null) {
                    p.data = this.playerdata.copy();
                    PresetController.instance.save();
               }
          }

          this.playerdata.propSyncClient();
     }

     @Override
     public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
     }
}
