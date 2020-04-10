/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableMultiResultListener<R extends TableMultiResult> implements TableMultiResultListener<R> {

    final WrappedMonitor monitor;
    final private TableMultiResultListener<R> wrapped;

    protected WrappedTableMultiResultListener(WrappedMonitor monitor, TableMultiResultListener<R> wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    public void tableMultiResultAdded(R multiTableResult) throws RemoteException {
        wrapped.tableMultiResultAdded(multiTableResult);
    }

    @Override
    public void tableMultiResultRemoved(R multiTableResult) throws RemoteException {
        wrapped.tableMultiResultRemoved(multiTableResult);
    }

    @Override
    public boolean equals(Object O) {
        if(O==null) return false;
        if(!(O instanceof TableMultiResultListener<?>)) return false;

        // Unwrap this
        TableMultiResultListener<?> thisTableMultiResultListener = WrappedTableMultiResultListener.this;
        while(thisTableMultiResultListener instanceof WrappedTableMultiResultListener<?>) thisTableMultiResultListener = ((WrappedTableMultiResultListener<?>)thisTableMultiResultListener).wrapped;

        // Unwrap other
        TableMultiResultListener<?> otherTableMultiResultListener = (TableMultiResultListener<?>)O;
        while(otherTableMultiResultListener instanceof WrappedTableMultiResultListener<?>) otherTableMultiResultListener = ((WrappedTableMultiResultListener<?>)otherTableMultiResultListener).wrapped;

        // Check equals
        return thisTableMultiResultListener.equals(otherTableMultiResultListener);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
