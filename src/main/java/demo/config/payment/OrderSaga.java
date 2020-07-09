package demo.config.payment;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.zeebe.client.ZeebeClient;

@Component
public class OrderSaga {
	
	@Value("${zeebe-endpoint}")
	private String broker;
	private ZeebeClient zeebe;

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {
    	zeebe = ZeebeClient.newClientBuilder().brokerContactPoint(broker).usePlaintext().build();
		zeebe.newDeployCommand().addResourceFromClasspath("order-process.bpmn").send().join();
    }
    
	@Bean
	public ZeebeClient zeebe() {
		return zeebe;
	}
	
	public void setZeebeClient(ZeebeClient zeebe) {
		this.zeebe = zeebe;
	}
}
