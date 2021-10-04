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

import com.aoindustries.noc.monitor.common.SingleResult;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedSingleResultListener implements SingleResultListener {

	final WrappedMonitor monitor;
	private final SingleResultListener wrapped;

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
