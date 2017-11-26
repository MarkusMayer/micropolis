// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import java.util.*;

/**
 * Contains the code for performing a city evaluation.
 */
public class CityEval
{
	private final Micropolis engine;
	private final Random PRNG;

	public CityEval(Micropolis engine)
	{
		this.engine = engine;
		this.PRNG = engine.PRNG;

		assert PRNG != null;
	}

	/** Percentage of population "approving" the mayor. Derived from cityScore. */
	public int cityYes;

	/** Percentage of population "disapproving" the mayor. Derived from cityScore. */
	public int cityNo;

	/** City assessment value. */
	public int cityAssValue;

	/** Player's score, 0-1000. */
	public int cityScore;

	/** Change in cityScore since last evaluation. */
	public int deltaCityScore;

	/** City population as of current evaluation. */
	public int cityPop;

	/** Change in cityPopulation since last evaluation. */
	public int deltaCityPop;

	/** Classification of city size. 0==village, 1==town, etc. */
	public int cityClass; // 0..5

	/** City's top 4 (or fewer) problems as reported by citizens. */
	public CityProblem [] problemOrder = new CityProblem[0];

	/** Number of votes given for the various problems identified by problemOrder[]. */
	public EnumMap<CityProblem,Integer> problemVotes = new EnumMap<CityProblem,Integer>(CityProblem.class);

	/** Score for various problems. */
	public EnumMap<CityProblem,Integer> problemTable = new EnumMap<CityProblem,Integer>(CityProblem.class);

	/**
	 * Perform an evaluation.
	 */
	void cityEvaluation()
	{
		if (engine.totalPop != 0) {
			calculateAssValue();
			doPopNum();
			doProblems();
			calculateScore();
			doVotes();
		} else {
			evalInit();
		}
		engine.fireEvaluationChanged();
	}

	/** Evaluate an empty city. */
	void evalInit()
	{
		cityYes = 0;
		cityNo = 0;
		cityAssValue = 0;
		cityClass = 0;
		cityScore = 500;
		deltaCityScore = 0;
		problemVotes.clear();
		problemOrder = new CityProblem[0];
	}

	void calculateAssValue()
	{
		int z = 0;
		z += engine.getRoadTotal() * 5;
		z += engine.getRailTotal() * 10;
		z += engine.getPoliceCount() * 1000;
		z += engine.getFireStationCount() * 1000;
		z += engine.getHospitalCount() * 400;
		z += engine.getStadiumCount() * 3000;
		z += engine.getSeaportCount() * 5000;
		z += engine.getAirportCount() * 10000;
		z += engine.getCoalCount() * 3000;
		z += engine.getNuclearCount() * 6000;
		cityAssValue = z * 1000;
	}

	void doPopNum()
	{
		int oldCityPop = cityPop;
		cityPop = engine.getCityPopulation();
		deltaCityPop = cityPop - oldCityPop;

		cityClass =
			cityPop > 500000 ? 5 :    //megalopolis
			cityPop > 100000 ? 4 :    //metropolis
			cityPop > 50000 ? 3 :     //capital
			cityPop > 10000 ? 2 :     //city
			cityPop > 2000 ? 1 :      //town
			0;                  //village
	}

	void doProblems()
	{
		problemTable.clear();
		problemTable.put(CityProblem.CRIME, engine.crimeAverage);
		problemTable.put(CityProblem.POLLUTION, engine.pollutionAverage);
		problemTable.put(CityProblem.HOUSING, (int)Math.round(engine.landValueAverage * 0.7));
		problemTable.put(CityProblem.TAXES, engine.cityTax * 10);
		problemTable.put(CityProblem.TRAFFIC, averageTrf());
		problemTable.put(CityProblem.UNEMPLOYMENT, getUnemployment());
		problemTable.put(CityProblem.FIRE, getFire());

		problemVotes = voteProblems(problemTable);

		CityProblem [] probOrder = CityProblem.values();
		Arrays.sort(probOrder, new Comparator<CityProblem>() {
			public int compare(CityProblem a, CityProblem b) {
				return -(problemVotes.get(a).compareTo(problemVotes.get(b)));
			}});

		int c = 0;
		while (c < probOrder.length &&
				problemVotes.get(probOrder[c]).intValue() != 0 &&
				c < 4)
			c++;

		problemOrder = new CityProblem[c];
		for (int i = 0; i < c; i++) {
			problemOrder[i] = probOrder[i];
		}
	}

	EnumMap<CityProblem,Integer> voteProblems(Map<CityProblem,Integer> probTab)
	{
		CityProblem [] pp = CityProblem.values();
		int [] votes = new int[pp.length];

		int countVotes = 0;
		for (int i = 0; i < 600; i++) {
			if (PRNG.nextInt(301) < probTab.get(pp[i%pp.length])) {
				votes[i%pp.length]++;
				countVotes++;
				if (countVotes >= 100)
					break;
			}
		}

		EnumMap<CityProblem,Integer> rv = new EnumMap<CityProblem,Integer>(CityProblem.class);
		for (int i = 0; i < pp.length; i++) {
			rv.put(pp[i], votes[i]);
		}
		return rv;
	}

