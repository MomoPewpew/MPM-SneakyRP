package noppes.mpm.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class TextBlockClient {
     public List lines;
     private Style style;
     public int color;
     public String name;

     public TextBlockClient(String name, String text, int lineWidth) {
          this(text, lineWidth, Minecraft.getMinecraft().player);
          this.name = name;
     }

     public TextBlockClient(String name, String text, int lineWidth, int color) {
          this(name, text, lineWidth);
          this.color = color;
     }

     public TextBlockClient(String text, int lineWidth) {
          this(text, lineWidth, Minecraft.getMinecraft().player);
     }

     public TextBlockClient(String text, int lineWidth, EntityPlayer player) {
          this.lines = new ArrayList();
          this.color = 14737632;
          this.style = new Style();
          String line = "";
          text = text.replaceAll("\n", " \n ");
          text = text.replaceAll("\r", " \r ");
          String[] words = text.split(" ");
          FontRenderer font = Minecraft.getMinecraft().fontRenderer;
          String[] var7 = words;
          int var8 = words.length;

          for(int var9 = 0; var9 < var8; ++var9) {
               String word = var7[var9];
               if (!word.isEmpty()) {
                    if (word.length() == 1) {
                         char c = word.charAt(0);
                         if (c == '\r' || c == '\n') {
                              this.addLine(line);
                              line = "";
                              continue;
                         }
                    }

                    String newLine;
                    if (line.isEmpty()) {
                         newLine = word;
                    } else {
                         newLine = line + " " + word;
                    }

                    if (font.getStringWidth(newLine) > lineWidth) {
                         this.addLine(line);
                         line = word.trim();
                    } else {
                         line = newLine;
                    }
               }
          }

          if (!line.isEmpty()) {
               this.addLine(line);
          }

     }

     private void addLine(String text) {
          TextComponentString line = new TextComponentString(text);
          line.setStyle(this.style);
          this.lines.add(line);
     }
}
