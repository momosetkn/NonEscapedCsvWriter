package csv.test;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import kotlin.text.Charsets;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class UnivocityTest {
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

        try (Writer writer = Files.newBufferedWriter(new File("/home/momose/Documents/testUnivocity.csv").toPath(), Charsets.UTF_8)) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setHeaders("booleanField", "localDateField", "localDateTimeField", "bigDecimalField", "stringField", "integerField", "longField");

            BeanWriterProcessor<ExampleBean> writerProcessor = new BeanWriterProcessor<>(ExampleBean.class);
            settings.setRowWriterProcessor(writerProcessor);

            CsvWriter csvWriter = new CsvWriter(writer, settings);

            csvWriter.writeHeaders();
            IntStream.range(0, MAX_RECORD_COUNT).mapToObj(i -> exampleBean).forEach(e -> {
                csvWriter.processRecord(e);
            });

            csvWriter.close(
            );
        } finally {
        }

        System.out.println(String.format(" %15dus", System.nanoTime() - l));
}


    @Data
    public static class ExampleBean {

        @Parsed(field = "booleanField")
        Boolean booleanField;

        @Parsed(field = "localDateField")
        LocalDate localDateField;

        @Parsed(field = "localDateTimeField")
        LocalDateTime localDateTimeField;

        @Parsed(field = "bigDecimalField")
        BigDecimal bigDecimalField;

        @Parsed(field = "stringField")
        String stringField;

        @Parsed(field = "integerField")
        Integer integerField;

        @Parsed(field = "longField")
        Long longField;
    }

}
