package noppes.mpm;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ModelPartData {
	private static Map<String, ResourceLocation> resources = new HashMap<String, ResourceLocation>();
	public int color = 0xFFFFFF;
	public int colorPattern = 0xFFFFFF;
	public byte type = 0;
	public byte pattern = 0;
	public boolean playerTexture = false;
	public String name;
	
	private ResourceLocation location;
	
	public ModelPartData(String name) {
		this.name = name;
	}

	public NBTTagCompound writeToNBT(){
		NBTTagCompound compound = new NBTTagCompound();
		compound.setByte("Type", type);
		compound.setInteger("Color", color);
		compound.setBoolean("PlayerTexture", playerTexture);
		compound.setByte("Pattern", pattern);
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound){
		type = compound.getByte("Type");
		color = compound.getInteger("Color");
		playerTexture = compound.getBoolean("PlayerTexture");
		pattern = compound.getByte("Pattern");
		location = null;
	}
	
	public ResourceLocation getResource(){
		if(location != null)
			return location;
		String texture = name + "/" + type;
		
		if((location = resources.get(texture)) != null)
			return location;
		
		location = new ResourceLocation("moreplayermodels:textures/" + texture + ".png");
		resources.put(texture, location);
		return location;
	}
	
	public void setType(int type){
		this.type = (byte) type;
		location = null;
	}
	
	public String toString(){
		return "Color: " + color + " Type: " + type;
	}

	public String getColor() {
		String str = Integer.toHexString(color);

    	while(str.length() < 6)
    		str = "0" + str;
    	
    	return str;
	}
}
