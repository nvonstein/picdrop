/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picdrop.helper.TestHelper;
import com.picdrop.json.JacksonConfigProvider;
import com.picdrop.json.Views;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static com.picdrop.helper.TestHelper.*;

/**
 *
 * @author nvonstein
 */
@RunWith(JUnit4.class)
public class RegisteredUserJsonTest {

    ObjectMapper mapper;
    ObjectMapper mapperOnline;

    RegisteredUser regUser;
    String mockJson;

    final String[] PUBLIC = {
        "name",
        "created",
        "lastname",
        "email",
        "lastlogin",
        "id"
    };

    final String[] DETAILED = {
        "permissions"
    };

    final String[] IGNORE = {
        "registered",
        "fullName",
        "refer",
        "phash"
    };

    public RegisteredUserJsonTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        this.mapperOnline = JacksonConfigProvider.createMapper();
        this.mapper = new ObjectMapper();

        this.mockJson = TestHelper.readMockJson("RegisteredUser");

        RegisteredUser ru = new RegisteredUser("123456a4cd37f408d6e79d90");
        ru.setEmail("foo");
        ru.setLastLogin();
        ru.setLastname("foo");
        ru.setName("foo");
        ru.setPhash("foo");
        ru.setPermissions(Arrays.asList("foo"));
        
        this.regUser = ru;
    }

    @After
    public void tearDown() {
    }


    @Test
    public void serializePublicTest() throws Exception {
        String json;

        json = mapper
                .writerWithView(Views.Public.class)
                .writeValueAsString(this.regUser);

        assertContains(json, quote(PUBLIC));
        assertNotContains(json, quote(DETAILED));
        assertNotContains(json, quote(IGNORE));
    }

    @Test
    public void serializeDetailedTest() throws Exception {
        String json;

        json = mapper
                .writerWithView(Views.Detailed.class)
                .writeValueAsString(this.regUser);

        assertContains(json, quote(PUBLIC));
        assertContains(json, quote(DETAILED));
        assertNotContains(json, quote(IGNORE));
    }

    @Test
    public void serializeTestOnline() throws Exception {
        String json;

        json = mapperOnline
                .writerWithView(Views.Public.class)
                .writeValueAsString(this.regUser);

        assertContains(json, quote(PUBLIC));
        assertNotContains(json, quote(DETAILED));
        assertNotContains(json, quote(IGNORE));
    }

    @Test
    public void deserializePublicTest() throws Exception {
        RegisteredUser u = this.mapper
                .readerWithView(Views.Public.class)
                .forType(RegisteredUser.class)
                .readValue(mockJson);

        
        assertEquals(0, u.getPermissions().size());

        assertNotNull(u.getId());
        assertNotNull(u.getEmail());
        assertNotNull(u.getName());
        assertNotNull(u.getPhash());
        assertNotNull(u.getLastname());
    }

}
