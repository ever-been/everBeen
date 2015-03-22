package cz.cuni.mff.d3s.been.databus;

/**
 * Created by donarus on 8.3.15.
 */
public enum UUIDType {
    BYTE(Byte.MAX_VALUE, 2),
    SHORT(Short.MAX_VALUE,4),
    INTEGER(Integer.MAX_VALUE,8),
    LONG(Long.MAX_VALUE,16);

    public long maxValue;
    public int hexCharCount;

    private UUIDType(long maxValue, int hexCharCount) {
        this.maxValue = maxValue;
        this.hexCharCount = hexCharCount;
    }
}
