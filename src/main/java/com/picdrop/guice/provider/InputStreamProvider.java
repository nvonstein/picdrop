/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public interface InputStreamProvider {
    InputStream get() throws IOException;
}
