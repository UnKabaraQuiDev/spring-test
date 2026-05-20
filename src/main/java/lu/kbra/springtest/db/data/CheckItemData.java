package lu.kbra.springtest.db.data;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.springtest.db.table.CheckTable;
import lu.kbra.springtest.db.table.ItemTable;

public class CheckItemData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@ForeignKey(table = CheckTable.class)
	private long checkId;

	@Column
	@PrimaryKey
	@ForeignKey(table = ItemTable.class)
	private long itemId;

	@Column
	private int count = 1;

	public CheckItemData() {
	}

	public CheckItemData(long checkId, long itemId) {
		this.checkId = checkId;
		this.itemId = itemId;
	}

	public CheckItemData(long checkId, long itemId, int count) {
		this.checkId = checkId;
		this.itemId = itemId;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getCheckId() {
		return checkId;
	}

	public long getItemId() {
		return itemId;
	}

	@Override
	public String toString() {
		return "SoldItemData@" + System.identityHashCode(this) + " [checkId=" + checkId + ", itemId=" + itemId + ", count=" + count + "]";
	}

}
