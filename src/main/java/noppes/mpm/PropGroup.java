package noppes.mpm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public class PropGroup {
    public String name;
    public List<Prop> props;
    public Boolean hide;

	public PropGroup(){}

    public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();

         compound.setString("groupName", this.name);

   		for (int i = 0; i < this.props.size(); i++) {
	    	 compound.setTag(("prop" + String.valueOf(i)), this.props.get(i).writeToNBT());
		}

        return compound;
     }

     public void readFromNBT(NBTTagCompound compound) {
        List<Prop> propsTemp = new ArrayList<Prop>();

        this.name = compound.getString("groupName");

   		for (int i = 0; i < Integer.MAX_VALUE; i++) {
  			 NBTTagCompound tag = compound.getCompoundTag("prop" + String.valueOf(i));
  			 Prop prop = new Prop();
	    	 prop.readFromNBT(tag);

	    	 if (prop.propString != "") {
	    		 propsTemp.add(prop);
	    	 } else {
				 break;
	    	 }
		}

        this.props = new ArrayList<Prop>(propsTemp);
     }

     public String getCommand() {
    	String command = "/prop group ";
		return command;
     }
}
