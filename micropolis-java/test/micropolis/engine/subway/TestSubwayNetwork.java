package micropolis.engine.subway;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.subway.SubwayConnection;
import micropolisj.engine.subway.SubwayNetwork;
import micropolisj.engine.subway.SubwayStation;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.util.Arrays;

public class TestSubwayNetwork {

	SubwayNetwork net;
	MapPosition pos1, pos2, pos3;
	private SubwayStation station1, station2, station3;

	@Before
	public void before() {
		net = new SubwayNetwork(new Micropolis(10, 10).getMap());
		pos1 = MapPosition.at(1, 1);
		pos2 = MapPosition.at(2, 2);
		pos3 = MapPosition.at(3, 3);
		station1 = new SubwayStation(pos1);
		station2 = new SubwayStation(pos2);
		station3 = new SubwayStation(pos3);
	}

	@Test
	public void testAddStation() {
		Assert.assertTrue(net.addStation(MapPosition.at(1, 1)));
		Assert.assertTrue(net.addStation(MapPosition.at(2, 2)));
		Assert.assertFalse(net.addStation(MapPosition.at(1, 1)));
		Assert.assertFalse(net.addStation(MapPosition.at(1000, 1000)));
	}

	@Test
	public void testConnect() {
		net.addStation(pos1);
		net.addStation(pos2);

		Assert.assertEquals(new SubwayConnection(station2, station1), net.connect(station1, station2));
		Assert.assertEquals(1, net.getConnections().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConnectToUnkownStation() {
		net.addStation(pos1);
		net.addStation(pos2);

		net.connect(station1, station3);
	}

	@Test
	public void testAssetValue() {
		net.addStation(pos1);
		net.addStation(pos2);
		net.connect(station1, station2);

		Assert.assertEquals(2200, net.getAssetValue());
	}

	@Test
	public void testMaintenanceCost() {
		net.addStation(pos1);
		net.addStation(pos2);
		net.connect(station1, station2);

		Assert.assertEquals(70, net.getMaintenanceCost());
	}

	@Test
	public void testGetStationsNearPos() {
		net.addStation(pos1);
		net.addStation(pos3);
		net.connect(station1, station3);
		
		Assert.assertThat(net.getStationsNearPos(pos2, 2),containsInAnyOrder(station1,station3));
		Assert.assertEquals(net.getStationsNearPos(MapPosition.at(4, 4), 1),Arrays.asList());
	}

}
