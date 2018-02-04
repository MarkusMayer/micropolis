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
		return !building.isCenterIntact();
	}

	@Override
	MapFragment getBulldozeFragment() {
		if (isBulldozable()) {
			MapFragment result=new MapFragment(MapPosition.at(1, 1));
			result.addTile(MapPosition.at(0, 0), SingleMapTile.getRubble());
			return result;
		} 
		return new MapFragment(MapPosition.at(0, 0));
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
