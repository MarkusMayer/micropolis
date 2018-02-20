package micropolisj.engine.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestBuilding {
	
	Building building;
	
	@Before
	public void setup() {
		building=new Building(BuildingType.coalPower, MapPosition.at(1, 1));
	}

	@Test
	public void testIsInside() {
		Assert.assertTrue(building.isInside(MapPosition.at(0, 0)));
	}

	@Test
	public void testIsOutside() {
		Assert.assertFalse(building.isInside(MapPosition.at(5, 5)));
	}
	
}
