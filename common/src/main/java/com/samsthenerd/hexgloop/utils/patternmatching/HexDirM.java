package com.samsthenerd.hexgloop.utils.patternmatching;

import com.samsthenerd.hexgloop.utils.patternmatching.HexLine.HexPt;

public enum HexDirM {
	EAST(0, true),
	NORTHEAST(1, true),
	NORTHWEST(2, true),
	WEST(3, false),
	SOUTHWEST(4, false),
	SOUTHEAST(5, false);

	public final int value;
	public final boolean basis;
	public final HexPt offset;

	HexDirM(int value, boolean basis) {
		this.value = value;
		this.basis = basis;
		this.offset = new HexPt(1, 0).rotate(HexAngleM.values()[value]);
	}

	public HexDirM rotate(HexAngleM angle) {
		return HexDirM.values()[(this.value + angle.count) % 6];
	}

	public HexDirM flip() {
		return HexDirM.values()[(this.value + 3) % 6];
	}
}
