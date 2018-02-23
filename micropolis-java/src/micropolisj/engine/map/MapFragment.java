package micropolisj.engine.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import micropolisj.engine.TileSpec;

class MapFragment {

	private final List<TilePos> fragment;
	private final MapPosition dim, offset;
	
	public static MapFragment empty() {
		return new MapFragment(MapPosition.at(0, 0));
	}

	public MapFragment(MapPosition dim) {
		this(Objects.requireNonNull(dim), MapPosition.at(0, 0));
	}

	MapFragment(MapPosition dim, MapPosition offset) {
		this.dim = Objects.requireNonNull(dim);
		this.offset = Objects.requireNonNull(offset);
		fragment = new ArrayList<>();
	}

	static MapFragment rectOfSingleMapTile(MapPosition dim, TileSpec spec) {
		return rectOfSingleMapTile(Objects.requireNonNull(dim), MapPosition.at(0, 0), Objects.requireNonNull(spec));
	}

	static MapFragment rectOfSingleMapTile(MapPosition dim, MapPosition offset, TileSpec spec) {
		MapFragment frag = new MapFragment(Objects.requireNonNull(dim), Objects.requireNonNull(offset));
		for (MapPosition aPos : MapPosition.at(0, 0).getPosForRect(dim)) {
			frag.addTile(aPos, new MapTile(Objects.requireNonNull(spec)));
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

	MapBase<MapTile> transposeAndSetTo(MapBase<MapTile> map, MapPosition leftTop) {
		MapBase<MapTile> result = new MapBase<MapTile>(map);
		for (TilePos tilePos : fragment) {
			result.putAt(tilePos.transpose(leftTop.plus(offset)).getPos(), tilePos.getTile());
		}
		return result;
	}
}
