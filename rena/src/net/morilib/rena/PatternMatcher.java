/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package net.morilib.rena;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

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

	public default PatternResult<A> match(String match, A attribute) {
		return match(match, 0, attribute);
	}

	public default PatternResult<A> parse(String match, A attribute) {
		return match(match, 0, attribute);
	}

	public default PatternResult<A> parsePart(String match, int index, A attribute) {
		PatternResult<A> result;

		for(int i = index; i < match.length(); i++) {
			result = match(match, index, attribute);
			if(result != null) {
				return result;
			}
		}
		return null;
	}

	public default PatternResult<A> parsePart(String match, A attribute) {
		return parsePart(match, 0, attribute);
	}

	public default A parsePartGlobal(String match, int index, A init, BiFunction<A, A, A> action) {
		A attr = init;

		for(int i = index; i < match.length(); i++) {
			PatternResult<A> result = match(match, index, attr);

			if(result != null) {
				attr = action.apply(result.getAttribute(), attr);
			}
		}
		return attr;
	}

	public default A parsePartGlobal(String match, A init, BiFunction<A, A, A> action) {
		return parsePartGlobal(match, 0, init, action);
	}

	public default List<A> parsePartGlobalList(String match, int index) {
		List<A> attr = new ArrayList<A>();

		for(int i = index; i < match.length(); i++) {
			PatternResult<A> result = match(match, index, null);

			if(result != null) {
				attr.add(result.getAttribute());
			}
		}
		return attr;
	}

	public default List<A> parsePartGlobalList(String match) {
		return parsePartGlobalList(match, 0);
	}

}
