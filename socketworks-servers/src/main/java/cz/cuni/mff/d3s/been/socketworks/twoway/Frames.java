package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An buffer of byte arrays received in a message
 */
class Frames implements Iterable<byte[]> {
	private final List<byte[]> frames;

	/**
	 * Create the frame buffer
	 */
	Frames() {
		this.frames = new ArrayList<byte[]>(3);
	}

	/**
	 * Create the frame buffer
	 *
	 * @return A new frame buffer
	 */
	public static Frames create() {
		return new Frames();
	}

	/**
	 * Add a byte array to the buffer
	 *
	 * @param frame Array to add
	 */
	public void add(byte[] frame) {
		frames.add(frame);
	}

	@Override
	public Iterator<byte[]> iterator() {
		return frames.iterator();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final Iterator<byte[]> frameIt = iterator();
		while (frameIt.hasNext()) {
			// We're mostly assuming to send legible strings over frame channels
			sb.append(Arrays.toString(frameIt.next()));
		}
		return sb.toString();
	}
}
