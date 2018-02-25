package micropolis.engine.subway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import micropolisj.engine.map.MapPosition;
import micropolisj.engine.subway.SubwayConnection;
import micropolisj.engine.subway.SubwayRide;
import micropolisj.engine.subway.SubwayStation;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

//TODO Improve tests, exact list commpare
public class TestSubwayStation {

	private SubwayStation station1, station2, station3, station4, station5, station6, noConnectStation;
	private List<SubwayConnection> conns = new ArrayList<SubwayConnection>();

	@Before
	public void setup() {
		station1 = new SubwayStation(MapPosition.at(1, 1));
		station2 = new SubwayStation(MapPosition.at(2, 2));
		station3 = new SubwayStation(MapPosition.at(3, 3));
		station4 = new SubwayStation(MapPosition.at(4, 4));
		station5 = new SubwayStation(MapPosition.at(5, 5));
		station6 = new SubwayStation(MapPosition.at(6, 6));
		noConnectStation = new SubwayStation(MapPosition.at(12, 12));
		conns.add(new SubwayConnection(station1, station2));
		conns.add(new SubwayConnection(station2, station3));
		conns.add(new SubwayConnection(station1, station4));
		conns.add(new SubwayConnection(station1, station3));
		conns.add(new SubwayConnection(station2, station5));
		conns.add(new SubwayConnection(station5, station6));
	}

	@Test
	public void testGetConnectedStations() {
		Assert.assertThat(station1.getConnectedStations(conns), containsInAnyOrder(station2, station3, station4));
		Assert.assertEquals(0, noConnectStation.getConnectedStations(conns).size());
	}

	@Test
	public void testGetReachableStations() {
		Assert.assertThat(station1.getReachableStationsWithOneStop(conns),
				containsInAnyOrder(station2, station3, station4, station5, station6));
		Assert.assertEquals(0, noConnectStation.getReachableStationsWithOneStop(conns).size());
	}

	@Test
	public void testGetPossibleRides() {
		Assert.assertThat(station1.getPossibleRides(conns),
				containsInAnyOrder(
						new SubwayRide(station1, station2, Arrays.asList(new SubwayConnection(station1, station2))),
						new SubwayRide(station1, station4, Arrays.asList(new SubwayConnection(station1, station4))),
						new SubwayRide(station1, station3, Arrays.asList(new SubwayConnection(station1, station3))),
						new SubwayRide(station1, station3,
								Arrays.asList(new SubwayConnection(station2, station3),
										new SubwayConnection(station1, station2))),
						new SubwayRide(station1, station5,
								Arrays.asList(new SubwayConnection(station2, station5),
										new SubwayConnection(station1, station2))),
						new SubwayRide(station1, station6, Arrays.asList(new SubwayConnection(station5, station6),
								new SubwayConnection(station2, station5), new SubwayConnection(station1, station2)))

				));

	}
}
