package noppes.mpm;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class Prop {
	public String propString = "";
	public ItemStack itemStack = new ItemStack(Blocks.STAINED_GLASS, 1, 2);
	public String bodyPartName = "lefthand";
	public Float scaleX = 1.0F;
	public Float scaleY = 1.0F;
	public Float scaleZ = 1.0F;
	public Float offsetX = 0.0F;
	public Float offsetY = 0.0F;
	public Float offsetZ = 0.0F;
	public Float rotateX = 0.0F;
	public Float rotateY = 0.0F;
	public Float rotateZ = 0.0F;
	public Boolean matchScaling = true;
	public Boolean hide = false;
	public String name = "NONAME";
	public EnumType type = EnumType.ITEM;
	public EnumParticleTypes particleType = null;
	public Float scatter = 0.0F;
	public Float frequency = 1.0F;
	public int amount = 1;
	public Float pitch = 0.0F;
	public Float yaw = 0.0F;
	public Double speed = 0.0D;
	public long lastplayed = System.currentTimeMillis();
	public Float ppOffsetX = 0.0F;
	public Float ppOffsetY = 0.0F;
	public Float ppOffsetZ = 0.0F;
	public Boolean lockrotation = false;

	public enum EnumType {
		ITEM,
		PARTICLE
	}

	public Prop(){}

	public Prop(String propString, String bodyPartName,
	Float scaleX, Float scaleY, Float scaleZ,
	Float offsetX, Float offsetY, Float offsetZ,
	Float rotateX, Float rotateY, Float rotateZ,
	Boolean matchScaling, Boolean hide, String name,
	Float ppOffsetX, Float ppOffsetY, Float ppOffsetZ)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.bodyPartName = bodyPartName;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
		this.hide = hide;
		this.matchScaling = matchScaling;
		this.name = name;
		this.ppOffsetX = ppOffsetX;
		this.ppOffsetY = ppOffsetY;
		this.ppOffsetZ = ppOffsetZ;
	}

	public Prop(String propString, String bodyPartName,
	Float motionScatter, Float frequency, int amount,
	Float offsetX, Float offsetY, Float offsetZ,
	Float pitch, Float yaw, Double speed,
	Boolean hide, String name, Boolean lockrotation)
	{
		this.propString = propString;
		this.parsePropString(this.propString);
		this.bodyPartName = bodyPartName;
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
		compound.setString("bodyPartName", this.bodyPartName);
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
		this.bodyPartName = compound.getString("bodyPartName");
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
	}

	public String getCommand() {
		String command = "";
		if (this.type == EnumType.ITEM) {
			command = "/prop " +
			this.propString + " " + this.bodyPartName + " " +
			this.scaleX + " " + this.scaleY + " " + this.scaleZ + " " +
			this.offsetX + " " + this.offsetY + " " + this.offsetZ + " " +
			this.rotateX + " " + this.rotateY + " " + this.rotateZ + " " +
			this.matchScaling + " " + this.hide + " " + this.name + " " +
			this.ppOffsetX + " " + this.ppOffsetY + " " + this.ppOffsetZ;
		} else if (this.type == EnumType.PARTICLE) {
			command = "/prop " +
			this.propString + " " + this.bodyPartName + " " +
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
			return false;
		} else {
			this.itemStack = new ItemStack(item, 1, dataValue);
			this.type = EnumType.ITEM;
			this.particleType = null;
			return true;
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
}
