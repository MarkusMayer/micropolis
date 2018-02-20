package micropolisj.engine.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public enum BuildingType {
	police(500, Tiles.get(TileConstants.POLICESTATION)), 
	firestation(500,Tiles.get(TileConstants.FIRESTATION)), 
	stadium(5000, Tiles.get(TileConstants.STADIUM)), 
	coalPower(3000, Tiles.get(TileConstants.POWERPLANT)), 
	nukePower(5000, Tiles.get(TileConstants.NUCLEAR)), 
	subway(500, Tiles.get(TileConstants.SUBWAY)), 
	icerink(500, Tiles.get(TileConstants.ICERINK)), 
	residential(100, Tiles.get(TileConstants.RZB)), 
	commercial(100, Tiles.get(TileConstants.CZB)), 
	industrial(100, Tiles.get(TileConstants.IZB)), 
	seaport(3000, Tiles.get(TileConstants.PORT)), 
	airport(10000, Tiles.get(TileConstants.AIRPORT));

	int cost;
	TileSpec base;

	private BuildingType(int cost, TileSpec base) {
		this.cost = cost;
		this.base=base;
	}

	public int getCost() {
		return cost;
	}
	
	public TileSpec getBase() {
		return base;
	}
	
	public int getTileNr() {
		return base.getTileNr();
	}

	public static Set<BuildingType> getPowerPlantTypes() {
		return Collections.unmodifiableSet(new HashSet<BuildingType>(Arrays.asList(coalPower, nukePower)));
	}
}