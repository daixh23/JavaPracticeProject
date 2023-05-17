package com.example;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
public class TestPreconditions {

    public static void main(String[] args) {
        String param = "";
        //旧式写法
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException("param字段不能为空");
        }
        // 新式写法：期望这个字段不能为空
        Preconditions.checkArgument(StringUtils.isNotEmpty(param), "param字段不能为空");
    }
}
