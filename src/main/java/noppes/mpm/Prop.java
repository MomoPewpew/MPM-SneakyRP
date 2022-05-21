package noppes.mpm;

import com.elytradev.architecture.common.shape.Shape;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import noppes.mpm.constants.EnumParts;
import noppes.mpm.util.BodyPartManager;
import noppes.mpm.util.EntityScaleManagerClient;

public class Prop {
	//These variables are read from NBT tags
	public String propString = "";
	public String bodyPartName = "lefthand";
	public int partIndex = 0;
	public float scaleX = 1.0F;
	public float scaleY = 1.0F;
	public float scaleZ = 1.0F;
	public float offsetX = 0.0F;
	public float offsetY = 0.0F;
	public float offsetZ = 0.0F;
	public float rotateX = 0.0F;
	public float rotateY = 0.0F;
	public float rotateZ = 0.0F;
	public boolean matchScaling = false;
	public boolean hide = true;
	public String name = "NONAME";
	public float scatter = 0.0F;
	public float frequency = 1.0F;
	public int amount = 1;
	public float pitch = 0.0F;
	public float yaw = 0.0F;
	public double speed = 0.0D;
	public float ppOffsetX = 0.0F;
	public float ppOffsetY = 0.0F;
	public float ppOffsetZ = 0.0F;
	public boolean lockrotation = false;

	//These are cached variables that are inferred and then used directly in the renderer
	public ItemStack itemStack = new ItemStack(Blocks.STAINED_GLASS, 1, 2);
	public EnumType type = EnumType.ITEM;
	public EnumParticleTypes particleType = null;
	public long lastPlayed = System.currentTimeMillis();
	public ModelRenderer propBodyPart = null;
	public boolean refreshCache = true;

	public float propScaleX = 1.0F;
	public float propScaleY = 1.0F;
	public float propScaleZ = 1.0F;
	public float propOffsetX = 0.0F;
	public float propOffsetY = 0.0F;
	public float propOffsetZ = 0.0F;
	public float partModifierX = 0.0F;
	public float partModifierY = 0.0F;
	public float partModifierZ = 0.0F;
	public float propPpOffsetX = 0.0F;
	public float propPpOffsetY = 0.0F;
	public float propPpOffsetZ = 0.0F;

	public enum EnumType {
		ITEM,
		PARTICLE
	}

	public Prop(){}

