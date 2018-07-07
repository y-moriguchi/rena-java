/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rena<A> {

	private abstract class RenaImpl implements LookaheadMatcher<A> {
		@Override
		public int skipSpace(String match, int index) {
			if(patternToIgnore != null) {
				Matcher matcherRe = patternToIgnore.matcher(match.substring(index));

				return matcherRe.lookingAt() ? index + matcherRe.end() : index;
			} else {
				return index;
			}
		}
	}

	private static class TrieNode {

		private Map<Integer, TrieNode> edges = new HashMap<Integer, TrieNode>();
		private String matched = null;

		private boolean contains(int ch) {
			return edges.containsKey(ch);
		}

		private TrieNode get(int ch) {
			if(!contains(ch) || edges.get(ch) == null) {
				throw new RuntimeException();
			}
			return edges.get(ch);
		}

		private boolean match(String key) {
			return matched != null && matched.equals(key);
		}

	}

	@FunctionalInterface
	private static interface ILetrec<A> {
		public A apply(ILetrec<A> f);
	}

	@FunctionalInterface
	private static interface ILetrecn<A> {
		public List<A> apply(ILetrecn<A> f);
	}

	private Pattern patternToIgnore;
	private TrieNode node = new TrieNode();

	public Rena() {}

	public Rena(String toIgnore) {
		patternToIgnore = Pattern.compile(toIgnore);
	}

	public Rena(String[] keys) {
		for(String key : keys) {
			addKeyword(key);
		}
	}

	public Rena(List<String> keys) {
		this(keys.toArray(new String[0]));
	}

	public Rena(String toIgnore, String[] keys) {
		this(toIgnore);
		for(String key : keys) {
			addKeyword(key);
		}
	}

	public Rena(String toIgnore, List<String> keys) {
		this(toIgnore, keys.toArray(new String[0]));
	}

	private void addKeyword(String key) {
		TrieNode node = this.node;

		if(key == null || key.equals("")) {
			throw new IllegalArgumentException("key must not be empty");
		}
		for(int i = 0; i < key.length(); i++) {
			int ch = key.charAt(i);

			if(!node.contains(ch)) {
				node.edges.put(ch, new TrieNode());
			}
			node = node.get(ch);
		}
		node.matched = key;
	}

	private int matchKeyword(String key, String toMatch, int index) {
		TrieNode node = this.node;
		int i;

		for(i = index; i < toMatch.length(); i++) {
			int ch = toMatch.charAt(i);

			if(node.contains(ch)) {
				node = node.get(ch);
			} else {
				return node.match(key) ? i : -1;
			}
		}
		return node.match(key) ? i : -1;
	}

	public LookaheadMatcher<A> matcher(final PatternMatcher<A> matcher,
			final PatternAction<A> action) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				PatternResult<A> result = matcher.match(match, index, attribute);

				if(result != null) {
					return new PatternResult<A>(result.getMatch(),
							result.getLastIndex(),
							action.action(result.getMatch(), result.getAttribute(), attribute));
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

	public LookaheadMatcher<A> string(final String string,
			final PatternAction<A> action) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				if(match.startsWith(string, index)) {
					return new PatternResult<A>(string,
							index + string.length(),
							action != null ? action.action(string, null, attribute) : attribute);
				} else {
					return null;
				}
			}
		};
	}

	public LookaheadMatcher<A> string(final String string) {
		return string(string, null);
	}

	public LookaheadMatcher<A> regex(final String regex,
			final PatternAction<A> action) {
		final Pattern pattern = Pattern.compile(regex);

		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				String toMatch = match.substring(index);
				Matcher matcher = pattern.matcher(toMatch);

				if(matcher.lookingAt()) {
					return new PatternResult<A>(match.substring(index, index + matcher.end()),
							index + matcher.end(),
							action != null ? action.action(match.substring(index, index + matcher.end()),
									null, attribute) : attribute);
				} else {
					return null;
				}
			}
		};
	}

	public LookaheadMatcher<A> regex(final String regex) {
		return regex(regex, null);
	}

	public LookaheadMatcher<A> key(final String key) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				int lastIndex;

				if((lastIndex = matchKeyword(key, match, index)) >= 0) {
					return new PatternResult<A>(key, lastIndex, attribute);
				} else {
					return null;
				}
			}
		};
	}

	public static<A> PatternMatcher<A> letrec(
			final Function<PatternMatcher<A>, PatternMatcher<A>> func) {
		ILetrec<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrec<PatternMatcher<A>> h = g -> func.apply((match, index, attr) -> g.apply(g).match(match, index, attr));

		return f.apply(h);
	}

	public static<A> PatternMatcher<A> letrec(
			final BiFunction<PatternMatcher<A>, PatternMatcher<A>, PatternMatcher<A>> func1,
			final BiFunction<PatternMatcher<A>, PatternMatcher<A>, PatternMatcher<A>> func2) {
		ILetrecn<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrecn<PatternMatcher<A>> h = g -> {
			List<PatternMatcher<A>> result = new ArrayList<PatternMatcher<A>>();
			PatternMatcher<A> x1 = (match, index, attr) -> g.apply(g).get(0).match(match, index, attr);
			PatternMatcher<A> x2 = (match, index, attr) -> g.apply(g).get(1).match(match, index, attr);

			result.add(func1.apply(x1, x2));
			result.add(func2.apply(x1, x2));
			return result;
		};

		return f.apply(h).get(0);
	}

	public static<A> PatternMatcher<A> letrec(
			final Letrec3Function<A> func1,
			final Letrec3Function<A> func2,
			final Letrec3Function<A> func3) {
		ILetrecn<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrecn<PatternMatcher<A>> h = g -> {
			List<PatternMatcher<A>> result = new ArrayList<PatternMatcher<A>>();
			PatternMatcher<A> x1 = (match, index, attr) -> g.apply(g).get(0).match(match, index, attr);
			PatternMatcher<A> x2 = (match, index, attr) -> g.apply(g).get(1).match(match, index, attr);
			PatternMatcher<A> x3 = (match, index, attr) -> g.apply(g).get(2).match(match, index, attr);

			result.add(func1.apply(x1, x2, x3));
			result.add(func2.apply(x1, x2, x3));
			result.add(func3.apply(x1, x2, x3));
			return result;
		};

		return f.apply(h).get(0);
	}

	public static<A> PatternMatcher<A> letrec(
			final Letrec4Function<A> func1,
			final Letrec4Function<A> func2,
			final Letrec4Function<A> func3,
			final Letrec4Function<A> func4) {
		ILetrecn<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrecn<PatternMatcher<A>> h = g -> {
			List<PatternMatcher<A>> result = new ArrayList<PatternMatcher<A>>();
			PatternMatcher<A> x1 = (match, index, attr) -> g.apply(g).get(0).match(match, index, attr);
			PatternMatcher<A> x2 = (match, index, attr) -> g.apply(g).get(1).match(match, index, attr);
			PatternMatcher<A> x3 = (match, index, attr) -> g.apply(g).get(2).match(match, index, attr);
			PatternMatcher<A> x4 = (match, index, attr) -> g.apply(g).get(3).match(match, index, attr);

			result.add(func1.apply(x1, x2, x3, x4));
			result.add(func2.apply(x1, x2, x3, x4));
			result.add(func3.apply(x1, x2, x3, x4));
			result.add(func4.apply(x1, x2, x3, x4));
			return result;
		};

		return f.apply(h).get(0);
	}

	public static<A> PatternMatcher<A> letrec(
			final Letrec5Function<A> func1,
			final Letrec5Function<A> func2,
			final Letrec5Function<A> func3,
			final Letrec5Function<A> func4,
			final Letrec5Function<A> func5) {
		ILetrecn<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrecn<PatternMatcher<A>> h = g -> {
			List<PatternMatcher<A>> result = new ArrayList<PatternMatcher<A>>();
			PatternMatcher<A> x1 = (match, index, attr) -> g.apply(g).get(0).match(match, index, attr);
			PatternMatcher<A> x2 = (match, index, attr) -> g.apply(g).get(1).match(match, index, attr);
			PatternMatcher<A> x3 = (match, index, attr) -> g.apply(g).get(2).match(match, index, attr);
			PatternMatcher<A> x4 = (match, index, attr) -> g.apply(g).get(3).match(match, index, attr);
			PatternMatcher<A> x5 = (match, index, attr) -> g.apply(g).get(4).match(match, index, attr);

			result.add(func1.apply(x1, x2, x3, x4, x5));
			result.add(func2.apply(x1, x2, x3, x4, x5));
			result.add(func3.apply(x1, x2, x3, x4, x5));
			result.add(func4.apply(x1, x2, x3, x4, x5));
			result.add(func5.apply(x1, x2, x3, x4, x5));
			return result;
		};

		return f.apply(h).get(0);
	}

}
