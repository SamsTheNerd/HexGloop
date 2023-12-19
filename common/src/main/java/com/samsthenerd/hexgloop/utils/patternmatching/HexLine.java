package com.samsthenerd.hexgloop.utils.patternmatching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.samsthenerd.hexgloop.utils.patternmatching.HexBB.Interval;

// this whole folder is just yoinked from alwinfy https://gist.github.com/Alwinfy/7168c35ba59e9da21515671a4547966d
public record HexLine(HexPt origin, List<HexDirM> steps) {
	public HexLine(List<HexDirM> steps) {
		this(HexPt.ORIGIN, steps);
	}

	public static HexLine parse(HexDirM start, String turnString) {
		var list = new ArrayList<HexDirM>();
		list.add(start);
		for (var ch : turnString.toCharArray()) {
			list.add(start = start.rotate(HexAngleM.parse(ch)));
		}
		return new HexLine(list);
	}

	public static HexLine parse(String turnString) {
		return parse(HexDirM.EAST, turnString);
	}

	public Set<HexPt> ptCloud() {
		var set = new HashSet<HexPt>();
		var point = origin;
		set.add(point);
		for (var step : steps) {
			set.add(point = point.offset(step));
		}
		return set;
	}

	public Set<HexSeg> segs() {
		var set = new HashSet<HexSeg>();
		var point = origin;
		for (var step : steps) {
			set.add(new HexSeg(point, step));
			point = point.offset(step);
		}
		return set;
	}

	public HexBB boundingBox() {
		var qint = new Interval.Build(origin.q());
		var rint = new Interval.Build(origin.r());
		var sint = new Interval.Build(origin.s());
		var point = origin;
		for (var step : steps) {
			point = point.offset(step);
			qint.update(point.q());
			rint.update(point.r());
			sint.update(point.s());
		}
		return new HexBB(qint.build(), rint.build(), sint.build());
	}

	/** Return a transform from the other hexline to our hexline such that the strokes overlap,
	 * or empty if the strokes don't match.
	 */
	public Optional<HexTrans> equalModStrokes(HexLine other) {
		if (steps.size() != other.steps().size()) {
			return Optional.empty();
		}
		var myBB = boundingBox();
		var theirBB = other.boundingBox();
		var mySegs = segs();
		var theirSegs = other.segs();
		// iterate over rotations:
		for (var rotation : HexAngleM.values()) {
			var newBB = theirBB.rotate(rotation);
			// if the bbs are equal up to translation:
			if (myBB.equalModTrans(newBB)) {
				// transform their bounding box to our bounding box
				var trans = new HexTrans(rotation, myBB.q2r().minus(newBB.q2r()));
				var transformedSegs = theirSegs.stream().map(trans::app).collect(Collectors.toSet());
				if (mySegs.equals(transformedSegs)) {
					return Optional.of(trans);
				}
			}
		}
		return Optional.empty();
	}

    public static record HexPt(int q, int r) implements Transformable<HexPt> {
        public int s() {
            return -q - r;
        }
    
        public HexPt neg() {
            return new HexPt(-q, -r);
        }
    
        @Override
        public HexPt plus(HexPt other) {
            return new HexPt(q + other.q, r + other.r);
        }
    
        public HexPt minus(HexPt other) {
            return new HexPt(q - other.q, r - other.r);
        }
    
        public HexPt offset(HexDirM other) {
            return plus(other.offset);
        }
    
        public static final HexPt ORIGIN = new HexPt(0, 0);
    
        @Override
        public HexPt rotate(HexAngleM angle) {
            int nq = q, nr = r;
            // TODO match for this
            for (var i = 0; i < angle.count; i++) {
                int nr2 = nr;
                nr = -nq;
                nq += nr2;
            }
            return new HexPt(nq, nr);
        }
    }

    public static interface Transformable<T extends Transformable<T>> {
        T rotate(HexAngleM angle);
        T plus(HexPt offset);
    }

    /** Rotation followed by a translation. */
    public record HexTrans(HexAngleM rot, HexPt trans) {

        public <T extends Transformable<T>> T app(Transformable<T> in) {
            return in.rotate(rot).plus(trans);
        }

        public HexTrans compose(HexTrans before) {
            return new HexTrans(
                rot.plus(before.rot),
                before.trans.rotate(rot).plus(trans)
            );
        }

        public HexTrans neg() {
            var negRot = rot.neg();
            return new HexTrans(
                negRot,
                trans.neg().rotate(negRot)
            );
        }

        public static final HexTrans IDENTITY = new HexTrans(HexAngleM.FORWARD, HexPt.ORIGIN);
    }
}

