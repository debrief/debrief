
package edu.nps.moves.math;

/**
 * EXECUTIVE SUMMARY Module Name: Vec4f.java Description: Definition of the
 * Vec4f class Author: Kent A. Watsen, http://www.mbay.net/~watsen
 */

public class Vec4f {
	static public float dot(final Vec4f vec1, final Vec4f vec2) {
		return vec1.get(0) * vec2.get(0) + vec1.get(1) * vec2.get(1) + vec1.get(2) * vec2.get(2)
				+ vec1.get(3) * vec2.get(3);
	}

	private final float v[];

	public Vec4f() {
		v = new float[4];
		makeNull();
	}

	public Vec4f(final float vec[]) {
		v = new float[4];
		set(vec);
	}

	public Vec4f(final float v0, final float v1, final float v2, final float v3) {
		v = new float[4];
		set(v0, v1, v2, v3);
	}

	public Vec4f(final Vec4f vec) {
		v = new float[4];
		set(vec);
	}

	public void add(final Vec4f vec) {
		v[0] = v[0] + vec.get(0);
		v[1] = v[1] + vec.get(1);
		v[2] = v[2] + vec.get(2);
		v[3] = v[3] + vec.get(3);
	}

	public void add(final Vec4f vec1, final Vec4f vec2) {
		v[0] = vec1.get(0) + vec2.get(0);
		v[1] = vec1.get(1) + vec2.get(1);
		v[2] = vec1.get(2) + vec2.get(2);
		v[3] = vec1.get(3) + vec2.get(3);
	}

	public float dot(final Vec4f vec) {
		return v[0] * vec.get(0) + v[1] * vec.get(1) + v[2] * vec.get(2) + v[3] * vec.get(3);
	}

	public void get(final float vec[]) {
		vec[0] = v[0];
		vec[1] = v[1];
		vec[2] = v[2];
		vec[3] = v[3];
	}

	public void get(final float v0[], final float v1[], final float v2[], final float v3[]) {
		v0[0] = v[0];
		v1[0] = v[1];
		v2[0] = v[2];
		v3[0] = v[3];
	}

	public float get(final int index) {
		if (index < 0 || index > 3)
			return 0.0f;
		return v[index];
	}

	public void get(final Vec4f vec) {
		vec.set(v[0], v[1], v[2], v[3]);
	}

	public float length() {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2] + v[3] * v[3]);
	}

	public float length_sqr() {
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2] + v[3] * v[3];
	}

	public void makeNull() {
		v[0] = 0f;
		v[1] = 0f;
		v[2] = 0f;
	}

	public void negate() {
		v[0] = -v[0];
		v[1] = -v[1];
		v[2] = -v[2];
		v[3] = -v[3];
	}

	public void negate(final Vec4f vec) {
		v[0] = -vec.get(0);
		v[1] = -vec.get(1);
		v[2] = -vec.get(2);
		v[3] = -vec.get(3);
	}

	public void normalize() {
		float len_sqr;
		float one_over_length;

		len_sqr = length_sqr();
		if (len_sqr > 0.0001f) {
			one_over_length = 1.0f / (float) Math.sqrt(len_sqr);
		} else {
			one_over_length = 0.0f;
		}
		v[0] = v[0] * one_over_length;
		v[1] = v[1] * one_over_length;
		v[2] = v[2] * one_over_length;
		v[3] = v[3] * one_over_length;
	}

	public void normalize(final Vec4f vec) {
		float len_sqr;
		float one_over_length;

		len_sqr = vec.length_sqr();
		if (len_sqr > 0.0001f) {
			one_over_length = 1.0f / (float) Math.sqrt(len_sqr);
		} else {
			one_over_length = 0.0f;
		}
		v[0] = vec.get(0) * one_over_length;
		v[1] = vec.get(1) * one_over_length;
		v[2] = vec.get(2) * one_over_length;
		v[3] = vec.get(3) * one_over_length;
	}

	public void print() {
		System.out.println("v = " + v[0] + ", " + v[1] + ", " + v[2] + ", " + v[3]);
	}

	public void scale(final float s) {
		v[0] = s * v[0];
		v[1] = s * v[1];
		v[2] = s * v[2];
		v[3] = s * v[3];
	}

	public void scale(final float s, final Vec4f vec) {
		v[0] = s * vec.get(0);
		v[1] = s * vec.get(1);
		v[2] = s * vec.get(2);
		v[3] = s * vec.get(3);
	}

	public void set(final float vec[]) {
		v[0] = vec[0];
		v[1] = vec[1];
		v[2] = vec[2];
		v[3] = vec[3];
	}

	public void set(final float v0, final float v1, final float v2, final float v3) {
		v[0] = v0;
		v[1] = v1;
		v[2] = v2;
		v[3] = v3;
	}

	public void set(final int index, final float val) {
		if (index < 0 || index > 3)
			return;
		v[index] = val;
	}

	public void set(final Vec4f vec) {
		v[0] = vec.get(0);
		v[1] = vec.get(1);
		v[2] = vec.get(2);
		v[3] = vec.get(3);
	}

	public void sub(final Vec4f vec) {
		v[0] = v[0] - vec.get(0);
		v[1] = v[1] - vec.get(1);
		v[2] = v[2] - vec.get(2);
		v[3] = v[3] - vec.get(3);
	}

	public void sub(final Vec4f vec1, final Vec4f vec2) {
		v[0] = vec1.get(0) - vec2.get(0);
		v[1] = vec1.get(1) - vec2.get(1);
		v[2] = vec1.get(2) - vec2.get(2);
		v[3] = vec1.get(3) - vec2.get(3);
	}

	public void xform(final Matrix4f mat) // math_utils
	{
		final float tmp_v[] = new float[4];
		final float m[][] = new float[4][4];

		mat.getMat(m);
		tmp_v[0] = v[0] * m[0][0] + v[1] * m[0][1] + v[2] * m[0][2] + v[3] * m[0][3];
		tmp_v[1] = v[0] * m[1][0] + v[1] * m[1][1] + v[2] * m[1][2] + v[3] * m[1][3];
		tmp_v[2] = v[0] * m[2][0] + v[1] * m[2][1] + v[2] * m[2][2] + v[3] * m[2][3];
		tmp_v[3] = v[0] * m[3][0] + v[1] * m[3][1] + v[2] * m[3][2] + v[3] * m[3][3];
		set(tmp_v);
	}

	public void xform(final Matrix4f mat, final Vec4f vec) // math_utils
	{
		final float tmp_v[] = new float[4];
		final float m[][] = new float[4][4];

		vec.get(tmp_v);
		mat.getMat(m);
		v[0] = tmp_v[0] * m[0][0] + tmp_v[1] * m[0][1] + tmp_v[2] * m[0][2] + tmp_v[3] * m[0][3];
		v[1] = tmp_v[0] * m[1][0] + tmp_v[1] * m[1][1] + tmp_v[2] * m[1][2] + tmp_v[3] * m[1][3];
		v[2] = tmp_v[0] * m[2][0] + tmp_v[1] * m[2][1] + tmp_v[2] * m[2][2] + tmp_v[3] * m[2][3];
		v[3] = tmp_v[0] * m[3][0] + tmp_v[1] * m[3][1] + tmp_v[2] * m[3][2] + tmp_v[3] * m[3][3];
	}
}

/*
 * Copyright (c) 1997-2003, Naval Postgraduate School All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the Naval Postgraduate School nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
