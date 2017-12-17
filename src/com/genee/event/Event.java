package com.genee.event;

public class Event {

	private EventData data;
	private EventType type;
	private String owner;

	public Event(String owner, EventType type) {
		this.owner = owner;
		this.type = type;
	}

	public Event(String owner, EventType type, EventData data) {
		this.owner = owner;
		this.type = type;
		this.data = data;
	}

	public EventData getData() {
		return this.data;
	}

	public void setData(EventData data) {
		this.data = data;
	}

	public EventType getType() {
		return this.type;
	}

	public void setEventType(EventType type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwnerId(String owner) {
		this.owner = owner;
	}

}
