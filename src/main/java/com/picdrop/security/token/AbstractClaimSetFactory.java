/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.google.common.base.Strings;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractClaimSetFactory<T> implements ClaimSetFactory<T> {

    Logger log = LogManager.getLogger(this.getClass());

    protected int configJwtExpiry;
    protected String configJwtIssuer;
    protected String configJwtAudience;

    public AbstractClaimSetFactory(
            int jwtExpiry,
            String jwtIssuer,
            String jwtAudience) {
        this.configJwtExpiry = jwtExpiry;
        this.configJwtIssuer = jwtIssuer;
        this.configJwtAudience = jwtAudience;
    }

    public AbstractClaimSetFactory() {
        this(60, "", "");
    }

    @Override
    public JWTClaimsSet generate() {
        return this.builder().build();
    }

    @Override
    public JWTClaimsSet.Builder builder() {
        DateTime now = DateTime.now(DateTimeZone.UTC);
        return new JWTClaimsSet.Builder()
                .audience(configJwtAudience)
                .issueTime(now.toDate())
                .expirationTime(now.plusMinutes(configJwtExpiry).toDate())
                .issuer(configJwtIssuer);
    }

    protected boolean verifyGeneralClaims(JWTClaimsSet claims) {
        if (claims == null) {
            log.debug("No claims provided");
            return false;
        }
        if ((claims.getExpirationTime() == null)
                || claims.getExpirationTime().before(DateTime.now(DateTimeZone.UTC).toDate())) {
            log.debug("Token expired");
            return false;
        }
        if (claims.getAudience().isEmpty()
                || !claims.getAudience().contains(configJwtAudience)) {
            log.debug("Wrong audience");
            return false;
        }
        if (Strings.isNullOrEmpty(claims.getIssuer())
                || !claims.getIssuer().equals(this.configJwtIssuer)) {
            log.debug("Illegal issuer");
            return false;
        }
        if (Strings.isNullOrEmpty(claims.getJWTID())) {
            log.debug("No JWT ID set");
            return false;
        }

        return true;
    }

}
