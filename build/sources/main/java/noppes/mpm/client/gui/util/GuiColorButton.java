package noppes.mpm.client.gui.util;

import net.minecraft.client.Minecraft;

public class GuiColorButton extends GuiNpcButton{
	public int color;
	public GuiColorButton(int id, int x, int y,	int color) {
		super(id, x, y, 50, 20, "");
		this.color = color;
	}
    public void drawButton(Minecraft mc, int mouseX, int mouseY){
        if (!this.visible)
        	return;
        drawRect(xPosition, yPosition, xPosition + 50, yPosition + 20, 0xFF000000 + color);
    }

}
