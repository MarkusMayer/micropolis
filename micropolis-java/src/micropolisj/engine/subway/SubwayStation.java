package micropolisj.engine.subway;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import micropolisj.engine.map.MapPosition;

public class SubwayStation {

	private final MapPosition pos;

	public SubwayStation(MapPosition pos) {
		super();
		this.pos = pos;
	}

	public SubwayStation(int x, int y) {
		this(new MapPosition(x, y));
	}

	public int getX() {
		return pos.getX();
	}

	public int getY() {
		return pos.getY();
	}

	public MapPosition getPos() {
		return pos;
	}

	public Set<SubwayStation> getReachableStationsWithOneStop(List<SubwayConnection> conns) {
		Set<SubwayRide> rides = getPossibleRides(conns);
		return rides.stream().map(aRide -> aRide.getFinish()).collect(Collectors.toSet());
	}

	public Set<SubwayRide> getPossibleRides(List<SubwayConnection> conns) {
		return lookForRides(new Stack<SubwayStation>(), new HashSet<SubwayStation>(), conns);
	}

	private Set<SubwayRide> lookForRides(Stack<SubwayStation> curRide, Set<SubwayStation> visited,
			List<SubwayConnection> conns) {
		Set<SubwayRide> rides = new HashSet<>();
		Set<SubwayStation> candidates = this.getConnectedStations(conns);
		candidates.removeAll(visited);

		curRide.push(this);
		visited.add(this);
		for (SubwayStation aCandidate : candidates) {
			rides.addAll(aCandidate.lookForRides(curRide, visited, conns));
		}
		if (curRide.size() >= 2)
			rides.add(new SubwayRide(curRide.firstElement(), this, SubwayConnection.asConnectionList(curRide)));
		curRide.pop();
		return rides;
	}

	public Set<SubwayStation> getConnectedStations(List<SubwayConnection> conns) {
		Set<SubwayStation> res = new HashSet<>();

		for (SubwayConnection aConn : conns) {
			aConn.connectsTo(this).map(target -> res.add(target));
		}

		return res;
	}

	public String toString() {
		return "Station " + pos.getX() + "/" + pos.getY();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
		SubwayStation other = (SubwayStation) obj;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}

}
