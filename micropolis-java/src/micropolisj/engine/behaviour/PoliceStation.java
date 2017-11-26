package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.POLICESTATION;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TrafficGen;

public class PoliceStation extends BuildingBehaviour {

	public PoliceStation(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incPoliceCount();;
		if ((city.cityTime % 8) == 0) {
			repairZone(POLICESTATION, 3);
		}

		int z;
		if (isPowered) {
			z = city.getPoliceEffect();
		} else {
			z = city.getPoliceEffect() / 2;
		}

		if (!new TrafficGen(city, xpos, ypos, null).findPerimeterRoad()) {
			z /= 2;
		}

		city.addPoliceStationMapValue(xpos/8,ypos/8,z);
	}

}
