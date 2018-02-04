package micropolisj.engine.map;

class TilePos {

	private final MapPosition pos;
	private final MapTile tile;
	
	TilePos(MapPosition pos,MapTile tile) {
		this.pos=pos;
		this.tile=tile;
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
