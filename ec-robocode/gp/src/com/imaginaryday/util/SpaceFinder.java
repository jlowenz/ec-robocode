package com.imaginaryday.util;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.space.JavaSpace;
import org.jini.rio.resources.client.JiniClient;

import java.rmi.RemoteException;

/**
 * @author rbowers
 *         Date: Nov 11, 2006
 *         Time: 10:02:01 PM
 */
public class SpaceFinder extends JiniClient {

    public SpaceFinder() throws Exception {
        super();
    }

    public JavaSpace getSpace() {
        long startTime = System.currentTimeMillis();
        ServiceTemplate template = new ServiceTemplate(null, new Class[]{JavaSpace.class}, null);
        while (System.currentTimeMillis() - startTime < 60000) {
            for (ServiceRegistrar r : this.getRegistrars()) {
                JavaSpace js = null;
                try {
                    js = (JavaSpace) r.lookup(template);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (js != null) return js;
            }
        }
        return null;
    }
}

