/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package net.morilib.rena;

/**
 * An interface with successor matcher.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
public interface ThenMatcher<A> extends OrMatcher<A> {

	/**
	 * creates a matcher which succeeds the given matcher
	 * and execute the given action when matches.
	 * 
	 * @param matcher a successor matcher
	 * @param action an action
	 * @return a matcher
	 */
	public default ThenMatcher<A> then(final PatternMatcher<A> matcher, final PatternAction<A> action) {
		return new ThenMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result1 = ThenMatcher.this.match(match, index, attribute), result2;
				int lastIndexNew;

				if(result1 == null) {
					return null;
				}
				lastIndexNew = skipSpace(match, result1.getLastIndex());
				if((result2 = matcher.match(match, lastIndexNew, result1.getAttribute())) == null) {
					return null;
				} else {
					String matched = match.substring(index, result2.getLastIndex());

					return new PatternResult<A>(matched,
							result2.getLastIndex(),
							action != null ?
									action.action(match, result2.getAttribute(), result1.getAttribute()) :
										result1.getAttribute());
				}			
			}

			public int skipSpace(String match, int index) {
				return ThenMatcher.this.skipSpace(match, index);
			}
		};
	}

	/**
	 * creates a matcher which succeeds the given matcher.
	 * 
	 * @param matcher a successor matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> then(final PatternMatcher<A> matcher) {
		return then(matcher, null);
	}

	/**
	 * repeats to the given count.<br>
	 * This method is NOT backtracking.
	 *
	 * @param countmin minimum of repetition
	 * @param countmax maximum of repetition
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenTimes(final int countmin,
			final int countmax,
			final OperationMatcher<A> pattern,
			final PatternAction<A> action) {
		return then(pattern.times(countmin, countmax, action), (str, syn, inherit) -> syn);
	}

	/**
	 * repeats to the given count.<br>
	 * This method is NOT backtracking.
	 *
	 * @param countmin minimum of repetition
	 * @param countmax maximum of repetition
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenTimes(final int countmin,
			final int countmax,
			final OperationMatcher<A> pattern) {
		return thenTimes(countmin, countmax, pattern, null);
	}

	/**
	 * repeats at least the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count minimum of repetition
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenAtLeast(final int count,
			final OperationMatcher<A> pattern,
			final PatternAction<A> action) {
		return thenTimes(count, -1, pattern, action);
	}

	/**
	 * repeats at least the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count minimum of repetition
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenAtLeast(final int count, final OperationMatcher<A> pattern) {
		return thenTimes(count, -1, pattern, null);
	}

	/**
	 * repeats at most the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count maximum of repetition
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenAtMost(final int count,
			final OperationMatcher<A> pattern,
			final PatternAction<A> action) {
		return thenTimes(0, count, pattern, action);
	}

	/**
	 * repeats at most the given count.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param count maximum of repetition
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenAtMost(final int count, final OperationMatcher<A> pattern) {
		return thenTimes(0, count, pattern, null);
	}

	/**
	 * matches zero or one of the this matcher.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenMaybe(final OperationMatcher<A> pattern, final PatternAction<A> action) {
		return thenTimes(0, 1, pattern, action);
	}

	/**
	 * matches zero or one of the this matcher.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenMaybe(final OperationMatcher<A> pattern) {
		return thenTimes(0, 1, pattern, null);
	}

	/**
	 * a shortcut of 'atLeast(0, action)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenZeroOrMore(final OperationMatcher<A> pattern, final PatternAction<A> action) {
		return thenTimes(0, -1, pattern, action);
	}

	/**
	 * a shortcut of 'atLeast(0)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenZeroOrMore(final OperationMatcher<A> pattern) {
		return thenTimes(0, -1, pattern, null);
	}

	/**
	 * a shortcut of 'atLeast(1, action)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenOneOrMore(final OperationMatcher<A> pattern, final PatternAction<A> action) {
		return thenTimes(1, -1, pattern, action);
	}

	/**
	 * a shortcut of 'atLeast(1)'.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenOneOrMore(final OperationMatcher<A> pattern) {
		return thenTimes(1, -1, pattern, null);
	}

	/**
	 * matches a string which is delimited by the given string.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @param delimiter a string of delimiter
	 * @param action an action to be invoked
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenDelimit(final OperationMatcher<A> pattern,
			final PatternMatcher<A> delimiter,
			final PatternAction<A> action) {
		return then(pattern.delimit(delimiter, action), (str, syn, inherit) -> syn);
	}

	/**
	 * matches a string which is delimited by the given string.<br>
	 * This method is NOT backtracking.
	 * 
	 * @param pattern a matcher
	 * @param delimiter a string of delimiter
	 * @return a matcher
	 */
	public default ThenMatcher<A> thenDelimit(final OperationMatcher<A> pattern,
			final PatternMatcher<A> delimiter) {
		return thenDelimit(pattern, delimiter, null);
	}

}
