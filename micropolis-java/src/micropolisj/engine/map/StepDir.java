package micropolisj.engine.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StepDir {
	none(0,0,MapPosition.at(0,0)),
	up (0,1,MapPosition.at(0,-1)),
	right (1,0,MapPosition.at(1,0)),
	down (0,-1,MapPosition.at(0,1)),
	left (-1,0,MapPosition.at(-1,0)),
	upleft (-1,1,MapPosition.at(-1,-1)),
	upright (1,1,MapPosition.at(1, -1)),
	downleft (-1,-1,MapPosition.at(-1, 1)),
	downright (1,-1,MapPosition.at(1, 1));
	
	public static List<StepDir> majorDirsAndNone(){
		return new ArrayList<>(Arrays.asList(StepDir.up,StepDir.right,StepDir.down,StepDir.left,StepDir.none));
	}
	
	private final int xVal,yVal;
	private final MapPosition pos;
	
	private StepDir(int xVal, int yVal,MapPosition pos) {
		this.xVal=xVal;
		this.yVal=yVal;
		this.pos=pos;
	}
	
	public int getXVal() {
		return xVal;
	}

	public int getYVal() {
		return yVal;
	}
	
	public MapPosition getPos() {
		return pos;
	}
}
