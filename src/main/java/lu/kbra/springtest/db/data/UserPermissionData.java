package lu.kbra.springtest.db.data;

import java.util.UUID;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.springtest.db.UserPermission;
import lu.kbra.springtest.db.table.UserTable;

public class UserPermissionData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = UserTable.class)
	private UUID userId;

	@Column(length = 16)
	@PrimaryKey
	private UserPermission permission;

	public UserPermissionData() {
	}

	public UserPermissionData(UUID userId, UserPermission permission) {
		this.userId = userId;
		this.permission = permission;
	}

	public UUID getUserId() {
		return userId;
	}

	public UserPermission getPermission() {
		return permission;
	}

	@Override
	public String toString() {
		return "UserPermissionData@" + System.identityHashCode(this) + " [userId=" + userId + ", permission=" + permission + "]";
	}

}
