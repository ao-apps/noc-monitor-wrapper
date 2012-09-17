/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.SingleResult;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedSingleResultListener implements SingleResultListener {

    final protected WrappedMonitor monitor;
    final private SingleResultListener wrapped;

    protected WrappedSingleResultListener(WrappedMonitor monitor, SingleResultListener wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    final public void singleResultUpdated(final SingleResult singleResult) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.singleResultUpdated(singleResult);
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
                        if(!(O instanceof SingleResultListener)) return false;

                        // Unwrap this
                        SingleResultListener thisSingleResultListener = WrappedSingleResultListener.this;
                        while(thisSingleResultListener instanceof WrappedSingleResultListener) thisSingleResultListener = ((WrappedSingleResultListener)thisSingleResultListener).wrapped;

                        // Unwrap other
                        SingleResultListener otherSingleResultListener = (SingleResultListener)O;
                        while(otherSingleResultListener instanceof WrappedSingleResultListener) otherSingleResultListener = ((WrappedSingleResultListener)otherSingleResultListener).wrapped;

                        // Check equals
                        return thisSingleResultListener.equals(otherSingleResultListener);
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
