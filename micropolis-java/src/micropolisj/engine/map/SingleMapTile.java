package micropolisj.engine.map;

import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public final class SingleMapTile extends MapTile {

	static MapTile getRubble() {
		return new SingleMapTile(Tiles.get(0), null, null);
	}
	
	SingleMapTile(TileSpec spec, MapPosition pos, CityMap map) {
		super(spec);
	}

	@Override
	MapFragment getBulldozeFragment() {
		MapFragment result=new MapFragment(MapPosition.at(1, 1));
		result.addTile(MapPosition.at(0, 0), getRubble());
		return result;
	}

	@Override
	boolean hasBuilding() {
		return false;
	}

	@Override
	Building getBuilding() {
		throw new IllegalArgumentException("Single map tile has no building instance.");
	}

}
