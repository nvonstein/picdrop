/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.picdrop.exception.ApplicationException;
import com.picdrop.helper.TestHelper;
import com.picdrop.model.Share;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.Collection.CollectionItem;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.FileResourceReference;
import com.picdrop.model.user.RegisteredUser;
import java.io.IOException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author nvonstein
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionServiceTest extends ServiceTestBase {

    CollectionService service;

    String ID1 = "590497a4cd27f408d6e79d98";
    String ID2 = "123456a4cd37f408d6e79d90";

    @Override
    @Before
    public void setUp() {
        super.setUp();
        service = inj.getInstance(CollectionService.class);
    }

    @Test
    public void testCreateCollectionEmptyValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        when(collectionRepo.save(any())).thenAnswer(TestHelper.reflectWithId(0, ID1));

        Collection col = new Collection();
        col.setName("Foobar");

        Collection actual = service.create(col);

        assertNotNull("returned null", actual);
        assertNotNull("owner is null", actual.getOwner());
        assertEquals("owner id mismatch", actual.getOwner().getId(), ID2);
        assertNotNull("id is null", actual.getId());
    }

    @Test
    public void testCreateCollectionWithItemValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        FileResource res = new FileResource(ID2);
        when(fileResourceRepo.get(ID2)).thenReturn(res);

        Collection.CollectionItem ci = new Collection.CollectionItem();
        ci.setResource(res);
        when(collectionItemRepo.save(any())).thenAnswer(TestHelper.reflectWithId(0, ID2));
        when(collectionItemRepo.get(ID2)).thenReturn(ci);

        Collection col = new Collection();
        col.setName("Foobar");
        col.addItem(ci);
        when(collectionRepo.save(any())).thenAnswer(TestHelper.reflectWithId(0, ID1));
        when(collectionRepo.update(eq(ID1), any())).thenAnswer(TestHelper.reflect(1));

        Collection actual = service.create(col);

        assertNotNull("returned null", actual);
        assertNotNull("items is null", actual.getItems());
        assertEquals("wrong number of items", 1, actual.getItems().size());

        Collection.CollectionItemReference actualCiRef = actual.getItems().get(0);
        assertNotNull("ci ref id null", actualCiRef.getId());
        assertEquals("ci ref id mismatch", ID2, actualCiRef.getId());

        Collection.CollectionItem actualCi = actualCiRef.resolve(false);
        assertNotNull("ci ref resolved to null", actualCi);
        assertNotNull("res ref is null", actualCi.getResource());
        assertEquals("res ref id mismatch", ID2, actualCi.getResource().getId());

        assertNotNull("comments not initialized", actualCi.getComments());
        assertNotNull("ratings not initialized", actualCi.getRatings());
    }

    @Test(expected = ApplicationException.class)
    public void testCreateCollectionWithItemInvalidResource() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        FileResource res = new FileResource(ID2);
