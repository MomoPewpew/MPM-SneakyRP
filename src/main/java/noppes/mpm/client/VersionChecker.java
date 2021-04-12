package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;

public class VersionChecker extends Thread {
  public void run() {
	String name = '\u00A7'+ "2MorePlayerModels" + '\u00A7' + "f";
	String link = '\u00A7'+"9"+'\u00A7' + "nClick here";
    String text = name + " installed. More info at " + link;
    try {
      EntityPlayerSP entityPlayerSP1 = (Minecraft.getMinecraft()).thePlayer;
    } catch (NoSuchMethodError e) {
      return;
    }
    EntityPlayerSP entityPlayerSP;
    while ((entityPlayerSP = (Minecraft.getMinecraft()).thePlayer) == null) {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    TextComponentTranslation message = new TextComponentTranslation(text, new Object[0]);
    message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/moreplayermodels/"));
    entityPlayerSP.addChatMessage((ITextComponent)message);
  }
}
