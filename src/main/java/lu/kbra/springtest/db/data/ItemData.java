package lu.kbra.springtest.db.data;

import java.util.Arrays;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Check;
import lu.kbra.pclib.db.autobuild.column.Checks;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.Nullable;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.query.NotNull;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class ItemData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column(length = 35)
	private String name;

	@Column(length = 200)
	@Nullable
	private String description;

	@Column(length = 13, type = CharType.class)
	@NotNull
	@Unique
	private char[] code;

	// in cents
	@Column
	@Check("price >= 0")
	private long price;

	public ItemData() {
	}

	public ItemData(long id) {
		this.id = id;
	}

	public ItemData(char[] code, String name, String description, long price) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public char[] getCode() {
		return code;
	}

	public void setCode(char[] code) {
		this.code = code;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ItemData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + ", description=" + description + ", code="
				+ Arrays.toString(code) + ", price=" + price + "]";
	}

}
