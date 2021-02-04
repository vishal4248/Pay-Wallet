package com.application.pay.PayWallet.user;

import com.application.pay.PayWallet.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletService walletService;

    public void createUser(UserRequest userRequest) {

        User user = User.builder()
                .userId(userRequest.getUserId())
                .email(userRequest.getEmail())
                .mobile(userRequest.getMobile())
                .build();
        userRepository.save(user);

        walletService.newUser(userRequest.getUserId());
    }

    public User getUser(String userId) {

        User user = userRepository.findUserByUserId(userId).orElseThrow(
                ()->new NoSuchElementException(String.format("User %s not found ",userId))
        );

        return user;
    }
}
