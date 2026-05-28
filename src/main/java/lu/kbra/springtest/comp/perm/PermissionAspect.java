package lu.kbra.springtest.comp.perm;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

	private final PermissionChecker permissionChecker;

	public PermissionAspect(PermissionChecker permissionChecker) {
		this.permissionChecker = permissionChecker;
	}

	@Before("@annotation(anyPermissions)")
	public void checkAnyPermissions(AnyPermission anyPermissions) {
		if (!permissionChecker.hasAny(anyPermissions.value())) {
			throw new AccessDeniedException("Missing required permission");
		}
	}

	@Before("@annotation(allPermissions)")
	public void checkAllPermissions(AllPermission allPermissions) {
		if (!permissionChecker.hasAll(allPermissions.value())) {
			throw new AccessDeniedException("Missing required permission");
		}
	}
}