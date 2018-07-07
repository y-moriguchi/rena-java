/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

import junit.framework.TestCase;

public class TestCaseBase extends TestCase {

	protected<A> void match(String toMatch, OperationMatcher<A> matcher, String match, int lastIndex, A init, A attr) {
		PatternResult<A> result = matcher.match(toMatch, 0, init);
		if(result == null) {
			fail("not matched");
		}
		assertEquals(result.getMatch(), match);
		assertEquals(result.getLastIndex(), lastIndex);
		assertEquals(result.getAttribute(), attr);
	}

	protected<A> void nomatch(String toMatch, OperationMatcher<A> matcher) {
		PatternResult<A> result = matcher.match(toMatch, 0, null);
		assertNull(result);
	}

	protected void assertArgumentException(Runnable runnable) {
		try {
			runnable.run();
			fail("not throwed");
		} catch(IllegalArgumentException ex) {
			// ok
		}
	}

}
