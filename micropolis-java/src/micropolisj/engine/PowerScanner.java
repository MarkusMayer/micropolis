package micropolisj.engine;

import static micropolisj.engine.TileConstants.NUCLEAR;
import static micropolisj.engine.TileConstants.POWERPLANT;
import static micropolisj.engine.TileConstants.isConductive;

import java.util.List;

import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.map.StepDir;

public class PowerScanner {

	private final List<MapPosition> powerPlants;
	private final Micropolis engine;

	public PowerScanner(List<MapPosition> powerPlants, Micropolis engine) {
		this.powerPlants = powerPlants;
		this.engine = engine;
	}

	public boolean[][] doScan() {
		boolean[][] result = new boolean[engine.getHeight()][engine.getWidth()];

		System.out.println("power Scan");
		//
		// Note: brownouts are based on total number of power plants, not the number
		// of powerplants connected to your city.
		//

		int maxPower = engine.getMap().getAllBuildingsOfType(BuildingType.coalPower).size() * 700
				+ engine.getMap().getAllBuildingsOfType(BuildingType.nukePower).size() * 2000;
		int numPower = 0;

		// This is kind of odd algorithm, but I haven't the heart to rewrite it at
		// this time.

		while (!powerPlants.isEmpty()) {
			MapPosition pos = powerPlants.remove(0);

			StepDir aDir = StepDir.none;
			int conNum;
			do {
				if (++numPower > maxPower) {
					// trigger notification
					engine.sendMessage(MicropolisMessage.BROWNOUTS_REPORT);
					return result;
				}
				// System.out.print("Step from " + loc + " in dir " + aDir);
				pos = pos.step(aDir);
				System.out.println("stepped to " + pos);
				engine.setPower(pos, true);

				conNum = 0;
				List<StepDir> dirsToCheck = StepDir.majorDirsAndNone();
				while (!dirsToCheck.isEmpty() && conNum < 2) {
					// System.out.println("currently at " + loc + " checking dir: " + dir);
					if (testForCond(pos, dirsToCheck.get(0))) {
						// System.out.println("dir ok");
						conNum++;
						aDir = dirsToCheck.get(0);
					} else {
						// System.out.println("dir not ok");
					}
					dirsToCheck.remove(0);
				}
				if (conNum > 1) {
					System.out.println("power plant add at " + pos + " " + powerPlants);
					powerPlants.add(pos);
				}
			} while (conNum != 0);
		}
		return result;
	}

	private boolean testForCond(MapPosition pos, StepDir dir) {
		boolean rv = false;
		MapPosition stepPos = pos.step(dir);
		if (engine.getMap().isPosInside(stepPos)) {
			// if (movePowerLocation(pos, dir)) {
			char t = engine.getTile(stepPos);
			rv = (isConductive(t) && t != NUCLEAR && t != POWERPLANT && !engine.hasPower(stepPos));
		}

		return rv;
	}
}
