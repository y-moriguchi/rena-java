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
 * A functional interface of four arguments of PatternMatcher.
 *
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
@FunctionalInterface
public interface Letrec4Function<A> {

	/**
	 * applies the given arguments.
	 * First PatternMatcher is used as return value.
	 *
	 * @param arg1 first PatternMatcher
	 * @param arg2 second PatternMatcher
	 * @param arg3 third PatternMatcher
	 * @param arg4 fourth PatternMatcher
	 * @return the argument arg1
	 */
	public PatternMatcher<A> apply(PatternMatcher<A> arg1,
			PatternMatcher<A> arg2,
			PatternMatcher<A> arg3,
			PatternMatcher<A> arg4);

}
