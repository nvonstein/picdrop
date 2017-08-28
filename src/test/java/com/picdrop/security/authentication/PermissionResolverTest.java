/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication;

import com.picdrop.security.authentication.PermissionResolver;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author nvonstein
 */
@RunWith(MockitoJUnitRunner.class)
public class PermissionResolverTest {
    
    PermissionResolver solver;
    
    public PermissionResolverTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.solver = new PermissionResolver();
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void simpleMatchTestValid() {
        String req = "/level1/*/level2/*/read";
        String ac = "/level1/1234/level2/abcd/read";
        
        assertTrue(solver.resolve(req, ac));
    }
    
    @Test
    public void simpleMatchTestInvalidOp() {
        String req = "/level1/*/level2/*/read";
        String ac = "/level1/1234/level2/abcd/write";
        
        assertFalse(solver.resolve(req, ac));
    }
   
    @Test
    public void deepMatchTestValid() {
        String req = "/level1/*/level2/*/read";
        String ac = "/level1/*/read";
        
        assertTrue(solver.resolve(req, ac));
    }
    
    @Test
    public void deepMatchTestValidAllowAll() {
        String req = "/level1/*/level2/*/read";
        String ac = "*/read";
        
        assertTrue(solver.resolve(req, ac));
    }
    
    @Test
    public void deepMatchTestInvalid() {
        String req = "/level1/*/level2/*/read";
        String ac = "/level1/abc/level2/abc/level3/abc/read";
        
        assertFalse(solver.resolve(req, ac));
    }
}
