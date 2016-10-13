/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.picdrop.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author i330120
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity("groups")
public class Group extends Identifiable {
    
    @org.mongodb.morphia.annotations.Reference(ignoreMissing = true)
    List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }  
    
    public void addUser(User user) {
        this.users.add(user);
    }
      
    public void removeUser(User user) {
        this.users.remove(user);
    }
}
