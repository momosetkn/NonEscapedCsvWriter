package csv.test;

import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanWriter;
import kotlin.text.Charsets;
import lombok.Data;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class SuperCsvAnnotationTest {
    static final int MAX_RECORD_COUNT = 100_000;
public static void main(String...args) throws IOException {
        ExampleBean exampleBean = new ExampleBean();
        exampleBean.setBooleanField(true);
        exampleBean.setLocalDateField(LocalDate.of(2018, 12, 25));
        exampleBean.setLocalDateTimeField(LocalDateTime.of(2018, 12, 25, 9, 30, 10));
        exampleBean.setBigDecimalField(BigDecimal.valueOf(100_000.000_001));
        exampleBean.setStringField("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456");
        exampleBean.setIntegerField(Integer.MAX_VALUE);
        exampleBean.setLongField(Long.MAX_VALUE);
        
        long l = System.nanoTime();


        try (LazyCsvAnnotationBeanWriter<ExampleBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                ExampleBean.class,
                Files.newBufferedWriter(new File("/home/momose/Documents/testSuperCsv.csv").toPath(), Charsets.UTF_8),
                CsvPreference.STANDARD_PREFERENCE)
        ) {
            csvWriter.init("booleanField","localDateField","localDateTimeField","bigDecimalField","stringField","integerField","longField");
            IntStream.range(0, MAX_RECORD_COUNT).mapToObj(i -> exampleBean).forEach(e-> {
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
    @com.github.mygreen.supercsv.annotation.CsvBean(header = true)
    public static class ExampleBean {

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "booleanField")
        Boolean booleanField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "localDateField")
        LocalDate localDateField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "localDateTimeField")
        LocalDateTime localDateTimeField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "bigDecimalField")
        BigDecimal bigDecimalField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "stringField")
        String stringField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "integerField")
        Integer integerField;

        @com.github.mygreen.supercsv.annotation.CsvColumn(label = "longField")
        Long longField;
    }

}
