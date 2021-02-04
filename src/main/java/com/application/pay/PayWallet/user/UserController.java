package com.application.pay.PayWallet.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello dear, Welcome to Pay-Wallet";
    }

    @PostMapping("/user")
    public void addUser(@RequestBody() UserRequest userRequest) {
        userService.createUser(userRequest);
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") String id) {

//  If user nor found
//
//        if(userService.getUser(id)==null) {
//
//        }


        return userService.getUser(id);
    }
}
