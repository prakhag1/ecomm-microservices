package demo.microservices.recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import demo.microservices.sync.grpc.ListRecommendationsResponse;
import demo.microservices.sync.grpc.RecommendationServiceGrpc;
import demo.microservices.sync.grpc.RecommendationsRequest;
import demo.model.Product;
import demo.service.ProductService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

@Controller
@Profile("microservices")
public class RecommendationController {

	@Autowired
	private ProductService productService;
	@Value("${recommendation-service-endpoint}")
	private String recommendationServiceEndpoint;
	@Value("${recommendation-service-port}")
	private String recommendationServicePort;
	
	// Async gRPC stub
	private RecommendationServiceGrpc.RecommendationServiceStub asyncStub;
	// Sync gRPC stub
	private RecommendationServiceGrpc.RecommendationServiceBlockingStub blockingStub;

	@PostConstruct
	private void init() {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(recommendationServiceEndpoint, Integer.parseInt(recommendationServicePort)).usePlaintext().build();
		asyncStub = RecommendationServiceGrpc.newStub(managedChannel);
		blockingStub = RecommendationServiceGrpc.newBlockingStub(managedChannel);
	}

	@GetMapping("/showdetails/{id}")
	public String getRecommendationByProductCategory(@PathVariable String id, @ModelAttribute("product") final Object product, Model model) throws InterruptedException {
		model.addAttribute("prod", product);
		model.addAttribute("recommend", getrecommendedProductsSync((Product) product));
		return "product";
	}

	/**
	 * The following method shows an example of a synchronous gRPC call to remote recommendation service
	 * This method is called as part of a composite UI/backend-for-frontend pattern for microservices 
	 */
	private List<Product> getrecommendedProductsSync(Product product) {
		
		List<Product> recommendedProducts = new ArrayList<Product>();
		RecommendationsRequest request = RecommendationsRequest.newBuilder().addAllProductCategory(product.getCategories()).build();
		
		// Blocking call to remote recommendation microservice
		try {
			ListRecommendationsResponse response = blockingStub.withDeadlineAfter(2, TimeUnit.SECONDS).listRecommendations(request);
			for (String pid : response.getProductIdsList()) {
				recommendedProducts.add(productService.findProductById(pid).get());
			}
		}
		catch (StatusRuntimeException e) {
			e.printStackTrace();
		}
		recommendedProducts.remove(product);
		return recommendedProducts;
	}
	
	/**
	 *  The following method demostrates gRPC async call to a remote recommendation service 
	 *  Since the UI expects recommended products to be provided at the time of rendering the view, the following code blocks until async call returns (using countdownlatch)
	 *  This example only intends to show how gRPC can be leveraged to make async calls between microservices
	 */
	@SuppressWarnings("unused")
	private List<Product> getrecommendedProductsAsync(Product product) throws InterruptedException {
		
		List<Product> recommendedProducts = new ArrayList<Product>();
		RecommendationsRequest request = RecommendationsRequest.newBuilder().addAllProductCategory(product.getCategories()).build();
		final CountDownLatch finishLatch = new CountDownLatch(1);
		
		// Async callback
		StreamObserver<ListRecommendationsResponse> responseObserver = new StreamObserver<ListRecommendationsResponse>() {
			@Override
			public void onNext(ListRecommendationsResponse response) {
				for (String pid : response.getProductIdsList()) {
					recommendedProducts.add(productService.findProductById(pid).get());
				}
				recommendedProducts.remove(product);
			}
			
			@Override
			public void onCompleted() {
				finishLatch.countDown();
			}

			@Override
			public void onError(Throwable arg0) {
				finishLatch.countDown();
			}
		};

		// Async call to recommendations microservice
		asyncStub.listRecommendations(request, responseObserver);		
		finishLatch.await();
		
		return recommendedProducts;
	}
}