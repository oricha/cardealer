package com.cardealer.controller;

import com.cardealer.model.User;
import com.cardealer.service.MessageService;
import com.cardealer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesController {

    private final UserService userService;
    private final MessageService messageService;

    @ModelAttribute("unreadMessageCount")
    public long unreadMessageCount(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return 0L;
        }

        try {
            User user = userService.getUserByEmail(authentication.getName());
            return messageService.getUnreadCount(user.getId());
        } catch (Exception ignored) {
            return 0L;
        }
    }
}
