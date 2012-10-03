package cz.cuni.mff.been.utils;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.mff.been.utils.ArrayUtils;

/**
 * 
 * @author Tadeáš Palusga
 *
 */
public class ArrayUtilsTest extends Assert {

	//
	// TESTS BEGIN
	//

	// TEST JOIN ******************************

	@Test
	public void test_Join_EmptyArray_ReturnsEmptyString() throws Exception {
		assertEquals("", ArrayUtils.join(",", new String[0]));
	}

	@Test
	public void test_Join_OneObjectArray_ReturnsOnlyThisObjectWithoutToken()
			throws Exception {
		assertEquals("test", ArrayUtils.join(",", new String[] { "test" }));
	}

	@Test
	public void test_Join_MultipleObjectArray_ReturnsObjectsDelimitedByToken()
			throws Exception {
		assertEquals("test1,test2",
				ArrayUtils.join(",", new String[] { "test1", "test2" }));
		/*
		 * FIXME -> null token is not determined -> example:
		 * assertEquals("test1test2",ArrayUtils.join(null,new
		 * String[]{"test1","test2"}));
		 */
	}

	@Test
	public void test_Join_UsesToStringMethodOnObject() throws Exception {
		final String toString1 = "tostring1";
		final String toString2 = "tostring2";
		Object o1 = objectWithToStringVal(toString1);
		Object o2 = objectWithToStringVal(toString2);
		String token = "TOKEN";
		assertEquals(toString1 + token + toString2,
				ArrayUtils.join(token, new Object[] { o1, o2 }));
	}

	@Test(expected = NullPointerException.class)
	public void test_Join_NullArray_ThrowsNPE() throws Exception {
		ArrayUtils.join(",", null);
	}

	// TEST REVERSE ***************************
	
	@Test
	public void test_Reverse_EmptyArray() throws Exception {
		Object[] arr = new Object[0];
		ArrayUtils.reverse(arr);
		assertEquals(0, arr.length);
	}
	
	@Test
	public void test_Reverse_OneObjectArray() throws Exception {
		Object obj = new Object();
		Object[] arr = new Object[] {obj};
		ArrayUtils.reverse(arr);
		assertEquals(1, arr.length);
		assertSame(obj,  arr[0]);
	}	
	
	@Test
	public void test_Reverse_EvenLengthArray() throws Exception {
		Object obj1 = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		Object[] arr = new Object[] {obj1, obj2, obj3};
		ArrayUtils.reverse(arr);
		assertEquals(3, arr.length);
		assertSame(obj1,  arr[2]);
		assertSame(obj2,  arr[1]);
		assertSame(obj3,  arr[0]);
	}
	
	@Test
	public void test_Reverse_OddLengthArray() throws Exception {
		Object obj1 = new Object();
		Object obj2 = new Object();
		Object obj3 = new Object();
		Object obj4 = new Object();
		Object[] arr = new Object[] {obj1, obj2, obj3, obj4};
		ArrayUtils.reverse(arr);
		assertEquals(4, arr.length);
		assertSame(obj1,  arr[3]);
		assertSame(obj2,  arr[2]);
		assertSame(obj3,  arr[1]);
		assertSame(obj4,  arr[0]);
	}
	
	@Test(expected = NullPointerException.class)
	public void test_Reverse_NullArray_ThrowsNPE() throws Exception {
		ArrayUtils.reverse(null);
	}
	
	//
	// TESTS END
	//

	private Object objectWithToStringVal(final String toStringValue) {
		return new Object() {
			@Override
			public String toString() {
				return toStringValue;
			}
		};
	}

}
