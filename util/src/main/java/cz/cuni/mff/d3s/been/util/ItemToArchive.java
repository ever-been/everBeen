package cz.cuni.mff.d3s.been.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * An item to be put into a ZIP stream/file.
 *
 * @author Tadeas Palusga
 * 
 */
public interface ItemToArchive {

	/** Size to use if the total length of the stream content is unknown */
    public static final long SIZE_UNKNOWN = -1;

    /**
     * Signal whether this item should become a directory in the targeted ZIP
     *
     * @return <code>true</code> when the item is a directory, <code>false</code> otherwise
     */
    boolean isDirectory();

    /**
     * Create an input stream to the item's content.
     *
     * @return An open input stream
     *
     * @throws IOException When the item's content cannot be read
     */
	InputStream getInputStream() throws IOException;

    /**
     * Get the item's desired path in the ZIP
     *
     * @return The path
     */
	String getPathInZip();

    /**
     * Get the size of this item's content.
     *
     * @return A non-negative long integer for known sizes, {@link #SIZE_UNKNOWN} for unknown size
     */
    long getSize();

}
