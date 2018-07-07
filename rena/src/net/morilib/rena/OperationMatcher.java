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
			final PatternAction<A> action,
			final A init) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result;
				String matched = "";
				int lastIndex = index;
				A attr = init == null ? attribute : init;
	
				if(countmin < 0) {
					throw new IllegalArgumentException("minimum of repetition must be non negative");
				} else if(countmin == 0 && countmax == 0) {
					throw new IllegalArgumentException("both minimum and maximum must not be all zero");
				} else if(countmax >= 0 && (countmin > countmax)) {
					throw new IllegalArgumentException("minimum must be less than or equal to maximum");
				}
	
				for(int i = 0; countmax < 0 || i < countmax; i++) {
					if((result = match(match, lastIndex, attr)) == null) {
						return i < countmin ? null : new PatternResult<A>(matched, lastIndex, attr);
					}
					matched = matched + result.getMatch();
					lastIndex = result.getLastIndex();
					if(action != null) {
						attr = action.action(result.getMatch(), result.getAttribute(), attr);
					}
				}
				return new PatternResult<A>(matched, lastIndex, attr);
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> times(final int countmin,
			final int countmax,
			final PatternAction<A> action) {
		return times(countmin, countmax, action, null);
	}

	public default OperationMatcher<A> atLeast(final int count, final PatternAction<A> action, final A init) {
		return times(count, -1, action, init);
	}

	public default OperationMatcher<A> atLeast(final int count, final PatternAction<A> action) {
		return times(count, -1, action, null);
	}

	public default OperationMatcher<A> atMost(final int count, final PatternAction<A> action, final A init) {
		return times(0, count, action, init);
	}

	public default OperationMatcher<A> atMost(final int count, final PatternAction<A> action) {
		return times(0, count, action, null);
	}

	public default OperationMatcher<A> maybe(final PatternAction<A> action, final A init) {
		return times(0, 1, action, init);
	}

	public default OperationMatcher<A> maybe(final PatternAction<A> action) {
		return times(0, 1, action, null);
	}

	public default OperationMatcher<A> zeroOrMore(final PatternAction<A> action, final A init) {
		return times(0, -1, action, init);
	}

	public default OperationMatcher<A> zeroOrMore(final PatternAction<A> action) {
		return times(0, -1, action, null);
	}

	public default OperationMatcher<A> oneOrMore(final PatternAction<A> action, final A init) {
		return times(1, -1, action, init);
	}

	public default OperationMatcher<A> oneOrMore(final PatternAction<A> action) {
		return times(1, -1, action, null);
	}

	public default OperationMatcher<A> delimit(final String delimiter,
			final PatternAction<A> action,
			final A init) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result;
				String matched = "";
				int lastIndex = index;
				A attr = init == null ? attribute : init;

				while(true) {
					if((result = match(match, lastIndex, attr)) == null) {
						return new PatternResult<A>(matched, lastIndex, attr);
					}
					matched = matched + result.getMatch();
					lastIndex = result.getLastIndex();
					if(action != null) {
						attr = action.action(result.getMatch(), result.getAttribute(), attr);
					}
					if(match.startsWith(delimiter, result.getLastIndex())) {
						return new PatternResult<A>(matched, lastIndex, attr);
					}
					matched = matched + delimiter;
					lastIndex += delimiter.length();
				}
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> delimit(final String delimiter, final PatternAction<A> action) {
		return delimit(delimiter, action, null);
	}

	public default OperationMatcher<A> cond(final Predicate<A> cond) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = match(match, index, attribute);

				return result != null && cond.test(result.getAttribute()) ? result : null;
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> attr(final A attr) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = match(match, index, attribute);

				return result != null ?
						new PatternResult<A>(result.getMatch(), result.getLastIndex(), attr) : null;
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default OperationMatcher<A> action(final PatternAction<A> action) {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = match(match, index, attribute);

				if(result == null) {
					return null;
				} else {
					A attrNew = action.action(result.getMatch(), result.getAttribute(), attribute);

					return new PatternResult<A>(result.getMatch(), result.getLastIndex(), attrNew);
				}
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

}
