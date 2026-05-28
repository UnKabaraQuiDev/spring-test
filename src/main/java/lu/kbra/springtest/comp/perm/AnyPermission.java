package lu.kbra.springtest.comp.perm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lu.kbra.springtest.db.UserPermission;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("@permission.hasAny(#root, #this)")
public @interface AnyPermission {

	UserPermission[] value();

}