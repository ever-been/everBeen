package cz.cuni.mff.been.taskmanager.tasktree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class TaskTreeAddressImplTest {
	
	private static final String[] SEGMENT_NAMES = {
		"one", "two", "three", "four", "five",
		"six", "seven", "eight", "nine", "ten",
		"eleven", "twelve", "thirteen"
	};
	
	/* A list of what's wrong for string paths. */
	private static final String EMPTY = "";
	private static final String JUST_SPACES = " ";
	private static final String JUST_WHITE = "\t\t  \t";
	private static final String JUST_RUBBISH = "rubbish";
	private static final String ONE_SEGMENT_EMPTY_FIRST = "/";
	private static final String TWO_SEGMENTS_EMPTY_FIRST = "//two";
	private static final String TWO_SEGMENTS_EMPTY_SECOND = "/one/";
	private static final String TWO_SEGMENTS_EMPTY_BOTH = "//";
	private static final String TWO_SEGMENTS_INITIAL_RUBBISH = "rubbish/one/two";
	private static final String TWO_SEGMENTS_INITIAL_SPACE = " /one/two";
	private static final String TWO_SEGMENTS_INITIAL_RUBBISH_EMPTY = "rubbish//two";
	private static final String TWO_SEGMENTS_INITIAL_SPACE_EMPTY = " /one/";
	private static final String THREE_SEGMENTS_EMPTY_FIRST = "//two/three";
	private static final String THREE_SEGMENTS_EMPTY_SECOND = "/one//three";
	private static final String THREE_SEGMENTS_EMPTY_THIRD = "/one/two/";
	private static final String THREE_SEGMENTS_EMPTY_FIRST_TWO = "///three";
	private static final String THREE_SEGMENTS_EMPTY_LAST_TWO = "/one//";
	private static final String THREE_SEGMENTS_EMPTY_FIRST_LAST = "//two/";
	private static final String THREE_SEGMENTS_INITIAL_RUBBISH = "rubbish/one/two/three";
	private static final String THREE_SEGMENTS_INITIAL_RUBBISH_EMPTY = "rubbish/one//three";
	private static final String THREE_SEGMENTS_INITIAL_RUBBISH_MORE_EMPTY = "rubbish//two/";
	private static final String TEN_SEGMENTS_EMPTY_FIRST = "//two/three/four/five/six/seven/eight/nine/ten";
	private static final String TEN_SEGMENTS_EMPTY_LAST = "/one/two/three/four/five/six/seven/eight/nine/";
	private static final String TEN_SEGMENTS_EMPTY_ALL = "//////////";
	private static final String TEN_SEGMENTS_EMPTY_INSIDE = "/one/two/three/four//six/seven/eight/nine/ten";
	private static final String TEN_SEGMENTS_EMPTY_SOME = "/one/two/three//five/six/seven//nine/ten";
	private static final String TEN_SEGMENTS_INITIAL_RUBBISH = "rubbish/one/two/three/four/five/six/seven/eight/nine/ten";
	private static final String TEN_SEGMENTS_INITIAL_RUBBISH_EMPTY = "rubbish//one/two/three/four/five/six/seven/eight/nine/ten";
	
	/* A list of what's wrong for array paths. */
	private static final String[] A_EMPTY = {};
	private static final String[] A_ONE_SEGMENT_EMPTY_FIRST = { "" };
	private static final String[] A_ONE_SEGMENT_NULL_FIRST = { null };
	private static final String[] A_TWO_SEGMENTS_EMPTY_FIRST = { "",  "two" };
	private static final String[] A_TWO_SEGMENTS_NULL_FIST = { null, "two" };
	private static final String[] A_TWO_SEGMENTS_EMPTY_SECOND = { "one", "" };
	private static final String[] A_TWO_SEGMENTS_NULL_SECOND = { "one", null };
	private static final String[] A_TWO_SEGMENTS_EMPTY_BOTH = { "", "" };
	private static final String[] A_TWO_SEGMENTS_NULL_BOTH = { null, null };
	private static final String[] A_THREE_SEGMENTS_EMPTY_FIRST = { "", "two", "three" };
	private static final String[] A_THREE_SEGMENTS_NULL_FIRST = { null, "two", "three" };
	private static final String[] A_THREE_SEGMENTS_EMPTY_SECOND = { "one", "", "three" };
	private static final String[] A_THREE_SEGMENTS_NULL_SECOND = { "one", null, "three" };
	private static final String[] A_THREE_SEGMENTS_EMPTY_THIRD = { "one", "two", "" };
	private static final String[] A_THREE_SEGMENTS_NULL_THIRD = { "one", "two", null };
	private static final String[] A_THREE_SEGMENTS_EMPTY_FIRST_TWO = { "", "", "three" };
	private static final String[] A_THREE_SEGMENTS_NULL_FIRST_TWO = { null, null, "three" };
	private static final String[] A_THREE_SEGMENTS_EMPTY_LAST_TWO = { "one", "", "" };
	private static final String[] A_THREE_SEGMENTS_NULL_LAST_TWO = { "one", null, null };
	private static final String[] A_THREE_SEGMENTS_EMPTY_FIRST_LAST = { "", "two", "" };
	private static final String[] A_THREE_SEGMENTS_NULL_FIRST_LAST = { null, "two", null };
	private static final String[] A_TEN_SEGMENTS_EMPTY_FIRST = {
		"", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_NULL_FIRST = {
		null, "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_EMPTY_LAST = {
		"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", ""
	};
	private static final String[] A_TEN_SEGMENTS_NULL_LAST = {
		"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", null
	};
	private static final String[] A_TEN_SEGMENTS_EMPTY_ALL = {
		"", "", "", "", "", "", "", "", "", ""
	};
	private static final String[] A_TEN_SEGMENTS_NULL_AL = {
		null, null, null, null, null, null, null, null, null, null
	};
	private static final String[] A_TEN_SEGMENTS_EMPTY_INSIDE = {
		"one", "two", "three", "four", "", "six", "seven", "eight", "nine", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_NULL_INSIDE = {
		"one", "two", "three", "four", null, "six", "seven", "eight", "nine", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_EMPTY_SOME = {
		"one", "two", "three", "", "five", "six", "seven", "", "nine", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_NULL_SOME = {
		"one", "two", "three", null, "five", "six", "seven", null, "nine", "ten"
	};
	private static final String[] A_ONE_SEGMENT_SLASH_1 = { "/one" };
	private static final String[] A_ONE_SEGMENT_SLASH_2 = { "one/" };
	private static final String[] A_ONE_SEGMENT_SLASH_3 = { "on/e" };
	private static final String[] A_ONE_SEGMENT_JUST_SLASH = { "/" };
	private static final String[] A_TWO_SEGMENTS_SLASH_1 = { "one", "/two" };
	private static final String[] A_TWO_SEGMENTS_SLASH_2 = { "o/ne", "two" };
	private static final String[] A_TWO_SEGMENTS_SLASH_3 = { "one", "two/" };
	private static final String[] A_TWO_SEGMENTS_JUST_SLASH = { "one", "/" };
	private static final String[] A_THREE_SEGMENTS_SLASH_1 = { "one", "/two", "three" };
	private static final String[] A_THREE_SEGMENTS_SLASH_2 = { "one", "t/wo", "three" };
	private static final String[] A_THREE_SEGMENTS_SLASH_3 = { "one", "two/", "three" };
	private static final String[] A_THREE_SEGMENTS_JUST_SLASH = { "one", "/", "three" };
	private static final String[] A_FIVE_SEGMENTS_SLASH_1 = { "one", "two", "three", "four", "five/" };
	private static final String[] A_FIVE_SEGMENTS_SLASH_2 = { "one", "two", "th/ree", "four", "five" };
	private static final String[] A_FIVE_SEGMENTS_SLASH_3 = { "one/", "two", "three", "four", "five" };
	private static final String[] A_FIVE_SEGMENTS_JUST_SLASH = { "one", "two", "three", "four", "/" };
	private static final String[] A_TEN_SEGMENTS_SLASHES_SINGLE = {
		"one", "two", "three", "four/", "five", "six", "/sev/en", "eight", "nine/", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_JUST_SINGLE_SLASHES = {
		"/", "/", "/", "/", "/", "/", "/", "/", "/", "/"
	};
	private static final String[] A_ONE_SEGMENT_SLASHES_1 = { "//one" };
	private static final String[] A_ONE_SEGMENT_SLASHES_2 = { "o/n/e/" };
	private static final String[] A_ONE_SEGMENT_SLASHeS_3 = { "on///e" };
	private static final String[] A_ONE_SEGMENT_JUST_SLASHES = { "///" };
	private static final String[] A_TWO_SEGMENTS_SLASHES_1 = { "one//", "///two" };
	private static final String[] A_TWO_SEGMENTS_SLASHES_2 = { "o/ne", "//two" };
	private static final String[] A_TWO_SEGMENTS_SLASHES_3 = { "/o/ne", "two/" };
	private static final String[] A_TWO_SEGMENTS_JUST_SLASHES = { "one", "/" };
	private static final String[] A_THREE_SEGMENTS_SLASHES_1 = { "///one", "/two", "three" };
	private static final String[] A_THREE_SEGMENTS_SLASHES_2 = { "one", "t/wo", "thr///ee" };
	private static final String[] A_THREE_SEGMENTS_SLASHES_3 = { "one", "two/", "///three" };
	private static final String[] A_THREE_SEGMENTS_JUST_SLASHES = { "o//ne", "/////", "th//ree" };
	private static final String[] A_TEN_SEGMENTS_SLASHES_MULTI = {
		"one", "two", "three", "/four/", "/five/", "//six//", "/sev///en", "eight", "ni/ne/", "ten"
	};
	private static final String[] A_TEN_SEGMENTS_JUST_MULTI_SLASHES = {
		"//", "/", "////", "//", "/", "/////", "//", "/", "////", "//"
	};

	/* A list of what's right for string paths. */
	private static final String ONE_SEGMENT = "/one";
	private static final String TWO_SEGMENTS = ONE_SEGMENT + "/two";
	private static final String THREE_SEGMENTS = TWO_SEGMENTS + "/three";
	private static final String FOUR_SEGMENTS = THREE_SEGMENTS + "/four";
	private static final String FIVE_SEGMENTS = FOUR_SEGMENTS + "/five";
	private static final String THIRTEEN_SEGMENTS = FIVE_SEGMENTS + "/six/seven/eight/nine/ten/eleven/twelve/thirteen";
	
	/* A list of what's right for array paths. */
	private static final String[] A_ONE_SEGMENT = { "one" };
	private static final String[] A_TWO_SEGMENTS = { "one", "two" };
	private static final String[] A_THREE_SEGMENTS = { "one", "two", "three" };
	private static final String[] A_FOUR_SEGMENTS = { "one", "two", "three", "four" };
	private static final String[] A_FIVE_SEGMENTS = { "one", "two", "three", "four", "five" };
	private static final String[] A_THIRTEEN_SEGMENTS = {
		"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
		"eleven", "twelve", "thirteen"
	};

	/* TODO The following should be a collection to avoid aditing two places of the source. */
	private static final String[] STRING_ALL_THE_BAD = {
		EMPTY, JUST_SPACES, JUST_WHITE, JUST_RUBBISH, ONE_SEGMENT_EMPTY_FIRST,
		TWO_SEGMENTS_EMPTY_FIRST, TWO_SEGMENTS_EMPTY_SECOND, TWO_SEGMENTS_EMPTY_BOTH,
		TWO_SEGMENTS_INITIAL_RUBBISH, TWO_SEGMENTS_INITIAL_SPACE,
		TWO_SEGMENTS_INITIAL_RUBBISH_EMPTY, TWO_SEGMENTS_INITIAL_SPACE_EMPTY,
		THREE_SEGMENTS_EMPTY_FIRST, THREE_SEGMENTS_EMPTY_SECOND, THREE_SEGMENTS_EMPTY_THIRD,
		THREE_SEGMENTS_EMPTY_FIRST_TWO, THREE_SEGMENTS_EMPTY_LAST_TWO,
		THREE_SEGMENTS_EMPTY_FIRST_LAST, THREE_SEGMENTS_INITIAL_RUBBISH,
		THREE_SEGMENTS_INITIAL_RUBBISH_EMPTY, THREE_SEGMENTS_INITIAL_RUBBISH_MORE_EMPTY,
		TEN_SEGMENTS_EMPTY_FIRST, TEN_SEGMENTS_EMPTY_LAST, TEN_SEGMENTS_EMPTY_ALL,
		TEN_SEGMENTS_EMPTY_INSIDE, TEN_SEGMENTS_EMPTY_SOME, TEN_SEGMENTS_INITIAL_RUBBISH,
		TEN_SEGMENTS_INITIAL_RUBBISH_EMPTY,
	};
	
	/* TODO The following should be a collection to avoid aditing two places of the source. */
	private static final String[][] ARRAY_ALL_THE_BAD = {
		A_EMPTY,
		A_ONE_SEGMENT_EMPTY_FIRST, A_ONE_SEGMENT_NULL_FIRST,
		A_TWO_SEGMENTS_EMPTY_FIRST, A_TWO_SEGMENTS_NULL_FIST, A_TWO_SEGMENTS_EMPTY_SECOND,
		A_TWO_SEGMENTS_NULL_SECOND, A_TWO_SEGMENTS_EMPTY_BOTH, A_TWO_SEGMENTS_NULL_BOTH,
		A_THREE_SEGMENTS_EMPTY_FIRST, A_THREE_SEGMENTS_NULL_FIRST, A_THREE_SEGMENTS_EMPTY_SECOND,
		A_THREE_SEGMENTS_NULL_SECOND, A_THREE_SEGMENTS_EMPTY_THIRD, A_THREE_SEGMENTS_NULL_THIRD,
		A_THREE_SEGMENTS_EMPTY_FIRST_TWO, A_THREE_SEGMENTS_NULL_FIRST_TWO,
		A_THREE_SEGMENTS_EMPTY_LAST_TWO, A_THREE_SEGMENTS_NULL_LAST_TWO,
		A_THREE_SEGMENTS_EMPTY_FIRST_LAST, A_THREE_SEGMENTS_NULL_FIRST_LAST,
		A_TEN_SEGMENTS_EMPTY_FIRST, A_TEN_SEGMENTS_NULL_FIRST, A_TEN_SEGMENTS_EMPTY_LAST,
		A_TEN_SEGMENTS_NULL_LAST, A_TEN_SEGMENTS_EMPTY_ALL, A_TEN_SEGMENTS_NULL_AL,
		A_TEN_SEGMENTS_EMPTY_INSIDE, A_TEN_SEGMENTS_NULL_INSIDE, A_TEN_SEGMENTS_EMPTY_SOME,
		A_TEN_SEGMENTS_NULL_SOME,
		A_ONE_SEGMENT_SLASH_1, A_ONE_SEGMENT_SLASH_2, A_ONE_SEGMENT_SLASH_3,
		A_ONE_SEGMENT_JUST_SLASH,
		A_TWO_SEGMENTS_SLASH_1, A_TWO_SEGMENTS_SLASH_2, A_TWO_SEGMENTS_SLASH_3,
		A_TWO_SEGMENTS_JUST_SLASH,
		A_THREE_SEGMENTS_SLASH_1, A_THREE_SEGMENTS_SLASH_2, A_THREE_SEGMENTS_SLASH_3,
		A_THREE_SEGMENTS_JUST_SLASH,
		A_FIVE_SEGMENTS_SLASH_1, A_FIVE_SEGMENTS_SLASH_2, A_FIVE_SEGMENTS_SLASH_3,
		A_FIVE_SEGMENTS_JUST_SLASH,
		A_TEN_SEGMENTS_SLASHES_SINGLE, A_TEN_SEGMENTS_JUST_SINGLE_SLASHES,
		A_ONE_SEGMENT_SLASHES_1, A_ONE_SEGMENT_SLASHES_2, A_ONE_SEGMENT_SLASHeS_3,
		A_ONE_SEGMENT_JUST_SLASHES,
		A_TWO_SEGMENTS_SLASHES_1, A_TWO_SEGMENTS_SLASHES_2, A_TWO_SEGMENTS_SLASHES_3,
		A_TWO_SEGMENTS_JUST_SLASHES,
		A_THREE_SEGMENTS_SLASHES_1, A_THREE_SEGMENTS_SLASHES_2, A_THREE_SEGMENTS_SLASHES_3,
		A_THREE_SEGMENTS_JUST_SLASHES,
		A_TEN_SEGMENTS_SLASHES_MULTI, A_TEN_SEGMENTS_JUST_MULTI_SLASHES
	};
	
	/* TODO The following should be a collection to avoid aditing two places of the source. */
	private static final String[] STRING_ALL_THE_GOOD = {
		ONE_SEGMENT, TWO_SEGMENTS, THREE_SEGMENTS, FOUR_SEGMENTS, FIVE_SEGMENTS, THIRTEEN_SEGMENTS
	};
	
	/* TODO The following should be a collection to avoid aditing two places of the source. */
	private static final String[][] ARRAY_ALL_THE_GOOD = {
		A_ONE_SEGMENT, A_TWO_SEGMENTS, A_THREE_SEGMENTS, A_FOUR_SEGMENTS, A_FIVE_SEGMENTS,
		A_THIRTEEN_SEGMENTS
	};
	
	private static String[] PARENT_PATHS = {
		"",
		ONE_SEGMENT,
		TWO_SEGMENTS,
		THREE_SEGMENTS,
		FOUR_SEGMENTS,
		"/one/two/three/four/five/six/seven/eight/nine/ten/eleven/twelve"
	};
	
	private static String[][] PARENT_SEGMENTSS = {
		{},
		A_ONE_SEGMENT,
		A_TWO_SEGMENTS,
		A_THREE_SEGMENTS,
		A_FOUR_SEGMENTS,
		{
			"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
			"eleven", "twelve"
		}
	};
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testConstructor_String_positive() throws MalformedAddressException, RemoteException {
		TaskTreeAddressBody address;
		int innerCounter, outerCounter;
		Long lastHash, curHash;
		
		lastHash = 0l;
		outerCounter = 0;
		for ( String path : STRING_ALL_THE_GOOD ) {
			address = new TaskTreeAddressBody( path );

			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			assertEquals( "Wrong path returned.", path, address.getPathString() );
			assertEquals( "Wrong parent path returned.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}

			curHash = address.longHashCode();
			assertFalse( "SUSPICIOUS: Same hash code returned.", lastHash == curHash );
			assertEquals( "Hash code clobbered.", curHash, address.longHashCode() );
			address.rehash();
			assertEquals( "rehash() did not add 1", Long.valueOf( curHash + 1 ), address.longHashCode() );
			
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned on repeated invocation.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}
			assertEquals( "Wrong parent path returned on repeated invocation.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			assertEquals( "Wrong path returned on repeated invocation.", path, address.getPathString() );
			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned on repeated iteration.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			
			++outerCounter;
		}
	}
	
	@Test
	public void testConstructor_String_negative() throws RemoteException {
		for ( String path : STRING_ALL_THE_BAD ) {
			try {
				new TaskTreeAddressBody( path );
				fail( "Illegal path string accepted: " + path );
			} catch ( MalformedAddressException exception ) {
				// That's what we want.
			}
		}
	}
	
	@Test
	public void testConstructor_ArrayOfString_positive() throws MalformedAddressException, RemoteException {
		TaskTreeAddressBody address;
		int innerCounter, outerCounter;
		Long lastHash, curHash;
		
		lastHash = 0l;
		outerCounter = 0;
		for ( String[] inputSegments : ARRAY_ALL_THE_GOOD ) {
			address = new TaskTreeAddressBody( inputSegments );
			
			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			assertEquals( "Wrong path returned.", STRING_ALL_THE_GOOD[ outerCounter ], address.getPathString() );
			assertEquals( "Wrong parent path returned.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}

			curHash = address.longHashCode();
			assertFalse( "SUSPICIOUS: Same hash code returned.", lastHash == curHash );
			assertEquals( "Hash code clobbered.", curHash, address.longHashCode() );
			address.rehash();
			assertEquals( "rehash() did not add 1.", Long.valueOf( curHash + 1 ), address.longHashCode() );
			
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned on repeated invocation.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}
			assertEquals( "Wrong parent path returned on repeated invocation.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			assertEquals( "Wrong path returned on repeated invocation.", STRING_ALL_THE_GOOD[ outerCounter ], address.getPathString() );
			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned on repeated iteration.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			
			++outerCounter;
		}
	}
	
	@Test
	public void testConstructor_ArrayOfString_negative() throws RemoteException {
		for ( String[] segments : ARRAY_ALL_THE_BAD ) {
			try {
				new TaskTreeAddressBody( segments );
				fail( "Illegal path segments accepted: " + arrayOutput( segments ) );
			} catch ( MalformedAddressException exception ) {
				// That's what we want.
			}
		}
	}
	
	@Test
	public void testPrivateConstructor_StringArrayOfString() throws RemoteException {
		TaskTreeAddressBody address;
		int innerCounter, outerCounter;
		Long lastHash, curHash;
		
		lastHash = 0l;
		outerCounter = 0;
		for ( String path : STRING_ALL_THE_GOOD ) {
			address = new TaskTreeAddressBody( path, ARRAY_ALL_THE_GOOD[ outerCounter ] );

			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			assertEquals( "Wrong path returned.", path, address.getPathString() );
			assertEquals( "Wrong parent path returned.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}

			curHash = address.longHashCode();
			assertFalse( "SUSPICIOUS: Same hash code returned.", lastHash == curHash );
			assertEquals( "Hash code clobbered.", curHash, address.longHashCode() );
			address.rehash();
			assertEquals( "rehash() did not add 1.", Long.valueOf( curHash + 1 ), address.longHashCode() );
			
			innerCounter = 0;
			for ( String segment : address.parentSegments() ) {
				assertEquals( "Invalid parent segments returned on repeated invocation.", PARENT_SEGMENTSS[ outerCounter ][ innerCounter++ ], segment );
			}
			assertEquals( "Wrong parent path returned on repeated invocation.", PARENT_PATHS[ outerCounter ], address.getParentPathString() );
			assertEquals( "Wrong path returned on repeated invocation.", path, address.getPathString() );
			innerCounter = 0;
			for ( String segment : address.getPathSegments() ) {
				assertEquals( "Invalid segment returned on repeated iteration.", SEGMENT_NAMES[ innerCounter++ ], segment );
			}
			
			++outerCounter;
		}
	}
	
	@After
	public void tearDown() {
	}
	
	private String arrayOutput( String ... elements ) {
		StringBuilder builder;
		
		builder = new StringBuilder();
		for ( String element : elements ) {
			builder.append(  '\'' + element + "\' "  );
		}
		if ( builder.length() > 0 ) {
			builder.deleteCharAt( builder.length() - 1 );
		}
		return builder.toString();
	}
}
