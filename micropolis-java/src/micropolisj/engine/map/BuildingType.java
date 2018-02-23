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
	police(500, Tiles.get(TileConstants.POLICESTATION),Arrays.asList()), 
	firestation(500,Tiles.get(TileConstants.FIRESTATION),Arrays.asList()), 
	stadium(5000, Tiles.get(TileConstants.STADIUM),Arrays.asList()), 
	coalPower(3000, Tiles.get(TileConstants.POWERPLANT),Arrays.asList()), 
	nukePower(5000, Tiles.get(TileConstants.NUCLEAR),Arrays.asList()), 
	subway(500, Tiles.get(TileConstants.SUBWAY),Arrays.asList()), 
	icerink(500, Tiles.get(TileConstants.ICERINK),Arrays.asList()), 
	residential(100, Tiles.get(TileConstants.RZB),Tiles.getAllResZones()), 
	commercial(100, Tiles.get(TileConstants.CZB),Tiles.getAllComZones()), 
	industrial(100, Tiles.get(TileConstants.IZB),Tiles.getAllIndZones()), 
	seaport(3000, Tiles.get(TileConstants.PORT),Arrays.asList()), 
	airport(10000, Tiles.get(TileConstants.AIRPORT),Arrays.asList());

	private int cost;
	private TileSpec base;
	private List<TileSpec>lookupSpecs;

	private BuildingType(int cost, TileSpec base,List<TileSpec> lookupSpecs) {
		this.cost = cost;
		this.base=Objects.requireNonNull(base);
		this.lookupSpecs=lookupSpecs;
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