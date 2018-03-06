package micropolisj.engine.map;

import java.util.Objects;

public final class MapArea {

	private final MapPosition topLeft, lowerRight;

	public static MapArea of(MapPosition upperLeft, MapPosition lowerRight) {
		return new MapArea(Objects.requireNonNull(upperLeft), Objects.requireNonNull(lowerRight));
	}

	public static MapArea ofEmpty() {
		return new MapArea(MapPosition.at(0, 0), MapPosition.at(0, 0));
	}

	private MapArea(MapPosition upperLeft, MapPosition lowerRight) {
		this.topLeft = Objects.requireNonNull(upperLeft);
		this.lowerRight = Objects.requireNonNull(lowerRight);
	}

	boolean isInside(MapPosition pos) {
		return (pos.greaterOrEqualThan(topLeft) && pos.lessThan(lowerRight));
	}
	
	public MapPosition getTopLeft() {
		return topLeft;
	}

	public MapPosition getBottomRight() {
		return lowerRight;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lowerRight == null) ? 0 : lowerRight.hashCode());
		result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
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
		if (topLeft == null) {
			if (other.topLeft != null)
				return false;
		} else if (!topLeft.equals(other.topLeft))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapArea [upperLeft=" + topLeft + ", lowerRight=" + lowerRight + "]";
	}

}
