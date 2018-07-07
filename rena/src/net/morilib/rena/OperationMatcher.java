/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

import java.util.function.Predicate;

public interface OperationMatcher<A> extends PatternMatcher<A> {

	public int skipSpace(String match, int index);

	public default OperationMatcher<A> times(final int countmin,
			final int countmax,
			final PatternAction<A> action) {
		if(countmin < 0) {
			throw new IllegalArgumentException("minimum of repetition must be non negative");
		} else if(countmin == 0 && countmax == 0) {
			throw new IllegalArgumentException("both minimum and maximum must not be all zero");
		} else if(countmax >= 0 && (countmin > countmax)) {
			throw new IllegalArgumentException("minimum must be less than or equal to maximum");
		}
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result;
				int lastIndex = index;
				A attr = attribute;

				for(int i = 0; countmax < 0 || i < countmax; i++) {
					lastIndex = skipSpace(match, lastIndex);
					if((result = OperationMatcher.this.match(match, lastIndex, attr)) == null) {
						return i < countmin ? null : new PatternResult<A>(match.substring(index, lastIndex), lastIndex, attr);
					}
					lastIndex = result.getLastIndex();
					if(action != null) {
						attr = action.action(result.getMatch(), result.getAttribute(), attr);
					}
				}
				return new PatternResult<A>(match.substring(index, lastIndex), lastIndex, attr);
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> times(final int countmin,
			final int countmax) {
		return times(countmin, countmax, null);
	}

	public default OperationMatcher<A> atLeast(final int count, final PatternAction<A> action) {
		return times(count, -1, action);
	}

	public default OperationMatcher<A> atLeast(final int count) {
		return times(count, -1, null);
	}

	public default OperationMatcher<A> atMost(final int count, final PatternAction<A> action) {
		return times(0, count, action);
	}

	public default OperationMatcher<A> atMost(final int count) {
		return times(0, count, null);
	}

	public default OperationMatcher<A> maybe(final PatternAction<A> action) {
		return times(0, 1, action);
	}

	public default OperationMatcher<A> maybe() {
		return times(0, 1, null);
	}

	public default OperationMatcher<A> zeroOrMore(final PatternAction<A> action) {
		return times(0, -1, action);
	}

	public default OperationMatcher<A> zeroOrMore() {
		return times(0, -1, null);
	}

	public default OperationMatcher<A> oneOrMore(final PatternAction<A> action) {
		return times(1, -1, action);
	}

	public default OperationMatcher<A> oneOrMore() {
		return times(1, -1, null);
	}

	public default OperationMatcher<A> delimit(final String delimiter,
			final PatternAction<A> action) {
		return new OperationMatcher<A>() {
			private PatternResult<A> isMatched(String match, int index, A attr) {
				int lastIndex = index;

				if(!match.startsWith(delimiter, index)) {
					return null;
				}
				lastIndex = skipSpace(match, lastIndex + delimiter.length());
				return OperationMatcher.this.match(match, lastIndex, attr);
			}

			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result;
				int lastIndex;
				A attr = attribute;

				if((result = OperationMatcher.this.match(match, index, attr)) == null) {
					return null;
				}
				lastIndex = result.getLastIndex();
				if(action != null) {
					attr = action.action(result.getMatch(), result.getAttribute(), attr);
				}
				while(true) {
					if((result = isMatched(match, lastIndex, attr)) == null) {
						return new PatternResult<A>(match.substring(index, lastIndex), lastIndex, attr);
					}
					lastIndex = skipSpace(match, result.getLastIndex());
					if(action != null) {
						attr = action.action(result.getMatch(), result.getAttribute(), attr);
					}
				}
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> delimit(final String delimiter) {
		return delimit(delimiter, null);
	}

	public default OperationMatcher<A> cond(final Predicate<A> cond) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = OperationMatcher.this.match(match, index, attribute);

				return result != null && cond.test(result.getAttribute()) ? result : null;
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> end() {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = OperationMatcher.this.match(match, index, attribute);

				return match.length() == result.getLastIndex() ? result : null;
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

}
