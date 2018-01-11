package micropolisj.engine;

import micropolisj.engine.behaviour.BuildingBehaviour;

public class Subway extends BuildingBehaviour {

	public Subway(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incSubwayStationCount();
	}

}
