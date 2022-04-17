package noppes.mpm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class EntityScaleManagerServer {
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

			setScaleMult(name, mult);
			return mult;
		}
	}

	public static void setScaleMult(String name, Float mult) {
		entityMap.put(name, mult);
		Path path = Paths.get(".." + File.separator + "moreplayermodels" + File.separator + "entityScaleMultiplies.txt");
		String string = System.lineSeparator() + name + " " + mult.toString();

		try {
		    Files.write(path, string.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
		    System.err.println(e);
		}
	}
}
