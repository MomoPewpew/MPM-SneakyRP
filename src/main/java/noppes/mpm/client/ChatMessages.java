package noppes.mpm.client;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import noppes.mpm.MorePlayerModels;
import org.lwjgl.opengl.GL11;

public class ChatMessages {
  private static Map<String, ChatMessages> users = new Hashtable<>();

  private Map<Long, TextBlockClient> messages = new TreeMap<>();

  private int boxLength = 46;

  private float scale = 0.5F;

  private String lastMessage = "";

  private long lastMessageTime = 0L;

  public void addMessage(String message) {
    if (!MorePlayerModels.EnableChatBubbles)
      return;
    long time = System.currentTimeMillis();
    if (message.equals(this.lastMessage) && this.lastMessageTime + 1000L > time)
      return;
    Map<Long, TextBlockClient> messages = new TreeMap<>(this.messages);
    messages.put(Long.valueOf(time), new TextBlockClient(message, this.boxLength * 4));
    if (messages.size() > 3)
      messages.remove(messages.keySet().iterator().next());
    this.messages = messages;
    this.lastMessage = message;
    this.lastMessageTime = time;
  }

  public void renderMessages(double par3, double par5, double par7, boolean inRange) {
    Map<Long, TextBlockClient> messages = getMessages();
    if (messages.isEmpty())
      return;
    if (inRange)
      render(par3, par5, par7, false);
    render(par3, par5, par7, true);
  }

  public static void drawRect(BufferBuilder tessellator, double x, double y, double x2, double y2, int color, double z) {
    if (x < x2) {
      double j1 = x;
      x = x2;
      x2 = j1;
    }
    if (y < y2) {
      double j1 = y;
      y = y2;
      y2 = j1;
    }
    float f = (color >> 24 & 0xFF) / 255.0F;
    float f1 = (color >> 16 & 0xFF) / 255.0F;
    float f2 = (color >> 8 & 0xFF) / 255.0F;
    float f3 = (color & 0xFF) / 255.0F;
    tessellator.func_181662_b(x, y, z).func_181666_a(f1, f2, f3, f).func_181675_d();
    tessellator.func_181662_b(x, y2, z).func_181666_a(f1, f2, f3, f).func_181675_d();
    tessellator.func_181662_b(x2, y2, z).func_181666_a(f1, f2, f3, f).func_181675_d();
    tessellator.func_181662_b(x2, y, z).func_181666_a(f1, f2, f3, f).func_181675_d();
  }

  public void render(double x, double y, double z, boolean depth) {
    FontRenderer font = (Minecraft.func_71410_x()).field_71466_p;
    float var13 = 1.6F;
    float var14 = 0.016666668F * var13;
    GlStateManager.func_179094_E();
    int size = 0;
    for (TextBlockClient block : this.messages.values())
      size += block.lines.size();
    Minecraft mc = Minecraft.func_71410_x();
    int textYSize = (int)((size * font.field_78288_b) * this.scale);
    GlStateManager.func_179109_b((float)x + 0.0F, (float)y + textYSize * var14, (float)z);
    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.func_179114_b(-(mc.func_175598_ae()).field_78735_i, 0.0F, 1.0F, 0.0F);
    GlStateManager.func_179114_b((mc.func_175598_ae()).field_78732_j, 1.0F, 0.0F, 0.0F);
    GlStateManager.func_179152_a(-var14, -var14, var14);
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.func_179132_a(true);
    GlStateManager.func_179140_f();
    GlStateManager.func_179147_l();
    if (depth) {
      GlStateManager.func_179126_j();
    } else {
      GlStateManager.func_179097_i();
    }
    int black = depth ? -16777216 : 1426063360;
    int white = depth ? -1140850689 : 1157627903;
    GlStateManager.func_179120_a(770, 771, 1, 0);
    GlStateManager.func_179090_x();
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    BufferBuilder tessellator = Tessellator.func_178181_a().func_178180_c();
    tessellator.func_181668_a(7, DefaultVertexFormats.field_181706_f);
    drawRect(tessellator, (-this.boxLength - 2), -2.0D, (this.boxLength + 2), (textYSize + 1), white, 0.11D);
    drawRect(tessellator, (-this.boxLength - 1), -3.0D, (this.boxLength + 1), -2.0D, black, 0.1D);
    drawRect(tessellator, (-this.boxLength - 1), (textYSize + 2), -1.0D, (textYSize + 1), black, 0.1D);
    drawRect(tessellator, 3.0D, (textYSize + 2), (this.boxLength + 1), (textYSize + 1), black, 0.1D);
    drawRect(tessellator, (-this.boxLength - 3), -1.0D, (-this.boxLength - 2), textYSize, black, 0.1D);
    drawRect(tessellator, (this.boxLength + 3), -1.0D, (this.boxLength + 2), textYSize, black, 0.1D);
    drawRect(tessellator, (-this.boxLength - 2), -2.0D, (-this.boxLength - 1), -1.0D, black, 0.1D);
    drawRect(tessellator, (this.boxLength + 2), -2.0D, (this.boxLength + 1), -1.0D, black, 0.1D);
    drawRect(tessellator, (-this.boxLength - 2), (textYSize + 1), (-this.boxLength - 1), textYSize, black, 0.1D);
    drawRect(tessellator, (this.boxLength + 2), (textYSize + 1), (this.boxLength + 1), textYSize, black, 0.1D);
    drawRect(tessellator, 0.0D, (textYSize + 1), 3.0D, (textYSize + 4), white, 0.11D);
    drawRect(tessellator, -1.0D, (textYSize + 4), 1.0D, (textYSize + 5), white, 0.11D);
    drawRect(tessellator, -1.0D, (textYSize + 1), 0.0D, (textYSize + 4), black, 0.1D);
    drawRect(tessellator, 3.0D, (textYSize + 1), 4.0D, (textYSize + 3), black, 0.1D);
    drawRect(tessellator, 2.0D, (textYSize + 3), 3.0D, (textYSize + 4), black, 0.1D);
    drawRect(tessellator, 1.0D, (textYSize + 4), 2.0D, (textYSize + 5), black, 0.1D);
    drawRect(tessellator, -2.0D, (textYSize + 4), -1.0D, (textYSize + 5), black, 0.1D);
    drawRect(tessellator, -2.0D, (textYSize + 5), 1.0D, (textYSize + 6), black, 0.1D);
    Tessellator.func_178181_a().func_78381_a();
    GlStateManager.func_179098_w();
    GlStateManager.func_179132_a(true);
    GlStateManager.func_179152_a(this.scale, this.scale, this.scale);
    int index = 0;
    for (TextBlockClient block : this.messages.values()) {
      for (ITextComponent chat : block.lines) {
        String message = chat.func_150254_d();
        font.func_78276_b(message, -font.func_78256_a(message) / 2, index * font.field_78288_b, black);
        index++;
      }
    }
    GlStateManager.func_179145_e();
    GlStateManager.func_179084_k();
    GlStateManager.func_179126_j();
    GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.func_179121_F();
  }

