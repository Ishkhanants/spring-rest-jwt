package am.ms.app.controller;

import am.ms.app.model.dto.UserDTO;
import am.ms.app.model.AuthenticationResponse;
import am.ms.app.security.JwtUtil;
import am.ms.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @author Martin Mirzoyan
 * @author Seroja Grigoryan
 */

@CrossOrigin(origins = "http://localhost:8080")
@RestController
class UserController {

    private AuthenticationManager authenticationManager;

    private JwtUtil jwtTokenUtil;

    private UserService userService;

    @Autowired
    public UserController(AuthenticationManager authenticationManager,
                          JwtUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @GetMapping("/")
    public String firstPage() {
        return "Hello";
    }

    @PostMapping(value = "/registration", produces = "application/json")
    public UserDTO submitAdd(@RequestBody UserDTO userDTO) {
        userService.save(userDTO);
        return userDTO;
    }

    @GetMapping("/hello")
    public String success() {
        return "Hello Dear Authenticated Friend!";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO userDTO)
            throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDTO.getUsername(),
                    userDTO.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService
                .loadUserByUsername(userDTO.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
