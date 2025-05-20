package ru.yandex_practicum.shoponline.util;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import ru.yandex_practicum.shoponline.model.entity.Product;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.stream.Collectors;
import org.springframework.core.io.buffer.DataBuffer;

public class CsvParserUtil {

//    public static Flux<Product> parseCsv(FilePart file) {
//        return Flux.create(sink -> {
//
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    Files.newInputStream(Files.createTempFile("upload-", file.filename())), StandardCharsets.UTF_8))) {
//                String data = reader.lines().collect(Collectors.joining(System.lineSeparator()));
//                CsvReader csvReader = new CsvReader();
//                csvReader.setContainsHeader(true);
//                csvReader.setFieldSeparator(',');
//                CsvContainer csvContainer = csvReader.read(new StringReader(data));
//                csvContainer.getRows().forEach(row -> {
//                    String name = row.getField("name");
//                    String description = row.getField("description");
//                    byte[] image = Base64.getDecoder().decode(row.getField("image"));
//                    Double price = Double.parseDouble(row.getField("price"));
//
//                    Product product = new Product();
//                    product.setName(name);
//                    product.setDescription(description);
//                    product.setImage(image);
//                    product.setPrice(price);
//
//                    sink.next(product);
//                });
//                sink.complete();
//            } catch (Exception e) {
//                sink.error(e);
//            }
//        });
//    }

    public static Flux<Product> parseCsv(FilePart file) {
        return file.content()
            .map(DataBuffer::asInputStream)
            .flatMap(inputStream -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    CsvReader csvReader = new CsvReader();
                    csvReader.setContainsHeader(true);
                    csvReader.setFieldSeparator(',');

//                    StringBuilder data = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        data.append(line).append(System.lineSeparator());
//                    }

                    String data = reader.lines().collect(Collectors.joining(System.lineSeparator()));

                    CsvContainer csvContainer = csvReader.read(new StringReader(data));

                    return Flux.fromIterable(csvContainer.getRows())
                            .map(row -> {
                                String name = row.getField("name");
                                String description = row.getField("description");
                                byte[] image = Base64.getDecoder().decode(row.getField("image"));
                                //byte[] image = row.getField("image").getBytes();
                                Double price = Double.parseDouble(row.getField("price"));

                                Product product = new Product();
                                product.setName(name);
                                product.setDescription(description);
                                product.setImage(image);
                                product.setPrice(price);

                                return product;
                            });
                } catch (Exception e) {
                    return Flux.error(e);
                }
            });
    }

}
