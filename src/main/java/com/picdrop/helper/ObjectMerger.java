/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface ObjectMerger {

    public <T> T merge(T defaults, T update) throws IOException;
}
