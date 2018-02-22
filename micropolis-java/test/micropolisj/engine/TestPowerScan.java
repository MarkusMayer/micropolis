package micropolisj.engine;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import micropolisj.engine.PowerScanner.PowerScanResult;
import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.MapPosition;

public class TestPowerScan {

	Micropolis engine;

	@Before
	public void setup() {
		engine = new Micropolis(10, 10);
	}

	@Test
	public void testPowerScan() {
		boolean[][] expectedPowerMap = { { false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, true, true, true, false },
				{ false, false, false, false, false, false, true, true, true, false },
				{ false, true, true, true, true, false, true, true, true, false },
				{ false, true, false, true, true, false, false, true, false, false },
				{ false, true, true, true, true, true, true, true, false, false },
				{ false, true, true, true, true, false, false, true, false, false },
				{ false, false, false, false, false, false, true, true, true, false },
				{ false, false, false, false, false, false, true, true, true, false },
				{ false, false, false, false, false, false, true, true, true, false } };

		// TODO inc coal count fehlt beim builden
		engine.getMap().build(MapPosition.at(1, 3), BuildingType.nukePower);
		engine.addPowerPlant(MapPosition.at(1, 3));
		engine.incCoalCount();
		engine.getMap().setSpec(MapPosition.at(5, 5), Tiles.get(208));
		engine.getMap().setSpec(MapPosition.at(6, 5), Tiles.get(208));
		engine.getMap().setSpec(MapPosition.at(7, 5), Tiles.get(208));
		engine.getMap().setSpec(MapPosition.at(7, 6), Tiles.get(208));
		engine.getMap().setSpec(MapPosition.at(7, 4), Tiles.get(208));
		engine.getMap().build(MapPosition.at(6, 1), BuildingType.residential);
		engine.getMap().build(MapPosition.at(6, 7), BuildingType.firestation);
		PowerScanResult scanResult = (new PowerScanner(
				new ArrayList<>(engine.getMap().getAllMapPosOfType(BuildingType.nukePower)),
				engine.getMap().getReadOnlyMap())).doScan();
		for (int i = 0; i < scanResult.getPowerMap().getDimension().getY(); i++) {
			for (int j = 0; j < scanResult.getPowerMap().getDimension().getX(); j++) {
				System.out.print(scanResult.getPowerMap().getAt(MapPosition.at(i, j)) ? "T " : "F ");
			}
			System.out.println("");
		}
		Boolean[][] res=scanResult.getPowerMap().asArray(new Boolean(true).getClass());
//		Assert.assertArrayEquals(expectedPowerMap, scanResult.getPowerMap());
		Assert.assertArrayEquals(expectedPowerMap, res);

	}

}
