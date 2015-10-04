package de.x8bit.Fantasya.Host.Terraforming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.x8bit.Fantasya.Atlantis.Atlantis;
import de.x8bit.Fantasya.Atlantis.Coordinates;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Regions.Lavastrom;
import de.x8bit.Fantasya.util.MapSelection;
import de.x8bit.Fantasya.util.Random;

/**
 *
 * @author hb
 */
public class KleinerLavastrom extends ProtoInsel {
	private static final long serialVersionUID = 1L;

    public KleinerLavastrom(int welt, String inselName) {
		super();
		
		this.name = inselName;
		this.zielGroesse = rndGroesse();
		this.setWelt(welt);
    }

	@Override
	public void create() {
		fuellTyp = Lavastrom.class;
		
		// zwei Keime pflanzen:
		Coordinates c1 = new Coordinates(0, 0, this.getWelt());
		MapSelection endpunktSuche = new MapSelection();
		endpunktSuche.add(c1);
		endpunktSuche.wachsen(this.getZielGroesse());
		Coordinates c2 = endpunktSuche.getInnenKontur().zufaelligeKoordinate();
		
		MapSelection ma = new MapSelection();
		ma.add(c1);
		Region r = new Lavastrom(); r.setCoords(c1); r.setName(getName() + "-Anfang!");
		this.putRegion(r);
		
		MapSelection mb = new MapSelection();
		mb.add(c2);
		r = new Lavastrom(); r.setCoords(c2); r.setName(getName() + "-Ende!");
		this.putRegion(r);
		
		
		// die 10000 ist nur zur Sicherheit, normalerweise findet vorher ein break; statt!
		for (int loop = 0; loop < 10000; loop++) {
			// von c1 in eine beliebige Richtung wachsen:
			MapSelection kontur = ma.getAussenKontur();
			Coordinates wachsen1 = kontur.zufaelligeKoordinate();
			// new Debug("ma wächst: " + wachsen1 + " / " + kontur.toString());
			if (mb.contains(wachsen1)) break;
			ma.add(wachsen1);
			
			// von c2 nur mit gleichem oder geringeren Abstand zu c1 wachsen:
			int minDistance = Integer.MAX_VALUE;
			for (Coordinates c : mb)	if (c.getDistance(c1) < minDistance) minDistance = c.getDistance(c1);
			List<Coordinates> kandidaten = new ArrayList<Coordinates>();
			for (Coordinates c : mb.getAussenKontur()) if (c.getDistance(c1) <= minDistance) kandidaten.add(c);
			Collections.shuffle(kandidaten);
			Coordinates wachsen2 = kandidaten.get(0);
			if (ma.contains(wachsen2)) break;
			mb.add(wachsen2);
			
			
			r = new Lavastrom(); r.setCoords(wachsen1); r.setName(getName() + "-Anfang@" + loop);
			// new Debug("Region @ " + r.getCoords());
			this.putRegion(r);
			// new Debug("Insel enthält: " + this.get(wachsen1.getX()).get(wachsen1.getY()));
			r = new Lavastrom(); r.setCoords(wachsen2); r.setName(getName() + "-Ende@" + loop);
			this.putRegion(r);
		}

		// Lücken füllen (Binnenseen):
		binnenSeenFuellen();
		
		// und der Mittelpunkt ist:
		this.mittelpunkt = null;
		Coordinates m = this.getMittelpunkt(true); // mit Ozean
		if (this.getRegion(m.getX(), m.getY()) != null) {
			r = this.getRegion(m.getX(), m.getY());
			r.setName("M-" + r.getName());
		}
		this.shift(0 - m.getX(), 0 - m.getY());

		luxusProdukteSetzen();
	}
	
	/**
	 * @return W6 + W2 + 1 = ~ 6 (zwischen 3 und 9 Regionen)
	 */
	@Override
	protected int rndGroesse() {
		return Random.W(6) + Random.W(2) + 1;
	}

	@Override
	protected int rndAbstand() {
		return (0);
	}

	@Override
	public Map<Class<? extends Atlantis>, Double> getTerrainProbabilities() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


}

