/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.model.TokenSet;
import com.picdrop.model.TokenSetReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author nvonstein
 */
public class AuthTokenClaimSetFactory extends AbstractClaimSetFactory<User> {

    protected HashFunction hashf;
    protected Repository<String, RegisteredUser> repo;

    @Inject
    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo,
            @Named("service.session.jwt.auth.exp") int jwtExpiry,
            @Named("service.session.jwt.iss") String jwtIssuer,
            @Named("service.session.jwt.aud") String jwtAudience) {
        super(jwtExpiry, jwtIssuer, jwtAudience);

        this.repo = repo;
        this.hashf = Hashing.murmur3_32(new Random().nextInt());
    }

    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo) {
        this(repo, 60, "", "");
    }

    @Override
    public JWTClaimsSet.Builder builder() {
        return super.builder()
                .jwtID(hashf.hashLong(DateTime.now(DateTimeZone.UTC).getMillis()).toString())
                .claim("tok", "auth");
    }

    @Override
    public User verify(JWTClaimsSet claims) {
        RegisteredUser user;
        if (verifyGeneralClaims(claims)) {
            return null;
        }

        try {
            if (Strings.isNullOrEmpty(claims.getStringClaim("tok"))
                    || !claims.getStringClaim("tok").equals("auth")) {
                return null;
            }

            user = repo.get(claims.getSubject());
            if (user == null) {
                return null;
            }

            String jti = claims.getJWTID();
            List<TokenSetReference> tsrefs = user.getTokens();

            TokenSet ts = null;
            boolean dirty = false;
            for (int i = 0; i < tsrefs.size(); i++) {
                ts = tsrefs.get(i).resolve(true);
                if (ts == null) {
                    user = user.removeToken(tsrefs.get(i));
                    dirty = true;
                } else if (ts.getAuthJti().equals(jti)) {
                    break;
                } else {
                    ts = null;
                }
            }

            if (dirty) {
                user = repo.update(user.getId(), user); // Update deleted TokenSet refs
            }

            if (ts == null) {
                return null;
            }

            user.setActiveToken(ts);
        } catch (ParseException ex) {
            return null;
        }

        return user;
    }
}
