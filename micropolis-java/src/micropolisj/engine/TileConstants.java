// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

/**
 * Contains symbolic names of certain tile values,
 * and helper functions to test tile attributes.
 * Attributes of tiles that are interesting:
 * <ul>
 * <li>ZONE - the special tile for a zone
 * <li>ANIM - the tile animates
 * <li>BULL - is bulldozable
 * <li>BURN - is combustible
 * <li>COND - can conduct power
 * <li>Road - traffic
 * <li>Rail - railroad
 * <li>Floodable - subject to floods
 * <li>Wet
 * <li>Rubble
 * <li>Tree
 * <li>OverWater
 * <li>Arsonable
 * <li>Vulnerable - vulnerable to earthquakes
 * <li>Bridge
 * <li>AutoDozeRRW - automatically bulldoze when placing Road/Rail/Wire
 * <li>AutoDozeZ - automatically bulldoze when placing Zone
 * </ul>
 */
public class TileConstants
{
	//
	// terrain mapping
	//
	public static final short CLEAR = -1;
	public static final char DIRT = 0;
	public static final char RIVER = 2;
	public static final char REDGE = 3;
	public static final char CHANNEL = 4;
	public static final char RIVEDGE = 5;
	public static final char FIRSTRIVEDGE = 5;
	public static final char LASTRIVEDGE = 20;
	public static final char TREEBASE = 21;
	public static final char WOODS_LOW = TREEBASE;
	public static final char WOODS = 37;
	public static final char WOODS_HIGH = 39;
	public static final char WOODS2 = 40;
	public static final char WOODS5 = 43;
	public static final char RUBBLE = 44;
	public static final char LASTRUBBLE = 47;
	public static final char FLOOD = 48;
	public static final char LASTFLOOD = 51;
	public static final char RADTILE = 52;
	public static final char FIRE = 56;
	public static final char ROADBASE = 64;
	public static final char HBRIDGE = 64;
	public static final char VBRIDGE = 65;
	public static final char ROADS = 66;
	public static final char ROADS2 = 67;
	private static final char ROADS3 = 68;
	private static final char ROADS4 = 69;
	private static final char ROADS5 = 70;
	private static final char ROADS6 = 71;
	private static final char ROADS7 = 72;
	private static final char ROADS8 = 73;
	private static final char ROADS9 = 74;
	private static final char ROADS10 = 75;
	public static final char INTERSECTION = 76;
	public static final char HROADPOWER = 77;
	public static final char VROADPOWER = 78;
	public static final char BRWH = 79;       //horz bridge, open
	public static final char LTRFBASE = 80;
	public static final char BRWV = 95;       //vert bridge, open
	public static final char HTRFBASE = 144;
	private static final char LASTROAD = 206;
	public static final char POWERBASE = 208;
	public static final char HPOWER = 208;    //underwater power-line
	public static final char VPOWER = 209;
	public static final char LHPOWER = 210;
	public static final char LVPOWER = 211;
	public static final char LVPOWER2 = 212;
	private static final char LVPOWER3 = 213;
	private static final char LVPOWER4 = 214;
	private static final char LVPOWER5 = 215;
	private static final char LVPOWER6 = 216;
	private static final char LVPOWER7 = 217;
	private static final char LVPOWER8 = 218;
	private static final char LVPOWER9 = 219;
	private static final char LVPOWER10 = 220;
	public static final char RAILHPOWERV = 221;
	public static final char RAILVPOWERH = 222;
	public static final char LASTPOWER = 222;
	public static final char RAILBASE = 224;
	public static final char HRAIL = 224;     //underwater rail (horz)
	public static final char VRAIL = 225;     //underwater rail (vert)
	public static final char LHRAIL = 226;
	public static final char LVRAIL = 227;
	public static final char LVRAIL2 = 228;
	private static final char LVRAIL3 = 229;
	private static final char LVRAIL4 = 230;
	private static final char LVRAIL5 = 231;
	private static final char LVRAIL6 = 232;
	private static final char LVRAIL7 = 233;
	private static final char LVRAIL8 = 234;
	private static final char LVRAIL9 = 235;
	private static final char LVRAIL10 = 236;
	public static final char HRAILROAD = 237;
	public static final char VRAILROAD = 238;
	public static final char LASTRAIL = 238;
	public static final char RESBASE = 240;
	public static final char RESCLR = 244;
	public static final char HOUSE = 249;
	public static final char LHTHR = 249;  //12 house tiles
	public static final char HHTHR = 260;
	public static final char RZB = 265; //residential zone base
	public static final char HOSPITAL = 409;
	public static final char CHURCH = 418;
	public static final char COMBASE = 423;
	public static final char COMCLR = 427;
	public static final char CZB = 436; //commercial zone base
	public static final char INDBASE = 612;
	public static final char INDCLR = 616;
	public static final char IZB = 625;
	public static final char PORTBASE = 693;
	public static final char PORT = 698;
	public static final char AIRPORT = 716;
	public static final char AIRPORTBASE = 709;
	public static final char POWERPLANT = 750;
	public static final char POWERPLANTBASE = 750;
	public static final char FIRESTATION = 765;
	public static final char FIRESTATIONBASE = 761;
	public static final char POLICESTATION = 774;
	public static final char POLICESTATIONBASE = 770;
	public static final char STADIUMBASE = 779;
	public static final char STADIUM = 784;
	public static final char FULLSTADIUM = 800;
	public static final char NUCLEAR = 816;
	public static final char NUCLEARBASE = 811;
	public static final char LASTZONE = 826;
	public static final char LIGHTNINGBOLT = 827;
	public static final char HBRDG0 = 828;   //draw bridge tiles (horz)
	public static final char HBRDG1 = 829;
	public static final char HBRDG2 = 830;
	public static final char HBRDG3 = 831;
	public static final char FOUNTAIN = 840;
	public static final char TINYEXP = 860;
	private static final char LASTTINYEXP = 867;
	public static final char FOOTBALLGAME1 = 932;
	public static final char FOOTBALLGAME2 = 940;
	public static final char VBRDG0 = 948;   //draw bridge tiles (vert)
	public static final char VBRDG1 = 949;
	public static final char VBRDG2 = 950;
	public static final char VBRDG3 = 951;
	public static final char ICERINK =965;
	public static final char SUBWAY = 977;
	public static final char LAST_TILE = 1000;

