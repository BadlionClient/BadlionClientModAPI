package net.badlion.modapicommon.utility;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Notification {

	public enum Level {
		ERROR, WARNING, SUCCESS, INFO
	}

	@SuppressWarnings("unused")
	private final String type = "modapi";
	private final JsonObject data;
	private final List<NotificationButton> buttons;

	private Notification() {
		this.data = new JsonObject();
		this.buttons = new ArrayList<>();
	}

	public static Notification.Builder newNotification() {
		return new Notification.Builder(new Notification());
	}

	public JsonObject getData() {
		return this.data;
	}

	public List<NotificationButton> getButtons() {
		return this.buttons;
	}

	public static class Builder {

		private final Notification notification;

		private Builder(Notification notification) {
			this.notification = notification;
		}

		public Notification.Builder setTitle(String title) {
			this.notification.getData().addProperty("title", title);
			return this;
		}

		public Notification.Builder setDescription(String description) {
			this.notification.getData().addProperty("description", description);
			return this;
		}

		public Notification.Builder setDurationSeconds(long seconds) {
			return this.setDuration(seconds * 1000);
		}

		public Notification.Builder setDuration(long durationMillis) {
			this.notification.getData().addProperty("duration", durationMillis);
			return this;
		}

		public Notification.Builder setCloseOnClick(boolean closeOnClick) {
			this.notification.getData().addProperty("closeOnClick", closeOnClick);
			return this;
		}

		public Notification.Builder setLevel(Level level) {
			this.notification.getData().addProperty("level", level.name());
			return this;
		}

		public Notification.Builder addButton(NotificationButton button) {
			this.notification.getButtons().add(button);
			return this;
		}

		public Notification build() {
			final JsonObject data = this.notification.getData();

			if (!data.has("title")) {
				throw new RuntimeException("Title of notification must be set!");
			}

			if (!data.has("description")) {
				throw new RuntimeException("Description of notification must be set!");
			}

			return this.notification;
		}
	}
}
