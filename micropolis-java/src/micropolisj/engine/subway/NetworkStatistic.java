package micropolisj.engine.subway;

public class NetworkStatistic {

	private int totalRide, totalRequest, periodRide, periodRequest;

	void incRide() {
		totalRide++;
		periodRide++;
	}

	void incRequest() {
		totalRequest++;
		periodRequest++;
	}

	int getTotalRide() {
		return totalRide;
	}

	int getTotalRequest() {
		return totalRequest;
	}

	int getPeriodRide() {
		return periodRide;
	}

	int getPeriodRequest() {
		return periodRequest;
	}

	double getPeriodRideRequestRatio() {
		return periodRide > 0 ? (periodRequest / periodRide) : 0;
	}

	double getTotalRideRequestRatio() {
		return totalRide > 0 ? (totalRequest / totalRide) : 0;
	}

	void startNewPeriod() {
		periodRequest = 0;
		periodRide = 0;
	}

	@Override
	public String toString() {
		return "NetworkStatistic [totalRide=" + totalRide + ", totalRequest=" + totalRequest + ", periodRide="
				+ periodRide + ", periodRequest=" + periodRequest + "]";
	}
}
