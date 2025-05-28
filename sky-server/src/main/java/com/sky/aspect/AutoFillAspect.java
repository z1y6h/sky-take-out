package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 * 实现公共字段自动填充处理逻辑
 */
@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}


    /**
     * 前置通知，在通知中填充公共字段
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {  //JoinPoint表示切点，封装了连接点（被拦截的方法）的信息
        log.info("开始进行公共字段填充");
        //获取当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType type = autoFill.value();//获取数据库操作类型

        //获取当前被拦截的方法参数，即实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        //获取实体对象
        Object entity = args[0];

        //准备公共字段数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime now  = LocalDateTime.now();


        //根据操作类型，为对应的字段通过反射赋值
        if (type == OperationType.INSERT) {
            //插入操作，为createTime、createUser、updateTime、updateUser赋值
            try {
                Method  setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method  setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method  setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method  setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为字段赋值
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (type ==  OperationType.UPDATE){
            //更新操作,为updateTime、updateUser赋值
            try {
                Method  setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method  setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
