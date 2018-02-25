package micropolisj.engine.subway;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class SubwayConnection {

	private static final int SUB_CONNECTION_MAINTENANCE_STEP = 2;

	private final SubwayStation station1, station2;

	public SubwayConnection(SubwayStation station1, SubwayStation station2) {
		if (station1.equals(station2))
			throw new IllegalArgumentException(
					"Cannot connect station to itself. Station1: " + station1 + ", Station2: " + station2);
		if (station1.getX() < station2.getX()) {
			this.station1 = station1;
			this.station2 = station2;
		} else if (station1.getX() == station2.getX()) {
			if (station1.getY() < station2.getY()) {
				this.station1 = station1;
				this.station2 = station2;
			} else {
				this.station1 = station2;
				this.station2 = station1;

			}
		} else {
			this.station1 = station2;
			this.station2 = station1;
		}

	}

	public SubwayStation getStation1() {
		return station1;
	}

	public SubwayStation getStation2() {
		return station2;
	}

	public boolean doesConnect(SubwayStation aStation1, SubwayStation aStation2) {
		return ((aStation1.equals(station1) || aStation1.equals(station2))
				&& (aStation2.equals(station1) || aStation2.equals(station2)));
	}

	public Optional<SubwayStation> connectsTo(SubwayStation station) {
		if (station1.equals(station))
			return Optional.of(station2);
		if (station2.equals(station))
			return Optional.of(station1);
		return Optional.empty();
	}

	public int getAnnualMaintenance() {
		return getLength() * SUB_CONNECTION_MAINTENANCE_STEP;
	}

	public int getLength() {
		return station1.getPos().getDistanceToPos(station2.getPos());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((station1 == null) ? 0 : station1.hashCode());
		result = prime * result + ((station2 == null) ? 0 : station2.hashCode());
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
		SubwayConnection other = (SubwayConnection) obj;
		if (station1 == null) {
			if (other.station1 != null)
				return false;
		} else if (!station1.equals(other.station1))
			return false;
		if (station2 == null) {
			if (other.station2 != null)
				return false;
		} else if (!station2.equals(other.station2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return station1 + " ==> " + station2 + " ( " + getAnnualMaintenance() + "$ )";
	}

	public static List<SubwayConnection> asConnectionList(Stack<SubwayStation> curRide) {
		Stack<SubwayStation> workingCopy=new Stack<SubwayStation>();
		workingCopy.addAll(curRide);
		if (curRide.size() < 2)
			throw new IllegalArgumentException();
		List<SubwayConnection> result = new ArrayList<SubwayConnection>();
		SubwayStation station1=workingCopy.pop();
		while(!workingCopy.isEmpty()) {
			SubwayStation station2=workingCopy.pop();
			result.add(new SubwayConnection(station1, station2));
			station1=station2;
		}
		return result;
	}

}
