package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import demo.model.Product;
import demo.service.CartService;

@Controller
@SessionAttributes("cart_size") //Used by header.ftl that's common across all pages, hence cart_size used as a session attribute
public class HomeController {

	@Autowired
	private List<Product> productList;
	@Autowired
	private CartService cartService;

	@GetMapping(value = "/")
	public String home(Model model) {
		model.addAttribute("products", productList);
		model.addAttribute("cart_size", cartService.getCart().getCartItems().size());
		return "productlist";
	}
}