package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import demo.model.Product;
import demo.service.ProductService;

@Controller
@Profile("monolith")
public class RecommendationController {

	@Autowired
	private ProductService productService;

	@GetMapping("/showdetails/{id}")
	public String getRecommendationByProductCategory(@PathVariable String id, @ModelAttribute("product") final Object product, Model model) {

		model.addAttribute("prod", product);
		model.addAttribute("recommend", getrecommendedProducts((Product) product));
		return "product";
	}
	
	private List<Product> getrecommendedProducts(Product product) {
		List<Product> recommendedProducts = productService.findProductsByCategory(product.getCategories());
		recommendedProducts.remove((Product) product);
		return recommendedProducts;
	}
}