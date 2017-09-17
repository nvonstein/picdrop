/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.picdrop.guice.names.Config;
import com.picdrop.json.JacksonConfigProvider;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.inject.Singleton;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author i330120
 */
public final class ApplicationModule extends AbstractApplicationModule {

    @Provides
    @Singleton
    @Override
    protected ObjectMapper provideObjectMapper(@Config Properties p) {
        return JacksonConfigProvider.createMapper(p.getProperty("service.json.view"));
    }

    @Provides
    @Override
    protected ObjectWriter provideObjectWriter(ObjectMapper mapper) {
        return mapper.writer();
    }

    @Provides
    @Singleton
    @Override
    protected Tika provideTika(@Named("service.tika.config") String tikaConfigPath) throws TikaException, IOException, SAXException {
        Tika tika;
        if (Strings.isNullOrEmpty(tikaConfigPath)) {
            tika = new Tika();
        } else {
            tika = new Tika(new TikaConfig(new File(tikaConfigPath)));
        }
        return tika;
    }
}
