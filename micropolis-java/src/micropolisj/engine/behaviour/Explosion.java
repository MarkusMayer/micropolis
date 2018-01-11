package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.RUBBLE;

import micropolisj.engine.Micropolis;

public class Explosion extends TileBehavior {

	public Explosion(Micropolis city) {
		super(city);
	}

	@Override
	public void apply() {
		// clear AniRubble
		city.setTile(xpos, ypos, (char) (RUBBLE + PRNG.nextInt(4)));
	}

}
