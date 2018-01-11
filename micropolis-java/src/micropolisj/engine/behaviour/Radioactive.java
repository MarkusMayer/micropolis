package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.DIRT;

import micropolisj.engine.Micropolis;

public class Radioactive extends TileBehavior {

	public Radioactive(Micropolis city) {
		super(city);
	}

	@Override
	public void apply() {
		if (PRNG.nextInt(4096) == 0)
		{
			// radioactive decay
			city.setTile(xpos, ypos, DIRT);
		}
	}

}
