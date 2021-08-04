package noppes.mpm.client.gui.util;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiNPCInterface extends GuiScreen {
	public EntityPlayerSP player;
	public boolean drawDefaultBackground = true;
	private HashMap buttons = new HashMap();
	private HashMap textfields = new HashMap();
	private HashMap labels = new HashMap();
	private HashMap scrolls = new HashMap();
	private HashMap sliders = new HashMap();
	private HashMap extra = new HashMap();
	protected ResourceLocation background = null;
	public boolean closeOnEsc = false;
	public boolean closeOnInventory = true;
	public int guiLeft;
	public int guiTop;
	public int xSize;
	public int ySize;
	private GuiNPCInterface subgui;
	public GuiNPCInterface parent;
	public int mouseX;
	public int mouseY;

	public GuiNPCInterface() {
		this.player = Minecraft.getMinecraft().thePlayer;
		this.xSize = 200;
		this.ySize = 222;
	}

	public void setBackground(String texture) {
		this.background = new ResourceLocation("moreplayermodels", "textures/gui/" + texture);
	}

	public ResourceLocation getResource(String texture) {
		return new ResourceLocation("moreplayermodels", "textures/gui/" + texture);
	}

	@Override
	public void initGui() {
		super.initGui();
		GuiNpcTextField.unfocus();
		if (this.subgui != null) {
			this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
			this.subgui.initGui();
		}

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		this.buttonList.clear();
		this.labels.clear();
		this.textfields.clear();
		this.buttons.clear();
		this.scrolls.clear();
		this.sliders.clear();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void updateScreen() {
		if (this.subgui != null) {
			this.subgui.updateScreen();
		} else {
			Iterator var1 = this.textfields.values().iterator();

			while(var1.hasNext()) {
				GuiNpcTextField tf = (GuiNpcTextField)var1.next();
				if (tf.enabled) {
					tf.updateCursorCounter();
				}
			}

			super.updateScreen();
		}

	}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		if (this.subgui != null) {
			this.subgui.mouseClicked(i, j, k);
		} else {
			Iterator var4 = (new ArrayList(this.textfields.values())).iterator();

			while(var4.hasNext()) {
				GuiNpcTextField tf = (GuiNpcTextField)var4.next();
				if (tf.enabled) {
					tf.mouseClicked(i, j, k);
				}
			}

			if (k == 0) {
				var4 = (new ArrayList(this.scrolls.values())).iterator();

				while(var4.hasNext()) {
					GuiCustomScroll scroll = (GuiCustomScroll)var4.next();
					scroll.mouseClicked(i, j, k);
				}
			}

			this.mouseEvent(i, j, k);
			super.mouseClicked(i, j, k);
		}

	}

	public void mouseEvent(int i, int j, int k) {
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (this.subgui != null) {
			this.subgui.buttonEvent(guibutton);
		} else {
			this.buttonEvent(guibutton);
		}

	}

	public void buttonEvent(GuiButton guibutton) {
	}

	@Override
	public void keyTyped(char c, int i) {
		if (this.subgui != null) {
			this.subgui.keyTyped(c, i);
		} else {
			Iterator var3 = this.textfields.values().iterator();

			while(var3.hasNext()) {
				GuiNpcTextField tf = (GuiNpcTextField)var3.next();
				tf.textboxKeyTyped(c, i);
			}

			if (this.closeOnEsc && (i == 1 || !GuiNpcTextField.isActive() && this.isInventoryKey(i) && this.closeOnInventory)) {
				this.close();
			}
		}

	}

	@Override
	public void onGuiClosed() {
		GuiNpcTextField.unfocus();
	}

	public void close() {
		if (this.parent != null) {
			this.parent.closeSubGui(this);
		} else {
			this.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
		}

		this.save();
	}

	public void addButton(GuiNpcButton button) {
		this.buttons.put(button.id, button);
		this.buttonList.add(button);
	}

	public GuiNpcButton getButton(int i) {
		return (GuiNpcButton)this.buttons.get(i);
	}

	public void addTextField(GuiNpcTextField tf) {
		this.textfields.put(tf.id, tf);
	}

	public GuiNpcTextField getTextField(int i) {
		return (GuiNpcTextField)this.textfields.get(i);
	}

	public void addLabel(GuiNpcLabel label) {
		this.labels.put(label.id, label);
	}

	public GuiNpcLabel getLabel(int i) {
		return (GuiNpcLabel)this.labels.get(i);
	}

	public void addSlider(GuiNpcSlider slider) {
		this.sliders.put(slider.id, slider);
		this.buttonList.add(slider);
	}

	public GuiNpcSlider getSlider(int i) {
		return (GuiNpcSlider)this.sliders.get(i);
	}

	public void addScroll(GuiCustomScroll scroll) {
		scroll.setWorldAndResolution(this.mc, 350, 250);
		this.scrolls.put(scroll.id, scroll);
	}

	public GuiCustomScroll getScroll(int id) {
		return (GuiCustomScroll)this.scrolls.get(id);
	}

	public abstract void save();

	@Override
	public void drawScreen(int i, int j, float f) {
		this.mouseX = i;
		this.mouseY = j;
		if (this.subgui == null || this.subgui.drawSubGuiBackground()) {
			if (this.drawDefaultBackground) {
				this.drawDefaultBackground();
			}

			if (this.background != null && this.mc.renderEngine != null) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.mc.renderEngine.bindTexture(this.background);
				if (this.xSize > 256) {
					this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 250, this.ySize);
					this.drawTexturedModalRect(this.guiLeft + 250, this.guiTop, 256 - (this.xSize - 250), 0, this.xSize - 250, this.ySize);
				} else {
					this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
				}
			}

			Iterator var4 = this.labels.values().iterator();

			while(var4.hasNext()) {
				GuiNpcLabel label = (GuiNpcLabel)var4.next();
				label.drawLabel(this, this.fontRendererObj);
			}

			var4 = this.textfields.values().iterator();

			while(var4.hasNext()) {
				GuiNpcTextField tf = (GuiNpcTextField)var4.next();
				tf.drawTextBox();
			}

			var4 = this.scrolls.values().iterator();

			while(var4.hasNext()) {
				GuiCustomScroll scroll = (GuiCustomScroll)var4.next();
				scroll.drawScreen(i, j, f, this.hasSubGui() ? 0 : Mouse.getDWheel());
			}

			var4 = this.extra.values().iterator();

			while(var4.hasNext()) {
				GuiScreen gui = (GuiScreen)var4.next();
				gui.drawScreen(i, j, f);
			}

			super.drawScreen(i, j, f);
		}

		if (this.subgui != null) {
			GlStateManager.translate(0.0F, 0.0F, 260.0F);
			this.subgui.drawScreen(i, j, f);
			GlStateManager.translate(0.0F, 0.0F, -260.0F);
		}

	}

	public boolean drawSubGuiBackground() {
		return true;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRendererObj;
	}

	public void elementClicked() {
		if (this.subgui != null) {
			this.subgui.elementClicked();
		}

	}
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void doubleClicked() {
	}

	public boolean isInventoryKey(int i) {
		return i == this.mc.gameSettings.keyBindInventory.getKeyCode();
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}

	public void displayGuiScreen(GuiScreen gui) {
		this.mc.displayGuiScreen(gui);
	}

	public void setSubGui(GuiNPCInterface gui) {
		this.subgui = gui;
		this.subgui.parent = this;
		this.subgui.setWorldAndResolution(this.mc, this.width, this.height);
		this.initGui();
	}

	public void closeSubGui(GuiNPCInterface gui) {
		this.subgui = null;
		if (this instanceof ISubGuiListener) {
			((ISubGuiListener)this).subGuiClosed(gui);
		}

		this.initGui();
	}

	public boolean hasSubGui() {
		return this.subgui != null;
	}

	public GuiNPCInterface getSubGui() {
		return this.hasSubGui() && this.subgui.hasSubGui() ? this.subgui.getSubGui() : this.subgui;
	}

	public void drawNpc(EntityLivingBase npc, int x, int y, float zoomed, int rotation) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)(this.guiLeft + x), (float)(this.guiTop + y), 50.0F);
		float scale = 1.0F;
		if ((double)npc.height > 2.4D) {
			scale = 2.0F / npc.height;
		}

		if (npc instanceof EntityPlayer) {
		}

		GlStateManager.scale(-60.0F * scale * zoomed, 60.0F * scale * zoomed, 60.0F * scale * zoomed);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = npc.renderYawOffset;
		float f3 = npc.rotationYaw;
		float f4 = npc.rotationPitch;
		float f7 = npc.rotationYawHead;
		float f5 = (float)(this.guiLeft + x) - (float)this.mouseX;
		float f6 = (float)(this.guiTop + y) - 100.0F * scale * zoomed - (float)this.mouseY;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float)Math.atan((double)(f6 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		npc.renderYawOffset = (float)rotation;
		npc.rotationYaw = (float)Math.atan((double)(f5 / 80.0F)) * 40.0F + (float)rotation;
		npc.rotationPitch = -((float)Math.atan((double)(f6 / 40.0F))) * 20.0F;
		npc.rotationYawHead = npc.rotationYaw;
		this.mc.getRenderManager().playerViewY = 180.0F;
		this.mc.getRenderManager().doRenderEntity(npc, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		npc.prevRenderYawOffset = npc.renderYawOffset = f2;
		npc.prevRotationYaw = npc.rotationYaw = f3;
		npc.prevRotationPitch = npc.rotationPitch = f4;
		npc.prevRotationYawHead = npc.rotationYawHead = f7;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void openLink(String link) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop").invoke((Object)null);
			oclass.getMethod("browse", URI.class).invoke(object, new URI(link));
		} catch (Throwable var4) {
		}

	}
}
