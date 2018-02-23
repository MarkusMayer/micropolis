// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import static micropolisj.engine.TileConstants.ALLBITS;
import static micropolisj.engine.TileConstants.CHANNEL;
import static micropolisj.engine.TileConstants.COMBASE;
import static micropolisj.engine.TileConstants.DIRT;
import static micropolisj.engine.TileConstants.FIRE;
import static micropolisj.engine.TileConstants.FLOOD;
import static micropolisj.engine.TileConstants.HHTHR;
import static micropolisj.engine.TileConstants.INDBASE;
import static micropolisj.engine.TileConstants.LASTZONE;
import static micropolisj.engine.TileConstants.LHTHR;
import static micropolisj.engine.TileConstants.LOMASK;
import static micropolisj.engine.TileConstants.NUCLEAR;
import static micropolisj.engine.TileConstants.PORTBASE;
import static micropolisj.engine.TileConstants.POWERPLANT;
import static micropolisj.engine.TileConstants.RADTILE;
import static micropolisj.engine.TileConstants.RESCLR;
import static micropolisj.engine.TileConstants.RIVER;
import static micropolisj.engine.TileConstants.RUBBLE;
import static micropolisj.engine.TileConstants.commercialZonePop;
import static micropolisj.engine.TileConstants.getDescriptionNumber;
import static micropolisj.engine.TileConstants.getPollutionValue;
import static micropolisj.engine.TileConstants.getTileBehavior;
import static micropolisj.engine.TileConstants.getZoneSizeFor;
import static micropolisj.engine.TileConstants.industrialZonePop;
import static micropolisj.engine.TileConstants.isAnimated;
import static micropolisj.engine.TileConstants.isArsonable;
import static micropolisj.engine.TileConstants.isCombustible;
import static micropolisj.engine.TileConstants.isConductive;
import static micropolisj.engine.TileConstants.isConstructed;
import static micropolisj.engine.TileConstants.isFloodable;
import static micropolisj.engine.TileConstants.isRiverEdge;
import static micropolisj.engine.TileConstants.isVulnerable;
import static micropolisj.engine.TileConstants.isZoneCenter;
import static micropolisj.engine.TileConstants.residentialZonePop;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;

import micropolisj.engine.PowerScanner.PowerScanResult;
import micropolisj.engine.behaviour.Airport;
import micropolisj.engine.behaviour.CoalPower;
import micropolisj.engine.behaviour.Commercial;
import micropolisj.engine.behaviour.Explosion;
import micropolisj.engine.behaviour.Fire;
import micropolisj.engine.behaviour.FireStation;
import micropolisj.engine.behaviour.Flood;
import micropolisj.engine.behaviour.HospitalChurch;
import micropolisj.engine.behaviour.IceRink;
import micropolisj.engine.behaviour.Industrial;
import micropolisj.engine.behaviour.NuclearPower;
import micropolisj.engine.behaviour.PoliceStation;
import micropolisj.engine.behaviour.Radioactive;
import micropolisj.engine.behaviour.Rail;
import micropolisj.engine.behaviour.Residential;
import micropolisj.engine.behaviour.Road;
import micropolisj.engine.behaviour.Seaport;
import micropolisj.engine.behaviour.StadiumEmpty;
import micropolisj.engine.behaviour.StadiumFull;
import micropolisj.engine.behaviour.TileBehavior;
import micropolisj.engine.map.BuildingType;
import micropolisj.engine.map.CityMap;
import micropolisj.engine.map.MapBase;
import micropolisj.engine.map.MapPosition;
import micropolisj.engine.map.StepDir;
import micropolisj.engine.subway.SubwayConnection;
import micropolisj.engine.subway.SubwayNetwork;
import micropolisj.engine.subway.SubwayStation;
import micropolisj.engine.tool.ToolEffect;
import micropolisj.engine.tool.ToolEffectIfc;

/**
 * The main simulation engine for Micropolis. The front-end should call
 * animate() periodically to move the simulation forward in time.
 */
public class Micropolis {
	static final Random DEFAULT_PRNG = new Random();

	public Random PRNG;

	// full size arrays
	private CityMap map;
	MapBase<Boolean> powerMap;

	// half-size arrays

	/**
	 * For each 2x2 section of the city, the land value of the city (0-250). 0 is
	 * lowest land value; 250 is maximum land value. Updated each cycle by
	 * ptlScan().
	 */
	int[][] landValueMem;

	/**
	 * For each 2x2 section of the city, the pollution level of the city (0-255). 0
	 * is no pollution; 255 is maximum pollution. Updated each cycle by ptlScan();
	 * affects land value.
	 */
	public int[][] pollutionMem;

	/**
	 * For each 2x2 section of the city, the crime level of the city (0-250). 0 is
	 * no crime; 250 is maximum crime. Updated each cycle by crimeScan(); affects
	 * land value.
	 */
	public int[][] crimeMem;

	/**
	 * For each 2x2 section of the city, the population density (0-?). Used for map
	 * overlays and as a factor for crime rates.
	 */
	public int[][] popDensity;

	/**
	 * For each 2x2 section of the city, the traffic density (0-255). If less than
	 * 64, no cars are animated. If between 64 and 192, then the "light traffic"
	 * animation is used. If 192 or higher, then the "heavy traffic" animation is
	 * used.
	 */
	int[][] trfDensity;

	// quarter-size arrays

	/**
	 * For each 4x4 section of the city, an integer representing the natural land
	 * features in the vicinity of this part of the city.
	 */
	int[][] terrainMem;

	// eighth-size arrays

	/**
	 * For each 8x8 section of the city, the rate of growth. Capped to a number
	 * between -200 and 200. Used for reporting purposes only; the number has no
	 * affect.
	 */
	public int[][] rateOGMem; // rate of growth?

	private int[][] fireStMap; // firestations- cleared and rebuilt each sim cycle

	public int[][] fireRate; // firestations reach- used for overlay graphs
	private int[][] policeMap; // police stations- cleared and rebuilt each sim cycle
	public int[][] policeMapEffect;// police stations reach- used for overlay graphs

	/**
	 * For each 8x8 section of city, this is an integer between 0 and 64, with
	 * higher numbers being closer to the center of the city.
	 */
	private int[][] comRate;

	static final int DEFAULT_WIDTH = 120;
	static final int DEFAULT_HEIGHT = 100;

	// public final CityBudget budget = new CityBudget(this);
	public final CityBudget budget = new CityBudget();
	public boolean autoBulldoze = true;
	public boolean autoBudget = false;
	public Speed simSpeed = Speed.NORMAL;
	public boolean noDisasters = false;

	private Levels gameLevel = Levels.easy;

	boolean autoGo;

	// census numbers, reset in phase 0 of each cycle, summed during map scan
	private int poweredZoneCount;
	private int unpoweredZoneCount;
	private int roadTotal;
	private int railTotal;
	private int fireCounter;
	private int resZoneCount;
	private int comZoneCount;
	private int indZoneCount;
	private int resPop;
	private int comPop;
	private int indPop;
	private int hospitalCount;
	private int churchCount;
	private int policeCount;
	private int fireStationCount;
	private int subStationCount;
	private int stadiumCount;
	private int coalCount;
	private int nuclearCount;
	private int seaportCount;
	private int airportCount;

	int totalPop;
	int lastCityPop;

	// used in generateBudget()
	int lastRoadTotal;
	int lastRailTotal;
	int lastTotalPop;
	int lastFireStationCount;
	int lastPoliceCount;

	int trafficMaxLocationX;
	int trafficMaxLocationY;
	int pollutionMaxLocationX;
	int pollutionMaxLocationY;
	int crimeMaxLocationX;
	int crimeMaxLocationY;
	public int centerMassX;
	public int centerMassY;

	private TypeDemand needHospital; // -1 too many already, 0 just right, 1 not enough
	private TypeDemand needChurch; // -1 too many already, 0 just right, 1 not enough

	int crimeAverage;
	int pollutionAverage;
	int landValueAverage;
	int trafficAverage;

	private int resValve; // ranges between -2000 and 2000, updated by setValves
	private int comValve; // ranges between -1500 and 1500
	private int indValve; // ranges between -1500 and 1500

	boolean resCap; // residents demand a stadium, caps resValve at 0
	boolean comCap; // commerce demands airport, caps comValve at 0
	boolean indCap; // industry demands sea port, caps indValve at 0
	int crimeRamp;
	int polluteRamp;

	//
	// budget stuff
	//
	private int cityTax = 7;
	private double roadPercent = 1.0;
	private double subPercent = 1.0;
	private double policePercent = 1.0;
	private double firePercent = 1.0;

	int taxEffect = 7;
	private int roadEffect = 32;

	private int policeEffect = 1000;
	private int fireEffect = 1000;

	int cashFlow; // net change in totalFunds in previous year

	boolean newPower;

	int floodCnt; // number of turns the flood will last
	int floodX;
	int floodY;

	public int cityTime; // counts "weeks" (actually, 1/48'ths years)
	int scycle; // same as cityTime, except mod 1024
	int fcycle; // counts simulation steps (mod 1024)
	int acycle; // animation cycle (mod 960)

	public CityEval evaluation;

	public SubwayNetwork subNet;

	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	static final int VALVERATE = 2;
	public static final int CENSUSRATE = 4;
	static final int TAXFREQ = 48;

	public Micropolis() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Micropolis(int width, int height, Random rnd) {
		this(width, height);
		PRNG = rnd;
	}

	public Micropolis(int width, int height) {
		PRNG = DEFAULT_PRNG;
		evaluation = new CityEval(this);
		subNet = new SubwayNetwork(this);
		init(width, height);
		initTileBehaviors();
	}

	protected void init(int width, int height) {
		map = new CityMap(width, height);
		powerMap = new MapBase<>(MapPosition.at(width, height), ()->false);
		CityMap m = new CityMap(1, 1);

		int hX = (width + 1) / 2;
		int hY = (height + 1) / 2;

		landValueMem = new int[hY][hX];
		pollutionMem = new int[hY][hX];
		crimeMem = new int[hY][hX];
		popDensity = new int[hY][hX];
		trfDensity = new int[hY][hX];

		int qX = (width + 3) / 4;
		int qY = (height + 3) / 4;

		terrainMem = new int[qY][qX];

		int smX = (width + 7) / 8;
		int smY = (height + 7) / 8;

		rateOGMem = new int[smY][smX];
		fireStMap = new int[smY][smX];
		policeMap = new int[smY][smX];
		policeMapEffect = new int[smY][smX];
		fireRate = new int[smY][smX];
		comRate = new int[smY][smX];

		centerMassX = hX;
		centerMassY = hY;
	}

	public void addSubwayStation(MapPosition pos) {
		subNet.addStation(pos);
	}

	public void setSubwayPercent(double newSubPerc) {
		subPercent = newSubPerc;
	}

	public double getSubwayPercent() {
		return subPercent;
	}

