package lu.kbra.springtest.db.data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class SessionData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@DefaultValue("(gen_random_uuid())")
	private String id;

	@Column
	private Instant creationTime;

	@Column
	private Instant lastAccessedTime;

	@Column
	private Duration maxInactiveInterval;

	@Column
	private Map<String, Object> attributes;

	public SessionData() {
	}

	public SessionData(String id) {
		this.id = id;
	}

	public SessionData(Instant creationTime, Instant lastAccessedTime, Duration maxInactiveInterval,
			Map<String, Object> attributes) {
		this.creationTime = creationTime;
		this.lastAccessedTime = lastAccessedTime;
		this.maxInactiveInterval = maxInactiveInterval;
		this.attributes = attributes;
	}

	public SessionData(String id, Instant creationTime, Instant lastAccessedTime, Duration maxInactiveInterval,
			Map<String, Object> attributes) {
		this.id = id;
		this.creationTime = creationTime;
		this.lastAccessedTime = lastAccessedTime;
		this.maxInactiveInterval = maxInactiveInterval;
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}

	public Instant getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(Instant lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public Duration getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(Duration maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "SessionData@" + System.identityHashCode(this) + " [id=" + id + ", creationTime=" + creationTime
				+ ", lastAccessedTime=" + lastAccessedTime + ", maxInactiveInterval=" + maxInactiveInterval + "]";
	}

}
