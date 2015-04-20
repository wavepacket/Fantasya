package de.x8bit.Fantasya.Host.ManualTests.Kampf;

import de.x8bit.Fantasya.Atlantis.Kampfposition;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Items.Bogen;
import de.x8bit.Fantasya.Atlantis.Items.Katapult;
import de.x8bit.Fantasya.Atlantis.Items.Kettenhemd;
import de.x8bit.Fantasya.Atlantis.Items.Speer;
import de.x8bit.Fantasya.Atlantis.Messages.Info;
import de.x8bit.Fantasya.Atlantis.Skills.Bogenschiessen;
import de.x8bit.Fantasya.Atlantis.Skills.Katapultbedienung;
import de.x8bit.Fantasya.Atlantis.Skills.Speerkampf;
import de.x8bit.Fantasya.Atlantis.Units.Mensch;
import de.x8bit.Fantasya.Host.ManualTests.TestBase;
import de.x8bit.Fantasya.Host.ManualTests.TestWorld;

/**
 *
 * @author hb
 */
public class BeispielKampf03f extends TestBase {

    @Override
    protected void mySetupTest() {
        TestWorld tw = this.getTestWorld();
		Partei pa = tw.createPartei(Mensch.class);
        pa.setName("Genaue");
		Partei pb = tw.createPartei(Mensch.class);
        pb.setName("Brutale");
        Region r = tw.nurBetretbar(getRegions()).get(0);
		// r.setName(getName()+" R");
        getRegions().remove(r);

        {
            Unit genauePikeniere = this.createUnit(pa, r);
            genauePikeniere.setName("Genaue Pikeniere");
            genauePikeniere.setKampfposition(Kampfposition.Vorne);
            genauePikeniere.setPersonen(10);
            genauePikeniere.setSkill(Speerkampf.class, genauePikeniere.getPersonen() * 450);
            genauePikeniere.setItem(Speer.class, genauePikeniere.getPersonen());
            genauePikeniere.setItem(Kettenhemd.class, genauePikeniere.getPersonen());
//            String idA = genauePikeniere.getNummerBase36();

            Unit schuetzen = this.createUnit(pa, r);
            schuetzen.setName("Genaue Schützen");
            schuetzen.setKampfposition(Kampfposition.Hinten);
            schuetzen.setPersonen(10);
            schuetzen.setSkill(Bogenschiessen.class, schuetzen.getPersonen() * 1650);
            schuetzen.setItem(Bogen.class, schuetzen.getPersonen());

            Unit schuetzen2 = this.createUnit(pa, r);
            schuetzen2.setName("Genaue Front-Schützen");
            schuetzen2.setKampfposition(Kampfposition.Hinten);
            schuetzen2.setPersonen(10);
            schuetzen2.setSkill(Bogenschiessen.class, schuetzen2.getPersonen() * 1650);
            schuetzen2.setItem(Bogen.class, schuetzen2.getPersonen());

            Unit schuetzen3 = this.createUnit(pa, r);
            schuetzen3.setName("Nicht so genaue Schützen");
            schuetzen3.setKampfposition(Kampfposition.Hinten);
            schuetzen3.setPersonen(10);
            schuetzen3.setSkill(Bogenschiessen.class, schuetzen3.getPersonen() * 1650);
            schuetzen3.setItem(Bogen.class, schuetzen3.getPersonen());
            schuetzen3.Befehle.add("ATTACKIERE VORNE");

            Unit b = this.createUnit(pb, r);
            b.setName("Brutale Pikeniere");
            b.setKampfposition(Kampfposition.Vorne);
            b.setPersonen(10);
            b.setSkill(Speerkampf.class, b.getPersonen() * 450);
            b.setItem(Speer.class, b.getPersonen());
            b.setItem(Kettenhemd.class, b.getPersonen());
            String idB = b.getNummerBase36();

            b = this.createUnit(pb, r);
            b.setName("Brutaler Katapultschütze");
            b.setKampfposition(Kampfposition.Hinten);
            b.setPersonen(1);
            b.setSkill(Katapultbedienung.class, b.getPersonen() * 300);
            // b.setSkill(Tarnung.class, b.getPersonen() * Skill.LerntageFuerTW(10));
            b.setItem(Katapult.class, 1);
            String idKatapultschuetze = b.getNummerBase36();

            b = this.createUnit(pb, r);
            b.setName("Brutale Schützen");
            b.setKampfposition(Kampfposition.Hinten);
            b.setPersonen(9);
            b.setSkill(Bogenschiessen.class, b.getPersonen() * 1650);
            b.setItem(Bogen.class, b.getPersonen());
            b.Befehle.add("ATTACKIERE vorne");
            String idBrutaleSchuetzen = b.getNummerBase36();

            b = this.createUnit(pb, r);
            b.setName("Brutal zum Kampf gezwungene Bauern");
            b.setKampfposition(Kampfposition.Hinten);
            b.setPersonen(80);

            genauePikeniere.Befehle.add("ATTACKIERE " + idB);
            schuetzen.Befehle.add("ATTACKIERE GEZIELT " + idKatapultschuetze + " " + idBrutaleSchuetzen);
            schuetzen2.Befehle.add("ATTACKIERE GEZIELT " + idKatapultschuetze + " " + idB);

            new Info(this.getName() + " Setup in " + r + ".", genauePikeniere, genauePikeniere.getCoordinates());
        }
    }

    @Override
    protected boolean verifyTest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
