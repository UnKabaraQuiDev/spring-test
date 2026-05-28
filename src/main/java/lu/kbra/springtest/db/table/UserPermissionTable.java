package lu.kbra.springtest.db.table;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Param;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.UserPermissionData;

@Component
public abstract class UserPermissionTable extends DeferredDataBaseTable<UserPermissionData> {

	public UserPermissionTable(DataBase dataBase) {
		super(dataBase);
	}

	@Query()
	public abstract List<UserPermissionData> byUser(@Param UUID userId);

}
