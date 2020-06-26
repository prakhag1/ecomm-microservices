package demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import demo.config.recommendation.GrpcClientInterceptor;
import demo.microservices.sync.grpc.ListRecommendationsResponse;
import demo.microservices.sync.grpc.RecommendationServiceGrpc;
import demo.microservices.sync.grpc.RecommendationsRequest;
import demo.model.Product;
import demo.utils.SpanUtils;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;

@Service
public class RecommendationService {
  @Autowired private ProductService productService;
  @Autowired private GrpcClientInterceptor interceptor;
  @Autowired private Tracer tracer;

  @Value("${recommendation-service-endpoint}")
  private String svcEndpoint;

  @Value("${recommendation-service-port}")
  private String svcPort;

  private RecommendationServiceGrpc.RecommendationServiceBlockingStub blockingStub;
  private List<Product> recommendedProducts;

  @PostConstruct
  private void init() throws Exception {
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forAddress(svcEndpoint, Integer.parseInt(svcPort))
            .usePlaintext()
            .build();
    Channel channel = ClientInterceptors.intercept(managedChannel, interceptor);
    blockingStub = RecommendationServiceGrpc.newBlockingStub(channel);
  }

  public List<Product> getrecommendedProducts(Product product) {
    Span span = SpanUtils.buildSpan(tracer, "Recommendation Service").startSpan();
    try (Scope ws = tracer.withSpan(span)) {
      recommendedProducts = new ArrayList<Product>();
      ListRecommendationsResponse response =
          blockingStub.listRecommendations(
              RecommendationsRequest.newBuilder()
                  .addAllProductCategory(product.getCategories())
                  .build());
      for (String pid : response.getProductIdsList()) {
        recommendedProducts.add(productService.findProductById(pid).get());
      }
      recommendedProducts.remove(product);
    }
    span.end();
    return recommendedProducts;
  }
}
