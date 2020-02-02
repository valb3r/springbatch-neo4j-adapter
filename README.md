![Java CI](https://github.com/valb3r/springbatch-neo4j-adapter/workflows/Java%20CI/badge.svg?branch=master)

# Purpose

This is Spring Batch adapter for Neo4j database. It allows Spring Batch to persist its metadata directly 
in Neo4j so that no RDBMS/SQL database is needed.

# Examples

All examples can be found inside [examples](examples) folder.

## Configuring 

This is the example of how to enable Spring-Batch Neo4j adapter

[Example:Enable Neo4j adapter](examples/src/main/java/com/github/valb3r/springbatch/adapters/examples/neo4j/ExampleApplication.java#L10-L26)
```groovy
@EnableBatchProcessing
@SpringBootApplication(
    // Spring-Batch includes this by default, disabling them
    exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
    }
)
@EnableSpringBatchNeo4jAdapter
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class);
    }
}
```

After you have enabled Neo4j adapter with `@EnableSpringBatchNeo4jAdapter` you can use Spring-Batch as if it was 
RDBMS database. See this snippet for example:

[Example:Execute simple batch job](examples/src/main/java/com/github/valb3r/springbatch/adapters/examples/neo4j/SimpleJobService.java#L16-L47)
```groovy
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleJobService {

    @Getter
    private final AtomicReference<String> result = new AtomicReference<>();

    private final JobRepository jobRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @SneakyThrows
    public void runSimpleJob() {
        val job = jobBuilderFactory.get("FOO")
            .start(stepBuilderFactory.get("ONE").tasklet((a, b) -> {
                log.info("STEP ONE!");
                return null;
            }).build())
            .start(stepBuilderFactory.get("TWO").tasklet((a, b) -> {
                log.info("STEP TWO!");
                result.set("Step TWO DONE");
                return null;
            }).build())
            .build();

        val exec = jobRepository.createJobExecution("Test one", new JobParameters());
        job.execute(exec);
    }
}
```
