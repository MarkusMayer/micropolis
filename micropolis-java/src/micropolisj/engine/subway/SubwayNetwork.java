package micropolisj.engine.subway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import micropolisj.engine.TileConstants;
import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapArea;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.map.ReadOnlyCityMap;

public class SubwayNetwork {

	private static final int SUBNET_STATION_ASSETVALUE = 1000;
	private static final int SUBNET_CON_STEP_ASSETVALUE = 100;

	static final int SUB_STATION_MAINTENANCE = 30;
	static final int SUB_CON_STEP_MAINTENANCE = 5;

	private final ReadOnlyCityMap map;
	private Set<SubwayConnection> connections;
	private Set<SubwayStation> stations;
	private List<SubwayRide> rides;
	private int nrRides = 0, nrRequests = 0;

	public SubwayNetwork(ReadOnlyCityMap map) {
		this.map = Objects.requireNonNull(map);
		connections = new HashSet<>();
		stations = new HashSet<>();
		rides = new ArrayList<>();
	}

	// TODO: Remove station when bulldozed
	public boolean addStation(MapPosition pos) {
		return map.isPosInside(Objects.requireNonNull(pos)) && stations.add(new SubwayStation(pos));
	}

	public SubwayConnection connect(SubwayStation station1, SubwayStation station2) {
		if (!stations.contains(station1) || !stations.contains(station2))
			throw new IllegalArgumentException("Cannot connect to a non-existing stations. station1: " + station1
					+ ", stations2: " + station2 + " Missing 1: " + stations.contains(station1) + " Missing 2: "
					+ stations.contains(station2));
		SubwayConnection con = new SubwayConnection(station1, station2);
		connections.add(con);
		return con;
	}

	private List<MapPosition> getWeightAdjustedMapPos(Map<BuildingType, List<MapPosition>> buildingPosMap) {
		List<MapPosition> result = new ArrayList<>();
		for (Entry<BuildingType, List<MapPosition>> entry : buildingPosMap.entrySet()) {
			for (MapPosition aPos : entry.getValue()) {
				result.addAll(Collections.nCopies(entry.getKey().getSubwayWeight(),aPos));
			}
		}
		return result;
	}

	public int checkRide(int trafficGood, MapPosition pos, double subwayPercent, int cityPop, Random rndGen) {
		// isAttractedBySubway() ==> distance + stationAttractivness
		// (=walkingDistanceToRelevantTarget+nrOfConnectionsToRelevantTarget)
		// Zufallselement
		List<SubwayStation> nearStations = getStationsNearPos(pos, 10);
		int attractiveness = 0;
		nrRequests++;
		List<MapPosition> potentialTargets = getWeightAdjustedMapPos(map.getAllMapPosOfAllBuildingTypes());
		MapPosition target=potentialTargets.get(rndGen.nextInt(potentialTargets.size()));
		MapArea targetArea=map.getOccupiedArea(target);
		
		// TODO: subway isPowered
		// TODO: Route für Anzahl Benützungen

		// Soll-Wert eine Station pro 50k Einwohner
		double subPopFactor = map.getAllBuildingsOfType(BuildingType.subway).size() / Math.max(1, (cityPop / 50000));
		// Basis Wert * U-Bahn Budget Faktor * subPopFactor
		int baseAttraction = (int) Math.round(10 * subwayPercent * (1 + subPopFactor / 10));

		int maxAttract = 0;
		SubwayRide bestRide = null;
		for (SubwayStation subwayStation : nearStations) {
			SubwayRide shortestRide = null;
			attractiveness = baseAttraction - subwayStation.getPos().getDistanceToPos(pos);
			Set<SubwayRide> newRides = subwayStation.getPossibleRides(getConnections());
			int shortestDistance = 999;
			for (SubwayRide aRide : newRides) {
				int dis = aRide.getFinish().getPos().getDistanceToArea(targetArea);
				if (dis < shortestDistance) {
					shortestDistance = dis;
					shortestRide = aRide;
				}
			}

			attractiveness -= shortestDistance;
			if (attractiveness > maxAttract) {
				maxAttract = attractiveness;
				bestRide = shortestRide;
			}
		}
		int rand = rndGen.nextInt(10);
		if (maxAttract > rand) {
			System.out.println("Took subway: " + maxAttract + " / " + rand +" / "+ bestRide);
			rides.add(bestRide);
			nrRides++;
			trafficGood = 1;
		} else {
			System.out.println("Try to use road instead... " + maxAttract + " / " + rand);
		}
		return trafficGood;
	}

	public boolean removeConnection(SubwayConnection aConn) {
		return connections.remove(aConn);
	}

	public List<SubwayStation> getStations() {
		return Collections.unmodifiableList(new ArrayList<>(stations));
	}

	public List<SubwayConnection> getConnections() {
		return Collections.unmodifiableList(new ArrayList<>(connections));
	}

	public int getSubStationCount() {
		return stations.size();
	}

	public int getSubConnectionCount() {
		return connections.size();
	}

	public List<SubwayStation> getStationsNearPos(MapPosition pos, int maxDistance) {
		List<SubwayStation> nearStations = new ArrayList<>();
		for (SubwayStation aStation : stations) {
			if (aStation.getPos().getDistanceToPos(pos) <= maxDistance)
				nearStations.add(aStation);
		}

		return nearStations;
	}

	public int getMaintenanceCost() {
		int maintCost = stations.size() * SUB_STATION_MAINTENANCE + getNetworkLength() * SUB_CON_STEP_MAINTENANCE;

		return maintCost;
	}

	private int getNetworkLength() {
		return getConnections().stream().collect(Collectors.summingInt(SubwayConnection::getLength));
	}

	public int getAssetValue() {
		return stations.size() * SUBNET_STATION_ASSETVALUE + getNetworkLength() * SUBNET_CON_STEP_ASSETVALUE;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(
				"SubwayNetwork [city=" + map + ", connections=" + connections + ", stations=" + stations + "]\r\n");

		for (SubwayConnection subwayConnection : connections) {
			int useCount = 0;
			for (SubwayRide aRide : rides) {
				if (aRide.getRoute().contains(subwayConnection))
					useCount++;
			}
			sb.append("Connection: " + subwayConnection + " ==> used " + useCount + " times.\r\n");
		}

		sb.append("ride/request ratio: " + (nrRequests > 0 ? nrRides / nrRequests : -1));

		return sb.toString();
	}

}
