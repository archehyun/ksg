package com.dtp.api.schedule.comparator;

import java.util.Comparator;

public class VesselComparator implements Comparator<IFComparator>{

	@Override
	public int compare(IFComparator o1, IFComparator o2) {
		return o1.getVesselName().compareTo(o2.getVesselName());
	}

}
