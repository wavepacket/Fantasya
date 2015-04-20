package de.x8bit.Fantasya.Host.Reports.util;

import java.util.Comparator;

import de.x8bit.Fantasya.Atlantis.util.Coordinates;

/**
 * @author hb
 */
public class CoordComparatorLNR implements Comparator<Coordinates> {

    /**
     *
     * @param c1
     * @param c2
     * @return -1 wenn r1 < r2, 0 wenn r1 == r2, +1 wenn r1 > r2
     */
    @Override
    public int compare(Coordinates c1, Coordinates c2) {
		// Oberwelt zuerst:
		if (c1.getZ() < c2.getZ()) return +1;
		if (c1.getZ() > c2.getZ()) return -1;

        if (c1.getY() > c2.getY()) return -1;
        if (c1.getY() < c2.getY()) return +1;

        if (c1.getX() < c2.getX()) return -1;
        if (c1.getX() > c2.getX()) return +1;

        return 0;
    }

}
