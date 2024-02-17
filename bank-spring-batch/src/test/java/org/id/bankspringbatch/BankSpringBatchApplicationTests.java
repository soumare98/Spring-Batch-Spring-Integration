package org.id.bankspringbatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = { TestConfig.class, SpringBatchConfig.class })

public class BankSpringBatchApplicationTests {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void contextLoads() throws Exception {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("inputFile", "src/main/resources/data.csv");
		jobParametersBuilder.addDate("dummy", new Date());
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParametersBuilder.toJobParameters());

		// Verify job execution status
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

		// Verify job step execution
		StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
		assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

		// Optionally, you can verify more details of your job execution or step
		// execution
		assertNotNull(stepExecution.getReadCount());
		assertNotNull(stepExecution.getWriteCount());
		assertNotNull(stepExecution.getFilterCount());
		// Add more assertions as needed

	}

}
