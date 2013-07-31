package cz.cuni.mff.d3s.been.web.pages;

import org.apache.tapestry5.annotations.Property;

/**
 * User: donarus Date: 4/29/13 Time: 2:26 PM
 */
public abstract class DetailPage<T> extends Page {

    @Property
	protected String itemId;

	// onPassivate() is called by Tapestry to get the
	// activation context to put in the URL.
	Object[] onPassivate() {
		return new String[] { itemId };
	}

	void onActivate(String itemId) {
		this.itemId = itemId;
	}


    // method is used when page is instantiated through the
    // InjectPage annotation.
	public void set(String itemId) {
		this.itemId = itemId;
	}

}
