package lu.kbra.springtest.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.CheckData;

@Component
public class CheckTable extends DeferredDataBaseTable<CheckData> {

	public CheckTable(DataBase dataBase) {
		super(dataBase);
	}

}
