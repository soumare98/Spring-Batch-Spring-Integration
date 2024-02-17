package org.id.bankspringbatch;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.springframework.core.io.Resource;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;



public class FileMessageToJobRequest {
    private Job job;

    private String fileParameterName = "JOB1";

   @Value("${inputFile}")
    private Resource inputFile;

    public void setFileParameterName(String fileParameterName) {
        this.fileParameterName = fileParameterName;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Transformer
    public JobLaunchRequest toRequest(Message<File> message) throws IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        jobParametersBuilder.addString(fileParameterName, inputFile.getFile().getAbsolutePath());
        // We add a dummy value to make job params unique, or else spring batch
        // will only run it the first time
        jobParametersBuilder.addDate("dummy", new Date());

        return new JobLaunchRequest(job, jobParametersBuilder.toJobParameters());
    }

}
