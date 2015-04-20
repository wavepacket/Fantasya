package de.x8bit.Fantasya.Atlantis.util;

import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Regions.Chaos;

public class DefaultConstantsFactory {
	
	public static final int NO_INT_VALUE = Integer.MIN_VALUE;
	
	public static final int MAX_INT_VALUE = Integer.MAX_VALUE - 1;
	public static final int MIN_INT_VALUE = Integer.MIN_VALUE + 1;
	
	public static final int MIN_COORDINATES_XY = -8192;
	public static final int MAX_COORDINATES_XY = 8191;
	public static final int MIN_COORDINATES_Z = -4;
	public static final int MAX_COORDINATES_Z = 3;
	
	public static final Coordinates NO_COORDINATES_VALUE = Coordinates.create(0, 0, 0);
	
	public static final Class<? extends Region> INVISIBLE_TERRAIN_CLASS = Chaos.class;
	public static final String INVISIBLE_TERRAIN_STRING_VALUE = INVISIBLE_TERRAIN_CLASS.getSimpleName();
}
