package demo.config.payment;

import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import demo.model.Cart;
import demo.model.OrderStatus;
import demo.service.OrderService;
import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.client.api.worker.JobHandler;
import io.zeebe.client.api.worker.JobWorker;

@Component
public class CancelOrder implements JobHandler {

	@Autowired
	private ZeebeClient zeebe;
	@Autowired 
	private OrderService orderService;
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
		System.out.println("Inside cancel order.");
		
		// Get cart record from db
		String orderId =  new JSONObject(job.getVariables()).getString("orderId");
		System.out.println("Order Id to be canceled: " +orderId);
		Cart cart = orderService.findOrderById(orderId);
		
		// Update status
		cart.setOrderStatus(Enum.valueOf(OrderStatus.class, "Failed"));
		orderService.addCustomerOrder(cart);
		System.out.println("Updated order details in db");

		// Finish workflow
		client.newCompleteCommand(job.getKey()).send().join();
	}
}
