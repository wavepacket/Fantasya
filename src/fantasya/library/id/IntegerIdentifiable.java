/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package fantasya.library.id;

/**
 * @author Michael Jahn
 * 
 * An interface identification of an object with integer.
 */

public interface IntegerIdentifiable extends Identifiable {
	
	/**
	 * Returns the id as integer.
	 * 
	 * @return id as integer.
	 */
	public int getIntegerId();
}
