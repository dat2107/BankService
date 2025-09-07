package org.example.bankservice.controller.View;

import org.example.bankservice.dto.AccountDTO;
import org.example.bankservice.dto.UserDTO;
import org.example.bankservice.model.Account;
import org.example.bankservice.service.AccountService;
import org.example.bankservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class ViewAuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    // Hiển thị trang login (chỉ render view)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.jsp / login.html
    }

    // API xử lý login trả JSON để FE lưu token
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            String token = userService.login(userDTO.getUsername(), userDTO.getPassword());
            return ResponseEntity.ok(token); // FE sẽ tự lưu vào localStorage
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Sai username hoặc password!");
        }
    }

    // Hiển thị trang register
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // register.jsp / register.html
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.create(accountDTO);
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/home")
    public String showHomeform(){
        return "home";
    }

    @GetMapping("/dashboard")
    public String showDashBoardform(){
        return "dashboard";
    }

    @GetMapping("/dashboardContent")
    public String dashboardContent() {
        return "dashboardContent";  // /WEB-INF/views/dashboardContent.jsp
    }

    @GetMapping("/forgot")
    public String showForgotform(){
        return "forgot";
    }

    @GetMapping("/account")
    public String showAccountForm() {
        return "account"; // account.jsp trong /WEB-INF/views/
    }

    @GetMapping("/createCard")
    public String showCreateCardForm(){
        return "createCard";
    }

    @GetMapping("/user")
    public String showUserForm(){
        return "user";
    }

    @GetMapping("/user-level")
    public String showUserLevelForm(){
        return "user-level";
    }

    @GetMapping("/vip-detail")
    public String vipDetailPage() {
        return "vip-detail"; // Spring sẽ map tới /WEB-INF/views/vip-detail.jsp
    }

    @GetMapping("/updateUser")
    public String updateUserPage() {
        return "updateUser";
    }

    @GetMapping("/userDetail")
    public String userDetailPage(){
        return "userDetail";
    }

    @GetMapping("/cardManager")
    public String cardManagerPage(){
        return "cardManager";
    }

    @GetMapping("/cardDetail")
    public String cardDetailPage(){
        return "cardDetail";
    }

    @GetMapping("/transfer")
    public String transferPage(){
        return "transfer";
    }
}
