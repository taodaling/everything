package com.daltao.utils;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {


    @Test
    public void percentageEscapeDecode() {
        Assertions.assertEquals("\n%\rAB\t", StringUtils.percentageEscapeDecode("%n%%%rAB%t"));
    }

    @Test
    public void percentageEscapeEncode() {
        Assertions.assertEquals("abc%%%%ta\n", StringUtils.percentageEscapeEncode("abc%%ta\n"));
    }
}
