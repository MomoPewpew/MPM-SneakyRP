package noppes.mpm.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import org.lwjgl.input.Keyboard;

public class GuiEditButtons extends GuiNPCInterface {
     private final String[] animations = new String[]{"gui.none", "animation.sleep", "animation.crawl", "animation.hug", "animation.sit", "animation.dance", "animation.wave", "animation.wag", "animation.bow", "animation.cry"};

     public GuiEditButtons() {
          this.closeOnEsc = true;
     }

     public void initGui() {
          super.initGui();
          int y = this.guiTop + 20;
          this.addLabel(new GuiNpcLabel(0, "message.animationmessage1", this.guiLeft, y, 16777215));
          this.addLabel(new GuiNpcLabel(6, "message.animationmessage2", this.guiLeft, y + 11, 16777215));
          this.getLabel(0).center(this.xSize);
          this.getLabel(6).center(this.xSize);
          y += 22;
          this.addButton(1, y, "MPM 1", MorePlayerModels.button1);
          y += 22;
          this.addButton(2, y, "MPM 2", MorePlayerModels.button2);
          y += 22;
          this.addButton(3, y, "MPM 3", MorePlayerModels.button3);
          y += 22;
          this.addButton(4, y, "MPM 4", MorePlayerModels.button4);
          y += 22;
          this.addButton(5, y, "MPM 5", MorePlayerModels.button5);
     }

     private void addButton(int id, int y, String title, int value) {
          KeyBinding[] var5 = Minecraft.getMinecraft().gameSettings.keyBindings;
          int var6 = var5.length;

          for(int var7 = 0; var7 < var6; ++var7) {
               KeyBinding key = var5[var7];
               if (key.getKeyDescription().equals(title)) {
                    title = title + " (" + Keyboard.getKeyName(key.getKeyCode()) + ")";
                    break;
               }
          }

          value = this.getValue(value);
          this.addButton(new GuiNpcButton(id, this.guiLeft + 50, y, 70, 20, this.animations, value));
          this.addLabel(new GuiNpcLabel(id, title, this.guiLeft, y + 5, 16777215));
     }

     private int getValue(int i) {
          if (i == 0) {
               return 0;
          } else {
               return i >= 1 && i <= 4 ? 1 : i - 3;
          }
     }

     protected void actionPerformed(GuiButton btn) {
          super.actionPerformed(btn);
          GuiNpcButton button = (GuiNpcButton)btn;
          if (button.id == 1) {
               MorePlayerModels.button1 = this.getValue(button);
               MorePlayerModels.instance.configLoader.updateConfig();
          }

          if (button.id == 2) {
               MorePlayerModels.button2 = this.getValue(button);
               MorePlayerModels.instance.configLoader.updateConfig();
          }

          if (button.id == 3) {
               MorePlayerModels.button3 = this.getValue(button);
               MorePlayerModels.instance.configLoader.updateConfig();
          }

          if (button.id == 4) {
               MorePlayerModels.button4 = this.getValue(button);
               MorePlayerModels.instance.configLoader.updateConfig();
          }

          if (button.id == 5) {
               MorePlayerModels.button5 = this.getValue(button);
               MorePlayerModels.instance.configLoader.updateConfig();
          }

          if (button.id == 66) {
               this.close();
          }

     }

     private int getValue(GuiNpcButton button) {
          int value = button.getValue();
          return value <= 1 ? value : value + 3;
     }

     public void save() {
     }
}
