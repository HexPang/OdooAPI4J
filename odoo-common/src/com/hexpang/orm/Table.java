package com.hexpang.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HexPang on 2017/3/30.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    public String value();
    public String primaryKey() default "id";
}
