package cz.cuni.mff.d3s.been.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

/**
 * Test for the {@link cz.cuni.mff.d3s.been.util.JsonToTypedMap}
 * @author darklight
 */
public class JsonToTypedMapTest {

	JsonToTypedMap jttm;

	{
		final Map<String, Class<?>> typeMap = new TreeMap<String, Class<?>>();
		typeMap.put("if", Integer.class);
		typeMap.put("sf", String.class);
		typeMap.put("df", Double.class);
		typeMap.put("o.if", Integer.class);
		typeMap.put("o.df", Double.class);

		final Map<String, String> aliases = new TreeMap<String, String>();
		aliases.put("myDbl", "o.df");
		aliases.put("myDbl2", "o.df");

		jttm = new JsonToTypedMap(typeMap, aliases);
	}

	@Test
	public void testParseSimple() throws Exception {
		final Map<String, Object> omap = jttm.convert("{\"if\": 1, \"sf\": \"tralala\", \"df\": 3.5}");
		Assert.assertEquals(3, omap.size());
		Assert.assertEquals(1, omap.get("if"));
		Assert.assertEquals(3.5, omap.get("df"));
		Assert.assertEquals("tralala", omap.get("sf"));
	}

	@Test
	public void testParseComposite() throws Exception {
		final Map<String, Object> omap = jttm.convert("{\"if\": 1, \"o\": {\"if\": 3}}");
		Assert.assertEquals(2, omap.size());
		Assert.assertEquals(1, omap.get("if"));
		Assert.assertEquals(3, omap.get("o.if"));
	}

	@Test
	public void testParseAliasedComposite() throws Exception {
		final Map<String, Object> omap = jttm.convert("{\"o\": {\"if\": 3, \"df\": 20.0}}");
		Assert.assertEquals(3, omap.size());
		Assert.assertEquals(3, omap.get("o.if"));
		Assert.assertEquals(20.0, omap.get("myDbl"));
		Assert.assertEquals(20.0, omap.get("myDbl2"));
		Assert.assertTrue(omap.get("myDbl") == omap.get("myDbl2"));
	}

}
