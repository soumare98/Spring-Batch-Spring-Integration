package org.id.bankspringbatch;

import java.util.ArrayList;
import java.util.List;
import org.id.bankspringbatch.dao.BankTransaction;
import org.id.bankspringbatch.dao.BankTransactionRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
// qui permet d'activer spring batch
@EnableBatchProcessing
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ItemReader<BankTransaction> bankTransactionItemReader;
    @Autowired
    private ItemWriter<BankTransaction> bankTransactionItemWriter;
    // @Autowired
    // private ItemProcessor<BankTransaction, BankTransaction>
    // bankTransactionItemProcessor;

    @Bean
    public Job bankJob() {
        Step step1 = stepBuilderFactory.get("ETL-TRANSACION-FILE-LOAD")
                .<BankTransaction, BankTransaction>chunk(100)
                .reader(bankTransactionItemReader)
                .writer(bankTransactionItemWriter)
                .processor(compositeItemProcessor())

                // .processor(bankTransactionItemProcessor)

                .build();

        return jobBuilderFactory.get("bankJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();

    }

    @Bean
    public ItemProcessor<BankTransaction, BankTransaction> compositeItemProcessor() {
        List<ItemProcessor<BankTransaction, BankTransaction>> itemProcessors = new ArrayList<>();
        itemProcessors.add(itemProcessor1());
        itemProcessors.add(itemProcessor2());
        CompositeItemProcessor<BankTransaction, BankTransaction> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(itemProcessors);
        return compositeItemProcessor;

    }

    @Bean
    BankTransactionItemProcessor itemProcessor1() {
        return new BankTransactionItemProcessor();
    }

    @Bean
    BankTransactionItemAnalyticsProcessor itemProcessor2() {
        return new BankTransactionItemAnalyticsProcessor();
    }

    // ItemReader
    @Bean

    public FlatFileItemReader<BankTransaction> fileItemReader(@Value("${inputFile}") Resource inputFile) {
        FlatFileItemReader<BankTransaction> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setName("FFRI1");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setResource(inputFile);
        fileItemReader.setLineMapper(lineMappe());
        return fileItemReader;

    }

    @Bean
    public LineMapper<BankTransaction> lineMappe() {
        DefaultLineMapper<BankTransaction> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "accountID", "srtTransactionDate", "transactionType", "amount");
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BankTransaction.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }
    // ItemProcessor

    /*
     * @Bean
     * public ItemProcessor<BankTransaction, BankTransaction> itemProcessor() {
     * return new ItemProcessor<BankTransaction, BankTransaction>() {
     * private SimpleDateFormat dateFormat = new
     * SimpleDateFormat("dd/MM/yyyy-HH:mm");
     * 
     * @Override
     * public BankTransaction process(BankTransaction bankTransaction) throws
     * Exception {
     * bankTransaction.setTransactionDate(dateFormat.parse(bankTransaction.
     * getStrTransactionDate()));
     * return bankTransaction;
     * }
     * };
     * 
     * }
     */

    // ItemWriter

    @Bean
  public ItemWriter<BankTransaction> itemWriter() {

        return new ItemWriter<BankTransaction>() {
            @Autowired
            private BankTransactionRepository bankTransactionRepository;

            @Override
            public void write(java.util.List<? extends BankTransaction> items) throws Exception {
                bankTransactionRepository.saveAll(items);

            }
        };
    }

}
