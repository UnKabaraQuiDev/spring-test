package lu.kbra.springtest.comp.perm;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lu.kbra.springtest.db.data.UserPermission;

@Component("permission")
public class PermissionChecker {

	public boolean has(final UserPermission permission) {
		return this.getAuthorities().contains(permission.name());
	}

	public boolean hasAny(final UserPermission... permissions) {
		final Set<String> authorities = this.getAuthorities();

		return Arrays.stream(permissions).map(Enum::name).anyMatch(authorities::contains);
	}

	public boolean hasAll(final UserPermission... permissions) {
		final Set<String> authorities = this.getAuthorities();

		return Arrays.stream(permissions).map(Enum::name).allMatch(authorities::contains);
	}

	private Set<String> getAuthorities() {
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return Set.of();
		}

		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
	}
}