/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public interface LookaheadMatcher<A> extends ThenMatcher<A> {

	public default LookaheadMatcher<A> lookahead(final PatternMatcher<A> matcher) {
		return new LookaheadMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result1 = LookaheadMatcher.this.match(match, index, attribute);

				if(result1 == null) {
					return null;
				} else if(matcher.match(match, result1.getLastIndex(), result1.getAttribute()) == null) {
					return null;
				} else {
					return result1;
				}			
			}

			public int skipSpace(String match, int index) {
				return LookaheadMatcher.this.skipSpace(match, index);
			}
		};
	}

	public default LookaheadMatcher<A> lookaheadNot(final PatternMatcher<A> matcher) {
		return new LookaheadMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result1 = LookaheadMatcher.this.match(match, index, attribute);

				if(result1 == null) {
					return null;
				} else if(matcher.match(match, result1.getLastIndex(), result1.getAttribute()) != null) {
					return null;
				} else {
					return result1;
				}			
			}

			public int skipSpace(String match, int index) {
				return LookaheadMatcher.this.skipSpace(match, index);
			}
		};
	}

}