	public double getRoadPercent() {
		return roadPercent;
	}

	public void setRoadPercent(double roadPercent) {
		this.roadPercent = roadPercent;
	}

	public double getPolicePercent() {
		return policePercent;
	}

	public void setPolicePercent(double policePercent) {
		this.policePercent = policePercent;
	}

	public double getFirePercent() {
		return firePercent;
	}

	public void setFirePercent(double firePercent) {
		this.firePercent = firePercent;
	}

	public int getCityTax() {
		return cityTax;
	}

	public void setCityTax(int cityTax) {
		this.cityTax = cityTax;
	}

	public int findNearestTileFromRange(MapPosition pos, int lowTile, int highTile) {
		int distance = 999;

		for (int xInd = Math.max(0, pos.getX() - 5); xInd <= Math.min(getWidth() - 1, pos.getX() + 5); xInd++) {
			for (int yInd = Math.max(0, pos.getY() - 5); yInd <= Math.min(getHeight() - 1, pos.getY() + 5); yInd++) {
				int tile = getTile(xInd, yInd);
				if (lowTile <= tile && tile < highTile) {
					distance = Math.min(distance, pos.getDistanceToPos(xInd, yInd));
				}
			}
		}
		return distance;
	}

	public void spend(int amount) {
		budget.totalFunds -= amount;
		fireFundsChanged();
	}

	public Levels getGameLevel() {
		return gameLevel;
	}

	void fireCensusChanged() {
		for (Listener l : listeners) {
			l.censusChanged();
		}
	}

	void fireCityMessage(MicropolisMessage message, MapPosition loc) {
		for (Listener l : listeners) {
			l.cityMessage(message, loc);
		}
	}

	void fireCitySound(Sound sound, MapPosition loc) {
		for (Listener l : listeners) {
			l.citySound(sound, loc);
		}
	}

	void fireDemandChanged() {
		for (Listener l : listeners) {
			l.demandChanged();
		}
	}

	void fireEarthquakeStarted() {
		for (EarthquakeListener l : earthquakeListeners) {
			l.earthquakeStarted();
		}
	}

	void fireEvaluationChanged() {
		for (Listener l : listeners) {
			l.evaluationChanged();
		}
	}

	void fireFundsChanged() {
		for (Listener l : listeners) {
			l.fundsChanged();
		}
	}

	void fireMapOverlayDataChanged(MapState overlayDataType) {
		for (MapListener l : mapListeners) {
			l.mapOverlayDataChanged(overlayDataType);
		}
	}

	void fireOptionsChanged() {
		for (Listener l : listeners) {
			l.optionsChanged();
		}
	}

	void fireSpriteMoved(Sprite sprite) {
		for (MapListener l : mapListeners) {
			l.spriteMoved(sprite);
		}
	}

	void fireTileChanged(int xpos, int ypos) {
		for (MapListener l : mapListeners) {
			l.tileChanged(xpos, ypos);
		}
	}

	void fireWholeMapChanged() {
		for (MapListener l : mapListeners) {
			l.wholeMapChanged();
		}
	}

	ArrayList<Listener> listeners = new ArrayList<Listener>();
	ArrayList<MapListener> mapListeners = new ArrayList<MapListener>();
	ArrayList<EarthquakeListener> earthquakeListeners = new ArrayList<EarthquakeListener>();

	public void addListener(Listener l) {
		this.listeners.add(l);
	}

	public void removeListener(Listener l) {
		this.listeners.remove(l);
	}

	public void addEarthquakeListener(EarthquakeListener l) {
		this.earthquakeListeners.add(l);
	}

	public void removeEarthquakeListener(EarthquakeListener l) {
		this.earthquakeListeners.remove(l);
	}

	public void addMapListener(MapListener l) {
		this.mapListeners.add(l);
	}

	public void removeMapListener(MapListener l) {
		this.mapListeners.remove(l);
	}

	/**
	 * The listener interface for receiving miscellaneous events that occur in the
	 * Micropolis city. Use the Micropolis class's addListener interface to register
	 * an object that implements this interface.
	 */
	public interface Listener {
		void cityMessage(MicropolisMessage message, MapPosition loc);

		void citySound(Sound sound, MapPosition loc);

		/**
		 * Fired whenever the "census" is taken, and the various historical counters
		 * have been updated. (Once a month in game.)
		 */
		void censusChanged();

		/**
		 * Fired whenever resValve, comValve, or indValve changes. (Twice a month in
		 * game.)
		 */
		void demandChanged();

		/**
		 * Fired whenever the city evaluation is recalculated. (Once a year.)
		 */
		void evaluationChanged();

		/**
		 * Fired whenever the mayor's money changes.
		 */
		void fundsChanged();

		/**
		 * Fired whenever autoBulldoze, autoBudget, noDisasters, or simSpeed change.
		 */
		void optionsChanged();
	}

	public int getWidth() {
		return map.getDimension().getX();
	}

	public int getHeight() {
		return map.getDimension().getY();
	}

	public char getTile(int xpos, int ypos) {
		return (char) getTileRaw(MapPosition.at(xpos, ypos));
	}

	public char getTile(MapPosition pos) {
		return (char) getTileRaw(pos);
	}

	public char getTileRaw(MapPosition pos) {
		return (char) map.getTileNr(pos);
	}

	public char getTileRaw(int xpos, int ypos) {
		return getTileRaw(MapPosition.at(xpos, ypos));
	}

	public boolean isTileDozeable(ToolEffectIfc eff) {
		int myTile = eff.getTile(0, 0);
		TileSpec ts = Tiles.get(myTile);
		if (ts.canBulldoze) {
			return true;
		}

		if (ts.owner != null) {
			// part of a zone; only bulldozeable if the owner tile is
			// no longer intact.

			int baseTile = eff.getTile(-ts.ownerOffsetX, -ts.ownerOffsetY);
			return !(ts.owner.tileNumber == baseTile);
		}

		return false;
	}

	public boolean isTileDozeable(int xpos, int ypos) {
		return isTileDozeable(new ToolEffect(this, xpos, ypos));
	}

	public boolean isTilePowered(int xpos, int ypos) {
		return map.isPowered(MapPosition.at(xpos, ypos));
	}

	/**
	 * Note: this method clears the PWRBIT of the given location.
	 */
	public void setTile(MapPosition pos, TileSpec newTile) {
		if (map.setSpec(pos, newTile)) {
			fireTileChanged(pos.getX(), pos.getY());
		}
	}

	/*
	 * @deprecated
	 */
	// TODO refactor to MapPosition
	public void setTile(int x, int y, int newTile) {
		setTile(MapPosition.at(x, y), Tiles.get(newTile));
	}

	public void setTile(MapPosition pos, int newTile) {
		setTile(pos, Tiles.get(newTile));
	}

	public void setTilePower(MapPosition pos, boolean power) {
		if (power)
			map.power(pos);
		else
			map.unpower(pos);
	}

	/*
	 * @deprecated
	 */
	// TODO refactor to MapPosition
	public void setTilePower(int x, int y, boolean power) {
		setTilePower(MapPosition.at(x, y), power);
	}

	/*
	 * @deprecated
	 */
	final public boolean testBounds(int xpos, int ypos) {
		// TODO refactor to MapPosition and map.isInside()
		return testBounds(MapPosition.at(xpos, ypos));
	}

	// TODO refactor to MapPosition and map.isInside()
	final public boolean testBounds(MapPosition pos) {
		return map.isPosInside(pos);
	}

	public final void setPower(MapPosition pos, boolean hasPower) {
		powerMap.putAt(pos, hasPower);
	}

	public final boolean hasPower(int x, int y) {
		return hasPower(MapPosition.at(x, y));
	}

	public final boolean hasPower(MapPosition pos) {
		return powerMap.getAt(pos);
	}

	/**
	 * Checks whether the next call to animate() will collect taxes and process the
	 * budget.
	 */
	public boolean isBudgetTime() {
		return (cityTime != 0 && (cityTime % TAXFREQ) == 0 && ((fcycle + 1) % 16) == 10 && ((acycle + 1) % 2) == 0);
	}

	void step() {
		fcycle = (fcycle + 1) % 1024;
		simulate(fcycle % 16);
	}

	void clearCensus() {
		poweredZoneCount = 0;
		unpoweredZoneCount = 0;
		fireCounter = 0;
		roadTotal = 0;
		railTotal = 0;
		resPop = 0;
		comPop = 0;
		indPop = 0;
		resZoneCount = 0;
		comZoneCount = 0;
		indZoneCount = 0;
		hospitalCount = 0;
		churchCount = 0;
		policeCount = 0;
		fireStationCount = 0;
		subStationCount = 0;
		stadiumCount = 0;
		coalCount = 0;
		nuclearCount = 0;
		seaportCount = 0;
		airportCount = 0;
		powerPlants.clear();

		for (int y = 0; y < fireStMap.length; y++) {
			for (int x = 0; x < fireStMap[y].length; x++) {
				fireStMap[y][x] = 0;
				policeMap[y][x] = 0;
			}
		}
	}

	void simulate(int mod16) {
		final int band = getWidth() / 8;

		switch (mod16) {
		case 0:
			scycle = (scycle + 1) % 1024;
			cityTime++;
			if (scycle % 2 == 0) {
				setValves();
			}
			clearCensus();
			break;

		case 1:
			mapScan(0 * band, 1 * band);
			break;

		case 2:
			mapScan(1 * band, 2 * band);
			break;

		case 3:
			mapScan(2 * band, 3 * band);
			break;

		case 4:
			mapScan(3 * band, 4 * band);
			break;

		case 5:
			mapScan(4 * band, 5 * band);
			break;

		case 6:
			mapScan(5 * band, 6 * band);
			break;

		case 7:
			mapScan(6 * band, 7 * band);
			break;

		case 8:
			mapScan(7 * band, getWidth());
			break;

		case 9:
			if (cityTime % CENSUSRATE == 0) {
				takeCensus();

				if (cityTime % (CENSUSRATE * 12) == 0) {
					takeCensus2();
				}

				fireCensusChanged();
			}

			collectTaxPartial();

			if (cityTime % TAXFREQ == 0) {
				collectTax();
				evaluation.cityEvaluation();
			}
			break;

		case 10:
			if (scycle % 5 == 0) { // every ~10 weeks
				decROGMem();
			}
			decTrafficMem();
			fireMapOverlayDataChanged(MapState.TRAFFIC_OVERLAY); // TDMAP
			fireMapOverlayDataChanged(MapState.TRANSPORT); // RDMAP
			fireMapOverlayDataChanged(MapState.ALL); // ALMAP
			fireMapOverlayDataChanged(MapState.RESIDENTIAL); // REMAP
			fireMapOverlayDataChanged(MapState.COMMERCIAL); // COMAP
			fireMapOverlayDataChanged(MapState.INDUSTRIAL); // INMAP
			doMessages();
			break;

		case 11:
			doPowerScan();

			break;

		case 12:
			ptlScan();
			break;

		case 13:
			crimeScan();
			break;

		case 14:
			popDenScan();
			break;

		case 15:
			fireAnalysis();
			doDisasters();
			break;

		default:
			throw new Error("unreachable");
		}
	}

