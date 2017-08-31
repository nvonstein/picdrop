/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author i330120
 */
public abstract class NamedQueries {

    public static Map<String, String> getQueries() {
        Map<String, String> m = new HashMap<>();


        m.put("with.owner", "{ owner: { _id: ObjectId(?0) } }");

        m.put("registeredUser.byEmail", "{ email: ?0 }");
        m.put("shares.byUri", "{ uri: ?0 }");
        m.put("citems.byResourceId", "{ resource: { _id: ?0}}");

        m.put("tokens.with.authJti.ownedBy", "{ authJti: ?0, owner: { _id: ObjectId(?1) }}");
        m.put("tokens.with.refreshJti.ownedBy", "{ refreshJti: ?0, owner: { _id: ObjectId(?1) }}");

        return Collections.unmodifiableMap(m);
    }
}
