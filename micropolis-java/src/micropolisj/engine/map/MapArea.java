package micropolisj.engine.map;

final class MapArea {
	
	private final MapPosition upperLeft,lowerRight;
	
	MapArea(MapPosition upperLeft, MapPosition lowerRight) {
		this.upperLeft=upperLeft;
		this.lowerRight=lowerRight;
	}
	
	boolean isInside(MapPosition pos) {
		return (pos.greaterOrEqualThan(upperLeft) && pos.lessThan(lowerRight));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lowerRight == null) ? 0 : lowerRight.hashCode());
		result = prime * result + ((upperLeft == null) ? 0 : upperLeft.hashCode());
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
		MapArea other = (MapArea) obj;
		if (lowerRight == null) {
			if (other.lowerRight != null)
				return false;
		} else if (!lowerRight.equals(other.lowerRight))
			return false;
		if (upperLeft == null) {
			if (other.upperLeft != null)
				return false;
		} else if (!upperLeft.equals(other.upperLeft))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapArea [upperLeft=" + upperLeft + ", lowerRight=" + lowerRight + "]";
	}
	
	

}
