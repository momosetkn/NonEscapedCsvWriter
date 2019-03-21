package com.github.momosetkn.csv;

import lombok.RequiredArgsConstructor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * エスケープ処理をしない高速でシンプルなCSV書き込みライブラリ.
 * ロックしていないため、スレッドアンセーフです.
 *
 * @param <E>
 */
public class NonEscapedCsvWriter<E> implements Closeable, Flushable {
    private Writer writer;
    private List<LabelWithMethod> labelWithMethodList = new ArrayList<>();
    private int charsCapacity = 100;
    private Function<Object, String> convert = (o) -> Objects.toString(o, "null");

    private static final int WRITE_BUFFER_SIZE = 0x400;//1k
    private char[] writeBuffer;

    public NonEscapedCsvWriter(Class<E> beanClazz, Writer writer) {
        this.writer = writer;
        for (Field field : beanClazz.getDeclaredFields()) {
            CsvColumn csvColumn = field.getAnnotation(CsvColumn.class);
            if (csvColumn != null) {
                String fieldName = field.getName();
                try {
                    PropertyDescriptor nameProp = new PropertyDescriptor(fieldName, beanClazz);
                    labelWithMethodList.add(new LabelWithMethod(csvColumn.label(), nameProp.getReadMethod()));
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public NonEscapedCsvWriter<E> charsCapacity(int charsCapacity) {
        this.charsCapacity = charsCapacity;
        return this;
    }

    public NonEscapedCsvWriter<E> convert(Function<Object, String> convert) {
        this.convert = convert;
        return this;
    }

    public void init() throws IOException {
        charsCapacity = labelWithMethodList.size() * 15;
        StringBuilder header = new StringBuilder(charsCapacity);
        labelWithMethodList.stream()
                .map(e -> e.label)
                .forEach(e -> header.append(e).append(","));
        header.deleteCharAt(header.length() - 1);
        writer.write(header.toString());
    }

    private void writeWithStringBuilder(StringBuilder sb) throws IOException {
        int len = sb.length();
        char cbuf[];
        if (len <= WRITE_BUFFER_SIZE) {
            if (writeBuffer == null) {
                writeBuffer = new char[WRITE_BUFFER_SIZE];
            }
            cbuf = writeBuffer;
        } else {
            cbuf = new char[len];
        }
        sb.getChars(0, len, cbuf, 0);
        writer.write(cbuf, 0, len);
    }

    public void write(E bean) throws IOException {
        StringBuilder values = new StringBuilder(charsCapacity).append("\n");
        for (LabelWithMethod labelWithMethod : labelWithMethodList) {
            try {
                values.append(
                        labelWithMethod.getter.invoke(bean));
            } catch (IllegalAccessException | InvocationTargetException e1) {
                throw new RuntimeException(e1);
            }
        }
        values.append(",");
        values.deleteCharAt(values.length() - 1);
        writeWithStringBuilder(values);
    }

    public void writeAll(Collection<E> beanList) throws IOException {
        for (E bean : beanList) {
            write(bean);
        }
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }

    @RequiredArgsConstructor
    private class LabelWithMethod {
        private final String label;
        private final Method getter;
    }
}