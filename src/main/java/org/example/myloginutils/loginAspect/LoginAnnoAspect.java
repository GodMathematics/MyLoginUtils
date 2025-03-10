package org.example.myloginutils.loginAspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.myloginutils.model.LoginInfo;
import org.example.myloginutils.utils.CookieUtil;
import org.example.myloginutils.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

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
    public LoginInfo logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LoginInfo loginInfo = new LoginInfo();
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取方法上的注解
        LoginToolAnno logAnnotation = method.getAnnotation(LoginToolAnno.class);
        //用户信息
        Object userInfo = null;
        //构建sql
        String passwordField = logAnnotation.passwordField();
        String usernameField = logAnnotation.usernameField();
        Class<?> voClass = logAnnotation.voClass();
        String usernameValue = "";  // 获取注解的 value 属性
        String passwordValue = "";
        String userNumber = "";
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
                    else if (field.getName().equals(usernameValue))
                        userNumber = String.valueOf(value);
                }
            }
        }

        String sql = logAnnotation.checkLoginSql();
        try {
            //构建sql
            sql = String.format(sql, usernameValue, passwordValue);
            // 执行 SQL 查询
            List<?> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(voClass));

            if (!result.isEmpty())
                userInfo = result.get(0);
        } catch (Exception e){
            log.error("sql {}; error : {}",sql,e.toString());
        }

        //略
        long duration = System.currentTimeMillis() - startTime;
        String methodName = joinPoint.getSignature().toShortString();
        System.out.println("[AOP] 方法 " + methodName + " 执行耗时: " + duration + "ms");

        if (userInfo == null) {
            return loginInfo.setMessage("用户不存在");
        } else {
            // 创建 Cookie
            loginInfo.setCookie(CookieUtil.setJwtCookie(JwtUtil.generateToken(userNumber)));
            loginInfo.setUserInfo(userInfo);

            return loginInfo.setMessage("登录成功");
        }
    }
}
