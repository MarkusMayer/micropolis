package micropolisj.engine;

import static micropolisj.engine.TileConstants.NUCLEAR;
import static micropolisj.engine.TileConstants.POWERPLANT;
import static micropolisj.engine.TileConstants.isConductive;

import java.util.List;
import java.util.Objects;

import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapBase;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.map.ReadOnlyCityMap;
import micropolisj.engine.map.StepDir;

public class PowerScanner {

	private final List<MapPosition> powerPlants;
	private final ReadOnlyCityMap cityMap;

	public PowerScanner(List<MapPosition> powerPlants, ReadOnlyCityMap cityMap) {
		this.powerPlants = Objects.requireNonNull(powerPlants);
		this.cityMap = Objects.requireNonNull(cityMap);
	}

	public PowerScanResult doScan() {
		//TODO change type to Map<MapPosition,boolean>
//		boolean[][] result = new boolean[cityMap.getDimension().getY()][cityMap.getDimension().getX()];
		MapBase<Boolean> result=new MapBase<>(cityMap.getDimension(), ()->false);
		
		System.out.println("power Scan");
		//
		// Note: brownouts are based on total number of power plants, not the number
		// of powerplants connected to your city.
		//

		int maxPower = cityMap.getAllBuildingsOfType(BuildingType.coalPower).size() * 700
				+ cityMap.getAllBuildingsOfType(BuildingType.nukePower).size() * 2000;
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
					//engine.sendMessage(MicropolisMessage.BROWNOUTS_REPORT);
					return new PowerScanResult(result,true);
				}
				// System.out.print("Step from " + loc + " in dir " + aDir);
				pos = pos.step(aDir);
				System.out.println("stepped to " + pos);
				result.putAt(pos, true);

				conNum = 0;
				List<StepDir> dirsToCheck = StepDir.majorDirsAndNone();
				while (!dirsToCheck.isEmpty() && conNum < 2) {
					// System.out.println("currently at " + loc + " checking dir: " + dir);
					if (testForCond(pos, dirsToCheck.get(0),result)) {
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
		return new PowerScanResult(result, false);
	}

	private boolean testForCond(MapPosition pos, StepDir dir,MapBase<Boolean> result) {
		boolean rv = false;
		MapPosition stepPos = pos.step(dir);
		if (cityMap.isPosInside(stepPos)) {
			// if (movePowerLocation(pos, dir)) {
			char t = (char)cityMap.getTileNr(stepPos);
			rv = (isConductive(t) && t != NUCLEAR && t != POWERPLANT && !result.getAt(stepPos));
		}

		return rv;
	}
	
	final class PowerScanResult {
		
		final private MapBase<Boolean>powerMap;
		final private boolean hasBrownout;
		
		PowerScanResult(MapBase<Boolean> powerMap, boolean hasBrownout) {
			this.powerMap=Objects.requireNonNull(powerMap);
			this.hasBrownout=Objects.requireNonNull(hasBrownout);
		}

		public MapBase<Boolean> getPowerMap() {
			return powerMap;
		}

		public boolean isHasBrownout() {
			return hasBrownout;
		}
		
	}
}
