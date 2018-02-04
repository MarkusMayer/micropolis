package micropolisj.engine.map;

public enum BuildingType {
	police (500),
	firestation (500),
	stadium (5000),
	coalPower (3000),
	nukePower (5000),
	subway(500),
	icerink(500),
	residential(100),
	commercial(100),
	industrial(100),
	seaport(3000),
	airport(10000);
	
	int cost;
	
	private BuildingType(int cost) {
		this.cost=cost;
	}
	
	public int getCost() {
		return cost;
	}
}