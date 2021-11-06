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
	private static String searchString;

	public GuiCreationEntities() {
		Iterator var1 = ForgeRegistries.ENTITIES.getValues().iterator();

		while(var1.hasNext()) {
			EntityEntry ent = (EntityEntry)var1.next();
			String name = new String(ent.getName());

			if (!MorePlayerModels.entityNamesRemovedFromGui.contains(name.toLowerCase()))

			if (name.contains(":")) {
				String[] namespaced = name.split(":");
				name = namespaced[namespaced.length - 1];
			}

			try {
				Class c = ent.getEntityClass();
				if (EntityLiving.class.isAssignableFrom(c) && c.getConstructor(World.class) != null && !Modifier.isAbstract(c.getModifiers()) && Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(c) instanceof RenderLivingBase) {
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
		searchString = "";
		this.closeOnInventory = false;
	}

	@Override
	public void initGui() {
		super.initGui();

		if (!MorePlayerModels.hasEntityPermission) {
			this.openGui(new GuiCreationOptions());
			return;
		}

		this.initiating = true;

		this.list.clear();

		for (Object name : this.data.keySet()) {
			if (((String) name).toLowerCase().contains(searchString))
				this.list.add((String) name);
		}

		if (I18n.translateToLocal("gui.player").toLowerCase().contains(searchString))
			this.list.add(I18n.translateToLocal("gui.player"));

		Collections.sort(this.list, String.CASE_INSENSITIVE_ORDER);

		this.addButton(new GuiNpcButton(10, this.guiLeft, this.guiTop + 46, 120, 20, "gui.resettoplayer"));
		if (this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0);
			this.scroll.setUnsortedList(this.list);
		}

		this.addTextField(new GuiNpcTextField(13, this, this.guiLeft + 2, this.guiTop + 70, 116, 16, searchString.equals("") ? "Search" : searchString));

		this.scroll.guiLeft = this.guiLeft;
		this.scroll.guiTop = this.guiTop + 45 + 22 + 22;
		this.scroll.setSize(100, this.ySize - 52 - 22 - 22);
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
		this.addTextField(new GuiNpcTextField(11, this, x + 103, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.entityScaleX)));
		this.addSlider(new GuiNpcSlider(this, 11, x, y, 100, 20, ((this.playerdata.entityScaleX - minScale) / (maxScale - minScale))));
		this.getSlider(11).displayString = "Width";
		y += 22;

		this.addTextField(new GuiNpcTextField(12, this, x + 103, y + 1, 36, 18, String.format(java.util.Locale.US,"%.2f", this.playerdata.entityScaleY)));
		this.addSlider(new GuiNpcSlider(this, 12, x, y, 100, 20, ((this.playerdata.entityScaleY - minScale) / (maxScale - minScale))));
		this.getSlider(12).displayString = "Height";

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

		if (slider.id >= 11 && slider.id <= 12) {
			Float value = 0.0F;
			String text = "";

			value = ((slider.sliderValue * (maxScale - minScale)) + minScale);

			if (slider.id == 11) {
				this.playerdata.entityScaleX = value;
			} else if (slider.id == 12) {
				this.playerdata.entityScaleY = value;
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

		if (textField.id >= 11 && textField.id <= 12) {
			Float value = null;
			try {
				value = Float.parseFloat(textField.getText().replace(',', '.'));
			} catch (NumberFormatException e) {
				return;
			}

			Float sliderValue = 0.0F;

			sliderValue = (value - minScale) / (maxScale - minScale);

			if (textField.id == 11) {
				this.playerdata.entityScaleX = value;
			} else if (textField.id == 12) {
				this.playerdata.entityScaleY = value;
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
		if (this.initiating) return;
		if (textField.id >= 13) {
			searchString = new String(textField.getText()).toLowerCase();
			int i = textField.getCursorPosition();

			this.initGui();

			if (searchString.equals("")) this.getTextField(13).setText("");

			this.getTextField(13).setFocused(true);
			this.getTextField(13).setCursorPosition(i);
			this.scroll.setScrollY(0);
		}
	}
}
