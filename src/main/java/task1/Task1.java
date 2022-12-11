package task1;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Task1 {

    /**
     *  Calculates statistics of violations from .json files with data of violations during different years.
     *  Handles files with data in parallel threads.
     *  Creates file 'statistics_parallel_X.xml' (where X is number of threads)
     *  with total amount of fines for every violation type sorted in descending order.
     *
     * @param path - path to a directory with .json files
     * @param numberThreads - number of threads
     * @throws IOException
     */
    public void getStatisticsParallel(String path, int numberThreads) throws IOException {
        Map<String, BigDecimal> statisticsMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(numberThreads);

        File dir = new File(path);
        File[] files = dir.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
        if (files == null) {
            throw new IllegalStateException("Given directory doesn't contain .json files");
        }

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (File file : files) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            handleFile(file, statisticsMap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        executorService.shutdown();

        List<Violation> violationList = statisticsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new Violation(e.getKey(), e.getValue())).collect(Collectors.toList());

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.writeValue(new File(path + "/statistics_parallel_" + numberThreads + ".xml"),
                new Statistics(violationList));
    }

    /**
     * Calculates statistics of violations from .json files with data of violations during different years.
     * Creates file 'statistics.xml' with total amount of fines for every violation type sorted in descending order.
     *
     * @param path - path to a directory with .json files
     * @throws IOException
     */
    public void getStatistics(String path) throws IOException {
        Map<String, BigDecimal> statisticsMap = new HashMap<>();
        JsonFactory jasonFactory = new MappingJsonFactory();
        File dir = new File(path);
        File[] files = dir.listFiles(file -> !file.isDirectory() && file.getName().endsWith(".json"));
        if (files == null) {
            throw new IllegalStateException("Given directory doesn't contain .json files");
        }
        for (File file : files) {
            try (JsonParser jsonParser = jasonFactory.createParser(file)) {
                if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IllegalStateException("An array is expected");
                }
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    Violation violation = readViolation(jsonParser);
                    BigDecimal sum = statisticsMap.getOrDefault(violation.getType(), new BigDecimal(0));
                    statisticsMap.put(violation.getType(), sum.add(violation.getAmount()));
                }
            }
        }

        List<Violation> violationList = statisticsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(e -> new Violation(e.getKey(), e.getValue())).collect(Collectors.toList());

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.writeValue(new File(path + "/statistics.xml"), new Statistics(violationList));
    }

    private void handleFile(File file, Map<String, BigDecimal> statisticsMap) throws IOException {
        JsonFactory jasonFactory = new MappingJsonFactory();
        try (JsonParser jsonParser = jasonFactory.createParser(file)) {
            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("An array is expected");
            }
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                Violation violation = readViolation(jsonParser);
                statisticsMap.compute(violation.getType(),
                        (key, val) -> (val == null) ? violation.getAmount() : val.add(violation.getAmount()));
            }
        }
    }

    private Violation readViolation(JsonParser jsonParser) throws IOException {
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("An object is expected");
        }

        Violation violation = new Violation();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();
            switch (property) {
                case "type":
                    violation.setType(jsonParser.getText());
                    break;
                case "fine_amount":
                    violation.setAmount(jsonParser.getDecimalValue());
                    break;
            }
        }
        return violation;
    }

    private static void executeTask(Task1 task1, int numThreads, String path) throws IOException {
        final int NUMBER_OF_TRIES = 10;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUMBER_OF_TRIES; i++) {
            if (numThreads == 1) {
                task1.getStatistics(path);
            } else {
                task1.getStatisticsParallel(path, numThreads);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("Execution with %d threads took %d milliseconds\n", numThreads, (endTime - startTime) / NUMBER_OF_TRIES);
    }

    public static void main(String[] args) {
        Task1 task1 = new Task1();
        String path = "./target/classes/task1/";
        try {
            executeTask(task1, 1, path);
            executeTask(task1, 2, path);
            executeTask(task1, 4, path);
            executeTask(task1, 8, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}