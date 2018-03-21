package micropolisj.engine.map;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReadOnlyCityMap {
	public int getTileNr(MapPosition pos);

	public boolean isPosInside(MapPosition pos);
	
	public void checkPosInside(MapPosition pos);
	
	public MapPosition getDimension();
	
	public Set<Building> getAllBuildingsOfType(BuildingType searchType);
	
	public Set<Building> getAllPoweredBuildingsOfType(BuildingType searchType);
	
	// TODO remove code
	public int findNearestTileFromRange(MapPosition pos, int lowTile, int highTile);
	
	public Map<BuildingType, List<MapPosition>> getAllMapPosOfAllBuildingTypes();

	public MapArea getOccupiedArea(MapPosition target);
}
