package micropolisj.engine.tool;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;

public interface CityToolEffect {

	void applyEffect(Micropolis city,MapPosition pos);
}
