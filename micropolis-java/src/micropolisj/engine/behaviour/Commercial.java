package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.COMCLR;
import static micropolisj.engine.TileConstants.CZB;
import static micropolisj.engine.TileConstants.commercialZonePop;

import micropolisj.engine.Micropolis;
import micropolisj.engine.ZoneType;

public class Commercial extends BuildingBehaviour {

	public Commercial(Micropolis city) {
		super(city);
	}

	/**
	 * Called when the current tile is the key tile of a commercial zone.
	 */
	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incComZoneCount();
		;

		int tpop = commercialZonePop(tile);
		city.setComPop(city.getComPop() + tpop);

		int trafficGood;
		if (tpop > PRNG.nextInt(6)) {
			trafficGood = makeTraffic(ZoneType.COMMERCIAL);
		} else {
			trafficGood = 1;
		}

		if (trafficGood == -1) {
			int value = getCRValue();
			doCommercialOut(tpop, value);
			return;
		}

		if (PRNG.nextInt(8) == 0) {
			int locValve = evalCommercial(trafficGood);
			int zscore = city.getComValve() + locValve;

			if (!isPowered)
				zscore = -500;

			if (trafficGood != 0 && zscore > -350 && zscore - 26380 > (PRNG.nextInt(0x10000) - 0x8000)) {
				int value = getCRValue();
				doCommercialIn(tpop, value);
				return;
			}

			if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000) - 0x8000)) {
				int value = getCRValue();
				doCommercialOut(tpop, value);
			}
		}
	}

	private void doCommercialIn(int pop, int value) {
		int z = city.getLandValue(xpos, ypos) / 32;
		if (pop > z)
			return;

		if (pop < 5) {
			comPlop(pop, value);
			adjustROG(8);
		}
	}

	private void doCommercialOut(int pop, int value) {
		if (pop > 1) {
			comPlop(pop - 2, value);
			adjustROG(-8);
		} else if (pop == 1) {
			zonePlop(COMCLR);
			adjustROG(-8);
		}
	}
	
	void comPlop(int density, int value)
	{
		int base = (value * 5 + density) * 9 + CZB;
		zonePlop(base);
	}
	
	/**
	 * Evaluates the zone value of the current commercial zone location.
	 * @return an integer between -3000 and 3000
	 * Same meaning as evalResidential.
	 */
	int evalCommercial(int traf)
	{
		if (traf < 0)
			return -3000;

		return city.getComRate()[ypos/8][xpos/8];
	}

	@Override
	boolean isRepairable() {
		return false;
	}
	
	

}
