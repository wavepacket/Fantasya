package de.x8bit.Fantasya.Host.ManualTests.Misc;


import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Host.ManualTests.TestBase;
import de.x8bit.Fantasya.Host.ManualTests.TestWorld;

/**
 *
 * @author hb
 */
public class TestCoords2ID extends TestBase {

    @Override
    protected void mySetupTest() {
        TestWorld tw = this.getTestWorld();

        Region r = tw.nurBetretbar(getRegions()).get(0);

		int id = r.getCoordinates().hashCode(); // new mode
		new SysMsg("Coord-ID zu " + r.getCoordinates() + " = " + id + " (0x" + Integer.toHexString(id) + ")");

		Coordinates c = Coordinates.fromHashCode(id);
		new SysMsg("Zurückgewandelte Coordinates: " + c);

		tw.setContinueWithZAT(false);
    }

    @Override
    protected boolean verifyTest() {
		throw new UnsupportedOperationException("Dieser Test ist nicht automatisiert.");
    }

}
