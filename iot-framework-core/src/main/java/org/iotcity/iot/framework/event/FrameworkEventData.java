package org.iotcity.iot.framework.event;

import java.io.Serializable;

/**
 * The framework event data.
 * @author ardon
 * @date 2021-09-19
 */
public final class FrameworkEventData implements Serializable {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The state of framework.
	 */
	private final FrameworkState state;

	/**
	 * Constructor for framework event data.
	 * @param state The state of framework.
	 */
	public FrameworkEventData(FrameworkState state) {
		this.state = state;
	}

	/**
	 * Gets the current framework state.
	 */
	public final FrameworkState getState() {
		return state;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{state=");
		sb.append(state);
		sb.append("}");
		return sb.toString();
	}

}
