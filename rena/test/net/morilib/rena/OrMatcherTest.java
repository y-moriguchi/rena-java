/*
 * rena-java
 *
 * Copyright (c) 2018 Yuichiro MORIGUCHI
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 **/
package net.morilib.rena;

public class OrMatcherTest extends TestCaseBase {

	public void testOr001() {
		Rena<String> r = new Rena<String>();
		OperationMatcher<String> matcher = r.string("765").or(r.string("346"));

		match("765pro", matcher, "765", 3, "", "");
		match("346pro", matcher, "346", 3, "", "");
		nomatch("961pro", matcher);
	}

}
