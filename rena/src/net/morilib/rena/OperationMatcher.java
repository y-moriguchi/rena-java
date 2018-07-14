/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package net.morilib.rena;

import java.util.function.Predicate;

/**
 * An interface with operations for matchers.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
public interface OperationMatcher<A> extends PatternMatcher<A> {

	/**
	 * skips a space of the given string.
	 * 
	 * @param match a string to match
	 * @param index a starting index
	 * @return a last index of skipping
	 */
	public int skipSpace(String match, int index);

	/**
	 * repeats to the given count.<br>
	 * This method is NOT backtracking.
	 *
	 * @param countmin minimum of repetition
	 * @param countmax maximum of repetition
	 * @param action an action to be invoked
	 * @return a matcher
	 */
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

	/**
	 * repeats to the given count.<br>
	 * This method is NOT backtracking.
	 *
	 * @param countmin minimum of repetition
	 * @param countmax maximum of repetition
	 * @return a matcher
	 */
	public default OperationMatcher<A> times(final int countmin,
			final int countmax) {
		return times(countmin, countmax, null);
	}

	/**
	 * repeats at least the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count minimum of repetition
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> atLeast(final int count, final PatternAction<A> action) {
		return times(count, -1, action);
	}

	/**
	 * repeats at least the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count minimum of repetition
	 * @return a matcher
	 */
	public default OperationMatcher<A> atLeast(final int count) {
		return times(count, -1, null);
	}

	/**
	 * repeats at most the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count maximum of repetition
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> atMost(final int count, final PatternAction<A> action) {
		return times(0, count, action);
	}

	/**
	 * repeats at most the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count maximum of repetition
	 * @return a matcher
	 */
	public default OperationMatcher<A> atMost(final int count) {
		return times(0, count, null);
	}

	/**
	 * matches zero or one of the this matcher.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> maybe(final PatternAction<A> action) {
		return times(0, 1, action);
	}

	/**
	 * matches zero or one of the this matcher.<br>
	 * This method is NOT backtracking.
	 * 
	 * @return a matcher
	 */
	public default OperationMatcher<A> maybe() {
		return times(0, 1, null);
	}

	/**
	 * a shortcut of 'atLeast(0, action)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> zeroOrMore(final PatternAction<A> action) {
		return times(0, -1, action);
	}

	/**
	 * a shortcut of 'atLeast(0)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @return a matcher
	 */
	public default OperationMatcher<A> zeroOrMore() {
		return times(0, -1, null);
	}

	/**
	 * a shortcut of 'atLeast(1, action)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> oneOrMore(final PatternAction<A> action) {
		return times(1, -1, action);
	}

	/**
	 * a shortcut of 'atLeast(1)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @return a matcher
	 */
	public default OperationMatcher<A> oneOrMore() {
		return times(1, -1, null);
	}

	/**
	 * matches a string which is delimited by the given string.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param delimiter a string of delimiter
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default OperationMatcher<A> delimit(final PatternMatcher<A> delimiter,
			final PatternAction<A> action) {
		return new OperationMatcher<A>() {
			private PatternResult<A> isMatched(String match, int index, A attr) {
				PatternResult<A> result;
				int lastIndex = index;

				if((result = delimiter.match(match, index, attr)) == null) {
					return null;
				}
				lastIndex = skipSpace(match, result.getLastIndex());
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

	/**
	 * matches a string which is delimited by the given string.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param delimiter a string of delimiter
	 * @return a matcher
	 */
	public default OperationMatcher<A> delimit(final PatternMatcher<A> delimiter) {
		return delimit(delimiter, null);
	}

	/**
	 * matches the pattern if the given condition is true.
	 * 
	 * @param cond the condition
	 * @return a matcher
	 */
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

	/**
	 * matches end of string.
	 * 
	 * @return a matcher
	 */
	public default OperationMatcher<A> end() {
		return new OperationMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = OperationMatcher.this.match(match, index, attribute);

				return result != null && match.length() == result.getLastIndex() ? result : null;
			}

			public int skipSpace(String match, int index) {
				return OperationMatcher.this.skipSpace(match, index);
			}
		};
	}

}