	public Prop(String propString, int partIndex,
		float scaleX, float scaleY, float scaleZ,
		float offsetX, float offsetY, float offsetZ,
		float rotateX, float rotateY, float rotateZ,
		boolean matchScaling, boolean hide, String name,
		float ppOffsetX, float ppOffsetY, float ppOffsetZ)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.partIndex = partIndex;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
		this.matchScaling = matchScaling;
		this.hide = hide;
		this.name = name;
		this.ppOffsetX = ppOffsetX;
		this.ppOffsetY = ppOffsetY;
		this.ppOffsetZ = ppOffsetZ;
	}

	public Prop(String propString, int partIndex,
		float motionScatter, float frequency, int amount,
		float offsetX, float offsetY, float offsetZ,
		float pitch, float yaw, double speed,
		boolean hide, String name, boolean lockrotation)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.partIndex = partIndex;
		this.scatter = motionScatter;
		this.frequency = frequency;
		this.amount = amount;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.pitch = pitch;
		this.yaw = yaw;
		this.speed = speed;
		this.hide = hide;
		this.name = name;
		this.lockrotation = lockrotation;
	}

	//Backwards compatibility assurance
	public Prop(String propString, String bodyPartName,
		float scaleX, float scaleY, float scaleZ,
		float offsetX, float offsetY, float offsetZ,
		float rotateX, float rotateY, float rotateZ,
		boolean matchScaling, boolean hide, String name,
		float ppOffsetX, float ppOffsetY, float ppOffsetZ)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.partIndex = switchBipedBodypart(bodyPartName);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
		this.matchScaling = matchScaling;
		this.hide = hide;
		this.name = name;
		this.ppOffsetX = ppOffsetX;
		this.ppOffsetY = ppOffsetY;
		this.ppOffsetZ = ppOffsetZ;
	}

	//Backwards compatibility assurance
	public Prop(String propString, String bodyPartName,
		float motionScatter, float frequency, int amount,
		float offsetX, float offsetY, float offsetZ,
		float pitch, float yaw, double speed,
		boolean hide, String name, boolean lockrotation)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.partIndex = switchBipedBodypart(bodyPartName);
		this.scatter = motionScatter;
		this.frequency = frequency;
		this.amount = amount;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.pitch = pitch;
		this.yaw = yaw;
		this.speed = speed;
		this.hide = hide;
		this.name = name;
		this.lockrotation = lockrotation;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("propString", this.propString);
		compound.setInteger("partIndex", this.partIndex);
		compound.setFloat("scaleX", this.scaleX);
		compound.setFloat("scaleY", this.scaleY);
		compound.setFloat("scaleZ", this.scaleZ);
		compound.setFloat("offsetX", this.offsetX);
		compound.setFloat("offsetY", this.offsetY);
		compound.setFloat("offsetZ", this.offsetZ);
		compound.setFloat("rotateX", this.rotateX);
		compound.setFloat("rotateY", this.rotateY);
		compound.setFloat("rotateZ", this.rotateZ);
		compound.setBoolean("matchScaling", this.matchScaling);
		compound.setBoolean("hide", this.hide);
		compound.setString("name", this.name);
		compound.setFloat("scatter", this.scatter);
		compound.setFloat("frequency", this.frequency);
		compound.setInteger("amount", this.amount);
		compound.setFloat("pitch", this.pitch);
		compound.setFloat("yaw", this.yaw);
		compound.setDouble("speed", this.speed);
		compound.setFloat("ppOffsetX", this.ppOffsetX);
		compound.setFloat("ppOffsetY", this.ppOffsetY);
		compound.setFloat("ppOffsetZ", this.ppOffsetZ);
		compound.setBoolean("lockrotation", this.lockrotation);
		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.propString = compound.getString("propString");
		this.parsePropString(this.propString);
		String bodyPartName = compound.getString("bodyPartName");
		this.partIndex = compound.getInteger("partIndex");
		this.scaleX = compound.getFloat("scaleX");
		this.scaleY = compound.getFloat("scaleY");
		this.scaleZ = compound.getFloat("scaleZ");
		this.offsetX = compound.getFloat("offsetX");
		this.offsetY = compound.getFloat("offsetY");
		this.offsetZ = compound.getFloat("offsetZ");
		this.rotateX = compound.getFloat("rotateX");
		this.rotateY = compound.getFloat("rotateY");
		this.rotateZ = compound.getFloat("rotateZ");
		this.matchScaling = compound.getBoolean("matchScaling");
		this.hide = compound.getBoolean("hide");
		this.name = compound.getString("name");
		this.scatter = compound.getFloat("scatter");
		this.frequency = compound.getFloat("frequency");
		this.amount = compound.getInteger("amount");
		this.pitch = compound.getFloat("pitch");
		this.yaw = compound.getFloat("yaw");
		this.speed = compound.getDouble("speed");
		this.ppOffsetX = compound.getFloat("ppOffsetX");
		this.ppOffsetY = compound.getFloat("ppOffsetY");
		this.ppOffsetZ = compound.getFloat("ppOffsetZ");
		this.lockrotation = compound.getBoolean("lockrotation");

		if (!bodyPartName.equals("")) {
			this.partIndex = switchBipedBodypart(bodyPartName);
		}
	}

	public String getCommand() {
		String command = "";
		if (this.type == EnumType.ITEM) {
			command = "/prop " +
			this.propString + " " + this.partIndex + " " +
			this.scaleX + " " + this.scaleY + " " + this.scaleZ + " " +
			this.offsetX + " " + this.offsetY + " " + this.offsetZ + " " +
			this.rotateX + " " + this.rotateY + " " + this.rotateZ + " " +
			this.matchScaling + " " + this.hide + " " + this.name + " " +
			this.ppOffsetX + " " + this.ppOffsetY + " " + this.ppOffsetZ;
		} else if (this.type == EnumType.PARTICLE) {
			command = "/prop " +
			this.propString + " " + this.partIndex + " " +
			this.scatter + " " + this.frequency + " " + this.amount + " " +
			this.offsetX + " " + this.offsetY + " " + this.offsetZ + " " +
			this.pitch + " " + this.yaw + " " + this.speed + " " +
			this.hide + " " + this.name + " " + this.lockrotation;
		}

		return command;
	}

	public boolean parsePropString(String propString) {
		for (String string : MorePlayerModels.blacklistedPropStrings) {
			if (propString.toLowerCase().contains(string.toLowerCase())) return false;
		}

		if (propString.startsWith("particle:"))
			return parseParticleString(propString.replace("particle:", ""));

		ItemStack stack = parseItemStack(propString);

		if (stack == null) {
			return false;
		} else {
			this.itemStack = stack;
			this.type = EnumType.ITEM;
			this.particleType = null;
			return true;
		}
	}

	public static ItemStack parseItemStack(String propString) {
		if (propString.startsWith("shape:")) {
			return parseArchitectureString(propString);
		} else {
			return getStack(propString);
		}
	}

	public static ItemStack getStack(String propString) {
		String nameSpacedId = "";
		short dataValue = 0;

		String[] parts = new String(propString).split(":");

		for (int i = 0; i < parts.length; i++) {
			if (i == (parts.length - 1)) {
				try {
					dataValue = Short.parseShort(parts[i]);
				} catch (NumberFormatException var2) {
					nameSpacedId += parts[i] + ":";
				}
			} else {
				nameSpacedId += parts[i] + ":";
			}
		}

		nameSpacedId = nameSpacedId.substring(0, nameSpacedId.length() - 1);

		ResourceLocation resourcelocation = new ResourceLocation(nameSpacedId);
		Item item = (Item)Item.REGISTRY.getObject(resourcelocation);

		if (item == null) {
			return null;
		} else {
			return new ItemStack(item, 1, dataValue);
		}
	}

	public boolean parseParticleString(String propString) {

		EnumParticleTypes particleType = null;

		String string = new String (propString).replace("_", "");

		switch(string) {
			case "blockcrack":
			particleType = EnumParticleTypes.BLOCK_CRACK;
			break;
			case "waterbubble":
			case "bubble":
			particleType = EnumParticleTypes.WATER_BUBBLE;
			break;
			case "cloud":
			particleType = EnumParticleTypes.CLOUD;
			break;
			case "critical":
			case "crit":
			particleType = EnumParticleTypes.CRIT;
			break;
			case "critmagic":
			case "magiccrit":
			particleType = EnumParticleTypes.CRIT_MAGIC;
			break;
			case "damage":
			case "damageindicator":
			particleType = EnumParticleTypes.DAMAGE_INDICATOR;
			break;
			case "dragonbreath":
			particleType = EnumParticleTypes.DRAGON_BREATH;
			break;
			case "driplava":
			particleType = EnumParticleTypes.DRIP_LAVA;
			break;
			case "dripwater":
			particleType = EnumParticleTypes.DRIP_WATER;
			break;
			case "enchantment":
			case "enchantmenttable":
			particleType = EnumParticleTypes.ENCHANTMENT_TABLE;
			break;
			case "endrod":
			particleType = EnumParticleTypes.END_ROD;
			break;
			case "explosionhuge":
			case "hugeexplosion":
			particleType = EnumParticleTypes.EXPLOSION_HUGE;
			break;
			case "explosionlarge":
			case "largeexplosion":
			particleType = EnumParticleTypes.EXPLOSION_LARGE;
			break;
			case "explosion":
			case "explode":
			case "explosionnormal":
			case "normalexplosion":
			particleType = EnumParticleTypes.EXPLOSION_NORMAL;
			break;
			case "fallingdust":
			case "dust":
			particleType = EnumParticleTypes.FALLING_DUST;
			break;
			case "fireworks":
			case "fireworksspark":
			case "spark":
			particleType = EnumParticleTypes.FIREWORKS_SPARK;
			break;
			case "flame":
			particleType = EnumParticleTypes.FLAME;
			break;
			case "footstep":
			particleType = EnumParticleTypes.FOOTSTEP;
			break;
			case "heart":
			particleType = EnumParticleTypes.HEART;
			break;
			case "itemcrack":
			particleType = EnumParticleTypes.ITEM_CRACK;
			break;
			case "lava":
			particleType = EnumParticleTypes.LAVA;
			break;
			case "mobappearance":
			particleType = EnumParticleTypes.MOB_APPEARANCE;
			break;
			case "note":
			case "music":
			case "musicnote":
			particleType = EnumParticleTypes.NOTE;
			break;
			case "portal":
			particleType = EnumParticleTypes.PORTAL;
			break;
			case "redstone":
			particleType = EnumParticleTypes.REDSTONE;
			break;
			case "slime":
			particleType = EnumParticleTypes.SLIME;
			break;
			case "smokelarge":
			case "largesmoke":
			particleType = EnumParticleTypes.SMOKE_LARGE;
			break;
			case "smoke":
			particleType = EnumParticleTypes.SMOKE_NORMAL;
			break;
			case "snowball":
			particleType = EnumParticleTypes.SNOWBALL;
			break;
			case "snowshovel":
			case "snow":
			particleType = EnumParticleTypes.SNOW_SHOVEL;
			break;
			case "spell":
			particleType = EnumParticleTypes.SPELL;
			break;
			case "spellinstant":
			case "instantspell":
			particleType = EnumParticleTypes.SPELL_INSTANT;
			break;
			case "spellmob":
			case "mobspell":
			particleType = EnumParticleTypes.SPELL_MOB;
			break;
			case "spellmobambient":
			particleType = EnumParticleTypes.SPELL_MOB_AMBIENT;
			break;
			case "spellwitch":
			case "witchspell":
			particleType = EnumParticleTypes.SPELL_WITCH;
			break;
			case "splash":
			case "watersplash":
			case "splashwater":
			particleType = EnumParticleTypes.WATER_SPLASH;
			break;
			case "suspended":
			particleType = EnumParticleTypes.SUSPENDED;
			break;
			case "suspendeddepth":
			particleType = EnumParticleTypes.SUSPENDED_DEPTH;
			break;
			case "sweepattack":
			case "sweep":
			case "attack":
			particleType = EnumParticleTypes.SWEEP_ATTACK;
			break;
			case "townaura":
			case "town":
			particleType = EnumParticleTypes.TOWN_AURA;
			break;
			case "villagerangry":
			case "angryvillager":
			particleType = EnumParticleTypes.VILLAGER_ANGRY;
			break;
			case "villagerhappy":
			case "happyvillager":
			particleType = EnumParticleTypes.VILLAGER_HAPPY;
			break;
			case "wake":
			case "waterwake":
			case "wakewater":
			particleType = EnumParticleTypes.WATER_WAKE;
			break;
		}

		if (particleType == null) {
			return false;
		} else {
			this.particleType = particleType;
			this.type = EnumType.PARTICLE;
			this.itemStack = null;
			return true;
		}
	}

	public static ItemStack parseArchitectureString(String propString) {
		Shape resultShape = null;

		String[] split = propString.split(":");
		if (split.length < 3) return null;

		switch(split[1]) {
			case "roof_tile":
			resultShape = Shape.ROOF_TILE;
			break;
			case "roof_outer_corner":
			resultShape = Shape.ROOF_OUTER_CORNER;
			break;
			case "roof_inner_corner":
			resultShape = Shape.ROOF_INNER_CORNER;
			break;
			case "roof_ridge":
			resultShape = Shape.ROOF_RIDGE;
			break;
			case "roof_smart_ridge":
			resultShape = Shape.ROOF_SMART_RIDGE;
			break;
			case "roof_valley":
			resultShape = Shape.ROOF_VALLEY;
			break;
			case "roof_smart_valley":
			resultShape = Shape.ROOF_SMART_VALLEY;
			break;
			case "roof_overhang":
			resultShape = Shape.ROOF_OVERHANG;
			break;
			case "roof_overhang_outer_corner":
			resultShape = Shape.ROOF_OVERHANG_OUTER_CORNER;
			break;
			case "roof_overhang_inner_corner":
			resultShape = Shape.ROOF_OVERHANG_INNER_CORNER;
			break;
			case "cylinder":
			resultShape = Shape.CYLINDER;
			break;
			case "cylinder_half":
			resultShape = Shape.CYLINDER_HALF;
			break;
			case "cylinder_quarter":
			resultShape = Shape.CYLINDER_QUARTER;
			break;
			case "cylinder_large_quarter":
			resultShape = Shape.CYLINDER_LARGE_QUARTER;
			break;
			case "anticylinder_large_quarter":
			resultShape = Shape.ANTICYLINDER_LARGE_QUARTER;
			break;
			case "pillar":
			resultShape = Shape.PILLAR;
			break;
			case "post":
			resultShape = Shape.POST;
			break;
			case "pole":
			resultShape = Shape.POLE;
			break;
			case "bevelled_outer_corner":
			resultShape = Shape.BEVELLED_OUTER_CORNER;
			break;
			case "bevelled_inner_corner":
			resultShape = Shape.BEVELLED_INNER_CORNER;
			break;
			case "pillar_base":
			resultShape = Shape.PILLAR_BASE;
			break;
			case "doric_capital":
			resultShape = Shape.DORIC_CAPITAL;
			break;
			case "ionic_capital":
			resultShape = Shape.IONIC_CAPITAL;
			break;
			case "corinthian_capital":
			resultShape = Shape.CORINTHIAN_CAPITAL;
			break;
			case "doric_triglyph":
			resultShape = Shape.DORIC_TRIGLYPH;
			break;
			case "doric_triglyph_corner":
			resultShape = Shape.DORIC_TRIGLYPH_CORNER;
			break;
			case "doric_metope":
			resultShape = Shape.DORIC_METOPE;
			break;
			case "architrave":
			resultShape = Shape.ARCHITRAVE;
			break;
			case "architrave_corner":
			resultShape = Shape.ARCHITRAVE_CORNER;
			break;
			case "window_frame":
			resultShape = Shape.WINDOW_FRAME;
			break;
			case "window_corner":
			resultShape = Shape.WINDOW_CORNER;
			break;
			case "window_mullion":
			resultShape = Shape.WINDOW_MULLION;
			break;
			case "sphere_full":
			resultShape = Shape.SPHERE_FULL;
			break;
			case "sphere_half":
			resultShape = Shape.SPHERE_HALF;
			break;
			case "sphere_quarter":
			resultShape = Shape.SPHERE_QUARTER;
			break;
			case "sphere_eighth":
			resultShape = Shape.SPHERE_EIGHTH;
			break;
			case "sphere_eighth_large":
			resultShape = Shape.SPHERE_EIGHTH_LARGE;
			break;
			case "sphere_eighth_large_rev":
			resultShape = Shape.SPHERE_EIGHTH_LARGE_REV;
			break;
			case "roof_overhang_gable_lh":
			resultShape = Shape.ROOF_OVERHANG_GABLE_LH;
			break;
			case "roof_overhang_gable_rh":
			resultShape = Shape.ROOF_OVERHANG_GABLE_RH;
			break;
			case "roof_overhang_gable_end_lh":
			resultShape = Shape.ROOF_OVERHANG_GABLE_END_LH;
			break;
			case "roof_overhang_gable_end_rh":
			resultShape = Shape.ROOF_OVERHANG_GABLE_END_RH;
			break;
			case "roof_overhang_ridge":
			resultShape = Shape.ROOF_OVERHANG_RIDGE;
			break;
			case "roof_overhang_valley":
			resultShape = Shape.ROOF_OVERHANG_VALLEY;
			break;
			case "cornice_lh":
			resultShape = Shape.CORNICE_LH;
			break;
			case "cornice_rh":
			resultShape = Shape.CORNICE_RH;
			break;
			case "cornice_end_lh":
			resultShape = Shape.CORNICE_END_LH;
			break;
			case "cornice_end_rh":
			resultShape = Shape.CORNICE_END_RH;
			break;
			case "cornice_ridge":
			resultShape = Shape.CORNICE_RIDGE;
			break;
			case "cornice_valley":
			resultShape = Shape.CORNICE_VALLEY;
			break;
			case "cornice_bottom":
			resultShape = Shape.CORNICE_BOTTOM;
			break;
			case "cladding_sheet":
			resultShape = Shape.CLADDING_SHEET;
			break;
			case "arch_d_1":
			resultShape = Shape.ARCH_D_1;
			break;
			case "arch_d_2":
			resultShape = Shape.ARCH_D_2;
			break;
			case "arch_d_3_a":
			resultShape = Shape.ARCH_D_3_A;
			break;
			case "arch_d_3_b":
			resultShape = Shape.ARCH_D_3_B;
			break;
			case "arch_d_3_c":
			resultShape = Shape.ARCH_D_3_C;
			break;
			case "arch_d_4_a":
			resultShape = Shape.ARCH_D_4_A;
			break;
			case "arch_d_4_b":
			resultShape = Shape.ARCH_D_4_B;
			break;
			case "arch_d_4_c":
			resultShape = Shape.ARCH_D_4_C;
			break;
			case "banister_plain_bottom":
			resultShape = Shape.BANISTER_PLAIN_BOTTOM;
			break;
			case "banister_plain":
			resultShape = Shape.BANISTER_PLAIN;
			break;
			case "banister_plain_top":
			resultShape = Shape.BANISTER_PLAIN_TOP;
			break;
			case "balustrade_fancy":
			resultShape = Shape.BALUSTRADE_FANCY;
			break;
			case "balustrade_fancy_corner":
			resultShape = Shape.BALUSTRADE_FANCY_CORNER;
			break;
			case "balustrade_fancy_with_newel":
			resultShape = Shape.BALUSTRADE_FANCY_WITH_NEWEL;
			break;
			case "balustrade_fancy_newel":
			resultShape = Shape.BALUSTRADE_FANCY_NEWEL;
			break;
			case "balustrade_plain":
			resultShape = Shape.BALUSTRADE_PLAIN;
			break;
			case "balustrade_plain_outer_corner":
			resultShape = Shape.BALUSTRADE_PLAIN_OUTER_CORNER;
			break;
			case "balustrade_plain_with_newel":
			resultShape = Shape.BALUSTRADE_PLAIN_WITH_NEWEL;
			break;
			case "banister_plain_end":
			resultShape = Shape.BANISTER_PLAIN_END;
			break;
			case "banister_fancy_newel_tall":
			resultShape = Shape.BANISTER_FANCY_NEWEL_TALL;
			break;
			case "balustrade_plain_inner_corner":
			resultShape = Shape.BALUSTRADE_PLAIN_INNER_CORNER;
			break;
			case "balustrade_plain_end":
			resultShape = Shape.BALUSTRADE_PLAIN_END;
			break;
			case "banister_fancy_bottom":
			resultShape = Shape.BANISTER_FANCY_BOTTOM;
			break;
			case "banister_fancy":
			resultShape = Shape.BANISTER_FANCY;
			break;
			case "banister_fancy_top":
			resultShape = Shape.BANISTER_FANCY_TOP;
			break;
			case "banister_fancy_end":
			resultShape = Shape.BANISTER_FANCY_END;
			break;
			case "banister_plain_inner_corner":
			resultShape = Shape.BANISTER_PLAIN_INNER_CORNER;
			break;
			case "slab":
			resultShape = Shape.SLAB;
			break;
			case "stairs":
			resultShape = Shape.STAIRS;
			break;
			case "stairs_outer_corner":
			resultShape = Shape.STAIRS_OUTER_CORNER;
			break;
			case "stairs_inner_corner":
			resultShape = Shape.STAIRS_INNER_CORNER;
			break;
		}

        if (resultShape != null) {
        	ItemStack materialStack = getStack(propString.replace("shape:" + split[1] + ":", ""));
        	if (materialStack != null) {
        		Item materialItem = materialStack.getItem();
        		if (materialItem instanceof ItemBlock) {
        			Block materialBlock = Block.getBlockFromItem(materialItem);
        			if (isAcceptableMaterial(materialBlock)) {
        				return resultShape.kind.newStack(resultShape, materialBlock, materialStack.getItemDamage(), 1);
        			}
        		}
        	}
        }

		return null;
	}

    public static boolean isAcceptableMaterial(Block block) {
        String name = ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString();
        if (block != Blocks.GLASS && block != Blocks.STAINED_GLASS && !(block instanceof BlockSlab) && !name.startsWith("chisel:glass")) {
             return block.getDefaultState().isFullCube() && !block.hasTileEntity();
        } else {
             return true;
        }
   }

	//This is backwards compatibility code. Bodyparts are no longer saved as strings, but we need to be able to load props from before this rework.
	private static int switchBipedBodypart(String bodyPartName) {
		Integer partIndex = 0;
		try {
			partIndex = Integer.valueOf(bodyPartName);
		} catch (NumberFormatException e) {
			partIndex = 0;
		}

		switch(bodyPartName) {
			case "model":
				partIndex = -1;
				break;
			case "hand":
			case "handleft":
			case "lefthand":
				partIndex = 0;
				break;
			case "handright":
			case "righthand":
				partIndex = 1;
				break;
			case "foot":
			case "footleft":
			case "leftfoot":
				partIndex = 2;
				break;
			case "footright":
			case "rightfoot":
				partIndex = 3;
				break;
			case "body":
			case "torso":
				partIndex = 4;
				break;
			case "hat":
			case "head":
				partIndex = 5;
				break;
		}

		return partIndex;
	}

	public void refreshCache(EntityPlayer player) {
		ModelData data = ModelData.get(player);
		EntityLivingBase entity = data.getEntity(player);

		if (entity == null) {
			ModelPartConfig config = null;
			this.propBodyPart = BodyPartManager.getRenderer(player, (this.partIndex >= 0) ? this.partIndex : 4);

			Float yMod = 0F;
			if (this.type == EnumType.ITEM) {
				yMod = 1.5F;
			} else {
				yMod = 0F;
			}

			switch(this.partIndex) {
				case 0:
					config = data.arm1;

					this.partModifierX = (-0.25F * data.getPartConfig(EnumParts.BODY).scaleX) + (-0.0625F * data.getPartConfig(EnumParts.ARM_LEFT).scaleX);
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * data.getPartConfig(EnumParts.BODY).scaleY - 0.125  * data.getPartConfig(EnumParts.ARM_LEFT).scaleY);

					this.propOffsetX = (this.offsetX + 0.0625F) * config.scaleX;
					this.propOffsetY = (this.offsetY - 0.7F) * config.scaleY;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				case 1:
					config = data.arm2;

					this.partModifierX = (0.25F * data.getPartConfig(EnumParts.BODY).scaleX) + (0.0625F * data.getPartConfig(EnumParts.ARM_RIGHT).scaleX);
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * data.getPartConfig(EnumParts.BODY).scaleY - 0.125  * data.getPartConfig(EnumParts.ARM_RIGHT).scaleY);

					this.propOffsetX = (this.offsetX - 0.0625F) * config.scaleX;
					this.propOffsetY = (this.offsetY - 0.7F) * config.scaleY;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				case 2:
					config = data.leg1;

					this.partModifierX = -0.125F * data.getPartConfig(EnumParts.LEG_LEFT).scaleX;
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY);

					this.propOffsetX = this.offsetX * config.scaleX;
					this.propOffsetY = (this.offsetY - 0.7F) * config.scaleY;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				case 3:
					config = data.leg2;

					this.partModifierX = 0.125F * data.getPartConfig(EnumParts.LEG_RIGHT).scaleX;
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_RIGHT).scaleY);

					this.propOffsetX = this.offsetX * config.scaleX;
					this.propOffsetY = (this.offsetY - 0.7F) * config.scaleY;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				case -1:
				case 4:
					config = data.body;

					this.partModifierX = 0.0F;
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * data.getPartConfig(EnumParts.BODY).scaleY);

					this.propOffsetX = this.offsetX * config.scaleX;
					this.propOffsetY = this.offsetY * config.scaleY;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				case 5:
					config = data.head;

					this.partModifierX = 0.0F;
					this.partModifierY = (float) (-yMod + 0.75 * data.getPartConfig(EnumParts.LEG_LEFT).scaleY + 0.75 * data.getPartConfig(EnumParts.BODY).scaleY);

					this.propOffsetX = this.offsetX * config.scaleX;
					this.propOffsetY = (this.offsetY + 0.50F) * config.scaleY + 0.20F;
					this.propOffsetZ = this.offsetZ * config.scaleZ;
					break;
				default:
					return;
			}

			this.propPpOffsetX = this.ppOffsetX * config.scaleX;
			this.propPpOffsetY = this.ppOffsetY * config.scaleY;
			this.propPpOffsetZ = this.ppOffsetZ * config.scaleZ;

			if (this.matchScaling) {
				this.propScaleX = this.scaleX * config.scaleX;
				this.propScaleY = this.scaleY * config.scaleY;
				this.propScaleZ = this.scaleZ * config.scaleZ;
			} else {
				this.propScaleX = this.scaleX;
				this.propScaleY = this.scaleY;
				this.propScaleZ = this.scaleZ;
			}

		} else {
			this.propBodyPart = BodyPartManager.getRenderer(entity, (this.partIndex >= 0) ? this.partIndex : 0);

			if (this.propBodyPart != null) {
				if (this.type == EnumType.ITEM) {
					this.partModifierY = (this.propBodyPart.rotationPointY / 16 - 1.5F) * data.entityScaleY * EntityScaleManagerClient.getScaleMult(data);
				} else {
					this.partModifierY = (this.propBodyPart.rotationPointY / 16) * data.entityScaleY * EntityScaleManagerClient.getScaleMult(data);
				}
			}

			this.propOffsetX = this.offsetX * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);
			this.propOffsetY = this.offsetY * data.entityScaleY * EntityScaleManagerClient.getScaleMult(data);
			this.propOffsetZ = this.offsetZ * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);

			if (this.matchScaling) {
				this.propScaleX = this.scaleX * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);
				this.propScaleY = this.scaleY * data.entityScaleY * EntityScaleManagerClient.getScaleMult(data);
				this.propScaleZ = this.scaleZ * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);
			} else {
				this.propScaleX = this.scaleX * EntityScaleManagerClient.getScaleMult(data);
				this.propScaleY = this.scaleY * EntityScaleManagerClient.getScaleMult(data);
				this.propScaleZ = this.scaleZ * EntityScaleManagerClient.getScaleMult(data);
			}

			this.propPpOffsetX = this.ppOffsetX * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);
			this.propPpOffsetY = this.ppOffsetY * data.entityScaleY * EntityScaleManagerClient.getScaleMult(data);
			this.propPpOffsetZ = this.ppOffsetZ * data.entityScaleX * EntityScaleManagerClient.getScaleMult(data);
		}

		this.refreshCache = false;
	}

	public void flip() {
		this.offsetX = -this.offsetX;

		switch (this.partIndex) {
		case 0:
			this.partIndex = 1;
			break;
		case 1:
			this.partIndex = 0;
			break;
		case 2:
			this.partIndex = 3;
			break;
		case 3:
			this.partIndex = 2;
			break;
		}

		if (this.type == EnumType.ITEM) {
			this.rotateY = -this.rotateY;
			this.rotateZ = -this.rotateZ;
		} else {
			this.yaw = -this.yaw;
		}
	}
}