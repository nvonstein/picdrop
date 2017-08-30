/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.nimbusds.jwt.JWTClaimsSet;

/**
 *
 * @author nvonstein
 */
public interface ClaimSetFactory<R> {
    public JWTClaimsSet generate();
    public JWTClaimsSet.Builder builder();
    public R verify(JWTClaimsSet claims);
}
