package micropolisj.engine.map;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public class PoliceDepartment extends Building {

	public static PoliceDepartment getPolice(CityMap map) {
		return new PoliceDepartment(100, Tiles.get(TileConstants.POLICESTATION), map);
	}

	private PoliceDepartment(int cost, TileSpec base, CityMap map) {
		super(cost, base, map);
	}

}
