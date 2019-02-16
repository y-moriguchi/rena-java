/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

import java.util.ArrayList;
import java.util.List;

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
		OperationMatcher<String> matcher = r.then(
				(match, ind, a) -> match.startsWith("765", ind) ? new PatternResult<String>("765", ind + 3, a) : null,
				(match, attr, inherit) -> match);

		match("765pro", matcher, "765", 3, "", "765");
		nomatch("961pro", matcher);
		nomatch("76", matcher);
		nomatch("8765", matcher);
	}

	public void testMatcher2() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.then(
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

	public void testNotKey001() {
		Rena<String> r = new Rena<String>(new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.notKey();

		match("/", matcher, "", 0, "", "");
		match("@", matcher, "", 0, "", "");
		match("@+", matcher, "", 0, "", "");
		nomatch("+", matcher);
		nomatch("++", matcher);
		nomatch("//", matcher);
		nomatch("+++", matcher);
		nomatch("///", matcher);
	}

	public void testBr001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.br();

		match("\r\n", matcher, "\r\n", 2, "", "");
		match("\r", matcher, "\r", 1, "", "");
		match("\n", matcher, "\n", 1, "", "");
		match("\r\na", matcher, "\r\n", 2, "", "");
		match("\ra", matcher, "\r", 1, "", "");
		match("\na", matcher, "\n", 1, "", "");
		nomatch("a\r\n", matcher);
		nomatch("a\n", matcher);
		nomatch("a\r", matcher);
	}

	public void testEqualsId001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.equalsId("id");

		match("id", matcher, "id", 2, "", "");
		match("ident", matcher, "id", 2, "", "");
		nomatch("iid", matcher);
		nomatch("s id", matcher);
	}

	public void testEqualsId002() {
		Rena<String> r = new Rena<String>("[ \t]+");
		OperationMatcher<String> matcher = r.equalsId("id");

		match("id", matcher, "id", 2, "", "");
		match("id ent", matcher, "id", 2, "", "");
		nomatch("sid", matcher);
		nomatch("s id", matcher);
		nomatch("ident", matcher);
	}

	public void testEqualsId003() {
		Rena<String> r = new Rena<String>(new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.equalsId("id");

		match("id", matcher, "id", 2, "", "");
		match("id+", matcher, "id", 2, "", "");
		match("id++", matcher, "id", 2, "", "");
		match("id//", matcher, "id", 2, "", "");
		nomatch("sid", matcher);
		nomatch("s id", matcher);
		nomatch("ident", matcher);
		nomatch("id/", matcher);
	}

	public void testEqualsId004() {
		Rena<String> r = new Rena<String>("[ \t]+", new String[] { "+", "++", "//" });
		OperationMatcher<String> matcher = r.equalsId("id");

		match("id", matcher, "id", 2, "", "");
		match("id ent", matcher, "id", 2, "", "");
		match("id+", matcher, "id", 2, "", "");
		match("id++", matcher, "id", 2, "", "");
		match("id//", matcher, "id", 2, "", "");
		nomatch("sid", matcher);
		nomatch("s id", matcher);
		nomatch("ident", matcher);
		nomatch("id/", matcher);
	}

	public void testReal001() {
		Rena<Double> r = new Rena<Double>();
		OperationMatcher<Double> matcher = r.real(false, (str, b, a) -> Double.parseDouble(str));

		match("0", matcher, -1.0, 0.0);
		match("765", matcher, -1.0, 765.0);
		match("76.5", matcher, -1.0, 76.5);
		match("0.765", matcher, -1.0, 0.765);
		match(".765", matcher, -1.0, 0.765);
		match("765e2", matcher, -1.0, 765e2);
		match("765E2", matcher, -1.0, 765E2);
		match("765e+2", matcher, -1.0, 765e+2);
		match("765e-2", matcher, -1.0, 765e-2);
		match("765e+346", matcher, -1.0, Double.POSITIVE_INFINITY);
		match("765e-346", matcher, -1.0, 0.0);
		nomatch("id/", matcher);
	}

	public void testReal002() {
		Rena<Double> r = new Rena<Double>();
		OperationMatcher<Double> matcher = r.real(true, (str, b, a) -> Double.parseDouble(str));

		match("0", matcher, -1.0, 0.0);
		match("765", matcher, -1.0, 765.0);
		match("76.5", matcher, -1.0, 76.5);
		match("0.765", matcher, -1.0, 0.765);
		match(".765", matcher, -1.0, 0.765);
		match("765e2", matcher, -1.0, 765e2);
		match("765E2", matcher, -1.0, 765E2);
		match("765e+2", matcher, -1.0, 765e+2);
		match("765e-2", matcher, -1.0, 765e-2);
		match("765e+346", matcher, -1.0, Double.POSITIVE_INFINITY);
		match("765e-346", matcher, -1.0, 0.0);
		match("+0", matcher, -1.0, 0.0);
		match("+765", matcher, -1.0, 765.0);
		match("+76.5", matcher, -1.0, 76.5);
		match("+0.765", matcher, -1.0, 0.765);
		match("+.765", matcher, -1.0, 0.765);
		match("+765e2", matcher, -1.0, 765e2);
		match("+765E2", matcher, -1.0, 765E2);
		match("+765e+2", matcher, -1.0, 765e+2);
		match("+765e-2", matcher, -1.0, 765e-2);
		match("+765e+346", matcher, -1.0, Double.POSITIVE_INFINITY);
		match("+765e-346", matcher, -1.0, 0.0);
		match("-0", matcher, -1.0, -0.0);
		match("-765", matcher, -1.0, -765.0);
		match("-76.5", matcher, -1.0, -76.5);
		match("-0.765", matcher, -1.0, -0.765);
		match("-.765", matcher, -1.0, -0.765);
		match("-765e2", matcher, -1.0, -765e2);
		match("-765E2", matcher, -1.0, -765E2);
		match("-765e+2", matcher, -1.0, -765e+2);
		match("-765e-2", matcher, -1.0, -765e-2);
		match("-765e+346", matcher, -1.0, -Double.POSITIVE_INFINITY);
		match("-765e-346", matcher, -1.0, -0.0);
		nomatch("id/", matcher);
	}

	public void testOr001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.or(r.string("765"), r.string("346"));

		match("765", matcher, "", "");
		match("346", matcher, "", "");
		nomatch("961", matcher);
	}

	public void testOr002() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.or(r.string("765"), r.string("346"), r.string("876"));

		match("765", matcher, "", "");
		match("346", matcher, "", "");
		match("876", matcher, "", "");
		nomatch("961", matcher);
	}

	public void testOr003() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.or(r.string("765"), r.string("346"), r.string("876"), r.string("283"));

		match("765", matcher, "", "");
		match("346", matcher, "", "");
		match("876", matcher, "", "");
		match("283", matcher, "", "");
		nomatch("961", matcher);
	}

	public void testOr004() {
		Rena<String> r = new Rena<String>();
		List<PatternMatcher<String>> list = new ArrayList<>();
		list.add(r.string("765"));
		list.add(r.string("346"));
		OperationMatcher<String> matcher = r.or(list);

		match("765", matcher, "", "");
		match("346", matcher, "", "");
		nomatch("961", matcher);
	}

	public void testOr005() {
		Rena<String> r = new Rena<String>();
		List<PatternMatcher<String>> list = new ArrayList<>();
		list.add(r.string("765"));
		OperationMatcher<String> matcher = r.or(list);

		match("765", matcher, "", "");
		nomatch("346", matcher);
		nomatch("961", matcher);
	}

	public void testOr901() {
		Rena<String> r = new Rena<String>();
		List<PatternMatcher<String>> list = new ArrayList<>();

		try {
			r.or(list);
			fail();
		} catch(IllegalArgumentException e) {
			// ok
		}
	}

	public void testTimes001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.times(1, 3, r.regex("[0-9]"), (str, b, a) -> a + str, "");

		match("765", matcher, "961", "765");
		match("7653", matcher, "765", 3, "961", "765");
		nomatch("", matcher);
	}

	public void testTimes002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.times(1, 3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("765", matcher, "", "765");
		match("7653", matcher, "765", 3, "", "765");
		nomatch("", matcher);
	}

	public void testTimes003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.times(1, 3, r.regex("[0-9]"));

		match("765", matcher, "", "");
		match("7653", matcher, "765", 3, "", "");
		nomatch("", matcher);
	}

	public void testAtLeast001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atLeast(3, r.regex("[0-9]"), (str, b, a) -> a + str, "");

		match("765", matcher, "961", "765");
		match("7653", matcher, "961", "7653");
		nomatch("71", matcher);
	}

	public void testAtLeast002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atLeast(3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("765", matcher, "", "765");
		match("7653", matcher, "", "7653");
		nomatch("71", matcher);
	}

	public void testAtLeast003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atLeast(3, r.regex("[0-9]"));

		match("765", matcher, "", "");
		match("7653", matcher, "", "");
		nomatch("71", matcher);
	}

	public void testAtMost001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atMost(3, r.regex("[0-9]"), (str, b, a) -> a + str, "");

		match("765", matcher, "961", "765");
		match("27", matcher, "961", "27");
		match("7653", matcher, "765", 3, "961", "765");
	}

	public void testAtMost002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atMost(3, r.regex("[0-9]"), (str, b, a) -> a + str);

		match("765", matcher, "", "765");
		match("27", matcher, "", "27");
		match("7653", matcher, "765", 3, "", "765");
	}

	public void testAtMost003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.atMost(3, r.regex("[0-9]"));

		match("765", matcher, "", "");
		match("27", matcher, "", "");
		match("7653", matcher, "765", 3, "", "");
	}

	public void testMaybe001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.maybe(r.regex("[0-9]{3}"), (str, b, a) -> str);

		match("765", matcher, "", "765");
		match("", matcher, "", "");
		match("27", matcher, "", 0, "", "");
	}

	public void testMaybe002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.maybe(r.regex("[0-9]{3}"));

		match("765", matcher, "", "");
		match("", matcher, "", "");
		match("27", matcher, "", 0, "", "");
	}

	public void testZeroOrMore001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.zeroOrMore(r.regex("[0-9]"), (str, b, a) -> a + str, "");

		match("765", matcher, "961", "765");
		match("9", matcher, "961", "9");
		match("", matcher, "", "");
	}

	public void testZeroOrMore002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.zeroOrMore(r.regex("[0-9]"), (str, b, a) -> a + str);

		match("765", matcher, "", "765");
		match("9", matcher, "", "9");
		match("", matcher, "", "");
	}

	public void testZeroOrMore003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.zeroOrMore(r.regex("[0-9]"));

		match("765", matcher, "", "");
		match("9", matcher, "", "");
		match("", matcher, "", "");
	}

	public void testOneOrMore001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.oneOrMore(r.regex("[0-9]"), (str, b, a) -> a + str, "");

		match("765", matcher, "961", "765");
		match("9", matcher, "961", "9");
		nomatch("", matcher);
	}

	public void testOneOrMore002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.oneOrMore(r.regex("[0-9]"), (str, b, a) -> a + str);

		match("765", matcher, "", "765");
		match("9", matcher, "", "9");
		nomatch("", matcher);
	}

	public void testOneOrMore003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.oneOrMore(r.regex("[0-9]"));

		match("765", matcher, "", "");
		match("9", matcher, "", "");
		nomatch("", matcher);
	}

	public void testDelimit001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.delimit(r.regex("[0-9]"), r.string("-"), (str, b, a) -> a + str, "");

		match("7-6-5", matcher, "961", "765");
		match("9", matcher, "961", "9");
		nomatch("", matcher);
	}

	public void testDelimit002() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.delimit(r.regex("[0-9]"), r.string("-"), (str, b, a) -> a + str);

		match("7-6-5", matcher, "", "765");
		match("9", matcher, "", "9");
		nomatch("", matcher);
	}

	public void testDelimit003() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.delimit(r.regex("[0-9]"), r.string("-"));

		match("7-6-5", matcher, "", "");
		match("9", matcher, "", "");
		nomatch("", matcher);
	}

	public void testAttr001() {
		Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher = r.attr("765");

		match("", matcher, "961", "765");
	}

	public void testLetrec001() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec(x -> r.then(r.string("(").then(x).then(r.string(")"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("((()))", matcher, "((()))", 6, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
	}

	public void testLetrec002() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec((x, y) -> r.then(r.string("(").then(y).then(r.string(")"))).maybe(),
						(x, y) -> r.then(r.string("[").then(x).then(r.string("]"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("([()])", matcher, "([()])", 6, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
		match("(())", matcher, "", 0, "", "");
	}

	public void testLetrec003() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec((x, y, z) -> r.then(r.string("(").then(y).then(r.string(")"))).maybe(),
						(x, y, z) -> r.then(r.string("[").then(z).then(r.string("]"))).maybe(),
						(x, y, z) -> r.then(r.string("{").then(x).then(r.string("}"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("([{()}])", matcher, "([{()}])", 8, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
		match("([()])", matcher, "", 0, "", "");
	}

	public void testLetrec004() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec((x, y, z, w) -> r.then(r.string("(").then(y).then(r.string(")"))).maybe(),
						(x, y, z, w) -> r.then(r.string("[").then(z).then(r.string("]"))).maybe(),
						(x, y, z, w) -> r.then(r.string("{").then(w).then(r.string("}"))).maybe(),
						(x, y, z, w) -> r.then(r.string("<").then(x).then(r.string(">"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("([{<()>}])", matcher, "([{<()>}])", 10, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
		match("([{()}])", matcher, "", 0, "", "");
	}

	public void testLetrec005() {
		final Rena<String> r = new Rena<String>();
		PatternMatcher<String> matcher =
				Rena.letrec((x, y, z, w, v) -> r.then(r.string("(").then(y).then(r.string(")"))).maybe(),
						(x, y, z, w, v) -> r.then(r.string("[").then(z).then(r.string("]"))).maybe(),
						(x, y, z, w, v) -> r.then(r.string("{").then(w).then(r.string("}"))).maybe(),
						(x, y, z, w, v) -> r.then(r.string("<").then(v).then(r.string(">"))).maybe(),
						(x, y, z, w, v) -> r.then(r.string("|").then(x).then(r.string("|"))).maybe());

		match("()", matcher, "()", 2, "", "");
		match("([{<|()|>}])", matcher, "([{<|()|>}])", 12, "", "");
		match("", matcher, "", 0, "", "");
		match("(()", matcher, "", 0, "", "");
		match("([{<()>}])", matcher, "", 0, "", "");
	}

}
