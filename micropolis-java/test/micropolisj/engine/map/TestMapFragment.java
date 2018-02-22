package micropolisj.engine.map;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import micropolisj.engine.TileConstants;
import micropolisj.engine.Tiles;

public class TestMapFragment {

	@Test
	public void testAddTile() {
		MapFragment frag = new MapFragment(MapPosition.at(3, 3));

		frag.addTile(MapPosition.at(0, 0), MapTile.getRubble());
		Assert.assertEquals(1, frag.getNrOfTiles());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOutOfBounds() {
		MapFragment frag = new MapFragment(MapPosition.at(3, 3));

		frag.addTile(MapPosition.at(5, 5), MapTile.getRubble());
	}

	@Test
	public void testTransposeAndSetTo() {
		MapFragment frag = new MapFragment(MapPosition.at(3, 3));
		MapBase<MapTile> origMap = new MapBase<MapTile>(MapPosition.at(10, 10),
				() -> new MapTile(Tiles.get(TileConstants.DIRT)));
		origMap.putAt(MapPosition.at(0, 0), MapTile.getRubble());
		origMap.putAt(MapPosition.at(1, 0), MapTile.getRubble());
		origMap.putAt(MapPosition.at(2, 0), MapTile.getRubble());

		frag.addTile(MapPosition.at(0, 0), MapTile.getRubble());
		MapBase<MapTile> newMap = frag.transposeAndSetTo(origMap, (MapPosition.at(3, 0)));
		Assert.assertNotNull(newMap.getAt(MapPosition.at(3, 0)));
	}

}
