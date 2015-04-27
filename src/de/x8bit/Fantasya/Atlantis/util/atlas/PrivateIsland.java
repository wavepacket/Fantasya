package de.x8bit.Fantasya.Atlantis.util.atlas;

public final class PrivateIsland extends Island {
	
	public PrivateIsland(int id, int explorationTurn, IslandType type) {
		super(id, explorationTurn,type);
	}
	
	@Override
	public void setName(String name) {
		if (name == null || name.length() <= 0) { this.name = null; return; }
		this.name = name;
	}
	
	@Override
	public void setDescription(String description) {
		if (description == null || description.length() <= 0) { this.description = null; return; }
		this.description = description;
	}
}
