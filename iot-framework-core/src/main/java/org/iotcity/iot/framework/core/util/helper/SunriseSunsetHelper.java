package org.iotcity.iot.framework.core.util.helper;

import java.util.TimeZone;

/**
 * Sunrise and sunset util
 * @author Ardon
 */
public class SunriseSunsetHelper {

	// --------------------------- Public fields ----------------------------

	/**
	 * Milliseconds per minute
	 */
	public static final long MINUTE_MS = 60 * 1000;
	/**
	 * Milliseconds per hour
	 */
	public static final long HOUR_MS = 60 * MINUTE_MS;
	/**
	 * Milliseconds per day
	 */
	public static final long DAY_MS = 24 * HOUR_MS;
	/**
	 * Milliseconds per month (31 days)
	 */
	public static final long MONTH_MS = 31 * DAY_MS;

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets sunrise and sunset time
	 * @param timeZone The time zone
	 * @param lat Latitude
	 * @param lng Longitude
	 * @param year Current year
	 * @param month Current month (starting at 1)
	 * @param day Current day (starting at 1)
	 * @return SunriseSunset sunrise and sunset result
	 */
	public static SunriseSunset getSunriseSunset(String timeZone, double lat, double lng, int year, int month, int day) {
		TimeZone tz = null;
		if (timeZone == null || timeZone.length() == 0) {
			tz = TimeZone.getDefault();
		} else {
			if (timeZone.startsWith("UTC")) {
				tz = TimeZone.getTimeZone("GMT" + timeZone.substring(3));
			} else {
				tz = TimeZone.getTimeZone(timeZone);
			}
		}
		if (month <= 2) {
			month = month + 12;
			year = year - 1;
		}
		double a = 10000.0 * year + 100.0 * month + day;
		double b;
		if (a <= 15821004.1) {
			b = -2 * Math.floor((year + 4716) / 4) - 1179;
		} else {
			b = Math.floor(year / 400) - Math.floor(year / 100) + Math.floor(year / 4);
		}
		a = 365.0 * year - 679004.0;
		double mjday = (a + b + Math.floor(30.6001 * (month + 1)) + day);
		double tzh = tz.getRawOffset() / HOUR_MS;
		return _calRiseSet(mjday, tzh, lng, lat);
	}

	private static double[] _minisun(double t) {
		double p2 = 6.283185307, coseps = 0.91748, sineps = 0.39778;
		double L, M, DL, SL, X, Y, Z, RHO, ra, dec;
		double[] suneq = new double[2];
		M = p2 * _frac(0.993133 + 99.997361 * t);
		DL = 6893.0 * Math.sin(M) + 72.0 * Math.sin(2 * M);
		L = p2 * _frac(0.7859453 + M / p2 + (6191.2 * t + DL) / 1296000);
		SL = Math.sin(L);
		X = Math.cos(L);
		Y = coseps * SL;
		Z = sineps * SL;
		RHO = Math.sqrt(1 - Z * Z);
		dec = (360.0 / p2) * Math.atan(Z / RHO);
		ra = (48.0 / p2) * Math.atan(Y / (X + RHO));
		if (ra < 0) ra += 24;
		suneq[0] = dec;
		suneq[1] = ra;
		return suneq;
	}

	private static double _frac(double x) {
		double a;
		a = x - Math.floor(x);
		if (a < 0) a += 1;
		return a;
	}

	private static double _lmst(double mjday, double glong) {
		double lst, t, d;
		d = mjday - 51544.5;
		t = d / 36525.0;
		lst = _range(280.46061837 + 360.98564736629 * d + 0.000387933 * t * t - t * t * t / 38710000);
		return (lst / 15.0 + glong / 15);
	}

	private static double _range(double x) {
		double a, b;
		b = x / 360;
		a = 360 * (b - _ipart(b));
		if (a < 0) {
			a = a + 360;
		}
		return a;
	}

	private static double _ipart(double x) {
		double a;
		if (x > 0) {
			a = Math.floor(x);
		} else {
			a = Math.ceil(x);
		}
		return a;
	}

	private static double _sinAlt(double mjd0, double hour, double glong, double cglat, double sglat) {
		double mjday, t, ra, dec, tau, salt, rads = 0.0174532925;
		mjday = mjd0 + hour / 24.0;
		t = (mjday - 51544.5) / 36525.0;
		double[] objpos = _minisun(t);
		ra = objpos[1];
		dec = objpos[0];
		tau = 15.0 * (_lmst(mjday, glong) - ra);
		salt = sglat * Math.sin(rads * dec) + cglat * Math.cos(rads * dec) * Math.cos(rads * tau);
		return salt;
	}

