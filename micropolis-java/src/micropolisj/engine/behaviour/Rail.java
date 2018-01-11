package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.RIVER;
import static micropolisj.engine.TileConstants.RUBBLE;
import static micropolisj.engine.TileConstants.isConductive;
import static micropolisj.engine.TileConstants.isOverWater;

import micropolisj.engine.Micropolis;

public class Rail extends TileBehavior {
	
	

	public Rail(Micropolis city) {
		super(city);
	}

	@Override
	public void apply() {
		city.incRailCounter();
		city.generateTrain(xpos, ypos);

		if (city.getRoadEffect() < 30) { // deteriorating rail
			if (PRNG.nextInt(512) == 0) {
				if (!isConductive(tile)) {
					if (city.getRoadEffect() < PRNG.nextInt(32)) {
						if (isOverWater(tile)) {
							city.setTile(xpos,ypos,RIVER);
						} else {
							city.setTile(xpos,ypos,(char)(RUBBLE + PRNG.nextInt(4)));
						}
					}
				}
			}
		}
	}

}
