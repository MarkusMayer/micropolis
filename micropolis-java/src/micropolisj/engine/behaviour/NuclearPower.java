package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.NUCLEAR;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;

public class NuclearPower extends BuildingBehaviour {

	
	
	public NuclearPower(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		if (!city.noDisasters && PRNG.nextInt(city.getGameLevel().getMeltdownRand()+1) == 0) {
			city.doMeltdown(xpos, ypos);
			return;
		}

		city.incNuclearCount();
		if ((city.cityTime % 8) == 0) {
			repairZone(NUCLEAR, 4);
		}

		city.incNuclearCount();
		city.addPowerPlant(MapPosition.at(xpos, ypos));
	}

}
