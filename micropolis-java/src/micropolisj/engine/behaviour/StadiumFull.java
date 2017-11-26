package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.STADIUM;

import micropolisj.engine.Micropolis;

public class StadiumFull extends BuildingBehaviour {

	public StadiumFull(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incStadiumCount();
		if (((city.cityTime + xpos + ypos) % 8) == 0) {
			drawStadium(STADIUM);
		}
	}

	@Override
	boolean isRepairable() {
		return false;
	}

}
