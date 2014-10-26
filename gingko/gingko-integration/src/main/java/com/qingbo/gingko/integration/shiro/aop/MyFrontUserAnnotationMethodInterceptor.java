package com.qingbo.gingko.integration.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

public class MyFrontUserAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {

    public MyFrontUserAnnotationMethodInterceptor() {
        super( new MyFrontUserAnnotationHandler() );
    }

    public MyFrontUserAnnotationMethodInterceptor(AnnotationResolver resolver) {
        super(new MyFrontUserAnnotationHandler(), resolver);
    }
}
