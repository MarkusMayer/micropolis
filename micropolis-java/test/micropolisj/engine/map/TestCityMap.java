package micropolisj.engine.map;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsMapContaining.hasEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import micropolisj.engine.TileConstants;
import micropolisj.engine.Tiles;

public class TestCityMap {

	CityMap map;
	Tiles t;

	@BeforeClass
	public static void setupSpecs() {

	}

	@Before
	public void buildMap() {
		map = new CityMap(10, 10);
	}

	@Test
	public void testGetSpecBuildingBase() {
		Assert.assertNotNull(map.getSpec(MapPosition.at(0, 0)));
		map.buildTopLeft(MapPosition.at(1, 1), BuildingType.firestation);
		Assert.assertEquals(Tiles.get(TileConstants.FIRESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildOutsideCity() {
		// a
		map.buildTopLeft(MapPosition.at(11, 11), BuildingType.firestation);
	}

	@Test
	public void testGetSpecBuildingOtherTile() {
		map.buildTopLeft(MapPosition.at(1, 1), BuildingType.police);
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE + 1), map.getSpec(MapPosition.at(2, 1)));
	}

	@Test
	public void bulldozeCenter() {
		map.buildTopLeft(MapPosition.at(1, 1), BuildingType.firestation);
		map.bulldoze(MapPosition.at(2, 2));
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(2, 2)));
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(1, 2)));
	}

	@Test
	public void buildOverBuilding() {
		map.buildTopLeft(MapPosition.at(0, 0), BuildingType.police);
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE), map.getSpec(MapPosition.at(0, 0)));
		Assert.assertNotEquals(Tiles.get(TileConstants.POLICESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
		map.buildTopLeft(MapPosition.at(1, 1), BuildingType.firestation);
		Assert.assertNotEquals(Tiles.get(TileConstants.FIRESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
	}

	@Test
	public void findPosOfSingleType() {
		map.buildTopLeft(MapPosition.at(0, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(4, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(5, 0), BuildingType.nukePower);

		Assert.assertEquals(2, map.getAllMapPosOfType(BuildingType.coalPower).size());
	}

	@Test
	public void findPosOfTypeSet() {
		map.buildTopLeft(MapPosition.at(0, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(4, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(5, 0), BuildingType.nukePower);
		map.buildTopLeft(MapPosition.at(5, 5), BuildingType.police);

		Set<BuildingType> searchTypes = new HashSet<>(Arrays.asList(BuildingType.coalPower, BuildingType.police));
		Assert.assertEquals(3, map.getAllMapPosOfType(searchTypes).size());
	}

	@Test
	public void findPowerPlants() {
		map.buildTopLeft(MapPosition.at(0, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(4, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(0, 5), BuildingType.nukePower);
		map.buildTopLeft(MapPosition.at(5, 5), BuildingType.police);

		Assert.assertEquals(3, map.getAllPowerPlantMapPos().size());
	}

	@Test
	public void setTilesChangesOnlyOneTile() {
		Assert.assertEquals(0, map.getTileNr(MapPosition.at(2, 2)));
		map.setSpec(MapPosition.at(5, 5), Tiles.get(3));
		Assert.assertEquals(3, map.getTileNr(MapPosition.at(5, 5)));
		Assert.assertEquals(0, map.getTileNr(MapPosition.at(2, 2)));
	}

	@Test
	public void getAllMapPosOfAllBuildingTypes() {
		map.buildTopLeft(MapPosition.at(0, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(4, 0), BuildingType.coalPower);
		map.buildTopLeft(MapPosition.at(5, 0), BuildingType.nukePower);
		map.buildTopLeft(MapPosition.at(5, 5), BuildingType.police);
		Assert.assertThat(map.getAllMapPosOfAllBuildingTypes().keySet(),
				containsInAnyOrder(BuildingType.coalPower, BuildingType.nukePower, BuildingType.police));
		Assert.assertThat(map.getAllMapPosOfAllBuildingTypes(),
				hasEntry(BuildingType.coalPower, Arrays.asList(MapPosition.at(1, 1), MapPosition.at(5, 1))));
		Assert.assertThat(map.getAllMapPosOfAllBuildingTypes(),
				hasEntry(BuildingType.nukePower, Arrays.asList(MapPosition.at(6, 1))));
	}
	
	@Test
	public void getMapArea() {
		map.buildTopLeft(MapPosition.at(4, 0), BuildingType.coalPower);
		Assert.assertEquals(MapArea.of(MapPosition.at(4, 0), MapPosition.at(8,4)),map.getOccupiedArea(MapPosition.at(5, 1)));
		
	}

}