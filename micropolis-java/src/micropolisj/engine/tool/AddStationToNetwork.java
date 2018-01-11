package micropolisj.engine.tool;

import micropolisj.engine.MapPosition;
import micropolisj.engine.Micropolis;

public class AddStationToNetwork implements CityToolEffect{

	@Override
	public void applyEffect(Micropolis city, MapPosition pos) {
		//uahhh... add offset from top left corner to base of subway
		city.addSubwayStation(new MapPosition(pos.getX()+1, pos.getY()));
		System.out.println("Network after addStation: "+city.getSubNet());
	}
	

}
