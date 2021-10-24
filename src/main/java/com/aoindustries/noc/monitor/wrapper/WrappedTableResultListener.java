/*
 * noc-monitor-wrapper - Base support for wrappers of Monitoring API.
 * Copyright (C) 2012, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of noc-monitor-wrapper.
 *
 * noc-monitor-wrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * noc-monitor-wrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with noc-monitor-wrapper.  If not, see <http://www.gnu.org/licenses/>.
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
	private final TableResultListener wrapped;

	protected WrappedTableResultListener(WrappedMonitor monitor, TableResultListener wrapped) {
		this.monitor = monitor;
		this.wrapped = wrapped;
	}

	@Override
	public void tableResultUpdated(TableResult tableResult) throws RemoteException {
		wrapped.tableResultUpdated(tableResult);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TableResultListener)) return false;

		// Unwrap this
		TableResultListener thisTableResultListener = WrappedTableResultListener.this;
		while(thisTableResultListener instanceof WrappedTableResultListener) thisTableResultListener = ((WrappedTableResultListener)thisTableResultListener).wrapped;

		// Unwrap other
		TableResultListener otherTableResultListener = (TableResultListener)obj;
		while(otherTableResultListener instanceof WrappedTableResultListener) otherTableResultListener = ((WrappedTableResultListener)otherTableResultListener).wrapped;

		// Check equals
		return thisTableResultListener.equals(otherTableResultListener);
	}

	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}
}
