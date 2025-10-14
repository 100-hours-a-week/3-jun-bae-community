package com.ktb.community.service;

import com.ktb.community.entity.OwnedByUser;
import com.ktb.community.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OwnershipVerifier {

    public void check(OwnedByUser target, User actor) {
        check(target, actor, "Only author can modify this resource");
    }

    public void check(OwnedByUser target, User actor, String message) {
        if (target == null || actor == null || actor.getId() == null || target.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ownership verification state");
        }
        if (target.getUser().getId() == null || !actor.getId().equals(target.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }
}
