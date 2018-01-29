package micropolisj.engine.map;

import java.util.ArrayList;
import java.util.List;

import micropolisj.engine.TileSpec;
import micropolisj.engine.TileSpec.BuildingInfo;
import micropolisj.engine.Tiles;

abstract class Building {

	private final int cost;
	private final TileSpec base;
	private final CityMap map;
	private boolean isPowered;
	private List<CompositeMapTile> tiles;
	private CompositeCenterMapTile centerTile;

	protected Building(int cost, TileSpec base, CityMap map) {
		this.cost = cost;
		this.base = base;
		this.isPowered = false;
		this.tiles = new ArrayList<>();
		this.map = map;
	}

	BuildingInfo getBuildingInfo() {
		return base.getBuildingInfo();
	}

	int getCost() {
		return cost;
	}

	void setPower(boolean newPower) {
		isPowered = newPower;
	}

	boolean getPower() {
		return isPowered;
	}

	void build(MapPosition pos) {
		BuildingInfo bi = getBuildingInfo();
		if (!map.isRectBuildable(pos, pos.plus(bi.getWidth(),bi.getHeight())))
				return;
		for (int yIdx = 0; yIdx < bi.getHeight(); yIdx++) {
			for (int xIdx = 0; xIdx < bi.getWidth(); xIdx++) {
				short curTileNumber = bi.getMembers()[yIdx * bi.getWidth() + xIdx];
				MapPosition newPos = new MapPosition(pos.getX() + xIdx, pos.getY() + yIdx);
				CompositeMapTile newTile;
				if (curTileNumber == bi.getCenterTile()) {
					newTile = centerTile = new CompositeCenterMapTile(Tiles.get(curTileNumber), this, newPos, map);
				} else {
					newTile = new CompositeMapTile(Tiles.get(curTileNumber), this, newPos, map);
					tiles.add(newTile);
				}
				map.setTile(newPos, newTile);
			}
		}
	}

	void bulldoze() {
		for (CompositeMapTile aTile : tiles) {
			map.buildRubble(aTile.getPos());
		}
		map.buildRubble(centerTile.getPos());
	}
}
