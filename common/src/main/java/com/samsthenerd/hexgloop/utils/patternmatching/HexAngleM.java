package com.samsthenerd.hexgloop.utils.patternmatching;

public enum HexAngleM {
    FORWARD(0, 'w'),
	LEFT(1, 'q'),
	SHARP_LEFT(2, 'a'),
	BACKWARD(3, 's'),
	SHARP_RIGHT(4, 'd'),
	RIGHT(5, 'e');

	public final int count;
	public final char name;

	HexAngleM(int count, char name) {
		this.count = count;
		this.name = name;
	}

	public HexAngleM plus(HexAngleM other) {
		return HexAngleM.values()[(count + other.count) % 6];
	}
	public HexAngleM neg() {
		return HexAngleM.values()[(6 - count) % 6];
	}

	public HexAngleM minus(HexAngleM other) {
		return HexAngleM.values()[(6 + count - other.count) % 6];
	}

	public static HexAngleM parse(char in) {
		for (var angle : HexAngleM.values()) {
			if (angle.name == in) {
				return angle;
			}
		}
		throw new IllegalArgumentException("Bad angle char: " + in);
	}
}
