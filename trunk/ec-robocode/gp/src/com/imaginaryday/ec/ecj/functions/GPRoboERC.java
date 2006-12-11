package com.imaginaryday.ec.ecj.functions;

import ec.ECJAgent;
import ec.gp.ERC;
import ec.gp.GPNode;

/**
 * <b>
 * User: jlowens<br>
 * Date: Dec 9, 2006<br>
 * Time: 12:36:53 PM<br>
 * </b>
 */
public abstract class GPRoboERC extends ERC implements GPRoboForm {
    private ECJAgent agent;
    private static final long serialVersionUID = 6015171590966739914L;

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
