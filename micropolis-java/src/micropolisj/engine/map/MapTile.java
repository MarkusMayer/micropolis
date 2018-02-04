package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

abstract class MapTile {
	
	protected TileSpec spec;
	
	MapTile(TileSpec spec){
		this.spec=spec;
	}
	
	TileSpec getTileSpec() {
		return spec;
	}

	boolean isBulldozable() {
		//TODO:
		return true;
	}

	@Override
	public String toString() {
		return "MapTile spec=" + spec;
	}

	public void animate() {
		if (spec.getAnimNext()!= null) {
			spec=spec.getAnimNext();
		}
	}
	
	abstract boolean hasBuilding();
	
	abstract Building getBuilding();
	
	abstract MapFragment getBulldozeFragment();
}
