
package edu.nps.moves.math;

/**
 * EXECUTIVE SUMMARY Module Name: Vec3f.java Description: Definition of the
 * Vec3f class
 *
 * @author Kent A. Watsen, http://www.mbay.net/~watsen
 */

public class Vec3f {
	static public float dot(final Vec3f vec1, final Vec3f vec2) {
		return vec1.get(0) * vec2.get(0) + vec1.get(1) * vec2.get(1) + vec1.get(2) * vec2.get(2);
	}

	private final float v[];

	public Vec3f() {
		v = new float[3];
		makeNull();
	}

	public Vec3f(final float vec[]) {
		v = new float[3];
		set(vec);
	}

	public Vec3f(final float v0, final float v1, final float v2) {
		v = new float[3];
		set(v0, v1, v2);
	}

	public Vec3f(final Vec3f vec) {
		v = new float[3];
		set(vec);
	}

	public void add(final Vec3f vec) {
		v[0] = v[0] + vec.get(0);
		v[1] = v[1] + vec.get(1);
		v[2] = v[2] + vec.get(2);
	}

	public void add(final Vec3f vec1, final Vec3f vec2) {
		v[0] = vec1.get(0) + vec2.get(0);
		v[1] = vec1.get(1) + vec2.get(1);
		v[2] = vec1.get(2) + vec2.get(2);
	}

	public void cross(final Vec3f vec) {
		final float tmp_float[] = new float[3];
		tmp_float[0] = v[1] * vec.get(2) - v[2] * vec.get(1);
		tmp_float[1] = v[2] * vec.get(0) - v[0] * vec.get(2);
		tmp_float[2] = v[0] * vec.get(1) - v[1] * vec.get(0);
		set(tmp_float);
	}

	public void cross(final Vec3f vec1, final Vec3f vec2) {
		v[0] = vec1.get(1) * vec2.get(2) - vec1.get(2) * vec2.get(1);
		v[1] = vec1.get(2) * vec2.get(0) - vec1.get(0) * vec2.get(2);
		v[2] = vec1.get(0) * vec2.get(1) - vec1.get(1) * vec2.get(0);
	}

	public float dot(final Vec3f vec) {
		return v[0] * vec.get(0) + v[1] * vec.get(1) + v[2] * vec.get(2);
	}

	public void get(final float vec[]) {
		vec[0] = v[0];
		vec[1] = v[1];
		vec[2] = v[2];
	}

	public void get(final float v0[], final float v1[], final float v2[]) {
		v0[0] = v[0];
		v1[0] = v[1];
		v2[0] = v[2];
	}

	public float get(final int index) {
		return v[index];
	}

	public void get(final Vec3f vec) {
		vec.set(0, v[0]);
		vec.set(1, v[1]);
		vec.set(2, v[2]);
	}

	public float length() {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	public float length_sqr() {
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
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
	}

	public void negate(final Vec3f vec) {
		v[0] = -vec.get(0);
		v[1] = -vec.get(1);
		v[2] = -vec.get(2);
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
	}

	public void normalize(final Vec3f vec) {
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
	}

	public void print() {
		System.out.println("v = " + v[0] + ", " + v[1] + ", " + v[2]);
	}

	public void scale(final float s) {
		v[0] = s * v[0];
		v[1] = s * v[1];
		v[2] = s * v[2];
	}

	public void scale(final float s, final Vec3f vec) {
		v[0] = s * vec.get(0);
		v[1] = s * vec.get(1);
		v[2] = s * vec.get(2);
	}

	public void set(final float vec[]) {
		v[0] = vec[0];
		v[1] = vec[1];
		v[2] = vec[2];
	}

	public void set(final float v0, final float v1, final float v2) {
		v[0] = v0;
		v[1] = v1;
		v[2] = v2;
	}

	public void set(final int index, final float val) {
		v[index] = val;
	}

	public void set(final Vec3f vec) {
		v[0] = vec.get(0);
		v[1] = vec.get(1);
		v[2] = vec.get(2);
	}

	public void sub(final Vec3f vec) {
		v[0] = v[0] - vec.get(0);
		v[1] = v[1] - vec.get(1);
		v[2] = v[2] - vec.get(2);
	}

	public void sub(final Vec3f vec1, final Vec3f vec2) {
		v[0] = vec1.get(0) - vec2.get(0);
		v[1] = vec1.get(1) - vec2.get(1);
		v[2] = vec1.get(2) - vec2.get(2);
	}

	public void xform(final Matrix3f mat) // math_utils
	{
		final float tmp_v[] = new float[3];
		final float m[][] = new float[3][3];

		mat.getMat(m);
		tmp_v[0] = v[0] * m[0][0] + v[1] * m[0][1] + v[2] * m[0][2];
		tmp_v[1] = v[0] * m[1][0] + v[1] * m[1][1] + v[2] * m[1][2];
		tmp_v[2] = v[0] * m[2][0] + v[1] * m[2][1] + v[2] * m[2][2];
		set(tmp_v);
	}

	public void xform(final Matrix3f mat, final Vec3f vec) // math_utils
	{
		final float tmp_v[] = new float[3];
		final float m[][] = new float[3][3];

		vec.get(tmp_v);
		mat.getMat(m);
		v[0] = tmp_v[0] * m[0][0] + tmp_v[1] * m[0][1] + tmp_v[2] * m[0][2];
		v[1] = tmp_v[0] * m[1][0] + tmp_v[1] * m[1][1] + tmp_v[2] * m[1][2];
		v[2] = tmp_v[0] * m[2][0] + tmp_v[1] * m[2][1] + tmp_v[2] * m[2][2];
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
