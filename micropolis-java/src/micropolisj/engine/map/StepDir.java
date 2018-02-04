package micropolisj.engine.map;

public enum StepDir {
	up (0,1),
	right (1,0),
	down (0,-1),
	left (-1,0);
	
	private final int xVal,yVal;
	
	private StepDir(int xVal, int yVal) {
		this.xVal=xVal;
		this.yVal=yVal;
	}
	
	public int getXVal() {
		return xVal;
	}

	public int getYVal() {
		return yVal;
	}
}
