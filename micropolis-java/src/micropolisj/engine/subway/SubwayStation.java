package micropolisj.engine.subway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import micropolisj.engine.MapPosition;

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
	
	public Set<SubwayStation> getReachableStations(List<SubwayConnection> conns){
		Set<SubwayStation> step1=this.getConnectedStations(conns);
		Set<SubwayStation> allReachableStations=new HashSet<>(step1);
		
		for (SubwayStation subwayStation : step1) {
			allReachableStations.addAll(subwayStation.getConnectedStations(conns));
		}
		
		allReachableStations.remove(this);
		
		return allReachableStations;
	}
	
	//TODO:Make recursive
	public Set<SubwayRide> getPossibleRides(List<SubwayConnection> conns) {
		Set<SubwayRide> rides=new HashSet<>(),step1=new HashSet<>();
		for (SubwayConnection aConn : conns) {
			SubwayStation target=aConn.connectsTo(this);
			if (target!=null) {
				step1.add(new SubwayRide(this, target, Arrays.asList(aConn)));
			}
		}
		rides.addAll(step1);
		for (SubwayRide subwayRide : step1) {
			for (SubwayConnection aConn : conns) {
				SubwayStation target=aConn.connectsTo(subwayRide.getFinish());
				if (target!=null && !target.equals(this)) {
					ArrayList<SubwayConnection> newConns=new ArrayList<>(subwayRide.getRoute());
					newConns.add(aConn);
 					step1.add(new SubwayRide(this, target, newConns));
				}
			}
		}
		
		return rides;
	}

	public Set<SubwayStation> getConnectedStations(List<SubwayConnection> conns){
		Set<SubwayStation> res=new HashSet<>();
		
		for (SubwayConnection aConn : conns) {
			SubwayStation target=aConn.connectsTo(this);
			if (target!=null)
				res.add(target);
		}
		
		return res;
	}

	public String toString() {
		return "Station "+pos.getX()+"/"+pos.getY();
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
