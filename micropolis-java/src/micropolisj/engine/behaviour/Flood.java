package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.*;

import micropolisj.engine.Micropolis;

public class Flood extends TileBehavior {

	public Flood(Micropolis city) {
		super(city);
	}

	/**
	 * Called when the current tile is a flooding tile.
	 */
	@Override
	public void apply() {

		final int[] DX = { 0, 1, 0, -1 };
		final int[] DY = { -1, 0, 1, 0 };

		if (city.isCurrentlyFlooded()) {
			for (int z = 0; z < 4; z++) {
				if (PRNG.nextInt(8) == 0) {
					int xx = xpos + DX[z];
					int yy = ypos + DY[z];
					if (city.testBounds(xx, yy)) {
						int t = city.getTile(xx, yy);
						if (isCombustible(t) || t == DIRT || (t >= WOODS5 && t < FLOOD)) {
							if (isZoneCenter(t)) {
								city.killZone(xx, yy, t);
							}
							city.setTile(xx, yy, (char) (FLOOD + PRNG.nextInt(3)));
						}
					}
				}
			}
		} else {
			if (PRNG.nextInt(16) == 0) {
				city.setTile(xpos, ypos, DIRT);
			}
		}
	}

}
