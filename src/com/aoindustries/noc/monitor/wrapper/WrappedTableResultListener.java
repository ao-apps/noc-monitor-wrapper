/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableResult;
import com.aoindustries.noc.monitor.common.TableResultListener;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableResultListener implements TableResultListener {

    final protected WrappedMonitor monitor;
    final private TableResultListener wrapped;

    protected WrappedTableResultListener(WrappedMonitor monitor, TableResultListener wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    final public void tableResultUpdated(final TableResult tableResult) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.tableResultUpdated(tableResult);
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
                        if(!(O instanceof TableResultListener)) return false;

                        // Unwrap this
                        TableResultListener thisTableResultListener = WrappedTableResultListener.this;
                        while(thisTableResultListener instanceof WrappedTableResultListener) thisTableResultListener = ((WrappedTableResultListener)thisTableResultListener).wrapped;

                        // Unwrap other
                        TableResultListener otherTableResultListener = (TableResultListener)O;
                        while(otherTableResultListener instanceof WrappedTableResultListener) otherTableResultListener = ((WrappedTableResultListener)otherTableResultListener).wrapped;

                        // Check equals
                        return thisTableResultListener.equals(otherTableResultListener);
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
