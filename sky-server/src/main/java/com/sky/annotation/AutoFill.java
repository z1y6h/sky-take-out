package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识需要自动填充的字段
 */
@Target(ElementType.METHOD)  // 表示该注解只能用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 表示该注解在运行时生效
public @interface AutoFill {
    // 定义枚举类，用于标识填充数据的操作类型
    OperationType value();  //  value()方法用于获取操作类型
}
