package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.client.ChatMessages;

public class LayerChatbubble extends LayerInterface implements LayerPreRender {
     public LayerChatbubble(RenderPlayer render) {
          super(render);
     }

     public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
     }

     public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
     }

     public void preRender(AbstractClientPlayer player) {
          Minecraft mc = Minecraft.getMinecraft();
          if (player != mc.player) {
               ChatMessages chat = ChatMessages.getChatMessages(player.getName());
               if (chat.hasMessage()) {
                    double x = mc.player.posX - player.posX;
                    double y = mc.player.posY - player.posY;
                    double z = mc.player.posZ - player.posZ;
                    boolean inRange = player.getDistance(mc.getRenderManager().renderViewEntity) <= 4.0F;
                    chat.renderMessages(-x, -y + 0.7D + (double)player.height, -z, inRange);
               }
          }
     }
}
