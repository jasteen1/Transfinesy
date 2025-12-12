package com.transfinesy.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring MVC Controller for handling home page requests.
 * 
 * This controller handles the root URL and home page of the application.
 * It displays the welcome/landing page to users.
 * 
 * Routes:
 * - GET / - Display home page
 * 
 * Functionality:
 * - Sets page title for the view
 * - Returns the index template
 * 
 * @author transFINESy Development Team
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Welcome");
        return "index";
    }
}