	public static final char [] RoadTable = new char[] {
		ROADS, ROADS2, ROADS, ROADS3,
		ROADS2, ROADS2, ROADS4, ROADS8,
		ROADS, ROADS6, ROADS, ROADS7,
		ROADS5, ROADS10, ROADS9, INTERSECTION
		};

	public static final char [] RailTable = new char[] {
		LHRAIL, LVRAIL, LHRAIL, LVRAIL2,
		LVRAIL, LVRAIL, LVRAIL3, LVRAIL7,
		LHRAIL, LVRAIL5, LHRAIL, LVRAIL6,
		LVRAIL4, LVRAIL9, LVRAIL8, LVRAIL10
		};

	public static final char [] WireTable = new char[] {
		LHPOWER, LVPOWER, LHPOWER, LVPOWER2,
		LVPOWER, LVPOWER, LVPOWER3, LVPOWER7,
		LHPOWER, LVPOWER5, LHPOWER, LVPOWER6,
		LVPOWER4, LVPOWER9, LVPOWER8, LVPOWER10
		};

	//
	// status bits
	//
	// public static final char PWRBIT = 32768;  // bit 15 ... currently powered
	// bit 14 ... unused
	// bit 13 ... unused
	// bit 12 ... unused
	// bit 11 ... unused
	// bit 10 ... unused

	/*
	 * @deprecated
	 */
	public static final char ALLBITS = 64512;   // mask for upper 6 bits
	/*
	 * @deprecated
	 */
	public static final char LOMASK = 1023; //mask for low 10 bits

	private TileConstants() {}

	/**
	 * Checks whether the tile can be auto-bulldozed for
	 * placement of road, rail, or wire.
	 */
	public static boolean canAutoBulldozeRRW(int tileValue)
	{
		// can we autobulldoze this tile?
		return (
		(tileValue >= FIRSTRIVEDGE && tileValue <= LASTRUBBLE) ||
		(tileValue >= TINYEXP && tileValue <= LASTTINYEXP)
		);
	}

