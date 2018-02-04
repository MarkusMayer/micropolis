package micropolisj.engine.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public class CityMap {

	private Map<MapPosition, MapTile> map;
	MapPosition dim;

	public CityMap(int xDim, int yDim) {
		dim = MapPosition.at(xDim, yDim);
		map = new HashMap<>();
		MapFragment frag = MapFragment.rectOf(dim, new SingleMapTile(Tiles.get(0), null, this));
		map = frag.transposeAndSetTo(map, MapPosition.at(0, 0));
	}

	MapTile getTile(MapPosition pos) {
		checkPosInside(pos);
		return map.get(pos);
	}

	private List<MapPosition> getAllPos() {
		return MapPosition.at(0, 0).getPosForRect(dim);
	}

	private List<MapTile> getAllTiles() {
		return getAllPos().stream().map(aPos -> getTile(aPos)).collect(Collectors.toList());
	}

	public TileSpec getSpec(MapPosition pos) {
		return getTile(pos).getTileSpec();
	}

	public boolean build(MapPosition pos, Building aBuilding) {
		checkPosInside(pos);
		MapFragment frag = aBuilding.getFragment();
		if (!isRectBuildable(pos, pos.plus(frag.getDim())))
			return false;

		map = frag.transposeAndSetTo(map, pos);
		return true;
	}

	void buildRubble(MapPosition pos) {
		putTile(pos, new SingleMapTile(Tiles.get(0), pos, this));
	}

	private void putTile(MapPosition pos, MapTile tile) {
		checkPosInside(pos);
		map.put(pos, tile);
	}

	public void bulldoze(MapPosition pos) {
		checkPosInside(pos);
		if (getTile(pos).isBulldozable()) {
			MapFragment bulldozeFrag = getTile(pos).getBulldozeFragment();
			map = bulldozeFrag.transposeAndSetTo(map, pos);
		}
	}

	private void checkPosInside(MapPosition pos) {
		if (!posInside(pos))
			throw new IllegalArgumentException("position outside city bounds. pos: "+pos+", city-dimmension: "+dim);
	}

	private boolean posInside(MapPosition pos) {
		return (pos.greaterOrEqualThan(MapPosition.at(0, 0)) && pos.lessThan(dim));
	}

	private boolean isRectBuildable(MapPosition leftTop, MapPosition rightBottom) {
		for (MapPosition aPos : leftTop.getPosForRect(rightBottom)) {
			char tileNr = (char) getSpec(aPos).getTileNr();
			if (!(TileConstants.canAutoBulldozeZ(tileNr) || tileNr == TileConstants.DIRT))
				return false;
		}
		return true;
	}

	public void animate() {
		// TODO:
		for (MapPosition pos : getAllPos()) {
			MapTile aTile = getTile(pos);
			aTile.animate();
		}
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

	Set<Building> getAllBuildings() {
		return getAllTiles().stream().filter(aTile -> aTile.hasBuilding()).map(aTile -> aTile.getBuilding())
				.collect(Collectors.toSet());
	}

	Set<Building> getAllBuildingsOfType(BuildingType searchType) {
		return getAllBuildings().stream().filter(aBuilding -> aBuilding.getType() == searchType)
				.collect(Collectors.toSet());
	}

}
