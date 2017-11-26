package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.INDCLR;
import static micropolisj.engine.TileConstants.IZB;
import static micropolisj.engine.TileConstants.industrialZonePop;

import micropolisj.engine.Micropolis;
import micropolisj.engine.ZoneType;

public class Industrial extends BuildingBehaviour {

	public Industrial(Micropolis city) {
		super(city);
	}

	/**
	 * Called when the current tile is the key tile of an
	 * industrial zone.
	 */
	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
			city.incIndZoneCount();

			int tpop = industrialZonePop(tile);
			city.setIndPop(city.getIndPop()+ tpop);

			int trafficGood;
			if (tpop > PRNG.nextInt(6))
			{
				trafficGood = makeTraffic(ZoneType.INDUSTRIAL);
			}
			else
			{
				trafficGood = 1;
			}

			if (trafficGood == -1)
			{
				doIndustrialOut(tpop, PRNG.nextInt(2));
				return;
			}

			if (PRNG.nextInt(8) == 0)
			{
				int locValve = evalIndustrial(trafficGood);
				int zscore = city.getIndValve() + locValve;

				if (!isPowered)
					zscore = -500;

				if (zscore > -350 &&
					zscore - 26380 > (PRNG.nextInt(0x10000)-0x8000))
				{
					int value = PRNG.nextInt(2);
					doIndustrialIn(tpop, value);
					return;
				}

				if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000)-0x8000))
				{
					int value = PRNG.nextInt(2);
					doIndustrialOut(tpop, value);
				}
			}
		}

	private void doIndustrialIn(int pop, int value)
	{
		if (pop < 4)
		{
			indPlop(pop, value);
			adjustROG(8);
		}
	}
	
	private void doIndustrialOut(int pop, int value)
	{
		if (pop > 1)
		{
			indPlop(pop-2, value);
			adjustROG(-8);
		}
		else if (pop == 1)
		{
			zonePlop(INDCLR);
			adjustROG(-8);
		}
	}
	
	void indPlop(int density, int value)
	{
		int base = (value * 4 + density) * 9 + IZB;
		zonePlop(base);
	}
	
	/**
	 * Evaluates the zone value of the current industrial zone location.
	 * @return an integer between -3000 and 3000.
	 * Same meaning as evalResidential.
	 */
	int evalIndustrial(int traf)
	{
		if (traf < 0)
			return -1000;
		else
			return 0;
	}

	@Override
	boolean isRepairable() {
		return false;
	}
	
	
}
