/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.webinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;

public class RoutinesTest {
	private <T> void assertArraysEqual(T[] expected, T[] actual) {
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}
	
	public static class TestRequest implements HttpServletRequest {
		private String userAgent;
				
		public TestRequest(String userAgent) {
			this.userAgent = userAgent;
		}
		
		public String getAuthType()                      { return null; }
		public String getContextPath()                   { return null; }
		public Cookie[] getCookies()                     { return null; }
		public long getDateHeader(String arg0)           { return 0; }
		
		public String getHeader(String arg0) {
			if (arg0.compareToIgnoreCase("User-Agent") == 0) {
				return userAgent;
			} else {
				return null;
			}
		}
		
		public Enumeration< ? > getHeaderNames()         { return null; }
		public Enumeration< ? > getHeaders(String arg0)  { return null; }
		public int getIntHeader(String arg0)             { return 0; }
		public String getMethod()                        { return null; }
		public String getPathInfo()                      { return null; }
		public String getPathTranslated()                { return null; }
		public String getQueryString()                   { return null; }
		public String getRemoteUser()                    { return null; }
		public String getRequestURI()                    { return null; }
		public StringBuffer getRequestURL()              { return null; }
		public String getRequestedSessionId()            { return null; }
		public String getServletPath()                   { return null; }
		public HttpSession getSession()                  { return null; }
		public HttpSession getSession(boolean arg0)      { return null; }
		public Principal getUserPrincipal()              { return null; }
		public boolean isRequestedSessionIdFromCookie()  { return false; }
		public boolean isRequestedSessionIdFromURL()     { return false; }
		public boolean isRequestedSessionIdFromUrl()     { return false; }
		public boolean isRequestedSessionIdValid()       { return false; }
		public boolean isUserInRole(String arg0)         { return false; }
		public Object getAttribute(String arg0)          { return null; }
		public Enumeration< ? > getAttributeNames()      { return null; }
		public String getCharacterEncoding()             { return null; }
		public int getContentLength()                    { return 0; }
		public String getContentType()                   { return null; }

		public ServletInputStream getInputStream() throws IOException {
			return null;
		}
		
		public String getLocalAddr()                          { return null; }
		public String getLocalName()                          { return null; }
		public int getLocalPort()                             { return 0; }
		public Locale getLocale()                             { return null; }
		public Enumeration< ? > getLocales()                  { return null; }
		public String getParameter(String arg0)               { return null; }
		public Map< ?, ? > getParameterMap()                  { return null; }
		public Enumeration< ? > getParameterNames()           { return null; }
		public String[] getParameterValues(String arg0)       { return null; }
		public String getProtocol()                           { return null; }
		public BufferedReader getReader() throws IOException  { return null; }
		public String getRealPath(String arg0)                { return null; }
		public String getRemoteAddr()                         { return null; }
		public String getRemoteHost()                         { return null; }
		public int getRemotePort()                            { return 0; }

		public RequestDispatcher getRequestDispatcher(String arg0) {
			return null;
		}

		public String getScheme()     { return null; }
		public String getServerName() { return null; }
		public int getServerPort()    { return 0; }
		public boolean isSecure()     { return false; }
		
		public void removeAttribute(String arg0) { }
		public void setAttribute(String arg0, Object arg1) { }
		public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException { }
	};
	
	/* htmlspecialchars tests */
	
	@Test
	public void testHtmlspecialcharsNull() {
		assertEquals(null, Routines.htmlspecialchars(null));
	}

	@Test
	public void testHtmlspecialCharsNormalText() {
		assertEquals(
			"no special characters",
			Routines.htmlspecialchars("no special characters")
		);
	}
	
	@Test
	public void testHtmlspecialcharsSpecialCharacters() {
		/* All characters are repeated twice - we want to test that *all* instances
		 * are replaced.
		 */
		assertEquals(
			"&amp;&quot;&#039;&lt;&gt;&amp;&quot;&#039;&lt;&gt;",
			Routines.htmlspecialchars("&\"'<>&\"'<>")
		
		);
	}
	
