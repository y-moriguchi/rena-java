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
	 */
	public PatternMatcher<A> apply(PatternMatcher<A> arg1,
			PatternMatcher<A> arg2,
			PatternMatcher<A> arg3,
			PatternMatcher<A> arg4);

}