  public static ChatMessages getChatMessages(String username) {
    if (users.containsKey(username))
      return users.get(username);
    ChatMessages chat = new ChatMessages();
    users.put(username, chat);
    return chat;
  }

  private static Pattern[] patterns = new Pattern[] { Pattern.compile("^<+([a-zA-z0-9_]{2,16})>[:]? (.*)"),
      Pattern.compile("^\\[.*[\\]]{1,16}[^a-zA-z0-9]?([a-zA-z0-9_]{2,16})[:]? (.*)"),
      Pattern.compile("^[a-zA-z0-9_]{2,10}[^a-zA-z0-9]([a-zA-z0-9_]{2,16})[:]? (.*)") };

  public static void parseMessage(String toParse) {
    toParse = toParse.replaceAll(", "");
    for (Pattern pattern : patterns) {
      Matcher m = pattern.matcher(toParse);
      if (m.find()) {
        String username = m.group(1);
        if (validPlayer(username)) {
          String message = m.group(2);
          getChatMessages(username).addMessage(message);
          return;
        }
      }
    }
  }

  public static void test() {
    test("<Sirnoppes01> :)", "Sirnoppes01: :)");
    test("<Sirnoppes01> hey", "Sirnoppes01: hey");
    test("<Sir_noppes> hey", "Sir_noppes: hey");
    test("<Sirnoppes>: hey", "Sirnoppes: hey");
    test("[member]Sirnoppes: hey", "Sirnoppes: hey");
    test("[member]Sirnoppes01: hey", "Sirnoppes01: hey");
    test("[member]Sir_noppes: hey", "Sir_noppes: hey");
    test("[member] Sirnoppes: hey", "Sirnoppes: hey");
    test("[g][member]Sirnoppes: hey", "Sirnoppes: hey");
    test("[g] [member]Sirnoppes: hey", "Sirnoppes: hey");
    test("[g] [member]-Sirnoppes: hey", "Sirnoppes: hey");
    test("[Player755: Teleported Player755 to Player885]", "");
    test("member Sirnoppes: hey", "Sirnoppes: hey");
    test("member-Sirnoppes: hey", "Sirnoppes: hey");
    test("member: Sirnoppes: hey", "");
  }

  private static void test(String toParse, String result) {
    for (Pattern pattern : patterns) {
      Matcher m = pattern.matcher(toParse);
      if (m.find()) {
        String username = m.group(1);
        String message = m.group(2);
        if (message != null && username != null) {
          if (result.isEmpty()) {
            System.err.println("failed: " + toParse + " - " + username + ": " + message);
            return;
          }
          if ((username + ": " + message).equals(result)) {
            System.out.println("success: " + toParse);
            return;
          }
        }
      }
    }
    if (result.isEmpty()) {
      System.out.println("success: " + toParse);
    } else {
      System.err.println("failed: " + toParse);
    }
  }

  private static boolean validPlayer(String username) {
    return ((Minecraft.func_71410_x()).field_71441_e.func_72924_a(username) != null);
  }

  private Map<Long, TextBlockClient> getMessages() {
    Map<Long, TextBlockClient> messages = new TreeMap<>();
    long time = System.currentTimeMillis();
    for (Map.Entry<Long, TextBlockClient> entry : this.messages.entrySet()) {
      if (time > ((Long)entry.getKey()).longValue() + 10000L)
        continue;
      messages.put(entry.getKey(), entry.getValue());
    }
    return this.messages = messages;
  }

  public boolean hasMessage() {
    return !this.messages.isEmpty();
  }
}
