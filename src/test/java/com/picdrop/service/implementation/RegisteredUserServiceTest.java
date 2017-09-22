/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.picdrop.exception.ApplicationException;
import com.picdrop.helper.TestHelper;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author nvonstein
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisteredUserServiceTest extends ServiceTestBase {

    String ID1 = "590497a4cd27f408d6e79d98";
    String ID2 = "123456a4cd37f408d6e79d90";

    RegisteredUserService service;

    @Override
    public void setUp() {
        super.setUp();
        this.service = inj.getInstance(RegisteredUserService.class);
    }

    @Test
    public void testCreateValid() throws ApplicationException {
        when(registeredUserRepo.save(any())).thenAnswer(TestHelper.reflectWithId(0, ID1));

        RegisteredUser user = new RegisteredUser();
        user.setEmail("test@picdrop.com");
        user.setName("Foo");
        user.setLastname("Bar");
        user.setPhash("secret");

        RegisteredUser actual = service.create(user);

        verify(registeredUserRepo, times(1)).save(any());
    }

    @Test(expected = ApplicationException.class)
    public void testCreateInvalidEmail() throws ApplicationException {

        RegisteredUser user = new RegisteredUser();
        user.setEmail("stupid");
        user.setPhash("secret");

        try {
            RegisteredUser actual = service.create(user);
        } catch (ApplicationException ex) {
            assertEquals("wrong status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(registeredUserRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateInvalidPHash() throws ApplicationException {

        RegisteredUser user = new RegisteredUser();
        user.setEmail("test@picdrop.com");
        user.setPhash(null);

        try {
            RegisteredUser actual = service.create(user);
        } catch (ApplicationException ex) {
            assertEquals("wrong status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(registeredUserRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateInvalidNameTooLong() throws ApplicationException {

        RegisteredUser user = new RegisteredUser();
        user.setEmail("test@picdrop.com");
        user.setPhash("secret");

        StringBuilder sb = new StringBuilder();

        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // >256

        user.setName(sb.toString());

        try {
            RegisteredUser actual = service.create(user);
        } catch (ApplicationException ex) {
            assertEquals("wrong status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(registeredUserRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateInvalidLastNameTooLong() throws ApplicationException {

        RegisteredUser user = new RegisteredUser();
        user.setEmail("test@picdrop.com");
        user.setPhash("secret");

        StringBuilder sb = new StringBuilder();

        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // >256

        user.setLastname(sb.toString());

        try {
            RegisteredUser actual = service.create(user);
        } catch (ApplicationException ex) {
            assertEquals("wrong status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(registeredUserRepo, times(0)).save(any());
        }
    }

    @Test
    public void testGetMe() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID1);
        when(ctx.getPrincipal()).thenReturn(user);

        User actual = service.getMe();

        verify(registeredUserRepo, times(0)).get(any());
        assertNotNull("user was null", actual);
    }

    @Test
    public void testDeleteMe() throws ApplicationException, IOException {
        RegisteredUser user = new RegisteredUser(ID1);
        when(ctx.getPrincipal()).thenReturn(user);

        FileResource fr = new FileResource(ID2);
        fr.setFileId("SOME_ID");
        when(fileResourceRepo.queryNamed(any(), eq(ID1))).thenReturn(Arrays.asList(fr));
        when(fileResourceRepo.delete(ID2)).thenReturn(true);

        when(fileHandlingRepo.delete(eq("SOME_ID"))).thenReturn(true);

        service.deleteMe();

        verify(registeredUserRepo, times(0)).get(any());

        verify(tokenSetRepo, times(1)).deleteNamed(any(), eq(ID1));
        verify(collectionItemRepo, times(1)).deleteNamed(any(), eq(ID1));
        verify(collectionRepo, times(1)).deleteNamed(any(), eq(ID1));
        verify(shareRepo, times(1)).deleteNamed(any(), eq(ID1));
        verify(registeredUserRepo, times(1)).delete(eq(ID1));
    }

    @Test
    public void testUpdateMe() throws ApplicationException, IOException {
        RegisteredUser user = new RegisteredUser(ID1);
        when(ctx.getPrincipal()).thenReturn(user);
        when(registeredUserRepo.update(any(), any())).thenAnswer(TestHelper.reflect(1));

        RegisteredUser update = new RegisteredUser(ID1);
        update.setName("Foo");

        RegisteredUser actual = service.updateMe(update);

        verify(registeredUserRepo, times(0)).get(any());
        verify(registeredUserRepo, times(1)).update(eq(ID1), eq(user));

        assertEquals("field not updated", "Foo", actual.getName());
    }
}
