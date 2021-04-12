package noppes.mpm.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import noppes.mpm.client.ChatMessages;

public class LayerChatbubble extends LayerInterface implements LayerPreRender {
  public LayerChatbubble(RenderPlayer render) {
    super(render);
  }

  public void render(float par2, float par3, float par4, float par5, float par6, float par7) {}

  public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {}

  public void preRender(AbstractClientPlayer player) {
    Minecraft mc = Minecraft.func_71410_x();
    if (player == mc.field_71439_g)
      return;
    ChatMessages chat = ChatMessages.getChatMessages(player.func_70005_c_());
    if (!chat.hasMessage())
      return;
    double x = mc.field_71439_g.field_70165_t - player.field_70165_t;
    double y = mc.field_71439_g.field_70163_u - player.field_70163_u;
    double z = mc.field_71439_g.field_70161_v - player.field_70161_v;
    boolean inRange = (player.func_70032_d((mc.func_175598_ae()).field_78734_h) <= 4.0F);
    chat.renderMessages(-x, -y + 0.7D + player.field_70131_O, -z, inRange);
  }
}
