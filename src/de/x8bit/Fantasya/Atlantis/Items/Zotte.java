package de.x8bit.Fantasya.Atlantis.Items;

import de.x8bit.Fantasya.util.ComplexName;

public class Zotte extends Pferd {
	public Zotte() { super(); }

	@Override
	public String getName()	{
		if (anzahl != 1) return "Zotten";
		return "Zotte";
	}

	@Override
	public ComplexName getComplexName() {
		return new ComplexName("Zotte", "Zotten", null);
	}
	
	// Rest wie Pferd
	
	// TODO Wachstum anpassen
}
