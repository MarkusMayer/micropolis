package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.FOOTBALLGAME1;
import static micropolisj.engine.TileConstants.FOOTBALLGAME2;
import static micropolisj.engine.TileConstants.FULLSTADIUM;
import static micropolisj.engine.TileConstants.STADIUM;

import micropolisj.engine.Micropolis;

public class StadiumEmpty extends BuildingBehaviour {

	public StadiumEmpty(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		city.incStadiumCount();
		if ((city.cityTime % 16) == 0) {
			repairZone(STADIUM, 4);
		}

		if (isPowered)
		{
			if (((city.cityTime + xpos + ypos) % 32) == 0) {
				drawStadium(FULLSTADIUM);
				city.setTile(xpos+1,ypos, (char)(FOOTBALLGAME1));
				city.setTile(xpos+1,ypos+1,(char)(FOOTBALLGAME2));
			}
		}
	}
	
}