	private static double[] _quad(double ym, double yz, double yp) {
		double nz, a, b, c, dis, dx, xe, ye, z1 = 0.0, z2 = 0.0;
		nz = 0;
		a = 0.5 * (ym + yp) - yz;
		b = 0.5 * (yp - ym);
		c = yz;
		xe = -b / (2 * a);
		ye = (a * xe + b) * xe + c;
		dis = b * b - 4.0 * a * c;
		if (dis > 0) {
			dx = 0.5 * Math.sqrt(dis) / Math.abs(a);
			z1 = xe - dx;
			z2 = xe + dx;
			if (Math.abs(z1) <= 1.0) nz += 1;
			if (Math.abs(z2) <= 1.0) nz += 1;
			if (z1 < -1.0) z1 = z2;
		}
		double[] quadout = new double[5];
		quadout[0] = nz;
		quadout[1] = z1;
		quadout[2] = z2;
		quadout[3] = xe;
		quadout[4] = ye;
		return quadout;
	}

	private static double _getZTTime(double mjday, double tz, double glong) {
		double date, ym, yz, utrise, utset, sinho;
		double yp, nz, hour, z1, z2, ye, rads = 0.0174532925;
		sinho = Math.sin(rads * -0.833);
		date = mjday - tz / 24;
		hour = 1.0;
		utrise = 0.0;
		utset = 0.0;
		ym = _sinAlt(date, hour - 1.0, glong, 1, 0) - sinho;
		while (hour < 25) {
			yz = _sinAlt(date, hour, glong, 1, 0) - sinho;
			yp = _sinAlt(date, hour + 1.0, glong, 1, 0) - sinho;
			double[] quadout = _quad(ym, yz, yp);
			nz = quadout[0];
			z1 = quadout[1];
			z2 = quadout[2];
			ye = quadout[4];
			if (nz == 1) {
				if (ym < 0.0)
					utrise = hour + z1;
				else
					utset = hour + z1;
			}
			if (nz == 2) {
				if (ye < 0.0) {
					utrise = hour + z2;
					utset = hour + z1;
				} else {
					utrise = hour + z1;
					utset = hour + z2;
				}
			}
			ym = yp;
			hour += 2.0;
		}
		double zt = (utrise + utset) / 2;
		if (zt < utrise) zt = (zt + 12) % 24;
		return zt;
	}

	private static int _getHourMinutes(double hours) {
		return Double.valueOf(Math.floor(hours * 60 + 0.5)).intValue();
	}

	private static SunriseSunset _calRiseSet(double mjday, double tz, double glong, double glat) {
		double sinho, cglat, sglat, date, ym, yz, utrise, utset, yp, nz, hour, z1, z2, ye, rads = 0.0174532925;
		boolean above = false, rise = false, sett = false;
		sinho = Math.sin(rads * -0.833);
		sglat = Math.sin(rads * glat);
		cglat = Math.cos(rads * glat);
		date = mjday - tz / 24;
		hour = 1.0;
		utrise = 0.0;
		utset = 0.0;
		ym = _sinAlt(date, hour - 1.0, glong, cglat, sglat) - sinho;
		if (ym > 0.0) above = true;
		while (hour < 25 && (!sett || !rise)) {
			yz = _sinAlt(date, hour, glong, cglat, sglat) - sinho;
			yp = _sinAlt(date, hour + 1.0, glong, cglat, sglat) - sinho;
			double[] quadout = _quad(ym, yz, yp);
			nz = quadout[0];
			z1 = quadout[1];
			z2 = quadout[2];
			ye = quadout[4];
			if (nz == 1) {
				if (ym < 0.0) {
					utrise = hour + z1;
					rise = true;
				} else {
					utset = hour + z1;
					sett = true;
				}
			}
			if (nz == 2) {
				if (ye < 0.0) {
					utrise = hour + z2;
					utset = hour + z1;
				} else {
					utrise = hour + z1;
					utset = hour + z2;
				}
			}
			ym = yp;
			hour += 2.0;
		}
		int sunRise = -1;
		int sunSet = -1;
		int sunCenter = -1;
		if (rise || sett) {
			if (rise == true) {
				sunRise = _getHourMinutes(utrise);
			}
			double zt = _getZTTime(mjday, tz, glong);
			if (zt > 0) {
				sunCenter = _getHourMinutes(zt);
			}
			if (sett == true) {
				sunSet = _getHourMinutes(utset);
			}
		} else {
			sunCenter = above ? _getHourMinutes(_getZTTime(mjday, tz, glong)) : -1;
		}
		return new SunriseSunsetHelper().new SunriseSunset(sunRise, sunSet, sunCenter);
	}

	/**
	 * The sunrise and sunset data
	 * @author Ardon
	 */
	public class SunriseSunset {

		/**
		 * (Readonly) Sunrise time (relative to 0:00, - 1 means that the sun never rise)
		 */
		public final int sunRise;
		/**
		 * (Readonly) Sunset time (relative to 0:00, -1 means that the sun never sets)
		 */
		public final int sunSet;
		/**
		 * (Readonly) Midday time (relative to 0:00, - 1 means that the day does not come out)
		 */
		public final int sunCenter;

		/**
		 * Constructor for sunrise and sunset time
		 * @param sunRise Sunrise time
		 * @param sunSet Sunset time
		 * @param sunCenter Midday time
		 */
		public SunriseSunset(int sunRise, int sunSet, int sunCenter) {
			this.sunRise = sunRise;
			this.sunSet = sunSet;
			this.sunCenter = sunCenter;
		}

	}

}
