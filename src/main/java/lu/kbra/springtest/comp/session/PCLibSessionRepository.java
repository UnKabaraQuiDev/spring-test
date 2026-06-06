package lu.kbra.springtest.comp.session;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

import lu.kbra.springtest.db.data.SessionData;
import lu.kbra.springtest.db.table.SessionTable;

@Component
public class PCLibSessionRepository implements SessionRepository<MapSession> {

	@Autowired
	private SessionTable sessionTable;

	@Override
	public MapSession createSession() {
		return new MapSession();
	}

	@Override
	public void save(final MapSession session) {
		final SessionData data = this.toData(session);

		this.sessionTable.loadIfExists(new SessionData(data.getId())).ifPresentOrElse(
				p -> this.sessionTable.updateAndReload(data), () -> this.sessionTable.insertAndReload(data));
	}

	@Override
	public MapSession findById(final String id) {
		return this.sessionTable.loadIfExists(new SessionData(id)).map(this::toSession).orElse(null);
	}

	@Override
	public void deleteById(final String id) {
		this.sessionTable.deleteIfExists(new SessionData(id));
	}

	private SessionData toData(final MapSession session) {
		final SessionData data = new SessionData();

		data.setId(session.getId());
		data.setCreationTime(session.getCreationTime());
		data.setLastAccessedTime(session.getLastAccessedTime());
		data.setMaxInactiveInterval(session.getMaxInactiveInterval());

		data.setAttributes(new HashMap<>(
				session.getAttributeNames().stream().collect(Collectors.toMap(name -> name, session::getAttribute))));

		return data;
	}

	private MapSession toSession(final SessionData data) {
		final MapSession session = new MapSession();

		session.setLastAccessedTime(data.getLastAccessedTime());
		session.setMaxInactiveInterval(data.getMaxInactiveInterval());

		data.getAttributes().forEach(session::setAttribute);

		return session;
	}

}
