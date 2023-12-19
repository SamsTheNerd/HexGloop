package com.samsthenerd.hexgloop.utils.patternmatching;

import com.samsthenerd.hexgloop.utils.patternmatching.HexLine.HexPt;
import com.samsthenerd.hexgloop.utils.patternmatching.HexLine.Transformable;

public record HexSeg(Seg seg) implements Transformable<HexSeg> {
	private record Seg(HexPt root, HexDirM dir) {}

	public HexSeg(HexPt root, HexDirM dir) {
		// canonicalize for inner seg
		this((dir.basis ? new Seg(root, dir) : new Seg(root.offset(dir), dir.flip())));
	}

	@Override
	public HexSeg plus(HexPt other) {
		return new HexSeg(root().plus(other), dir());
	}

	@Override
	public HexSeg rotate(HexAngleM angle) {
		return new HexSeg(root().rotate(angle), dir().rotate(angle));
	}

	public HexPt root() {
		return seg.root;
	}
	public HexDirM dir() {
		return seg.dir;
	}
}
