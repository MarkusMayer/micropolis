package micropolisj.engine.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import micropolisj.engine.TileConstants;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;

public enum BuildingType {
	police(500, Tiles.get(TileConstants.POLICESTATION),Arrays.asList(),2), 
	firestation(500,Tiles.get(TileConstants.FIRESTATION),Arrays.asList(),2), 
	stadium(5000, Tiles.get(TileConstants.STADIUM),Arrays.asList(),8), 
	coalPower(3000, Tiles.get(TileConstants.POWERPLANT),Arrays.asList(),1), 
	nukePower(5000, Tiles.get(TileConstants.NUCLEAR),Arrays.asList(),1), 
	subway(500, Tiles.get(TileConstants.SUBWAY),Arrays.asList(),0), 
	icerink(500, Tiles.get(TileConstants.ICERINK),Arrays.asList(),8), 
	residential(100, Tiles.get(TileConstants.RZB),Tiles.getAllResZones(),2), 
	commercial(100, Tiles.get(TileConstants.CZB),Tiles.getAllComZones(),2), 
	industrial(100, Tiles.get(TileConstants.IZB),Tiles.getAllIndZones(),2), 
	seaport(3000, Tiles.get(TileConstants.PORT),Arrays.asList(),5), 
	airport(10000, Tiles.get(TileConstants.AIRPORT),Arrays.asList(),20);

	private int cost;
	private TileSpec base;
	private List<TileSpec>lookupSpecs;
	private int subwayWeight;

	private BuildingType(int cost, TileSpec base,List<TileSpec> lookupSpecs, int subwayWeight) {
		this.cost = cost;
		this.base=Objects.requireNonNull(base);
		this.lookupSpecs=lookupSpecs;
		this.subwayWeight=subwayWeight;
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
	
	public int getSubwayWeight() {
		return subwayWeight;
	}

	public static Set<BuildingType> getPowerPlantTypes() {
		return Collections.unmodifiableSet(new HashSet<BuildingType>(Arrays.asList(coalPower, nukePower)));
	}
	
	public static Optional<BuildingType> getTypeFromSpec(TileSpec spec) {
		for (BuildingType aType : BuildingType.values()) {
			if (aType.getBase().getTileNr()==spec.getTileNr() || aType.hasLookup(spec))
				return Optional.of(aType);
		}
		return Optional.empty();
	}
	
	private boolean hasLookup(TileSpec spec) {
		for (TileSpec lookuppec: lookupSpecs) {
			if (lookuppec.getTileNr()==spec.getTileNr() )
				return true;
		}
		return false;
	}
}