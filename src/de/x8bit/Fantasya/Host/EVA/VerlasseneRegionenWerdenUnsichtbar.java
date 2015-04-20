package de.x8bit.Fantasya.Host.EVA;

import de.x8bit.Fantasya.Atlantis.Helper.RegionsSicht;
import de.x8bit.Fantasya.Atlantis.util.Coordinates;
import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Host.EVA.util.Einzelbefehl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hb
 */
public class VerlasseneRegionenWerdenUnsichtbar extends EVABase implements NotACommand {

    public VerlasseneRegionenWerdenUnsichtbar() {
        super("Verlassene Regionen verschwinden aus dem Sichtbereich...");
    }

    @Override
    public void PostAction() {
        // detaillierte Regions-Sichten aussortieren, die auf nicht mehr vorhandenen Einheiten beruhen:
    	List<Partei> allFactionList = new ArrayList<Partei>();
    	allFactionList.addAll(Partei.getPlayerFactionList());
    	allFactionList.addAll(Partei.getNPCFactionList());
    	allFactionList.add(Partei.OMNI_FACTION);
        for (Partei partei : allFactionList) {
            Set<Coordinates> nichtMehrDetailliert = new HashSet<Coordinates>();
            for (RegionsSicht rs : partei.getKnownRegions(false)) {
                if (!rs.hasDetails()) continue; // das ist immer okay...
                if (rs.getQuelle() != Unit.class) continue; // da mischen wir uns auch nicht ein.

                boolean anwesend = false;
                for (Unit u : Unit.CACHE.getAll(rs.getCoordinates())) {
                    if (u.getOwner() == partei.getNummer()) {
                        anwesend = true;
                        break;
                    }
                }

                // gotcha:
                if (!anwesend) nichtMehrDetailliert.add(rs.getCoordinates());
            }

            for (Coordinates c : nichtMehrDetailliert) {
                partei.removeKnownRegion(c);
                partei.addKnownRegion(c, false, Unit.class);
                // war vorher ja auch Unit.class als Quelle,
                // stell also wahrscheinlich eine in dieser Runde
                // verlassene Region dar.
            }
        }
    }

    @Override
    public boolean DoAction(Unit u, String[] befehl) { throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public void DoAction(Region r, String befehl) { throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public void DoAction(Einzelbefehl eb) { throw new UnsupportedOperationException("Not supported yet."); }

    @Override
    public void PreAction() { }

}
