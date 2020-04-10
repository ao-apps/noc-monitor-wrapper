/*
 * Copyright 2012, 2020 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableResult;
import com.aoindustries.noc.monitor.common.TableResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableResultListener implements TableResultListener {

	final WrappedMonitor monitor;
	final private TableResultListener wrapped;

	protected WrappedTableResultListener(WrappedMonitor monitor, TableResultListener wrapped) {
		this.monitor = monitor;
		this.wrapped = wrapped;
	}

	@Override
	public void tableResultUpdated(TableResult tableResult) throws RemoteException {
		wrapped.tableResultUpdated(tableResult);
	}

	@Override
	public boolean equals(Object O) {
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

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}
}
