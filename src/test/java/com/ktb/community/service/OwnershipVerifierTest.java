package com.ktb.community.service;

import com.ktb.community.entity.OwnedByUser;
import com.ktb.community.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OwnershipVerifierTest {

    private OwnershipVerifier ownershipVerifier;

    @BeforeEach
    void setUp() {
        ownershipVerifier = new OwnershipVerifier();
    }

    @Test
    void check_withMatchingIds_allowsAccess() {
        User owner = buildUser(1L);
        OwnedByUser resource = new OwnedResource(owner);

        ownershipVerifier.check(resource, buildUser(1L));
    }

    @Test
    void check_withNullActor_throwsBadRequest() {
        User owner = buildUser(1L);
        OwnedByUser resource = new OwnedResource(owner);

        assertThatThrownBy(() -> ownershipVerifier.check(resource, null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void check_withDifferentOwner_throwsForbidden() {
        User owner = buildUser(1L);
        OwnedByUser resource = new OwnedResource(owner);
        User other = buildUser(2L);

        assertThatThrownBy(() -> ownershipVerifier.check(resource, other, "custom"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    private static User buildUser(Long id) {
        User user = User.builder()
                .email("user" + id + "@example.com")
                .password("pw")
                .nickname("user" + id)
                .active(true)
                .admin(false)
                .deleted(false)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private static final class OwnedResource implements OwnedByUser {
        private final User user;

        private OwnedResource(User user) {
            this.user = user;
        }

        @Override
        public User getUser() {
            return user;
        }
    }
}
