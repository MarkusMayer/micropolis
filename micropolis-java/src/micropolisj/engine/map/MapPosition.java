package micropolisj.engine.map;

import java.util.ArrayList;
import java.util.List;

public class MapPosition {
	private final int x, y;

	public MapPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static MapPosition at(int x, int y) {
		return new MapPosition(x, y);
	}

	public MapPosition step(StepDir dir) {
		return plus(dir.getPos());
	}

	public MapPosition plus(int xAdd, int yAdd) {
		return plus(MapPosition.at(xAdd, yAdd));
	}

	public MapPosition plus(MapPosition posToAdd) {
		return MapPosition.at(getX() + posToAdd.getX(), getY() + posToAdd.getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public List<MapPosition> getPosForRect(MapPosition rightBottom) {
		if (getX() >= rightBottom.getX() || getY() >= rightBottom.getY())
			throw new IllegalArgumentException(
					"leftTop is right and or below rightBottom: " + this + ", " + rightBottom);

		ArrayList<MapPosition> posList = new ArrayList<>();
		for (int x = getX(); x < rightBottom.getX(); x++) {
			for (int y = getY(); y < rightBottom.getY(); y++) {
				posList.add(new MapPosition(x, y));
			}
		}

		return posList;
	}

	public boolean isSamePos(int x, int y) {
		return this.equals(new MapPosition(x, y));
	}

	public int getDistanceToPos(MapPosition pos) {
		return getDistanceToPos(pos.getX(), pos.getY());
	}

	public int getDistanceToPos(int x, int y) {
		return Math.abs(getX() - x) + Math.abs(getY() - y);
	}

	public MapPosition reverse() {
		return MapPosition.at(getX() * -1, getY() * -1);
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

	@Override
	public String toString() {
		return "MapPosition [x=" + x + ", y=" + y + "]";
	}

	public boolean lessThan(MapPosition pos) {
		return (x < pos.getX() && y < pos.getY());
	}

	public boolean greaterOrEqualThan(MapPosition pos) {
		return (x >= pos.getX() && y >= pos.getY());
	}

	public int getDistanceToArea(MapArea targetArea) {
		if (targetArea.equals(MapArea.ofEmpty()))
			return -1;
		else if (targetArea.isInside(this))
			return 0;
		else {
			int xDist = 0;
			if (getX() < targetArea.getTopLeft().getX())
				xDist += targetArea.getTopLeft().getX() - getX();
			if (getX() > targetArea.getBottomRight().getX())
				xDist += getX() - targetArea.getBottomRight().getX();

			int yDist = 0;
			if (getY() < targetArea.getTopLeft().getY())
				yDist += targetArea.getTopLeft().getY() - getY();
			if (getY() > targetArea.getBottomRight().getY())
				yDist += getY() - targetArea.getBottomRight().getY();
			return xDist + yDist;
		}
	}
}
