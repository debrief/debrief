/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

public class CDateTimeBuilder {

	public static CDateTimeBuilder getCompact() {
		final CDateTimeBuilder builder = new CDateTimeBuilder();
		builder.setHeader(Header.MonthPrev(), Header.DateNow(), Header.MonthNext(),
				Header.Month().align(SWT.RIGHT, SWT.FILL, true), Header.Year(), Header.Time());
		builder.setBody(Body.Days().spacedAt(1).compact(), Body.Months(), Body.Years(), Body.Time());
		return builder;
	}

	public static CDateTimeBuilder getStandard() {
		final CDateTimeBuilder builder = new CDateTimeBuilder();
		builder.setHeader(Header.MonthPrev(), Header.MonthNext(), Header.Month().align(SWT.LEFT, SWT.FILL, true),
				Header.Year().align(SWT.RIGHT, SWT.FILL, false), Header.YearPrev(), Header.YearNext());
		builder.setBody(Body.Days().spacedAt(1), Body.Months(), Body.Years(), Body.Time().newColumn());
		builder.setFooter(Footer.VerboseToday());
		return builder;
	}

	private Header[] headers = new Header[0];
	private Body[] bodies = new Body[0];
	private Footer[] footers = new Footer[0];

	private List<Header> activeHeaders = new ArrayList<Header>();
	private List<Body> activeBodies = new ArrayList<Body>();
	private List<Footer> activeFooters = new ArrayList<Footer>();

	private int headerAlignment;
	private boolean headerEqualColumns;

	private int footerAlignment;
	private boolean footerEqualColumns;

	public List<Body> getBodies() {
		return activeBodies;
	}

	public int getFooterAlignment() {
		return footerAlignment;
	}

	public boolean getFooterEqualColumns() {
		return footerEqualColumns;
	}

	public List<Footer> getFooters() {
		return activeFooters;
	}

	public int getHeaderAlignment() {
		return headerAlignment;
	}

	public boolean getHeaderEqualColumns() {
		return headerEqualColumns;
	}

	public List<Header> getHeaders() {
		return activeHeaders;
	}

	public boolean hasBody() {
		return !activeBodies.isEmpty();
	}

	public boolean hasBody(final int type) {
		for (final Body body : activeBodies) {
			if (body.type == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFooter() {
		return !activeFooters.isEmpty();
	}

	public boolean hasFooter(final int type) {
		for (final Footer footer : activeFooters) {
			if (footer.type == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasHeader() {
		return !activeHeaders.isEmpty();
	}

	public boolean hasHeader(final int type) {
		for (final Header header : activeHeaders) {
			if (header.type == type) {
				return true;
			}
		}
		return false;
	}

	public void setBody(final Body... attrs) {
		this.bodies = attrs;
	}

	public void setFields(final int[] calendarFields) {
		activeHeaders.clear();
		activeHeaders = new ArrayList<Header>();
		activeBodies.clear();
		activeBodies = new ArrayList<Body>();
		activeFooters.clear();
		activeFooters = new ArrayList<Footer>();

		boolean found = false;
		for (final Body a : bodies) {
			found = false;
			for (final int cf : calendarFields) {
				for (final int f : a.fields) {
					if (f == cf) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
			}
			if (found) {
				activeBodies.add(a);
			}
		}
		if (activeBodies.size() > 1 || activeBodies.get(0).type == Body.YEARS) {
			for (final Header a : headers) {
				if (activeBodies.size() > 1 || a.type == Header.YEAR_NEXT || a.type == Header.YEAR_PREV) {
					found = false;
					for (final int cf : calendarFields) {
						for (final int f : a.fields) {
							if (f == cf) {
								found = true;
								break;
							}
						}
						if (found) {
							break;
						}
					}
					if (found) {
						activeHeaders.add(a);
					}
				}
			}
		}
		if (activeBodies.size() > 1) {
			for (final Footer a : footers) {
				found = false;
				for (final int cf : calendarFields) {
					for (final int f : a.fields) {
						if (f == cf) {
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
				if (found) {
					activeFooters.add(a);
				}
			}
		}
	}

	public void setFooter(final Footer... attrs) {
		setFooter(SWT.FILL, true, attrs);
	}

	public void setFooter(final int alignment, final boolean equalColumns, final Footer... attrs) {
		footerAlignment = alignment;
		footerEqualColumns = equalColumns;
		this.footers = attrs;
	}

	public void setHeader(final Header... attrs) {
		setHeader(SWT.FILL, false, attrs);
	}

	public void setHeader(final int alignment, final boolean equalColumns, final Header... attrs) {
		headerAlignment = alignment;
		headerEqualColumns = equalColumns;
		this.headers = attrs;
	}

}
