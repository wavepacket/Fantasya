package de.x8bit.Fantasya.Host.ManualTests.mogel;

import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.Skills.Magie;
import de.x8bit.Fantasya.Atlantis.Spells.Luftreise;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Host.ManualTests.TestBase;

public class CoordsTest extends TestBase {

	@Override
	protected void mySetupTest() 
	{
		// SetupCoordsDistance();
		SetupLuftreise();
	}
	
	public void SetupCoordsDistance()
	{
		Coordinates c1 = Coordinates.create(0, 0, 0);
		Coordinates c2 = Coordinates.create(1, 0, 0);
		Coordinates c3 = Coordinates.create(1, 1, 0);
		Coordinates c4 = Coordinates.create(1, 2, 0);
		
		new SysMsg("Distance c1 - c2 " + c1.getDistance(c2));	// 1
		new SysMsg("Distance c1 - c3 " + c1.getDistance(c3));	// 1
		new SysMsg("Distance c1 - c4 " + c1.getDistance(c4));	// 2
		
		System.exit(0);
	}
	
	public void SetupLuftreise()
	{
		Partei partei = Partei.getFaction(1);
		Region region = (this.getTestWorld().nurBetretbar(this.getTestWorld().getAlleRegionen())).get(0);

		Unit mage = this.createUnit(partei, region);
		mage.setSkill(Magie.class, 1800);
		mage.setSpell(new Luftreise());
		mage.setAura(mage.Talentwert(Magie.class) * mage.Talentwert(Magie.class));
		mage.Befehle.add("ZAUBERE \"Luftreise\" 1 0");
	}

    @Override
    protected boolean verifyTest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
