package lu.kbra.springtest.db.data;

import java.util.UUID;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class UserData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@DefaultValue("(gen_random_uuid())")
	private UUID id;

	@Column(length = 35)
	@Unique
	private String name;

	@Column(length = 320)
	@Unique(1)
	private String email;

	@Column(length = 60)
	private String pass;

	public UserData() {
	}

	public UserData(UUID id) {
		this.id = id;
	}

	public UserData(String name, String email, String pass) {
		this.name = name;
		this.email = email;
		this.pass = pass;
	}

	public UserData(UUID id, String name, String email, String pass) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.pass = pass;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Override
	public String toString() {
		return "UserData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + ", email=" + email + ", pass=" + pass + "]";
	}

}
