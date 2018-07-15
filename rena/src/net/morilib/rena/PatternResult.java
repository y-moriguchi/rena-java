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
 * A class of matching result.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
public class PatternResult<A> {

	private String match;
	private int lastIndex;
	private A attribute;

	/**
	 * constructs matching result.
	 * 
	 * @param match matched string
	 * @param lastIndex last index of matching
	 * @param attribute attribute
	 */
	public PatternResult(String match, int lastIndex, A attribute) {
		this.match = match;
		this.lastIndex = lastIndex;
		this.attribute = attribute;
	}

	/**
	 * gets matched string.
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * gets start index of matching.
	 */
	public int getStartIndex() {
		return lastIndex - match.length();
	}

	/**
	 * gets last index of matching.
	 */
	public int getLastIndex() {
		return lastIndex;
	}

	/**
	 * gets an attribute.
	 */
	public A getAttribute() {
		return attribute;
	}

	public String toString() {
		return "match=" + match + ",lastIndex=" + lastIndex + ",attribute=" + attribute;
	}
}
