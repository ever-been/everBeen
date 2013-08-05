package cz.cuni.mff.d3s.been.persistence;

import java.io.Serializable;

/**
 * A generic filter for attribute selection
 *
 * @author darklight
 */
public interface AttributeFilter extends Serializable {
	AttributeFilterType getType();
}
