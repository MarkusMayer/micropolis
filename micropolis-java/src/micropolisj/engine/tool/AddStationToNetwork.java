package micropolisj.engine.tool;

import micropolisj.engine.Micropolis;
import micropolisj.engine.map.MapPosition;

public class AddStationToNetwork implements CityToolEffect{

	@Override
	public void applyEffect(Micropolis city, MapPosition pos) {
		//uahhh... add offset from top left corner to base of subway
		city.addSubwayStation(MapPosition.at(pos.getX()+1, pos.getY()));
		System.out.println("Network after addStation: "+city.getSubNet());
	}
	

}
