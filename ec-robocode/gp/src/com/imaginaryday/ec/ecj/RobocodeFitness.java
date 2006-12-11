package com.imaginaryday.ec.ecj;

import com.imaginaryday.util.Stuff;
import ec.EvolutionState;
import ec.Fitness;
import ec.util.Parameter;
import robocode.control.RobotResults;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 10:13:06 PM<br>
 * </b>
 */
public class RobocodeFitness extends Fitness {

    private double cumulativeFitness = 0.0;
    private int numBattles = 0;
    private static final long serialVersionUID = 1246191225394751489L;

    public void addResults(int gen, RobotResults robot, RobotResults opponent) {
        cumulativeFitness += robot.getBulletDamage();
        numBattles++;
    }

    public float fitness() {
        return (float)(cumulativeFitness / (double)numBattles);
    }
    public boolean isIdealFitness() {
        return false;
    }
    public boolean equivalentTo(Fitness _fitness) {
        return Stuff.close(fitness(), _fitness.fitness());
    }
    public boolean betterThan(Fitness _fitness) {
        return fitness() > _fitness.fitness();
    }

    public Parameter defaultBase() {
        return new Parameter("rfit");
    }

    @Override
    public void writeFitness(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(cumulativeFitness);
        dataOutput.writeInt(numBattles);
    }

    @Override
    public void readFitness(final EvolutionState state, final DataInput dataInput) throws IOException {
        cumulativeFitness = dataInput.readDouble();
        numBattles = dataInput.readInt();
    }
}
