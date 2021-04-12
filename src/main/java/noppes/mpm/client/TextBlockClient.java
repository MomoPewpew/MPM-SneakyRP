package noppes.mpm.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class TextBlockClient{
	public List<ITextComponent> lines = new ArrayList<ITextComponent>();
	private Style style;
	public int color = 0xe0e0e0;
	public String name;
	
	public TextBlockClient(String name, String text, int lineWidth){
		this(text, lineWidth, Minecraft.getMinecraft().thePlayer);
		this.name = name;
	}

	public TextBlockClient(String name, String text, int lineWidth, int color) {
		this(name, text, lineWidth);
		this.color = color;
	}

	public TextBlockClient(String text, int lineWidth){
		this(text, lineWidth, Minecraft.getMinecraft().thePlayer);
	}
	
	public TextBlockClient(String text, int lineWidth, EntityPlayer player){
		style = new Style();
		
		String line = "";
		text = text.replaceAll("\n", " \n ");
		text = text.replaceAll("\r", " \r ");
		String[] words = text.split(" ");
		
		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		for(String word : words){
			if(word.isEmpty())
				continue;
			if(word.length() == 1){
				char c = word.charAt(0);
				if(c == '\r' || c == '\n'){
	        		addLine(line);
					line = "";
					continue;
				}
			}
			String newLine;
			if(line.isEmpty())
				newLine = word;
			else
				newLine = line + " " + word;
			
			if(font.getStringWidth(newLine) > lineWidth){
				addLine(line);
				line = word.trim();
			}
			else{
				line = newLine;
			}			
		}
		if(!line.isEmpty())
			addLine(line);
	}
	private void addLine(String text){
		TextComponentString line = new TextComponentString(text);
		line.setStyle(style);
		lines.add(line);
	}
}
