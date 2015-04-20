package de.x8bit.Fantasya.Atlantis.util.atlas;

public final class PublicIsland extends Island {
	
	public PublicIsland(int id, IslandType type) {
		super(id, type);
	}
	
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("PublicIlsand do not support individual name.");
	}
	
	@Override
	public void setDescription(String description) {
		throw new UnsupportedOperationException("PublicIlsand do not support individual description.");
	}
}
