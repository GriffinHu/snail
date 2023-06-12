package com.acgist.snail.context.wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.acgist.snail.utils.Performance;

class HttpHeaderWrapperTest extends Performance {

    @Test
    void testGetFileName() throws UnsupportedEncodingException {
        var headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='snail.jar'"));
        var headerWrapper = HttpHeaderWrapper.newInstance(headers);
        var fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("snail.jar", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='%e6%b5%8b%e8%af%95.exe'"));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("测试.exe", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='" + new String("测试.exe".getBytes("GBK"), "ISO-8859-1") + "'"));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("测试.exe", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename=\"snail.jar\""));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("snail.jar", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename=\"￨ﾜﾗ￧ﾉﾛ.txt\""));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("蜗牛.txt", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename=snail.jar?version=1.0.0"));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("snail.jar", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("inline;filename=\"snail.jar\";filename*=utf-8''snail.jar"));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("snail.jar", fileName);
        headers = Map.of(HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("inline;charset=utf-8;fileName=\"snail.jar\";filename*=utf-8''snail.jar"));
        headerWrapper = HttpHeaderWrapper.newInstance(headers);
        fileName = headerWrapper.getFileName("错误");
        this.log(fileName);
        assertEquals("snail.jar", fileName);
    }
    
    @Test
    void testFileSize() {
        var headers = Map.of(
            HttpHeaderWrapper.HEADER_CONTENT_LENGTH, List.of(" 10000 "),
            HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='snail.jar'")
        );
        var wrapper = HttpHeaderWrapper.newInstance(headers);
        assertFalse(wrapper.range());
        assertEquals(10000L, wrapper.fileSize());
        headers = Map.of(
            HttpHeaderWrapper.HEADER_CONTENT_RANGE, List.of("bytes 0-100/100"),
            HttpHeaderWrapper.HEADER_CONTENT_LENGTH, List.of(" 10000 "),
            HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='snail.jar'")
        );
        wrapper = HttpHeaderWrapper.newInstance(headers);
        assertTrue(wrapper.range());
        headers = Map.of(
            HttpHeaderWrapper.HEADER_CONTENT_RANGE, List.of("bytes"),
            HttpHeaderWrapper.HEADER_CONTENT_LENGTH, List.of(" 10000 "),
            HttpHeaderWrapper.HEADER_CONTENT_DISPOSITION, List.of("attachment;filename='snail.jar'")
        );
        wrapper = HttpHeaderWrapper.newInstance(headers);
        assertTrue(wrapper.range());
    }
    
}
