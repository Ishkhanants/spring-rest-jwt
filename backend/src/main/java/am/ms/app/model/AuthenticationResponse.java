package am.ms.app.model;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author Martin Mirzoyan
 * @author Seroja Grigoryan
 */

@Getter
public class AuthenticationResponse implements Serializable {

    private final String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }
}
