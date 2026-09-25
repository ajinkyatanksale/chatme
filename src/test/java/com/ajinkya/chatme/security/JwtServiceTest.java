package com.ajinkya.chatme.security;

import com.ajinkya.chatme.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    JwtService jwtService;

    private void setValues() {
        ReflectionTestUtils.setField(jwtService, "JWT_TOKEN_VALIDITY", 86400000);
        ReflectionTestUtils.setField(jwtService, "SECRET", "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf");
    }

    @Test
    public void testGenerateToken() {
        setValues();
        User user = User.builder().username("abc").build();
        String token = jwtService.generateToken(user.getUsername());

        assertNotNull(token);
        assert(!token.isBlank());
    }

    @Test
    public void extractUsernameTest() {
        setValues();
        User user = User.builder().username("abc").build();
        String token = jwtService.generateToken(user.getUsername());

        String username = jwtService.extractUsername(token);

        assertNotNull(username);
        assert(!username.isBlank());
        assert("abc".equals(username));
    }

    @Test
    public void isTokenTest() {
        setValues();
        User user = User.builder().username("abc").build();
        String token = jwtService.generateToken(user.getUsername());

        Boolean isValid = jwtService.isTokenValid(token, user);

        assertNotNull(isValid);
        assert(isValid);
    }
}
