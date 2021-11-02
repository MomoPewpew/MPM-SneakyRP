package noppes.mpm.client.gui;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.mpm.MorePlayerModels;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.client.gui.util.ITextfieldListener;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener, ITextfieldListener  {
	public HashMap data = new HashMap();
	private List list;
	private GuiCustomScroll scroll;
	private boolean resetToSelected = true;
	private static final float minScale = 0.5F;
	private static final float maxScale = 1.5F;
	private Boolean initiating = false;


	public GuiCreationEntities() {
		Iterator var1 = ForgeRegistries.ENTITIES.getValues().iterator();

		while(var1.hasNext()) {
			EntityEntry ent = (EntityEntry)var1.next();
			String name = ent.getName();

			try {
				Class c = ent.getEntityClass();
				if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
					if (!MorePlayerModels.entityNamesRemovedFromGui.contains(name.toLowerCase()))
					this.data.put(name, c.asSubclass(EntityLivingBase.class));
				}
			} catch (SecurityException var5) {
				var5.printStackTrace();
			} catch (Exception var6) {
			}
		}

		this.list = new ArrayList(this.data.keySet());
		this.list.add(I18n.translateToLocal("gui.player"));
		Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);
		this.active = 1;
		this.xOffset = 60;
	}

	@Override
	public void initGui() {
		super.initGui();

		if (!MorePlayerModels.hasEntityPermission) {
			this.openGui(new GuiCreationOptions());
			return;
		}

		this.initiating = true;

		this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "gui.resettoplayer"));
		if (this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0);
			this.scroll.setUnsortedList(this.list);
		}

		this.scroll.guiLeft = this.guiLeft;
		this.scroll.guiTop = this.guiTop + 45 + 22;
		this.scroll.setSize(100, this.ySize - 52 - 22);
		String selected = I18n.translateToLocal("gui.player");
		if (this.entity != null) {
			Iterator var2 = this.data.entrySet().iterator();

			while(var2.hasNext()) {
				Entry en = (Entry)var2.next();
				if (((Class)en.getValue()).toString().equals(this.entity.getClass().toString())) {
					selected = (String)en.getKey();
				}
			}
		}

		this.scroll.setSelected(selected);
		if (this.resetToSelected) {
			this.scroll.scrollTo(this.scroll.getSelected());
			this.resetToSelected = false;
		}

		this.addScroll(this.scroll);

		int x = this.guiLeft + 122;
		int y = this.guiTop + 46;
		this.addTextField(new GuiNpcTextField(11, this, x + 103, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", playerdata.entityScaleX)));
		this.addSlider(new GuiNpcSlider(this, 11, x, y, 100, 20, ((playerdata.entityScaleX - minScale) / (maxScale - minScale))));
		this.getSlider(11).displayString = "X";
		y += 22;

		this.addTextField(new GuiNpcTextField(12, this, x + 103, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", playerdata.entityScaleY)));
		this.addSlider(new GuiNpcSlider(this, 12, x, y, 100, 20, ((playerdata.entityScaleY - minScale) / (maxScale - minScale))));
		this.getSlider(12).displayString = "Y";

		y += 22;
		this.addTextField(new GuiNpcTextField(13, this, x + 103, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", playerdata.entityScaleZ)));
		this.addSlider(new GuiNpcSlider(this, 13, x, y, 100, 20, ((playerdata.entityScaleZ - minScale) / (maxScale - minScale))));
		this.getSlider(13).displayString = "Z";

		this.initiating = false;
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if (btn.id == 10) {
			this.playerdata.setEntityClass((Class)null);
			this.resetToSelected = true;
			this.initGui();
		}

	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		this.playerdata.setEntityClass((Class)this.data.get(scroll.getSelected()));
		this.initGui();
	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);
		if (this.initiating) return;

		if (slider.id >= 11 && slider.id <= 13) {
			Float value = 0.0F;
			String text = "";

			value = ((slider.sliderValue * (maxScale - minScale)) + minScale);

			if (slider.id == 11) {
				playerdata.entityScaleX = value;
			} else if (slider.id == 12) {
				playerdata.entityScaleY = value;
			} else if (slider.id == 13) {
				playerdata.entityScaleZ = value;
			}

			text = String.format(java.util.Locale.US,"%.2f", value);

			this.getTextField(slider.id).setText(text);
		}
	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unFocused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if (textField.id >= 11 && textField.id <= 13) {
			Float value = null;
			try {
				value = Float.parseFloat(textField.getText().replace(',', '.'));
			} catch (NumberFormatException e) {
				return;
			}

			Float sliderValue = 0.0F;

			sliderValue = (value - minScale) / (maxScale - minScale);

			if (textField.id == 11) {
				playerdata.entityScaleX = value;
			} else if (textField.id == 12) {
				playerdata.entityScaleY = value;
			} else if (textField.id == 13) {
				playerdata.entityScaleZ = value;
			}

			textField.setCursorPositionZero();
			textField.setSelectionPos(0);
			this.getSlider(textField.id).sliderValue = sliderValue;
		}
	}

	@Override
	public void focused(GuiNpcTextField textField) {
		if (this.initiating) return;

		if ((textField.id >= 11 && textField.id <= 13)) {
			textField.setCursorPositionZero();
			textField.setSelectionPos(textField.getText().length());
		}

	}

	@Override
	public void textboxKeyTyped(GuiNpcTextField textField) {
		// TODO Auto-generated method stub

	}
}
