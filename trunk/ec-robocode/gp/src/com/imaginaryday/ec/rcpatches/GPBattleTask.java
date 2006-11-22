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

    public Boolean done = Boolean.FALSE;

    public GPBattleTask(int generation, int battle, Member member1, Member member2) {
        this.generation = generation;
        this.battle = battle;

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

    public GPBattleTask(int generation, int battle, Member member1, String standardBotName) {
        this.generation = generation;
        this.battle = battle;

        robot1 = member1.getName();
        moveProgram1 = member1.getMoveProgram();
        turretProgram1 = member1.getTurretProgram();
        radarProgram1 = member1.getRadarProgram();
        shootProgram1 = member1.getShootProgram();

        robot2 = standardBotName;
        moveProgram2 = null;
        turretProgram2 = null;
        radarProgram2 = null;
        shootProgram2 = null;
    }

    public String shortString() {

        return new StringBuilder().append("BattleTask[").append(generation).append(" | ")
                .append(battle).append("] ").append(robot1).append(" vs ")
                .append(robot2).toString();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("GPBattleTask{").append("battle=").append(battle)
                .append(", generation=").append(generation).append(", robot1='").append(robot1)
                .append('\'').append(", robot2='").append(robot2).append('\'').append(", moveProgram1=")
                .append(moveProgram1).append(", turretProgram1=").append(turretProgram1).append(", radarProgram1=")
                .append(radarProgram1).append(", shootProgram1=").append(shootProgram1).append(", moveProgram2=")
                .append(moveProgram2).append(", turretProgram2=").append(turretProgram2).append(", radarProgram2=")
                .append(radarProgram2).append(", shootProgram2=").append(shootProgram2).append(", done=")
                .append(done).append('}').toString();
    }
}
