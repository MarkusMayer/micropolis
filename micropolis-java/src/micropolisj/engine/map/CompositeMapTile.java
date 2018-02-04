package micropolisj.engine.map;

import micropolisj.engine.TileSpec;

public class CompositeMapTile extends MapTile {

	protected final Building building;

	CompositeMapTile(TileSpec spec, Building building) {
		super(spec);
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

	@Override
	MapFragment getBulldozeFragment() {
		throw new IllegalArgumentException("CompositeMapTile is not bulldozable");
	}
	
	@Override
	boolean hasBuilding() {
		return true;
	}

	@Override
	Building getBuilding() {
		return building;
	}
}
