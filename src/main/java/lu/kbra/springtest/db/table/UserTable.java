package lu.kbra.springtest.db.table;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.UserData;

@Component
public abstract class UserTable extends DeferredDataBaseTable<UserData> {

	public UserTable(DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<UserData> byName(String name);

	@Deprecated
	@Query(columns = { "name", "pass" })
	public abstract Optional<UserData> byNameAndPass(String name, String pass);

}
