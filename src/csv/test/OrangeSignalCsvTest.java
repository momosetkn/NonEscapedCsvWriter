package csv.test;

import kotlin.text.Charsets;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class OrangeSignalCsvTest {
    static final int MAX_RECORD_COUNT = 100_000;

    public static void main(String... args) throws IOException {
        ExampleBean exampleBean = new ExampleBean();
        exampleBean.setBooleanField(true);
        exampleBean.setLocalDateField(LocalDate.of(2018, 12, 25));
        exampleBean.setLocalDateTimeField(LocalDateTime.of(2018, 12, 25, 9, 30, 10));
        exampleBean.setBigDecimalField(BigDecimal.valueOf(100_000.000_001));
        exampleBean.setStringField("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");
        exampleBean.setIntegerField(Integer.MAX_VALUE);
        exampleBean.setLongField(Long.MAX_VALUE);

        long l = System.nanoTime();


        try (com.orangesignal.csv.io.CsvEntityWriter<ExampleBean> csvWriter = new com.orangesignal.csv.io.CsvEntityWriter<>(
                new com.orangesignal.csv.CsvWriter(
                        Files.newBufferedWriter(new File("/home/momose/Documents/testOrangeSignal.csv").toPath(), Charsets.UTF_8)
                )
                ,
                ExampleBean.class)
        ) {
            IntStream.range(0, MAX_RECORD_COUNT).mapToObj(i -> exampleBean).forEach(e -> {
                try {
                    csvWriter.write(e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        }

        System.out.println(String.format(" %15dus", System.nanoTime() - l));
    }


    @Data
    @com.orangesignal.csv.annotation.CsvEntity
    public static class ExampleBean {

        @com.orangesignal.csv.annotation.CsvColumn(name = "booleanField")
        Boolean booleanField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "localDateField")
        LocalDate localDateField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "localDateTimeField")
        LocalDateTime localDateTimeField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "bigDecimalField")
        BigDecimal bigDecimalField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "stringField")
        String stringField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "integerField")
        Integer integerField;

        @com.orangesignal.csv.annotation.CsvColumn(name = "longField")
        Long longField;
    }
}
