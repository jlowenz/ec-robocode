package com.imaginaryday.ec.rcpatches;

import com.imaginaryday.ec.gp.Node;
import com.imaginaryday.ec.main.Member;
import net.jini.core.entry.Entry;

/**
 * @author rbowers
 *         Date: Nov 2, 2006
 *         Time: 9:59:10 PM
 */
public class GPBattleTask implements Entry {
    public Integer generation;
    public Integer battle;

    public String robot1;
    public String robot2;

    public Node moveProgram1;
    public Node turretProgram1;
    public Node radarProgram1;
    public Node shootProgram1;

    public Node moveProgram2;
    public Node turretProgram2;
    public Node radarProgram2;
    public Node shootProgram2;

    public Boolean done;

    public GPBattleTask(Member member1, Member member2) {
        robot1 = member1.getName();
        moveProgram1 = member1.getMoveProgram();
        turretProgram1 = member1.getTurretProgram();
        radarProgram1 = member1.getRadarProgram();
        shootProgram1 = member1.getShootProgram();

        robot2 = member2.getName();
        moveProgram2 = member2.getMoveProgram();
        turretProgram2 = member2.getTurretProgram();
        radarProgram2 = member2.getRadarProgram();
        shootProgram2 = member2.getShootProgram();
    }

    public GPBattleTask() {
        
    }
}
