package noppes.mpm.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.translation.I18n;
import noppes.mpm.ModelEyeData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.gui.util.GuiButtonBiDirectional;
import noppes.mpm.client.gui.util.GuiColorButton;
import noppes.mpm.client.gui.util.GuiCustomScroll;
import noppes.mpm.client.gui.util.GuiNpcButton;
import noppes.mpm.client.gui.util.GuiNpcButtonYesNo;
import noppes.mpm.client.gui.util.GuiNpcLabel;
import noppes.mpm.client.gui.util.GuiNpcTextField;
import noppes.mpm.client.gui.util.ICustomScrollListener;
import noppes.mpm.client.gui.util.ITextfieldListener;
import noppes.mpm.constants.EnumParts;

public class GuiCreationParts extends GuiCreationScreenInterface implements ITextfieldListener, ICustomScrollListener {
	private GuiCustomScroll scroll;
	private GuiCreationParts.GuiPart[] parts;
	private static int selected = 0;

	public GuiCreationParts() {
		if (this.playerdata.eyesShared) {
			this.parts = new GuiCreationParts.GuiPart[]{(
					new GuiCreationParts.GuiPart(EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}),
					new GuiCreationParts.GuiPartHorns(),
					new GuiCreationParts.GuiPartHair(),
					(new GuiCreationParts.GuiPart(EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(),
					new GuiCreationParts.GuiPartSnout(),
					new GuiCreationParts.GuiPartBeard(),
					(new GuiCreationParts.GuiPart(EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}),
					(new GuiCreationParts.GuiPart(EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(),
					new GuiCreationParts.GuiPartWings(),
					new GuiCreationParts.GuiPartClaws(),
					(new GuiCreationParts.GuiPart(EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}),
					new GuiCreationParts.GuiPartLegs(),
					new GuiCreationParts.GuiPartTail(),
					new GuiCreationParts.GuiPartHalo(),
					new GuiCreationParts.GuiPartEyes(),
					//new GuiCreationParts.GuiPartParticles(),
					new GuiCreationParts.GuiPartArmLeft(),
					new GuiCreationParts.GuiPartArmRight(),
					new GuiCreationParts.GuiPartLegLeft(),
					new GuiCreationParts.GuiPartLegRight(),
					new GuiCreationParts.GuiPartBody(),
					new GuiCreationParts.GuiPartHead()};
		} else {
			this.parts = new GuiCreationParts.GuiPart[]{(
					new GuiCreationParts.GuiPart(EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}),
					new GuiCreationParts.GuiPartHorns(),
					new GuiCreationParts.GuiPartHair(),
					(new GuiCreationParts.GuiPart(EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(),
					new GuiCreationParts.GuiPartSnout(),
					new GuiCreationParts.GuiPartBeard(),
					(new GuiCreationParts.GuiPart(EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}),
					(new GuiCreationParts.GuiPart(EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(),
					new GuiCreationParts.GuiPartWings(),
					new GuiCreationParts.GuiPartClaws(),
					(new GuiCreationParts.GuiPart(EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}),
					new GuiCreationParts.GuiPartLegs(),
					new GuiCreationParts.GuiPartTail(),
					new GuiCreationParts.GuiPartHalo(),
					new GuiCreationParts.GuiPartEye1(),
					new GuiCreationParts.GuiPartEye2(),
					//new GuiCreationParts.GuiPartParticles(),
					new GuiCreationParts.GuiPartArmLeft(),
					new GuiCreationParts.GuiPartArmRight(),
					new GuiCreationParts.GuiPartLegLeft(),
					new GuiCreationParts.GuiPartLegRight(),
					new GuiCreationParts.GuiPartBody(),
					new GuiCreationParts.GuiPartHead()};
		}

		this.active = 2;
		Arrays.sort(this.parts, (o1, o2) -> {
			String s1 = I18n.translateToLocal("part." + o1.part.name);
			String s2 = I18n.translateToLocal("part." + o2.part.name);
			return s1.compareToIgnoreCase(s2);
		});
	}

		@Override
		public void initGui() {
			super.initGui();
			if (this.entity != null) {
				this.openGui(new GuiCreationExtra());
			} else {
				if (this.scroll == null) {
					List list = new ArrayList();
					GuiCreationParts.GuiPart[] var2 = this.parts;
					int var3 = var2.length;

					for(int var4 = 0; var4 < var3; ++var4) {
						GuiCreationParts.GuiPart part = var2[var4];
						list.add(I18n.translateToLocal("part." + part.part.name));
					}

					this.scroll = new GuiCustomScroll(this, 0);
					this.scroll.setUnsortedList(list);
				}

				this.scroll.guiLeft = this.guiLeft;
				this.scroll.guiTop = this.guiTop + 45;
				this.scroll.setSize(100, this.ySize - 52);
				this.addScroll(this.scroll);
				if (this.parts[selected] != null) {
					this.scroll.setSelected(I18n.translateToLocal("part." + this.parts[selected].part.name));
					this.parts[selected].initGui();
				}

			}
		}

		@Override
		protected void actionPerformed(GuiButton btn) {
			super.actionPerformed(btn);
			if (this.parts[selected] != null) {
				this.parts[selected].actionPerformed(btn);
			}

		}

		@Override
		public void unFocused(GuiNpcTextField textfield) {
			if (textfield.id == 23) {
			}

		}

		@Override
		public void scrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
			if (scroll.selected >= 0) {
				selected = scroll.selected;
				this.initGui();
			}

		}

		@Override
		public void scrollDoubleClicked(String selection, GuiCustomScroll scroll) {
		}

		class GuiPartHead extends GuiCreationParts.GuiPart {
			public GuiPartHead() {
				super(EnumParts.HEAD);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartArmLeft extends GuiCreationParts.GuiPart {
			public GuiPartArmLeft() {
				super(EnumParts.ARM_LEFT);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartArmRight extends GuiCreationParts.GuiPart {
			public GuiPartArmRight() {
				super(EnumParts.ARM_RIGHT);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartBody extends GuiCreationParts.GuiPart {
			public GuiPartBody() {
				super(EnumParts.BODY);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartLegLeft extends GuiCreationParts.GuiPart {
			public GuiPartLegLeft() {
				super(EnumParts.LEG_LEFT);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartLegRight extends GuiCreationParts.GuiPart {
			public GuiPartLegRight() {
				super(EnumParts.LEG_RIGHT);
				this.types = new String[]{"gui.none", "gui.normal"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = false;
				int y = super.initGui();

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartLegs extends GuiCreationParts.GuiPart {
			public GuiPartLegs() {
				super(EnumParts.LEGS);
				this.types = new String[]{"gui.none", "gui.normal", "legs.naga", "legs.spider", "legs.horse", "legs.mermaid", "legs.digitigrade"};
				this.canBeDeleted = false;
			}

			@Override
			public int initGui() {
				this.hasPlayerOption = this.data.type == 1 || this.data.type == 5;
				int y = super.initGui();
				if (this.data.type == 4) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[]{"1", "2"}, this.data.pattern));
				}

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i <= 1) {
						this.data.playerTexture = true;
					} else {
						this.data.playerTexture = false;
					}
				}

				if (btn.id == 22) {
					this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartClaws extends GuiCreationParts.GuiPart {
			public GuiPartClaws() {
				super(EnumParts.CLAWS);
				this.types = new String[]{"gui.none", "gui.show"};
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data == null) {
					return y;
				} else {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[]{"gui.both", "gui.left", "gui.right"}, this.data.pattern));
					return y;
				}
			}
		}

		class GuiPartWings extends GuiCreationParts.GuiPart {
			public GuiPartWings() {
				super(EnumParts.WINGS);
				this.setTypes(new String[]{"gui.none", "1", "2", "3", "4", "5"});
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data == null) {
					return y;
				} else {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(24, I18n.translateToLocal("part.wings") + "/" + I18n.translateToLocal("item.elytra.name"), GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(34, GuiCreationParts.this.guiLeft + 185, y, 100, 20, new String[]{"gui.both", "part.wings", "item.elytra.name"}, GuiCreationParts.this.playerdata.wingMode));
					return y;
				}
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 34) {
					GuiCreationParts.this.playerdata.wingMode = ((GuiButtonBiDirectional)btn).getValue();
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartEyes extends GuiCreationParts.GuiPart {
			private ModelEyeData eye1;
			private ModelEyeData eye2;

			public GuiPartEyes() {
				super(EnumParts.EYE1);
				this.types = new String[]{"gui.none", "1", "2"};
				this.noPlayerOptions();
				this.canBeDeleted = false;
				this.eye1 = (ModelEyeData)GuiCreationParts.this.playerdata.getPartData(EnumParts.EYE1);
				this.eye2 = (ModelEyeData)GuiCreationParts.this.playerdata.getPartData(EnumParts.EYE2);
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data != null && this.eye1.isEnabled()) {
					int var10004 = GuiCreationParts.this.guiLeft + 145;
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(37, var10004, y, 100, 20, new String[]{I18n.translateToLocal("gui.down") + "x2", "gui.down", "gui.normal", "gui.up"}, this.eye1.eyePos + 1));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(37, "gui.position", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 145;
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(34, var10004, y, this.eye1.glint));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(34, "eye.glint", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(35, var10004, y, this.eye1.browColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(35, "eye.brow", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(38, GuiCreationParts.this.guiLeft + 225, y, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, this.eye1.browThickness));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(36, var10004, y, this.eye1.skinColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(36, "eye.lid", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButton(39, GuiCreationParts.this.guiLeft + 145, y, 50, 20, "gui.yes"));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(39, "scale.shared", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
				}

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i == 0 && this.canBeDeleted) {
						GuiCreationParts.this.playerdata.removePart(EnumParts.EYE1);
						GuiCreationParts.this.playerdata.removePart(EnumParts.EYE2);
					} else {
						this.eye1.pattern = 0;
						this.eye1.setType(i - 1);
						this.eye2.pattern = 0;
						this.eye2.setType(i - 1);
					}

					GuiCreationParts.this.initGui();
				}

				if (btn.id == 23) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.data.color, (color) -> {
						this.eye1.color = color;
						this.eye2.color = color;
					}));
				}

				if (btn.id == 34) {
					this.eye1.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
					this.eye2.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
				}

				if (btn.id == 35) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eye1.browColor, (color) -> {
						this.eye1.browColor = color;
						this.eye2.browColor = color;
					}));
				}

				if (btn.id == 36) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eye1.skinColor, (color) -> {
						this.eye1.skinColor = color;
						this.eye2.skinColor = color;
					}));
				}

				if (btn.id == 37) {
					this.eye1.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
					this.eye2.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
				}

				if (btn.id == 38) {
					this.eye1.browThickness = ((GuiButtonBiDirectional)btn).getValue();
					this.eye2.browThickness = ((GuiButtonBiDirectional)btn).getValue();
				}

				if (btn.id == 39) {
					GuiCreationParts.this.playerdata.eyesShared = false;

					GuiCreationParts.this.parts = new GuiCreationParts.GuiPart[]{(
							new GuiCreationParts.GuiPart(EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}),
							new GuiCreationParts.GuiPartHorns(),
							new GuiCreationParts.GuiPartHair(),
							(new GuiCreationParts.GuiPart(EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartSnout(),
							new GuiCreationParts.GuiPartBeard(),
							(new GuiCreationParts.GuiPart(EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}),
							(new GuiCreationParts.GuiPart(EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartWings(),
							new GuiCreationParts.GuiPartClaws(),
							(new GuiCreationParts.GuiPart(EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}),
							new GuiCreationParts.GuiPartLegs(),
							new GuiCreationParts.GuiPartTail(),
							new GuiCreationParts.GuiPartHalo(),
							new GuiCreationParts.GuiPartEye1(),
							new GuiCreationParts.GuiPartEye2(),
							//new GuiCreationParts.GuiPartParticles(),
							new GuiCreationParts.GuiPartArmLeft(),
							new GuiCreationParts.GuiPartArmRight(),
							new GuiCreationParts.GuiPartLegLeft(),
							new GuiCreationParts.GuiPartLegRight(),
							new GuiCreationParts.GuiPartBody(),
							new GuiCreationParts.GuiPartHead()};

					Arrays.sort(GuiCreationParts.this.parts, (o1, o2) -> {
						String s1 = I18n.translateToLocal("part." + o1.part.name);
						String s2 = I18n.translateToLocal("part." + o2.part.name);
						return s1.compareToIgnoreCase(s2);
					});

					GuiCreationParts.this.scroll = null;
					GuiCreationParts.this.initGui();
				}
			}
		}

		class GuiPartEye1 extends GuiCreationParts.GuiPart {
			private ModelEyeData eyes;

			public GuiPartEye1() {
				super(EnumParts.EYE1);
				this.types = new String[]{"gui.none", "1", "2"};
				this.noPlayerOptions();
				this.canBeDeleted = false;
				this.eyes = (ModelEyeData)this.data;
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data != null && this.eyes.isEnabled()) {
					int var10004 = GuiCreationParts.this.guiLeft + 145;
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(37, var10004, y, 100, 20, new String[]{I18n.translateToLocal("gui.down") + "x2", "gui.down", "gui.normal", "gui.up"}, this.eyes.eyePos + 1));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(37, "gui.position", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 145;
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(34, var10004, y, this.eyes.glint));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(34, "eye.glint", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(35, var10004, y, this.eyes.browColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(35, "eye.brow", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(38, GuiCreationParts.this.guiLeft + 225, y, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, this.eyes.browThickness));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(36, var10004, y, this.eyes.skinColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(36, "eye.lid", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButton(39, GuiCreationParts.this.guiLeft + 145, y, 50, 20, "gui.no"));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(39, "scale.shared", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
				}

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 34) {
					this.eyes.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
				}

				if (btn.id == 35) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.browColor, (color) -> {
						this.eyes.browColor = color;
					}));
				}

				if (btn.id == 36) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.skinColor, (color) -> {
						this.eyes.skinColor = color;
					}));
				}

				if (btn.id == 37) {
					this.eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
				}

				if (btn.id == 38) {
					this.eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
				}

				if (btn.id == 39) {
					GuiCreationParts.this.playerdata.eyesShared = false;
					GuiCreationParts.this.playerdata.eye2.clone(eyes);

					GuiCreationParts.this.parts = new GuiCreationParts.GuiPart[]{(
							new GuiCreationParts.GuiPart(EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}),
							new GuiCreationParts.GuiPartHorns(),
							new GuiCreationParts.GuiPartHair(),
							(new GuiCreationParts.GuiPart(EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartSnout(),
							new GuiCreationParts.GuiPartBeard(),
							(new GuiCreationParts.GuiPart(EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}),
							(new GuiCreationParts.GuiPart(EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartWings(),
							new GuiCreationParts.GuiPartClaws(),
							(new GuiCreationParts.GuiPart(EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}),
							new GuiCreationParts.GuiPartLegs(),
							new GuiCreationParts.GuiPartTail(),
							new GuiCreationParts.GuiPartHalo(),
							new GuiCreationParts.GuiPartEyes(),
							//new GuiCreationParts.GuiPartParticles(),
							new GuiCreationParts.GuiPartArmLeft(),
							new GuiCreationParts.GuiPartArmRight(),
							new GuiCreationParts.GuiPartLegLeft(),
							new GuiCreationParts.GuiPartLegRight(),
							new GuiCreationParts.GuiPartBody(),
							new GuiCreationParts.GuiPartHead()};

					Arrays.sort(GuiCreationParts.this.parts, (o1, o2) -> {
						String s1 = I18n.translateToLocal("part." + o1.part.name);
						String s2 = I18n.translateToLocal("part." + o2.part.name);
						return s1.compareToIgnoreCase(s2);
					});

					GuiCreationParts.this.scroll = null;
					GuiCreationParts.this.initGui();
				}
				super.actionPerformed(btn);
			}
		}

		class GuiPartEye2 extends GuiCreationParts.GuiPart {
			private ModelEyeData eyes;

			public GuiPartEye2() {
				super(EnumParts.EYE2);
				this.types = new String[]{"gui.none", "1", "2"};
				this.noPlayerOptions();
				this.canBeDeleted = false;
				this.eyes = (ModelEyeData)this.data;
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data != null && this.eyes.isEnabled()) {
					int var10004 = GuiCreationParts.this.guiLeft + 145;
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(37, var10004, y, 100, 20, new String[]{I18n.translateToLocal("gui.down") + "x2", "gui.down", "gui.normal", "gui.up"}, this.eyes.eyePos + 1));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(37, "gui.position", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 145;
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(34, var10004, y, this.eyes.glint));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(34, "eye.glint", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(35, var10004, y, this.eyes.browColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(35, "eye.brow", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(38, GuiCreationParts.this.guiLeft + 225, y, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, this.eyes.browThickness));
					var10004 = GuiCreationParts.this.guiLeft + 170;
					y += 25;
					GuiCreationParts.this.addButton(new GuiColorButton(36, var10004, y, this.eyes.skinColor));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(36, "eye.lid", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					y += 25;
					GuiCreationParts.this.addButton(new GuiNpcButton(39, GuiCreationParts.this.guiLeft + 145, y, 50, 20, "gui.no"));
					GuiCreationParts.this.addLabel(new GuiNpcLabel(39, "scale.shared", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
				}

				return y;
			}

			@Override
			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 34) {
					this.eyes.glint = ((GuiNpcButtonYesNo)btn).getBoolean();
				}

				if (btn.id == 35) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.browColor, (color) -> {
						this.eyes.browColor = color;
					}));
				}

				if (btn.id == 36) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.eyes.skinColor, (color) -> {
						this.eyes.skinColor = color;
					}));
				}

				if (btn.id == 37) {
					this.eyes.eyePos = ((GuiButtonBiDirectional)btn).getValue() - 1;
				}

				if (btn.id == 38) {
					this.eyes.browThickness = ((GuiButtonBiDirectional)btn).getValue();
				}

				if (btn.id == 39) {
					GuiCreationParts.this.playerdata.eyesShared = false;
					GuiCreationParts.this.playerdata.eye1.clone(eyes);

					GuiCreationParts.this.parts = new GuiCreationParts.GuiPart[]{(
							new GuiCreationParts.GuiPart(EnumParts.EARS)).setTypes(new String[]{"gui.none", "gui.normal", "ears.bunny"}),
							new GuiCreationParts.GuiPartHorns(),
							new GuiCreationParts.GuiPartHair(),
							(new GuiCreationParts.GuiPart(EnumParts.MOHAWK)).setTypes(new String[]{"gui.none", "1", "2"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartSnout(),
							new GuiCreationParts.GuiPartBeard(),
							(new GuiCreationParts.GuiPart(EnumParts.FIN)).setTypes(new String[]{"gui.none", "fin.shark", "fin.reptile"}),
							(new GuiCreationParts.GuiPart(EnumParts.BREASTS)).setTypes(new String[]{"gui.none", "1", "2", "3"}).noPlayerOptions(),
							new GuiCreationParts.GuiPartWings(),
							new GuiCreationParts.GuiPartClaws(),
							(new GuiCreationParts.GuiPart(EnumParts.SKIRT)).setTypes(new String[]{"gui.none", "gui.normal"}),
							new GuiCreationParts.GuiPartLegs(),
							new GuiCreationParts.GuiPartTail(),
							new GuiCreationParts.GuiPartHalo(),
							new GuiCreationParts.GuiPartEyes(),
							//new GuiCreationParts.GuiPartParticles(),
							new GuiCreationParts.GuiPartArmLeft(),
							new GuiCreationParts.GuiPartArmRight(),
							new GuiCreationParts.GuiPartLegLeft(),
							new GuiCreationParts.GuiPartLegRight(),
							new GuiCreationParts.GuiPartBody(),
							new GuiCreationParts.GuiPartHead()};

					Arrays.sort(GuiCreationParts.this.parts, (o1, o2) -> {
						String s1 = I18n.translateToLocal("part." + o1.part.name);
						String s2 = I18n.translateToLocal("part." + o2.part.name);
						return s1.compareToIgnoreCase(s2);
					});

					GuiCreationParts.this.scroll = null;
					GuiCreationParts.this.initGui();
				}

				super.actionPerformed(btn);
			}
		}

		class GuiPartBeard extends GuiCreationParts.GuiPart {
			public GuiPartBeard() {
				super(EnumParts.BEARD);
				this.types = new String[]{"gui.none", "1", "2", "3", "4"};
				this.noPlayerTypes();
			}
		}

		class GuiPartSnout extends GuiCreationParts.GuiPart {
			public GuiPartSnout() {
				super(EnumParts.SNOUT);
				this.types = new String[]{"gui.none", "snout.small", "snout.medium", "snout.large", "snout.bunny", "snout.beak"};
			}
		}

		class GuiPartHair extends GuiCreationParts.GuiPart {
			public GuiPartHair() {
				super(EnumParts.HAIR);
				this.types = new String[]{"gui.none", "1", "2", "3", "4"};
				this.noPlayerTypes();
			}
		}

		class GuiPartHorns extends GuiCreationParts.GuiPart {
			public GuiPartHorns() {
				super(EnumParts.HORNS);
				this.types = new String[]{"gui.none", "horns.bull", "horns.antlers", "horns.antenna"};
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				if (this.data != null && this.data.type == 2) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[]{"1", "2"}, this.data.pattern));
				}

				return y;
			}
		}

		class GuiPartTail extends GuiCreationParts.GuiPart {
			public GuiPartTail() {
				super(EnumParts.TAIL);
				this.types = new String[]{"gui.none", "part.tail", "tail.dragon", "tail.horse", "tail.squirrel", "tail.fin", "tail.rodent", "tail.bird", "tail.fox"};
			}

			@Override
			public int initGui() {
				this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
				this.hasPlayerOption = this.data != null && (this.data.type == 0 || this.data.type == 1 || this.data.type == 6 || this.data.type == 7);
				int y = super.initGui();
				if (this.data != null && this.data.type == 0) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(22, "gui.pattern", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(22, GuiCreationParts.this.guiLeft + 145, y, 100, 20, new String[]{"1", "2"}, this.data.pattern));
				}

				return y;
			}
		}

		class GuiPartParticles extends GuiCreationParts.GuiPart {
			public GuiPartParticles() {
				super(EnumParts.PARTICLES);
				this.types = new String[]{"gui.none", "1", "2"};
			}

			@Override
			public int initGui() {
				int y = super.initGui();
				return this.data == null ? y : y;
			}
		}

		class GuiPartHalo extends GuiCreationParts.GuiPart {
			public GuiPartHalo() {
				super(EnumParts.HALO);
				this.setTypes(new String[]{"gui.none", "1"});
			}

			@Override
			public int initGui() {
				this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
				this.hasPlayerOption = this.data != null && (this.data.type == 2);
				int y = super.initGui();

				return y;
			}
		}

		class GuiPart {
			EnumParts part;
			private int paterns = 0;
			protected String[] types = new String[]{"gui.none"};
			protected ModelPartData data;
			protected boolean hasPlayerOption = true;
			protected boolean noPlayerTypes = false;
			protected boolean canBeDeleted = true;

			public GuiPart(EnumParts part) {
				this.part = part;
				this.data = GuiCreationParts.this.playerdata.getPartData(part);
			}

			public int initGui() {
				this.data = GuiCreationParts.this.playerdata.getPartData(this.part);
				int y = GuiCreationParts.this.guiTop + 50;
				if (this.data == null || !this.data.playerTexture || !this.noPlayerTypes) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(20, "gui.type", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiButtonBiDirectional(20, GuiCreationParts.this.guiLeft + 145, y, 100, 20, this.types, this.data == null ? 0 : this.data.type + 1));
					y += 25;
				}

				if (this.data != null && this.hasPlayerOption) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(21, "gui.playerskin", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiNpcButtonYesNo(21, GuiCreationParts.this.guiLeft + 170, y, this.data.playerTexture));
					y += 25;
				}

				if (this.data != null && !this.data.playerTexture) {
					GuiCreationParts.this.addLabel(new GuiNpcLabel(23, "gui.color", GuiCreationParts.this.guiLeft + 102, y + 5, 16777215));
					GuiCreationParts.this.addButton(new GuiColorButton(23, GuiCreationParts.this.guiLeft + 170, y, this.data.color));
					y += 25;
				}

				return y;
			}

			protected void actionPerformed(GuiButton btn) {
				if (btn.id == 20) {
					int i = ((GuiNpcButton)btn).getValue();
					if (i == 0 && this.canBeDeleted) {
						GuiCreationParts.this.playerdata.removePart(this.part);
					} else {
						this.data = GuiCreationParts.this.playerdata.getOrCreatePart(this.part);
						this.data.pattern = 0;
						this.data.setType(i - 1);
					}

					GuiCreationParts.this.initGui();
				}

				if (btn.id == 22) {
					this.data.pattern = (byte)((GuiNpcButton)btn).getValue();
				}

				if (btn.id == 21) {
					this.data.playerTexture = ((GuiNpcButtonYesNo)btn).getBoolean();
					GuiCreationParts.this.initGui();
				}

				if (btn.id == 23) {
					GuiCreationParts.this.setSubGui(new GuiModelColor(GuiCreationParts.this, this.data.color, (color) -> {
						this.data.color = color;
					}));
				}

			}

			public GuiCreationParts.GuiPart noPlayerOptions() {
				this.hasPlayerOption = false;
				return this;
			}

			public GuiCreationParts.GuiPart noPlayerTypes() {
				this.noPlayerTypes = true;
				return this;
			}

			public GuiCreationParts.GuiPart setTypes(String[] types) {
				this.types = types;
				return this;
			}
		}

		@Override
		public void focused(GuiNpcTextField var1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void scrollSubButtonClicked(int var1, int var2, int var3, GuiCustomScroll var4) {
			// TODO Auto-generated method stub

		}

		@Override
		public void textboxKeyTyped(GuiNpcTextField textField) {
			// TODO Auto-generated method stub

		}
	}
