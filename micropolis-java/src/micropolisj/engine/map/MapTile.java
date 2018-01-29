package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

abstract class MapTile {
	
	protected final TileSpec spec;
	private final MapPosition pos;
	private final CityMap map;
	
	MapTile(TileSpec spec,MapPosition pos,CityMap map){
		this.spec=spec;
		this.pos=pos;
		this.map=map;
	}
	
	MapPosition getPos() {
		return pos;
	}
	
	TileSpec getTileSpec() {
		return spec;
	}

	boolean isBulldozable() {
		//TODO:
		return true;
	}

	abstract void bulldoze();

	@Override
	public String toString() {
		return "MapTile [spec=" + spec + ", pos=" + pos + ", map=" + map + "]";
	}
}
