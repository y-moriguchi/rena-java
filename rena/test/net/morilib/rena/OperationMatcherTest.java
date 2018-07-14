/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class OperationMatcherTest extends TestCaseBase {

	public void testTimes001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").times(3, 5, (m, b, a) -> a + m.toUpperCase());

		match("pro", matcher, "pro", 3, "765", "765PRO");
		match("puro", matcher, "puro", 4, "765", "765PURO");
		match("aaaaa", matcher, "aaaaa", 5, "765", "765AAAAA");
		match("pro765", matcher, "pro", 3, "765", "765PRO");
		match("aaaaaa", matcher, "aaaaa", 5, "765", "765AAAAA");
		nomatch("pr", matcher);
		nomatch("2pro", matcher);
		nomatch("961", matcher);
	}

	public void testTimes002() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").times(3, 5);

		match("pro", matcher, "pro", 3, "765", "765");
		match("puro", matcher, "puro", 4, "765", "765");
		match("aaaaa", matcher, "aaaaa", 5, "765", "765");
		match("pro765", matcher, "pro", 3, "765", "765");
		match("produc", matcher, "produ", 5, "", "");
		nomatch("pr", matcher);
		nomatch("2pro", matcher);
		nomatch("961", matcher);
	}

	public void testTimes003() {
		Rena<String> r = new Rena<String>("[ \t]+");
		OperationMatcher<String> matcher = r.regex("[a-z]").times(3, 5);

		match("p  ro", matcher, "p  ro", 5, "765", "765");
		match("p  u  ro", matcher, "p  u  ro", 8, "765", "765");
		match("a   a  a  a  a", matcher, "a   a  a  a  a", 14, "765", "765");
		match("p  ro 7 6 5", matcher, "p  ro ", 6, "765", "765");
		match("p   r  o  d  u  c", matcher, "p   r  o  d  u", 14, "", "");
		nomatch("p  r", matcher);
		nomatch("2  p ro", matcher);
		nomatch("9  6  1", matcher);
	}

	public void testTimes004() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").times(0, -1, (m, b, a) -> a + m.toUpperCase());

		match("", matcher, "", 0, "765", "765");
		match("pro", matcher, "pro", 3, "765", "765PRO");
		match("pro765", matcher, "pro", 3, "765", "765PRO");
		match("2pro", matcher, "", 0, "", "");
	}

	public void testTimes005() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").times(4, 4, (m, b, a) -> a + m.toUpperCase());

		match("abcd", matcher, "abcd", 4, "", "ABCD");
		match("abcd123", matcher, "abcd", 4, "", "ABCD");
		match("abcde", matcher, "abcd", 4, "", "ABCD");
		nomatch("pro", matcher);
		nomatch("2pro", matcher);
	}

	public void testTimes006() {
		Rena<Integer> r = new Rena<Integer>();
		OperationMatcher<Integer> matcher =
				r.regex("[0-9]", (m, a, b) -> Integer.parseInt(m)).times(3, 5, (m, b, a) -> a + b);

		match("765", matcher, "765", 3, 0, 7 + 6 + 5);
	}

	public void testTimes901() {
		final Rena<String> r = new Rena<String>();

		assertArgumentException(() -> r.string("a").times(-1, 1));
		assertArgumentException(() -> r.string("a").times(0, 0));
		assertArgumentException(() -> r.string("a").times(4, 3));
	}

	public void testAtLeast001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").atLeast(3, (m, b, a) -> a + m.toUpperCase());

		match("abc", matcher, "abc", 3, "", "ABC");
		match("abcde", matcher, "abcde", 5, "", "ABCDE");
		match("abc123", matcher, "abc", 3, "", "ABC");
		nomatch("bc", matcher);
		nomatch("2bcd", matcher);
		nomatch("961", matcher);
	}

	public void testAtMost001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").atMost(3, (m, b, a) -> a + m.toUpperCase());

		match("", matcher, "", 0, "", "");
		match("abc", matcher, "abc", 3, "", "ABC");
		match("abc123", matcher, "abc", 3, "", "ABC");
		match("abcd", matcher, "abc", 3, "", "ABC");
		match("2bcd", matcher, "", 0, "", "");
	}

	public void testMaybe001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").maybe((m, b, a) -> a + m.toUpperCase());

		match("", matcher, "", 0, "", "");
		match("a", matcher, "a", 1, "", "A");
		match("ab", matcher, "a", 1, "", "A");
		match("a123", matcher, "a", 1, "", "A");
		match("765", matcher, "", 0, "", "");
	}

	public void testZeroOrMore001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").zeroOrMore((m, b, a) -> a + m.toUpperCase());

		match("", matcher, "", 0, "765", "765");
		match("pro", matcher, "pro", 3, "765", "765PRO");
		match("pro765", matcher, "pro", 3, "765", "765PRO");
		match("2pro", matcher, "", 0, "765", "765");
	}

	public void testOneOrMore001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[a-z]").oneOrMore((m, b, a) -> a + m.toUpperCase());

		match("a", matcher, "a", 1, "765", "765A");
		match("pro", matcher, "pro", 3, "765", "765PRO");
		match("pro765", matcher, "pro", 3, "765", "765PRO");
		nomatch("", matcher);
		nomatch("2pro", matcher);
		nomatch("961", matcher);
	}

	public void testDemilit001() {
		Rena<Integer> r = new Rena<Integer>();
		OperationMatcher<Integer> matcher = r.regex("[0-9]+", (m, b, a) -> Integer.parseInt(m))
				.delimit(r.string("+"), (m, b, a) -> a + b);

		match("765+346", matcher, "765+346", 7, 0, 1111);
		match("765+346+876", matcher, "765+346+876", 11, 0, 1111 + 876);
		match("765", matcher, "765", 3, 0, 765);
		match("765+", matcher, "765", 3, 0, 765);
		match("765+pro", matcher, "765", 3, 0, 765);
		match("765pro", matcher, "765", 3, 0, 765);
		match("765+346+", matcher, "765+346", 7, 0, 1111);
		match("765+346+pro", matcher, "765+346", 7, 0, 1111);
		match("765+346pro", matcher, "765+346", 7, 0, 1111);
		nomatch("", matcher);
		nomatch("aaa", matcher);
		nomatch("+765", matcher);
	}

	public void testDemilit002() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[0-9]+").delimit(r.string("+"), (m, b, a) -> a + m);

		match("765+346", matcher, "765+346", 7, "", "765346");
	}

	public void testDemilit003() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.regex("[0-9]+").delimit(r.string("+"));

		match("765+346", matcher, "765+346", 7, "", "");
	}

	public void testCond001() {
		Rena<Integer> r = new Rena<Integer>();
		OperationMatcher<Integer> matcher = r.regex("[0-9]+", (m, b, a) -> Integer.parseInt(m))
				.cond(x -> x == 765 || x == 346);

		match("765", matcher, "765", 3, 0, 765);
		nomatch("961", matcher);
	}

}
