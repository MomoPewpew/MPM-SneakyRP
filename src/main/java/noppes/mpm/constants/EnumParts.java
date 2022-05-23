package noppes.mpm.constants;

public enum EnumParts {
	EARS("ears"),
	HORNS("horns"),
	HAIR("hair"),
	MOHAWK("mohawk"),
	SNOUT("snout"),
	BEARD("beard"),
	TAIL("tail"),
	CLAWS("claws"),
	LEGS("legs"),
	FIN("fin"),
	SKIRT("skirt"),
	WINGS("wings"),
	HEAD("head"),
	BODY("body"),
	BREASTS("breasts"),
	PARTICLES("particles"),
	ARM_LEFT("armleft"),
	ARM_RIGHT("armright"),
	LEG_LEFT("legleft"),
	LEG_RIGHT("legright"),
	EYE1("eye1"),
	EYE2("eye2"),
	HALO("halo"),
	ARMS("arms");

	public String name;
	public int patterns = 1;

	private EnumParts(String name) {
		this.name = name;
	}

	public static EnumParts FromName(String name) {
		EnumParts[] var1 = values();
		int var2 = var1.length;

		for(int var3 = 0; var3 < var2; ++var3) {
			EnumParts e = var1[var3];
			if (e.name.equals(name)) {
				return e;
			}
		}

		return null;
	}
}
