package lu.kbra.springtest.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.CheckItemData;

@Component
public class CheckItemTable extends DeferredDataBaseTable<CheckItemData> {

	public CheckItemTable(DataBase dataBase) {
		super(dataBase);
	}

}
