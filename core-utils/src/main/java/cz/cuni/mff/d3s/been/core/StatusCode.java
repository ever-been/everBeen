package cz.cuni.mff.d3s.been.core;

/**
 * Exit status codes.
 * 
 * @author Martin Sixta
 */
public enum StatusCode {

	EX_OK(0), EX_USAGE(67);

	private int code;

	StatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
