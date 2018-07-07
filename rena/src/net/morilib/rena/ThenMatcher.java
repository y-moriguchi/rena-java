/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public interface ThenMatcher<A> extends OrMatcher<A> {

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

	public default ThenMatcher<A> then(final PatternMatcher<A> matcher) {
		return then(matcher, null);
	}

}
