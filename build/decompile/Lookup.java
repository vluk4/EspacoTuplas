/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.jini.core.lookup.ServiceRegistrar
 *  net.jini.core.lookup.ServiceTemplate
 *  net.jini.discovery.DiscoveryEvent
 *  net.jini.discovery.DiscoveryListener
 *  net.jini.discovery.LookupDiscovery
 */
package br.edu.ifce.space;

import java.io.IOException;
import java.rmi.RemoteException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;

class Lookup
implements DiscoveryListener {
    private ServiceTemplate theTemplate;
    private LookupDiscovery theDiscoverer;
    private Object theProxy;

    Lookup(Class aServiceInterface) {
        Class[] myServiceTypes = new Class[]{aServiceInterface};
        this.theTemplate = new ServiceTemplate(null, myServiceTypes, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object getService() {
        Lookup lookup = this;
        synchronized (lookup) {
            if (this.theDiscoverer == null) {
                try {
                    this.theDiscoverer = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
                    this.theDiscoverer.addDiscoveryListener((DiscoveryListener)this);
                }
                catch (IOException anIOE) {
                    System.err.println("Failed to init lookup");
                    anIOE.printStackTrace(System.err);
                }
            }
        }
        return this.waitForProxy();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void terminate() {
        Lookup lookup = this;
        synchronized (lookup) {
            if (this.theDiscoverer != null) {
                this.theDiscoverer.terminate();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object waitForProxy() {
        Lookup lookup = this;
        synchronized (lookup) {
            while (this.theProxy == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
            return this.theProxy;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void signalGotProxy(Object aProxy) {
        Lookup lookup = this;
        synchronized (lookup) {
            if (this.theProxy == null) {
                this.theProxy = aProxy;
                this.notify();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void discovered(DiscoveryEvent anEvent) {
        Lookup lookup = this;
        synchronized (lookup) {
            if (this.theProxy != null) {
                return;
            }
        }
        ServiceRegistrar[] myRegs = anEvent.getRegistrars();
        for (int i = 0; i < myRegs.length; ++i) {
            ServiceRegistrar myReg = myRegs[i];
            Object myProxy = null;
            try {
                myProxy = myReg.lookup(this.theTemplate);
                if (myProxy == null) continue;
                this.signalGotProxy(myProxy);
                break;
            }
            catch (RemoteException anRE) {
                System.err.println("ServiceRegistrar barfed");
                anRE.printStackTrace(System.err);
            }
        }
    }

    public void discarded(DiscoveryEvent anEvent) {
    }
}
