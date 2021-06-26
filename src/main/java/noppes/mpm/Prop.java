package noppes.mpm;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class Prop {
	public String propString;
    public ItemStack itemStack;
    public String bodyPartName;
    public Float scaleX;
    public Float scaleY;
    public Float scaleZ;
    public Float offsetX;
    public Float offsetY;
    public Float offsetZ;
    public Float rotateX;
    public Float rotateY;
    public Float rotateZ;
    public Boolean matchScaling;
    public Boolean hide;

	public Prop(){}

	public Prop(String propString, ItemStack itemStack, String bodyPartName,
	    Float scaleX, Float scaleY, Float scaleZ,
	    Float offsetX, Float offsetY, Float offsetZ,
	    Float rotateX, Float rotateY, Float rotateZ,
	    Boolean matchScaling, Boolean hide)
	{
		this.propString = propString;
	    this.itemStack = itemStack;
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
	    this.matchScaling = matchScaling;
	    this.hide = hide;

	    if (this.itemStack == null) {
	    	 ResourceLocation resourcelocation = new ResourceLocation(this.propString);
	    	 this.itemStack = new ItemStack(Item.REGISTRY.getObject(resourcelocation));
	    }
	}

	public Prop(String propString, String bodyPartName,
		    Float scaleX, Float scaleY, Float scaleZ,
		    Float offsetX, Float offsetY, Float offsetZ,
		    Float rotateX, Float rotateY, Float rotateZ,
		    Boolean matchScaling, Boolean hide)
		{
			this.propString = propString;
	    	ResourceLocation resourcelocation = new ResourceLocation(this.propString);
	    	this.itemStack = new ItemStack(Item.REGISTRY.getObject(resourcelocation));
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
         return compound;
     }

     public void readFromNBT(NBTTagCompound compound) {
    	 this.propString = compound.getString("propString");
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

    	 ResourceLocation resourcelocation = new ResourceLocation(this.propString);
    	 this.itemStack = new ItemStack(Item.REGISTRY.getObject(resourcelocation));
     }

     public String getCommand() {
    	 String command = "/prop " +
    			 this.propString + " " + this.bodyPartName + " " +
    			 this.scaleX + " " + this.scaleY + " " + this.scaleZ + " " +
    			 this.offsetX + " " + this.offsetY + " " + this.offsetZ + " " +
    			 this.rotateX + " " + this.rotateY + " " + this.rotateZ + " " +
    			 this.matchScaling + " " + this.hide;
		return command;
     }
}
