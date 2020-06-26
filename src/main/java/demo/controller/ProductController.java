package demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import demo.model.Product;
import demo.service.AdsService;
import demo.service.ProductService;
import demo.service.RecommendationService;
import demo.utils.SpanUtils;
import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;

@Controller
public class ProductController {
  @Autowired private ProductService productService;
  @Autowired private RecommendationService recommendationService;
  @Autowired private AdsService adsService;
  @Autowired private Tracer tracer;

  @RequestMapping("/product/{id}")
  public String getProductById(@PathVariable String id, Model model) throws Exception {
    Span span = SpanUtils.buildSpan(tracer, "Get Product Details").startSpan();
    try (Scope ws = tracer.withSpan(span)) {
      Product prod = productService.findProductById(id).get();
      model.addAttribute("prod", prod);
      model.addAttribute("recommend", recommendationService.getrecommendedProducts((Product) prod));
      model.addAttribute("ad", adsService.getrecommendedAd((Product) prod));
    }
    span.end();
    return "product";
  }
}
