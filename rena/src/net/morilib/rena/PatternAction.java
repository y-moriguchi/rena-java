/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

@FunctionalInterface
public interface PatternAction<A> {

	public A action(String match, A attribute, A inheritedAttribute);

}
