/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.cwt.svg;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.cwt.svg.SvgPaint.PaintType;
import org.eclipse.nebula.cwt.svg.SvgTransform.Type;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

class SvgLoader {

	private static final char[] ATTR_CLASS = new char[] { 'c', 'l', 'a', 's', 's' };
	private static final char[] ATTR_CX = new char[] { 'c', 'x' };
	private static final char[] ATTR_CY = new char[] { 'c', 'y' };
	private static final char[] ATTR_D = new char[] { 'd' };
	private static final char[] ATTR_FILL = new char[] { 'f', 'i', 'l', 'l' };
	private static final char[] ATTR_FILL_OPACITY = new char[] { 'f', 'i', 'l', 'l', '-', 'o', 'p', 'a', 'c', 'i', 't',
			'y' };
	private static final char[] ATTR_FILL_RULE = new char[] { 'f', 'i', 'l', 'l', '-', 'r', 'u', 'l', 'e' };
	private static final char[] ATTR_FX = new char[] { 'f', 'x' };
	private static final char[] ATTR_FY = new char[] { 'f', 'y' };
	private static final char[] ATTR_GRADIENT_TRANSFORM = new char[] { 'g', 'r', 'a', 'd', 'i', 'e', 'n', 't', 'T', 'r',
			'a', 'n', 's', 'f', 'o', 'r', 'm' };
	private static final char[] ATTR_GRADIENT_UNITS = new char[] { 'g', 'r', 'a', 'd', 'i', 'e', 'n', 't', 'U', 'n',
			'i', 't', 's' };
	private static final char[] ATTR_HEIGHT = new char[] { 'h', 'e', 'i', 'g', 'h', 't' };
	private static final char[] ATTR_ID = new char[] { 'i', 'd' };
	private static final char[] ATTR_OFFSET = new char[] { 'o', 'f', 'f', 's', 'e', 't' };
	private static final char[] ATTR_POINTS = new char[] { 'p', 'o', 'i', 'n', 't', 's' };
	private static final char[] ATTR_R = new char[] { 'r' };
	private static final char[] ATTR_RX = new char[] { 'r', 'x' };
	private static final char[] ATTR_RY = new char[] { 'r', 'y' };
	private static final char[] ATTR_SPREAD_METHOD = new char[] { 's', 'p', 'r', 'e', 'a', 'd', 'M', 'e', 't', 'h', 'o',
			'd' };
	private static final char[] ATTR_STOP = new char[] { 's', 't', 'o', 'p' };
	private static final char[] ATTR_STOP_COLOR = new char[] { 's', 't', 'o', 'p', '-', 'c', 'o', 'l', 'o', 'r' };
	private static final char[] ATTR_STOP_OPACITY = new char[] { 's', 't', 'o', 'p', '-', 'o', 'p', 'a', 'c', 'i', 't',
			'y' };
	private static final char[] ATTR_STROKE = new char[] { 's', 't', 'r', 'o', 'k', 'e' };
	private static final char[] ATTR_STROKE_OPACITY = new char[] { 's', 't', 'r', 'o', 'k', 'e', '-', 'o', 'p', 'a',
			'c', 'i', 't', 'y' };
	private static final char[] ATTR_STROKE_WIDTH = new char[] { 's', 't', 'r', 'o', 'k', 'e', '-', 'w', 'i', 'd', 't',
			'h' };
	private static final char[] ATTR_STROKE_CAP = new char[] { 's', 't', 'r', 'o', 'k', 'e', '-', 'l', 'i', 'n', 'e',
			'c', 'a', 'p' };
	private static final char[] ATTR_STROKE_JOIN = new char[] { 's', 't', 'r', 'o', 'k', 'e', '-', 'l', 'i', 'n', 'e',
			'j', 'o', 'i', 'n' };
	private static final char[] ATTR_STYLE = new char[] { 's', 't', 'y', 'l', 'e' };
	private static final char[] ATTR_TRANSFORM = new char[] { 't', 'r', 'a', 'n', 's', 'f', 'o', 'r', 'm' };
	private static final char[] ATTR_VIEWBOX = new char[] { 'v', 'i', 'e', 'w', 'B', 'o', 'x' };
	private static final char[] ATTR_WIDTH = new char[] { 'w', 'i', 'd', 't', 'h' };
	private static final char[] ATTR_X = new char[] { 'x' };
	private static final char[] ATTR_X1 = new char[] { 'x', '1' };
	private static final char[] ATTR_X2 = new char[] { 'x', '2' };
	private static final char[] ATTR_XLINK_HREF = new char[] { 'x', 'l', 'i', 'n', 'k', ':', 'h', 'r', 'e', 'f' };
	private static final char[] ATTR_Y = new char[] { 'y' };
	private static final char[] ATTR_Y1 = new char[] { 'y', '1' };
	private static final char[] ATTR_Y2 = new char[] { 'y', '2' };
	private static final char[] ELEMENT_CDATA = new char[] { '!', '[', 'C', 'D', 'A', 'T', 'A', '[' };
	private static final char[] ELEMENT_CDATA_END = new char[] { ']', ']', '>' };
	private static final char[] ELEMENT_CIRCLE = new char[] { 'c', 'i', 'r', 'c', 'l', 'e' };
	private static final char[] ELEMENT_COMMENT = new char[] { '!', '-', '-' };
	private static final char[] ELEMENT_COMMENT_END = new char[] { '-', '-', '>' };
	private static final char[] ELEMENT_DESCRIPTION = new char[] { 'd', 'e', 's', 'c' };
	private static final char[] ELEMENT_DEFS = new char[] { 'd', 'e', 'f', 's' };
	private static final char[] ELEMENT_DOCTYPE = new char[] { '!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
	private static final char[] ELEMENT_ELLIPSE = new char[] { 'e', 'l', 'l', 'i', 'p', 's', 'e' };
	private static final char[] ELEMENT_GROUP = new char[] { 'g' };
	private static final char[] ELEMENT_LINEAR_GRADIENT = new char[] { 'l', 'i', 'n', 'e', 'a', 'r', 'G', 'r', 'a', 'd',
			'i', 'e', 'n', 't' };
	private static final char[] ELEMENT_LINE = new char[] { 'l', 'i', 'n', 'e' };
	private static final char[] ELEMENT_PATH = new char[] { 'p', 'a', 't', 'h' };
	private static final char[] ELEMENT_POLYGON = new char[] { 'p', 'o', 'l', 'y', 'g', 'o', 'n' };
	private static final char[] ELEMENT_POLYLINE = new char[] { 'p', 'o', 'l', 'y', 'l', 'i', 'n', 'e' };
	private static final char[] ELEMENT_RADIAL_GRADIENT = new char[] { 'r', 'a', 'd', 'i', 'a', 'l', 'G', 'r', 'a', 'd',
			'i', 'e', 'n', 't' };
	private static final char[] ELEMENT_RECT = new char[] { 'r', 'e', 'c', 't' };
	private static final char[] ELEMENT_SVG = new char[] { 's', 'v', 'g' };
	private static final char[] ELEMENT_STYLE = new char[] { 's', 't', 'y', 'l', 'e' };
	private static final char[] ELEMENT_TITLE = new char[] { 't', 'i', 't', 'l', 'e' };
	private static final char[] ELEMENT_USE = new char[] { 'u', 's', 'e' };
	private static final char[] ELEMENT_XML = new char[] { '?', 'x', 'm', 'l' };

	// private static final String paramRegex = "[^\\d^\\.^-]+"; //$NON-NLS-1$
	private static final String paramRegex = "[ ,]+"; //$NON-NLS-1$
	private static final Matcher urlMatcher = Pattern.compile(" *url\\( *#(\\w+) *\\) *").matcher(""); //$NON-NLS-1$ //$NON-NLS-2$

	private static void addArc(final String[] sa, int ix, final List<Byte> types, final List<Float> points,
			final boolean relative) {
		final float x1 = points.get(points.size() - 2);
		final float y1 = points.get(points.size() - 1);
		float rx = abs(Float.parseFloat(sa[ix++]));
		float ry = abs(Float.parseFloat(sa[ix++]));
		final float phi = clampAngle(Float.parseFloat(sa[ix++]));
		final boolean largeArc = (!sa[ix++].equals("0")); //$NON-NLS-1$
		final boolean sweep = (!sa[ix++].equals("0")); //$NON-NLS-1$
		float x2 = Float.parseFloat(sa[ix++]);
		float y2 = Float.parseFloat(sa[ix++]);
		if (relative) {
			x2 += x1;
			y2 += y1;
		}

		if (x1 == x2 && y1 == y2) {
			return;
		}
		if (rx == 0 || ry == 0) {
			types.add((byte) SWT.PATH_LINE_TO);
			points.add(x2);
			points.add(y2);
			return;
		}

		final double radPhi = toRadians(phi);

		final double x0 = (cos(radPhi) * ((x1 - x2) / 2)) + (sin(radPhi) * ((y1 - y2) / 2));
		final double y0 = (-sin(radPhi) * ((x1 - x2) / 2)) + (cos(radPhi) * ((y1 - y2) / 2));
		final double lambda = ((x0 * x0) / (rx * rx)) + ((y0 * y0) / (ry * ry));
		double radicand;
		if (lambda > 1) {
			rx *= sqrt(lambda);
			ry *= sqrt(lambda);
			radicand = 0;
		} else {
			radicand = ((rx * rx * ry * ry) - (rx * rx * y0 * y0) - (ry * ry * x0 * x0))
					/ ((rx * rx * y0 * y0) + (ry * ry * x0 * x0));
		}
		if (radicand < 0) {
			rx *= sqrt(lambda);
			ry *= sqrt(lambda);
			radicand = 0;
		}
		int sign = (largeArc != sweep) ? 1 : -1;
		final double cx0 = sign * sqrt(radicand) * rx * y0 / ry;
		final double cy0 = sign * sqrt(radicand) * -ry * x0 / rx;
		final double cx = (cos(radPhi) * cx0) - (sin(radPhi) * cy0) + ((x1 + x2) / 2);
		final double cy = (sin(radPhi) * cx0) + (cos(radPhi) * cy0) + ((y1 + y2) / 2);

		double theta1 = getAngle(1, 0, (x0 - cx0) / rx, (y0 - cy0) / ry);
		double dTheta = getAngle((x0 - cx0) / rx, (y0 - cy0) / ry, (-x0 - cx0) / rx, (-y0 - cy0) / ry);
		double theta2 = theta1 + dTheta;
		theta1 = clampAngle(theta1);
		dTheta = clampAngle(dTheta);
		theta2 = clampAngle(theta2);

		if (!sweep) {
			dTheta = 360 - dTheta;
		}

		final int increment = 5;
		final int lines = round((float) dTheta) / increment;
		double theta = theta1;
		for (int i = 0; i < lines; i++) {
			sign = (sweep) ? 1 : -1;
			theta = clampAngle(theta + (sign * increment));

			final double radTheta = toRadians(theta);
			final double x = cos(radPhi) * rx * cos(radTheta) - sin(radPhi) * ry * sin(radTheta) + cx;
			final double y = sin(radPhi) * rx * cos(radTheta) + cos(radPhi) * ry * sin(radTheta) + cy;

			types.add((byte) SWT.PATH_LINE_TO);
			if (i == lines - 1) {
				points.add(x2);
				points.add(y2);
			} else {
				points.add((float) x);
				points.add((float) y);
			}
		}
	}

	private static void addPoint(final List<Float> points, final String s, final boolean relative) {
		if (relative) {
			points.add(points.get(points.size() - 2) + Float.parseFloat(s));
		} else {
			points.add(new Float(s));
		}
	}

	private static double clampAngle(double deg) {
		if (deg < 0) {
			deg += 360;
		} else if (deg > 360) {
			deg -= 360;
		}
		return deg;
	}

	private static float clampAngle(float deg) {
		if (deg < 0) {
			deg += 360;
		} else if (deg > 360) {
			deg -= 360;
		}
		return deg;
	}

	private static int closer(final char[] ca, final int start, final int end) {
		if (start >= 0) {
			final char opener = ca[start];
			final char closer = closerChar(opener);
			int count = 1;
			for (int i = start + 1; i < ca.length && i <= end; i++) {
				if (ca[i] == opener && ca[i] != closer) {
					count++;
				} else if (ca[i] == closer) {
					if (closer != '"' || ca[i - 1] != '\\') { // check for escape char
						count--;
						if (count == 0) {
							return i;
						}
					}
				} else if (ca[i] == '"') {
					i = closer(ca, i, end); // just entered a string - get out of it
				}
			}
		}
		return -1;
	}

	private static char closerChar(final char c) {
		switch (c) {
		case '<':
			return '>';
		case '(':
			return ')';
		case '{':
			return '}';
		case '[':
			return ']';
		case '"':
			return '"';
		case '\'':
			return '\'';
		}
		return 0;
	}

	private static int findAll(final char[] ca, final int from, final int to, final char... cs) {
		for (int i = from; i >= 0 && i < ca.length && i <= to; i++) {
			if (ca[i] == cs[0]) {
				if (cs.length == 1) {
					return i;
				}
				for (int j = 1; j < cs.length && (i + j) <= to; j++) {
					if ((i + j) == ca.length) {
						return -1;
					}
					if (ca[i + j] != cs[j]) {
						break;
					}
					if (j == cs.length - 1) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	private static int findAny(final char[] ca, final int from, final int to, final char... cs) {
		for (int i = from; i >= 0 && i < ca.length && i <= to; i++) {
			for (final char c : cs) {
				if (ca[i] == c) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * find the closer for the XML tag which begins with the given start position
	 * ('<' should be the first char)
	 * 
	 * @param ca
	 * @param start
	 * @return
	 */
	private static int findClosingTag(final char[] ca, final int start, final int end) {
		if (start >= 0 && start < ca.length && start < end) {
			final int s1 = findAny(ca, start, end, ' ', '>');
			if (s1 != -1) {
				final char[] opener = new char[s1 - start];
				opener[0] = '<';
				final char[] closer = new char[s1 - start + 2];
				closer[0] = '<';
				closer[1] = '/';
				closer[closer.length - 1] = '>';
				int i = start + 1;
				for (; i < s1; i++) {
					opener[i - start] = ca[i];
					closer[i - start + 1] = ca[i];
				}

				int count1 = 1;
				int count2 = 1;
				for (; i < ca.length; i++) {
					if (ca[i] == '<') {
						count1++;
						if (isNext(ca, i, opener)) {
							count2++;
						} else if (isNext(ca, i, closer)) {
							count2--;
							if (count2 == 0) {
								return i;
							}
						} else if (isNext(ca, i, ELEMENT_CDATA)) {
							i = findAll(ca, i + ELEMENT_CDATA.length, end, ELEMENT_CDATA_END);
						}
					} else if (ca[i] == '>') {
						if (ca[i - 1] == '/') {
							count1--;
						}
						if (count1 == 0) {
							return i;
						}
					} else if (ca[i] == '"') {
						i = closer(ca, i, end); // just entered a string - get out of it
					}
				}
			}
		}
		return -1;
	}

	private static int findNextTag(final char[] ca, final int start, final int end) {
		final int s1 = findAll(ca, start, end, '<');
		if (s1 != -1 && s1 < ca.length - 1) {
			if (ca[s1 + 1] != '/') {
				return s1;
			} else {
				return findNextTag(ca, s1 + 1, end);
			}
		}
		return -1;
	}

	private static int forward(final char[] ca, final int from) {
		for (int i = from; i >= 0 && i < ca.length; i++) {
			if (!Character.isWhitespace(ca[i])) {
				return i;
			}
		}
		return -1;
	}

	private static double getAngle(final double ux, final double uy, final double vx, final double vy) {
		final double dot = ux * vx + uy * vy;
		final double au = hypot(ux, uy);
		final double av = hypot(vx, vy);
		double alpha = dot / (au * av);
		if (alpha > 1) {
			alpha = 1;
		} else if (alpha < -1) {
			alpha = -1;
		}
		double theta = 180 * acos(alpha) / PI;
		if ((ux * vy - uy * vx) < 0) {
			theta *= -1;
		}
		return theta;
	}

	private static String getAttrValue(final char[] ca, final int start, final int end, final char... name) {
		final char[] search = new char[name.length + 2];
		System.arraycopy(name, 0, search, 1, name.length);
		search[0] = ' ';
		search[search.length - 1] = '=';
		int s1 = findAll(ca, start, end, search);
		if (s1 != -1) {
			s1 = findAll(ca, s1, end, '"');
			if (s1 != -1) {
				final int s2 = closer(ca, s1, end);
				if (s1 != -1) {
					return new String(ca, s1 + 1, s2 - s1 - 1);
				}
			}
		}
		return null;
	}

	private static int[] getAttrValueRange(final char[] ca, final int start, final int end, final char... name) {
		final char[] search = new char[name.length + 2];
		System.arraycopy(name, 0, search, 1, name.length);
		search[0] = ' ';
		search[search.length - 1] = '=';
		int s1 = findAll(ca, start, end, search);
		if (s1 != -1) {
			s1 = findAll(ca, s1, end, '"');
			if (s1 != -1) {
				final int s2 = closer(ca, s1, end);
				if (s1 != -1) {
					return new int[] { s1 + 1, s2 - 1 };
				}
			}
		}
		return new int[] { -1, -1 };
	}

	private static Map<String, String> getClassStyles(final SvgElement element, final char[] ca, final int start,
			final int end) {
		final String s = getAttrValue(ca, start, end, ATTR_CLASS);
		if (s != null) {
			final Map<String, String> styles = new HashMap<String, String>();
			final String[] classes = s.trim().split(" +"); //$NON-NLS-1$
			for (final String c : classes) {
				final Map<String, String> pairs = element.getFragment().getStyles("." + c); //$NON-NLS-1$
				if (pairs != null) {
					styles.putAll(pairs);
				}
			}
			return styles;
		}
		return new HashMap<String, String>(0);
	}

	private static Integer getColorAsInt(final String color) {
		if (color != null) {
			if (SvgColors.contains(color)) {
				return SvgColors.get(color);
			} else if ('#' == color.charAt(0)) {
				if (color.length() == 4) {
					final char[] ca = new char[6];
					ca[0] = color.charAt(1);
					ca[1] = color.charAt(1);
					ca[2] = color.charAt(2);
					ca[3] = color.charAt(2);
					ca[4] = color.charAt(3);
					ca[5] = color.charAt(3);
					return Integer.parseInt(new String(ca), 16);
				} else if (color.length() == 7) {
					return Integer.parseInt(color.substring(1), 16);
				}
			}
		}
		return null;
	}

	private static Map<String, String> getIdStyles(final SvgElement element, final char[] ca, final int start,
			final int end) {
		final String s = element.getId();
		if (s != null) {
			final Map<String, String> styles = new HashMap<String, String>();
			final Map<String, String> pairs = element.getFragment().getStyles("#" + s); //$NON-NLS-1$
			if (pairs != null) {
				styles.putAll(pairs);
			}
			return styles;
		}
		return new HashMap<String, String>(0);
	}

	private static String getLink(final String link) {
		urlMatcher.reset(link);
		if (urlMatcher.matches()) {
			return urlMatcher.group(1);
		}
		return null;
	}

	/**
	 * Types:
	 * <ul>
	 * <li>matrix(<a> <b> <c> <d> <e> <f>)</li>
	 * <li>translate(<tx> [<ty>])</li>
	 * <li>scale(<sx> [<sy>])</li>
	 * <li>rotate(<rotate-angle> [<cx> <cy>])</li>
	 * <li>skewX(<skew-angle>)</li>
	 * <li>skewY(<skew-angle>)</li>
	 * </ul>
	 *
	 * @param str
	 * @return
	 */
	private static SvgTransform getTransform(final char[] ca, final int[] range) {
		int s1 = range[0];

		SvgTransform first = null;
		SvgTransform transform = null;
		while (s1 != -1 && s1 < range[1]) {
			final int s2 = findAll(ca, s1, range[1], '(');
			final int s3 = findAll(ca, s2, range[1], ')');
			if (s1 != -1 && s2 != -1 && s3 != -1) {
				if (transform == null) {
					first = transform = new SvgTransform();
				} else {
					transform.next = new SvgTransform();
					transform = transform.next;
				}
				if (isEqual(ca, s1, s2 - 1, "matrix".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.Matrix, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				} else if (isEqual(ca, s1, s2 - 1, "translate".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.Translate, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				} else if (isEqual(ca, s1, s2 - 1, "scale".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.Scale, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				} else if (isEqual(ca, s1, s2 - 1, "rotate".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.Rotate, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				} else if (isEqual(ca, s1, s2 - 1, "skewx".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.SkewX, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				} else if (isEqual(ca, s1, s2 - 1, "skewy".toCharArray())) { //$NON-NLS-1$
					transform.setData(Type.SkewY, new String(ca, s2 + 1, s3 - s2 - 1).split(paramRegex));
				}
			}
			s1 = forward(ca, s3 + 1);
		}

		if (first != null) {
			return first;
		} else {
			return new SvgTransform();
		}
	}

	private static String getValue(final String name, final Map<String, String> idStyles,
			final Map<String, String> classStyles, final Map<String, String> attrStyles, final String attrValue) {
		return getValue(name, idStyles, classStyles, attrStyles, attrValue, null);
	}

	private static String getValue(final String name, final Map<String, String> idStyles,
			final Map<String, String> classStyles, final Map<String, String> attrStyles, final String attrValue,
			final String defaultValue) {
		if (attrValue != null) {
			return attrValue;
		}
		if (attrStyles.containsKey(name)) {
			return attrStyles.get(name);
		}
		if (classStyles.containsKey(name)) {
			return classStyles.get(name);
		}
		if (idStyles.containsKey(name)) {
			return idStyles.get(name);
		}
		return defaultValue;
	}

	private static boolean isEqual(final char[] ca, final int start, final int end, final char... test) {
		if (test.length != (end - start + 1)) {
			return false;
		}
		for (int i = start, j = 0; i < end && j < test.length; i++, j++) {
			if (ca[i] != test[j]) {
				return false;
			}
		}
		return true;
	}

	private static boolean isNext(final char[] ca, final int start, final char... test) {
		for (int i = start, j = 0; j < test.length; i++, j++) {
			if (ca[i] != test[j]) {
				return false;
			}
		}
		return true;
	}

	private static boolean isTag(final char[] ca, final int start, final char[] tagName) {
		if (start >= 0 && start < ca.length && ca[start] == '<') {
			int i = start + 1;
			for (int j = 0; i < ca.length && j < tagName.length; i++, j++) {
				if (ca[i] != tagName[j]) {
					return false;
				}
			}
			if (i < ca.length) {
				return (ca[i] == ' ') || (ca[i] == '>');
			}
		}
		return false;
	}

	static SvgDocument load(final InputStream in) {
		final StringBuilder sb = new StringBuilder();
		final BufferedInputStream bis = new BufferedInputStream(in);
		int i;
		try {
			while ((i = bis.read()) != -1) {
				final char c = (char) i;
				if (Character.isWhitespace(c)) { // replace all whitespace chars with a space char
					if (' ' != sb.charAt(sb.length() - 1)) { // no point in having multiple spaces
						sb.append(' ');
					}
				} else {
					sb.append(c);
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final char[] ca = new char[sb.length()];
		sb.getChars(0, sb.length(), ca, 0);
		final SvgDocument doc = new SvgDocument();
		parse(doc, ca, 0, ca.length - 1);
		return doc;
	}

	static SvgDocument load(final String src) {
		final SvgDocument doc = new SvgDocument();
		parse(doc, src.toCharArray(), 0, src.length() - 1);
		return doc;
	}

	private static void parse(final SvgContainer container, final char[] ca, final int start, final int end) {
		int s1 = start;
		while (s1 != -1 && s1 < end) {
			s1 = findNextTag(ca, s1, end);
			if (isTag(ca, s1, ELEMENT_GROUP)) {
				s1 = parseGroup(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_TITLE)) {
				s1 = parseTitle(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_DESCRIPTION)) {
				s1 = parseDescription(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_SVG)) {
				s1 = parseSvg(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_STYLE)) {
				s1 = parseStyle(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_USE)) {
				s1 = parseUse(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_PATH)) {
				s1 = parsePath(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_CIRCLE)) {
				s1 = parseCircle(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_ELLIPSE)) {
				s1 = parseEllipse(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_LINE)) {
				s1 = parseLine(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_POLYGON)) {
				s1 = parsePolygon(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_POLYLINE)) {
				s1 = parsePolyline(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_RECT)) {
				s1 = parseRectangle(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_LINEAR_GRADIENT)) {
				s1 = parseLinearGradient(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_RADIAL_GRADIENT)) {
				s1 = parseRadialGradient(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_DEFS)) {
				s1 = parseDefs(container, ca, s1, end);
			} else if (isTag(ca, s1, ELEMENT_XML) || isTag(ca, s1, ELEMENT_DOCTYPE)) {
				s1 = findAll(ca, s1, end, '>');
			} else if (isTag(ca, s1, ELEMENT_COMMENT)) {
				s1 = findAll(ca, s1, end, ELEMENT_COMMENT_END);
			} else {
				if (s1 != -1) {
					final int s2 = findAny(ca, s1, end, ' ', '>');
					System.out.println("dunno: " + new String(ca, s1 + 1, s2 - s1 - 1)); //$NON-NLS-1$
				}
				s1 = findClosingTag(ca, s1, end);
			}
		}
	}

	private static int parseCircle(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			element.pathData = new PathData();
			element.pathData.points = new float[4];
			element.pathData.points[0] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_CX), 0);
			element.pathData.points[1] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_CY), 0);
			element.pathData.points[2] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_R));
			element.pathData.points[3] = element.pathData.points[2];
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static void parseCss(final SvgStyle element, final char[] ca, final int start, final int end) {
		final Map<String, Map<String, String>> styles = new HashMap<String, Map<String, String>>();

		int s1 = forward(ca, start);
		int s = findAll(ca, s1, end, '{');
		int s2 = reverse(ca, s - 1);
		String names;

		while (s1 != -1 && s2 != -1) {
			names = new String(ca, s1, s2 - s1 + 1);
			s2 = closer(ca, s, end);
			if (s2 != -1) {
				final Map<String, String> pairs = parseStyles(ca, s + 1, s2 - 1);
				for (final String name : names.split(" *, *")) { //$NON-NLS-1$
					final Map<String, String> existing = styles.get(name);
					if (existing != null) {
						final Map<String, String> m = new HashMap<String, String>();
						m.putAll(existing);
						m.putAll(pairs);
						styles.put(name, m);
					} else {
						styles.put(name, pairs);
					}
				}
				s1 = forward(ca, s2 + 1);
				s = findAll(ca, s1, end, '{');
				s2 = reverse(ca, s - 1);
			}
		}

		element.styles = styles;
	}

	private static int parseDefs(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgContainer element = new SvgContainer(container, "defs"); //$NON-NLS-1$
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
			parse(element, ca, endAttrs, end);
		}
		return end;
	}

	private static int parseDescription(final SvgContainer container, final char[] ca, int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			start = closer(ca, start, end);
			if (start != -1) {
				container.description = new String(ca, start + 1, end - start - 1);
			}
		}
		return end;
	}

	private static int parseEllipse(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			element.pathData = new PathData();
			element.pathData.points = new float[4];
			element.pathData.points[0] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_CX), 0);
			element.pathData.points[1] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_CY), 0);
			element.pathData.points[2] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_RX));
			element.pathData.points[3] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_RY));
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static void parseFill(final SvgGraphic element, final char[] ca, final int start, final int end) {
		final Map<String, String> idStyles = getIdStyles(element, ca, start, end);
		final Map<String, String> classStyles = getClassStyles(element, ca, start, end);
		final Map<String, String> attrStyles = parseStyles(getAttrValue(ca, start, end, ATTR_STYLE));

		String s = getValue("fill", idStyles, classStyles, attrStyles, getAttrValue(ca, start, end, ATTR_FILL)); //$NON-NLS-1$
		parsePaint(element.fill, s);

		s = getValue("fill-opacity", idStyles, classStyles, attrStyles, //$NON-NLS-1$
				getAttrValue(ca, start, end, ATTR_FILL_OPACITY));
		element.fill.opacity = parseFloat(s);

		s = getValue("fill-rule", idStyles, classStyles, attrStyles, getAttrValue(ca, start, end, ATTR_FILL_RULE)); //$NON-NLS-1$
		element.fill.rule = parseRule(s);
	}

	private static Float parseFloat(final String s) {
		return parseFloat(s, null);
	}

	private static float parseFloat(final String s, final float defaultValue) {
		if (s == null) {
			return defaultValue;
		} else {
			return Float.parseFloat(s);
		}
	}

	private static Float parseFloat(final String s, final Float defaultValue) {
		if (s == null) {
			return defaultValue;
		} else {
			return new Float(s);
		}
	}

	private static int parseGradientStop(final SvgGradient gradient, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgGradientStop stop = new SvgGradientStop(gradient, getAttrValue(ca, start, endAttrs, ATTR_ID));

			final Map<String, String> idStyles = getIdStyles(stop, ca, start, endAttrs);
			final Map<String, String> classStyles = getClassStyles(stop, ca, start, endAttrs);
			final Map<String, String> attrStyles = parseStyles(getAttrValue(ca, start, endAttrs, ATTR_STYLE));

			String s = getValue("offset", idStyles, classStyles, attrStyles, //$NON-NLS-1$
					getAttrValue(ca, start, endAttrs, ATTR_OFFSET));
			stop.offset = parsePercentage(s, 0f, true);

			s = getValue("stop-color", idStyles, classStyles, attrStyles, //$NON-NLS-1$
					getAttrValue(ca, start, endAttrs, ATTR_STOP_COLOR));
			stop.color = getColorAsInt(s);

			s = getValue("stop-opacity", idStyles, classStyles, attrStyles, //$NON-NLS-1$
					getAttrValue(ca, start, endAttrs, ATTR_STOP_OPACITY), "1"); //$NON-NLS-1$
			stop.opacity = parseFloat(s);

			gradient.stops.add(stop);
		}
		return end;
	}

	private static int parseGroup(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgContainer element = new SvgContainer(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
			parse(element, ca, endAttrs, end);
		}
		return end;
	}

	// cm, em, ex, in, mm, pc, pt, px
	private static float parseLength(String s, final String defaultString) {
		if (s == null) {
			s = defaultString;
		}
		if (s.endsWith("%")) { //$NON-NLS-1$
			throw new UnsupportedOperationException("TODO parseLength: %"); //$NON-NLS-1$
		} else if (s.endsWith("cm")) { //$NON-NLS-1$
			final Point dpi = new Point(0, 0);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dpi.x = Display.getDefault().getDPI().x;
				}
			});
			return Float.parseFloat(s.substring(0, s.length() - 2)) * dpi.x * 0.393700787f;
		} else if (s.endsWith("em")) { //$NON-NLS-1$
			throw new UnsupportedOperationException("TODO parseLength: em"); //$NON-NLS-1$
		} else if (s.endsWith("ex")) { //$NON-NLS-1$
			throw new UnsupportedOperationException("TODO parseLength: ex"); //$NON-NLS-1$
		} else if (s.endsWith("in")) { //$NON-NLS-1$
			final Point dpi = new Point(0, 0);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dpi.x = Display.getDefault().getDPI().x;
				}
			});
			return Float.parseFloat(s.substring(0, s.length() - 2)) * dpi.x;
		} else if (s.endsWith("mm")) { //$NON-NLS-1$
			final Point dpi = new Point(0, 0);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					dpi.x = Display.getDefault().getDPI().x;
				}
			});
			return Float.parseFloat(s.substring(0, s.length() - 2)) * dpi.x * 0.0393700787f;
		} else if (s.endsWith("pc")) { //$NON-NLS-1$
			throw new UnsupportedOperationException("TODO parseLength: pc"); //$NON-NLS-1$
		} else if (s.endsWith("pt")) { //$NON-NLS-1$
			throw new UnsupportedOperationException("TODO parseLength: pt"); //$NON-NLS-1$
		} else if (s.endsWith("px")) { //$NON-NLS-1$
			return Float.parseFloat(s.substring(0, s.length() - 2));
		} else {
			return Float.parseFloat(s);
		}
	}

	private static int parseLine(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			element.pathData = new PathData();
			element.pathData.types = new byte[2];
			element.pathData.points = new float[4];
			element.pathData.types[0] = (byte) SWT.PATH_MOVE_TO;
			element.pathData.points[0] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_X1), 0);
			element.pathData.points[1] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_Y1), 0);
			element.pathData.types[1] = (byte) SWT.PATH_LINE_TO;
			element.pathData.points[2] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_X2), 0);
			element.pathData.points[3] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_Y2), 0);
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
		}
		return end;
	}

	private static int parseLinearGradient(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgGradient gradient = new SvgGradient(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			gradient.data = new float[4];
			gradient.data[0] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_X1), 0f, false);
			gradient.data[1] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_Y1), 0f, false);
			gradient.data[2] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_X2), 1f, false);
			gradient.data[3] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_Y2), 0f, false);
			gradient.setLinkId(getAttrValue(ca, start, endAttrs, ATTR_XLINK_HREF));
			gradient.setSpreadMethod(getAttrValue(ca, start, endAttrs, ATTR_SPREAD_METHOD));
			gradient.setUnits(getAttrValue(ca, start, endAttrs, ATTR_GRADIENT_UNITS));
			gradient.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_GRADIENT_TRANSFORM));
			int s1 = endAttrs;
			while (s1 != -1 && s1 < end) {
				s1 = findNextTag(ca, s1 + 1, end);
				if (isNext(ca, s1 + 1, ATTR_STOP)) {
					s1 = parseGradientStop(gradient, ca, s1, end);
				}
			}
		}
		return end;
	}

	private static String parseLinkId(final String id) {
		if (id != null && id.length() > 2 && '#' == id.charAt(0)) {
			return id.substring(1);
		}
		return null;
	}

	private static void parsePaint(final SvgPaint paint, final String s) {
		if (s != null) {
			if ("none".equals(s)) { //$NON-NLS-1$
				paint.type = PaintType.None;
			} else if ("currentColor".equals(s)) { //$NON-NLS-1$
				paint.type = PaintType.Current;
			} else if (s.startsWith("url")) { //$NON-NLS-1$
				paint.type = PaintType.Link;
				paint.linkId = getLink(s);
			} else {
				final Integer i = getColorAsInt(s);
				if (i != null) {
					paint.type = PaintType.Color;
					paint.color = i;
				} else {
					paint.type = PaintType.None;
					System.out.println("dunno fill " + paint); //$NON-NLS-1$
				}
			}
		}
	}

	private static int parsePath(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			parsePathData(element, getAttrValue(ca, start, endAttrs, ATTR_D));
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	public static void parsePathData(final SvgShape path, final String data) {
		final String[] sa = parsePathDataStrings(data);
		boolean relative;
		final List<Byte> types = new ArrayList<Byte>();
		final List<Float> points = new ArrayList<Float>();
		int i = -1;
		while (i < sa.length - 1) {
			i++;
			switch (sa[i].charAt(0)) {
			case 'M':
			case 'm':
				types.add((byte) SWT.PATH_MOVE_TO);
				relative = ('m' == sa[i].charAt(0));
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'L':
			case 'l':
				types.add((byte) SWT.PATH_LINE_TO);
				relative = ('l' == sa[i].charAt(0));
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'H':
			case 'h':
				types.add((byte) SWT.PATH_LINE_TO);
				relative = ('h' == sa[i].charAt(0));
				addPoint(points, sa[++i], relative);
				points.add(points.get(points.size() - 2));
				break;
			case 'V':
			case 'v':
				types.add((byte) SWT.PATH_LINE_TO);
				relative = ('v' == sa[i].charAt(0));
				points.add(points.get(points.size() - 2));
				addPoint(points, sa[++i], relative);
				break;
			case 'C':
			case 'c':
				types.add((byte) SWT.PATH_CUBIC_TO);
				relative = ('c' == sa[i].charAt(0));
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'S':
			case 's':
				types.add((byte) SWT.PATH_CUBIC_TO);
				relative = ('s' == sa[i].charAt(0));
				if (SWT.PATH_CUBIC_TO == types.get(types.size() - 2)) {
					final float x2 = points.get(points.size() - 4);
					final float y2 = points.get(points.size() - 3);
					final float x = points.get(points.size() - 2);
					final float y = points.get(points.size() - 1);
					final float x1 = 2 * x - x2;
					final float y1 = 2 * y - y2;
					points.add(x1);
					points.add(y1);
				} else {
					points.add(points.get(points.size() - 2));
					points.add(points.get(points.size() - 2));
				}
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'Q':
			case 'q':
				types.add((byte) SWT.PATH_QUAD_TO);
				relative = ('q' == sa[i].charAt(0));
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'T':
			case 't':
				types.add((byte) SWT.PATH_QUAD_TO);
				relative = ('q' == sa[i].charAt(0));
				if (SWT.PATH_QUAD_TO == types.get(types.size() - 2)) {
					final float x2 = points.get(points.size() - 4);
					final float y2 = points.get(points.size() - 3);
					final float x = points.get(points.size() - 2);
					final float y = points.get(points.size() - 1);
					final float x1 = 2 * x - x2;
					final float y1 = 2 * y - y2;
					points.add(x1);
					points.add(y1);
				} else {
					points.add(points.get(points.size() - 2));
					points.add(points.get(points.size() - 2));
				}
				addPoint(points, sa[++i], relative);
				addPoint(points, sa[++i], relative);
				break;
			case 'Z':
			case 'z':
				types.add((byte) SWT.PATH_CLOSE);
				break;
			case 'A':
			case 'a':
				relative = ('a' == sa[i].charAt(0));
				addArc(sa, ++i, types, points, relative);
				i += 6;
				break;
			}
		}

		path.pathData = new PathData();
		path.pathData.types = new byte[types.size()];
		for (i = 0; i < types.size(); i++) {
			path.pathData.types[i] = types.get(i).byteValue();
		}
		path.pathData.points = new float[points.size()];
		for (i = 0; i < points.size(); i++) {
			path.pathData.points[i] = points.get(i).floatValue();
		}
	}

	private static String[] parsePathDataStrings(final String data) {
		final List<String> strs = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		final char[] ca = data.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			final char c = ca[i];
			if ('e' == c) {
				sb.append(c);
				if (i < ca.length - 1 && ca[i + 1] == '-') {
					i++;
					sb.append(ca[i]);
				}
			} else if (Character.isLetter(c)) {
				if (sb != null && sb.length() > 0) {
					strs.add(sb.toString());
				}
				strs.add(Character.toString(c));
				sb = new StringBuilder();
			} else if ('.' == c || Character.isDigit(c)) {
				sb.append(c);
			} else {
				if (sb != null && sb.length() > 0) {
					strs.add(sb.toString());
				}
				sb = new StringBuilder();
				if ('-' == c) {
					sb.append(c);
				}
			}
		}
		if (sb != null && sb.length() > 0) {
			strs.add(sb.toString());
		}
		return strs.toArray(new String[strs.size()]);
	}

	private static Float parsePercentage(final String s, final Float defaultValue, final boolean clamp) {
		if (s != null) {
			Float offset;
			if ('%' == s.charAt(s.length() - 1)) {
				offset = Float.parseFloat(s.substring(0, s.length() - 1)) / 100;
			} else {
				offset = Float.parseFloat(s);
			}
			if (clamp) {
				if (offset > 1) {
					offset = new Float(1);
				} else if (offset < 0) {
					offset = new Float(0);
				}
			}
			return offset;
		}
		return defaultValue;
	}

	private static float[] parsePoints(final String s) {
		if (s != null) {
			final String[] sa = s.trim().split("[ ,]"); //$NON-NLS-1$
			final float[] points = new float[sa.length];
			for (int i = 0; i < sa.length; i++) {
				points[i] = parseFloat(sa[i]);
			}
			return points;
		}
		return new float[0];
	}

	private static int parsePolygon(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			final float[] linePoints = parsePoints(getAttrValue(ca, start, endAttrs, ATTR_POINTS));
			element.pathData = new PathData();
			element.pathData.types = new byte[1 + (linePoints.length / 2)];
			element.pathData.points = new float[linePoints.length];
			element.pathData.types[0] = (byte) SWT.PATH_MOVE_TO;
			element.pathData.points[0] = linePoints[0];
			element.pathData.points[1] = linePoints[1];
			int i = 2, j = 1;
			while (i < linePoints.length - 1) {
				element.pathData.types[j++] = (byte) SWT.PATH_LINE_TO;
				element.pathData.points[i] = linePoints[i++];
				element.pathData.points[i] = linePoints[i++];
			}
			element.pathData.types[element.pathData.types.length - 1] = (byte) SWT.PATH_CLOSE;
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static int parsePolyline(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			final float[] linePoints = parsePoints(getAttrValue(ca, start, endAttrs, ATTR_POINTS));
			element.pathData = new PathData();
			element.pathData.types = new byte[linePoints.length / 2];
			element.pathData.points = new float[linePoints.length];
			element.pathData.types[0] = (byte) SWT.PATH_MOVE_TO;
			element.pathData.points[0] = linePoints[0];
			element.pathData.points[1] = linePoints[1];
			int i = 2, j = 1;
			while (i < linePoints.length - 1) {
				element.pathData.types[j++] = (byte) SWT.PATH_LINE_TO;
				element.pathData.points[i] = linePoints[i++];
				element.pathData.points[i] = linePoints[i++];
			}
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static int parseRadialGradient(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgGradient gradient = new SvgGradient(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			gradient.data = new float[5];
			gradient.data[0] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_CX), null, false);
			gradient.data[1] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_CY), null, false);
			gradient.data[2] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_FX), gradient.data[0], false);
			gradient.data[3] = parsePercentage(getAttrValue(ca, start, endAttrs, ATTR_FY), gradient.data[1], false);
			gradient.data[4] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_R));
			gradient.setLinkId(getAttrValue(ca, start, endAttrs, ATTR_XLINK_HREF));
			gradient.setSpreadMethod(getAttrValue(ca, start, endAttrs, ATTR_SPREAD_METHOD));
			gradient.setUnits(getAttrValue(ca, start, endAttrs, ATTR_GRADIENT_UNITS));
			gradient.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_GRADIENT_TRANSFORM));
			int s1 = endAttrs;
			while (s1 != -1 && s1 < end) {
				s1 = findNextTag(ca, s1 + 1, end);
				if (isNext(ca, s1 + 1, ATTR_STOP)) {
					s1 = parseGradientStop(gradient, ca, s1, end);
				}
			}
		}
		return end;
	}

	private static int parseRectangle(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgShape element = new SvgShape(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			element.pathData = new PathData();
			element.pathData.points = new float[6];
			element.pathData.points[0] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_X));
			element.pathData.points[1] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_Y));
			element.pathData.points[2] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_WIDTH));
			element.pathData.points[3] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_HEIGHT));
			element.pathData.points[4] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_RX), 0);
			element.pathData.points[5] = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_RY), 0);
			parseFill(element, ca, start, endAttrs);
			parseStroke(element, ca, start, endAttrs);
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static Integer parseRule(final String s) {
		if (s != null) {
			if ("evenodd".equals(s)) { //$NON-NLS-1$
				return SWT.FILL_EVEN_ODD;
			} else if ("winding".equals(s)) { //$NON-NLS-1$
				return SWT.FILL_WINDING;
			}
		}
		return null;
	}

	private static void parseStroke(final SvgGraphic element, final char[] ca, final int start, final int end) {
		final Map<String, String> idStyles = getIdStyles(element, ca, start, end);
		final Map<String, String> classStyles = getClassStyles(element, ca, start, end);
		final Map<String, String> attrStyles = parseStyles(getAttrValue(ca, start, end, ATTR_STYLE));

		String s = getValue("stroke", idStyles, classStyles, attrStyles, getAttrValue(ca, start, end, ATTR_STROKE)); //$NON-NLS-1$
		parsePaint(element.stroke, s);

		s = getValue("stroke-opacity", idStyles, classStyles, attrStyles, //$NON-NLS-1$
				getAttrValue(ca, start, end, ATTR_STROKE_OPACITY));
		element.stroke.opacity = parseFloat(s);

		s = getValue("stroke-width", idStyles, classStyles, attrStyles, //$NON-NLS-1$
				getAttrValue(ca, start, end, ATTR_STROKE_WIDTH));
		element.stroke.width = parseStrokeWidth(s);

		s = getValue("stroke-linecap", idStyles, classStyles, attrStyles, //$NON-NLS-1$
				getAttrValue(ca, start, end, ATTR_STROKE_CAP));
		element.stroke.lineCap = parseStrokeLineCap(s);

		s = getValue("stroke-linejoin", idStyles, classStyles, attrStyles, //$NON-NLS-1$
				getAttrValue(ca, start, end, ATTR_STROKE_JOIN));
		element.stroke.lineJoin = parseStrokeLineJoin(s);
	}

	private static Integer parseStrokeLineCap(final String s) {
		if (s != null) {
			if ("butt".equals(s)) { //$NON-NLS-1$
				return SWT.CAP_FLAT;
			} else if ("round".equals(s)) { //$NON-NLS-1$
				return SWT.CAP_ROUND;
			} else if ("square".equals(s)) { //$NON-NLS-1$
				return SWT.CAP_SQUARE;
			}
		}
		return null;
	}

	private static Integer parseStrokeLineJoin(final String s) {
		if (s != null) {
			if ("bevel".equals(s)) { //$NON-NLS-1$
				return SWT.JOIN_BEVEL;
			} else if ("miter".equals(s)) { //$NON-NLS-1$
				return SWT.JOIN_MITER;
			} else if ("round".equals(s)) { //$NON-NLS-1$
				return SWT.JOIN_ROUND;
			}
		}
		return null;
	}

	private static Float parseStrokeWidth(final String s) {
		if (s != null) {
			if (s.endsWith("px")) { //$NON-NLS-1$
				return new Float(s.substring(0, s.length() - 2));
			} else {
				return new Float(s);
			}
		}
		return null;
	}

	private static int parseStyle(final SvgContainer container, final char[] ca, int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			int endData = closer(ca, start, end);
			final SvgStyle element = new SvgStyle(container);
			final int cd1 = findAll(ca, start, end, ELEMENT_CDATA);
			if (cd1 != -1) {
				start = cd1 + ELEMENT_CDATA.length;
				endData = findAll(ca, start, end, ELEMENT_CDATA_END);
			} else {
				start = endData + 1;
				endData = end;
			}
			parseCss(element, ca, start, endData);
		}
		return end;
	}

	private static Map<String, String> parseStyles(final char[] ca, final int start, final int end) {
		final Map<String, String> styles = new HashMap<String, String>();
		final int len = end - start + 1;
		if (len > 0 && start + len <= ca.length) {
			final String[] sa = new String(ca, start, end - start + 1).trim().split(" *; *"); //$NON-NLS-1$
			for (final String s : sa) {
				final String[] sa2 = s.split(" *: *"); //$NON-NLS-1$
				if (sa2.length == 2) {
					styles.put(sa2[0], sa2[1]);
				}
			}
		}
		return styles;
	}

	private static Map<String, String> parseStyles(final String styles) {
		if (styles != null) {
			return parseStyles(styles.toCharArray(), 0, styles.length() - 1);
		}
		return new HashMap<String, String>(0);
	}

	private static int parseSvg(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgFragment element = new SvgFragment(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			if (container != null) {
				// x and y have no effect on outermost svg fragments
				element.x = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_X));
				element.y = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_Y));
			}
			element.width = parseLength(getAttrValue(ca, start, endAttrs, ATTR_WIDTH), "100%"); //$NON-NLS-1$
			element.height = parseLength(getAttrValue(ca, start, endAttrs, ATTR_HEIGHT), "100%"); //$NON-NLS-1$
			element.viewBox = parseViewBox(getAttrValue(ca, start, endAttrs, ATTR_VIEWBOX));
			// TODO element.preserveAspectRatio =
			parse(element, ca, endAttrs, end);
		}
		return end;
	}

	private static int parseTitle(final SvgContainer container, final char[] ca, int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			start = closer(ca, start, end);
			if (start != -1) {
				container.title = new String(ca, start + 1, end - start - 1);
			}
		}
		return end;
	}

	private static int parseUse(final SvgContainer container, final char[] ca, final int start, int end) {
		end = findClosingTag(ca, start, end);
		if (end != -1) {
			final int endAttrs = closer(ca, start, end);
			final SvgUse element = new SvgUse(container, getAttrValue(ca, start, endAttrs, ATTR_ID));
			element.x = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_X), 0);
			element.y = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_Y), 0);
			element.w = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_WIDTH));
			element.h = parseFloat(getAttrValue(ca, start, endAttrs, ATTR_HEIGHT));
			element.linkId = parseLinkId(getAttrValue(ca, start, endAttrs, ATTR_XLINK_HREF));
			element.transform = getTransform(ca, getAttrValueRange(ca, start, endAttrs, ATTR_TRANSFORM));
		}
		return end;
	}

	private static float[] parseViewBox(final String s) {
		if (s != null) {
			final float[] vb = new float[4];
			final String[] sa = s.split(paramRegex);
			if (sa.length == 4) {
				vb[0] = Float.parseFloat(sa[0]);
				vb[1] = Float.parseFloat(sa[1]);
				vb[2] = Float.parseFloat(sa[2]);
				vb[3] = Float.parseFloat(sa[3]);
				return vb;
			}
		}
		return null;
	}

	private static int reverse(final char[] ca, final int from) {
		for (int i = from; i >= 0 && i < ca.length; i--) {
			if (!Character.isWhitespace(ca[i])) {
				return i;
			}
		}
		return -1;
	}

	private SvgLoader() {
		// class should never be instantiated
	}

}
