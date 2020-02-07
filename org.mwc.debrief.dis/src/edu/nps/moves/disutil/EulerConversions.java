package edu.nps.moves.disutil;

/**
 * Class contains methods that convert to Tait_Bryan_angles (i.e., roll, pitch
 * and yaw/heading) given the position (i.e., latitude, longitude) and the euler
 * angles (i.e., psi, theta, and phi).
 *
 * Class also has methods for the corollary: converting to psi, theta, and phi
 * given the lat/lon position and the entity's roll, pitch and yaw angles
 *
 * In this class roll, pitch and yaw are always expressed in degrees whereas
 * psi, theta, and phi are always in radians.
 *
 * Note: latitude and longitude are also expressed in radians.
 *
 *
 * @author loyaj & bhughes
 *
 */

public class EulerConversions {

	static double _toDegrees = 57.2957795131;
	static double _toRadians = 0.01745329252;

	/**
	 * Gets a degree heading for an entity based on euler angles. All angular values
	 * passed in must be in radians.
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param psi   Psi angle, IN RADIANS
	 * @param theta Theta angle, IN RADIANS
	 * @return the heading, in degrees, with 0 being north, positive angles going
	 *         clockwise, and negative angles going counterclockwise (i.e., 90 deg
	 *         is east, -90 is west)
	 */
	public static double getOrientationFromEuler(final double lat, final double lon, final double psi,
			final double theta) {
		final double sinlat = Math.sin(lat);
		final double sinlon = Math.sin(lon);
		final double coslon = Math.cos(lon);
		final double coslat = Math.cos(lat);
		final double sinsin = sinlat * sinlon;

		final double cosTheta = Math.cos(theta);
		final double cosPsi = Math.cos(psi);
		final double sinPsi = Math.sin(psi);
		final double sinTheta = Math.sin(theta);

		final double cosThetaCosPsi = cosTheta * cosPsi;
		final double cosThetaSinPsi = cosTheta * sinPsi;
		final double sincos = sinlat * coslon;

		final double b11 = -sinlon * cosThetaCosPsi + coslon * cosThetaSinPsi;
		final double b12 = -sincos * cosThetaCosPsi - sinsin * cosThetaSinPsi - coslat * sinTheta;

		return Math.toDegrees(Math.atan2(b11, b12));// range is -pi to pi
	}

	/**
	 * Gets the Euler Phi value (in radians) from position and Tait-Brayn yaw, pitch
	 * and roll angles
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param yaw   yaw angle (also know as the entity's bearing or heading angle),
	 *              in degrees
	 * @param pitch entity's pitch angle, in degrees
	 * @param roll  entity's roll angle (0 is level flight, + roll is clockwise
	 *              looking out the nose), in degrees
	 * @return the Phi value in radians
	 */
	public static double getPhiFromTaitBryanAngles(final double lat, final double lon, final double yaw,
			final double pitch, final double roll) {

		final double sinLat = Math.sin(lat);
		final double cosLat = Math.cos(lat);

		final double cosRoll = Math.cos(roll * _toRadians);
		final double sinRoll = Math.sin(roll * _toRadians);
		final double cosPitch = Math.cos(pitch * _toRadians);
		final double sinPitch = Math.sin(pitch * _toRadians);
		final double sinYaw = Math.sin(yaw * _toRadians);
		final double cosYaw = Math.cos(yaw * _toRadians);

		final double a_23 = cosLat * (-sinYaw * cosRoll + cosYaw * sinPitch * sinRoll) - sinLat * cosPitch * sinRoll;
		final double a_33 = cosLat * (sinYaw * sinRoll + cosYaw * sinPitch * cosRoll) - sinLat * cosPitch * cosRoll;

		return Math.atan2(a_23, a_33);
	}

	/**
	 * Gets a degree pitch for an entity based on euler angles. All angular values
	 * passed in must be in radians.
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param psi   Psi angle, IN RADIANS
	 * @param theta Theta angle, IN RADIANS
	 * @return the pitch, in degrees, with 0 being level. A negative values is when
	 *         the entity's nose is pointing downward, positive value is when the
	 *         entity's nose is pointing upward.
	 */
	public static double getPitchFromEuler(final double lat, final double lon, final double psi, final double theta) {
		final double sinlat = Math.sin(lat);
		final double sinlon = Math.sin(lon);
		final double coslon = Math.cos(lon);
		final double coslat = Math.cos(lat);
		final double cosLatCosLon = coslat * coslon;
		final double cosLatSinLon = coslat * sinlon;

		final double cosTheta = Math.cos(theta);
		final double cosPsi = Math.cos(psi);
		final double sinPsi = Math.sin(psi);
		final double sinTheta = Math.sin(theta);

		return Math.toDegrees(
				Math.asin(cosLatCosLon * cosTheta * cosPsi + cosLatSinLon * cosTheta * sinPsi - sinlat * sinTheta));
	}

