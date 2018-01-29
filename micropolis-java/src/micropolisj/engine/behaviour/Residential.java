package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.CHURCH;
import static micropolisj.engine.TileConstants.DIRT;
import static micropolisj.engine.TileConstants.HHTHR;
import static micropolisj.engine.TileConstants.HOSPITAL;
import static micropolisj.engine.TileConstants.HOUSE;
import static micropolisj.engine.TileConstants.LHTHR;
import static micropolisj.engine.TileConstants.LOMASK;
import static micropolisj.engine.TileConstants.RESCLR;
import static micropolisj.engine.TileConstants.RZB;
import static micropolisj.engine.TileConstants.isRail;
import static micropolisj.engine.TileConstants.isResidentialClear;
import static micropolisj.engine.TileConstants.isRoadAny;
import static micropolisj.engine.TileConstants.residentialZonePop;

import java.util.List;
import java.util.Set;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TileConstants;
import micropolisj.engine.TypeDemand;
import micropolisj.engine.ZoneType;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.subway.SubwayStation;

public class Residential extends BuildingBehaviour {

	public Residential(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incResZoneCount();

		int tpop; // population of this zone
		if (tile == RESCLR) {
			tpop = city.doFreePop(xpos, ypos);
		} else {
			tpop = residentialZonePop(tile);
		}

		city.setResPop(city.getResPop() + tpop);

		int trafficGood = 0;

		trafficGood = city.getSubNet().checkRide(trafficGood,new MapPosition(xpos, ypos));
			

		if (trafficGood == 0) {
			if (tpop > PRNG.nextInt(36)) {
				trafficGood = makeTraffic(ZoneType.RESIDENTIAL);
			} else {
				trafficGood = 1;
			}
		}

		if (trafficGood == -1) {
			int value = getCRValue();
			doResidentialOut(tpop, value);
			return;
		}

		if (tile == RESCLR || PRNG.nextInt(8) == 0) {
			int locValve = evalResidential(trafficGood);
			int zscore = city.getResValve() + locValve;

			if (!isPowered)
				zscore = -500;

			if (zscore > -350 && zscore - 26380 > (PRNG.nextInt(0x10000) - 0x8000)) {
				if (tpop == 0 && PRNG.nextInt(4) == 0) {
					makeHospitalOrChurch();
					return;
				}

				int value = getCRValue();
				doResidentialIn(tpop, value);
				return;
			}

			if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000) - 0x8000)) {
				int value = getCRValue();
				doResidentialOut(tpop, value);
			}
		}
	}

	/**
	 * Place hospital or church if needed.
	 */
	void makeHospitalOrChurch() {
		if (city.getNeedHospital() == TypeDemand.increase) {
			zonePlop(HOSPITAL);
			city.resetHospitalDemand();
		}

		// FIXME- should be 'else if'
		if (city.getNeedChurch() == TypeDemand.increase) {
			zonePlop(CHURCH);
			city.resetChurchDemand();
		}
	}

	private void doResidentialIn(int pop, int value) {
		assert value >= 0 && value <= 3;

		int z = city.pollutionMem[ypos / 2][xpos / 2];
		if (z > 128)
			return;

		if (tile == RESCLR) {
			if (pop < 8) {
				buildHouse(value);
				adjustROG(1);
				return;
			}

			if (city.getPopulationDensity(xpos, ypos) > 64) {
				residentialPlop(0, value);
				adjustROG(8);
				return;
			}
			return;
		}

		if (pop < 40) {
			residentialPlop(pop / 8 - 1, value);
			adjustROG(8);
		}
	}

	void residentialPlop(int density, int value) {
		int base = (value * 4 + density) * 9 + RZB;
		zonePlop(base);
	}

	/**
	 * Build a single-lot house on the current residential zone.
	 */
	private void buildHouse(int value) {
		assert value >= 0 && value <= 3;

		final int[] ZeX = { 0, -1, 0, 1, -1, 1, -1, 0, 1 };
		final int[] ZeY = { 0, -1, -1, -1, 0, 0, 1, 1, 1 };

		int bestLoc = 0;
		int hscore = 0;

		for (int z = 1; z < 9; z++) {
			int xx = xpos + ZeX[z];
			int yy = ypos + ZeY[z];

			if (city.testBounds(xx, yy)) {
				int score = evalLot(xx, yy);

				if (score != 0) {
					if (score > hscore) {
						hscore = score;
						bestLoc = z;
					}

					if ((score == hscore) && PRNG.nextInt(8) == 0) {
						bestLoc = z;
					}
				}
			}
		}

		if (bestLoc != 0) {
			int xx = xpos + ZeX[bestLoc];
			int yy = ypos + ZeY[bestLoc];
			int houseNumber = value * 3 + PRNG.nextInt(3);
			assert houseNumber >= 0 && houseNumber < 12;

			assert city.testBounds(xx, yy);
			city.setTile(xx, yy, (char) (HOUSE + houseNumber));
		}
	}

	/**
	 * Consider the value of building a single-lot house at certain coordinates.
	 * 
	 * @return integer; positive number indicates good place for house to go; zero
	 *         or a negative number indicates a bad place.
	 */
	int evalLot(int x, int y) {
		// test for clear lot
		int aTile = city.getTile(x, y);
		if (aTile != DIRT && !isResidentialClear(aTile)) {
			return -1;
		}

		int score = 1;

		final int[] DX = { 0, 1, 0, -1 };
		final int[] DY = { -1, 0, 1, 0 };
		for (int z = 0; z < 4; z++) {
			int xx = x + DX[z];
			int yy = y + DY[z];

			// look for road
			if (city.testBounds(xx, yy)) {
				int tmp = city.getTile(xx, yy);
				if (isRoadAny(tmp) || isRail(tmp)) {
					score++;
				}
			}
		}

		return score;
	}

	private void doResidentialOut(int pop, int value) {
		assert value >= 0 && value < 4;

		final char[] Brdr = { 0, 3, 6, 1, 4, 7, 2, 5, 8 };

		if (pop == 0)
			return;

		if (pop > 16) {
			// downgrade to a lower-density full-size residential zone
			residentialPlop((pop - 24) / 8, value);
			adjustROG(-8);
			return;
		}

		if (pop == 16) {
			// downgrade from full-size zone to 8 little houses

			boolean pwr = city.isTilePowered(xpos, ypos);
			city.setTile(xpos, ypos, RESCLR);
			city.setTilePower(xpos, ypos, pwr);

			for (int x = xpos - 1; x <= xpos + 1; x++) {
				for (int y = ypos - 1; y <= ypos + 1; y++) {
					if (city.testBounds(x, y)) {
						if (!(x == xpos && y == ypos)) {
							// pick a random small house
							int houseNumber = value * 3 + PRNG.nextInt(3);
							city.setTile(x, y, (char) (HOUSE + houseNumber));
						}
					}
				}
			}

			adjustROG(-8);
			return;
		}

		if (pop < 16) {
			// remove one little house
			adjustROG(-1);
			int z = 0;

			for (int x = xpos - 1; x <= xpos + 1; x++) {
				for (int y = ypos - 1; y <= ypos + 1; y++) {
					if (city.testBounds(x, y)) {
						int loc = city.getMap()[y][x] & LOMASK;
						if (loc >= LHTHR && loc <= HHTHR) { // little house
							city.setTile(x, y, (char) (Brdr[z] + RESCLR - 4));
							return;
						}
					}
					z++;
				}
			}
		}
	}

	/**
	 * Evaluates the zone value of the current residential zone location.
	 * 
	 * @return an integer between -3000 and 3000. The higher the number, the more
	 *         likely the zone is to GROW; the lower the number, the more likely the
	 *         zone is to SHRINK.
	 */
	int evalResidential(int traf) {
		if (traf < 0)
			return -3000;

		int value = city.getLandValue(xpos, ypos);
		value -= city.pollutionMem[ypos / 2][xpos / 2];

		if (value < 0)
			value = 0; // cap at 0
		else
			value *= 32;

		if (value > 6000)
			value = 6000; // cap at 6000

		return value - 3000;
	}

	@Override
	boolean isRepairable() {
		return false;
	}

}
