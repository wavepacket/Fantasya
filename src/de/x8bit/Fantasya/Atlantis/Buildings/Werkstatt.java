package de.x8bit.Fantasya.Atlantis.Buildings;

import de.x8bit.Fantasya.Atlantis.Building;
import de.x8bit.Fantasya.Atlantis.Item;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Items.Eisen;
import de.x8bit.Fantasya.Atlantis.Items.Holz;
import de.x8bit.Fantasya.Atlantis.Items.Silber;
import de.x8bit.Fantasya.Atlantis.Items.Stein;
import de.x8bit.Fantasya.Atlantis.Messages.Fehler;
import de.x8bit.Fantasya.Atlantis.Skills.Burgenbau;
import de.x8bit.Fantasya.util.ComplexName;

public class Werkstatt extends Building
{
	public void Zerstoere(Unit u)
	{
		super.Zerstoere(u, new Item [] { new Stein(2), new Eisen(2), new Holz(2) });	
	}
	
	public String getTyp() { return "Werkstatt"; }
	
	public int GebaeudeUnterhalt() { return 100; }
	public int UnterhaltEinheit() { return 15; }

	
	/**
	 * erstellt ein Gebäude bzw. baut daran weiter
	 * @param u - diese Einheit will daran bauen
	 */
	@Override
	public void Mache(Unit u)
	{
		// nochmal holen ... ist größer als Null
		int tw = u.Talentwert(Burgenbau.class);
		if (tw < 4)
		{
			new Fehler(u + " hat nicht genügend Talent um an " + this + " zu bauen.", u);
			return;
		}
		
		// zusätzlichen Gebäude testen
		Region region = Region.Load(u.getCoordinates());
		if (!region.hatGebaeude(Burg.class, 50, u))
		{
			new Fehler(u + " - in " + region + " fehlt ein Schloss um " + getTyp() + " bauen zu können.", u);
			return;
		}
		
		GenericMake(u, 0, Burgenbau.class, 4, new Item [] { new Stein(4), new Eisen(2), new Holz(6), new Silber(250) } );
	}

	@Override
	public ComplexName getComplexName() {
		return new ComplexName("Werkstatt", "Werkstätten", null);
	}
}
