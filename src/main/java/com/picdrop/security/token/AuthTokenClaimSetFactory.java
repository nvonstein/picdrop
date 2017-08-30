/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import java.text.ParseException;

/**
 *
 * @author nvonstein
 */
public class AuthTokenClaimSetFactory extends AbstractClaimSetFactory<User> {

    protected Repository<String, RegisteredUser> repo;

    @Inject
    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo,
            @Named("service.session.jwt.exp") int jwtExpiry,
            @Named("service.session.jwt.iss") String jwtIssuer,
            @Named("service.session.jwt.aud") String jwtAudience) {
        super(jwtExpiry, jwtIssuer, jwtAudience);

        this.repo = repo;
    }

    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo) {
        this(repo, 60, "", "");
    }

    @Override
    public JWTClaimsSet.Builder builder() {
        return super.builder()
                .claim("tok", "auth");
    }
    
    @Override
    public User verify(JWTClaimsSet claims) {
        if (verifyGeneralClaims(claims)) {
            return null;
        }
        
        try {
            if (Strings.isNullOrEmpty(claims.getStringClaim("tok"))
                    || !claims.getStringClaim("tok").equals("auth")) {
                return null;
            }
        } catch (ParseException ex) {
            return null;
        }
        
        return repo.get(claims.getSubject());
    }
}
