package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.NUCLEAR;
import static micropolisj.engine.TileConstants.POWERPLANT;
import static micropolisj.engine.TileConstants.getZoneSizeFor;
import static micropolisj.engine.TileConstants.isAnimated;
import static micropolisj.engine.TileConstants.isIndestructible;
import static micropolisj.engine.TileConstants.isIndestructible2;
import static micropolisj.engine.TileConstants.isZoneCenter;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TileSpec;
import micropolisj.engine.Tiles;
import micropolisj.engine.TrafficGen;
import micropolisj.engine.ZoneType;

public abstract class BuildingBehaviour extends TileBehavior {
	
//	protected TrafficGen traffic;	

	public BuildingBehaviour(Micropolis city) {
		super(city);
//		traffic=new TrafficGen(city);
	}

	@Override
	public void apply() {
		boolean isPowered=false;
		if (isPowerCheckRequired()) {
			isPowered=checkZonePower();
		}
		doBuildingBehaviour(isPowered);
	}

	boolean isPowerCheckRequired() {
		return true;
	}
	
	boolean isRepairable() {
		return true;
	}

	private boolean checkZonePower()
	{
		boolean zonePwrFlag = setZonePower();

		if (zonePwrFlag)
		{
			city.incPoweredZoneCount();
		}
		else
		{
			city.incUnpoweredZoneCount();
		}

		return zonePwrFlag;
	}

	private boolean setZonePower()
	{
		boolean oldPower = city.isTilePowered(xpos, ypos);
		boolean newPower = (
			tile == NUCLEAR ||
			tile == POWERPLANT ||
			city.hasPower(xpos,ypos)
			);

		if (newPower && !oldPower)
		{
			city.setTilePower(xpos, ypos, true);
			city.powerZone(xpos, ypos, getZoneSizeFor(tile));
		}
		else if (!newPower && oldPower)
		{
			city.setTilePower(xpos, ypos, false);
			city.shutdownZone(xpos, ypos, getZoneSizeFor(tile));
		}

		return newPower;
	}
	
	/**
	 * Place a 3x3 zone on to the map, centered on the current location.
	 * Note: nothing is done if part of this zone is off the edge
	 * of the map or is being flooded or radioactive.
	 *
	 * @param base The "zone" tile value for this zone.
	 * @return true iff the zone was actually placed.
	 */
	boolean zonePlop(int base)
	{
		assert isZoneCenter(base);

		TileSpec.BuildingInfo bi = Tiles.get(base).getBuildingInfo();
		assert bi != null;
		if (bi == null)
			return false;

		for (int y = ypos-1; y < ypos-1+bi.getHeight(); y++)
		{
			for (int x = xpos-1; x < xpos-1+bi.getWidth(); x++)
			{
				if (!city.testBounds(x, y)) {
					return false;
				}
				if (isIndestructible2(city.getTile(x,y))) {
					// radioactive, on fire, or flooded
					return false;
				}
			}
		}

		assert bi.getMembers().length == bi.getWidth() * bi.getHeight();
		int i = 0;
		for (int y = ypos-1; y < ypos-1+bi.getHeight(); y++)
		{
			for (int x = xpos-1; x < xpos-1+bi.getWidth(); x++)
			{
				city.setTile(x, y, (char) bi.getMembers()[i]);
				i++;
			}
		}

		// refresh own tile property
		this.tile = city.getTile(xpos, ypos);

		setZonePower();
		return true;
	}
	
	/**
	 * Regenerate the tiles that make up the zone, repairing from
	 * fire, etc.
	 * Only tiles that are not rubble, radioactive, flooded, or
	 * on fire will be regenerated.
	 * @param zoneCenter the tile value for the "center" tile of the zone
	 * @param zoneSize integer (3-6) indicating the width/height of
	 * the zone.
	 */
	void repairZone(char zoneCenter, int zoneSize)
	{
		// from the given center tile, figure out what the
		// northwest tile should be
		int zoneBase = zoneCenter - 1 - zoneSize;

		for (int y = 0; y < zoneSize; y++)
		{
			for (int x = 0; x < zoneSize; x++, zoneBase++)
			{
				int xx = xpos - 1 + x;
				int yy = ypos - 1 + y;

				if (city.testBounds(xx, yy))
				{
					int thCh = city.getTile(xx, yy);
					if (isZoneCenter(thCh)) {
						continue;
					}

					if (isAnimated(thCh))
						continue;

					if (!isIndestructible(thCh))
					{  //not rubble, radioactive, on fire or flooded

						city.setTile(xx,yy,(char) zoneBase);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the land-value class (0-3) for the current
	 * residential or commercial zone location.
	 * @return integer from 0 to 3, 0 is the lowest-valued
	 * zone, and 3 is the highest-valued zone.
	 */
	int getCRValue()
	{
		int lval = city.getLandValue(xpos, ypos);
		lval -= city.pollutionMem[ypos/2][xpos/2];

		if (lval < 30)
			return 0;

		if (lval < 80)
			return 1;

		if (lval < 150)
			return 2;

		return 3;
	}
	
	/**
	 * Record a zone's population change to the rate-of-growth
	 * map.
	 * An adjustment of +/- 1 corresponds to one little house.
	 * An adjustment of +/- 8 corresponds to a full-size zone.
	 *
	 * @param amount the positive or negative adjustment to record.
	 */
	void adjustROG(int amount)
	{
		city.rateOGMem[ypos/8][xpos/8] += 4*amount;
	}
	
	/**
	 * @return 1 if traffic "passed", 0 if traffic "failed", -1 if no roads found
	 */
	int makeTraffic(ZoneType zoneType)
	{
//		traffic.mapX = xpos;
//		traffic.mapY = ypos;
//		traffic.sourceZone = zoneType;
		return new TrafficGen(city, xpos, ypos, zoneType).makeTraffic();
	}
	
	/**
	 * Place tiles for a stadium (full or empty).
	 * @param zoneCenter either STADIUM or FULLSTADIUM
	 */
	void drawStadium(int zoneCenter)
	{
		int zoneBase = zoneCenter - 1 - 4;

		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				city.setTile(xpos - 1 + x, ypos - 1 + y, (char)zoneBase);
				zoneBase++;
			}
		}
		city.setTilePower(xpos, ypos, true);
	}

	protected abstract void doBuildingBehaviour(boolean isPowered);
}
