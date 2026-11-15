/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.Testing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.AssertionFailedError;

/**
 * Compares a rendered image against a committed reference PNG, with a
 * per-pixel tolerance, so that a library upgrade which changes how a plot is
 * drawn shows up as a test failure.
 *
 * <p>
 * References live beside the other golden files ({@link GoldenFile#dir()}) and
 * are regenerated the same way: {@value GoldenFile#REGENERATE_ENV}{@code =true}
 * or {@code -D}{@value GoldenFile#REGENERATE_PROPERTY}{@code =true}.
 *
 * <p>
 * A pixel counts as different when any of its red, green or blue values differs
 * from the reference by more than {@code channelTolerance}. The test fails when
 * the share of different pixels is above {@code maxDifferentFraction}. On a
 * failure the actual image and a diff image (different pixels in red over a
 * faded copy of the reference) are written to {@link #failureDir()}, which CI
 * uploads.
 */
public final class GoldenImage {

	/** system property: where to write the actual and diff images of a failure */
	public static final String FAILURE_DIR_PROPERTY = "debrief.golden.failures";

	private static final String DEFAULT_FAILURE_DIR = "target/golden-failures";

	/** the result of comparing two images of the same size */
	public static final class Comparison {
		public final int differentPixels;
		public final int totalPixels;
		public final BufferedImage diff;

		Comparison(final int differentPixels, final int totalPixels, final BufferedImage diff) {
			this.differentPixels = differentPixels;
			this.totalPixels = totalPixels;
			this.diff = diff;
		}

		public double fraction() {
			return totalPixels == 0 ? 0 : (double) differentPixels / totalPixels;
		}
	}

	/**
	 * check the image against the named reference PNG, or write it if
	 * regenerating
	 *
	 * @param name                 file name within the golden directory, e.g.
	 *                             "rendering/sample_dpf.png"
	 * @param actual               the rendered image
	 * @param channelTolerance     largest difference (0-255) in any colour
	 *                             channel that still counts as the same
	 * @param maxDifferentFraction largest share of different pixels (0-1) that
	 *                             still passes
	 */
	public static void assertMatches(final String name, final BufferedImage actual, final int channelTolerance,
			final double maxDifferentFraction) throws IOException {
		final File golden = new File(GoldenFile.dir(), name);
		if (GoldenFile.isRegenerating()) {
			golden.getParentFile().mkdirs();
			ImageIO.write(actual, "png", golden);
			System.out.println("Regenerated golden image " + golden.getCanonicalPath());
			return;
		}
		if (!golden.isFile()) {
			final File written = writeFailure(name, "actual", actual);
			throw new AssertionFailedError("Golden image " + golden.getCanonicalPath() + " is missing. Actual image written to "
					+ written.getCanonicalPath() + ". Run the test with " + GoldenFile.REGENERATE_ENV
					+ "=true to create it.");
		}
		final BufferedImage expected = ImageIO.read(golden);
		if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
			final File written = writeFailure(name, "actual", actual);
			throw new AssertionFailedError("Image size differs from golden image " + golden.getCanonicalPath()
					+ ": expected " + expected.getWidth() + "x" + expected.getHeight() + " but was "
					+ actual.getWidth() + "x" + actual.getHeight() + ". Actual image written to "
					+ written.getCanonicalPath());
		}
		final Comparison c = compare(expected, actual, channelTolerance);
		if (c.fraction() > maxDifferentFraction) {
			final File actualFile = writeFailure(name, "actual", actual);
			final File diffFile = writeFailure(name, "diff", c.diff);
			throw new AssertionFailedError(String.format(
					"Image differs from golden image %s: %d of %d pixels (%.3f%%) differ by more than %d,"
							+ " above the limit of %.3f%%. Actual image: %s; diff: %s. If the change is intended,"
							+ " regenerate with %s=true and explain why in the pull request.",
					golden.getCanonicalPath(), c.differentPixels, c.totalPixels, 100 * c.fraction(), channelTolerance,
					100 * maxDifferentFraction, actualFile.getCanonicalPath(), diffFile.getCanonicalPath(),
					GoldenFile.REGENERATE_ENV));
		}
	}

	/** compare two images of the same size */
	public static Comparison compare(final BufferedImage expected, final BufferedImage actual,
			final int channelTolerance) {
		final int w = expected.getWidth();
		final int h = expected.getHeight();
		final BufferedImage diff = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		int different = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				final int e = expected.getRGB(x, y);
				final int a = actual.getRGB(x, y);
				final boolean same = Math.abs(((e >> 16) & 0xff) - ((a >> 16) & 0xff)) <= channelTolerance
						&& Math.abs(((e >> 8) & 0xff) - ((a >> 8) & 0xff)) <= channelTolerance
						&& Math.abs((e & 0xff) - (a & 0xff)) <= channelTolerance;
				if (same) {
					// a faded copy of the reference, so the differences stand out
					final int grey = (((e >> 16) & 0xff) + ((e >> 8) & 0xff) + (e & 0xff)) / 3;
					final int faded = 192 + grey / 4;
					diff.setRGB(x, y, (faded << 16) | (faded << 8) | faded);
				} else {
					different++;
					diff.setRGB(x, y, 0xff0000);
				}
			}
		}
		return new Comparison(different, w * h, diff);
	}

	/** where the actual and diff images of a failure are written */
	public static File failureDir() {
		return new File(System.getProperty(FAILURE_DIR_PROPERTY, DEFAULT_FAILURE_DIR));
	}

	private static File writeFailure(final String name, final String kind, final BufferedImage image)
			throws IOException {
		final String base = name.replace('/', '_').replaceAll("\\.png$", "");
		final File out = new File(failureDir(), base + "-" + kind + ".png");
		out.getParentFile().mkdirs();
		ImageIO.write(image, "png", out);
		return out;
	}

	private GoldenImage() {
	}
}
