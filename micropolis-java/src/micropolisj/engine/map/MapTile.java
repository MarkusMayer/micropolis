package micropolisj.engine.map;

import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

class MapTile {
	
	protected TileSpec spec;
	protected boolean powered;
	
	MapTile(TileSpec spec){
		this.spec=spec;
		this.powered=false;
	}
	
	TileSpec getTileSpec() {
		return spec;
	}
	
	boolean setTileSpec(TileSpec newSpec) {
		if (newSpec!=spec) {
			spec=newSpec;
			return true;
		}
		return false;
	}
	
	void power() {
		powered=true;
	}

	void unpower() {
		powered=false;
	}
	
	boolean isPowered() {
		return powered;
	}
	
	boolean isBulldozable() {
		return true;
	}

	@Override
	public String toString() {
		return "MapTile spec=" + spec;
	}

	void animate() {
		if (spec.getAnimNext()!= null) {
			spec=spec.getAnimNext();
		}
	}
	
	MapFragment getBulldozeFragment() {
		MapFragment result=new MapFragment(MapPosition.at(1, 1));
		result.addTile(MapPosition.at(0, 0), getRubble());
		return result;
	};
	
	static MapTile getRubble() {
		return new MapTile(Tiles.get(0));
	}
}
