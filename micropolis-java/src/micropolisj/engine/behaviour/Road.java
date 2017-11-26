package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.BRWH;
import static micropolisj.engine.TileConstants.BRWV;
import static micropolisj.engine.TileConstants.CHANNEL;
import static micropolisj.engine.TileConstants.HBRDG0;
import static micropolisj.engine.TileConstants.HBRDG1;
import static micropolisj.engine.TileConstants.HBRDG2;
import static micropolisj.engine.TileConstants.HBRDG3;
import static micropolisj.engine.TileConstants.HBRIDGE;
import static micropolisj.engine.TileConstants.HTRFBASE;
import static micropolisj.engine.TileConstants.LTRFBASE;
import static micropolisj.engine.TileConstants.RIVER;
import static micropolisj.engine.TileConstants.ROADBASE;
import static micropolisj.engine.TileConstants.RUBBLE;
import static micropolisj.engine.TileConstants.VBRDG0;
import static micropolisj.engine.TileConstants.VBRDG1;
import static micropolisj.engine.TileConstants.VBRDG2;
import static micropolisj.engine.TileConstants.VBRDG3;
import static micropolisj.engine.TileConstants.VBRIDGE;
import static micropolisj.engine.TileConstants.isCombustible;
import static micropolisj.engine.TileConstants.isConductive;
import static micropolisj.engine.TileConstants.isOverWater;

import java.util.Optional;

import micropolisj.engine.Micropolis;
import micropolisj.engine.Sprite;
import micropolisj.engine.SpriteKind;
import micropolisj.engine.TileBehavior;

public class Road extends TileBehavior {
	
	private static int [] TRAFFIC_DENSITY_TAB = { ROADBASE, LTRFBASE, HTRFBASE };

	public Road(Micropolis city) {
		super(city);
	}

	@Override
	public void apply() {
		city.incRoadCounter();

		if (city.getRoadEffect() < 30)
		{
			// deteriorating roads
			if (PRNG.nextInt(512) == 0)
			{
				if (!isConductive(tile))
				{
					if (city.getRoadEffect() < PRNG.nextInt(32))
					{
						if (isOverWater(tile))
							city.setTile(xpos, ypos, RIVER);
						else
							city.setTile(xpos, ypos, (char)(RUBBLE + PRNG.nextInt(4)));
						return;
					}
				}
			}
		}

		if (!isCombustible(tile)) //bridge
		{
			city.incRoadCounter(4);
			if (doBridge())
				return;
		}

		int tden;
		if (tile < LTRFBASE)
			tden = 0;
		else if (tile < HTRFBASE)
			tden = 1;
		else {
			city.incRoadCounter();
			tden = 2;
		}

		int trafficDensity = city.getTrafficDensity(xpos, ypos);
		int newLevel = trafficDensity < 64 ? 0 :
			trafficDensity < 192 ? 1 : 2;
		
		assert newLevel >= 0 && newLevel < TRAFFIC_DENSITY_TAB.length;

		if (tden != newLevel)
		{
			int z = ((tile - ROADBASE) & 15) + TRAFFIC_DENSITY_TAB[newLevel];
			city.setTile(xpos, ypos, (char) z);
		}
	}
	
	/**
	 * Called when the current tile is a road bridge over water.
	 * Handles the draw bridge. For the draw bridge to appear,
	 * there must be a boat on the water, the boat must be
	 * within a certain distance of the bridge, it must be where
	 * the map generator placed 'channel' tiles (these are tiles
	 * that look just like regular river tiles but have a different
	 * numeric value), and you must be a little lucky.
	 *
	 * @return true if the draw bridge is open; false otherwise
	 */
	boolean doBridge()
	{
		final int HDx[] = { -2,  2, -2, -1,  0,  1,  2 };
		final int HDy[] = { -1, -1,  0,  0,  0,  0,  0 };
		final char HBRTAB[] = {
			HBRDG1,       HBRDG3,
			HBRDG0,       RIVER,
			BRWH,         RIVER,
			HBRDG2 };
		final char HBRTAB2[] = {
			RIVER,        RIVER,
			HBRIDGE,      HBRIDGE,
			HBRIDGE,      HBRIDGE,
			HBRIDGE };

		final int VDx[] = {  0,  1,  0,  0,  0,  0,  1 };
		final int VDy[] = { -2, -2, -1,  0,  1,  2,  2 };
		final char VBRTAB[] = {
			VBRDG0,       VBRDG1,
			RIVER,        BRWV,
			RIVER,        VBRDG2,
			VBRDG3 };
		final char VBRTAB2[] = {
			VBRIDGE,      RIVER,
			VBRIDGE,      VBRIDGE,
			VBRIDGE,      VBRIDGE,
			RIVER };

		if (tile == BRWV) {
			// vertical bridge, open
			if (PRNG.nextInt(4) == 0 && getBoatDis() > 340/16) {
				//close the bridge
				applyBridgeChange(VDx, VDy, VBRTAB, VBRTAB2);
			}
			return true;
		}
		else if (tile == BRWH) {
			// horizontal bridge, open
			if (PRNG.nextInt(4) == 0 && getBoatDis() > 340/16) {
				// close the bridge
				applyBridgeChange(HDx, HDy, HBRTAB, HBRTAB2);
			}
			return true;
		}

		if (getBoatDis() < 300/16 && PRNG.nextInt(8) == 0) {
			if ((tile & 1) != 0) {
				// vertical bridge
				if (xpos < city.getWidth()-1) {
					// look for CHANNEL tile to right of
					// bridge. the CHANNEL tiles are only
					// found in the very center of the
					// river
					if (city.getTile(xpos+1,ypos) == CHANNEL) {
						// vertical bridge, open it up
						applyBridgeChange(VDx, VDy, VBRTAB2, VBRTAB);
						return true;
					}
				}
				return false;
			}
			else {
				// horizontal bridge
				if (ypos > 0) {
					// look for CHANNEL tile just above
					// bridge. the CHANNEL tiles are only
					// found in the very center of the
					// river
					if (city.getTile(xpos, ypos-1) == CHANNEL) {
						// open it up
						applyBridgeChange(HDx, HDy, HBRTAB2, HBRTAB);
						return true;
					}
				}
				return false;
			}
		}

		return false;
	}

	/**
	 * Helper function for doBridge- it toggles the draw-bridge.
	 */
	private void applyBridgeChange(int [] Dx, int [] Dy, char [] fromTab, char [] toTab)
	{
	//FIXME- a closed bridge with traffic on it is not
	// correctly handled by this subroutine, because the
	// the tiles representing traffic on a bridge do not match
	// the expected tile values of fromTab

		for (int z = 0; z < 7; z++) {
			int x = xpos + Dx[z];
			int y = ypos + Dy[z];
			if (city.testBounds(x,y)) {
				if ((city.getTile(x,y) == fromTab[z]) ||
					(city.getTile(x,y) == CHANNEL)
					) {
					city.setTile(x, y, toTab[z]);
				}
			}
		}
	}

	/**
	 * Calculate how far away the boat currently is from the
	 * current tile.
	 */
	int getBoatDis()
	{
		int dist = 99999;
		Optional<Sprite> s=city.getVisibleBoatSprite();
			if (s.isPresent())
			{
				int x = s.get().x / 16;
				int y = s.get().y / 16;
				int d = Math.abs(xpos-x) + Math.abs(ypos-y);
				dist = Math.min(d, dist);
			}
		return dist;
	}

}
