package de.x8bit.Fantasya.Atlantis.Items;

import de.x8bit.Fantasya.util.ComplexName;

public class Mastodon extends Elefant {
	public Mastodon() { super(); }
	
	public String getName() {
		if (anzahl != 1) return "Mastodonten";
		return "Mastodon";
	}
	
	@Override
	public ComplexName getComplexName() {
		return new ComplexName("Mastodon", "Mastodonten", new String[]{"Mastodons"});
	}

	// Rest Elefant
	
	// TODO Wachstum
}
