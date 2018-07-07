/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class RenaTest extends TestCaseBase {

	public void testString() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.string("765", (match, attr, inherit) -> match);

		match("765pro", matcher, "765", 3, "", "765");
		nomatch("961pro", matcher);
		nomatch("76", matcher);
		nomatch("8765", matcher);
	}

	public void testRegex() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[0-9]+", (match, attr, inherit) -> match);

		match("765pro", matcher, "765", 3, "", "765");
		match("346pro", matcher, "346", 3, "", "346");
		nomatch("pro", matcher);
		nomatch("p961", matcher);
	}

	public void testMatcher1() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.matcher(
				(match, ind, a) -> match.startsWith("765", ind) ? new PatternResult<String>("765", ind + 3, a) : null,
				(match, attr, inherit) -> match);

		match("765pro", matcher, "765", 3, "", "765");
		nomatch("961pro", matcher);
		nomatch("76", matcher);
		nomatch("8765", matcher);
	}

	public void testMatcher2() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.matcher(
				(match, ind, a) -> match.startsWith("765", ind) ? new PatternResult<String>("765", ind + 3, a) : null);

		match("765pro", matcher, "765", 3, "", "");
		nomatch("961pro", matcher);
		nomatch("76", matcher);
		nomatch("8765", matcher);
	}

	public void testKey001() {
		Rena<String> r = new Rena<String>(new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.key("++");

		match("++", matcher, "++", 2, "", "");
		match("+++", matcher, "++", 2, "", "");
		nomatch("+", matcher);
		nomatch("-", matcher);
	}

	public void testKey002() {
		Rena<String> r = new Rena<String>(new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.key("+");

		match("+", matcher, "+", 1, "", "");
		nomatch("++", matcher);
		nomatch("-", matcher);
	}

	public void testKey003() {
		Rena<String> r = new Rena<String>(new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.key("//");

		match("//", matcher, "//", 2, "", "");
		match("///", matcher, "//", 2, "", "");
		nomatch("/", matcher);
		nomatch("++", matcher);
		nomatch("-", matcher);
	}

	public void testLetrec001() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec(x -> r.matcher(r.string("(").then(x).then(r.string(")"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("((()))", matcher, "((()))", 6, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
	}

}
