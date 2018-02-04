package micropolisj.engine.map;

import org.junit.Assert;
import org.junit.Test;

public class TestTilePos {
	
	@Test
	public void testTranspose() {
		TilePos tp=new TilePos(MapPosition.at(2, 2), SingleMapTile.getRubble());
		Assert.assertEquals(MapPosition.at(4, 4),tp.transpose(MapPosition.at(2, 2)).getPos());
	}

}
