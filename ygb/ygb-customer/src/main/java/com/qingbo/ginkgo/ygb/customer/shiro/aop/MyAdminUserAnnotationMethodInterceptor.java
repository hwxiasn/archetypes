package com.qingbo.ginkgo.ygb.customer.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

public class MyAdminUserAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {

    public MyAdminUserAnnotationMethodInterceptor() {
        super( new MyAdminUserAnnotationHandler() );
    }

    public MyAdminUserAnnotationMethodInterceptor(AnnotationResolver resolver) {
        super(new MyAdminUserAnnotationHandler(), resolver);
    }
}
