package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.FIRE;
import static micropolisj.engine.TileConstants.IZB;
import static micropolisj.engine.TileConstants.RUBBLE;
import static micropolisj.engine.TileConstants.isCombustible;
import static micropolisj.engine.TileConstants.isZoneCenter;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TileBehavior;

public class Fire extends TileBehavior {
	
	
	public Fire(Micropolis city) {
		super(city);
	}

	@Override
	public void apply() {
		city.reportActiveFire();

		// one in four times
		if (PRNG.nextInt(4) != 0) {
			return;
		}

		final int [] DX = { 0, 1, 0, -1 };
		final int [] DY = { -1, 0, 1, 0 };

		for (int dir = 0; dir < 4; dir++)
		{
			if (PRNG.nextInt(8) == 0)
			{
				int xtem = xpos + DX[dir];
				int ytem = ypos + DY[dir];
				if (!city.testBounds(xtem, ytem))
					continue;

				int c = city.getTile(xtem, ytem);
				if (isCombustible(c)) {
					if (isZoneCenter(c)) {
						city.killZone(xtem, ytem, c);
						if (c > IZB) { //explode
							city.makeExplosion(xtem, ytem);
						}
					}
					city.setTile(xtem, ytem, (char)(FIRE + PRNG.nextInt(4)));
				}
			}
		}

		int cov = city.getFireStationCoverage(xpos, ypos);
		int rate = cov > 100 ? 1 :
			cov > 20 ? 2 :
			cov != 0 ? 3 : 10;

		if (PRNG.nextInt(rate+1) == 0) {
			city.setTile(xpos, ypos, (char)(RUBBLE + PRNG.nextInt(4)));
		}
	}

}