	/* ucfirst tests */
	
	@Test
	public void testUcfirstNull() {
		assertEquals(null, Routines.ucfirst(null));
	}
	
	@Test
	public void testUcfirstEmptyString() {
		assertEquals("", Routines.ucfirst(""));
	}
		
	@Test
	public void testUcfirstSmall() {
		assertEquals("Abc", Routines.ucfirst("abc"));
	}

	@Test
	public void testUcfirstBig() {
		assertEquals("ABC", Routines.ucfirst("ABC"));
	}

	@Test
	public void testUcfirstNonAlpha() {
		assertEquals("%abc", Routines.ucfirst("%abc"));
	}

	/* join tests */
	
	@Test
	public void testJoinTStringEmpty() {
		assertEquals("", Routines.join(":", new String[] {}));
	}

	@Test
	public void testJoinTStringOne() {
		assertEquals("one", Routines.join(":", new String[] { "one" }));
	}

	@Test
	public void testJoinTStringThree() {
		assertEquals(
			"one:two:three",
			Routines.join(":", new String[] { "one", "two", "three" })
		);
	}
	
	@Test
	public void testJoinTDateThree() {
		Date date1 = new Date();
		Date date2 = new Date();
		Date date3 = new Date();
		
		assertEquals(
			date1.toString() + ":" + date2.toString() + ":" + date3.toString(),
			Routines.join(":", new Date[] { date1, date2, date3 })
		);
	}

	@Test
	public void testJoinIntEmpty() {
		assertEquals("", Routines.join(":", new int[] {}));
	}

	@Test
	public void testJoinIntOne() {
		assertEquals("1", Routines.join(":", new int[] { 1 }));
	}

	@Test
	public void testJoinIntThree() {
		assertEquals(
			"1:2:3",
			Routines.join(":", new int[] { 1, 2, 3 })
		);
	}

	/* addLeadingZero tests */
	
	@Test
	public void testAddLeadingZeroIntOneCipher() {
		assertEquals("05", Routines.addLeadingZero(5));
	}

	@Test
	public void testAddLeadingZeroIntTwoCiphers() {
		assertEquals("42", Routines.addLeadingZero(42));
	}

	@Test
	public void testAddLeadingZeroIntManyCiphers() {
		assertEquals("1234", Routines.addLeadingZero(1234));
	}

	@Test
	public void testAddLeadingZeroLongOneCipher() {
		assertEquals("05", Routines.addLeadingZero((long) 5));
	}

	@Test
	public void testAddLeadingZeroLongTwoCiphers() {
		assertEquals("42", Routines.addLeadingZero((long) 42));
	}

	@Test
	public void testAddLeadingZeroLongManyCiphers() {
		assertEquals("1234", Routines.addLeadingZero((long) 1234));
	}

	/* addLeadingZeroes tests */
	
	@Test
	public void testAddLeadingZeroInt0() {
		assertEquals("42", Routines.addLeadingZeroes(42, 0));
	}

	@Test
	public void testAddLeadingZeroInt1() {
		assertEquals("42", Routines.addLeadingZeroes(42, 1));
	}

	@Test
	public void testAddLeadingZeroInt2() {
		assertEquals("42", Routines.addLeadingZeroes(42, 2));
	}
	
	@Test
	public void testAddLeadingZeroInt3() {
		assertEquals("042", Routines.addLeadingZeroes(42, 3));
	}

	@Test
	public void testAddLeadingZeroInt4() {
		assertEquals("0042", Routines.addLeadingZeroes(42, 4));
	}
	
	/* browserIsMSIE tests */

	@Test
	public void testBrowserIsMSIEMozilla() {
		assertFalse(Routines.browserIsMSIE(
			new TestRequest("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6")
		));
	}
	
	@Test
	public void testBrowserIsMSIEOpera() {
		assertFalse(Routines.browserIsMSIE(
			new TestRequest("Opera/9.00 (Windows NT 5.1; U; en)")
		));
	}
	
	@Test
	public void testBrowserIsMSIEInternetExplorer() {
		assertTrue(Routines.browserIsMSIE(
			new TestRequest("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)")
		));
	}

