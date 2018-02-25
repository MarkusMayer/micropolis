package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.PORT;

import micropolisj.engine.Micropolis;

public class Seaport extends BuildingBehaviour {

	public Seaport(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incSeaportCount();
		if ((city.cityTime % 16) == 0) {
			repairZone(PORT, 4);
		}

		if (isPowered) {
			city.generateShip();
		}
	}

}
