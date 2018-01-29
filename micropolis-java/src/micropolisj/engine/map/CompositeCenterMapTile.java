package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

public final class CompositeCenterMapTile extends CompositeMapTile {

	CompositeCenterMapTile(TileSpec spec, Building building, MapPosition pos, CityMap map) {
		super(spec, building, pos, map);
	}

	@Override
	boolean isBulldozable() {
		return true;
	}

	@Override
	void bulldoze() {
		building.bulldoze();
	}

}
