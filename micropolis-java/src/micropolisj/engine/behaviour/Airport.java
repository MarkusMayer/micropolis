package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.AIRPORT;

import micropolisj.engine.Micropolis;

public class Airport extends BuildingBehaviour {

	public Airport(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incAirportCount();
		if ((city.cityTime % 8) == 0) {
			repairZone(AIRPORT, 6);
		}

		if (isPowered) {

			if (PRNG.nextInt(6) == 0) {
				city.generatePlane(xpos, ypos);
			}

			if (PRNG.nextInt(13) == 0) {
				city.generateCopter(xpos, ypos);
			}
		}
	}

}
