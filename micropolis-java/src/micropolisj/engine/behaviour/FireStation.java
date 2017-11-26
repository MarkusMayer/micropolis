package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.FIRESTATION;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TrafficGen;

public class FireStation extends BuildingBehaviour {

	public FireStation(Micropolis city) {
		super(city);
	}
	
	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incFireStationCount();
		if ((city.cityTime % 8) == 0) {
			repairZone(FIRESTATION, 3);
		}

		int z;
		if (isPowered) {
			z = city.getFireEffect();  //if powered, get effect
		} else {
			z = city.getFireEffect()/2; // from the funding ratio
		}

		if (!new TrafficGen(city, xpos, ypos, null).findPerimeterRoad()) {
			z /= 2;
		}

		city.addFireStationMapValue(xpos/8, ypos/8, z);		
	}


	
	

}
