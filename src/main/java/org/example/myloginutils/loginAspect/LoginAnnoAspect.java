package org.example.myloginutils.loginAspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LoginAnnoAspect {
    // 定义切点：拦截所有带有 @LogExecutionTime 注解的方法
    @Pointcut("@annotation(org.example.myloginutils.loginAspect.LoginToolAnno)")
    public void logExecutionTimePointcut() {}

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 环绕通知（最灵活）
    @Around("logExecutionTimePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        LoginToolAnno logAnnotation = method.getAnnotation(LoginToolAnno.class);

        String passwordField = logAnnotation.passwordField();
        String usernameField = logAnnotation.usernameField();
        String usernameValue = "";  // 获取注解的 value 属性
        String passwordValue = "";


        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg != null) {
                // 获取对象的属性
                Field[] fields = arg.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);  // 允许访问私有属性
                    Object value = field.get(arg);
                    if (field.getName().equals(passwordField))
                        passwordValue = String.valueOf(value);
                    else if (field.getName().equals(usernameField))
                        usernameValue = String.valueOf(value);
                }
            }
        }

        String sql = logAnnotation.checkLoginSql();
        boolean ableLogin = false;
        try {
            sql = String.format(sql, usernameValue, passwordValue);

            // 执行 SQL 语句（无返回值）
            ableLogin = jdbcTemplate.queryForObject(sql, Integer.class) > 0;
        } catch (Exception e){
            log.error("sql {}; error : {}",sql,e.toString());
        }


        long duration = System.currentTimeMillis() - startTime;
        String methodName = joinPoint.getSignature().toShortString();

        System.out.println("[AOP] 方法 " + methodName + " 执行耗时: " + duration + "ms");
        return ableLogin;
    }
}
