package com.acgist.snail.logger;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * 日志单元
 * 每条日志对应一个日志单元
 * 
 * @author acgist
 */
public final class Tuple {

    /**
     * 格式化符号
     */
    public static final String FORMAT_CODE = "{}";
    
    /**
     * 原始信息
     */
    private final String message;
    /**
     * 格式化信息
     */
    private final String[] format;
    /**
     * 后缀文本
     */
    private final String suffix;
    
    /**
     * @param message 原始日志
     */
    public Tuple(String message) {
        int pos   = 0;
        int last  = 0;
        int index = 0;
        this.message = message;
        String[] format = new String[Byte.SIZE];
        final int messageLength = message.length();
        final int formatCodeLength = FORMAT_CODE.length();
        while ((pos = message.indexOf(FORMAT_CODE, last)) >= 0) {
            format[index++] = message.substring(last, pos);
            last = pos + formatCodeLength;
            if(index >= format.length) {
                final String[] resizeFormat = new String[format.length + Byte.SIZE];
                System.arraycopy(format, 0, resizeFormat, 0, format.length);
                format = resizeFormat;
            }
        }
        if(last != 0 && last < messageLength) {
            this.suffix = message.substring(last);
        } else {
            this.suffix = null;
        }
        this.format = new String[index];
        System.arraycopy(format, 0, this.format, 0, index);
    }
    
    /**
     * 格式化日志
     * 
     * @param objects 参数列表
     * 
     * @return 日志信息
     */
    public final String format(Object ... objects) {
        final StringBuilder builder = new StringBuilder();
        return this.format(builder, objects).toString();
    }
    
    /**
     * 格式化日志
     * 
     * @param builder 日志Builder
     * @param objects 参数列表
     * 
     * @return 日志信息
     */
    public final StringBuilder format(StringBuilder builder, Object ... objects) {
        Objects.requireNonNull(builder, "没有日志Builder");
        final int formatLength = this.format.length;
        if(objects == null || objects.length == 0 || formatLength == 0) {
            builder.append(this.message);
            return builder;
        }
        final int objectLength = objects.length;
        for (int index = 0; index < formatLength; index++) {
            builder.append(this.format[index]);
            if(index < objectLength) {
                final Object object = objects[index];
                if(object == null) {
                    builder.append(object);
                } else if(object.getClass().isArray()) {
                    builder.append(this.array(object));
                } else {
                    builder.append(object);
                }
            } else {
                builder.append(FORMAT_CODE);
            }
        }
        if(this.suffix != null) {
            builder.append(this.suffix);
        }
        // 注意：直接忽略后面多余参数
        return builder;
    }
    
    /**
     * @param objects 参数列表
     * 
     * @return 异常
     */
    public final Throwable throwable(Object ... objects) {
        if(objects == null || objects.length == 0) {
            return null;
        }
        final Object object = objects[objects.length - 1];
        if(object instanceof Throwable t) {
            return t;
        }
        return null;
    }
    
    /**
     * 处理数组参数
     * 
     * @param object 参数
     * 
     * @return 字符输出
     */
    private final String array(Object object) {
        final StringJoiner joiner = new StringJoiner(", ", "[", "]");
        if(object instanceof boolean[] array) {
            for (boolean value : array) {
                joiner.add(Boolean.toString(value));
            }
        } else if(object instanceof byte[] array) {
            for (byte value : array) {
                joiner.add(Byte.toString(value));
            }
        } else if(object instanceof char[] array) {
            for (char value : array) {
                joiner.add(Character.toString(value));
            }
        } else if(object instanceof short[] array) {
            for (short value : array) {
                joiner.add(Short.toString(value));
            }
        } else if(object instanceof int[] array) {
            for (int value : array) {
                joiner.add(Integer.toString(value));
            }
        } else if(object instanceof long[] array) {
            for (long value : array) {
                joiner.add(Long.toString(value));
            }
        } else if(object instanceof float[] array) {
            for (float value : array) {
                joiner.add(Float.toString(value));
            }
        } else if(object instanceof double[] array) {
            for (double value : array) {
                joiner.add(Double.toString(value));
            }
        } else if(object instanceof Object[] array) {
            for (Object value : array) {
                joiner.add(String.valueOf(value));
            }
        } else {
            return String.valueOf(object);
        }
        return joiner.toString();
    }
    
}
