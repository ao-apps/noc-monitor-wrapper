/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.SingleResult;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedSingleResultListener implements SingleResultListener {

    final WrappedMonitor monitor;
    final private SingleResultListener wrapped;

    protected WrappedSingleResultListener(WrappedMonitor monitor, SingleResultListener wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    public void singleResultUpdated(SingleResult singleResult) throws RemoteException {
        wrapped.singleResultUpdated(singleResult);
    }

    @Override
    public boolean equals(Object O) {
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

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
