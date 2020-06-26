package demo.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import demo.model.Ad;
import demo.model.Product;
import demo.utils.HttpUtils;
import demo.utils.SpanUtils;
import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;

@Service
public class AdsService {
  @Autowired private Tracer tracer;

  @Value("${ads-service-endpoint}")
  private String svcEndpoint;

  public Ad getrecommendedAd(Product product) {
    Span span = SpanUtils.buildSpan(tracer, "Ad Service").startSpan();
    String result;
    String url = "http://" + svcEndpoint + "/servead";

    try (Scope ws = tracer.withSpan(span)) {
      result =
          HttpUtils.callEndpoint(
              url,
              HttpMethod.POST,
              tracer,
              new JSONObject().put("category", product.getCategories().get(0)));
    } catch (Exception e) {
      span.setStatus(Status.ABORTED);
      span.addAnnotation("Error while calling service");
      result = "";
    }
    span.end();
    return new Ad.AdBuilder()
        .setRedirectUrl(result.split(":")[0])
        .setText(result.split(":")[1])
        .build();
  }
}
