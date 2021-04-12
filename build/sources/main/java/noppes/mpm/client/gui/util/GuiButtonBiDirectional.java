package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonBiDirectional extends GuiNpcButton{
	public static final ResourceLocation resource = new ResourceLocation("moreplayermodels:textures/gui/arrowbuttons.png");

	public GuiButtonBiDirectional(int id, int x, int y, int width, int height, String[] arr, int current) {
		super(id, x, y, width, height, arr, current);
	}

    public void drawButton(Minecraft mc, int mouseX, int mouseY){
        if (!this.visible)
        	return;
        boolean disabled = !this.enabled || display.length <= 1;

        boolean hover = !disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        boolean hoverL = !disabled && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height;

        boolean hoverR = !disabled && !hoverL && mouseX >= this.xPosition + width - 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(resource);

        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, disabled?20:hoverL?40:0, 11, 20);

        this.drawTexturedModalRect(this.xPosition + width - 11, this.yPosition, 11, disabled?20:hoverR?40:0, 11, 20);

        int l = 0xffffff;
        if (packedFGColour != 0){
            l = packedFGColour;
        }
        else if (!this.enabled || disabled){
            l = 10526880;
        }
        else if (hover){
            l = 16777120;
        }
        String text = "";
        float maxWidth = this.width - 36;
        if(mc.fontRendererObj.getStringWidth(displayString) > maxWidth){
        	for(int h = 0; h < displayString.length(); h++){
        		char c = displayString.charAt(h);
        		text += c;
        		if(mc.fontRendererObj.getStringWidth(text) > maxWidth)
        			break;
        	}
        	text += "...";
        }
        else
        	text = displayString;
        if(hover)
        	text = (char)167 + "n" + text;

        this.drawCenteredString(mc.fontRendererObj, text, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY){
    	int value = getValue();
    	boolean bo = super.mousePressed(minecraft, mouseX, mouseY);
    	if(bo && display != null && display.length != 0){
            boolean hoverL = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + 11 && mouseY < this.yPosition + this.height;

            boolean hoverR = !hoverL && mouseX >= this.xPosition + 11 && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if(hoverR)
            	value = (value+1) % display.length;
            if(hoverL){
            	if(value <= 0)
            		value = display.length;
            	value--;
            }
    		this.setDisplay(value);
    	}
    	return bo;
    }
}
