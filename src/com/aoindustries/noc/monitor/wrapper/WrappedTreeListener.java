/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.AlertLevelChange;
import com.aoindustries.noc.monitor.common.TreeListener;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTreeListener implements TreeListener {

    final protected WrappedMonitor monitor;
    final private TreeListener wrapped;

    protected WrappedTreeListener(WrappedMonitor monitor, TreeListener wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    final public void nodeAdded() throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.nodeAdded();
                    return null;
                }
            }
        );
    }

    @Override
    final public void nodeRemoved() throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.nodeRemoved();
                    return null;
                }
            }
        );
    }

    @Override
    final public void nodeAlertLevelChanged(final List<AlertLevelChange> changes) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.nodeAlertLevelChanged(changes);
                    return null;
                }
            }
        );
    }

    @Override
    final public boolean equals(final Object O) {
        try {
            return monitor.call(
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws RemoteException {
                        if(O==null) return false;
                        if(!(O instanceof TreeListener)) return false;

                        // Unwrap this
                        TreeListener thisTreeListener = WrappedTreeListener.this;
                        while(thisTreeListener instanceof WrappedTreeListener) thisTreeListener = ((WrappedTreeListener)thisTreeListener).wrapped;

                        // Unwrap other
                        TreeListener otherTreeListener = (TreeListener)O;
                        while(otherTreeListener instanceof WrappedTreeListener) otherTreeListener = ((WrappedTreeListener)otherTreeListener).wrapped;

                        // Check equals
                        return thisTreeListener.equals(otherTreeListener);
                    }
                }
            );
        } catch(RemoteException e) {
            throw new WrappedException(e);
        }
    }

    @Override
    final public int hashCode() {
        try {
            return monitor.call(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws RemoteException {
                        return wrapped.hashCode();
                    }
                }
            );
        } catch(RemoteException e) {
            throw new WrappedException(e);
        }
    }
}
