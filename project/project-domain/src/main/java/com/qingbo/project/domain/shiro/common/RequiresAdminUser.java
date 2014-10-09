package com.qingbo.project.domain.shiro.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@RequiresRoles(value = { "AA", "A", "NO", "AO" }, logical = Logical.OR)
public @interface RequiresAdminUser {

}
