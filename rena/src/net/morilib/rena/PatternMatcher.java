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
 * A functional interface of matchers used in this framework.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
@FunctionalInterface
public interface PatternMatcher<A> {

	/**
	 * matches the given string starts with the given index.<br>
	 * returns an instance of PatternResult if it matches,
	 * or returns null if it does not match.
	 * 
	 * @param match a string to match
	 * @param index an index to start matching
	 * @param attribute inherited attribute
	 * @return result of matching or null
	 */
	public PatternResult<A> match(String match, int index, A attribute);

}
