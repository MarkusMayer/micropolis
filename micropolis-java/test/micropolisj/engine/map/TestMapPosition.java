package micropolisj.engine.map;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class TestMapPosition {
	
	@Test(expected=IllegalArgumentException.class)
	public void testPosListSwappedSides() {
		MapPosition.at(2, 1).getPosForRect(MapPosition.at(0, 0));
	}

	@Test
	public void testPosList() {
		List<MapPosition> pos=MapPosition.at(1, 1).getPosForRect(MapPosition.at(3, 2));
		Assert.assertEquals(2,pos.size());
		Assert.assertTrue(pos.contains(MapPosition.at(1, 1)));
		Assert.assertTrue(pos.contains(MapPosition.at(2, 1)));
	}
	
	@Test
	public void testPosPlus() {
		Assert.assertEquals(MapPosition.at(3, 3), MapPosition.at(1, 1).plus(2, 2));
	}
	
	@Test
	public void testLessThanOk() {
		Assert.assertTrue(MapPosition.at(1, 1).lessThan(MapPosition.at(2, 2)));
	}

	@Test
	public void testLessThanOutside() {
		Assert.assertFalse(MapPosition.at(1, 1).lessThan(MapPosition.at(1, 1)));
	}
	
	@Test
	public void testLessXDimOutside() {
		Assert.assertFalse(MapPosition.at(1, 1).lessThan(MapPosition.at(1, 2)));
	}

	@Test
	public void testLessYDimOutside() {
		Assert.assertFalse(MapPosition.at(1, 1).lessThan(MapPosition.at(2, 1)));
	}

}
