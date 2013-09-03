package cz.cuni.mff.d3s.been.persistence;

import java.io.Serializable;

/**
 * A generic filter for attribute selection
 *
 * @author darklight
 */
public interface AttributeFilter extends Serializable {

	/**
	 * Get the type of this attribute filter
	 *
	 * @return The type
	 */
	AttributeFilterType getType();
}
