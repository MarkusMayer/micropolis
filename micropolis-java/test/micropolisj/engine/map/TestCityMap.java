package micropolisj.engine.map;

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
		map.build(MapPosition.at(1, 1), BuildingType.firestation);
		Assert.assertEquals(Tiles.get(TileConstants.FIRESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildOutsideCity() {
		// a
		map.build(MapPosition.at(11, 11), BuildingType.firestation);
	}

	@Test
	public void testGetSpecBuildingOtherTile() {
		map.build(MapPosition.at(1, 1), BuildingType.police);
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE + 1), map.getSpec(MapPosition.at(2, 1)));
	}

	@Test
	public void bulldozeCenter() {
		map.build(MapPosition.at(1, 1), BuildingType.firestation);
		map.bulldoze(MapPosition.at(2, 2));
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(2, 2)));
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(1, 2)));
	}

	@Test
	public void buildOverBuilding() {
		map.build(MapPosition.at(0, 0), BuildingType.police);
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE), map.getSpec(MapPosition.at(0, 0)));
		Assert.assertNotEquals(Tiles.get(TileConstants.POLICESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
		map.build(MapPosition.at(1, 1), BuildingType.firestation);
		Assert.assertNotEquals(Tiles.get(TileConstants.FIRESTATIONBASE), map.getSpec(MapPosition.at(1, 1)));
	}

	@Test
	public void findPosOfSingleType() {
		map.build(MapPosition.at(0, 0), BuildingType.coalPower);
		map.build(MapPosition.at(4, 0), BuildingType.coalPower);
		map.build(MapPosition.at(5, 0), BuildingType.nukePower);

		Assert.assertEquals(2, map.getAllMapPosOfType(BuildingType.coalPower).size());
	}

	@Test
	public void findPosOfTypeSet() {
		map.build(MapPosition.at(0, 0), BuildingType.coalPower);
		map.build(MapPosition.at(4, 0), BuildingType.coalPower);
		map.build(MapPosition.at(5, 0), BuildingType.nukePower);
		map.build(MapPosition.at(5, 5), BuildingType.police);

		Set<BuildingType> searchTypes = new HashSet<>(Arrays.asList(BuildingType.coalPower, BuildingType.police));
		Assert.assertEquals(3, map.getAllMapPosOfType(searchTypes).size());
	}

	@Test
	public void findPowerPlants() {
		map.build(MapPosition.at(0, 0), BuildingType.coalPower);
		map.build(MapPosition.at(4, 0), BuildingType.coalPower);
		map.build(MapPosition.at(0, 5), BuildingType.nukePower);
		map.build(MapPosition.at(5, 5), BuildingType.police);

		Assert.assertEquals(3, map.getAllPowerPlantMapPos().size());
	}

	@Test
	public void setTilesChangesOnlyOneTile() {
		Assert.assertEquals(0, map.getTileNr(MapPosition.at(2, 2)));
		map.setSpec(MapPosition.at(5, 5), Tiles.get(3));
		Assert.assertEquals(3, map.getTileNr(MapPosition.at(5, 5)));
		Assert.assertEquals(0, map.getTileNr(MapPosition.at(2, 2)));
	}

}