//        when(fileResourceRepo.get(ID2)).thenReturn(res); // simulate not existing resource

        Collection.CollectionItem ci = new Collection.CollectionItem();
        ci.setResource(res);

        Collection col = new Collection();
        col.addItem(ci);

        try {
            Collection actual = service.create(col);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).save(any());
            verify(collectionItemRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateCollectionInvalidNoBody() throws ApplicationException {
        try {
            Collection actual = service.create(null);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateCollectionEmptyInvalidName1() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        StringBuilder sb = new StringBuilder();

        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // >256

        Collection col = new Collection();
        col.setName(sb.toString());

        try {
            Collection actual = service.create(col);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).save(any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCreateCollectionEmptyInvalidName2() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        Collection col = new Collection();
        col.setName("abc&%$xyz");

        try {
            Collection actual = service.create(col);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).save(any());
        }
    }

    @Test
    public void testDeleteCollectionEmptyValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);

        Collection col = new Collection(ID1);
        col.setOwner(user);
        when(collectionRepo.get(eq(ID1))).thenReturn(col);
        when(collectionRepo.delete(eq(ID1))).thenReturn(true);

        service.delete(ID1);

        verify(collectionItemRepo, times(0)).delete(any());
        verify(shareRepo, times(0)).delete(any());
    }

    @Test
    public void testDeleteCollectionWithItemValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);

        Collection col = new Collection(ID1);
        col.setOwner(user);
        col.addItem(new Collection.CollectionItemReference(ID2));

        when(collectionRepo.get(eq(ID1))).thenReturn(col);
        when(collectionRepo.delete(eq(ID1))).thenReturn(true);

        when(collectionItemRepo.delete(eq(ID2))).thenReturn(true);

        service.delete(ID1);

        verify(shareRepo, times(0)).delete(any());
    }

    @Test
    public void testDeleteCollectionWithShareValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);

        Collection col = new Collection(ID1);
        col.setOwner(user);
        when(collectionRepo.get(eq(ID1))).thenReturn(col);
        when(collectionRepo.delete(eq(ID1))).thenReturn(true);

        Share s = new Share(ID2);
        col.addShare(s);
        when(shareRepo.delete(ID2)).thenReturn(true);

        service.delete(ID1);

        verify(collectionItemRepo, times(0)).delete(any());
    }

    @Test(expected = ApplicationException.class)
    public void testDeleteCollectionEmptyErrorOnRepo() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);

        Collection col = new Collection(ID1);
        col.setOwner(user);
        col.addItem(new Collection.CollectionItemReference(ID2));

        when(collectionRepo.get(eq(ID1))).thenReturn(col);
        when(collectionRepo.delete(eq(ID1))).thenReturn(false);

        try {
            service.delete(ID1);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 500, ex.getStatus());
            throw ex;
        } finally {

        }
    }

    @Test
    public void testUpdateCollectionValid() throws ApplicationException, IOException {
        Collection col = mock(Collection.class);
        when(col.merge(any())).thenAnswer(TestHelper.reflect(0));

        when(collectionRepo.get(eq(ID1))).thenReturn(col);
        when(collectionRepo.update(eq(ID1), any())).thenAnswer(TestHelper.reflect(1));

        Collection update = new Collection(ID1);

        service.update(ID1, update);
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateCollectionErrorMerge() throws ApplicationException, IOException {
        Collection col = mock(Collection.class);
        when(col.merge(any())).thenThrow(new IOException());

        when(collectionRepo.get(eq(ID1))).thenReturn(col);

        Collection update = new Collection(ID1);

        try {
            service.update(ID1, update);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 500, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateCollectionInvalidNoBody() throws ApplicationException {
        try {
            Collection actual = service.update(ID1, null);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testGetCollectionInvalidId() throws ApplicationException {
        try {
            Collection actual = service.get(ID1);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 404, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(1)).get(any());
        }
    }

    @Test
    public void testAddCItemValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        Collection col = spy(new Collection(ID2));
        when(collectionRepo.get(ID2)).thenReturn(col);
        when(collectionRepo.update(eq(ID2), any())).thenAnswer(TestHelper.reflect(1));

        CollectionItem ci = new Collection.CollectionItem();
        when(collectionItemRepo.save(any())).thenAnswer(TestHelper.reflectWithId(0, ID1));

        FileResourceReference fref = new FileResourceReference(ID2);
        ci.setResource(fref);
        when(fileResourceRepo.get(eq(ID2))).thenReturn(new FileResource(ID2));

        CollectionItem actual = service.addElement(ID2, ci);

        assertNotNull("comments not initialized", actual.getComments());
        assertNotNull("ratings not initialized", actual.getRatings());

        verify(col, atLeastOnce()).addItem(any(CollectionItem.class));
    }

    @Test(expected = ApplicationException.class)
    public void testAddCItemInvalidResource() throws ApplicationException {
        Collection col = spy(new Collection(ID2));
        when(collectionRepo.get(ID2)).thenReturn(col);

        CollectionItem ci = new Collection.CollectionItem();

        FileResourceReference fref = new FileResourceReference(ID2);
        ci.setResource(fref);

        try {
            CollectionItem actual = service.addElement(ID2, ci);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).save(any());
            verify(collectionRepo, times(0)).update(any(), any());
        }
    }

    @Test
    public void testDeleteCItemValid() throws ApplicationException {
        CollectionItem ci = new Collection.CollectionItem(ID1);
        when(collectionItemRepo.delete(eq(ID1))).thenReturn(true);

        Collection col = spy(new Collection(ID2));
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);
        when(collectionRepo.update(eq(ID2), any())).thenAnswer(TestHelper.reflect(1));

        service.deleteElement(ID2, ID1);

        verify(col, atLeastOnce()).removeItem(any(Collection.CollectionItemReference.class));
    }

    @Test(expected = ApplicationException.class)
    public void testDeleteCItemInvalidId() throws ApplicationException {
        Collection col = spy(new Collection(ID2));
        when(collectionRepo.get(ID2)).thenReturn(col);

        try {
            service.deleteElement(ID2, ID1);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 404, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionRepo, times(0)).update(any(), any());
            verify(collectionItemRepo, times(0)).delete(any());
        }
    }

    @Test
    public void testRateValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);
        when(collectionItemRepo.update(eq(ID1), any())).thenAnswer(TestHelper.reflect(1));

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Rating r = new Collection.Rating();
        r.setRate(3);
        service.rate(ID2, ID1, r);

        verify(ci, atLeastOnce()).addRating(any());
    }

    @Test(expected = ApplicationException.class)
    public void testRateInvalidNoBody() throws ApplicationException {
        try {
            service.rate(ID2, ID1, null);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testRateInvalidName1() throws ApplicationException {
        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Rating rate = new Collection.Rating();
        rate.setRate(3);

        StringBuilder sb = new StringBuilder();

        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // >256

        rate.setName(sb.toString());
        try {
            service.rate(ID2, ID1, rate);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testRateInvalidName2() throws ApplicationException {
        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Rating rate = new Collection.Rating();
        rate.setRate(3);

        rate.setName("abc%$&xyz");
        try {
            service.rate(ID2, ID1, rate);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test
    public void testCommentValid() throws ApplicationException {
        RegisteredUser user = new RegisteredUser(ID2);
        when(ctx.getPrincipal()).thenReturn(user);

        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);
        when(collectionItemRepo.update(eq(ID1), any())).thenAnswer(TestHelper.reflect(1));

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Comment comment = new Collection.Comment();
        comment.setComment("example");
        service.comment(ID2, ID1, comment);

        verify(ci, atLeastOnce()).addComment(any());
    }

    @Test(expected = ApplicationException.class)
    public void testCommentInvalidNoBody() throws ApplicationException {
        try {
            service.comment(ID2, ID1, null);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCommentInvalidName1() throws ApplicationException {
        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Comment comment = new Collection.Comment();
        comment.setComment("example");

        StringBuilder sb = new StringBuilder();

        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        sb.append("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); // >256

        comment.setName(sb.toString());
        try {
            service.comment(ID2, ID1, comment);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCommentInvalidName2() throws ApplicationException {
        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Comment comment = new Collection.Comment();
        comment.setComment("example");

        comment.setName("abc%$&xyz");
        try {
            service.comment(ID2, ID1, comment);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }

    @Test(expected = ApplicationException.class)
    public void testCommentInvalidComment() throws ApplicationException {
        CollectionItem ci = spy(new CollectionItem(ID1));
        when(collectionItemRepo.get(eq(ID1))).thenReturn(ci);

        Collection col = new Collection(ID2);
        col.addItem(ci);
        when(collectionRepo.get(ID2)).thenReturn(col);

        Collection.Comment comment = new Collection.Comment();
        comment.setName("example");
        comment.setComment(null);

        try {
            service.comment(ID2, ID1, comment);
        } catch (ApplicationException ex) {
            assertEquals("wrong http status", 400, ex.getStatus());
            throw ex;
        } finally {
            verify(collectionItemRepo, times(0)).update(any(), any());
        }
    }


    // TODO check name resolving on unregistered/registered principle
}
