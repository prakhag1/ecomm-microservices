package demo.config.payment;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.client.api.worker.JobHandler;
import io.zeebe.client.api.worker.JobWorker;

@Component
public class CancelOrder implements JobHandler {

	@Autowired
	private ZeebeClient zeebe;

	private JobWorker subscription;

	@PostConstruct
	public void subscribe() {
		subscription = zeebe.newWorker().jobType("cancel-order").handler(this).timeout(Duration.ofMinutes(1)).open();
	}

	@PreDestroy
	public void closeSubscription() {
		subscription.close();
	}

	@Override
	public void handle(JobClient client, ActivatedJob job) {
		System.out.println("Inside cancel order!!");
		client.newCompleteCommand(job.getKey()).send().join();
	}
}