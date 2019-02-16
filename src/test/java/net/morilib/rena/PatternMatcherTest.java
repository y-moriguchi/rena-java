/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package net.morilib.rena;

import java.util.List;

public class PatternMatcherTest extends TestCaseBase {

	static PatternMatcher<String> MATCHER = (str, index, attr) -> {
		if(str.startsWith("765", index) || str.startsWith("346", index)) {
			return new PatternResult<String>(str.substring(index, index + 3), index + 3, attr);
		} else {
			return null;
		}
	};

	static PatternMatcher<String> MATCHER2 = (str, index, attr) -> {
		if(str.startsWith("765", index) || str.startsWith("346", index)) {
			return new PatternResult<String>(
					str.substring(index, index + 3), index + 3, str.substring(index, index + 3));
		} else {
			return null;
		}
	};

	private void testMatch(PatternResult<String> result, String match, int index, String attr) {
		if(result == null) {
			fail();
		}
		assertEquals(result.getMatch(), match);
		assertEquals(result.getLastIndex(), index);
		assertEquals(result.getAttribute(), attr);
	}

	private void testMatch(PatternResult<String> result, String match, String attr) {
		testMatch(result, match, match.length(), attr);
	}

	public void testMatch001() {
		testMatch(MATCHER.match("765", ""), "765", "");
		testMatch(MATCHER.match("765a", ""), "765", 3, "");
		assertNull(MATCHER.match("a765", ""));
	}

	public void testParse001() {
		testMatch(MATCHER.parse("765", ""), "765", "");
		testMatch(MATCHER.parse("765a", ""), "765", 3, "");
		assertNull(MATCHER.parse("a765", ""));
	}

	public void testParsePart001() {
		testMatch(MATCHER.parsePart("aaa765", 3, ""), "765", 6, "");
		testMatch(MATCHER.parsePart("aaa765a", 3, ""), "765", 6, "");
		testMatch(MATCHER.parsePart("aaaa765", 3, ""), "765", 7, "");
		assertNull(MATCHER.parsePart("aa765", 3, ""));
	}

	public void testParsePart002() {
		testMatch(MATCHER.parsePart("aaa765", ""), "765", 6, "");
		testMatch(MATCHER.parsePart("aaa765a", ""), "765", 6, "");
		testMatch(MATCHER.parsePart("aa765", ""), "765", 5, "");
		testMatch(MATCHER.parsePart("765", ""), "765", 3, "");
	}

	public void testParsePartGlobal001() {
		assertEquals(MATCHER2.parsePartGlobal("aa765aaaa346aaaa765", 3, "", (b, a) -> a + b), "346765");
		assertEquals(MATCHER2.parsePartGlobal("aaaaaaaaaaa", 3, "", (b, a) -> a + b), "");
	}

	public void testParsePartGlobal002() {
		assertEquals(MATCHER2.parsePartGlobal("aa765aaaa346aaaa765", "", (b, a) -> a + b), "765346765");
		assertEquals(MATCHER2.parsePartGlobal("aaaaaaaaaaa", "", (b, a) -> a + b), "");
	}

	public void testParsePartGlobalList001() {
		List<String> list;

		list = MATCHER2.parsePartGlobalList("aa765aaaa346aaaa765", 3);
		assertEquals(list.size(), 2);
		assertEquals(list.get(0), "346");
		assertEquals(list.get(1), "765");

		list = MATCHER2.parsePartGlobalList("aaaaaaaaaaaa", 3);
		assertEquals(list.size(), 0);
	}

	public void testParsePartGlobalList002() {
		List<String> list;

		list = MATCHER2.parsePartGlobalList("aa765aaaa346aaaa765");
		assertEquals(list.size(), 3);
		assertEquals(list.get(0), "765");
		assertEquals(list.get(1), "346");
		assertEquals(list.get(2), "765");

		list = MATCHER2.parsePartGlobalList("aaaaaaaaaaaa");
		assertEquals(list.size(), 0);
	}

}
