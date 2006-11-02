/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5.0
 *******************************************************************************/
package robocode.peer.robot;


import robocode.Robot;
import robocode.manager.NameManager;
import robocode.peer.TeamPeer;
import robocode.repository.RobotSpecification;
import robocode.security.RobocodeClassLoader;
import robocode.util.Utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobotClassManager {
    private RobotSpecification robotSpecification;
    private Class robotClass;
    private Hashtable<String, String> referencedClasses = new Hashtable<String, String>();
    private RobocodeClassLoader robotClassLoader = null;
    // only used if we're being controlled by RobocodeEngine:
    private robocode.control.RobotSpecification controlRobotSpecification;

    private String fullClassName;
    private TeamPeer teamManager;

    private String uid = "";

    public RobotClassManager() {
        this(null, null);
    }

    /**
     * RobotClassHandler constructor
     */
    public RobotClassManager(RobotSpecification robotSpecification) {
        this(robotSpecification, null);
    }

    public RobotClassManager(RobotSpecification robotSpecification, TeamPeer teamManager) {
        this.robotSpecification = robotSpecification;
        this.fullClassName = robotSpecification.getName();
        this.teamManager = teamManager;
    }

    public String getRootPackage() {
        return getClassNameManager().getRootPackage();
    }

    public NameManager getClassNameManager() {
        return robotSpecification.getNameManager();
    }

    public void addReferencedClasses(Vector<String> v) {
        if (v == null) {
            return;
        }
        for (int i = 0; i < v.size(); i++) {
            String className = v.elementAt(i).replace('/', '.');

            if (getRootPackage() == null || (className.indexOf("java") != 0 && className.indexOf("robocode") != 0)) {
                if (getRootPackage() == null && !className.equals(fullClassName)) {
                    continue;
                }
                if (!referencedClasses.containsKey(className)) {
                    referencedClasses.put(className, "false");
                }
            }
        }
    }

    public void addResolvedClass(String className) {
        if (!referencedClasses.containsKey(className)) {
            Utils.log(fullClassName + ": Cannot set " + className + " to resolved, did not know it was referenced.");
            return;
        }
        referencedClasses.put(className, "true");
    }

    public String getFullClassName() {
        // Better not be null...
        return fullClassName;
    }

    public Enumeration getReferencedClasses() {
        return referencedClasses.keys();
    }

    public Class getRobotClass() {
        return robotClass;
    }

    public RobocodeClassLoader getRobotClassLoader() {
        if (robotClassLoader == null) {
            robotClassLoader = new RobocodeClassLoader(getClass().getClassLoader(), this);
        }
        return robotClassLoader;
    }

    public RobotSpecification getRobotSpecification() {
        return robotSpecification;
    }

    public void loadUnresolvedClasses() throws ClassNotFoundException {
        Enumeration keys = referencedClasses.keys();

        while (keys.hasMoreElements()) {
            String s = (String) keys.nextElement();

            if (referencedClasses.get(s).equals("false")) {
                // resolve, then rebuild keys...
                robotClassLoader.loadRobotClass(s, false);
                keys = referencedClasses.keys();
            }
        }
    }

    public void setRobotClass(Class newRobotClass) {
        robotClass = newRobotClass;
    }

    public String toString() {
        return getRobotSpecification().getNameManager().getUniqueFullClassNameWithVersion();
    }

    /**
     * Gets the robotSpecification.
     *
     * @return Returns a RobotSpecification
     */
    public robocode.control.RobotSpecification getControlRobotSpecification() {
        return controlRobotSpecification;
    }

    /**
     * Sets the robotSpecification.
     *
     * @param robotSpecification The robotSpecification to set
     */
    public void setControlRobotSpecification(robocode.control.RobotSpecification controlRobotSpecification) {
        this.controlRobotSpecification = controlRobotSpecification;
    }

    /**
     * Gets the teamManager.
     *
     * @return Returns a TeamManager
     */
    public TeamPeer getTeamManager() {
        return teamManager;
    }

    /**
     * Gets the uid.
     *
     * @return Returns a long
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the uid.
     *
     * @param uid The uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    public Robot getRobotInstance() throws IllegalAccessException, InstantiationException {
        Robot robot = null;
        robot = (Robot) this.robotClass.newInstance();
        return robot;
    }
}
