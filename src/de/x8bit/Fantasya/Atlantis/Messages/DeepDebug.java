package de.x8bit.Fantasya.Atlantis.Messages;

import de.x8bit.Fantasya.Atlantis.Message;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;

public class DeepDebug extends Message
{
	/**
	 * Konstruktor für die Instantiierung via Reflection beim Laden aus der DB
	 */
	public DeepDebug() {}

	public DeepDebug(String msg, Coordinates c)
	{
		super();
		print(0, msg, null, c);
	}
}
