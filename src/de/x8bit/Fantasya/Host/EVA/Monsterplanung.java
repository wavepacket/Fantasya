package de.x8bit.Fantasya.Host.EVA;


import de.x8bit.Fantasya.Atlantis.Partei;
import de.x8bit.Fantasya.Atlantis.Region;
import de.x8bit.Fantasya.Atlantis.Unit;
import de.x8bit.Fantasya.Atlantis.Helper.Monster;
import de.x8bit.Fantasya.Atlantis.Messages.Debug;
import de.x8bit.Fantasya.Atlantis.Messages.SysMsg;
import de.x8bit.Fantasya.Atlantis.Units.Dragonfly;
import de.x8bit.Fantasya.Atlantis.Units.Goblin;
import de.x8bit.Fantasya.Atlantis.Units.Greif;
import de.x8bit.Fantasya.Atlantis.Units.Hoellenhund;
import de.x8bit.Fantasya.Atlantis.Units.Kobold;
import de.x8bit.Fantasya.Atlantis.Units.Krake;
import de.x8bit.Fantasya.Atlantis.Units.Puschkin;
import de.x8bit.Fantasya.Host.BefehlsSpeicher;
import de.x8bit.Fantasya.Host.GameRules;
import de.x8bit.Fantasya.Host.EVA.util.Einzelbefehl;
import de.x8bit.Fantasya.util.StringUtils;
import java.util.HashSet;

public class Monsterplanung extends EVABase implements NotACommand
{
	
	public Monsterplanung()
	{
		super("Monsterplanung");
		
		// NMR's für alle Monstervölker zurück setzen
		for(Partei p : Partei.getNPCFactionList()) p.setNMR(GameRules.getRunde());

		// sicherstellen, dass es die fixen Monsterparteien gibt:
        // checkPartei("dark");		// Partei.MONSTER_FACTION
		// checkPartei("tier");		// Partei.ANIMAL_FACTION
		
        // tier - "harmlose" Tiere
        Greif.spawnMonster();
        Dragonfly.spawnMonster();
        Puschkin.spawnMonster();

        // dark - einfache Monster
        Krake.spawnMonster();

        Goblin.NeueRunde();
        Kobold.NeueRunde();

        Hoellenhund.NeueRunde();
		
		// wg. concurrent modification exception. Denke ich...
		for(Unit u : new HashSet<Unit>(Unit.CACHE)) {
			if (!(u instanceof Monster)) continue;
			if (Partei.getFaction(u.getOwner()).isPlayerFaction()) continue;
			
			new Debug(" -> Monsterplanung für " + u);
			((Monster)u).planMonster();
			
			// damit die Befehle auch gut bei der "Klassifizierung" ankommen:
			for (Einzelbefehl eb : u.BefehleExperimental) BefehlsSpeicher.getInstance().remove(eb);
			u.BefehleExperimental.clear();
			
			for (String befehl : u.Befehle) {
				// Deutsche-Umlaute ersetzen
				befehl = befehl.replace("=C4", "Ä");
				befehl = befehl.replace("ä", "ae");
				befehl = befehl.replace("ö", "oe");
				befehl = befehl.replace("ü", "ue");
				befehl = befehl.replace("Ä", "Ae");
				befehl = befehl.replace("Ö", "Oe");
				befehl = befehl.replace("Ü", "Ue");
				befehl = befehl.replace("ß", "ss");
				befehl = StringUtils.only7bit(befehl);
				
				Einzelbefehl eb = new Einzelbefehl(u, befehl);
				try {
					u.BefehleExperimental.add(eb);
				} catch (IllegalArgumentException ex) {
					new SysMsg("Monsterplanung für " + u.getRasse() + " " + u + ": " + ex.getMessage() + "\n" + StringUtils.aufzaehlungUnd(u.Befehle) + "\n" + StringUtils.aufzaehlungUnd(u.BefehleExperimental));
				}
			}
		}

//		new SysMsg(" - Monsterplanung beendet");
	}
	
	/** braucht kein Sack */@Override
	public boolean DoAction(Unit u, String[] befehl) { return false; }
	/** braucht kein Sack */@Override
	public void DoAction(Region r, String befehl) { }
	/** braucht kein Sack */@Override
	public void PostAction() { }
	/** braucht kein Sack */@Override
	public void PreAction() { }
	/** braucht kein Sack */@Override
	public void DoAction(Einzelbefehl eb) { }
	
}
