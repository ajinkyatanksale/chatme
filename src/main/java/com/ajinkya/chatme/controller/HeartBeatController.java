package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.service.PresenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class HeartBeatController {
    @Autowired
    PresenceService presenceService;

    @MessageMapping("/heartbeat")
    public void heartBeat(@RequestBody @Valid Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        User user = (User) authentication.getPrincipal();
        if (user != null) {
            presenceService.refreshPresence(user.getId());
        }
    }
}
