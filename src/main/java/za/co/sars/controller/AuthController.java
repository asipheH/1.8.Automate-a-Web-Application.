package za.co.sars.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import za.co.sars.model.Employee;

@Controller
public class AuthController {

    private final Map<String, Employee> employees = new ConcurrentHashMap<String, Employee>();

    @GetMapping("/")
    public String index(){
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationFrom(Model model){
        model.addAttribute("employee", new Employee());
        return "register";
    }

    @PostMapping("/register")
public String registerEmployee(
        @ModelAttribute("employee") Employee employee,
        @RequestParam("confirmPassword") String confirmPassword,
        Model model) {

    String email = employee.getEmail() == null
            ? ""
            : employee.getEmail().trim().toLowerCase();

    String fullName = employee.getFullName() == null
            ? ""
            : employee.getFullName().trim();

    String password = employee.getPassword() == null
            ? ""
            : employee.getPassword();

    String confirmedPassword = confirmPassword == null
            ? ""
            : confirmPassword;

    if (fullName.isEmpty()
            || email.isEmpty()
            || password.isEmpty()
            || confirmedPassword.isEmpty()) {

        model.addAttribute(
                "error",
                "Make sure all fields are filled in"
        );

        return "register";
    }

    if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
        model.addAttribute(
                "error",
                "Please enter a valid email address"
        );

        return "register";
    }

    if (!password.equals(confirmedPassword)) {
        model.addAttribute(
                "error",
                "Passwords do not match"
        );

        return "register";
    }

    // Reject duplicate email before saving the employee
    if (employees.containsKey(email)) {
        model.addAttribute(
                "error",
                "An employee with this email already exists"
        );

        return "register";
    }

    employee.setFullName(fullName);
    employee.setEmail(email);
    employee.setPassword(password);

    employees.put(email, employee);

    return "redirect:/login?registered";
}

    @GetMapping("/login")
    public String showLoginForm(@RequestParam (value = "registered", required = false) String registered, Model model){
        if(registered != null){
            model.addAttribute("success", "Registration successful. You can now login");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model){
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();

        Employee employee = employees.get(normalizedEmail);

        if(employee != null && employee.getPassword().equals(password)){
            model.addAttribute("fullName", employee.getFullName());
            model.addAttribute("email", employee.getEmail());

            return "home";
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }
}
