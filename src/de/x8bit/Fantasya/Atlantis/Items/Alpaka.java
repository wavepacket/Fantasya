package de.x8bit.Fantasya.Atlantis.Items;

import de.x8bit.Fantasya.Atlantis.Helper.ConstructionContainer;
import de.x8bit.Fantasya.Atlantis.Skills.Pferdedressur;
import de.x8bit.Fantasya.util.ComplexName;

public class Alpaka extends Kamel
{
	public Alpaka() 
	{
		super();
		setConstructionSkills(new ConstructionContainer [] { new ConstructionContainer(Pferdedressur.class, 1) } );
	}
	
	public String getName()
	{
		if (anzahl != 1) return "Alpakas";
		return "Alpaka";
	}

	@Override
	public ComplexName getComplexName() {
		return new ComplexName("Alpaka", "Alpakas", null);
	}
	
	// TODO Wachstum
}