	/**
	 * Checks whether the tile can be auto-bulldozed for
	 * placement of a zone.
	 */
	public static boolean canAutoBulldozeZ(char tileValue)
	{
		//FIXME- what is significance of POWERBASE+2 and POWERBASE+12 ?

		// can we autobulldoze this tile?
		if ((tileValue >= FIRSTRIVEDGE && tileValue <= LASTRUBBLE) ||
			(tileValue >= POWERBASE + 2 && tileValue <= POWERBASE + 12) ||
			(tileValue >= TINYEXP && tileValue <= LASTTINYEXP))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//used by scanTile
	public static String getTileBehavior(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts != null ? ts.getAttribute("behavior") : null;
	}

	//used by queryZoneStatus
	public static int getDescriptionNumber(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			return ts.getDescriptionNumber();
		}
		else {
			return -1;
		}
	}

	public static int getPollutionValue(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null ? spec.getPollutionValue() : 0;
	}

	public static boolean isAnimated(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.getAnimNext() != null;
	}

	//used by setFire()
	public static boolean isArsonable(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (
			!isZoneCenter(tile) &&
			tile >= LHTHR &&
			tile <= LASTZONE
			);
	}

	//used by Sprite::destroyTile
	public static boolean isBridge(int tile)
	{
		return isRoad(tile) && !isCombustible(tile);
	}

	public static boolean isCombustible(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canBurn;
	}

	public static boolean isConductive(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canConduct;
	}

	/** Used in repairZone(). */
	public static boolean isIndestructible(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RUBBLE && tile < ROADBASE;
	}

	/** Used in zonePlop(). */
	public static boolean isIndestructible2(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= FLOOD && tile < ROADBASE;
	}

	public static boolean isOverWater(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.overWater;
	}

	public static boolean isRubble(int tile)
	{
		assert (tile & LOMASK) == tile;

		return ((tile >= RUBBLE) &&
			(tile <= LASTRUBBLE));
	}

	public static boolean isTree(int tile)
	{
		assert (tile & LOMASK) == tile;

		return ((tile >= WOODS_LOW) &&
			(tile <= WOODS_HIGH));
	}

	//used by makeEarthquake
	public static boolean isVulnerable(int tile)
	{
		assert (tile & LOMASK) == tile;

		if (tile < RESBASE ||
			tile > LASTZONE ||
			isZoneCenter(tile)
			) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkWet(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile == POWERBASE ||
			tile == POWERBASE+1 ||
			tile == RAILBASE ||
			tile == RAILBASE + 1 ||
			tile == BRWH ||
			tile == BRWV);
	}

	public static CityDimension getZoneSizeFor(int tile)
	{
		assert isZoneCenter(tile);
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null ? spec.getBuildingSize() : null;
	}

	public static boolean isConstructed(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= 0 && tile >= ROADBASE;
	}

	static boolean isRiverEdge(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= FIRSTRIVEDGE && tile <= LASTRIVEDGE;
	}

	public static boolean isDozeable(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canBulldoze;
	}

