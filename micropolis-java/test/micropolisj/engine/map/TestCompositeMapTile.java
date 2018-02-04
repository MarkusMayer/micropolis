package micropolisj.engine.map;

import org.junit.Assert;
import org.junit.Test;

public class TestCompositeMapTile {

	@Test
	public void testCenterIntactNotBulldozable() {
		CityMap map=new CityMap(5, 5);
		MapPosition pos = MapPosition.at(0, 0);
		map.build(pos, Building.getFirestation());
		MapTile origTile=map.getTile(pos);
		map.bulldoze(pos);
		Assert.assertEquals(origTile,map.getTile(pos));
	}

}
