package demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import demo.model.Product;
import demo.service.ProductService;

@Controller
public class ProductController {	

	@Autowired
	private ProductService productService;
	
	@RequestMapping("/product/{id}")
	public String getProductById(@PathVariable String id, Model model, final RedirectAttributes redirectAttributes) throws Exception {
		
		// Product to be passed to as ModelAttribute to the next controller
		Product prod = productService.findProductById(id).get();
		redirectAttributes.addFlashAttribute("product", prod);
		return "redirect:/showdetails/"+id;
	}
}