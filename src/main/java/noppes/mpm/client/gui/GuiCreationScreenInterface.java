package noppes.mpm.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.mpm.ModelData;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.util.GuiNPCInterface;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ISubGuiListener;
import noppes.mpm.util.MPMEntityUtil;
import org.lwjgl.input.Keyboard;

public abstract class GuiCreationScreenInterface extends GuiNPCInterface implements ISubGuiListener, ISliderListener {
	public static String Message = "";
	public EntityLivingBase entity;
	public int active = 0;
	private EntityPlayer player;
	public int xOffset = 0;
	public ModelData playerdata;
	public static GuiCreationScreenInterface Gui = new GuiCreationParts();
	private static float rotation = 0.5F;

	public GuiCreationScreenInterface() {
		this.playerdata = ModelData.get(Minecraft.getMinecraft().thePlayer);
		this.xSize = 400;
		this.ySize = 240;
		this.xOffset = 140;
		this.player = Minecraft.getMinecraft().thePlayer;
		this.closeOnEsc = true;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.entity = this.playerdata.getEntity(this.mc.thePlayer);
		Keyboard.enableRepeatEvents(true);
		this.addButton(new GuiNpcButton(0, this.guiLeft, this.guiTop, 60, 20, "gui.options"));
		this.addButton(new GuiNpcButton(1, this.guiLeft + 62, this.guiTop, 60, 20, "gui.entity"));
		if (!MorePlayerModels.hasEntityPermission) {
			this.getButton(1).enabled = false;
		}
		this.addButton(new GuiNpcButton(100, this.guiLeft + 124, this.guiTop, 60, 20, "gui.props"));
		this.addButton(new GuiNpcButton(450, this.guiLeft + 124, this.guiTop + 23, 60, 20, "gui.propgroupload"));
		this.addButton(new GuiNpcButton(400, this.guiLeft + 248, this.guiTop, 60, 20, "gui.skinload"));
		this.addButton(new GuiNpcButton(601, this.guiLeft + 186, this.guiTop, 60, 20, "gui.emoteeditor"));
		this.addButton(new GuiNpcButton(651, this.guiLeft + 186, this.guiTop + 23, 60, 20, "gui.emoteload"));
		if (this.entity == null) {
			this.addButton(new GuiNpcButton(2, this.guiLeft, this.guiTop + 23, 60, 20, "gui.parts"));
		} else {
			GuiCreationExtra gui = new GuiCreationExtra();
			gui.playerdata = this.playerdata;
			if (!gui.getData(this.entity).isEmpty()) {
				this.addButton(new GuiNpcButton(2, this.guiLeft, this.guiTop + 23, 60, 20, "gui.extra"));
			} else if (this.active == 2) {
				this.openGui(new GuiCreationEntities());
				return;
			}
		}

		if (this.entity == null) {
			this.addButton(new GuiNpcButton(3, this.guiLeft + 62, this.guiTop + 23, 60, 20, "gui.scale"));
		}

		if (this.active >= 0)
		this.getButton(this.active).enabled = false;

		this.addButton(new GuiNpcButton(66, this.guiLeft + this.xSize - 20, this.guiTop, 20, 20, "X"));
		this.addLabel(new GuiNpcLabel(0, Message, this.guiLeft + 120, this.guiTop + this.ySize - 10, 16711680));
		this.getLabel(0).center(this.xSize - 120);
		this.addSlider(new GuiNpcSlider(this, 500, this.guiLeft + this.xOffset + 142, this.guiTop + 210, 120, 20, rotation));
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if (btn.id == 0) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationOptions());
		}

		else if (btn.id == 1) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationEntities());
		}

		else if (btn.id == 2) {
			this.playerdata.endPreviewEmote();
			if (this.entity == null) {
				this.openGui(new GuiCreationParts());
			} else {
				this.openGui(new GuiCreationExtra());
			}
		}

		else if (btn.id == 3) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationScale());
		}

		else if (btn.id == 66) {
			this.close();
		}

		else if (btn.id == 100) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationProps());
		}

		else if (btn.id == 400) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationSkinLoad());
		}

		else if (btn.id == 450) {
			this.playerdata.endPreviewEmote();
			this.openGui(new GuiCreationPropLoad());
		}

		else if (btn.id == 601) {
			this.openGui(new GuiCreationEmotes());
		}
		else if (btn.id == 651) {
			this.openGui(new GuiCreationEmoteLoad());
		}
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(x, y, f);
		this.entity = this.playerdata.getEntity(this.mc.thePlayer);
		EntityLivingBase entity = this.entity;
		if (entity == null) {
			entity = this.player;
		} else {
			MPMEntityUtil.Copy(this.mc.thePlayer, this.player);
		}

		this.drawNpc((EntityLivingBase)entity, this.xOffset + 200, 200, 1.0F, (int)(rotation * 360.0F - 180.0F));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void save() {
	}

	@Override
	public boolean drawSubGuiBackground() {
		return true;
	}

	public void openGui(GuiNPCInterface gui) {
		this.parent.setSubGui(gui);
		if (gui instanceof GuiCreationScreenInterface && ((GuiCreationScreenInterface) gui).active >= 0) {
			Gui = (GuiCreationScreenInterface)gui;
		}

	}

	@Override
	public void subGuiClosed(GuiNPCInterface subgui) {
		this.initGui();
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		if (slider.id == 500) {
			rotation = slider.sliderValue;
			slider.setString("" + (int)(rotation * 360.0F));
		}

	}

	@Override
	public void mousePressed(GuiNpcSlider slider) {
	}

	@Override
	public void mouseReleased(GuiNpcSlider slider) {
	}

	public EntityPlayer getPlayer() {
		return player;
	}


	@Override
	public void close() {
		super.close();
		this.playerdata.endPreviewEmote();
	}
}
