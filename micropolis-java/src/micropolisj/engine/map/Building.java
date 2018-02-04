package micropolisj.engine.map;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.TileSpec.BuildingInfo;
import micropolisj.engine.Tiles;

class Building {

	private final TileSpec base;
	private boolean isPowered;
	private final BuildingType type;

	public static Building getPolice() {
		return new Building(BuildingType.police, Tiles.get(TileConstants.POLICESTATION));
	}
	
	public static Building getFirestation() {
		return new Building(BuildingType.firestation, Tiles.get(TileConstants.FIRESTATION));
	}
	
	public static Building getStadium() {
		return new Building(BuildingType.stadium, Tiles.get(TileConstants.STADIUM));
	}

	
	protected Building(BuildingType type, TileSpec base) {
		this.type = type;
		this.base = base;
		this.isPowered = false;
	}

	BuildingInfo getBuildingInfo() {
		return base.getBuildingInfo();
	}

	BuildingType getType() {
		return type;
	}

	void setPower(boolean newPower) {
		isPowered = newPower;
	}

	boolean getPower() {
		return isPowered;
	}

	MapFragment getFragment() {
		BuildingInfo bi = getBuildingInfo();
		MapFragment frag = new MapFragment(MapPosition.at(bi.getWidth(), bi.getHeight()));

		for (int yIdx = 0; yIdx < bi.getHeight(); yIdx++) {
			for (int xIdx = 0; xIdx < bi.getWidth(); xIdx++) {
				short curTileNumber = bi.getMembers()[yIdx * bi.getWidth() + xIdx];
				MapPosition newPos = new MapPosition(xIdx, yIdx);
				CompositeMapTile newTile;
				if (curTileNumber == bi.getCenterTile()) {
					newTile = new CompositeCenterMapTile(Tiles.get(curTileNumber), this);
				} else {
					newTile = new CompositeMapTile(Tiles.get(curTileNumber), this);
				}
				frag.addTile(newPos, newTile);
			}
		}

		return frag;
	}

	MapFragment getBulldozeFragment() {
		BuildingInfo bi = getBuildingInfo();

		return MapFragment.rectOf(MapPosition.at(bi.getWidth(), bi.getHeight()), getCenterOffset(),
				SingleMapTile.getRubble());

	}

	private MapPosition getCenterOffset() {
		BuildingInfo bi = getBuildingInfo();

		for (int yIdx = 0; yIdx < bi.getHeight(); yIdx++) {
			for (int xIdx = 0; xIdx < bi.getWidth(); xIdx++) {
				short curTileNumber = bi.getMembers()[yIdx * bi.getWidth() + xIdx];
				if (curTileNumber == bi.getCenterTile()) {
					return new MapPosition(-xIdx, -yIdx);
				}
			}
		}

		throw new IllegalArgumentException("Missing center tile for: " + bi);
	}
	
	public BuildingType getBuildingType() {
		return type;
	};
}
