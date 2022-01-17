package net.badlion.modapicommon.utility;

public class NotificationButton {

	public enum Action {
		OPEN_URL, RUN_COMMAND, SUGGEST_COMMAND
	}

	private final String text;
	private final Action action;
	private final String clickInfo;

	public NotificationButton(String text, Action action, String clickInfo) {
		this.text = text;
		this.action = action;
		this.clickInfo = clickInfo;
	}
}
