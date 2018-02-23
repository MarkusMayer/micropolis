package micropolisj.engine.map;

import java.util.Objects;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.TileSpec.BuildingInfo;
import micropolisj.engine.Tiles;

class Building {

	private final TileSpec base;
	private final MapPosition pos;
	private final MapArea area;
	private boolean isPowered;
	private final BuildingType type;
	private boolean centerIntact;

	protected Building(BuildingType type,MapPosition pos) {
		this.type = Objects.requireNonNull(type);
		this.base = type.getBase();
		this.pos=Objects.requireNonNull(pos);
		this.area=getArea();
		this.isPowered = false;
		this.centerIntact=true;
	}

	BuildingInfo getBuildingInfo() {
		return base.getBuildingInfo();
	}

	BuildingType getType() {
		return type;
	}
	
	public MapPosition getPos() {
		return pos;
	}

	void setPower(boolean newPower) {
		isPowered = newPower;
	}

	boolean getPower() {
		return isPowered;
	}
	
	boolean isCenterIntact() {
		return centerIntact;
	}
	
	void notifyCenterDestroyed() {
		centerIntact=false;
	}

	MapFragment getFragment() {
		BuildingInfo bi = getBuildingInfo();
		MapFragment frag = new MapFragment(MapPosition.at(bi.getWidth(), bi.getHeight()));

		for (int yIdx = 0; yIdx < bi.getHeight(); yIdx++) {
			for (int xIdx = 0; xIdx < bi.getWidth(); xIdx++) {
				short curTileNumber = bi.getMembers()[yIdx * bi.getWidth() + xIdx];
				MapPosition newPos = new MapPosition(xIdx, yIdx);
				
				frag.addTile(newPos, new MapTile(Tiles.get(curTileNumber)));
			}
		}

		return frag;
	}

	MapFragment getBulldozeFragment() {
		BuildingInfo bi = getBuildingInfo();

		return MapFragment.rectOfSingleMapTile(MapPosition.at(bi.getWidth(), bi.getHeight()), getCenterOffset().reverse(),
				Tiles.get(TileConstants.DIRT));

	}
	
	private MapArea getArea() {
		MapPosition upperLeft=pos.plus(getCenterOffset().reverse());
		MapPosition lowerRight=upperLeft.plus(MapPosition.at(getBuildingInfo().getWidth(), getBuildingInfo().getHeight()));
		return new MapArea(upperLeft, lowerRight);
	}

	MapPosition getCenterOffset() {
		BuildingInfo bi = getBuildingInfo();

		for (int yIdx = 0; yIdx < bi.getHeight(); yIdx++) {
			for (int xIdx = 0; xIdx < bi.getWidth(); xIdx++) {
				short curTileNumber = bi.getMembers()[yIdx * bi.getWidth() + xIdx];
				if (curTileNumber == bi.getCenterTile()) {
					return new MapPosition(xIdx, yIdx);
				}
			}
		}

		throw new IllegalArgumentException("Missing center tile for: " + bi);
	}
	
	BuildingType getBuildingType() {
		return type;
	};
	
	boolean isInside(MapPosition pos) {
		return area.isInside(pos);
	}
}
