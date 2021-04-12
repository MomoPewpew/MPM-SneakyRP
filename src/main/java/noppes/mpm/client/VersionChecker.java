package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;

public class VersionChecker extends Thread {
  public void run() {
    String name = ";
    String link = "here";
    String text = name + " installed. More info at " + link;
    try {
      EntityPlayerSP entityPlayerSP1 = (Minecraft.func_71410_x()).field_71439_g;
    } catch (NoSuchMethodError e) {
      return;
    }
    EntityPlayerSP entityPlayerSP;
    while ((entityPlayerSP = (Minecraft.func_71410_x()).field_71439_g) == null) {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    TextComponentTranslation message = new TextComponentTranslation(text, new Object[0]);
    message.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/moreplayermodels/"));
    entityPlayerSP.func_145747_a((ITextComponent)message);
  }
}
