package micropolisj.engine.behaviour;

import micropolisj.engine.Micropolis;

public class IceRink extends BuildingBehaviour {

	public IceRink(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		System.out.println(city.getResValve());
		if (isPowered)
			city.setResValve(city.getResValve()+10);
	}

}