	/**
	 * Gets the Euler Psi value (in radians) from position and Tait-Brayn yaw and
	 * roll angles
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param yaw   ettity's yaw angle (also know as the entity's bearing or heading
	 *              angle), in degrees
	 * @param pitch entity's pitch angle, in degrees
	 * @return the Psi value in radians
	 */
	public static double getPsiFromTaitBryanAngles(final double lat, final double lon, final double yaw,
			final double pitch) {

		final double sinLat = Math.sin(lat);
		final double sinLon = Math.sin(lon);
		final double cosLon = Math.cos(lon);
		final double cosLat = Math.cos(lat);
		final double cosLatCosLon = cosLat * cosLon;
		final double cosLatSinLon = cosLat * sinLon;
		final double sinLatCosLon = sinLat * cosLon;
		final double sinLatSinLon = sinLat * sinLon;

		final double cosPitch = Math.cos(pitch * _toRadians);
		final double sinPitch = Math.sin(pitch * _toRadians);
		final double sinYaw = Math.sin(yaw * _toRadians);
		final double cosYaw = Math.cos(yaw * _toRadians);

		final double a_11 = -sinLon * sinYaw * cosPitch - sinLatCosLon * cosYaw * cosPitch + cosLatCosLon * sinPitch;
		final double a_12 = cosLon * sinYaw * cosPitch - sinLatSinLon * cosYaw * cosPitch + cosLatSinLon * sinPitch;

		return Math.atan2(a_12, a_11);
	}

	/**
	 * Gets the degree roll for an entity based on euler angles. All angular values
	 * passed in must be in radians.
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param psi   Psi angle, IN RADIANS
	 * @param theta Theta angle, IN RADIANS
	 * @param phi   Phi angle, IN RADIANS
	 * @return the roll, in degrees, with 0 being level flight, + roll is clockwise
	 *         when looking out the front of the entity.
	 */
	public static double getRollFromEuler(final double lat, final double lon, final double psi, final double theta,
			final double phi) {
		final double sinlat = Math.sin(lat);
		final double sinlon = Math.sin(lon);
		final double coslon = Math.cos(lon);
		final double coslat = Math.cos(lat);
		final double cosLatCosLon = coslat * coslon;
		final double cosLatSinLon = coslat * sinlon;

		final double cosTheta = Math.cos(theta);
		final double sinTheta = Math.sin(theta);
		final double cosPsi = Math.cos(psi);
		final double sinPsi = Math.sin(psi);
		final double sinPhi = Math.sin(phi);
		final double cosPhi = Math.cos(phi);

		final double sinPhiSinTheta = sinPhi * sinTheta;
		final double cosPhiSinTheta = cosPhi * sinTheta;

		final double b23 = cosLatCosLon * (-cosPhi * sinPsi + sinPhiSinTheta * cosPsi)
				+ cosLatSinLon * (cosPhi * cosPsi + sinPhiSinTheta * sinPsi) + sinlat * (sinPhi * cosTheta);

		final double b33 = cosLatCosLon * (sinPhi * sinPsi + cosPhiSinTheta * cosPsi)
				+ cosLatSinLon * (-sinPhi * cosPsi + cosPhiSinTheta * sinPsi) + sinlat * (cosPhi * cosTheta);

		return Math.toDegrees(Math.atan2(-b23, -b33));
	}

	/**
	 * Gets the Euler Theta value (in radians) from position and Tait-Brayn yaw and
	 * roll angles
	 *
	 * @param lat   Entity's latitude, IN RADIANS
	 * @param lon   Entity's longitude, IN RADIANS
	 * @param yaw   entity's yaw angle (also know as the entity's bearing or heading
	 *              angle), in degrees
	 * @param pitch entity's pitch angle, in degrees
	 * @return the Theta value in radians
	 */
	public static double getThetaFromTaitBryanAngles(final double lat, final double lon, final double yaw,
			final double pitch) {
		final double sinLat = Math.sin(lat);
		final double cosLat = Math.cos(lat);

		final double cosPitch = Math.cos(pitch * _toRadians);
		final double sinPitch = Math.sin(pitch * _toRadians);
		final double cosYaw = Math.cos(yaw * _toRadians);

		return Math.asin(-cosLat * cosYaw * cosPitch - sinLat * sinPitch);
	}

}
