package de.x8bit.Fantasya.util;

import de.x8bit.Fantasya.Atlantis.Coordinates;
import de.x8bit.Fantasya.Atlantis.Messages.BigError;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author hb
 */
public class MapSelection extends TreeSet<Coordinates> {
	private static final long serialVersionUID = 8824892696365664390L;

    public Coordinates getMittelpunkt() {
        int sx = 0; int sy = 0;
        int swelt = 0;
        for (Coordinates c : this) {
            sx += c.getX();
            sy += c.getY();
            swelt += c.getWelt();
        }

        return new Coordinates(
                (int)Math.round((double)sx / (double)this.size()),
                (int)Math.round((double)sy / (double)this.size()),
                (int)Math.round((double)swelt / (double)this.size())
                );
    }

    public void saveFile(String filename) {
        File f = new File(filename);
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));

            for (Coordinates c : this) {
                out.write(c.getX() + " " + c.getY() + " " + c.getWelt() + "\n");
            }

            out.close();
        } catch (FileNotFoundException ex) {
            new BigError(ex);
        } catch (UnsupportedEncodingException ex) {
            new BigError(ex);
        } catch (IOException ex) {
            new BigError(ex);
        }
    }

	public void wachsen(int schritte) {
        for (int i=0; i<schritte; i++) {
            List<Coordinates> zuwachs = new ArrayList<Coordinates>();
            for (Coordinates c : this) {
                for (Coordinates n : c.getNachbarn()) {
                    if (!this.contains(n)) {
                        zuwachs.add(n);
                    }
                }
            }

            for (Coordinates neu : zuwachs) this.add(neu);
        }
    }

    public void schrumpfen(int schritte) {
        for (int i=0; i<schritte; i++) {
            List<Coordinates> wegfall = new ArrayList<Coordinates>();
            for (Coordinates c : this) {
                for (Coordinates n : c.getNachbarn()) {
                    if (!this.contains(n)) {
                        wegfall.add(c);
                    }
                }
            }

            for (Coordinates raus : wegfall) this.remove(raus);
        }
    }

	/**
	 * @return Eine MapSelection aller Koordinaten, die neben Regionen dieser MapSelection liegen und selbst nicht Bestandteil sind.
	 */
	public MapSelection getAussenKontur() {
		// TODO mglw. alternative Implementierung: b = a.clone, b.wachsen(1), b.subtrahieren(a)
		MapSelection sel = new MapSelection();
		for (Coordinates c : this) {
			for (Coordinates n : c.getNachbarn()) {
				if (!this.contains(n)) sel.add(n);
			}
		}
		return sel;
	}

	/**
	 * @return Eine MapSelection aller Koordinaten, die Nachbarn haben, welche wiederum nicht Bestandteil dieser MapSelection sind.
	 */
	public MapSelection getInnenKontur() {
		MapSelection sel = new MapSelection();
		for (Coordinates c : this) {
			for (Coordinates n : c.getNachbarn()) {
				if (!this.contains(n)) {
					sel.add(c);
					break;
				}
			}
		}
		return sel;
	}

	public List<Coordinates> asList() {
		List<Coordinates> retval = new ArrayList<Coordinates>();
		for (Coordinates c : this) retval.add(c);
		return retval;
	}

	/**
	 * @return eine zufällige ausgewählte Koordinate dieser MapSelection - oder null, wenn es hier gar keine Coords gibt.
	 */
	public Coordinates zufaelligeKoordinate() {
		if (this.size() == 0) return null;
		if (this.size() == 1) return this.first();

		List<Coordinates> alle = new ArrayList<Coordinates>();
		alle.addAll(this);
		Collections.shuffle(alle);
		return alle.get(0);
	}

	@Override
	public String toString() {
		return "MapSelection: " + this.size() + " Coords - " + StringUtils.aufzaehlung(this);
	}


}
