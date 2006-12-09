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
 *     - Added setEnableGUI() and isGUIEnabled()
 *     - Code cleanup
 *******************************************************************************/
package com.imaginaryday.ec.rcpatches;


import robocode.control.RobocodeListener;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class ECJRobocodeManager extends RobocodeManager {

    private BattleManager ecjBattleManager;

    public ECJRobocodeManager(boolean slave, RobocodeListener listener) {
        super(slave, listener);
        setEnableGUI(false);
        setEnableSound(false);
    }

    /**
     * Gets the battleManager.
     *
     * @return Returns a BattleManager
     */
    public BattleManager getBattleManager() {
        if (this.ecjBattleManager == null) {
            ecjBattleManager = new ECJBattleManager(this);
        }
        return ecjBattleManager;
    }

}
