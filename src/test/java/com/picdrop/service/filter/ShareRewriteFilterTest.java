/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.picdrop.model.Share;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.service.implementation.ServiceTestBase;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

/**
 *
 * @author nvonstein
 */
@RunWith(MockitoJUnitRunner.class)
public class ShareRewriteFilterTest extends ServiceTestBase {

    protected ShareRewriteFilter filter;

    protected final String SHARE_ID = "590497a4cd27f4081ae79d98";
    protected final String SHARE_RES_STRING = "/shares/" + SHARE_ID;

    protected final String RES_ID = "123456a4cd37f408d6e79d90";
    protected final String REMAINING_RES_STRING = "/resources/" + RES_ID;

    protected final String FULL_URI = SHARE_RES_STRING + REMAINING_RES_STRING;

    String ID1 = "590497a4cd27f408d6e79d98";

    public ShareRewriteFilterTest() {
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        this.filter = this.inj.getInstance(ShareRewriteFilter.class);
    }

    /**
     * Tests a legal shared resource
     */
    @Test
    public void testMatchValid() {
        Matcher mtch = filter.getMatcher(FULL_URI);
        assertTrue("no match", filter.isResourceAccess(mtch));
    }

    /**
     * Missing resource string
     */
    @Test
    public void testNoMatchValid() {
        Matcher mtch = filter.getMatcher(SHARE_RES_STRING);
        assertFalse("match", filter.isResourceAccess(mtch));
    }

    /**
     * No share id, no trailing slash
     */
    @Test
    public void testNoMatchValidNoShareId1() {
        Matcher mtch = filter.getMatcher("/shares");
        assertFalse("match", filter.isResourceAccess(mtch));
    }

    /**
     * empty share id
     */
    @Test
    public void testNoMatchValidNoShareId2() {
        Matcher mtch = filter.getMatcher("/shares/");
        assertFalse("match", filter.isResourceAccess(mtch));
    }

    /**
     * Resouce strings must contain two compnents '/{type}/{id}'
     */
    @Test
    public void testNoMatchValidFlatLevel() {
        Matcher mtch = filter.getMatcher(SHARE_RES_STRING + "/first");
        assertFalse("match", filter.isResourceAccess(mtch));
    }

    /**
     * Checking legal share resource string matching
     */
    @Test
    public void testGetShareResourceStringValid() {
        Matcher mtch = filter.getMatcher(FULL_URI);

        filter.isResourceAccess(mtch);

        assertEquals("share resource string differs", SHARE_RES_STRING, filter.getShareResourceString(mtch));
    }

    /**
     * Checks matching of root level resource string on legal uri
     */
    @Test
    public void testRemainingResourceStringValid() {
        Matcher mtch = filter.getMatcher(FULL_URI);

        filter.isResourceAccess(mtch);

        assertEquals("resource string differs", REMAINING_RES_STRING, filter.getRootResourceString(mtch));
    }

    /**
     * Checks that only root level resource string is matched and no trailing
     * components
     */
    @Test
    public void testRemainingResourceStringValidDeepString() {
        Matcher mtch = filter.getMatcher(FULL_URI + "/something/very/deep/nested");

        filter.isResourceAccess(mtch);

        assertEquals("resource string differs", REMAINING_RES_STRING, filter.getRootResourceString(mtch));
    }

    /**
     * Tests if all request context information are properly set downstream,
     * namely principle and read permissions on shared resource
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testFilterValidMain() throws IOException, URISyntaxException {
        RegisteredUser user = new RegisteredUser(ID1);

        ResourceReference resRef = mock(ResourceReference.class);
        when(resRef.toResourceString()).thenReturn(REMAINING_RES_STRING);

        Share s = new Share(SHARE_ID);
        s.setOwner(user);
        s.setResource(resRef);
        when(this.shareRepo.get(eq(SHARE_ID), any())).thenReturn(s);

        URI uri = new URI(FULL_URI);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(uri);

        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);

        filter.filter(reqCtx);

        verify(ctx, times(1)).addPermission(eq(REMAINING_RES_STRING + "/*/read"));
        verify(ctx, times(1)).setPrincipal(eq(user));
        verify(ctx, times(0)).setUser(any());
    }

    /**
     * tests specifically if commenting permission is add on request context
     * 
     * @throws IOException
     * @throws URISyntaxException 
     */
    @Test
    public void testFilterValidAllowingComments() throws IOException, URISyntaxException {
        RegisteredUser user = new RegisteredUser(ID1);

        ResourceReference resRef = mock(ResourceReference.class);
        when(resRef.toResourceString()).thenReturn(REMAINING_RES_STRING);

        Share s = new Share(SHARE_ID);
        s.setOwner(user);
        s.setResource(resRef);
        s.setAllowComment(true);
        when(this.shareRepo.get(eq(SHARE_ID), any())).thenReturn(s);

        URI uri = new URI(FULL_URI);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(uri);

        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);

        filter.filter(reqCtx);

        verify(ctx, times(1)).addPermission(eq(REMAINING_RES_STRING + "/*/comment"));
        verify(ctx, times(0)).addPermission(eq(REMAINING_RES_STRING + "/*/rate"));
    }

    /**
     * tests specifically if rating permission is add on request context
     * 
     * @throws IOException
     * @throws URISyntaxException 
     */
    @Test
    public void testFilterValidAllowingRates() throws IOException, URISyntaxException {
        RegisteredUser user = new RegisteredUser(ID1);

        ResourceReference resRef = mock(ResourceReference.class);
        when(resRef.toResourceString()).thenReturn(REMAINING_RES_STRING);

        Share s = new Share(SHARE_ID);
        s.setOwner(user);
        s.setResource(resRef);
        s.setAllowRating(true);
        when(this.shareRepo.get(eq(SHARE_ID), any())).thenReturn(s);

        URI uri = new URI(FULL_URI);

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUri()).thenReturn(uri);

        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);

        filter.filter(reqCtx);

        verify(ctx, times(1)).addPermission(eq(REMAINING_RES_STRING + "/*/rate"));
        verify(ctx, times(0)).addPermission(eq(REMAINING_RES_STRING + "/*/comment"));
    }
}
