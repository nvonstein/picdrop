/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.picdrop.guice.names.Config;
import com.picdrop.helper.EnvHelper;
import com.picdrop.helper.TestHelper;
import java.io.IOException;
import java.util.Properties;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import static org.mockito.Mockito.*;
import org.xml.sax.SAXException;

/**
 *
 * @author nvonstein
 */
public class ApplicationModuleMock extends AbstractApplicationModule {

    ApplicationModule appModule = new ApplicationModule();
    Tika t = mock(Tika.class);

    @Override
    protected void bindProperties(Binder binder) {
        EnvHelper ehlp = new EnvHelper("");
        ehlp.setConfig(TestHelper.getTestConfig());

        binder.bind(EnvHelper.class).toInstance(ehlp);

        Names.bindProperties(binder, TestHelper.getTestConfig());
        binder.bind(Properties.class)
                .annotatedWith(Config.class)
                .toInstance(TestHelper.getTestConfig());
    }

    @Provides
    @Override
    protected ObjectMapper provideObjectMapper(@Config Properties p) {
        return appModule.provideObjectMapper(p);
    }

    @Provides
    @Override
    protected ObjectWriter provideObjectWriter(ObjectMapper mapper) {
        return appModule.provideObjectWriter(mapper);
    }

    @Provides
    @Override
    protected Tika provideTika(String tikaConfigPath) throws TikaException, IOException, SAXException {
        return t;
    }

    public Tika getTika() {
        return t;
    }

}
