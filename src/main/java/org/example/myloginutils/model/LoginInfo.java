package org.example.myloginutils.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.ResponseCookie;

@Data
@Accessors(chain = true)
public class LoginInfo {
    private ResponseCookie cookie;

    private Object userInfo;

    private String message;
}
