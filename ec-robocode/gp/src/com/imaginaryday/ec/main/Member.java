package com.imaginaryday.ec.main;

import com.imaginaryday.ec.gp.Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: rbowers
 * Date: Nov 6, 2006
 * Time: 1:56:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = 1481195345545820852L;
    private static AtomicInteger ID = new AtomicInteger(0);

    private int generation;
    private int id;
    private String name;

    private Node moveProgram;
    private Node turretProgram;
    private Node radarProgram;
    private Node shootProgram;

    double cumulativeFitness = 0.0;
    int numBattles = 0;

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (ID.get() < id) ID.set(id+1);
	}

    public Member(int generation, int id) {
        this.generation = generation;
        this.id = ID.getAndIncrement();
    }

    public Member(Member old) {
        this.generation = old.generation;
        this.id = ID.getAndIncrement();
        this.name = null;
        getName();
        this.moveProgram = old.moveProgram;
        this.turretProgram = old.turretProgram;
        this.radarProgram = old.radarProgram;
        this.shootProgram = old.shootProgram;

        this.numBattles = 0;
        this.cumulativeFitness = 0.0;
    }

    public int compareTo(Member member) {
        return Double.valueOf(getFitness()).compareTo(member.getFitness());
    }

    public void addFitness(double fitness) {
        cumulativeFitness += fitness;
        ++numBattles;
    }

    public double getFitness() {
        return cumulativeFitness / numBattles;
    }

    public int getGeneration() {
        return generation;
    }

	public void incrementGeneration() {
		this.generation++;
		name = null;
	}
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        name = null;
    }

    public String getName() {
        if (name == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("G").append(generation);
            sb.append("R").append(id);
            this.name = sb.toString();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getMoveProgram() {
        return moveProgram;
    }

    public void setMoveProgram(Node moveProgram) {
        this.moveProgram = moveProgram;
    }

    public Node getTurretProgram() {
        return turretProgram;
    }

    public void setTurretProgram(Node turretProgram) {
        this.turretProgram = turretProgram;
    }

    public Node getRadarProgram() {
        return radarProgram;
    }

    public void setRadarProgram(Node radarProgram) {
        this.radarProgram = radarProgram;
    }

    public Node getShootProgram() {
        return shootProgram;
    }

    public void setShootProgram(Node shootProgram) {
        this.shootProgram = shootProgram;
    }

    public double getCumulativeFitness() {
        return cumulativeFitness;
    }

    public int getNumBattles() {
        return numBattles;
    }


    public void setGeneration(int i) {
        this.generation = i;
        this.name = null;
    }
}
