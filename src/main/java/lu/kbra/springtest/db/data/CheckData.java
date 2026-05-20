package lu.kbra.springtest.db.data;

import java.time.LocalDateTime;
import java.util.UUID;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.springtest.db.table.UserTable;

public class CheckData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column
	@DefaultValue("(NOW())")
	private LocalDateTime dateTime;

	@Column
	@ForeignKey(table = UserTable.class)
	private UUID userId;

	public CheckData() {
	}

	public CheckData(long id) {
		this.id = id;
	}

	public CheckData(LocalDateTime dateTime, UUID userId) {
		this.dateTime = dateTime;
		this.userId = userId;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CheckData@" + System.identityHashCode(this) + " [id=" + id + ", dateTime=" + dateTime + ", userId=" + userId + "]";
	}

}
