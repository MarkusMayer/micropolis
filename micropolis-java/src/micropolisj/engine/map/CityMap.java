package micropolisj.engine.map;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.TileSpec.BuildingInfo;
import micropolisj.engine.Tiles;

public class CityMap {

	private MapTile[][] map;

	public CityMap(int xDim, int yDim) {
		map = new MapTile[xDim][yDim];
		for (int x = 0; x < xDim; x++) {
			for (int y = 0; y < yDim; y++) {
				buildRubble(new MapPosition(x, y));
			}
		}
	}
	
	private MapTile getTile(MapPosition pos) {
		return map[pos.getX()][pos.getY()];
	}

	public TileSpec getSpec(MapPosition pos) {
		return getTile(pos).getTileSpec();
	}

	public void build(MapPosition pos, Building aBuilding) {
		aBuilding.build(pos);
	}

	public void buildRubble(MapPosition pos) {
		map[pos.getX()][pos.getY()] = new SingleMapTile(Tiles.get(0),pos,this);
	}

	public void bulldoze(MapPosition pos) {
		getTile(pos).bulldoze();
	}
	
	boolean isRectBuildable(MapPosition leftTop, MapPosition rightBottom) {
		for (MapPosition aPos : leftTop.getPosForRect(rightBottom)) {
			char tileNr=(char)getSpec(aPos).getTileNr();
			if (!(TileConstants.canAutoBulldozeZ(tileNr) || tileNr==TileConstants.DIRT))
				return false;
		}
		return true;
	}

	public void animate() {
		//TODO:
	}

	public void power(MapPosition pos) {
		// TODO:
	}

	public void unpower(MapPosition pos) {
		// TODO:
	}

	public boolean getPower(MapPosition pos) {
		// TODO:
		return false;
	}

	void setTile(MapPosition pos, MapTile newTile) {
		map[pos.getX()][pos.getY()] = newTile;
	}
}