	int averageTrf()
	{
		int count = 1;
		int total = 0;

		for (int y = 0; y < engine.getHeight(); y++) {
			for (int x = 0; x < engine.getWidth(); x++) {
				// only consider tiles that have nonzero landvalue
				if (engine.getLandValue(x, y) != 0) {
					total += engine.getTrafficDensity(x, y);
					count++;
				}
			}
		}

		engine.trafficAverage = (int)Math.round(((double)total / (double)count) * 2.4);
		return engine.trafficAverage;
	}

	int getUnemployment()
	{
		int b = (engine.getComPop() + engine.getIndPop()) * 8;
		if (b == 0)
			return 0;

		double r = (double)engine.getResPop() / (double)b;
		b = (int)Math.floor((r-1.0)*255);
		if (b > 255) {
			b = 255;
		}
		return b;
	}

	int getFire()
	{
		int z = engine.getFireCount() * 5;
		return Math.min(255, z);
	}

	static double clamp(double x, double min, double max)
	{
		return Math.max(min, Math.min(max, x));
	}

	void calculateScore()
	{
		int oldCityScore = cityScore;

		int x = 0;
		for (Integer z : problemTable.values()) {
			x += z.intValue();
		}

		x /= 3;
		x = Math.min(256, x);

		double z = clamp((256 - x) * 4, 0, 1000);

		if (engine.resCap) { z = 0.85 * z; }
		if (engine.comCap) { z = 0.85 * z; }
		if (engine.indCap) { z = 0.85 * z; }
		if (engine.getRoadEffect() < 32) { z -= (32 - engine.getRoadEffect()); }
		if (engine.getPoliceEffect() < 1000) { z *= (0.9 + (engine.getPoliceEffect() / 10000.1)); }
		if (engine.getFireEffect() < 1000) { z *= (0.9 + (engine.getFireEffect() / 10000.1)); }
		if (engine.getResValve() < -1000) { z *= 0.85; }
		if (engine.getComValve() < -1000) { z *= 0.85; }
		if (engine.getIndValve() < -1000) { z *= 0.85; }

		double SM = 1.0;
		if (cityPop == 0 && deltaCityPop == 0) {
			SM = 1.0;
		}
		else if (deltaCityPop == cityPop) {
			SM = 1.0;
		}
		else if (deltaCityPop > 0) {
			SM = (double)deltaCityPop / (double)cityPop + 1.0;
		}
		else if (deltaCityPop < 0) {
			SM = 0.95 + ((double)deltaCityPop / (double)(cityPop-deltaCityPop));
		}
		z *= SM;
		z -= getFire();
		z -= engine.cityTax;

		int TM = engine.getUnpoweredZoneCount() + engine.getPoweredZoneCount();
		SM = TM != 0 ? ((double)engine.getPoweredZoneCount() / (double)TM) : 1.0;
		z *= SM;

		z = clamp(z, 0, 1000);

		cityScore = (int)Math.round((cityScore + z) / 2.0);
		deltaCityScore = cityScore - oldCityScore;
	}

	void doVotes()
	{
		cityYes = cityNo = 0;
		for (int i = 0; i < 100; i++) {
			if (PRNG.nextInt(1001) < cityScore) {
				cityYes++;
			} else {
				cityNo++;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cityAssValue;
		result = prime * result + cityClass;
		result = prime * result + cityNo;
		result = prime * result + cityPop;
		result = prime * result + cityScore;
		result = prime * result + cityYes;
		result = prime * result + deltaCityPop;
		result = prime * result + deltaCityScore;
		result = prime * result + Arrays.hashCode(problemOrder);
		result = prime * result + ((problemTable == null) ? 0 : problemTable.hashCode());
		result = prime * result + ((problemVotes == null) ? 0 : problemVotes.hashCode());
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
		CityEval other = (CityEval) obj;
//		if (cityAssValue != other.cityAssValue)
//			return false;
		if (cityClass != other.cityClass)
			return false;
//		if (cityNo != other.cityNo)
//			return false;
//		if (cityPop != other.cityPop)
//			return false;
////XX		if (cityScore != other.cityScore)
////XX			return false;
//		if (cityYes != other.cityYes)
//			return false;
//		if (deltaCityPop != other.deltaCityPop)
//			return false;
//		if (deltaCityScore != other.deltaCityScore)
//			return false;
//		if (!Arrays.equals(problemOrder, other.problemOrder))
//			return false;
//		if (problemTable == null) {
//			if (other.problemTable != null)
//				return false;
//		} else if (!problemTable.equals(other.problemTable))
//			return false;
//		if (problemVotes == null) {
//			if (other.problemVotes != null)
//				return false;
//		} else if (!problemVotes.equals(other.problemVotes))
//			return false;
		return true;
	}
	
}
