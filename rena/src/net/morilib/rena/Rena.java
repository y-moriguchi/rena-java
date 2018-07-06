/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rena<A> {

	private abstract class RenaImpl implements LookaheadMatcher<A> {
		@Override
		public int skipSpace(String match, int index) {
			Matcher matcherRe = patternToIgnore.matcher(match.substring(index));

			return matcherRe.matches() ? index + matcherRe.end() : index;
		}
	}

	private Pattern patternToIgnore;

	public Rena(String toIgnore) {
		patternToIgnore = Pattern.compile(toIgnore);
	}

	public LookaheadMatcher<A> start(final OperationMatcher<A> matcher) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				return matcher.match(match, index, attribute);
			}
		};
	}

	public LookaheadMatcher<A> string(final String string) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				if(match.startsWith(string, index)) {
					return new PatternResult<A>(match, index + string.length(), attribute);
				} else {
					return null;
				}
			}
		};
	}

	public LookaheadMatcher<A> regex(final String regex) {
		final Pattern pattern = Pattern.compile(regex);

		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				String toMatch = match.substring(index);
				Matcher matcher = pattern.matcher(toMatch);

				if(matcher.matches()) {
					return new PatternResult<A>(match, index + matcher.end(), attribute);
				} else {
					return null;
				}
			}
		};
	}

	public LookaheadMatcher<A> matcher(final PatternMatcher<A> matcher) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				return matcher.match(match, index, attribute);
			}
		};
	}

}
