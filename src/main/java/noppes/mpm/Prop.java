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
    public String name;

	public Prop(){}

	public Prop(String propString, ItemStack itemStack, String bodyPartName,
	    Float scaleX, Float scaleY, Float scaleZ,
	    Float offsetX, Float offsetY, Float offsetZ,
	    Float rotateX, Float rotateY, Float rotateZ,
	    Boolean matchScaling, Boolean hide, String name)
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
	    this.name = name;
	}

	public Prop(String propString, String bodyPartName,
		    Float scaleX, Float scaleY, Float scaleZ,
		    Float offsetX, Float offsetY, Float offsetZ,
		    Float rotateX, Float rotateY, Float rotateZ,
		    Boolean matchScaling, Boolean hide, String name)
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
     }

     public String getCommand() {
    	 String command = "/prop " +
    			 this.propString + " " + this.bodyPartName + " " +
    			 this.scaleX + " " + this.scaleY + " " + this.scaleZ + " " +
    			 this.offsetX + " " + this.offsetY + " " + this.offsetZ + " " +
    			 this.rotateX + " " + this.rotateY + " " + this.rotateZ + " " +
    			 this.matchScaling + " " + this.hide + " " + this.name;
		return command;
     }

     public boolean parsePropString(String propString) {
    	 for (String string : MorePlayerModels.blacklistedPropStrings) {
    		 if (propString.contains(string)) return false;
    	 }

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
              return true;
          }
     }
}
