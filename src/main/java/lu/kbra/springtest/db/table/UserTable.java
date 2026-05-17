package lu.kbra.springtest.db.table;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.springtest.db.data.UserData;

@Component
public abstract class UserTable extends DeferredDataBaseTable<UserData> {

	public UserTable(final DataBase dataBase) {
		super(dataBase);
	}

	public Optional<UserData> byId(final UUID id) {
		return this.loadIfExists(new UserData(id));
	}

	@Query(columns = { "name" })
	@Cacheable(cacheNames = "user.name")
	public abstract Optional<UserData> byName(String name);

	@Caching(evict = { @CacheEvict(cacheNames = "user.name", allEntries = true) })
	@Override
	public UserData update(final UserData data) throws DBException {
		return super.update(data);
	}

	@Caching(evict = { @CacheEvict(cacheNames = "user.name", allEntries = true) })
	@Override
	public UserData updateAndReload(final UserData data) throws DBException {
		return super.updateAndReload(data);
	}

	@Caching(evict = { @CacheEvict(cacheNames = "user.name", allEntries = true) })
	@Override
	public UserData insertAndReload(final UserData data) throws DBException {
		return super.insertAndReload(data);
	}

	@Caching(evict = { @CacheEvict(cacheNames = "user.name", allEntries = true) })
	@Override
	public UserData insert(final UserData data) throws DBException {
		return super.insert(data);
	}

	@Caching(evict = { @CacheEvict(cacheNames = "user.name", allEntries = true) })
	@Override
	public UserData delete(UserData data) throws DBException {
		return super.delete(data);
	}

}
