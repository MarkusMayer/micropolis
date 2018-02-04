package micropolisj.engine.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MapFragment {

	private final List<TilePos> fragment;
	private final MapPosition dim, offset;

	public MapFragment(MapPosition dim) {
		this(dim, MapPosition.at(0, 0));
	}

	MapFragment(MapPosition dim, MapPosition offset) {
		this.dim = dim;
		this.offset = offset;
		fragment = new ArrayList<>();
	}

	static MapFragment rectOf(MapPosition dim, MapTile tile) {
		return rectOf(dim, MapPosition.at(0, 0), tile);
	}

	static MapFragment rectOf(MapPosition dim, MapPosition offset, MapTile tile) {
		MapFragment frag = new MapFragment(dim, offset);
		for (MapPosition aPos : MapPosition.at(0, 0).getPosForRect(dim)) {
			frag.addTile(aPos, tile);
		}

		return frag;
	}

	void addTile(MapPosition pos, MapTile tile) {
		if (pos.lessThan(dim)) {
			fragment.add(new TilePos(pos, tile));
		} else
			throw new IllegalArgumentException("Position outside fragment bounds. Pos: " + pos + " ,Bounds: " + dim);
	}

	int getNrOfTiles() {
		return fragment.size();
	}

	MapPosition getDim() {
		return dim;
	}

	MapPosition getOffset() {
		return offset;
	}

	Map<MapPosition, MapTile> transposeAndSetTo(Map<MapPosition, MapTile> map, MapPosition leftTop) {
		Map<MapPosition, MapTile> result = new HashMap<>(map);
		for (TilePos tilePos : fragment) {
			result.put(tilePos.transpose(leftTop.plus(offset)).getPos(), tilePos.getTile());
		}
		return result;
	}
}
