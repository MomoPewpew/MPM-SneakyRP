package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelPartConfig;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcSlider;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ISliderListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationScale extends GuiCreationScreenInterface implements ISliderListener, ICustomScrollListener {
	private GuiCustomScroll scroll;
	private List data = new ArrayList();
	private static EnumParts selected;

	public GuiCreationScale() {
		this.active = 3;
		this.xOffset = 140;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (this.scroll == null) {
			this.scroll = new GuiCustomScroll(this, 0);
		}

		ArrayList list = new ArrayList();
		EnumParts[] parts = new EnumParts[]{EnumParts.HEAD, EnumParts.BODY, EnumParts.ARM_LEFT, EnumParts.ARM_RIGHT, EnumParts.LEG_LEFT, EnumParts.LEG_RIGHT};
		this.data.clear();
		EnumParts[] var3 = parts;
		int y = parts.length;

		for(int var5 = 0; var5 < y; ++var5) {
			EnumParts part = var3[var5];
			ModelPartConfig config;
			if (part == EnumParts.ARM_RIGHT) {
				config = this.playerdata.getPartConfig(EnumParts.ARM_LEFT);
				if (!config.notShared) {
					continue;
				}
			}

			if (part == EnumParts.LEG_RIGHT) {
				config = this.playerdata.getPartConfig(EnumParts.LEG_LEFT);
				if (!config.notShared) {
					continue;
				}
			}

			this.data.add(part);
			list.add(I18n.translateToLocal("part." + part.name));
		}

		this.scroll.setUnsortedList(list);
		this.scroll.setSelected(I18n.translateToLocal("part." + selected.name));
		this.scroll.guiLeft = this.guiLeft;
		this.scroll.guiTop = this.guiTop + 45;
		this.scroll.setSize(100, this.ySize - 52);
		this.addScroll(this.scroll);
		ModelPartConfig config = this.playerdata.getPartConfig(selected);
		y = this.guiTop + 65;
		this.addLabel(new GuiNpcLabel(10, "scale.width", this.guiLeft + 102, y + 5, 16777215));
		this.addSlider(new GuiNpcSlider(this, 10, this.guiLeft + 150, y, 100, 20, config.scaleX - 0.5F));
		y += 22;
		this.addLabel(new GuiNpcLabel(11, "scale.height", this.guiLeft + 102, y + 5, 16777215));
		this.addSlider(new GuiNpcSlider(this, 11, this.guiLeft + 150, y, 100, 20, config.scaleY - 0.5F));
		y += 22;
		this.addLabel(new GuiNpcLabel(12, "scale.depth", this.guiLeft + 102, y + 5, 16777215));
		this.addSlider(new GuiNpcSlider(this, 12, this.guiLeft + 150, y, 100, 20, config.scaleZ - 0.5F));
		if (selected == EnumParts.ARM_LEFT || selected == EnumParts.LEG_LEFT) {
			y += 22;
			this.addLabel(new GuiNpcLabel(13, "scale.shared", this.guiLeft + 102, y + 5, 16777215));
			this.addButton(new GuiNpcButton(13, this.guiLeft + 150, y, 50, 20, new String[]{"gui.no", "gui.yes"}, config.notShared ? 0 : 1));
		}

	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		super.actionPerformed(btn);
		if (btn.id == 13) {
			boolean bo = ((GuiNpcButton)btn).getValue() == 0;
			this.playerdata.getPartConfig(selected).notShared = bo;
			this.initGui();
		}

	}

	@Override
	public void mouseDragged(GuiNpcSlider slider) {
		super.mouseDragged(slider);
		if (slider.id >= 10 && slider.id <= 12) {
			int percent = (int)(50.0F + slider.sliderValue * 100.0F);
			slider.setString(percent + "%");
			ModelPartConfig config = this.playerdata.getPartConfig(selected);
			if (slider.id == 10) {
				config.scaleX = slider.sliderValue + 0.5F;
			}

			if (slider.id == 11) {
				config.scaleY = slider.sliderValue + 0.5F;
			}

			if (slider.id == 12) {
				config.scaleZ = slider.sliderValue + 0.5F;
			}

			this.updateTransate();
		}

	}

	private void updateTransate() {
		EnumParts[] var1 = EnumParts.values();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			EnumParts part = var1[var3];
			ModelPartConfig config = this.playerdata.getPartConfig(part);
			if (config != null) {
				if (part == EnumParts.HEAD) {
					config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
				} else {
					ModelPartConfig leg;
					float x;
					float y;
					if (part == EnumParts.ARM_LEFT) {
						leg = this.playerdata.getPartConfig(EnumParts.BODY);
						x = (1.0F - leg.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
						y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
						config.setTranslate(-x, y, 0.0F);
						if (!config.notShared) {
							ModelPartConfig arm = this.playerdata.getPartConfig(EnumParts.ARM_RIGHT);
							arm.copyValues(config);
						}
					} else if (part == EnumParts.ARM_RIGHT) {
						leg = this.playerdata.getPartConfig(EnumParts.BODY);
						x = (1.0F - leg.scaleX) * 0.25F + (1.0F - config.scaleX) * 0.075F;
						y = this.playerdata.getBodyY() + (1.0F - config.scaleY) * -0.1F;
						config.setTranslate(x, y, 0.0F);
					} else if (part == EnumParts.LEG_LEFT) {
						config.setTranslate(config.scaleX * 0.125F - 0.113F, this.playerdata.getLegsY(), 0.0F);
						if (!config.notShared) {
							leg = this.playerdata.getPartConfig(EnumParts.LEG_RIGHT);
							leg.copyValues(config);
						}
					} else if (part == EnumParts.LEG_RIGHT) {
						config.setTranslate((1.0F - config.scaleX) * 0.125F, this.playerdata.getLegsY(), 0.0F);
					} else if (part == EnumParts.BODY) {
						config.setTranslate(0.0F, this.playerdata.getBodyY(), 0.0F);
					}
				}
			}
		}

	}

	@Override
	public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
		if (scroll.selected >= 0) {
			selected = (EnumParts)this.data.get(scroll.selected);
			this.initGui();
		}

	}

	@Override
	public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
	}

	static {
		selected = EnumParts.HEAD;
	}

	@Override
	public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
		// TODO Auto-generated method stub

	}
}
