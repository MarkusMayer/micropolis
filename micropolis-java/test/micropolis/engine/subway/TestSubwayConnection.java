package micropolis.engine.subway;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import micropolisj.engine.map.MapPosition;
import micropolisj.engine.subway.SubwayConnection;
import micropolisj.engine.subway.SubwayRide;
import micropolisj.engine.subway.SubwayStation;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class TestSubwayConnection {
	SubwayStation station1,station2,station3;
	SubwayConnection con;
	
	@Before
	public void setup() {
		station1=new SubwayStation(MapPosition.at(1, 1));
		station2 = new SubwayStation(MapPosition.at(2, 2));
		station3 = new SubwayStation(MapPosition.at(3, 3));
		con = new SubwayConnection(station1,
				station2);
	}
	
	@Test
	public void testDoesConnect() {
		Assert.assertTrue(con.doesConnect(station1, station2));
		Assert.assertTrue(con.doesConnect(station2, station1));
		Assert.assertFalse(con.doesConnect(station1, station3));
	}

	@Test
	public void testConnectTo() {
		Assert.assertEquals(Optional.of(station2),con.connectsTo(station1));
		Assert.assertEquals(Optional.of(station1), con.connectsTo(station2));
		Assert.assertEquals(Optional.empty(),con.connectsTo(station3));
	}
	
	@Test
	public void testAsConnectionList() {
		Stack<SubwayStation> curRide=new Stack<>();
		curRide.push(station1);
		curRide.push(station2);
		curRide.push(station3);
		Assert.assertThat(SubwayConnection.asConnectionList(curRide),
				containsInAnyOrder(
						new SubwayConnection(station1, station2),
						new SubwayConnection(station2, station3)));
	}
}
