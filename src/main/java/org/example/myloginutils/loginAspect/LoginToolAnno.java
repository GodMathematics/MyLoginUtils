package org.example.myloginutils.loginAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginToolAnno {
    String usernameField();

    String passwordField();

    String checkLoginSql();

    Class<?> voClass();
}
