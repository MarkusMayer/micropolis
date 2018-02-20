package micropolisj.engine.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestMapArea {

	MapArea area;
	
	@Before
	public void setup() {
		area=new MapArea(MapPosition.at(0, 0), MapPosition.at(5, 5));
	}
	
	@Test
	public void testInside() {
		Assert.assertTrue(area.isInside(MapPosition.at(1,1)));
	}

	@Test
	public void testOutside() {
		Assert.assertFalse(area.isInside(MapPosition.at(6,6)));
	}
	
	public void testBorders() {
		Assert.assertTrue(area.isInside(MapPosition.at(0,0)));
		Assert.assertFalse(area.isInside(MapPosition.at(5,5)));		
	}
}
