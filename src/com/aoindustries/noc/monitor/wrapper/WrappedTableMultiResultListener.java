/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableMultiResultListener<R extends TableMultiResult> implements TableMultiResultListener<R> {

    final protected WrappedMonitor monitor;
    final private TableMultiResultListener<R> wrapped;

    protected WrappedTableMultiResultListener(WrappedMonitor monitor, TableMultiResultListener<R> wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    final public void tableMultiResultAdded(final R multiTableResult) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.tableMultiResultAdded(multiTableResult);
                    return null;
                }
            }
        );
    }

    @Override
    final public void tableMultiResultRemoved(final R multiTableResult) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.tableMultiResultRemoved(multiTableResult);
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
                        if(!(O instanceof TableMultiResultListener)) return false;

                        // Unwrap this
                        TableMultiResultListener<?> thisTableMultiResultListener = WrappedTableMultiResultListener.this;
                        while(thisTableMultiResultListener instanceof WrappedTableMultiResultListener) thisTableMultiResultListener = ((WrappedTableMultiResultListener)thisTableMultiResultListener).wrapped;

                        // Unwrap other
                        TableMultiResultListener<?> otherTableMultiResultListener = (TableMultiResultListener<?>)O;
                        while(otherTableMultiResultListener instanceof WrappedTableMultiResultListener) otherTableMultiResultListener = ((WrappedTableMultiResultListener)otherTableMultiResultListener).wrapped;

                        // Check equals
                        return thisTableMultiResultListener.equals(otherTableMultiResultListener);
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
