/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.picdrop.exception.AbstractExceptionMapper;
import com.picdrop.exception.ApplicationExeptionMapper;
import com.picdrop.guice.names.Config;
import com.picdrop.helper.EnvHelper;
import com.picdrop.json.JacksonConfigProvider;
import com.picdrop.json.Views;
import com.picdrop.service.implementation.AuthorizationService;
import com.picdrop.service.implementation.CollectionService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.FileResourceService;
import com.picdrop.service.implementation.ShareService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author i330120
 */
public class ApplicationModule extends AbstractApplicationModule {


    @Provides
    @Override
    protected ObjectMapper provideObjectMapper(EnvHelper env) {
        Properties p = env.getPropertiesWithDefault();
        return JacksonConfigProvider.createMapper(p.getProperty("service.json.view"));
    }

    @Provides
    @Override
    protected ObjectWriter provideObjectWriter(ObjectMapper mapper) {
        return mapper.writer();
    }

    @Provides
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
