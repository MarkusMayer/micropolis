package micropolisj.engine.map;

import java.util.Set;

public interface ReadOnlyCityMap {
	public int getTileNr(MapPosition pos);

	public boolean isPosInside(MapPosition pos);
	
	public MapPosition getDimension();
	
	public Set<Building> getAllBuildingsOfType(BuildingType searchType);
}
