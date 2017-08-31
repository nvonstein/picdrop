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
public class AuthTokenClaimSetFactory extends AbstractClaimSetFactory<RegisteredUser> {

    protected HashFunction hashf;
    protected Repository<String, RegisteredUser> repo;
    protected Repository<String, TokenSet> tsrepo;

    @Inject
    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo,
            Repository<String, TokenSet> tsrepo,
            @Named("service.jwt.auth.exp") int jwtExpiry,
            @Named("service.jwt.iss") String jwtIssuer,
            @Named("service.jwt.aud") String jwtAudience) {
        super(jwtExpiry, jwtIssuer, jwtAudience);

        this.repo = repo;
        this.tsrepo = tsrepo;
        this.hashf = Hashing.murmur3_32(new Random().nextInt());
    }

    public AuthTokenClaimSetFactory(Repository<String, RegisteredUser> repo, Repository<String, TokenSet> tsrepo) {
        this(repo, tsrepo, 60, "", "");
    }

    @Override
    public JWTClaimsSet.Builder builder() {
        return super.builder()
                .jwtID(hashf.hashLong(DateTime.now(DateTimeZone.UTC).getMillis()).toString())
                .claim("tok", "auth");
    }

    @Override
    public RegisteredUser verify(JWTClaimsSet claims) {
        RegisteredUser user;
        if (!verifyGeneralClaims(claims)) {
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
            List<TokenSet> tss = tsrepo.queryNamed("tokens.with.authJti.ownedBy", jti, user.getId());

            if (tss.isEmpty()) {
                return null;
            }

            user.setActiveToken(tss.get(0));
        } catch (ParseException | IOException ex) {
            return null;
        }

        return user;
    }
}
