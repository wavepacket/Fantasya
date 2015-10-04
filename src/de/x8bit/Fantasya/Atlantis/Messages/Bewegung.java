package de.x8bit.Fantasya.Atlantis.Messages;

import de.x8bit.Fantasya.Atlantis.Coordinates;
import de.x8bit.Fantasya.Atlantis.Message;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Unit;

public class Bewegung extends Message
{
	/**
	 * Konstruktor für die Instantiierung via Reflection beim Laden aus der DB
	 */
	public Bewegung() {}

	public Bewegung(String msg, Unit u)
	{
		super();
		print(0, msg, u.getCoords(), u);
	}
	
	public Bewegung(String msg, Partei partei, Coordinates c)
	{
		super();
		print(0, msg, partei, c);
	}
}
