package micropolisj.engine.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public class CityMap implements ReadOnlyCityMap {
	// TODO implement read only view
	// TODO generalize Map to <MapPosition, Object> with scale property for various
	// maps (power map,...)

	private MapBase<MapTile> map;
	private MapBase<Building> buildMap;
	MapPosition dim;

	public CityMap(int xDim, int yDim) {
		dim = MapPosition.at(xDim, yDim);
		map = new MapBase<>(dim, () -> new MapTile(Tiles.get(TileConstants.DIRT)));
		buildMap = new MapBase<>(dim);
		// MapFragment frag = MapFragment.rectOfSingleMapTile(dim,
		// Tiles.get(TileConstants.DIRT));
		// map = frag.transposeAndSetTo(map, MapPosition.at(0, 0));
	}

	public MapPosition getDimension() {
		return dim;
	}

	MapTile getTile(MapPosition pos) {
		checkPosInside(pos);
		return map.getAt(pos);
	}

	private List<MapPosition> getAllPos() {
		return MapPosition.at(0, 0).getPosForRect(dim);
	}

	List<MapTile> getAllTiles() {
		return getAllPos().stream().map(aPos -> getTile(aPos)).collect(Collectors.toList());
	}

	public TileSpec getSpec(MapPosition pos) {
		checkPosInside(pos);
		return getTile(pos).getTileSpec();
	}

	public int getTileNr(MapPosition pos) {
		return getSpec(pos).getTileNr();
	}

	public boolean setSpec(MapPosition pos, TileSpec newSpec) {
		checkPosInside(pos);
		// System.out.println(pos+" ==> "+newSpec);
		boolean res = getTile(pos).setTileSpec(newSpec);
		BuildingType.getTypeFromSpec(newSpec).map(aType -> build(pos, aType));
		// System.out.println("after: "+pos+" ==> "+getTileNr(pos));
		return res;
	}

	public boolean build(MapPosition pos, BuildingType type) {
		System.out.println("build(pos, type): "+pos+" ,"+type);
		checkPosInside(pos);
		Building aBuilding = new Building(type, pos);
		buildMap.putAt(pos.plus(aBuilding.getCenterOffset()), aBuilding);
		MapFragment frag = aBuilding.getFragment();
		if (!isRectBuildable(pos, pos.plus(frag.getDim())))
			return false;

		map = frag.transposeAndSetTo(map, pos);
		return true;
	}

	public void bulldoze(MapPosition pos) {
		checkPosInside(pos);
		MapFragment bulldozeFrag = MapFragment.empty();
		if (buildMap.containsKey(pos)) {
			bulldozeFrag = buildMap.getAt(pos).getBulldozeFragment();
		} else if (getBuilding(pos).isPresent()) {
			// TODO fix this
			Building building = getBuilding(pos).get();
			if (!building.isCenterIntact())
				bulldozeFrag = getTile(pos).getBulldozeFragment();
		} else {
			if (getTile(pos).isBulldozable())
				bulldozeFrag = getTile(pos).getBulldozeFragment();
		}

		map = bulldozeFrag.transposeAndSetTo(map, pos);
	}

	void buildRubble(MapPosition pos) {
		putTile(pos, MapTile.getRubble());
	}

	private void putTile(MapPosition pos, MapTile tile) {
		checkPosInside(pos);
		map.putAt(pos, tile);
	}

	private void checkPosInside(MapPosition pos) {
		if (!isPosInside(pos))
			throw new IllegalArgumentException(
					"position outside city bounds. pos: " + pos + ", city-dimmension: " + dim);
	}

	public boolean isPosInside(MapPosition pos) {
		return (pos.greaterOrEqualThan(MapPosition.at(0, 0)) && pos.lessThan(dim));
	}

	private boolean isRectBuildable(MapPosition leftTop, MapPosition rightBottom) {
		for (MapPosition aPos : leftTop.getPosForRect(rightBottom)) {
			if (!isPosInside(aPos))
				return false;
			char tileNr = (char) getSpec(aPos).getTileNr();
			if (!(TileConstants.canAutoBulldozeZ(tileNr) || tileNr == TileConstants.DIRT))
				return false;
		}
		return true;
	}

	public void animate() {
		for (MapTile tile : getAllTiles()) {
			tile.animate();
		}
	}

	public void power(MapPosition pos) {
		getBuilding(pos).ifPresent(building -> building.setPower(true));
		getTile(pos).power();
	}

	public void unpower(MapPosition pos) {
		getBuilding(pos).ifPresent(building -> building.setPower(false));
		getTile(pos).unpower();
	}

	public boolean isPowered(MapPosition pos) {
		return getBuilding(pos).map(building -> building.getPower()).orElse(getTile(pos).isPowered());
	}

	Set<Building> getAllBuildings() {
		return new HashSet<>(buildMap.values());
	}

	// TODO change signature to TilePos or new class BuildingPos
	public Set<MapPosition> getAllMapPosOfType(BuildingType searchType) {
		return buildMap.values().stream().filter(aBuilding -> aBuilding.getType() == searchType)
				.map(aBuilding -> aBuilding.getPos()).collect(Collectors.toSet());
	}

	public Set<MapPosition> getAllMapPosOfType(Set<BuildingType> searchTypes) {
		Set<MapPosition> result = new HashSet<>();
		for (BuildingType type : searchTypes) {
			result.addAll(getAllMapPosOfType(type));
		}
		return result;
	}

	public Set<Building> getAllBuildingsOfType(BuildingType searchType) {
		return getAllBuildings().stream().filter(aBuilding -> aBuilding.getType() == searchType)
				.collect(Collectors.toSet());
	}

	public Set<MapPosition> getAllPowerPlantMapPos() {
		return getAllMapPosOfType(BuildingType.getPowerPlantTypes());
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("=================================================================\n");
		for (MapPosition pos : getAllPos()) {
			res.append(getTile(pos) + ", ");
		}
		return res.toString();
	}

	private Optional<Building> getBuilding(MapPosition pos) {
		for (MapPosition centerPos : buildMap.keySet()) {
			Building building = buildMap.getAt(centerPos);
			if (building.isInside(pos))
				return Optional.of(building);
		}
		return Optional.empty();
	}

	public ReadOnlyCityMap getReadOnlyMap() {
		return this;
	}

	public void rebuildFromTiles() {
		for (MapPosition aPos : getAllPos()) {
			boolean x = BuildingType.getTypeFromSpec(getSpec(aPos)).map(aType -> build(aPos, aType)).orElse(false);
		}
		System.out.println("rebuildFromTiles: buildMap.size():  "+buildMap.keySet().size());
	}
}
