/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 *
 * @author nvonstein
 */
public abstract class LogHelper {
    public static final Marker REPO = MarkerManager.getMarker("REPO");
    public static final Marker REPO_UPDATE = MarkerManager.getMarker("REPO_UPDATE").addParents(REPO);
    public static final Marker REPO_GET = MarkerManager.getMarker("REPO_GET").addParents(REPO);
    public static final Marker REPO_DELETE = MarkerManager.getMarker("REPO_DELETE").addParents(REPO);
    public static final Marker REPO_SAVE = MarkerManager.getMarker("REPO_SAVE").addParents(REPO);
    
    public static final Marker SERVICE = MarkerManager.getMarker("SERVICE");
    
    public static final Marker FILTER = MarkerManager.getMarker("FILTER");
}
