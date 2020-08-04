package demo.config.payment;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
public class BookOrder implements JobHandler {

  @Autowired private ZeebeClient zeebe;
  @Autowired private OrderService orderService;
  private JobWorker subscription;

  @PostConstruct
  public void subscribe() {
    subscription =
        zeebe
            .newWorker()
            .jobType("place-order")
            .handler(this)
            .timeout(Duration.ofMinutes(1))
            .open();
  }

  @PreDestroy
  public void closeSubscription() {
    subscription.close();
  }
  
  @Override
  public void handle(JobClient client, ActivatedJob job) {
    System.out.println("Order received");
    
    Cart cart = job.getVariablesAsType(Cart.class);
    cart.setOrderStatus(Enum.valueOf(OrderStatus.class, "Pending"));
    
    String orderId = orderService.addCustomerOrder(cart);
    System.out.println("Saved order details in db");

    // Add variables to be passed to the next service
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("ordersuccess", true);
    variables.put("orderId", orderId);
    
    // Call the next step in the workflow
    client
        .newCompleteCommand(job.getKey())
        .variables(variables)
        .send()
        .join();
  }
}

