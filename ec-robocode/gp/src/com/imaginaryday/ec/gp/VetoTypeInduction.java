package com.imaginaryday.ec.gp;

/**
 * <b>
 * User: jlowens<br>
 * Date: Oct 31, 2006<br>
 * Time: 9:13:00 PM<br>
 * </b>
 */
public class VetoTypeInduction extends RuntimeException {

    public VetoTypeInduction() {
    }

    public VetoTypeInduction(String string) {
        super(string);
    }

    public VetoTypeInduction(String string, Throwable throwable) {
        super(string, throwable);
    }

    public VetoTypeInduction(Throwable throwable) {
        super(throwable);
    }
}
