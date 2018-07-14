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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to create parser definition.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
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

	private class InitAttr extends RenaImpl {

		private A init;

		private InitAttr(A init) {
			this.init = init;
		}

		@Override
		public PatternResult<A> match(String match, int index, A attribute) {
			return new PatternResult<A>(match, index, init);
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

	}

	@FunctionalInterface
	private static interface ILetrec<A> {
		public A apply(ILetrec<A> f);
	}

	@FunctionalInterface
	private static interface ILetrecn<A> {
		public List<A> apply(ILetrecn<A> f);
	}

	private static final String REAL_NO_SIGN =
			"(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)(?:[eE][\\+\\-]?[0-9]+)?";
	private static final String REAL_WITH_SIGN =
			"[\\+\\-]?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)(?:[eE][\\+\\-]?[0-9]+)?";

	private Pattern patternToIgnore;
	private TrieNode node;

	/**
	 * Constructs a class to create parser definition with default settings.
	 */
	public Rena() {}

	/**
	 * Constructs a class to create parser definition with a regular expression to ignore.
	 * 
	 * @param toIgnore a regular expression to ignore
	 */
	public Rena(String toIgnore) {
		patternToIgnore = Pattern.compile(toIgnore);
	}

	/**
	 * Constructs a class to create parser definition with keywords.<br>
	 * A longest keyword will be matched.
	 * 
	 * @param keys an array of keywords
	 */
	public Rena(String[] keys) {
		node = new TrieNode();
		for(String key : keys) {
			addKeyword(key);
		}
	}

	/**
	 * Constructs a class to create parser definition with keywords.<br>
	 * A longest keyword will be matched.
	 * 
	 * @param keys an array of keywords
	 */
	public Rena(List<String> keys) {
		this(keys.toArray(new String[0]));
	}

	/**
	 * Constructs a class to create parser definition with a regular expression to ignore and keywords.<br>
	 * A longest keyword will be matched.
	 * 
	 * @param toIgnore a regular expression to ignore
	 * @param keys an array of keywords
	 */
	public Rena(String toIgnore, String[] keys) {
		this(toIgnore);
		node = new TrieNode();
		for(String key : keys) {
			addKeyword(key);
		}
	}

	/**
	 * Constructs a class to create parser definition with a regular expression to ignore and keywords.<br>
	 * A longest keyword will be matched.
	 * 
	 * @param toIgnore a regular expression to ignore
	 * @param keys an array of keywords
	 */
	public Rena(String toIgnore, List<String> keys) {
		this(toIgnore, keys.toArray(new String[0]));
	}

	private void addKeyword(String key) {
		TrieNode node = this.node;

		if(node == null) {
			throw new IllegalStateException();
		} else if(key == null || key.equals("")) {
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

	private String searchKeyword(String toMatch, int index) {
		TrieNode node = this.node;
		int i;

		if(node == null) {
			return null;
		}
		for(i = index; i < toMatch.length(); i++) {
			int ch = toMatch.charAt(i);

			if(node.contains(ch)) {
				node = node.get(ch);
			} else {
				return node.matched;
			}
		}
		return node.matched;
	}

	private int matchKeyword(String key, String toMatch, int index) {
		String result = searchKeyword(toMatch, index);

		return result != null && result.equals(key) ? index + result.length() : -1;
	}

	/**
	 * wraps a given matcher and an action which executes when the pattern is matched.
	 * 
	 * @param matcher a matcher by PatternMatcher interface
	 * @param action an action to execute
	 * @return a matcher
	 */
	public LookaheadMatcher<A> then(final PatternMatcher<A> matcher,
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

	/**
	 * wraps a given matcher.
	 * 
	 * @param matcher a matcher by PatternMatcher interface
	 * @return a matcher
	 */
	public LookaheadMatcher<A> then(final PatternMatcher<A> matcher) {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				return matcher.match(match, index, attribute);
			}
		};
	}

	/**
	 * creates a matcher which matches with a given string
	 * and an action which executes when the pattern is matched.
	 * 
	 * @param string a string to be matched
	 * @param action an action to execute
	 * @return a matcher
	 */
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

	/**
	 * creates a matcher which matches with a given string.
	 * 
	 * @param string a string to be matched
	 * @return a matcher
	 */
	public LookaheadMatcher<A> string(final String string) {
		return string(string, null);
	}

	/**
	 * creates a matcher which matches with a regular expression
	 * and an action which executes when the pattern is matched.
	 * 
	 * @param regex a regular expression to be matched
	 * @param action an action to execute
	 * @return a matcher
	 */
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

	/**
	 * creates a matcher which matches with a regular expression.
	 * 
	 * @param regex a regular expression to be matched
	 * @return a matcher
	 */
	public LookaheadMatcher<A> regex(final String regex) {
		return regex(regex, null);
	}

	/**
	 * creates a matcher which matches a given keyword.<br>
	 * A longest keyword will be matched.
	 * 
	 * @param key a regular expression to be matched
	 * @return a matcher
	 */
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

	public LookaheadMatcher<A> notKey() {
		return new RenaImpl() {
			@Override
			public PatternResult<A> match(String match, int index, A attribute) {
				if(searchKeyword(match, index) == null) {
					return new PatternResult<A>("", index, attribute);
				} else {
					return null;
				}
			}
		};
	}

	public LookaheadMatcher<A> br() {
		return regex("\r\n|\r|\n");
	}

	public LookaheadMatcher<A> equalsId(final String id) {
		return string(id)
				.lookahead((str, index, attr) -> {
					PatternResult<A> result = new PatternResult<A>("", index, null);
					if(index == str.length()) {
						return result;
					} else if(patternToIgnore == null && node == null) {
						return result;
					} else if(patternToIgnore != null &&
							patternToIgnore.matcher(str.substring(index)).lookingAt()) {
						return result;
					} else if(node != null && searchKeyword(str, index) != null) {
						return result;
					} else {
						return null;
					}
				});
	}

	public LookaheadMatcher<A> real(boolean signum, final PatternAction<A> action) {
		return regex(signum ? REAL_WITH_SIGN : REAL_NO_SIGN, action);
	}

	public OrMatcher<A> or(PatternMatcher<A> arg1, PatternMatcher<A> arg2) {
		return then(arg1).or(arg2);
	}

	public OrMatcher<A> or(PatternMatcher<A> arg1,
			PatternMatcher<A> arg2,
			PatternMatcher<A> arg3) {
		return then(arg1).or(arg2).or(arg3);
	}

	public OrMatcher<A> or(PatternMatcher<A> arg1,
			PatternMatcher<A> arg2,
			PatternMatcher<A> arg3,
			PatternMatcher<A> arg4) {
		return then(arg1).or(arg2).or(arg3).or(arg4);
	}

	public OrMatcher<A> or(List<PatternMatcher<A>> args) {
		OrMatcher<A> result;

		if(args.size() == 0) {
			throw new IllegalArgumentException("too few arguments");
		}
		result = then(args.get(0));
		for(int i = 1; i < args.size(); i++) {
			result = result.or(args.get(i));
		}
		return result;
	}

	public OperationMatcher<A> times(int countmin, int countmax, PatternMatcher<A> pattern,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).times(countmin, countmax, action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> times(int countmin, int countmax, PatternMatcher<A> pattern,
			PatternAction<A> action) {
		return then(pattern).times(countmin, countmax, action);
	}

	public OperationMatcher<A> times(int countmin, int countmax, PatternMatcher<A> pattern) {
		return then(pattern).times(countmin, countmax);
	}

	public OperationMatcher<A> atLeast(int count, PatternMatcher<A> pattern,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).atLeast(count, action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> atLeast(int count, PatternMatcher<A> pattern,
			PatternAction<A> action) {
		return then(pattern).atLeast(count, action);
	}

	public OperationMatcher<A> atLeast(int count, PatternMatcher<A> pattern) {
		return then(pattern).atLeast(count);
	}

	public OperationMatcher<A> atMost(int count, PatternMatcher<A> pattern,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).atMost(count, action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> atMost(int count, PatternMatcher<A> pattern,
			PatternAction<A> action) {
		return then(pattern).atMost(count, action);
	}

	public OperationMatcher<A> atMost(int count, PatternMatcher<A> pattern) {
		return then(pattern).atMost(count);
	}

	public OperationMatcher<A> maybe(PatternMatcher<A> pattern, PatternAction<A> action) {
		return then(pattern).maybe(action);
	}

	public OperationMatcher<A> maybe(PatternMatcher<A> pattern) {
		return then(pattern).maybe();
	}

	public OperationMatcher<A> zeroOrMore(PatternMatcher<A> pattern,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).zeroOrMore(action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> zeroOrMore(PatternMatcher<A> pattern, PatternAction<A> action) {
		return then(pattern).zeroOrMore(action);
	}

	public OperationMatcher<A> zeroOrMore(PatternMatcher<A> pattern) {
		return then(pattern).zeroOrMore();
	}

	public OperationMatcher<A> oneOrMore(PatternMatcher<A> pattern,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).oneOrMore(action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> oneOrMore(PatternMatcher<A> pattern, PatternAction<A> action) {
		return then(pattern).oneOrMore(action);
	}

	public OperationMatcher<A> oneOrMore(PatternMatcher<A> pattern) {
		return then(pattern).oneOrMore();
	}

	public OperationMatcher<A> delimit(PatternMatcher<A> pattern, String delimiter,
			PatternAction<A> action, A init) {
		return new InitAttr(init).then(then(pattern).delimit(delimiter, action),
				(str, syn, inherit) -> syn);
	}

	public OperationMatcher<A> delimit(PatternMatcher<A> pattern, String delimiter,
			PatternAction<A> action) {
		return then(pattern).delimit(delimiter, action);
	}

	public OperationMatcher<A> delimit(PatternMatcher<A> pattern, String delimiter) {
		return then(pattern).delimit(delimiter);
	}

	public LookaheadMatcher<A> attr(A attr) {
		return new InitAttr(attr);
	}

	/**
	 * A method which can refer a return value of the function itself.<br>
	 * This method will be used for defining a pattern with recursion.
	 * 
	 * @param func a function whose argument is a return value itself.
	 * @return PatternMatcher interface
	 */
	public static<A> PatternMatcher<A> letrec(
			final Function<PatternMatcher<A>, PatternMatcher<A>> func) {
		ILetrec<PatternMatcher<A>> f = g -> g.apply(g);
		ILetrec<PatternMatcher<A>> h = g -> func.apply((match, index, attr) -> g.apply(g).match(match, index, attr));

		return f.apply(h);
	}

	/**
	 * A method which can refer a return values of the function itself.<br>
	 * This method will be used for defining a pattern with recursion.
	 * 
	 * @param func1 a function whose first argument is a return values itself.
	 * @param func2 a function whose second argument is a return values itself.
	 * @return PatternMatcher interface
	 */
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

	/**
	 * A method which can refer a return values of the function itself.<br>
	 * This method will be used for defining a pattern with recursion.
	 * 
	 * @param func1 a function whose first argument is a return values itself.
	 * @param func2 a function whose second argument is a return values itself.
	 * @param func3 a function whose third argument is a return values itself.
	 * @return PatternMatcher interface
	 */
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

	/**
	 * A method which can refer a return values of the function itself.<br>
	 * This method will be used for defining a pattern with recursion.
	 * 
	 * @param func1 a function whose first argument is a return values itself.
	 * @param func2 a function whose second argument is a return values itself.
	 * @param func3 a function whose third argument is a return values itself.
	 * @param func4 a function whose fourth argument is a return values itself.
	 * @return PatternMatcher interface
	 */
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

	/**
	 * A method which can refer a return values of the function itself.<br>
	 * This method will be used for defining a pattern with recursion.
	 * 
	 * @param func1 a function whose first argument is a return values itself.
	 * @param func2 a function whose second argument is a return values itself.
	 * @param func3 a function whose third argument is a return values itself.
	 * @param func4 a function whose fourth argument is a return values itself.
	 * @param func5 a function whose fifth argument is a return values itself.
	 * @return PatternMatcher interface
	 */
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