	static boolean isFloodable(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile == DIRT || (isDozeable(tile) && isCombustible(tile)));
	}

	/**
	 * Note: does not include rail/road tiles.
	 * @see #isRoadAny
	 */
	public static boolean isRoad(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= ROADBASE && tile < POWERBASE);
	}

	public static boolean isRoadAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= ROADBASE && tile < POWERBASE)
			|| (tile == HRAILROAD)
			|| (tile == VRAILROAD);
	}

	/**
	 * Checks whether the tile is a road that will automatically change to connect to
	 * neighboring roads.
	 */
	public static boolean isRoadDynamic(int tile)
	{
		int tmp = neutralizeRoad(tile);
		return (tmp >= ROADS && tmp <= INTERSECTION);
	}

	public static boolean roadConnectsEast(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER)
			) &&
			(tile != VROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != VBRIDGE));
	}

	public static boolean roadConnectsNorth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER)
			) &&
			(tile != HROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != ROADBASE));
	}

	public static boolean roadConnectsSouth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER)
			) &&
			(tile != HROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != ROADBASE));
	}

	public static boolean roadConnectsWest(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER)	
			) &&
			(tile != VROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != VBRIDGE));
	}

	public static boolean isRail(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= RAILBASE && tile < RESBASE);
	}

	public static boolean isRailAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= RAILBASE && tile < RESBASE)
			|| (tile == RAILHPOWERV)
			|| (tile == RAILVPOWERH);
	}

	public static boolean isRailDynamic(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= LHRAIL && tile <= LVRAIL10);
	}

	public static boolean railConnectsEast(int tile)
	{
		tile = neutralizeRoad(tile);
		return (tile >= RAILHPOWERV && tile <= VRAILROAD &&
			tile != RAILVPOWERH &&
			tile != VRAILROAD &&
			tile != VRAIL);
	}

	public static boolean railConnectsNorth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (tile >= RAILHPOWERV && tile <= VRAILROAD &&
			tile != RAILHPOWERV &&
			tile != HRAILROAD &&
			tile != HRAIL);
	}

	public static boolean railConnectsSouth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (tile >= RAILHPOWERV && tile <= VRAILROAD &&
			tile != RAILHPOWERV &&
			tile != HRAILROAD &&
			tile != HRAIL);
	}

	public static boolean railConnectsWest(int tile)
	{
		tile = neutralizeRoad(tile);
		return (tile >= RAILHPOWERV && tile <= VRAILROAD &&
			tile != RAILVPOWERH &&
			tile != VRAILROAD &&
			tile != VRAIL);
	}

	public static boolean isWireDynamic(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= LHPOWER && tile <= LVPOWER10);
	}

	public static boolean wireConnectsEast(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != HPOWER &&
			ntile != HROADPOWER &&
			ntile != RAILHPOWERV);
	}

	public static boolean wireConnectsNorth(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != VPOWER &&
			ntile != VROADPOWER &&
			ntile != RAILVPOWERH);
	}

	public static boolean wireConnectsSouth(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != VPOWER &&
			ntile != VROADPOWER &&
			ntile != RAILVPOWERH);
	}

	public static boolean wireConnectsWest(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != HPOWER &&
			ntile != HROADPOWER &&
			ntile != RAILHPOWERV);
	}

	public static boolean isCommercialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.isComZoneCenter();
		}
		return false;
	}

	public static boolean isHospitalOrChurch(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= HOSPITAL &&
			tile < COMBASE;
	}

	/**
	 * Checks whether the tile is defined with the "industrial-zone" attribute.
	 * Note: the old version of this function erroneously included the coal power
	 * plant smoke as an industrial zone.
	 */
	public static boolean isIndustrialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.isIndZoneCenter();
		}
		return false;
	}

	public static boolean isResidentialClear(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE && tile <= RESBASE+8;
	}

	/** Note: does not include hospital/church.
	 * @see #isHospitalOrChurch
	 */
	public static boolean isResidentialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE &&
			tile < HOSPITAL;
	}

	// includes hospital/church.
	public static boolean isResidentialZoneAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.isResZoneCenter();
		}
		return false;
	}

	/** Tile represents a part of any sort of building. */
	public static boolean isZoneAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE;
	}

	public static boolean isZoneCenter(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.zone;
	}

	/**
	 * Converts a road tile value with traffic to the equivalent
	 * road tile without traffic.
	 */
	public static char neutralizeRoad(int tile)
	{
		assert (tile & LOMASK) == tile;

		if (tile >= ROADBASE && tile <= LASTROAD) {
			tile = ((tile - ROADBASE) & 0xf) + ROADBASE;
		}
		return (char)tile;
	}

	/**
	 * Determine the population level of a Residential zone
	 * tile. Note: the input tile MUST be a full-size res zone,
	 * it cannot be an empty zone.
	 * @return int multiple of 8 between 16 and 40.
	 */
	public static int residentialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation();
	}

	/**
	 * Determine the population level of a Commercial zone
	 * tile.
	 * The input tile MAY be an empty zone.
	 * @return int between 0 and 5.
	 */
	public static int commercialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation() / 8;
	}

	/**
	 * Determine the population level of an Industrial zone tile.
	 * The input tile MAY be an empty zone.
	 * @return int between 0 and 4.
	 */
	public static int industrialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation() / 8;
	}
	
	public static boolean isSubway(int tile) {
		assert (tile & LOMASK) == tile;
		
		return tile==SUBWAY;
	}
}
