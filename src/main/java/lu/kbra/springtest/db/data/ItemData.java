package lu.kbra.springtest.db.data;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Check;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.Nullable;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class ItemData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Long id;

	@Column(length = 35)
	private String name;

	@Column(length = 500)
	@Nullable
	private String description;

	// in cents
	@Column
	@Check("price >= 0")
	private long price;

	@Column
	private boolean active;

	public ItemData() {
	}

	public ItemData(long id) {
		this.id = id;
	}

	public ItemData(String name, String description, long price, boolean active) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.active = active;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ItemData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + ", description=" + description + ", price="
				+ price + ", active=" + active + "]";
	}

}
