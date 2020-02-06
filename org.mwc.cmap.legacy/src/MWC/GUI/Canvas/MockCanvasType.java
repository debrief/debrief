/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.util.Enumeration;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;

public class MockCanvasType implements CanvasType {

	@Override
	public void addPainter(final PaintListener listener) {

	}

	@Override
	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {

	}

	@Override
	public boolean drawImage(final Image img, final int x, final int y, final int width, final int height,
			final ImageObserver observer) {
		return false;
	}

	@Override
	public void drawLine(final int startX, final int startY, final int endX, final int endY) {

	}

	@Override
	public void drawOval(final int x, final int y, final int width, final int height) {

	}

	@Override
	public void drawPolygon(final int[] points, final int[] points2, final int points3) {

	}

	@Override
	public void drawPolyline(final int[] points) {
	}

	@Override
	public void drawPolyline(final int[] points, final int[] points2, final int points3) {

	}

	@Override
	public void drawRect(final int x1, final int y1, final int wid, final int height) {

	}

	@Override
	public void drawText(final Font theFont, final String theStr, final int x, final int y) {

	}

	@Override
	public void drawText(final String str, final int x, final int y) {

	}

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate) {

	}

	@Override
	public void drawText(final String str, final int x, final int y, final float rotate, final boolean above) {

	}

	@Override
	public void endDraw(final Object theVal) {

	}

	@Override
	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {

	}

	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle, final int alpha) {

	}

	@Override
	public void fillOval(final int x, final int y, final int width, final int height) {

	}

	@Override
	public void fillPolygon(final int[] points, final int[] points2, final int points3) {

	}

	@Override
	public void fillRect(final int x, final int y, final int wid, final int height) {

	}

	@Override
	public Color getBackgroundColor() {
		return null;
	}

	@Override
	public Graphics getGraphicsTemp() {
		return null;
	}

	@Override
	public float getLineWidth() {
		return 0;
	}

	@Override
	public Enumeration<PaintListener> getPainters() {
		return null;
	}

	@Override
	public PlainProjection getProjection() {
		return null;
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public int getStringHeight(final Font theFont) {
		return 0;
	}

	@Override
	public int getStringWidth(final Font theFont, final String theString) {
		return 0;
	}

	@Override
	public void removePainter(final PaintListener listener) {

	}

	@Override
	public void rescale() {

	}

	@Override
	public void setBackgroundColor(final Color theColor) {

	}

	@Override
	public void setColor(final Color theCol) {

	}

	@Override
	public void setFont(final Font theFont) {

	}

	@Override
	public void setLineStyle(final int style) {

	}

	@Override
	public void setLineWidth(final float width) {

	}

	@Override
	public void setProjection(final PlainProjection val) {

	}

	@Override
	public void setTooltipHandler(final TooltipHandler handler) {

	}

	@Override
	public void startDraw(final Object theVal) {

	}

	@Override
	public Point toScreen(final WorldLocation val) {
		return new Point(1, 2);
	}

	@Override
	public WorldLocation toWorld(final Point val) {
		return null;
	}

	@Override
	public void updateMe() {

	}

}
