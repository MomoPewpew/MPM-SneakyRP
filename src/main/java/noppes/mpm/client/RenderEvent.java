package noppes.mpm.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class TextBlockClient {
  public List<ITextComponent> lines = new ArrayList<>();

  private Style style;

  public int color = 14737632;

  public String name;

  public TextBlockClient(String name, String text, int lineWidth) {
    this(text, lineWidth, (EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
    this.name = name;
  }

  public TextBlockClient(String name, String text, int lineWidth, int color) {
    this(name, text, lineWidth);
    this.color = color;
  }

  public TextBlockClient(String text, int lineWidth) {
    this(text, lineWidth, (EntityPlayer)(Minecraft.getMinecraft()).thePlayer);
  }

  public TextBlockClient(String text, int lineWidth, EntityPlayer player) {
    this.style = new Style();
    String line = "";
    text = text.replaceAll("\n", " \n ");
    text = text.replaceAll("\r", " \r ");
    String[] words = text.split(" ");
    FontRenderer font = (Minecraft.getMinecraft()).fontRendererObj;
    for (String word : words) {
      String newLine;
      if (word.isEmpty())
        continue;
      if (word.length() == 1) {
        char c = word.charAt(0);
        if (c == '\r' || c == '\n') {
          addLine(line);
          line = "";
          continue;
        }
      }
      if (line.isEmpty()) {
        newLine = word;
      } else {
        newLine = line + " " + word;
      }
      if (font.getStringWidth(newLine) > lineWidth) {
        addLine(line);
        line = word.trim();
      } else {
        line = newLine;
      }
      continue;
    }
    if (!line.isEmpty())
      addLine(line);
  }

  private void addLine(String text) {
    TextComponentString line = new TextComponentString(text);
    line.setStyle(this.style);
    this.lines.add(line);
  }
}
