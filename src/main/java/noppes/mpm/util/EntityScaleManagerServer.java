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

		File file = new File(dir, "entityScaleMultiplies.txt");
		if (!file.exists()) file.createNewFile();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String strLine;
		while (true) {
			do {
				do {
					if ((strLine = reader.readLine()) == null) {
						reader.close();
						return;
					}
				} while(strLine.startsWith("#"));
			} while(strLine.length() == 0);

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

	public static Float getScaleMult(String name) {
		if (entityMap.containsKey(name)) {
			return entityMap.get(name);
		} else {
			Float mult;
			Boolean child;

			if (name.endsWith("_child")) {
				mult = 0.5F;
				child = true;
			} else {
				mult = 1.0F;
				child = false;
			}

			//TODO Parse class for potential multipliers

			try {
				setScaleMult(name, mult);
			} catch (IOException e) {

			}
			return mult;
		}
	}

	public static void setScaleMult(String name, Float mult) throws IOException {
		entityMap.put(name, mult);

		File dir = null;
		dir = new File(dir, ".." + File.separator + "moreplayermodels");

		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, "entityScaleMultiplies.txt");
		if (!file.exists()) file.createNewFile();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String strLine;
		Boolean reading = true;
		ArrayList<String> array = new ArrayList<String>();
		while (reading) {
			do {
				do {
					if ((strLine = reader.readLine()) == null) {
						reader.close();
						reading = false;
					}
				} while(strLine.length() == 0);
			} while (!strLine.startsWith(name));

			array.add(strLine);
		}

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
