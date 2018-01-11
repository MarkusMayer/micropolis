package micropolisj.engine;

public class MapPosition {
	private final int x,y;
	
	public MapPosition(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isSamePos(int x, int y) {
		return this.equals(new MapPosition(x, y));
	}
	
	public int getDistanceToPos(MapPosition pos) {
		return getDistanceToPos(pos.getX(), pos.getY());
	}
	
	public int getDistanceToPos(int x, int y) {
		return Math.abs(getX()-x)+Math.abs(getY()-y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapPosition other = (MapPosition) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	
}
