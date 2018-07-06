/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class PatternResult<A> {

	private String match;
	private int lastIndex;
	private A attribute;

	public PatternResult(String match, int lastIndex, A attribute) {
		this.match = match;
		this.lastIndex = lastIndex;
		this.attribute = attribute;
	}

	public String getMatch() {
		return match;
	}

	public int getLastIndex() {
		return lastIndex;
	}

	public A getAttribute() {
		return attribute;
	}
}
