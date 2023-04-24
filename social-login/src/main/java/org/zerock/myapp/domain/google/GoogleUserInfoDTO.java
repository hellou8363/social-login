package org.zerock.myapp.domain.google;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class GoogleUserInfoDTO {
    private String iss;
    private String azp; // O
    private String aud; // O
    private String sub; // O
    private String email; // O
    private String email_verified; // O
    private String atHash;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private String locale;
    private String iat;
    private String exp;
    private String alg;
    private String kid;
    private String typ;
    private String scope;
    private String expires_in;
    private String access_type;
} // end class