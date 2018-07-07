/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class LookaheadMatcherTest extends TestCaseBase {

	public void testLookahead001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.string("765").lookahead(r.string("pro"));

		match("765pro", matcher, "765", 3, "", "");
		nomatch("765p", matcher);
		nomatch("961pro", matcher);
	}

	public void testLookaheadNot001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.string("765").lookaheadNot(r.string("pro"));

		match("765Pro", matcher, "765", 3, "", "");
		match("765", matcher, "765", 3, "", "");
		nomatch("765pro", matcher);
		nomatch("961pro", matcher);
	}

}
