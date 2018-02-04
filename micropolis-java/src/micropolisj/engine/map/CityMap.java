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

	private MapTile getTile(MapPosition pos) {
		return map.get(pos);
	}

	private List<MapPosition> getAllPos() {
		return MapPosition.at(0, 0).getPosForRect(dim);
	}

	private List<MapTile> getAllTiles() {
		return getAllPos().stream()
			.map(aPos -> getTile(aPos))
			.collect(Collectors.toList());
	}

	public TileSpec getSpec(MapPosition pos) {
		return getTile(pos).getTileSpec();
	}

	public void build(MapPosition pos, Building aBuilding) {
		MapFragment frag = aBuilding.getFragment();
		if (!isRectBuildable(pos, pos.plus(frag.getDim())))
			return;

		map = frag.transposeAndSetTo(map, pos);
	}

	public void buildRubble(MapPosition pos) {
		map.put(pos, new SingleMapTile(Tiles.get(0), pos, this));
	}

	public void bulldoze(MapPosition pos) {
		if (getTile(pos).isBulldozable()) {
			MapFragment bulldozeFrag=getTile(pos).getBulldozeFragment();
			map=bulldozeFrag.transposeAndSetTo(map, pos);
		};
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
		return getAllTiles().stream()
				.filter(aTile -> aTile.hasBuilding())
				.map(aTile -> aTile.getBuilding())
				.collect(Collectors.toSet());
	}

	Set<Building> getAllBuildingsOfType(BuildingType searchType) {
		return getAllBuildings().stream().filter(aBuilding -> aBuilding.getType() == searchType)
				.collect(Collectors.toSet());
	}

}
