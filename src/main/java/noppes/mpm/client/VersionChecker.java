package noppes.mpm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;

public class VersionChecker extends Thread {
	@Override
     public void run() {
		String name = '\u00A7'+ "2MorePlayerModels" + '\u00A7' + "f";
		String link = '\u00A7'+"9"+'\u00A7' + "nClick here";
		String text =  name +" installed. More info at " + link;

          EntityPlayerSP player;
          try {
               player = Minecraft.getMinecraft().thePlayer;
          } catch (NoSuchMethodError var7) {
               return;
          }

          while((player = Minecraft.getMinecraft().thePlayer) == null) {
               try {
                    Thread.sleep(2000L);
               } catch (InterruptedException var6) {
                    var6.printStackTrace();
               }
          }

          TextComponentTranslation message = new TextComponentTranslation(text, new Object[0]);
          message.getStyle().setClickEvent(new ClickEvent(Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/moreplayermodels/"));
          //player.addChatMessage(message);
     }
}
