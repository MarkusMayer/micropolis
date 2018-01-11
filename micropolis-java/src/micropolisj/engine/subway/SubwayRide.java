package micropolisj.engine.subway;

import java.util.List;

public class SubwayRide {

	private final SubwayStation start,finish;
	private final List<SubwayConnection> route;
	
	public SubwayRide(SubwayStation start, SubwayStation finish, List<SubwayConnection> route) {
		this.start=start;
		this.finish=finish;
		this.route=route;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((finish == null) ? 0 : finish.hashCode());
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		SubwayRide other = (SubwayRide) obj;
		if (finish == null) {
			if (other.finish != null)
				return false;
		} else if (!finish.equals(other.finish))
			return false;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	public SubwayStation getStart() {
		return start;
	}

	public SubwayStation getFinish() {
		return finish;
	}

	public List<SubwayConnection> getRoute() {
		return route;
	}
}
