package micropolisj.engine.map;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestMapFragment {

	@Test
	public void testAddTile() {
		MapFragment frag=new MapFragment(MapPosition.at(3, 3));
		
		frag.addTile(MapPosition.at(0, 0), new SingleMapTile(null, MapPosition.at(0, 0), null));
		Assert.assertEquals(1, frag.getNrOfTiles());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOutOfBounds() {
		MapFragment frag=new MapFragment(MapPosition.at(3, 3));
		
		frag.addTile(MapPosition.at(5, 5), new SingleMapTile(null, MapPosition.at(0, 0), null));
	}
	
	@Test
	public void testTransposeAndSetTo() {
		MapFragment frag=new MapFragment(MapPosition.at(3, 3));
		Map<MapPosition,MapTile> origMap=new HashMap<>();
		origMap.put(MapPosition.at(0, 0), new SingleMapTile(null, MapPosition.at(0, 0), null));
		origMap.put(MapPosition.at(1, 0), new SingleMapTile(null, MapPosition.at(0, 0), null));
		origMap.put(MapPosition.at(2, 0), new SingleMapTile(null, MapPosition.at(0, 0), null));
		
		frag.addTile(MapPosition.at(0, 0), new SingleMapTile(null, MapPosition.at(0, 0), null));
		Map<MapPosition,MapTile>newMap=frag.transposeAndSetTo(origMap, (MapPosition.at(3,0)));
		Assert.assertNotNull(newMap.get(MapPosition.at(3,0)));
	}
	
}