	private void doPowerScan() {
		PowerScanResult result = (new PowerScanner(powerPlants, this.getMap().getReadOnlyMap())).doScan();
		powerMap = result.getPowerMap();
		if (result.isHasBrownout())
			sendMessage(MicropolisMessage.BROWNOUTS_REPORT);
		fireMapOverlayDataChanged(MapState.POWER_OVERLAY);
		newPower = true;
	}

	private int computePopDen(int x, int y, char tile) {
		if (tile == RESCLR)
			return doFreePop(x, y);

		if (tile < COMBASE)
			return residentialZonePop(tile);

		if (tile < INDBASE)
			return commercialZonePop(tile) * 8;

		if (tile < PORTBASE)
			return industrialZonePop(tile) * 8;

		return 0;
	}

	private static int[][] doSmooth(int[][] tem) {
		final int h = tem.length;
		final int w = tem[0].length;
		int[][] tem2 = new int[h][w];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int z = tem[y][x];
				if (x > 0)
					z += tem[y][x - 1];
				if (x + 1 < w)
					z += tem[y][x + 1];
				if (y > 0)
					z += tem[y - 1][x];
				if (y + 1 < h)
					z += tem[y + 1][x];
				z /= 4;
				if (z > 255)
					z = 255;
				tem2[y][x] = z;
			}
		}

		return tem2;
	}

	public void calculateCenterMass() {
		popDenScan();
	}

	private void popDenScan() {
		int xtot = 0;
		int ytot = 0;
		int zoneCount = 0;
		int width = getWidth();
		int height = getHeight();
		int[][] tem = new int[(height + 1) / 2][(width + 1) / 2];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				char tile = getTile(x, y);
				if (isZoneCenter(tile)) {
					int den = computePopDen(x, y, tile) * 8;
					if (den > 254)
						den = 254;
					tem[y / 2][x / 2] = den;
					xtot += x;
					ytot += y;
					zoneCount++;
				}
			}
		}

		tem = doSmooth(tem);
		tem = doSmooth(tem);
		tem = doSmooth(tem);

		for (int x = 0; x < (width + 1) / 2; x++) {
			for (int y = 0; y < (height + 1) / 2; y++) {
				popDensity[y][x] = 2 * tem[y][x];
			}
		}

		distIntMarket(); // set ComRate

		// find center of mass for city
		if (zoneCount != 0) {
			centerMassX = xtot / zoneCount;
			centerMassY = ytot / zoneCount;
		} else {
			centerMassX = (width + 1) / 2;
			centerMassY = (height + 1) / 2;
		}

		fireMapOverlayDataChanged(MapState.POPDEN_OVERLAY); // PDMAP
		fireMapOverlayDataChanged(MapState.GROWTHRATE_OVERLAY); // RGMAP
	}

	private void distIntMarket() {
		for (int y = 0; y < comRate.length; y++) {
			for (int x = 0; x < comRate[y].length; x++) {
				int z = getDisCC(x * 4, y * 4);
				z /= 4;
				z = 64 - z;
				comRate[y][x] = z;
			}
		}
	}

	// tends to empty RateOGMem[][]
	private void decROGMem() {
		for (int y = 0; y < rateOGMem.length; y++) {
			for (int x = 0; x < rateOGMem[y].length; x++) {
				int z = rateOGMem[y][x];
				if (z == 0)
					continue;

				if (z > 0) {
					rateOGMem[y][x]--;
					if (z > 200) {
						rateOGMem[y][x] = 200; // prevent overflow?
					}
					continue;
				}

				if (z < 0) {
					rateOGMem[y][x]++;
					if (z < -200) {
						rateOGMem[y][x] = -200;
					}
					continue;
				}
			}
		}
	}

	// tends to empty trfDensity
	private void decTrafficMem() {
		for (int y = 0; y < trfDensity.length; y++) {
			for (int x = 0; x < trfDensity[y].length; x++) {
				int z = trfDensity[y][x];
				if (z != 0) {
					if (z > 200)
						trfDensity[y][x] = z - 34;
					else if (z > 24)
						trfDensity[y][x] = z - 24;
					else
						trfDensity[y][x] = 0;
				}
			}
		}
	}

	void crimeScan() {
		policeMap = smoothFirePoliceMap(policeMap);
		policeMap = smoothFirePoliceMap(policeMap);
		policeMap = smoothFirePoliceMap(policeMap);

		for (int sy = 0; sy < policeMap.length; sy++) {
			for (int sx = 0; sx < policeMap[sy].length; sx++) {
				policeMapEffect[sy][sx] = policeMap[sy][sx];
			}
		}

		int count = 0;
		int sum = 0;
		int cmax = 0;
		for (int hy = 0; hy < landValueMem.length; hy++) {
			for (int hx = 0; hx < landValueMem[hy].length; hx++) {
				int val = landValueMem[hy][hx];
				if (val != 0) {
					count++;
					int newCrimeLevel = 128 - val + popDensity[hy][hx];
					newCrimeLevel = Math.min(300, newCrimeLevel);
					newCrimeLevel -= policeMap[hy / 4][hx / 4];
					newCrimeLevel = Math.min(250, newCrimeLevel);
					newCrimeLevel = Math.max(0, newCrimeLevel);
					crimeMem[hy][hx] = newCrimeLevel;

					sum += newCrimeLevel;
					if (newCrimeLevel > cmax || (newCrimeLevel == cmax && PRNG.nextInt(4) == 0)) {
						cmax = newCrimeLevel;
						crimeMaxLocationX = hx * 2;
						crimeMaxLocationY = hy * 2;
					}
				} else {
					crimeMem[hy][hx] = 0;
				}
			}
		}

		if (count != 0)
			crimeAverage = sum / count;
		else
			crimeAverage = 0;

		fireMapOverlayDataChanged(MapState.POLICE_OVERLAY);
	}

	void doDisasters() {
		if (floodCnt > 0) {
			floodCnt--;
		}

		// final int [] DisChance = { 480, 240, 60 };
		if (noDisasters)
			return;

		if (PRNG.nextInt(gameLevel.getDisiasterChance() + 1) != 0)
			return;

		switch (PRNG.nextInt(9)) {
		case 0:
		case 1:
			setFire();
			break;
		case 2:
		case 3:
			makeFlood();
			break;
		case 4:
			break;
		case 5:
			makeTornado();
			break;
		case 6:
			makeEarthquake();
			break;
		case 7:
		case 8:
			if (pollutionAverage > 60) {
				makeMonster();
			}
			break;
		}
	}

	public void addFireStationMapValue(int x, int y, int value) {
		fireStMap[y][x] += value;
	}

	public void addPoliceStationMapValue(int x, int y, int value) {
		policeMap[y][x] += value;
	}

	private int[][] smoothFirePoliceMap(int[][] omap) {
		int smX = omap[0].length;
		int smY = omap.length;
		int[][] nmap = new int[smY][smX];
		for (int sy = 0; sy < smY; sy++) {
			for (int sx = 0; sx < smX; sx++) {
				int edge = 0;
				if (sx > 0) {
					edge += omap[sy][sx - 1];
				}
				if (sx + 1 < smX) {
					edge += omap[sy][sx + 1];
				}
				if (sy > 0) {
					edge += omap[sy - 1][sx];
				}
				if (sy + 1 < smY) {
					edge += omap[sy + 1][sx];
				}
				edge = edge / 4 + omap[sy][sx];
				nmap[sy][sx] = edge / 2;
			}
		}
		return nmap;
	}

	public int getPoliceEffect() {
		return policeEffect;
	}

	public int getFireEffect() {
		return fireEffect;
	}

	public int getHospitalCount() {
		return hospitalCount;
	}

	public int getChurchCount() {
		return churchCount;
	}

	public int getStadiumCount() {
		return stadiumCount;
	}

	public int getNuclearCount() {
		return nuclearCount;
	}

	public int getSeaportCount() {
		return seaportCount;
	}

	public int getAirportCount() {
		return airportCount;
	}

	public int getPoliceCount() {
		return policeCount;
	}

	public int getFireStationCount() {
		return fireStationCount;
	}

	public int getSubStationCount() {
		return subStationCount;
	}

	public void incHospitalCount() {
		hospitalCount++;
	}

	public void incChurchCount() {
		churchCount++;
	}

	public void incPoliceCount() {
		policeCount++;
	}

	public void incFireStationCount() {
		fireStationCount++;
	}

	public void incSubwayStationCount() {
		subStationCount++;
	}

	public void incStadiumCount() {
		stadiumCount++;
	}

	public void incNuclearCount() {
		nuclearCount++;
	}

	public void incSeaportCount() {
		seaportCount++;
	}

	public void incAirportCount() {
		airportCount++;
	}

	public int getCoalCount() {
		return coalCount;
	}

	public void incCoalCount() {
		this.coalCount++;
	}

	public int getFireCount() {
		return fireCounter;
	}

	public void reportActiveFire() {
		fireCounter++;
	}

	void fireAnalysis() {
		fireStMap = smoothFirePoliceMap(fireStMap);
		fireStMap = smoothFirePoliceMap(fireStMap);
		fireStMap = smoothFirePoliceMap(fireStMap);
		for (int sy = 0; sy < fireStMap.length; sy++) {
			for (int sx = 0; sx < fireStMap[sy].length; sx++) {
				fireRate[sy][sx] = fireStMap[sy][sx];
			}
		}

		fireMapOverlayDataChanged(MapState.FIRE_OVERLAY);
	}

	// private boolean testForCond(MapPosition pos, StepDir dir) {
	// boolean rv = false;
	// MapPosition stepPos = pos.step(dir);
	// if (map.isPosInside(stepPos)){
	//// if (movePowerLocation(pos, dir)) {
	// char t = getTile(stepPos);
	// rv = (isConductive(t) && t != NUCLEAR && t != POWERPLANT &&
	// !hasPower(stepPos));
	// }
	//
	// return rv;
	// }
	//
	// private boolean movePowerLocation(CityLocation loc, int dir) {
	// switch (dir) {
	// case 0:
	// if (loc.y > 0) {
	// loc.y--;
	// return true;
	// } else
	// return false;
	// case 1:
	// if (loc.x + 1 < getWidth()) {
	// loc.x++;
	// return true;
	// } else
	// return false;
	// case 2:
	// if (loc.y + 1 < getHeight()) {
	// loc.y++;
	// return true;
	// } else
	// return false;
	// case 3:
	// if (loc.x > 0) {
	// loc.x--;
	// return true;
	// } else
	// return false;
	// case 4:
	// return true;
	// }
	// return false;
	// }

	// void powerScan() {
	// // clear powerMap
	// for (boolean[] bb : powerMap) {
	// Arrays.fill(bb, false);
	// }
	// System.out.println("power Scan");
	// //
	// // Note: brownouts are based on total number of power plants, not the number
	// // of powerplants connected to your city.
	// //
	//
	// int maxPower = coalCount * 700 + nuclearCount * 2000;
	// int numPower = 0;
	//
	// // This is kind of odd algorithm, but I haven't the heart to rewrite it at
	// // this time.
	//
	// while (!powerPlants.isEmpty()) {
	// MapPosition pos = powerPlants.pop();
	//
	// StepDir aDir = StepDir.none;
	// int conNum;
	// do {
	// if (++numPower > maxPower) {
	// // trigger notification
	// sendMessage(MicropolisMessage.BROWNOUTS_REPORT);
	// return;
	// }
	//// System.out.print("Step from " + loc + " in dir " + aDir);
	// pos=pos.step(aDir);
	// System.out.println("stepped to " + pos);
	// setPower(pos, true);
	//
	// conNum = 0;
	// List<StepDir> dirsToCheck=StepDir.majorDirsAndNone();
	// while (!dirsToCheck.isEmpty() && conNum < 2) {
	//// System.out.println("currently at " + loc + " checking dir: " + dir);
	// if (testForCond(pos, dirsToCheck.get(0))) {
	//// System.out.println("dir ok");
	// conNum++;
	// aDir = dirsToCheck.get(0);
	// } else {
	//// System.out.println("dir not ok");
	// }
	// dirsToCheck.remove(0);
	// }
	// if (conNum > 1) {
	// System.out.println("power plant add at " + pos + " " + powerPlants);
	// powerPlants.add(pos);
	// }
	// } while (conNum != 0);
	// }
	// }

	/**
	 * Increase the traffic-density measurement at a particular spot.
	 * 
	 * @param traffic
	 *            the amount to add to the density
	 */
	void addTraffic(int mapX, int mapY, int traffic) {
		int z = trfDensity[mapY / 2][mapX / 2];
		z += traffic;

		// FIXME- why is this only capped to 240
		// by random chance. why is there no cap
		// the rest of the time?

		if (z > 240 && PRNG.nextInt(6) == 0) {
			z = 240;
			trafficMaxLocationX = mapX;
			trafficMaxLocationY = mapY;

			HelicopterSprite copter = (HelicopterSprite) getSprite(SpriteKind.COP);
			if (copter != null) {
				copter.destX = mapX;
				copter.destY = mapY;
			}
		}

		trfDensity[mapY / 2][mapX / 2] = z;
	}

	/** Accessor method for fireRate[]. */
	public int getFireStationCoverage(int xpos, int ypos) {
		return fireRate[ypos / 8][xpos / 8];
	}

	/** Accessor method for landValueMem overlay. */
	public int getLandValue(int xpos, int ypos) {
		if (testBounds(xpos, ypos)) {
			return landValueMem[ypos / 2][xpos / 2];
		} else {
			return 0;
		}
	}

	public int getTrafficDensity(int xpos, int ypos) {
		if (testBounds(xpos, ypos)) {
			return trfDensity[ypos / 2][xpos / 2];
		} else {
			return 0;
		}
	}

	public void setTrafficDensity(int xpos, int ypos, int newTrfDensity) {
		if (testBounds(xpos, ypos)) {
			trfDensity[ypos / 2][xpos / 2] = newTrfDensity;
		}
	}

	// power, terrain, land value
	void ptlScan() {
		final int qX = (getWidth() + 3) / 4;
		final int qY = (getHeight() + 3) / 4;
		int[][] qtem = new int[qY][qX];

		int landValueTotal = 0;
		int landValueCount = 0;

		final int HWLDX = (getWidth() + 1) / 2;
		final int HWLDY = (getHeight() + 1) / 2;
		int[][] tem = new int[HWLDY][HWLDX];
		for (int x = 0; x < HWLDX; x++) {
			for (int y = 0; y < HWLDY; y++) {
				int plevel = 0;
				int lvflag = 0;
				int zx = 2 * x;
				int zy = 2 * y;

				for (int mx = zx; mx <= zx + 1; mx++) {
					for (int my = zy; my <= zy + 1; my++) {
						int tile = getTile(mx, my);
						if (tile != DIRT) {
							if (tile < RUBBLE) // natural land features
							{
								// inc terrainMem
								qtem[y / 2][x / 2] += 15;
								continue;
							}
							plevel += getPollutionValue(tile);
							if (isConstructed(tile))
								lvflag++;
						}
					}
				}

				if (plevel < 0)
					plevel = 250; // ?

				if (plevel > 255)
					plevel = 255;

				tem[y][x] = plevel;

				if (lvflag != 0) {
					// land value equation

					int dis = 34 - getDisCC(x, y);
					dis *= 4;
					dis += terrainMem[y / 2][x / 2];
					dis -= pollutionMem[y][x];
					if (crimeMem[y][x] > 190) {
						dis -= 20;
					}
					if (dis > 250)
						dis = 250;
					if (dis < 1)
						dis = 1;
					landValueMem[y][x] = dis;
					landValueTotal += dis;
					landValueCount++;
				} else {
					landValueMem[y][x] = 0;
				}
			}
		}

		landValueAverage = landValueCount != 0 ? (landValueTotal / landValueCount) : 0;

		tem = doSmooth(tem);
		tem = doSmooth(tem);

		int pcount = 0;
		int ptotal = 0;
		int pmax = 0;
		for (int x = 0; x < HWLDX; x++) {
			for (int y = 0; y < HWLDY; y++) {
				int z = tem[y][x];
				pollutionMem[y][x] = z;

				if (z != 0) {
					pcount++;
					ptotal += z;

					if (z > pmax || (z == pmax && PRNG.nextInt(4) == 0)) {
						pmax = z;
						pollutionMaxLocationX = 2 * x;
						pollutionMaxLocationY = 2 * y;
					}
				}
			}
		}

		pollutionAverage = pcount != 0 ? (ptotal / pcount) : 0;

		terrainMem = smoothTerrain(qtem);

		fireMapOverlayDataChanged(MapState.POLLUTE_OVERLAY); // PLMAP
		fireMapOverlayDataChanged(MapState.LANDVALUE_OVERLAY); // LVMAP
	}

	public MapPosition getLocationOfMaxPollution() {
		return MapPosition.at(pollutionMaxLocationX, pollutionMaxLocationY);
	}

	// static final int [] TaxTable = {
	// 200, 150, 120, 100, 80, 50, 30, 0, -10, -40, -100,
	// -150, -200, -250, -300, -350, -400, -450, -500, -550, -600 };

	public static class History {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + cityTime;
			result = prime * result + Arrays.hashCode(com);
			result = prime * result + comMax;
			result = prime * result + Arrays.hashCode(crime);
			result = prime * result + Arrays.hashCode(ind);
			result = prime * result + indMax;
			result = prime * result + Arrays.hashCode(money);
			result = prime * result + Arrays.hashCode(pollution);
			result = prime * result + Arrays.hashCode(res);
			result = prime * result + resMax;
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
			History other = (History) obj;
			// if (cityTime != other.cityTime)
			// return false;
			if (!Arrays.equals(com, other.com))
				return false;
			// if (comMax != other.comMax)
			// return false;
			if (!Arrays.equals(crime, other.crime))
				return false;
			if (!Arrays.equals(ind, other.ind))
				return false;
			// if (indMax != other.indMax)
			// return false;
			if (!Arrays.equals(money, other.money))
				return false;
			if (!Arrays.equals(pollution, other.pollution))
				return false;
			if (!Arrays.equals(res, other.res))
				return false;
			// if (resMax != other.resMax)
			// return false;
			return true;
		}

		public int cityTime;
		public int[] res = new int[240];
		public int[] com = new int[240];
		public int[] ind = new int[240];
		public int[] money = new int[240];
		public int[] pollution = new int[240];
		public int[] crime = new int[240];
		int resMax;
		int comMax;
		int indMax;
	}

	public History history = new History();

	void setValves() {
		double normResPop = (double) resPop / 8.0;
		totalPop = (int) (normResPop + comPop + indPop);

		double employment;
		if (normResPop != 0.0) {
			employment = (history.com[1] + history.ind[1]) / normResPop;
		} else {
			employment = 1;
		}

		double migration = normResPop * (employment - 1);
		final double BIRTH_RATE = 0.02;
		double births = (double) normResPop * BIRTH_RATE;
		double projectedResPop = normResPop + migration + births;

		double temp = (history.com[1] + history.ind[1]);
		double laborBase;
		if (temp != 0.0) {
			laborBase = history.res[1] / temp;
		} else {
			laborBase = 1;
		}

		// clamp laborBase to between 0.0 and 1.3
		laborBase = Math.max(0.0, Math.min(1.3, laborBase));

		double internalMarket = (double) (normResPop + comPop + indPop) / 3.7;
		double projectedComPop = internalMarket * laborBase;

		// int z = gameLevel;
		temp = gameLevel.getLaborBaseMulti();

		double projectedIndPop = indPop * laborBase * temp;
		if (projectedIndPop < 5.0)
			projectedIndPop = 5.0;

		double resRatio;
		if (normResPop != 0) {
			resRatio = (double) projectedResPop / (double) normResPop;
		} else {
			resRatio = 1.3;
		}

		double comRatio;
		if (comPop != 0)
			comRatio = (double) projectedComPop / (double) comPop;
		else
			comRatio = projectedComPop;

		double indRatio;
		if (indPop != 0)
			indRatio = (double) projectedIndPop / (double) indPop;
		else
			indRatio = projectedIndPop;

		if (resRatio > 2.0)
			resRatio = 2.0;

		if (comRatio > 2.0)
			comRatio = 2.0;

		if (indRatio > 2.0)
			indRatio = 2.0;

		resRatio = (resRatio - 1) * 600 + gameLevel.getTaxModifier(taxEffect);
		comRatio = (comRatio - 1) * 600 + gameLevel.getTaxModifier(taxEffect);
		indRatio = (indRatio - 1) * 600 + gameLevel.getTaxModifier(taxEffect);

		// ratios are velocity changes to valves
		resValve += (int) resRatio;
		comValve += (int) comRatio;
		indValve += (int) indRatio;

		if (resValve > 2000)
			resValve = 2000;
		else if (resValve < -2000)
			resValve = -2000;

		if (comValve > 1500)
			comValve = 1500;
		else if (comValve < -1500)
			comValve = -1500;

		if (indValve > 1500)
			indValve = 1500;
		else if (indValve < -1500)
			indValve = -1500;

		if (resCap && resValve > 0) {
			// residents demand stadium
			resValve = 0;
		}

		if (comCap && comValve > 0) {
			// commerce demands airport
			comValve = 0;
		}

		if (indCap && indValve > 0) {
			// industry demands sea port
			indValve = 0;
		}

		fireDemandChanged();
	}

	int[][] smoothTerrain(int[][] qtem) {
		final int QWX = qtem[0].length;
		final int QWY = qtem.length;

		int[][] mem = new int[QWY][QWX];
		for (int y = 0; y < QWY; y++) {
			for (int x = 0; x < QWX; x++) {
				int z = 0;
				if (x > 0)
					z += qtem[y][x - 1];
				if (x + 1 < QWX)
					z += qtem[y][x + 1];
				if (y > 0)
					z += qtem[y - 1][x];
				if (y + 1 < QWY)
					z += qtem[y + 1][x];
				mem[y][x] = z / 4 + qtem[y][x] / 2;
			}
		}
		return mem;
	}

	// calculate manhatten distance (in 2-units) from center of city
	// capped at 32
	int getDisCC(int x, int y) {
		assert x >= 0 && x <= getWidth() / 2;
		assert y >= 0 && y <= getHeight() / 2;

		int xdis = Math.abs(x - centerMassX / 2);
		int ydis = Math.abs(y - centerMassY / 2);

		int z = (xdis + ydis);
		if (z > 32)
			return 32;
		else
			return z;
	}

	Map<String, TileBehavior> tileBehaviors;

	void initTileBehaviors() {
		HashMap<String, TileBehavior> bb;
		bb = new HashMap<String, TileBehavior>();

		bb.put("FIRE", new Fire(this));
		bb.put("FLOOD", new Flood(this));
		bb.put("RADIOACTIVE", new Radioactive(this));
		bb.put("ROAD", new Road(this));
		bb.put("RAIL", new Rail(this));
		bb.put("EXPLOSION", new Explosion(this));
		bb.put("RESIDENTIAL", new Residential(this));
		bb.put("HOSPITAL_CHURCH", new HospitalChurch(this));
		bb.put("COMMERCIAL", new Commercial(this));
		bb.put("INDUSTRIAL", new Industrial(this));
		bb.put("COAL", new CoalPower(this));
		bb.put("NUCLEAR", new NuclearPower(this));
		bb.put("FIRESTATION", new FireStation(this));
		bb.put("POLICESTATION", new PoliceStation(this));
		bb.put("STADIUM_EMPTY", new StadiumEmpty(this));
		bb.put("STADIUM_FULL", new StadiumFull(this));
		bb.put("AIRPORT", new Airport(this));
		bb.put("SEAPORT", new Seaport(this));
		bb.put("ICERINK", new IceRink(this));
		bb.put("SUBWAY", new Subway(this));

		this.tileBehaviors = bb;
	}

	void mapScan(int x0, int x1) {
		for (int x = x0; x < x1; x++) {
			for (int y = 0; y < getHeight(); y++) {
				mapScanTile(x, y);
			}
		}
	}

	void mapScanTile(int xpos, int ypos) {
		int tile = getTile(xpos, ypos);
		String behaviorStr = getTileBehavior(tile);
		if (behaviorStr == null) {
			return; // nothing to do
		}

		TileBehavior b = tileBehaviors.get(behaviorStr);
		if (b != null) {
			b.processTile(xpos, ypos);
		} else {
			throw new Error("Unknown behavior: " + behaviorStr);
		}
	}

	public void generateShip() {
		if (hasSprite(SpriteKind.SHI)) // maximum one ship
			return;

		int edge = PRNG.nextInt(4);

		if (edge == 0) {
			for (int x = 4; x < getWidth() - 2; x++) {
				if (getTile(x, 0) == CHANNEL) {
					makeShipAt(x, 0, ShipSprite.NORTH_EDGE);
					return;
				}
			}
		} else if (edge == 1) {
			for (int y = 1; y < getHeight() - 2; y++) {
				if (getTile(0, y) == CHANNEL) {
					makeShipAt(0, y, ShipSprite.EAST_EDGE);
					return;
				}
			}
		} else if (edge == 2) {
			for (int x = 4; x < getWidth() - 2; x++) {
				if (getTile(x, getHeight() - 1) == CHANNEL) {
					makeShipAt(x, getHeight() - 1, ShipSprite.SOUTH_EDGE);
					return;
				}
			}
		} else {
			for (int y = 1; y < getHeight() - 2; y++) {
				if (getTile(getWidth() - 1, y) == CHANNEL) {
					makeShipAt(getWidth() - 1, y, ShipSprite.EAST_EDGE);
					return;
				}
			}
		}
	}

	Sprite getSprite(SpriteKind kind) {
		for (Sprite s : sprites) {
			if (s.kind == kind)
				return s;
		}
		return null;
	}

	boolean hasSprite(SpriteKind kind) {
		return getSprite(kind) != null;
	}

	void makeShipAt(int xpos, int ypos, int edge) {
		assert !hasSprite(SpriteKind.SHI);

		sprites.add(new ShipSprite(this, xpos, ypos, edge));
	}

	public void generateCopter(int xpos, int ypos) {
		if (!hasSprite(SpriteKind.COP)) {
			sprites.add(new HelicopterSprite(this, xpos, ypos));
		}
	}

	public void generatePlane(int xpos, int ypos) {
		if (!hasSprite(SpriteKind.AIR)) {
			sprites.add(new AirplaneSprite(this, xpos, ypos));
		}
	}

	public void generateTrain(int xpos, int ypos) {
		if (totalPop > 20 && !hasSprite(SpriteKind.TRA) && PRNG.nextInt(26) == 0) {
			sprites.add(new TrainSprite(this, xpos, ypos));
		}
	}

	private Stack<MapPosition> powerPlants = new Stack<>();

	public void addPowerPlant(MapPosition pos) {
		powerPlants.add(pos);
	}

	// counts the population in a certain type of residential zone
	public int doFreePop(int xpos, int ypos) {
		int count = 0;

		for (int x = xpos - 1; x <= xpos + 1; x++) {
			for (int y = ypos - 1; y <= ypos + 1; y++) {
				if (testBounds(x, y)) {
					char loc = getTile(x, y);
					if (loc >= LHTHR && loc <= HHTHR)
						count++;
				}
			}
		}

		return count;
	}

	// called every several cycles; this takes the census data collected in this
	// cycle and records it to the history
	//
	void takeCensus() {
		int resMax = 0;
		int comMax = 0;
		int indMax = 0;

		for (int i = 118; i >= 0; i--) {
			if (history.res[i] > resMax)
				resMax = history.res[i];
			if (history.com[i] > comMax)
				comMax = history.res[i];
			if (history.ind[i] > indMax)
				indMax = history.ind[i];

			history.res[i + 1] = history.res[i];
			history.com[i + 1] = history.com[i];
			history.ind[i + 1] = history.ind[i];
			history.crime[i + 1] = history.crime[i];
			history.pollution[i + 1] = history.pollution[i];
			history.money[i + 1] = history.money[i];
		}

		history.resMax = resMax;
		history.comMax = comMax;
		history.indMax = indMax;

		// graph10max = Math.max(resMax, Math.max(comMax, indMax));

		history.res[0] = resPop / 8;
		history.com[0] = comPop;
		history.ind[0] = indPop;

		crimeRamp += (crimeAverage - crimeRamp) / 4;
		history.crime[0] = Math.min(255, crimeRamp);

		polluteRamp += (pollutionAverage - polluteRamp) / 4;
		history.pollution[0] = Math.min(255, polluteRamp);

		int moneyScaled = cashFlow / 20 + 128;
		if (moneyScaled < 0)
			moneyScaled = 0;
		if (moneyScaled > 255)
			moneyScaled = 255;
		history.money[0] = moneyScaled;

		history.cityTime = cityTime;

		if (hospitalCount < resPop / 256) {
			needHospital = TypeDemand.increase;
		} else if (hospitalCount > resPop / 256) {
			needHospital = TypeDemand.decrease;
		} else {
			needHospital = TypeDemand.dontChange;
		}

		if (churchCount < resPop / 256) {
			needChurch = TypeDemand.increase;
		} else if (churchCount > resPop / 256) {
			needChurch = TypeDemand.decrease;
		} else {
			needChurch = TypeDemand.dontChange;
		}
	}

	void takeCensus2() {
		// update long term graphs
		int resMax = 0;
		int comMax = 0;
		int indMax = 0;

		for (int i = 238; i >= 120; i--) {
			if (history.res[i] > resMax)
				resMax = history.res[i];
			if (history.com[i] > comMax)
				comMax = history.res[i];
			if (history.ind[i] > indMax)
				indMax = history.ind[i];

			history.res[i + 1] = history.res[i];
			history.com[i + 1] = history.com[i];
			history.ind[i + 1] = history.ind[i];
			history.crime[i + 1] = history.crime[i];
			history.pollution[i + 1] = history.pollution[i];
			history.money[i + 1] = history.money[i];
		}

		history.res[120] = resPop / 8;
		history.com[120] = comPop;
		history.ind[120] = indPop;
		history.crime[120] = history.crime[0];
		history.pollution[120] = history.pollution[0];
		history.money[120] = history.money[0];
	}

	/**
	 * Road/rail maintenance cost multiplier, for various difficulty settings.
	 */
	// static final double [] RLevels = { 0.7, 0.9, 1.2 };

	/**
	 * Tax income multiplier, for various difficulty settings.
	 */
	// static final double [] FLevels = { 1.4, 1.2, 0.8 };

	void collectTaxPartial() {
		lastRoadTotal = roadTotal;
		lastRailTotal = railTotal;
		lastTotalPop = totalPop;
		lastFireStationCount = fireStationCount;
		lastPoliceCount = policeCount;

		BudgetNumbers b = generateBudget();

		budget.taxFund += b.taxIncome;
		budget.roadFundEscrow -= b.roadFunded;
		budget.fireFundEscrow -= b.fireFunded;
		budget.policeFundEscrow -= b.policeFunded;

		taxEffect = b.taxRate;
		roadEffect = b.roadRequest != 0 ? (int) Math.floor(32.0 * (double) b.roadFunded / (double) b.roadRequest) : 32;
		policeEffect = b.policeRequest != 0
				? (int) Math.floor(1000.0 * (double) b.policeFunded / (double) b.policeRequest)
				: 1000;
		fireEffect = b.fireRequest != 0 ? (int) Math.floor(1000.0 * (double) b.fireFunded / (double) b.fireRequest)
				: 1000;
	}

	public static class FinancialHistory {
		public int cityTime;
		public int totalFunds;
		public int taxIncome;
		public int operatingExpenses;
	}

	public ArrayList<FinancialHistory> financialHistory = new ArrayList<FinancialHistory>();

	void collectTax() {
		int revenue = budget.taxFund / TAXFREQ;
		int expenses = -(budget.roadFundEscrow + budget.fireFundEscrow + budget.policeFundEscrow) / TAXFREQ;

		FinancialHistory hist = new FinancialHistory();
		hist.cityTime = cityTime;
		hist.taxIncome = revenue;
		hist.operatingExpenses = expenses;

		cashFlow = revenue - expenses;
		spend(-cashFlow);

		hist.totalFunds = budget.totalFunds;
		financialHistory.add(0, hist);

		budget.taxFund = 0;
		budget.roadFundEscrow = 0;
		budget.fireFundEscrow = 0;
		budget.policeFundEscrow = 0;
	}

	/** Annual maintenance cost of each police station. */
	static final int POLICE_STATION_MAINTENANCE = 100;

	/** Annual maintenance cost of each fire station. */
	static final int FIRE_STATION_MAINTENANCE = 100;

	/**
	 * Calculate the current budget numbers.
	 */
	public BudgetNumbers generateBudget() {
		BudgetNumbers b = new BudgetNumbers();
		b.taxRate = Math.max(0, cityTax);
		b.roadPercent = Math.max(0.0, roadPercent);
		b.subPercent = Math.max(0.0, subPercent);
		b.firePercent = Math.max(0.0, firePercent);
		b.policePercent = Math.max(0.0, policePercent);

		b.previousBalance = budget.totalFunds;
		b.taxIncome = (int) Math
				.round(lastTotalPop * landValueAverage / 120 * b.taxRate * gameLevel.getTaxIncomeMulti());
		assert b.taxIncome >= 0;

		b.roadRequest = (int) Math.round((lastRoadTotal + lastRailTotal * 2) * gameLevel.getTrafficMaintenanceMulti());
		// TODO: ConnectionPrice depending on connection length
		b.subRequest = subNet.getMaintenanceCost();
		System.out.println(subNet);

		b.fireRequest = FIRE_STATION_MAINTENANCE * lastFireStationCount;
		b.policeRequest = POLICE_STATION_MAINTENANCE * lastPoliceCount;

		b.roadFunded = (int) Math.round(b.roadRequest * b.roadPercent);
		b.subFunded = (int) Math.round(b.subRequest * b.subPercent);
		b.fireFunded = (int) Math.round(b.fireRequest * b.firePercent);
		b.policeFunded = (int) Math.round(b.policeRequest * b.policePercent);

		int yumDuckets = budget.totalFunds + b.taxIncome;
		assert yumDuckets >= 0;
		if (yumDuckets >= b.roadFunded) {
			yumDuckets -= b.roadFunded;

			if (yumDuckets >= b.subFunded) {
				yumDuckets -= b.subFunded;
				if (yumDuckets >= b.fireFunded) {
					yumDuckets -= b.fireFunded;
					if (yumDuckets >= b.policeFunded) {
						yumDuckets -= b.policeFunded;
					} else {
						assert b.policeRequest != 0;

						b.policeFunded = yumDuckets;
						b.policePercent = (double) b.policeFunded / (double) b.policeRequest;
						yumDuckets = 0;
					}
				} else {
					assert b.fireRequest != 0;

					b.fireFunded = yumDuckets;
					b.firePercent = (double) b.fireFunded / (double) b.fireRequest;
					b.policeFunded = 0;
					b.policePercent = 0.0;
					yumDuckets = 0;
				}
			} else {
				if (b.subRequest > 0) {
					b.subFunded = yumDuckets;
					b.subPercent = (double) b.subFunded / (double) b.subRequest;
					yumDuckets = b.fireFunded = b.policeFunded = 0;
					b.firePercent = b.policePercent = 0;
				}
			}

		} else {
			assert b.roadRequest != 0;

			b.roadFunded = yumDuckets;
			b.roadPercent = (double) b.roadFunded / (double) b.roadRequest;
			b.subFunded = 0;
			b.subPercent = 0;
			b.fireFunded = 0;
			b.firePercent = 0.0;
			b.policeFunded = 0;
			b.policePercent = 0.0;
		}

		b.operatingExpenses = b.roadFunded + b.subFunded + b.fireFunded + b.policeFunded;
		b.newBalance = b.previousBalance + b.taxIncome - b.operatingExpenses;

		return b;
	}

	public int getPopulationDensity(int xpos, int ypos) {
		return popDensity[ypos / 2][xpos / 2];
	}

	/*
	 * @deprecated
	 */
	// TODO refactor to MapPosition
	public void doMeltdown(int x, int y) {
		doMeltdown(MapPosition.at(x, y));
	}

	public void doMeltdown(MapPosition pos) {
		makeExplosion(pos.getX() - 1, pos.getY() - 1);
		makeExplosion(pos.getX() - 1, pos.getY() + 2);
		makeExplosion(pos.getX() + 2, pos.getY() - 1);
		makeExplosion(pos.getX() + 2, pos.getY() + 2);

		for (MapPosition aPos : pos.plus(-1, -1).getPosForRect(pos.plus(3, 3))) {
			setTile(aPos, Tiles.get(FIRE + PRNG.nextInt(4)));
		}

		for (int z = 0; z < 200; z++) {
			MapPosition newPos = MapPosition.at(pos.getX() - 20 + PRNG.nextInt(41), pos.getY() - 15 + PRNG.nextInt(31));
			if (!map.isPosInside(newPos))
				continue;

			int t = map.getTileNr(newPos);
			if (isZoneCenter(t)) {
				continue;
			}
			if (isCombustible(t) || t == DIRT) {
				setTile(newPos, Tiles.get(RADTILE));
			}
		}

		clearMes();
		sendMessageAt(MicropolisMessage.MELTDOWN_REPORT, pos.getX(), pos.getY());
	}

	static final int[] MltdwnTab = { 30000, 20000, 10000 };

	void loadHistoryArray(int[] array, DataInputStream dis) throws IOException {
		for (int i = 0; i < 240; i++) {
			array[i] = dis.readShort();
		}
	}

	void writeHistoryArray(int[] array, DataOutputStream out) throws IOException {
		for (int i = 0; i < 240; i++) {
			// System.out.println(array[i]);
			out.writeShort(array[i]);
		}
	}

	void loadMisc(DataInputStream dis) throws IOException {
		dis.readShort(); // [0]... ignored?
		dis.readShort(); // [1] externalMarket, ignored
		resPop = dis.readShort(); // [2-4] populations
		comPop = dis.readShort();
		indPop = dis.readShort();
		resValve = dis.readShort(); // [5-7] valves
		comValve = dis.readShort();
		indValve = dis.readShort();
		cityTime = dis.readInt(); // [8-9] city time
		crimeRamp = dis.readShort(); // [10]
		polluteRamp = dis.readShort();
		landValueAverage = dis.readShort(); // [12]
		crimeAverage = dis.readShort();
		pollutionAverage = dis.readShort(); // [14]
		gameLevel = Levels.getLevelByKey(dis.readShort());
		evaluation.cityClass = dis.readShort(); // [16]
		evaluation.cityScore = dis.readShort();

		for (int i = 18; i < 50; i++) {
			dis.readShort();
		}

		budget.totalFunds = dis.readInt(); // [50-51] total funds
		autoBulldoze = dis.readShort() != 0; // 52
		autoBudget = dis.readShort() != 0;
		autoGo = dis.readShort() != 0; // 54
		dis.readShort(); // userSoundOn (this setting not saved to game file
		// in this edition of the game)
		cityTax = dis.readShort(); // 56
		taxEffect = cityTax;
		int simSpeedAsInt = dis.readShort();
		if (simSpeedAsInt >= 0 && simSpeedAsInt <= 4)
			simSpeed = Speed.values()[simSpeedAsInt];
		else
			simSpeed = Speed.NORMAL;

		// read budget numbers, convert them to percentages
		//
		long n = dis.readInt(); // 58,59... police percent
		policePercent = (double) n / 65536.0;
		n = dis.readInt(); // 60,61... fire percent
		firePercent = (double) n / 65536.0;
		n = dis.readInt(); // 62,63... road percent
		roadPercent = (double) n / 65536.0;
		n = dis.readInt();
		subPercent = (double) n / 65536.0;

		short nrStations = dis.readShort();
		short nrConns = dis.readShort();

		for (int i = 0; i < nrStations; i++) {
			short x1 = dis.readShort();
			short y1 = dis.readShort();
			subNet.addStation(new MapPosition(x1, y1));
		}

		for (int i = 0; i < nrConns; i++) {
			short x1 = dis.readShort();
			short y1 = dis.readShort();
			short x2 = dis.readShort();
			short y2 = dis.readShort();
			SubwayStation stat1 = new SubwayStation(x1, y1);
			SubwayStation stat2 = new SubwayStation(x2, y2);
			subNet.connect(stat1, stat2);
		}

		for (int i = 68 + nrStations * 2 + nrConns * 4; i < 120; i++) {
			dis.readShort();
		}

		if (cityTime < 0) {
			cityTime = 0;
		}
		if (cityTax < 0 || cityTax > 20) {
			cityTax = 7;
		}
		// if (gameLevel < 0 || gameLevel > 2) { gameLevel = 0; }
		if (evaluation.cityClass < 0 || evaluation.cityClass > 5) {
			evaluation.cityClass = 0;
		}
		if (evaluation.cityScore < 1 || evaluation.cityScore > 999) {
			evaluation.cityScore = 500;
		}

		resCap = false;
		comCap = false;
		indCap = false;

		lastCityPop = calcPopulation();
	}

	void writeMisc(DataOutputStream out) throws IOException {
		out.writeShort(0);
		out.writeShort(0);
		out.writeShort(resPop);
		out.writeShort(comPop);
		out.writeShort(indPop);
		out.writeShort(resValve);
		out.writeShort(comValve);
		out.writeShort(indValve);
		// 8
		out.writeInt(cityTime);
		out.writeShort(crimeRamp);
		out.writeShort(polluteRamp);
		// 12
		out.writeShort(landValueAverage);
		out.writeShort(crimeAverage);
		out.writeShort(pollutionAverage);
		out.writeShort(gameLevel.getKey());
		// 16
		out.writeShort(evaluation.cityClass);
		out.writeShort(evaluation.cityScore);
		// 18
		for (int i = 18; i < 50; i++) {
			out.writeShort(0);
		}
		// 50
		out.writeInt(budget.totalFunds);
		out.writeShort(autoBulldoze ? 1 : 0);
		out.writeShort(autoBudget ? 1 : 0);
		// 54
		out.writeShort(autoGo ? 1 : 0);
		out.writeShort(1); // userSoundOn
		out.writeShort(cityTax);
		out.writeShort(simSpeed.ordinal());

		// 58
		out.writeInt((int) (policePercent * 65536));
		out.writeInt((int) (firePercent * 65536));
		out.writeInt((int) (roadPercent * 65536));
		out.writeInt((int) (subPercent * 65536));
		// 64 <==66
		out.writeShort(subNet.getSubStationCount());
		out.writeShort(subNet.getSubConnectionCount());
		// 68
		for (SubwayStation aStation : subNet.getStations()) {
			out.writeShort(aStation.getX());
			out.writeShort(aStation.getY());
		}
		for (SubwayConnection aConn : subNet.getConnections()) {
			out.writeShort(aConn.getStation1().getX());
			out.writeShort(aConn.getStation1().getY());
			out.writeShort(aConn.getStation2().getX());
			out.writeShort(aConn.getStation2().getY());
		}

		for (int i = 68 + subNet.getSubStationCount() * 2 + subNet.getSubConnectionCount() * 4; i < 120; i++) {
			out.writeShort(0);
		}
	}

	void loadMap(DataInputStream dis) throws IOException {
		for (int x = 0; x < DEFAULT_WIDTH; x++) {
			for (int y = 0; y < DEFAULT_HEIGHT; y++) {
				int z = dis.readShort();
				z &= ~(1024 | 2048 | 4096 | 8192 | 16384); // clear ZONEBIT,ANIMBIT,BULLBIT,BURNBIT,CONDBIT on import
				map.setSpec(MapPosition.at(x, y), Tiles.get(z));
			}
		}
	}

	void writeMap(DataOutputStream out) throws IOException {
		for (int x = 0; x < DEFAULT_WIDTH; x++) {
			for (int y = 0; y < DEFAULT_HEIGHT; y++) {
				int z = map.getTileNr(MapPosition.at(x, y));
				if (isConductive(z & LOMASK)) {
					z |= 16384; // synthesize CONDBIT on export
				}
				if (isCombustible(z & LOMASK)) {
					z |= 8192; // synthesize BURNBIT on export
				}
				if (isTileDozeable(x, y)) {
					z |= 4096; // synthesize BULLBIT on export
				}
				if (isAnimated(z & LOMASK)) {
					z |= 2048; // synthesize ANIMBIT on export
				}
				if (isZoneCenter(z & LOMASK)) {
					z |= 1024; // synthesize ZONEBIT
				}
				out.writeShort(z);
			}
		}
	}

	public void load(File filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		if (fis.getChannel().size() > 27120) {
			// some editions of the classic Simcity game
			// start the file off with a 128-byte header,
			// but otherwise use the same format as us,
			// so read in that 128-byte header and continue
			// as before.
			byte[] bbHeader = new byte[128];
			fis.read(bbHeader);
		}
		load(fis);
	}

	void checkPowerMap() {
		coalCount = map.getAllBuildingsOfType(BuildingType.coalPower).size();
		nuclearCount = map.getAllBuildingsOfType(BuildingType.nukePower).size();

		powerPlants.clear();
		for (MapPosition pos : map.getAllPowerPlantMapPos()) {
			powerPlants.add(pos);
		}

		doPowerScan();
	}

	public void load(InputStream inStream) throws IOException {
		DataInputStream dis = new DataInputStream(inStream);
		loadHistoryArray(history.res, dis);
		loadHistoryArray(history.com, dis);
		loadHistoryArray(history.ind, dis);
		loadHistoryArray(history.crime, dis);
		loadHistoryArray(history.pollution, dis);
		loadHistoryArray(history.money, dis);
		loadMisc(dis);
		loadMap(dis);
		dis.close();

		map.rebuildFromTiles();
		checkPowerMap();

		fireWholeMapChanged();
		fireDemandChanged();
		fireFundsChanged();
	}

	public void save(File filename) throws IOException {
		save(new FileOutputStream(filename));
	}

	public void save(OutputStream outStream) throws IOException {
		DataOutputStream out = new DataOutputStream(outStream);
		writeHistoryArray(history.res, out);
		writeHistoryArray(history.com, out);
		writeHistoryArray(history.ind, out);
		writeHistoryArray(history.crime, out);
		writeHistoryArray(history.pollution, out);
		writeHistoryArray(history.money, out);
		writeMisc(out);
		writeMap(out);
		out.close();
	}

	public void toggleAutoBudget() {
		autoBudget = !autoBudget;
		fireOptionsChanged();
	}

	public void toggleAutoBulldoze() {
		autoBulldoze = !autoBulldoze;
		fireOptionsChanged();
	}

	public void toggleDisasters() {
		noDisasters = !noDisasters;
		fireOptionsChanged();
	}

	public void setSpeed(Speed newSpeed) {
		simSpeed = newSpeed;
		fireOptionsChanged();
	}

	public void animate() {
		this.acycle = (this.acycle + 1) % 960;
		if (this.acycle % 2 == 0) {
			step();
		}
		moveObjects();
		animateTiles();
	}

	public Sprite[] allSprites() {
		return sprites.toArray(new Sprite[0]);
	}

	void moveObjects() {
		for (Sprite sprite : allSprites()) {
			sprite.move();

			if (sprite.frame == 0) {
				sprites.remove(sprite);
			}
		}
	}

	void animateTiles() {
		map.animate();
	}

	public Optional<Sprite> getVisibleBoatSprite() {
		for (Sprite s : sprites) {
			if (s.isVisible() && s.kind == SpriteKind.SHI) {
				return Optional.of(s);
			}
		}

		return Optional.empty();
	}

	public int[][] getComRate() {
		return comRate;
	}

	public void setComRate(int[][] comRate) {
		this.comRate = comRate;
	}

	public TypeDemand getNeedHospital() {
		return needHospital;
	}

	public void resetHospitalDemand() {
		needHospital = TypeDemand.dontChange;
	}

	public TypeDemand getNeedChurch() {
		return needChurch;
	}

	public void resetChurchDemand() {
		needChurch = TypeDemand.dontChange;
	}

	public int getResPop() {
		return resPop;
	}

	public void setResPop(int resPop) {
		this.resPop = resPop;
	}

	public int getComPop() {
		return comPop;
	}

	public void setComPop(int comPop) {
		this.comPop = comPop;
	}

	public int getIndPop() {
		return indPop;
	}

	public void setIndPop(int indPop) {
		this.indPop = indPop;
	}

	public void setResValve(int resValve) {
		this.resValve = resValve;
	}

	public void setComValve(int comValve) {
		this.comValve = comValve;
	}

	public void setIndValve(int indValve) {
		this.indValve = indValve;
	}

	public int getResZoneCount() {
		return resZoneCount;
	}

	public void incResZoneCount() {
		this.resZoneCount++;
	}

	public int getComZoneCount() {
		return comZoneCount;
	}

	public void incComZoneCount() {
		this.comZoneCount++;
	}

	public int getIndZoneCount() {
		return indZoneCount;
	}

	public void incIndZoneCount() {
		this.indZoneCount++;
	}

	public CityMap getMap() {
		return map;
	}

	public int getCityPopulation() {
		return lastCityPop;
	}

	public void makeSound(int x, int y, Sound sound) {
		fireCitySound(sound, MapPosition.at(x, y));
	}

	public void makeEarthquake() {
		makeSound(centerMassX, centerMassY, Sound.EXPLOSION_LOW);
		fireEarthquakeStarted();

		sendMessageAt(MicropolisMessage.EARTHQUAKE_REPORT, centerMassX, centerMassY);
		int time = PRNG.nextInt(701) + 300;
		for (int z = 0; z < time; z++) {
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			assert testBounds(x, y);

			if (isVulnerable(getTile(x, y))) {
				if (PRNG.nextInt(4) != 0) {
					setTile(x, y, (char) (RUBBLE + PRNG.nextInt(4)));
				} else {
					setTile(x, y, (char) (FIRE + PRNG.nextInt(8)));
				}
			}
		}
	}

	void setFire() {
		int x = PRNG.nextInt(getWidth());
		int y = PRNG.nextInt(getHeight());
		int t = getTile(x, y);

		if (isArsonable(t)) {
			setTile(x, y, (char) (FIRE + PRNG.nextInt(8)));
			sendMessageAt(MicropolisMessage.FIRE_REPORT, x, y);
		}
	}

	public void makeFire() {
		// forty attempts at finding place to start fire
		for (int t = 0; t < 40; t++) {
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			int tile = getTile(x, y);
			if (!isZoneCenter(tile) && isCombustible(tile)) {
				if (tile > 21 && tile < LASTZONE) {
					setTile(x, y, (char) (FIRE + PRNG.nextInt(8)));
					sendMessageAt(MicropolisMessage.FIRE_REPORT, x, y);
					return;
				}
			}
		}
	}

	/**
	 * Force a meltdown to occur.
	 * 
	 * @return true if a metldown was initiated.
	 */
	public boolean makeMeltdown() {
		List<MapPosition> candidates = new ArrayList<>(map.getAllMapPosOfType(BuildingType.nukePower));

		if (candidates.isEmpty()) {
			// tell caller that no nuclear plants were found
			return false;
		}

		int i = PRNG.nextInt(candidates.size());
		MapPosition pos = candidates.get(i);
		doMeltdown(pos);
		return true;
	}

	public void makeMonster() {
		MonsterSprite monster = (MonsterSprite) getSprite(SpriteKind.GOD);
		if (monster != null) {
			// already have a monster in town
			monster.soundCount = 1;
			monster.count = 1000;
			monster.flag = false;
			monster.destX = pollutionMaxLocationX;
			monster.destY = pollutionMaxLocationY;
			return;
		}

		// try to find a suitable starting spot for monster

		for (int i = 0; i < 300; i++) {
			int x = PRNG.nextInt(getWidth() - 19) + 10;
			int y = PRNG.nextInt(getHeight() - 9) + 5;
			int t = getTile(x, y);
			if (t == RIVER) {
				makeMonsterAt(x, y);
				return;
			}
		}

		// no "nice" location found, just start in center of map then
		makeMonsterAt(getWidth() / 2, getHeight() / 2);
	}

	void makeMonsterAt(int xpos, int ypos) {
		assert !hasSprite(SpriteKind.GOD);
		sprites.add(new MonsterSprite(this, xpos, ypos));
		sendMessageAt(MicropolisMessage.MONSTER_REPORT, xpos, ypos);
	}

	public void makeTornado() {
		TornadoSprite tornado = (TornadoSprite) getSprite(SpriteKind.TOR);
		if (tornado != null) {
			// already have a tornado, so extend the length of the
			// existing tornado
			tornado.count = 200;
			return;
		}

		// FIXME- this is not exactly like the original code
		int xpos = PRNG.nextInt(getWidth() - 19) + 10;
		int ypos = PRNG.nextInt(getHeight() - 19) + 10;
		sprites.add(new TornadoSprite(this, xpos, ypos));
		sendMessageAt(MicropolisMessage.TORNADO_REPORT, xpos, ypos);
	}

	public void makeFlood() {
		final int[] DX = { 0, 1, 0, -1 };
		final int[] DY = { -1, 0, 1, 0 };

		for (int z = 0; z < 300; z++) {
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			int tile = getTile(x, y);
			if (isRiverEdge(tile)) {
				for (int t = 0; t < 4; t++) {
					int xx = x + DX[t];
					int yy = y + DY[t];
					MapPosition newPos = MapPosition.at(xx, yy);
					if (map.isPosInside(newPos)) {
						int c = map.getSpec(newPos).getTileNr();
						if (isFloodable(c)) {
							setTile(xx, yy, FLOOD);
							floodCnt = 30;
							sendMessageAt(MicropolisMessage.FLOOD_REPORT, xx, yy);
							floodX = xx;
							floodY = yy;
							return;
						}
					}
				}
			}
		}
	}

	public boolean isCurrentlyFlooded() {
		return floodCnt > 0;
	}

	/**
	 * Makes all component tiles of a zone bulldozable. Should be called whenever
	 * the key zone tile of a zone is destroyed, since otherwise the user would no
	 * longer have a way of destroying the zone.
	 * 
	 * @see #shutdownZone
	 */
	public void killZone(int xpos, int ypos, int zoneTile) {
		rateOGMem[ypos / 8][xpos / 8] -= 20;

		assert isZoneCenter(zoneTile);
		CityDimension dim = getZoneSizeFor(zoneTile);
		assert dim != null;
		assert dim.width >= 3;
		assert dim.height >= 3;

		int zoneBase = (zoneTile & LOMASK) - 1 - dim.width;

		// this will take care of stopping smoke animations
		shutdownZone(xpos, ypos, dim);
	}

	/**
	 * If a zone has a different image (animation) for when it is powered, switch to
	 * that different image here. Note: pollution is not accumulated here; see
	 * ptlScan() instead.
	 * 
	 * @see #shutdownZone
	 */
	public void powerZone(int xpos, int ypos, CityDimension zoneSize) {
		assert zoneSize.width >= 3;
		assert zoneSize.height >= 3;

		for (int dx = 0; dx < zoneSize.width; dx++) {
			for (int dy = 0; dy < zoneSize.height; dy++) {
				int x = xpos - 1 + dx;
				int y = ypos - 1 + dy;
				int tile = getTileRaw(x, y);
				TileSpec ts = Tiles.get(tile & LOMASK);
				if (ts != null && ts.onPower != null) {
					setTile(x, y, (char) (ts.onPower.tileNumber | (tile & ALLBITS)));
				}
			}
		}
	}

	/**
	 * If a zone has a different image (animation) for when it is powered, switch
	 * back to the original image.
	 * 
	 * @see #powerZone
	 * @see #killZone
	 */
	public void shutdownZone(int xpos, int ypos, CityDimension zoneSize) {
		assert zoneSize.width >= 3;
		assert zoneSize.height >= 3;

		for (int dx = 0; dx < zoneSize.width; dx++) {
			for (int dy = 0; dy < zoneSize.height; dy++) {
				int x = xpos - 1 + dx;
				int y = ypos - 1 + dy;
				int tile = getTileRaw(x, y);
				TileSpec ts = Tiles.get(tile & LOMASK);
				if (ts != null && ts.onShutdown != null) {
					setTile(x, y, (char) (ts.onShutdown.tileNumber | (tile & ALLBITS)));
				}
			}
		}
	}

	public void makeExplosion(int xpos, int ypos) {
		makeExplosionAt(xpos * 16 + 8, ypos * 16 + 8);
	}

	/**
	 * Uses x,y coordinates as 1/16th-length tiles.
	 */
	void makeExplosionAt(int x, int y) {
		sprites.add(new ExplosionSprite(this, x, y));
	}

	void checkGrowth() {
		if (cityTime % 4 == 0) {
			int newPop = calcPopulation();
			if (lastCityPop != 0) {
				MicropolisMessage z = null;
				if (lastCityPop < 500000 && newPop >= 500000) {
					z = MicropolisMessage.POP_500K_REACHED;
				} else if (lastCityPop < 100000 && newPop >= 100000) {
					z = MicropolisMessage.POP_100K_REACHED;
				} else if (lastCityPop < 50000 && newPop >= 50000) {
					z = MicropolisMessage.POP_50K_REACHED;
				} else if (lastCityPop < 10000 && newPop >= 10000) {
					z = MicropolisMessage.POP_10K_REACHED;
				} else if (lastCityPop < 2000 && newPop >= 2000) {
					z = MicropolisMessage.POP_2K_REACHED;
				}
				if (z != null) {
					sendMessageAt(z, centerMassX, centerMassY);
				}
			}
			lastCityPop = newPop;
		}
	}

	int calcPopulation() {
		return (resPop + comPop * 8 + indPop * 8) * 20;
	}

	void doMessages() {
		// MORE (scenario stuff)

		checkGrowth();

		int totalZoneCount = resZoneCount + comZoneCount + indZoneCount;
		int powerCount = nuclearCount + coalCount;

		int z = cityTime % 64;
		switch (z) {
		case 1:
			if (totalZoneCount / 4 >= resZoneCount) {
				sendMessage(MicropolisMessage.NEED_RES);
			}
			break;
		case 5:
			if (totalZoneCount / 8 >= comZoneCount) {
				sendMessage(MicropolisMessage.NEED_COM);
			}
			break;
		case 10:
			if (totalZoneCount / 8 >= indZoneCount) {
				sendMessage(MicropolisMessage.NEED_IND);
			}
			break;
		case 14:
			if (totalZoneCount > 10 && totalZoneCount * 2 > roadTotal) {
				sendMessage(MicropolisMessage.NEED_ROADS);
			}
			break;
		case 18:
			if (totalZoneCount > 50 && totalZoneCount > railTotal) {
				sendMessage(MicropolisMessage.NEED_RAILS);
			}
			break;
		case 22:
			if (totalZoneCount > 10 && powerCount == 0) {
				sendMessage(MicropolisMessage.NEED_POWER);
			}
			break;
		case 26:
			resCap = (resPop > 500 && stadiumCount == 0);
			if (resCap) {
				sendMessage(MicropolisMessage.NEED_STADIUM);
			}
			break;
		case 28:
			indCap = (indPop > 70 && seaportCount == 0);
			if (indCap) {
				sendMessage(MicropolisMessage.NEED_SEAPORT);
			}
			break;
		case 30:
			comCap = (comPop > 100 && airportCount == 0);
			if (comCap) {
				sendMessage(MicropolisMessage.NEED_AIRPORT);
			}
			break;
		case 32:
			int TM = unpoweredZoneCount + poweredZoneCount;
			if (TM != 0) {
				if ((double) poweredZoneCount / (double) TM < 0.7) {
					sendMessage(MicropolisMessage.BLACKOUTS);
				}
			}
			break;
		case 35:
			if (pollutionAverage > 60) { // FIXME, consider changing threshold to 80
				sendMessageAt(MicropolisMessage.HIGH_POLLUTION, pollutionMaxLocationX, pollutionMaxLocationY);
			}
			break;
		case 42:
			if (crimeAverage > 100) {
				sendMessageAt(MicropolisMessage.HIGH_CRIME, crimeMaxLocationX, crimeMaxLocationY);
			}
			break;
		case 45:
			if (totalPop > 60 && fireStationCount == 0) {
				sendMessage(MicropolisMessage.NEED_FIRESTATION);
			}
			break;
		case 48:
			if (totalPop > 60 && policeCount == 0) {
				sendMessage(MicropolisMessage.NEED_POLICE);
			}
			break;
		case 51:
			if (cityTax > 12) {
				sendMessage(MicropolisMessage.HIGH_TAXES);
			}
			break;
		case 54:
			if (roadEffect < 20 && roadTotal > 30) {
				sendMessage(MicropolisMessage.ROADS_NEED_FUNDING);
			}
			break;
		case 57:
			if (fireEffect < 700 && totalPop > 20) {
				sendMessage(MicropolisMessage.FIRE_NEED_FUNDING);
			}
			break;
		case 60:
			if (policeEffect < 700 && totalPop > 20) {
				sendMessage(MicropolisMessage.POLICE_NEED_FUNDING);
			}
			break;
		case 63:
			if (trafficAverage > 60) {
				sendMessageAt(MicropolisMessage.HIGH_TRAFFIC, trafficMaxLocationX, trafficMaxLocationY);
			}
			break;
		default:
			// nothing
		}
	}

	void clearMes() {
		// TODO.
		// What this does in the original code is clears the 'last message'
		// properties, ensuring that the next message will be delivered even
		// if it is a repeat.
	}

	void sendMessage(MicropolisMessage message) {
		fireCityMessage(message, null);
	}

	void sendMessageAt(MicropolisMessage message, int x, int y) {
		fireCityMessage(message, MapPosition.at(x, y));
	}

	public ZoneStatus queryZoneStatus(int xpos, int ypos) {
		ZoneStatus zs = new ZoneStatus();
		zs.building = getDescriptionNumber(getTile(xpos, ypos));

		int z;
		z = (popDensity[ypos / 2][xpos / 2] / 64) % 4;
		zs.popDensity = z + 1;

		z = landValueMem[ypos / 2][xpos / 2];
		z = z < 30 ? 4 : z < 80 ? 5 : z < 150 ? 6 : 7;
		zs.landValue = z + 1;

		z = ((crimeMem[ypos / 2][xpos / 2] / 64) % 4) + 8;
		zs.crimeLevel = z + 1;

		z = Math.max(13, ((pollutionMem[ypos / 2][xpos / 2] / 64) % 4) + 12);
		zs.pollution = z + 1;

		z = rateOGMem[ypos / 8][xpos / 8];
		z = z < 0 ? 16 : z == 0 ? 17 : z <= 100 ? 18 : 19;
		zs.growthRate = z + 1;

		return zs;
	}

	public int getPoweredZoneCount() {
		return poweredZoneCount;
	}

	public void incPoweredZoneCount() {
		this.poweredZoneCount++;
	}

	public int getUnpoweredZoneCount() {
		return unpoweredZoneCount;
	}

	public void incUnpoweredZoneCount() {
		unpoweredZoneCount++;
	}

	public int getResValve() {
		return resValve;
	}

	public int getRoadEffect() {
		return roadEffect;
	}

	public int getRailTotal() {
		return railTotal;
	}

	public void incRailCounter() {
		railTotal++;
	}

	public int getRoadTotal() {
		return roadTotal;
	}

	public void incRoadCounter() {
		this.roadTotal++;
	}

	public void incRoadCounter(int inc) {
		this.roadTotal += inc;
	}

	public int getComValve() {
		return comValve;
	}

	public int getIndValve() {
		return indValve;
	}

	public void setGameLevel(Levels newLevel) {
		gameLevel = newLevel;
		fireOptionsChanged();
	}

	public void setFunds(int totalFunds) {
		budget.totalFunds = totalFunds;
	}

	public List<SubwayStation> getSubways() {
		ArrayList<SubwayStation> stations = new ArrayList<>();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (TileConstants.isSubway(getTile(x, y))) {
					stations.add(new SubwayStation(x, y));
				}
			}
		}
		return stations;
	}

	public SubwayNetwork getSubNet() {
		return subNet;
	}

}
