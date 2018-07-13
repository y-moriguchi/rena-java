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
 * A functional interface of actions used in this framework.
 * 
 * @author Yuichiro MORIGUCHI
 * @param <A> attribute
 */
@FunctionalInterface
public interface PatternAction<A> {

	/**
	 * a method of an action.
	 * 
	 * @param match matched string
	 * @param attribute synthesized attribute
	 * @param inheritedAttribute inherited attribute
	 * @return new synthesized attribute
	 */
	public A action(String match, A attribute, A inheritedAttribute);

}
