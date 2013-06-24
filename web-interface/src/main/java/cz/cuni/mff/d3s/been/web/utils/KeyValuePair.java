package cz.cuni.mff.d3s.been.web.utils;


import org.apache.tapestry5.ValueEncoder;

public final class KeyValuePair {

    public int key;

    public String value;

    public KeyValuePair(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyValuePair) {
            return false;
        }

        return this.key == ((KeyValuePair) obj).key;
    }

}
