package micropolisj.engine;

import micropolisj.engine.behaviour.BuildingBehaviour;

public class Subway extends BuildingBehaviour {

	public Subway(Micropolis city) {
		super(city);
	}

	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		System.out.println("Traffic(" + xpos + ", " + ypos + "): " + city.getTrafficDensity(xpos, ypos));
		if (isPowered) {
			for (int i = 0; i < 5; i++) {
				for (int idxX = xpos - i; idxX <= xpos + i; idxX++) {
					for (int idxY = ypos - i; idxY <= ypos + i; idxY++) {
						System.out.println(
								"Traffic Decrease(" + idxX + ", " + idxY + "): " + city.getTrafficDensity(idxX, idxY)
										+ " ==> " + (int) (city.getTrafficDensity(idxX, idxY) * 0.99));
						city.setTrafficDensity(idxX, idxY, (int) (city.getTrafficDensity(idxX, idxY) * 0.99));
					}
				}
			}
		}
	}

}
