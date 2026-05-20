package lu.kbra.springtest.db.table;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Param;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.ItemData;

@Component
public abstract class ItemTable extends DeferredDataBaseTable<ItemData> {

	public ItemTable(DataBase dataBase) {
		super(dataBase);
	}

	@Cacheable(cacheNames = "item.code")
	@Query
	public abstract Optional<ItemData> byCode(@Param("code") String ean);

	@Cacheable(cacheNames = "item.id")
	@Query
	public abstract Optional<ItemData> byId(@Param long id);

}
