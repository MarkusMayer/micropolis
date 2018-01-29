package micropolisj.engine.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;
import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public class TestCityMap {
	
	CityMap map;
	Tiles t;
	
	@BeforeClass
	public static void setupSpecs() {
		
	}
	
	@Before
	public void buildMap() {
		map=new CityMap(5, 5);
	}
	
	@Test
	public void testGetSpecBuildingBase() {
		Assert.assertNotNull(map.getSpec(MapPosition.at(0, 0)));
		map.build(MapPosition.at(1, 1), PoliceDepartment.getPolice(map));
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE),map.getSpec(MapPosition.at(1, 1)));
	}
	
	@Test
	public void testGetSpecBuildingOtherTile() {
		map.build(MapPosition.at(1, 1), PoliceDepartment.getPolice(map));
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE+1),map.getSpec(MapPosition.at(2, 1)));		
	}
	
//	@Test
//	public void testPower() {
//		map.build(1, 1, PoliceDepartment.getPolice());
//		map.power(2, 1);
//		Assert.assertEquals(true, map.getPower(1, 1));
//		Assert.assertEquals(true, map.getPower(2, 1));
//	}

//	@Test
//	public void bulldozeSingleTile() {
//		map.build(1, 1, PoliceDepartment.getPolice());
//		Assert.assertEquals(false, map.isBulldozed(1, 2));
//		map.bulldoze(1, 2);
//		Assert.assertEquals(true, map.isBulldozed(1, 2));
//		Assert.assertEquals(false, map.isBulldozed(1, 1));
//	}
//	
//	@Test
//	public void isCenter() {
//		map.build(1, 1, PoliceDepartment.getPolice());
//		Assert.assertEquals(false, map.isBulldozed(1, 1));		
//		Assert.assertEquals(true, map.isBulldozed(1, 2));		
//	}
//	
	@Test
	public void bulldozeCenter() {
		map.build(MapPosition.at(1, 1), PoliceDepartment.getPolice(map));
		map.bulldoze(MapPosition.at(2, 2));
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(2, 2)));		
		Assert.assertEquals(Tiles.get(0), map.getSpec(MapPosition.at(1, 2)));		
	}
	
	@Test
	public void buildOverBuilding() {
		map.build(MapPosition.at(0, 0), PoliceDepartment.getPolice(map));
		Assert.assertEquals(Tiles.get(TileConstants.POLICESTATIONBASE),map.getSpec(MapPosition.at(0, 0)));
		Assert.assertNotEquals(Tiles.get(TileConstants.POLICESTATIONBASE),map.getSpec(MapPosition.at(1, 1)));
		map.build(MapPosition.at(1, 1), PoliceDepartment.getPolice(map));
		Assert.assertNotEquals(Tiles.get(TileConstants.POLICESTATIONBASE),map.getSpec(MapPosition.at(1, 1)));
	}
}
