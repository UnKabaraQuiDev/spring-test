package lu.kbra.springtest.db.table;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Param;
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

	@Query()
	@Cacheable(cacheNames = "user.name")
	public abstract Optional<UserData> byName(@Param("name") String name);

	@Query()
	@Cacheable(cacheNames = "user.email")
	public abstract Optional<UserData> byEmail(@Param("email") String email);

	@Caching(
			evict = { @CacheEvict(cacheNames = "user.name", allEntries = true), @CacheEvict(cacheNames = "user.email", allEntries = true) }
	)
	@Override
	public UserData update(final UserData data) throws DBException {
		return super.update(data);
	}

	@Caching(
			evict = { @CacheEvict(cacheNames = "user.name", allEntries = true), @CacheEvict(cacheNames = "user.email", allEntries = true) }
	)
	@Override
	public UserData updateAndReload(final UserData data) throws DBException {
		return super.updateAndReload(data);
	}

	@Caching(
			evict = {
					@CacheEvict(cacheNames = "user.name", key = "#data.name"),
					@CacheEvict(cacheNames = "user.email", key = "#data.email") }
	)
	@Override
	public UserData delete(UserData data) throws DBException {
		return super.delete(data);
	}

}
