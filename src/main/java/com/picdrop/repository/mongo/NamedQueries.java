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
        
//        m.put("getChild", "{ parent: DBRef(?0, ObjectId(?1)) }");
        m.put("getChild", "{ parent: { $ref:?0, $id: ObjectId(?1) } }");
        m.put("ownedBy", "{ owner: DBRef(?0, ObjectId(?1)) }");
        
        m.put("registeredUser.byEmail", "{ email: ?0 }");
         m.put("shares.byUri", "{ uri: ?0 }");
        
        return Collections.unmodifiableMap(m);
    }
}