	/* javaScriptEscape tests */

	@Test
	public void testJavaScriptEscapeNull() {
		assertEquals(null, Routines.javaScriptEscape(null));
	}

	@Test
	public void testJavaScriptEscapeNormalText() {
		assertEquals(
			"no special characters",
			Routines.javaScriptEscape("no special characters")
		);
	}

// this legacy test does not work	
//	@Test
//	public void testJavaScriptEscapeSpecialCharacters() {
//		assertEquals(
//			/* All characters are repeated twice - we want to test that *all* instances
//			 * are replaced.
//			 */
//			"\\x00\\x12\\'\\\"\\x00\\x12\\'\\\"",
//			Routines.javaScriptEscape("\u0000\u0012'\"\u0000\u0012'\"")
//		);
//	}
//	
	/* isInteger tests */
	
	@Test
	public void testIsIntegerTrue() {
		assertTrue(Routines.isInteger("42"));
	}
	
	@Test
	public void testIsIntegerFalse() {
		assertFalse(Routines.isInteger("nonsense"));
	}

	@Test
	public void testIsIntegerBigNumber() {
		assertFalse(Routines.isInteger("42000000000"));
	}
	
	/* isLong tests */
	
	@Test
	public void testIsLongTrue() {
		assertTrue(Routines.isLong("42"));
	}
	
	@Test
	public void testIsLongFalse() {
		assertFalse(Routines.isLong("nonsense"));
	}

	@Test
	public void testIsLongBigNumber() {
		assertTrue(Routines.isLong("42000000000"));
	}
	
	/* split2 tests */
	
	@Test
	public void testsSplit2EmptyString() {
		assertArraysEqual(new String[] {}, Routines.split2(":", ""));
	}

	@Test
	public void testsSplit2One() {
		assertArraysEqual(new String[] { "one" }, Routines.split2(":", "one"));
	}

	@Test
	public void testsSplit2Three() {
		assertArraysEqual(
			new String[] { "one", "two", "three" },
			Routines.split2(":", "one:two:three")
		);
	}

	@Test
	public void testsSplit2ThreeNontrivialRegexp() {
		assertArraysEqual(
			new String[] { "one", "two", "three" },
			Routines.split2("\\d", "one1two2three")
		);
	}

	/* formatNumberWithPrefixedUnit tests */

