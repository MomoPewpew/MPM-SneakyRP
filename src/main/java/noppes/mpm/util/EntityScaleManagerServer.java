package noppes.mpm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.mpm.Server;
import noppes.mpm.constants.EnumPackets;

public class EntityScaleManagerServer extends EntityScaleManagerBase {
	private static Map<String, Float> entityMap;
	static {
		entityMap = new HashMap<String, Float>();
	}

	public static void buildMap() throws IOException {
		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, "entityScaleMultipliers.txt");
		if (!file.exists()) file.createNewFile();

		FileReader f = new FileReader(file);
		BufferedReader reader = new BufferedReader(f);

		String strLine;

		while((strLine = reader.readLine()) != null){
			if (!strLine.equals("")) {
				String[] array = strLine.split(" ");

				if (array.length == 2) {
					String name = array[0];
					Float mult = null;

					try {
						mult = Float.valueOf(array[1]);
					} catch (NumberFormatException e) {

					}

					if (mult != null) {
						entityMap.put(name, mult);
					}
				}
			}
		}

		f.close();
		reader.close();
	}

	public static void getScaleMult(EntityPlayerMP player, String name) {
		if (entityMap.containsKey(name)) {
			Float mult =  entityMap.get(name);


			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("name", name);
			compound.setFloat("mult", mult);

			Server.sendData(player, EnumPackets.ENTITY_SCALE_MULT, compound);
		}
	}

	public static void setScaleMult(String name, Float mult) throws IOException {
		entityMap.put(name, mult);

		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, "entityScaleMultipliers.txt");
		if (!file.exists()) file.createNewFile();

		FileReader f = new FileReader(file);
		BufferedReader reader = new BufferedReader(f);
		ArrayList<String> array = new ArrayList<String>();

		String strLine;

		while((strLine = reader.readLine()) != null){
			if (!strLine.equals("") && !strLine.startsWith(name)) array.add(strLine);
		}

		f.close();
		reader.close();

		array.add(name + " " + Float.toString(mult));
		Collections.sort(array);

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		for (String string : array) {
			out.write(string);
			out.write(System.getProperty("line.separator"));
		}

		out.close();
	}
}
