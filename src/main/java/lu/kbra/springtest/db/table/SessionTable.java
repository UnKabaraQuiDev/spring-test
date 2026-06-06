package lu.kbra.springtest.db.table;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.SessionData;

@Component
public abstract class SessionTable extends DeferredDataBaseTable<SessionData> {

	public SessionTable(DataBase dataBase) {
		super(dataBase);
	}

}
