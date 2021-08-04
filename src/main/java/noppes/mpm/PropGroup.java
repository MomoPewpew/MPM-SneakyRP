package noppes.mpm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.client.Client;
import noppes.mpm.constants.EnumPackets;

public class PropGroup {
	public String name = "";
	public List<Prop> props = new ArrayList<Prop>();
	public Boolean hide = false;
	public EntityPlayer player = null;

	public PropGroup(EntityPlayer player){
		this.player = player;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		compound.setString("groupName", this.name);
		compound.setBoolean("hide", this.hide);

		for (int i = 0; i < this.props.size(); i++) {
			compound.setTag(("prop" + String.valueOf(i)), this.props.get(i).writeToNBT());
		}

		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.props = new ArrayList<Prop>();

		this.name = compound.getString("groupName");
		this.hide = compound.getBoolean("hide");

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			NBTTagCompound tag = compound.getCompoundTag("prop" + String.valueOf(i));
			Prop prop = new Prop();
			prop.readFromNBT(tag);

			if (prop.propString != "") {
				this.props.add(prop);
			} else {
				break;
			}
		}
	}

	public String getCommand() {
		UUID uuid = UUID.randomUUID();
		String command = "/prop group " + uuid.toString();
		return command;
	}

	public void addPropClient(String propString, String bodyPartName,
	Float propScaleX, Float propScaleY, Float propScaleZ,
	Float propOffsetX, Float propOffsetY, Float propOffsetZ,
	Float propRotateX, Float propRotateY, Float propRotateZ,
	Boolean propMatchScaling, Boolean hide, String name,
	Float propPpOffsetX, Float propPpOffsetY, Float propPpOffsetZ
	) {
		Prop prop = new Prop(propString, bodyPartName,
		propScaleX, propScaleY, propScaleZ,
		propOffsetX, propOffsetY, propOffsetZ,
		propRotateX, propRotateY, propRotateZ,
		propMatchScaling, hide, name,
		propPpOffsetX, propPpOffsetY, propPpOffsetZ);

		Client.sendData(EnumPackets.PROP_ADD, prop.writeToNBT());
	}

	public void addPropServer(String propString, String bodyPartName,
	Float propScaleX, Float propScaleY, Float propScaleZ,
	Float propOffsetX, Float propOffsetY, Float propOffsetZ,
	Float propRotateX, Float propRotateY, Float propRotateZ,
	Boolean propMatchScaling, Boolean hide, String name,
	Float propPpOffsetX, Float propPpOffsetY, Float propPpOffsetZ
	) {
		Prop prop = new Prop(propString, bodyPartName,
		propScaleX, propScaleY, propScaleZ,
		propOffsetX, propOffsetY, propOffsetZ,
		propRotateX, propRotateY, propRotateZ,
		propMatchScaling, hide, name,
		propPpOffsetX, propPpOffsetY, propPpOffsetZ);

		this.props.add(prop);
		Server.sendAssociatedData(this.player, EnumPackets.PROP_ADD, this.player.getUniqueID(), prop.writeToNBT());
	}

	public void addPropServer(Prop prop) {
		NBTTagCompound compound = prop.writeToNBT();

		Prop propTemp = new Prop();
		propTemp.readFromNBT(compound);

		this.props.add(propTemp);
		Server.sendAssociatedData(this.player, EnumPackets.PROP_ADD, this.player.getUniqueID(), compound);
	}


	public void removePropClient(Integer index) {
		Client.sendData(EnumPackets.PROP_REMOVE, index);
	}

	public void removePropServer(Integer index) {
		this.props.remove(index);
		Server.sendAssociatedData(this.player, EnumPackets.PROP_REMOVE, this.player.getUniqueID(), index);
	}

	public void removePropServerByName (String name) {
		for (int i = 0; i < this.props.size(); i++) {
			if (this.props.get(i).name.toLowerCase().equals(name.toLowerCase())) {
				this.props.remove(i);
				Server.sendAssociatedData(this.player, EnumPackets.PROP_REMOVE, this.player.getUniqueID(), i);
			}
		}
	}

	public void hidePropServer (Integer index) {
		this.props.get(index).hide = true;
		Server.sendAssociatedData(this.player, EnumPackets.PROP_HIDE, this.player.getUniqueID(), index);
	}

	public void hidePropServerByName (String name) {
		for (int i = 0; i < this.props.size(); i++) {
			if (this.props.get(i).name.toLowerCase().equals(name.toLowerCase())) {
				this.props.get(i).hide = true;
				Server.sendAssociatedData(this.player, EnumPackets.PROP_HIDE, this.player.getUniqueID(), i);
			}
		}
	}

	public void showPropServer (Integer index) {
		this.props.get(index).hide = false;
		Server.sendAssociatedData(this.player, EnumPackets.PROP_SHOW, this.player.getUniqueID(), index);
	}

	public void showPropServerByName (String name) {
		for (int i = 0; i < this.props.size(); i++) {
			if (this.props.get(i).name.toLowerCase().equals(name.toLowerCase())) {
				this.props.get(i).hide = false;
				Server.sendAssociatedData(this.player, EnumPackets.PROP_SHOW, this.player.getUniqueID(), i);
			}
		}
	}

	public void togglePropServer (Integer index) {
		if (this.props.get(index).hide == true) {
			this.props.get(index).hide = false;
			Server.sendAssociatedData(this.player, EnumPackets.PROP_SHOW, this.player.getUniqueID(), index);
		} else {
			this.props.get(index).hide = true;
			Server.sendAssociatedData(this.player, EnumPackets.PROP_HIDE, this.player.getUniqueID(), index);
		}
	}

	public void togglePropServerByName (String name) {
		for (int i = 0; i < this.props.size(); i++) {
			if (this.props.get(i).name.toLowerCase().equals(name.toLowerCase())) {
				if (this.props.get(i).hide == true) {
					this.props.get(i).hide = false;
					Server.sendAssociatedData(this.player, EnumPackets.PROP_SHOW, this.player.getUniqueID(), i);
				} else {
					this.props.get(i).hide = true;
					Server.sendAssociatedData(this.player, EnumPackets.PROP_HIDE, this.player.getUniqueID(), i);
				}
			}
		}
	}

	public void namePropServer (String name) {
		this.props.get(this.props.size() - 1).name = name;
		Server.sendAssociatedData(this.player, EnumPackets.PROP_NAME, this.player.getUniqueID(), name);
	}
}
