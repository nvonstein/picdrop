/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author nvonstein
 */
@Entity("ratings")
public class Rating extends InteractionBase {
    
    int rate = 0;

    public Rating() {
    }

    @JsonView(value = Views.Public.class)
    public int getRate() {
        return rate;
    }

    @JsonView(value = Views.Public.class)
    public void setRate(int rate) {
        this.rate = (rate < 0) ? 0 : (rate > 6) ? 6 : rate;
    }
    
}
