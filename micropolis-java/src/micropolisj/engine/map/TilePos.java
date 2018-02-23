package micropolisj.engine.map;

import java.util.Objects;

class TilePos {

	private final MapPosition pos;
	private final MapTile tile;
	
	TilePos(MapPosition pos,MapTile tile) {
		this.pos=Objects.requireNonNull(pos);
		this.tile=Objects.requireNonNull(tile);
	}
	
	MapTile getTile() {
		return tile;
	}
	
	MapPosition getPos() {
		return pos;
	}
	
	TilePos transpose(MapPosition posToAdd) {
		return new TilePos(pos.plus(posToAdd), tile);
	}
}
