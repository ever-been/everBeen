package cz.cuni.mff.d3s.been.results;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Primitive types supported for processing.
 * Serves to restrict the all too generic mapping of available types to {@link java.lang.Class}
 *
 * @author darklight
 */
public enum PrimitiveType {

	INT(Integer.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return Integer.parseInt(valueToParse);
			} catch (NumberFormatException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						Integer.class.getCanonicalName()
				), e);
			}
		}
	},
	LONG(Long.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return Long.parseLong(valueToParse);
			} catch (NumberFormatException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						Long.class.getCanonicalName()
				), e);
			}
		}
	},
	BIGINT(BigInteger.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return new BigInteger(valueToParse);
			} catch (RuntimeException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						BigInteger.class.getCanonicalName()
				), e);
			}
		}
	},
	FLOAT(Float.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return Float.parseFloat(valueToParse);
			} catch (NumberFormatException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						Float.class.getCanonicalName()
				), e);
			}
		}
	},
	DOUBLE(Double.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return Double.parseDouble(valueToParse);
			} catch (NumberFormatException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						Double.class.getCanonicalName()
				), e);
			}
		}
	},
	BIGDEC(BigDecimal.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			try {
				return new BigDecimal(valueToParse);
			} catch (RuntimeException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						BigDecimal.class.getCanonicalName()
				), e);
			}
		}
	},
	STRING(String.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			return valueToParse;
		}
	},
	DATE(Date.class){
		@Override
		public Object parse(String valueToParse) throws PrimitiveTypeException {
			SimpleDateFormat sdf = dateFormat.get();
			if (sdf == null) {
				sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
				dateFormat.set(sdf);
			}
			try {
				return sdf.parse(valueToParse);
			} catch (ParseException e) {
				throw new PrimitiveTypeException(String.format(
						"Could not parse value [%s] to %s",
						valueToParse,
						Date.class.getCanonicalName()
				), e);
			}
		}
	};

	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>();

	private final Class<?> primitiveClass;

	private PrimitiveType(
			Class<?> primitiveClass
	) {
		this.primitiveClass = primitiveClass;
	}

	/**
	 * Get the type alias for this primitive
	 *
	 * @return Type alias
	 */
	public String getTypeAlias() {
		return name().toLowerCase();
	}

	/**
	 * Get the actual java {@link java.lang.Class} for this primitive type
	 *
	 * @return Java class
	 */
	public Class<?> getPrimitiveClass() {
		return primitiveClass;
	}

	/**
	 * Determine whether the type is numeric (has applicable arithmetics) or not
	 *
	 * @return <code>true</code> if type is numeric; <code>false</code> if not
	 */
	public boolean isNumeric() {
		return (
				INT == this ||
				LONG == this ||
				BIGINT == this ||
				FLOAT == this ||
				DOUBLE == this ||
				BIGDEC == this
		);
	}

	public abstract Object parse(String valueToParse) throws PrimitiveTypeException;
	/**
	 * Lookup the primitive for given type alias.
	 *
	 * @param typeAlias Alias of the given type
	 *
	 * @return Primitive for given type alias
	 *
	 * @throws PrimitiveTypeException When type alias isn't recognized
	 */
	public static PrimitiveType fromTypeAlias(String typeAlias) throws PrimitiveTypeException {
		for (PrimitiveType type: PrimitiveType.values()) {
			if (type.getTypeAlias().equals(typeAlias)) return type;
		}
		throw new PrimitiveTypeException(String.format(
				"Unrecognized type alias: [%s]",
				typeAlias
		));
	}

	/**
	 * Lookup the primitive for given class.
	 *
	 * @param primitiveClass Class to lookup
	 *
	 * @return The primitive for given class
	 *
	 * @throws PrimitiveTypeException If a primitive doesn't exist for given class
	 */
	public static PrimitiveType forClass(Class<?> primitiveClass) throws PrimitiveTypeException {
		for (PrimitiveType type: PrimitiveType.values()) {
			if (type.getPrimitiveClass().equals(primitiveClass)) return type;
		}
		throw new IllegalArgumentException(String.format(
				"Unrecognized type: [%s]",
				primitiveClass.getCanonicalName()
		));
	}

	/**
	 * Convert a {@link cz.cuni.mff.d3s.been.results.PrimitiveType} mapping to {@link java.lang.Class} mapping
	 *
	 * @param types Primitive type mappings
	 *
	 * @return Class mappings
	 */
	public static Map<String, Class<?>> toClasses(Map<String, PrimitiveType> types) {
		final Map<String, Class<?>> classes = new TreeMap<String, Class<?>>();
		for (Map.Entry<String, PrimitiveType> typeMapping: types.entrySet()) {
			classes.put(typeMapping.getKey(), typeMapping.getValue().getPrimitiveClass());
		}
		return classes;
	}

	/**
	 * Convert type alias mapping to {@link cz.cuni.mff.d3s.been.results.PrimitiveType} mapping
	 *
	 * @param typeAliases Type alias mapping
	 *
	 * @return Primitive type mapping
	 *
	 * @throws PrimitiveTypeException When one or more of the aliases have no equivalents among primitive types
	 */
	public static Map<String, PrimitiveType> toTypes(Map<String, String> typeAliases) throws PrimitiveTypeException {
		final Map<String, PrimitiveType> types = new TreeMap<String, PrimitiveType>();
		for (Map.Entry<String, String> alias: typeAliases.entrySet())
			types.put(alias.getKey(), PrimitiveType.fromTypeAlias(alias.getValue()));
		return types;
	}
}
