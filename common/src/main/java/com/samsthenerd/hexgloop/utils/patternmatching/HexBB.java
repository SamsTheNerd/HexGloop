package com.samsthenerd.hexgloop.utils.patternmatching;

import com.samsthenerd.hexgloop.utils.patternmatching.HexLine.HexPt;
import com.samsthenerd.hexgloop.utils.patternmatching.HexLine.Transformable;

public record HexBB(Interval q, Interval r, Interval s) implements Transformable<HexBB> {

	// return the point at min q, max r
	public HexPt q2r() {
		return new HexPt(q.min(), r.max());
	}
	// return the point at min r, max s
	public HexPt r2s() {
		return new HexPt(-s.max() - r.min(), r.min());
	}
	// return the point at min s, max q
	public HexPt s2q() {
		return new HexPt(q.max(), -s.min() - q.max());
	}
	// the three above functions can be converted back and forth to the interval repr
	// we store as interval bc they're easier to rotate,
	// but equalModTrans can test distances between the points for easier comparison

	@Override
	public HexBB rotate(HexAngleM angle) {
		Interval q = q(), r = r(), s = s();
		for (var i = 0; i < angle.count; i++) {
			var q2 = q;
			q = s.flip();
			s = r.flip();
			r = q2.flip();
		}
		return new HexBB(q, r, s);
	}

	@Override
	public HexBB plus(HexPt in) {
		return new HexBB(q.plus(in.q()), r.plus(in.r()), s.plus(in.s()));
	}

	public boolean equalModTrans(HexBB other) {
		var r2s = r2s();
		var or2s = other.r2s();
		return r2s.minus(q2r()).equals(or2s.minus(other.q2r())) && r2s.minus(s2q()).equals(or2s.minus(other.s2q()));
	}

    public static record Interval(int min, int max) {
        public Interval {
            if (min > max) {
                throw new IllegalArgumentException("Tried to create interval [" + min + ", " + max + "]!");
            }
        }
    
        public static class Build {
            private int min;
            private int max;
            public Build(int value) {
                min = max = value;
            }
            public void update(int value) {
                min = Math.min(value, min);
                max = Math.max(value, max);
            }
            public Interval build() {
                return new Interval(min, max);
            }
        }
    
        public int range() {
            return max - min;
        }
        public Interval plus(int val) {
            return new Interval(min + val, max + val);
        }
        public Interval flip() {
            return new Interval(-max, -min);
        }
    }
}
