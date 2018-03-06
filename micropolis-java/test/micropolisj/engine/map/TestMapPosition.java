package micropolisj.engine.map;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestMapPosition {

	@Test(expected = IllegalArgumentException.class)
	public void testPosListSwappedSides() {
		MapPosition.at(2, 1).getPosForRect(MapPosition.at(0, 0));
	}

	@Test
	public void testPosList() {
		List<MapPosition> pos = MapPosition.at(1, 1).getPosForRect(MapPosition.at(3, 2));
		Assert.assertEquals(2, pos.size());
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

	@Test
	public void testGreaterOrEqualForEqual() {
		Assert.assertTrue(MapPosition.at(1, 1).greaterOrEqualThan(MapPosition.at(1, 1)));
	}

	@Test
	public void testGreaterOrEqualForGreater() {
		Assert.assertTrue(MapPosition.at(2, 2).greaterOrEqualThan(MapPosition.at(1, 1)));
	}

	@Test
	public void testGreaterOrEqualForSmaller() {
		Assert.assertFalse(MapPosition.at(0, 2).greaterOrEqualThan(MapPosition.at(1, 1)));
	}

	@Test
	public void testStepDir() {
		Assert.assertEquals(MapPosition.at(0, 0), MapPosition.at(1, 1).step(StepDir.upleft));
	}

	@Test
	public void testDistanceTo() {
		Assert.assertEquals(0, MapPosition.at(0, 0).getDistanceToPos(MapPosition.at(0, 0)));
		Assert.assertEquals(2, MapPosition.at(0, 0).getDistanceToPos(MapPosition.at(2, 0)));
		Assert.assertEquals(5, MapPosition.at(0, 0).getDistanceToPos(MapPosition.at(2, 3)));
		Assert.assertEquals(4, MapPosition.at(3, 3).getDistanceToPos(MapPosition.at(1, 1)));
	}

	@Test
	public void testGetDistanceToArea() {
		final MapArea area22_44 = MapArea.of(MapPosition.at(2, 2), MapPosition.at(4,4));
		Assert.assertEquals(4,
				MapPosition.at(0, 0).getDistanceToArea(area22_44));
		Assert.assertEquals(8,
				MapPosition.at(8, 8).getDistanceToArea(area22_44));
		Assert.assertEquals(2,
				MapPosition.at(3, 0).getDistanceToArea(area22_44));
		Assert.assertEquals(2,
				MapPosition.at(0, 3).getDistanceToArea(area22_44));
		Assert.assertEquals(0, MapPosition.at(2, 2).getDistanceToArea(MapArea.of(MapPosition.at(1, 1), MapPosition.at(3, 3))));
		Assert.assertEquals(-1, MapPosition.at(0, 0).getDistanceToArea(MapArea.ofEmpty()));
	}
}
