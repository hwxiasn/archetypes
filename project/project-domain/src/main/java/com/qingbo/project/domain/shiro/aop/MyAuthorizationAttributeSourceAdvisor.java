package com.qingbo.project.domain.shiro.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import com.qingbo.project.domain.shiro.common.RequiresAdminUser;
import com.qingbo.project.domain.shiro.common.RequiresFrontUser;

@SuppressWarnings({"serial","unchecked","rawtypes"})
public class MyAuthorizationAttributeSourceAdvisor extends AuthorizationAttributeSourceAdvisor {
	private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[] {
                    RequiresAdminUser.class, RequiresFrontUser.class,
                    RequiresPermissions.class, RequiresRoles.class,
                    RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class
            };
	
	public MyAuthorizationAttributeSourceAdvisor() {
		super();
		
		AopAllianceAnnotationsAuthorizingMethodInterceptor advice = (AopAllianceAnnotationsAuthorizingMethodInterceptor)getAdvice();
		advice.getMethodInterceptors().add(new MyAdminUserAnnotationMethodInterceptor());
		advice.getMethodInterceptors().add(new MyFrontUserAnnotationMethodInterceptor());
	}
	
	@Override
	public boolean matches(Method method, Class targetClass) {
        Method m = method;

        if ( isAuthzAnnotationPresent(m) ) {
            return true;
        }
        
        if( isAuthzAnnotationPresent(m.getDeclaringClass()) ) {
        	return true;
        }

        //The 'method' parameter could be from an interface that doesn't have the annotation.
        //Check to see if the implementation has it.
        if ( targetClass != null) {
            try {
                m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                if ( isAuthzAnnotationPresent(m) ) {
                    return true;
                }
                if( isAuthzAnnotationPresent(targetClass) ) {
                	return true;
                }
            } catch (NoSuchMethodException ignored) {
                //default return value is false.  If we can't find the method, then obviously
                //there is no annotation, so just use the default return value.
            }
        }
        
		return false;
	}
	
    private boolean isAuthzAnnotationPresent(Method method) {
        for( Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES ) {
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if ( a != null ) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isAuthzAnnotationPresent(Class clazz) {
    	for( Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES ) {
    		Annotation a = AnnotationUtils.findAnnotation(clazz, annClass);
    		if ( a != null ) {
    			return true;
    		}
    	}
    	return false;
    }
}
