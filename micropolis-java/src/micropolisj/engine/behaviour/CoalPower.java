package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.POWERPLANT;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;

public class CoalPower extends BuildingBehaviour {

	public CoalPower(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incCoalCount();;
		if ((city.cityTime % 8) == 0) {
			repairZone(POWERPLANT, 4);
		}

		city.incCoalCount();
		city.addPowerPlant(MapPosition.at(xpos,ypos));
	}

}
