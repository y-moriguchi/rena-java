/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class ThenMatcherTest extends TestCaseBase {

	public void testThen001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[0-9]+").then(r.regex("[a-z]+"));

		match("765pro", matcher, "765pro", 6, "", "");
		nomatch("765", matcher);
		nomatch("pro", matcher);
	}

	public void testThen002() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[0-9]+")
				.then(r.regex("[a-z]+", (m, b, a) -> m), (m, b, a) -> a + b);

		match("765pro", matcher, "765pro", 6, "aaa", "aaapro");
		nomatch("765", matcher);
		nomatch("pro", matcher);
	}

	public void testThen003() {
		Rena<String> r = new Rena<String>("[ \t]+");
		OperationMatcher<String> matcher = r.regex("[0-9]+").then(r.regex("[a-z]+"));

		match("765pro", matcher, "765pro", 6, "", "");
		match("765  pro", matcher, "765  pro", 8, "", "");
		nomatch("765", matcher);
		nomatch("pro", matcher);
	}

}
