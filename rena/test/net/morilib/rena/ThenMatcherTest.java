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

	public void testThenTimes001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenTimes(1, 3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("@765", matcher, "", "765");
		match("@7653", matcher, "@765", 4, "", "765");
		nomatch("@", matcher);
	}

	public void testThenTimes002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenTimes(1, 3, r.regex("[0-9]"));

		match("@765", matcher, "", "");
		match("@7653", matcher, "@765", 4, "", "");
		nomatch("@", matcher);
	}

	public void testThenAtLeast001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenAtLeast(3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("@765", matcher, "", "765");
		match("@7653", matcher, "", "7653");
		nomatch("@71", matcher);
	}

	public void testThenAtLeast002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenAtLeast(3, r.regex("[0-9]"));

		match("@765", matcher, "", "");
		match("@7653", matcher, "", "");
		nomatch("@71", matcher);
	}

	public void testThenAtMost001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenAtMost(3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("@765", matcher, "", "765");
		match("@27", matcher, "", "27");
		match("@7653", matcher, "@765", 4, "", "765");
	}

	public void testThenAtMost002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenAtMost(3, r.regex("[0-9]"));

		match("@765", matcher, "", "");
		match("@27", matcher, "", "");
		match("@7653", matcher, "@765", 4, "", "");
	}

	public void testThenMaybe001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenMaybe(r.regex("[0-9]{3}"), (str, b, a) -> str);

		match("@765", matcher, "", "765");
		match("@", matcher, "", "");
		match("@27", matcher, "@", 1, "", "");
	}

	public void testThenMaybe002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenMaybe(r.regex("[0-9]{3}"));

		match("@765", matcher, "", "");
		match("@", matcher, "", "");
		match("@27", matcher, "@", 1, "", "");
	}

	public void testThenZeroOrMore001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenZeroOrMore(r.regex("[0-9]"), (str, b, a) -> a + str);

		match("@765", matcher, "", "765");
		match("@9", matcher, "", "9");
		match("@", matcher, "", "");
	}

	public void testThenZeroOrMore002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenZeroOrMore(r.regex("[0-9]"));

		match("@765", matcher, "", "");
		match("@9", matcher, "", "");
		match("@", matcher, "", "");
	}

	public void testThenOneOrMore001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenOneOrMore(r.regex("[0-9]"), (str, b, a) -> a + str);

		match("@765", matcher, "", "765");
		match("@9", matcher, "", "9");
		nomatch("@", matcher);
	}

	public void testThenOneOrMore002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenOneOrMore(r.regex("[0-9]"));

		match("@765", matcher, "", "");
		match("@9", matcher, "", "");
		nomatch("@", matcher);
	}

	public void testThenDelimit001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenDelimit(r.regex("[0-9]"), r.string("-"), (str, b, a) -> a + str);

		match("@7-6-5", matcher, "", "765");
		match("@9", matcher, "", "9");
		nomatch("@", matcher);
	}

	public void testThenDelimit002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.string("@").thenDelimit(r.regex("[0-9]"), r.string("-"));

		match("@7-6-5", matcher, "", "");
		match("@9", matcher, "", "");
		nomatch("@", matcher);
	}

}
