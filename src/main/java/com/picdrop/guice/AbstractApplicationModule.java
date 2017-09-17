/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.picdrop.exception.AbstractExceptionMapper;
import com.picdrop.exception.ApplicationExeptionMapper;
import com.picdrop.guice.names.Config;
import com.picdrop.helper.EnvHelper;
import com.picdrop.json.JacksonConfigProvider;
import com.picdrop.service.implementation.AuthorizationService;
import com.picdrop.service.implementation.CollectionService;
import com.picdrop.service.implementation.FileResourceService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.ShareService;
import java.io.IOException;
import java.util.Properties;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractApplicationModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        // Services
        bindServices(binder);
        // Environment
        bindProperties(binder);
        // Static ObjectMapper
        bindStatics(binder);
    }
    
    protected void bindProperties(Binder binder) {
        EnvHelper ehlp = EnvHelper.from("picdrop.app.properties");
        binder.bind(EnvHelper.class).toInstance(ehlp);
        
        Properties config = ehlp.getPropertiesWithDefault();
        Names.bindProperties(binder, config);
        binder.bind(Properties.class).annotatedWith(Config.class).toInstance(config);
    }
    
    protected void bindStatics(Binder binder) {
        binder.requestStaticInjection(ApplicationExeptionMapper.class);
        binder.requestStaticInjection(AbstractExceptionMapper.class);
        
        binder.requestStaticInjection(JacksonConfigProvider.class);
    }
    
    protected void bindServices(Binder binder) {
        binder.bind(FileResourceService.class).asEagerSingleton();
        binder.bind(RegisteredUserService.class).asEagerSingleton();
        binder.bind(AuthorizationService.class).asEagerSingleton();
        binder.bind(CollectionService.class).asEagerSingleton();
        binder.bind(ShareService.class).asEagerSingleton();
    }
    
    protected abstract ObjectMapper provideObjectMapper(Properties p);
    
    protected abstract ObjectWriter provideObjectWriter(ObjectMapper mapper);
    
    protected abstract Tika provideTika(String tikaConfigPath) throws TikaException, IOException, SAXException;
    
}
