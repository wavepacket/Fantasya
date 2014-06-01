package de.x8bit.Fantasya.Atlantis.Helper;

import de.x8bit.Fantasya.Atlantis.Skill;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Messages.Fehler;
import de.x8bit.Fantasya.Host.ZAT.Battle.Krieger;

/**
 * eigentlich nur eine Zwischenklasse um zu verhindern das Monster irgendwas
 * herstellen, lernen etc. ... damit muss nicht bei jedem Monster die passende
 * Methode überschrieben werden ... darf das Monster doch lernen so muss es nur
 * in der Monster-Klasse erlaubt werden<br><br>
 * da Java etwas unflexibel ist, erlauben die <i>allowSOMTHING</i> Methoden wiederrum
 * zur Lernen etc. ... <b>super</b>.<i>super</i>.SOMTHING() ist ja nicht möglich
 * mit Java
 * @author mogel
 */
public abstract class Monster extends Unit
{
    @Override
	public boolean Lernen(Class<? extends Skill> skill) { new Fehler(this + " kann nicht lernen.", this); return false; }
	protected boolean allowLernen(Class<? extends Skill> skill) { return super.Lernen(skill); }

	/**
	 * setzt die entsprechenden Monster-Waffen
	 * @param krieger - dieser Krieger wird gesetzt
	 */
	public void Factory(Krieger krieger)
	{
		// per Default nutzen die nur die üblichen/öffentlichen Waffen
	}

	/**
	 * alle Monster können per Default nicht hungern
	 */
    @Override
	public boolean actionUnterhalt()
	{
		return false;
	}
	
	/**
	 * alle Monster können per Default nicht hungern -> manche sollen aber doch:
	 */
	public boolean actionUnterhaltNormal() {
		return super.actionUnterhalt();
	}
	

    public abstract void planMonster();
    
    /**
     * wird vor der Monsterplanung aufgerufen - bevor die Meldungen der 
     * vergangenen Runde gelöscht werden. Hier kann man also auf das Geschehene
     * reagieren.
     */
    public abstract void meldungenAuswerten();
	
}
