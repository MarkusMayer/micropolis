package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

public final class CompositeCenterMapTile extends CompositeMapTile {

	CompositeCenterMapTile(TileSpec spec, Building building) {
		super(spec, building);
	}

	@Override
	boolean isBulldozable() {
		return true;
	}

	@Override
	MapFragment getBulldozeFragment() {
		return building.getBulldozeFragment();
	}
}
