package com.imaginaryday.ec.ecj.functions;

import ec.ECJAgent;
import ec.gp.GPNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 11:32:10 AM<br>
 * </b>
 */
public abstract class GPRoboNode extends GPNode implements GPRoboForm {
    private ECJAgent agent;
    private static final long serialVersionUID = -6219465243404871867L;

    public void setAgent(ECJAgent agent) {
        this.agent = agent;
        for (GPNode aChildren : children) {
            ((GPRoboForm) aChildren).setAgent(agent);
        }
    }

    public ECJAgent getAgent() {
        return agent;
    }

}
