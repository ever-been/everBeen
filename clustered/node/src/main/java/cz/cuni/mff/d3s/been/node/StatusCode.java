package cz.cuni.mff.d3s.been.node;

/**
 * Exit status codes.
 *
 * @author Martin Sixta
 */
enum StatusCode {

	EX_USAGE(67);

	private int code;


	StatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
