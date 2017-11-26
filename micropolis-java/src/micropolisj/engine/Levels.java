package micropolisj.engine;

public enum Levels {
	
	easy 		(20000,0,0,480,	0.7,1.4,1.2,	30000),
	medium 		(10000,1,1,240,	0.9,1.2,1.1,	20000),
	hard 		( 5000,2,2,60,	1.2,0.8,0.98,	10000),
	superEasy 	(50000,3,0,1000,0.7,1.4,1.2,	100000);

	
	private static final int [] TaxTable = {
			200, 150, 120, 100, 80, 50, 30, 0, -10, -40, -100,
			-150, -200, -250, -300, -350, -400, -450, -500, -550, -600 };

	int startingFund, key,disasterChance,meltdownRand,taxTableShift;
	double trafficMaintenanceMulti,taxIncomeMulti,laborBaseMulti;
	
	private Levels(int startingFund, int key,int taxTableShift,int disasterChance, double trafficMaintenanceMulti, double taxIncomeMulti,double laborBaseMulti,int meltdownRand) {
		this.startingFund=startingFund;
		this.key=key;
		this.taxTableShift=taxTableShift;
		this.disasterChance=disasterChance;
		this.trafficMaintenanceMulti=trafficMaintenanceMulti;
		this.taxIncomeMulti=taxIncomeMulti;
		this.laborBaseMulti=laborBaseMulti;
		this.meltdownRand=meltdownRand;
	}

	public int getStartingFunds() {
		return startingFund;
	}

	public double getTrafficMaintenanceMulti() {
		return trafficMaintenanceMulti;
	}

	public double getTaxIncomeMulti() {
		return taxIncomeMulti;
	}

	public int getKey() {
		return key;
	}
	
	public int getDisiasterChance() {
		return disasterChance;
	}
	
	public double getLaborBaseMulti() {
		return laborBaseMulti;
	}

	public int getTaxModifier(int taxRate) {
		int idx=Math.min(taxRate+taxTableShift,20);
		return TaxTable[idx];
	};
	
	public int getMeltdownRand () {
		return this.meltdownRand;
	}
	
	public static Levels getLevelByKey(int key) {
		for (Levels level:Levels.values()) {
			if (level.getKey()==key) 
				return level;
		}
		
		throw new IllegalArgumentException("No Level found for key: "+key);
	}
}
