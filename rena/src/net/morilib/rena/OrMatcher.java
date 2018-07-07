/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public interface OrMatcher<A> extends OperationMatcher<A> {

	public default OrMatcher<A> or(final OperationMatcher<A> matcher) {
		return new ThenMatcher<A>() {
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result1, result2;

				if((result1 = OrMatcher.this.match(match, index, attribute)) != null) {
					return result1;
				} else if((result2 = matcher.match(match, index, attribute)) != null) {
					return result2;
				} else {
					return null;
				}			
			}

			public int skipSpace(String match, int index) {
				return OrMatcher.this.skipSpace(match, index);
			}
		};
	}
}
