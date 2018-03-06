package micropolisj.engine.map;

import java.util.Objects;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.TileSpec.BuildingInfo;
import micropolisj.engine.Tiles;

class Building {

	private final TileSpec base;
	private final MapPosition topLeft,center;
	private final MapArea area;
	private boolean isPowered;
	private final BuildingType type;
	private boolean centerIntact;

	static Building fromTopLeft(BuildingType type,MapPosition topLeft) {
		return new Building(type,topLeft);
	}

	static Building fromCenter(BuildingType type,MapPosition center) {
		return new Building(type,center.plus(type.getBase().getBuildingInfo().getCenterOffset().reverse()));
	}
	
	private Building(BuildingType type,MapPosition topLeft) {
		this.type = Objects.requireNonNull(type);
		this.base = type.getBase();
		this.topLeft=Objects.requireNonNull(topLeft);
		this.center=topLeft.plus(getCenterOffset());
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
	
	public MapPosition getTopLeftPos() {
		return topLeft;
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
	
	public MapArea getArea() {
		MapPosition upperLeft=topLeft;
		MapPosition lowerRight=upperLeft.plus(MapPosition.at(getBuildingInfo().getWidth(), getBuildingInfo().getHeight()));
		return MapArea.of(upperLeft, lowerRight);
	}

	MapPosition getCenterOffset() {
		return getBuildingInfo().getCenterOffset();
	}
	
	BuildingType getBuildingType() {
		return type;
	};
	
	boolean isInside(MapPosition pos) {
		return area.isInside(pos);
	}

	public MapPosition getCenter() {
		return center;
	}
}
