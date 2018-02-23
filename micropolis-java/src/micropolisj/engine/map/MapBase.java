package micropolisj.engine.map;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class MapBase<T> {

	private Map<MapPosition, T> map;
	MapPosition dim;

	public MapBase(MapBase<T> orig) {
		this.dim=orig.getDimension();
		map = new HashMap<>();
		for (MapPosition aPos : MapPosition.at(0, 0).getPosForRect(dim)) {
			map.put(aPos, orig.getAt(aPos));
		}
	}
	
	public MapBase(MapPosition dim, Supplier<T> initValueSupplier) {
		this.dim = Objects.requireNonNull(dim);
		map = new HashMap<>();
		for (MapPosition aPos : MapPosition.at(0, 0).getPosForRect(dim)) {
			map.put(aPos, initValueSupplier.get());
		}
	}
	
	public MapBase(MapPosition dim) {
		this.dim=Objects.requireNonNull(dim);
		map=new HashMap<>();
	}

	public MapPosition getDimension() {
		return dim;
	}

	public T getAt(MapPosition pos) {
		return map.get(pos);
	}

	public void putAt(MapPosition pos, T tile) {
		checkPosInside(Objects.requireNonNull(pos));
		map.put(pos, tile);
	}
	
	public boolean containsKey(MapPosition pos) {
		return map.containsKey(Objects.requireNonNull(pos));
	}
	
	public Set<MapPosition> keySet() {
		return map.keySet();
	}

	public Collection<T> values() {
		return map.values();
	}
	
	private void checkPosInside(MapPosition pos) {
		if (!isPosInside(Objects.requireNonNull(pos)))
			throw new IllegalArgumentException(
					"position outside city bounds. pos: " + pos + ", city-dimmension: " + dim);
	}

	public boolean isPosInside(MapPosition pos) {
		return (pos.greaterOrEqualThan(MapPosition.at(0, 0)) && pos.lessThan(dim));
	}

	@SuppressWarnings(value="unchecked")
	public <T> T[][] asArray(Class<T> componentType) {
	    T[][]result=(T[][]) Array.newInstance(Objects.requireNonNull(componentType), dim.getY(),dim.getX());
	    for (MapPosition aPos:MapPosition.at(0, 0).getPosForRect(dim)) {
	    	result[aPos.getY()][aPos.getX()]=(T) getAt(aPos);
	    }
	    return result;
	}
	
}