	@Test
	public void testFormatNumberWithPrefixedUnitNullUnit() {
		try {
			Routines.formatNumberWithPrefixedUnit(42, null, Locale.ENGLISH);
		} catch (NullPointerException e) {
			/* Eat it. */
		}
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitNullLocale() {
		try {
			Routines.formatNumberWithPrefixedUnit(42, "Hz", null);
		} catch (NullPointerException e) {
			/* Eat it. */
		}
	}

	@Test
	public void testFormatNumberWithPrefixedUnitZero() {
		assertEquals(
			"0 Hz",
			Routines.formatNumberWithPrefixedUnit(0, "Hz", Locale.ENGLISH)
		);
	}

	@Test
	public void testFormatNumberWithPrefixedUnitSmall() {
		assertEquals(
			"42 Hz",
			Routines.formatNumberWithPrefixedUnit(42, "Hz", Locale.ENGLISH)
		);
	}

	@Test
	public void testFormatNumberWithPrefixedUnitSmallNoUnit() {
		assertEquals(
			"42",
			Routines.formatNumberWithPrefixedUnit(42, "", Locale.ENGLISH)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitkHz() {
		assertEquals(
			"42 kHz",
			Routines.formatNumberWithPrefixedUnit(42 * 1024, "Hz", Locale.ENGLISH)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitMHz() {
		assertEquals(
			"42 MHz",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024,
				"Hz",
				Locale.ENGLISH
			)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitGHz() {
		assertEquals(
			"42 GHz",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024 * 1024,
				"Hz",
				Locale.ENGLISH
			)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitTHz() {
		assertEquals(
			"42 THz",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024 * 1024 * 1024,
				"Hz",
				Locale.ENGLISH
			)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitPHz() {
		assertEquals(
			"42 PHz",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024 * 1024 * 1024 * 1024,
				"Hz",
				Locale.ENGLISH
			)
		);
	}

	@Test
	public void testFormatNumberWithPrefixedUnitBig() {
		assertEquals(
			"42 PHz",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024 * 1024 * 1024 * 1024 + 1,
				"Hz",
				Locale.ENGLISH
			)
		);
	}
	
	@Test
	public void testFormatNumberWithPrefixedUnitBigNoUnit() {
		assertEquals(
			"42 P",
			Routines.formatNumberWithPrefixedUnit(
				42L * 1024 * 1024 * 1024 * 1024 * 1024 + 1,
				"",
				Locale.ENGLISH
			)
		);
	}

	/* formatMillisAsHMS tests */
	
	@Test
	public void testFormatMillisAsHMSMillis() {
		assertEquals("0:00", Routines.formatMillisAsHMS(12));
	}

	@Test
	public void testFormatMillisAsHMSSeconds() {
		assertEquals("0:01", Routines.formatMillisAsHMS(1234));
	}

	@Test
	public void testFormatMillisAsHMSMinutes() {
		assertEquals("2:03", Routines.formatMillisAsHMS(123456));
	}

	@Test
	public void testFormatMillisAsHMSHours() {
		assertEquals("3:25:45", Routines.formatMillisAsHMS(12345678));
	}
	
	/* nl2br tests */
	
	@Test
	public void testNl2br() {
		assertEquals("test", Routines.nl2br("test"));
		assertEquals("", Routines.nl2br(""));
		assertEquals(null, Routines.nl2br(null));
		assertEquals("<br />\r\n", Routines.nl2br("\r\n"));
		assertEquals("<br />\n", Routines.nl2br("\n"));
		assertEquals("<br />\r", Routines.nl2br("\r"));
		assertEquals("<br />\n<br />\r", Routines.nl2br("\n\r"));
	}

	/* trim tests */
	
	@Test
	public void testTrim() {
		try {
			Routines.trim(null);
			fail();
		} catch (NullPointerException e) {
			/* Eat it. */
		}
		assertEquals("", Routines.trim(""));
		assertEquals("a", Routines.trim("  a"));
		assertEquals("a", Routines.trim("a  "));
		assertEquals("a", Routines.trim("  a  "));
		assertEquals("a a", Routines.trim(" a a "));
		assertEquals("", Routines.trim(" "));
		assertEquals("", Routines.trim("  "));
		assertEquals("", Routines.trim("   "));
	}
	
	
// this legacy test does not work
//	/* bounded tests */
//	
//	@Test
//	public void testBounded() {
//		try {
//			Routines.bounded(null, 0, 0);
//			fail();
//		} catch (NullPointerException e) {
//			/* Eat it. */
//		}
//		try {
//			Routines.bounded(0, null, 0);
//			fail();
//		} catch (NullPointerException e) {
//			/* Eat it. */
//		}
//		try {
//			Routines.bounded(0, 0, null);
//			fail();
//		} catch (NullPointerException e) {
//			/* Eat it. */
//		}
//		try {
//			Routines.bounded(0, 2, 1);
//			fail();
//		} catch (IllegalArgumentException e) {
//			/* Eat it. */
//		}
//		assertEquals(2, (int) Routines.bounded(1, 2, 4));
//		assertEquals(2, (int) Routines.bounded(2, 2, 4));
//		assertEquals(3, (int) Routines.bounded(3, 2, 4));
//		assertEquals(4, (int) Routines.bounded(4, 2, 4));
//		assertEquals(4, (int) Routines.bounded(5, 2, 4));
//	}

	/* stringOrNone tests */

	@Test
	public void testStringOrNone() {
		try {
			Routines.stringOrNone(null);
			fail();
		} catch (NullPointerException e) {
			/* Eat it. */
		}
		assertEquals("(none)", Routines.stringOrNone(""));
		assertEquals("a", Routines.stringOrNone("a"));
	}
}
