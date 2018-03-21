package micropolisj.engine.tool;

import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapPosition;

public class ToolEvent {

	final EventType evType;
	final BuildingType building;
	final MapPosition centerPos;

	ToolEvent(EventType evType, MapPosition centerPos) {
		this.evType = evType;
		this.building = null;
		this.centerPos = centerPos;
	}
	
	ToolEvent(EventType evType, BuildingType building, MapPosition centerPos) {
		this.evType = evType;
		this.building = building;
		this.centerPos = centerPos;
	}
	
	public EventType getEvType() {
		return evType;
	}

	public BuildingType getBuilding() {
		return building;
	}

	public MapPosition getCenterPos() {
		return centerPos;
	}

	@Override
	public String toString() {
		return "ToolEvent [evType=" + evType + ", building=" + building + ", topLeft=" + centerPos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((evType == null) ? 0 : evType.hashCode());
		result = prime * result + ((centerPos == null) ? 0 : centerPos.hashCode());
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
		ToolEvent other = (ToolEvent) obj;
		if (building != other.building)
			return false;
		if (evType != other.evType)
			return false;
		if (centerPos == null) {
			if (other.centerPos != null)
				return false;
		} else if (!centerPos.equals(other.centerPos))
			return false;
		return true;
	}



	public enum EventType {
		build, bulldoze
	}
}
