package micropolisj.engine.behaviour;

import static micropolisj.engine.TileConstants.CHURCH;
import static micropolisj.engine.TileConstants.HOSPITAL;
import static micropolisj.engine.TileConstants.RESCLR;

import micropolisj.engine.Micropolis;
import micropolisj.engine.TypeDemand;

public class HospitalChurch extends BuildingBehaviour {

	public HospitalChurch(Micropolis city) {
		super(city);
	}

	/**
	 * Called when the current tile is the key tile of a hospital or church.
	 */
	@Override
	protected void doBuildingBehaviour(boolean isPowered) {
		if (tile == HOSPITAL) {
			city.incHospitalCount();

			if (city.cityTime % 16 == 0) {
				repairZone(HOSPITAL, 3);
			}
			if (city.getNeedHospital() == TypeDemand.decrease) // too many hospitals
			{
				if (PRNG.nextInt(21) == 0) {
					zonePlop(RESCLR);
				}
			}
		} else if (tile == CHURCH) {
			city.incChurchCount();

			if (city.cityTime % 16 == 0) {
				repairZone(CHURCH, 3);
			}
			if (city.getNeedChurch() == TypeDemand.decrease) // too many churches
			{
				if (PRNG.nextInt(21) == 0) {
					zonePlop(RESCLR);
				}
			}
		}
	}

}
