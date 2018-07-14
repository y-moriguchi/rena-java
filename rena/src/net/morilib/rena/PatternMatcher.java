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
	 * @param match a string to be matched
	 * @param index an index to start matching
	 * @param attribute inherited attribute
	 * @return result of matching or null
	 */
	public PatternResult<A> match(String match, int index, A attribute);

	/**
	 * matches the given string starts with 0.<br>
	 * returns an instance of PatternResult if it matches,
	 * or returns null if it does not match.
	 * 
	 * @param match a string to be matched
	 * @param attribute inherited attribute
	 * @return result of matching or null
	 */
	public default PatternResult<A> match(String match, A attribute) {
		return match(match, 0, attribute);
	}

	/**
	 * An alias of match.
	 * 
	 * @param match a string to be matched
	 * @param attribute inherited attribute
	 * @return
	 */
	public default PatternResult<A> parse(String match, A attribute) {
		return match(match, 0, attribute);
	}

	/**
	 * searches this pattern in the given string from the given index.<br>
	 * returns an instance of PatternResult if the pattern found in the string from the index.
	 * or returns null if the pattern is not found.
	 * 
	 * @param match a string to be matched
	 * @param index an index to start matching
	 * @param attribute inherited attribute
	 * @return result of matching or null
	 */
	public default PatternResult<A> parsePart(String match, int index, A attribute) {
		PatternResult<A> result;

		for(int i = index; i < match.length(); i++) {
			result = match(match, i, attribute);
			if(result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * searches this pattern in the given string.<br>
	 * returns an instance of PatternResult if the pattern found in the string.
	 * or returns null if the pattern is not found.
	 * 
	 * @param match a string to be matched
	 * @param index an index to start matching
	 * @param attribute inherited attribute
	 * @return result of matching or null
	 */
	public default PatternResult<A> parsePart(String match, A attribute) {
		return parsePart(match, 0, attribute);
	}

	/**
	 * searches all patterns in the given string from the given index.<br>
	 * returns accumulated attribute by the given action.
	 * 
	 * @param match a string to be matched
	 * @param index an index to start matching
	 * @param init initial attribute
	 * @param action an accumulator
	 * @return accumulated attribute
	 */
	public default A parsePartGlobal(String match, int index, A init, BiFunction<A, A, A> action) {
		A attr = init;

		for(int i = index; i < match.length();) {
			PatternResult<A> result = match(match, i, attr);

			if(result != null) {
				attr = action.apply(result.getAttribute(), attr);
				i = result.getLastIndex();
			} else {
				i++;
			}
		}
		return attr;
	}

	/**
	 * searches all patterns in the given string.<br>
	 * returns accumulated attribute by the given action.
	 * 
	 * @param match a string to be matched
	 * @param init initial attribute
	 * @param action an accumulator
	 * @return accumulated attribute
	 */
	public default A parsePartGlobal(String match, A init, BiFunction<A, A, A> action) {
		return parsePartGlobal(match, 0, init, action);
	}

	/**
	 * searches all patterns in the given string from the given index.<br>
	 * returns a list of all attributes.
	 * 
	 * @param match a string to be matched
	 * @param index an index to start matching
	 * @return a list of all attributes
	 */
	public default List<A> parsePartGlobalList(String match, int index) {
		List<A> attr = new ArrayList<A>();

		for(int i = index; i < match.length();) {
			PatternResult<A> result = match(match, i, null);

			if(result != null) {
				attr.add(result.getAttribute());
				i = result.getLastIndex();
			} else {
				i++;
			}
		}
		return attr;
	}

	/**
	 * searches all patterns in the given string.<br>
	 * returns a list of all attributes.
	 * 
	 * @param match a string to be matched
	 * @return a list of all attributes
	 */
	public default List<A> parsePartGlobalList(String match) {
		return parsePartGlobalList(match, 0);
	}

}
