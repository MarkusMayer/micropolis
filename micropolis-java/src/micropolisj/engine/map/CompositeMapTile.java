package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

public class CompositeMapTile extends MapTile {

	protected final Building building;

	CompositeMapTile(TileSpec spec, Building building, MapPosition pos, CityMap map) {
		super(spec, pos, map);
		this.building = building;
	}

	void setPower(boolean newPower) {
		building.setPower(newPower);
	}

	boolean getPower() {
		return building.getPower();
	}

	@Override
	boolean isBulldozable() {
		return false;
	}

	void bulldoze() {
		throw new IllegalArgumentException("CompositeMapTile is not bulldozable");
	}
}
