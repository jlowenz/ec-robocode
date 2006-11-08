package com.imaginaryday.ec.rcpatches;

import robocode.repository.RobotSpecification;

/**
 * @author rbowers
 *         Date: Nov 2, 2006
 *         Time: 9:02:35 PM
 */
public class GPRobotSpecification extends RobotSpecification {

    private String name;
    private String robotDescription = "GPRobot";
    private String robotAuthorName = "GPRobocode!";
    private String robotAuthorEmail = "jlowens@gmail.com";
    private String authorWebsite = "code.google.com/p/ec-robocode";
    private boolean robotJavaSourceIncluded = false;
    private String robotVersion = "None";
    private String robotClassPath = "/";
    private String uid;

    public GPRobotSpecification(String name, String uid) {
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRobotAuthorEmail() {
        return robotAuthorEmail;
    }

    public void setRobotAuthorEmail(String robotAuthorEmail) {
        this.robotAuthorEmail = robotAuthorEmail;
    }

    public String getRobotAuthorName() {
        return robotAuthorName;
    }

    public void setRobotAuthorName(String robotAuthorName) {
        this.robotAuthorName = robotAuthorName;
    }

    public String getRobotClassPath() {
        return robotClassPath;
    }

    public void setRobotClassPath(String robotClassPath) {
        this.robotClassPath = robotClassPath;
    }

    public String getRobotDescription() {
        return robotDescription;
    }

    public void setRobotDescription(String robotDescription) {
        this.robotDescription = robotDescription;
    }

    public boolean isRobotJavaSourceIncluded() {
        return robotJavaSourceIncluded;
    }

    public void setRobotJavaSourceIncluded(boolean robotJavaSourceIncluded) {
        this.robotJavaSourceIncluded = robotJavaSourceIncluded;
    }

    public String getRobotVersion() {
        return robotVersion;
    }

    public void setRobotVersion(String robotVersion) {
        this.robotVersion = robotVersion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
