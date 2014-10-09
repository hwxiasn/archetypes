package com.qingbo.project.domain.shiro.aop;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.springframework.core.annotation.AnnotationUtils;

import com.qingbo.project.domain.shiro.common.RequiresFrontUser;


public class MyFrontUserAnnotationHandler extends AuthorizingAnnotationHandler {
    public MyFrontUserAnnotationHandler() {
        super(RequiresFrontUser.class);
    }
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if(!(a instanceof RequiresFrontUser)) return;
        
        RequiresRoles rrAnnotation = AnnotationUtils.findAnnotation(a.getClass(), RequiresRoles.class);
        String[] roles = rrAnnotation.value();

        if (roles.length == 1) {
            getSubject().checkRole(roles[0]);
            return;
        }
        if (Logical.AND.equals(rrAnnotation.logical())) {
            getSubject().checkRoles(Arrays.asList(roles));
            return;
        }
        if (Logical.OR.equals(rrAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOneRole = false;
            for (String role : roles) if (getSubject().hasRole(role)) hasAtLeastOneRole = true;
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOneRole) getSubject().checkRole(roles[0]);
        }
    }
}
