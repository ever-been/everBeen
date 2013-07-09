package cz.cuni.mff.d3s.been.web.model;


/**
 * Storage for int key and String value pair.
 */
public final class KeyValuePair {

    public int key;

    public String value;

    public KeyValuePair(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Compare using ke field.
     *
     * @param object with which is this object compared
     * @return True if and only if givven object is not null and is
     *         of same type ({@link KeyValuePair}) keys on both objects
     *         are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof KeyValuePair)) {
            return false;
        }

        return this.key == ((KeyValuePair) object).key;
    }

}
