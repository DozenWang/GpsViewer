package com.ledongli.util;

public class GeomTransform {
	private double gauss_X, gauss_Y;
	private double earth_Long, earth_Lat;
	private double center_L;
	private double dRa = 6378245.0;
	private double dE1 = 8.1813332e-2;
	private double dE2 = 8.2088520e-2;


	public GeomTransform()
	{
	}

	public double getDistance(double x1, double y1, double x2, double y2)
	{
		setCenterL( (x1 + x2) / 2);

		double nRes = 0;

		try
		{
			double X1 = getGaussX(x1, y1);
			double Y1 = getGaussY(x1, y1);
			double X2 = getGaussX(x2, y2);
			double Y2 = getGaussY(x2, y2);

			double x = (X1 - X2);
			x *= x;
			double y = Y1 - Y2;
			y *= y;

			nRes = Math.sqrt(x + y);
		}
		catch (Exception ex)
		{
		}

		return nRes;
	}

	public void setCenterL(double centerL)
	{
		center_L = centerL;
	}
	public double getLong(double GaussX, double GaussY) throws Exception
	{
		double e2, e12, tmp, bf, nf, Nf, tf, fa, yNf;
		double cosBf;

		e2 = dE1 * dE1;
		e12 = dE2 * dE2;

		fa = 0.1570460641219e-6 * GaussX;
		bf = fa + 0.25184647783e-2 * Math.sin(2.0 * fa) +
			 0.36998873e-5 * Math.sin(4.0 * fa) +
			 0.74449e-8 * Math.sin(6.0 * fa) + 0.1828e-10 * Math.sin(8.0 * fa);

		tf = Math.sin(bf);
		cosBf = Math.cos(bf);
		if (cosBf + 1.0 == 1.0)
		{
			earth_Long = 0.0;

			if (earth_Long < 0 || earth_Long > 180)
			{
				throw new Exception(
					"com.pdager.pubclass.geoTransform.TranException\n" +
					"                at com.pdager.pubclass.geoTransform.getLong(geoTransform.java:101): invalid GaussX or GaussY");
			}

			return earth_Long;
		}
		Nf = Math.sqrt(1.0 - e2 * tf * tf);
		Nf = dRa / Nf;
		tf = tf / cosBf;
		nf = Math.sqrt(e12) * cosBf;

		yNf = GaussY / Nf;
		tmp = 1.0 -
			  yNf * yNf * (5.0 + 3.0 * tf * tf + nf * nf - 9.0 * tf * tf * nf * nf) /
			  12.0;
		tmp = tmp +
			  Math.pow(yNf, 4.0) * (61.0 + 90.0 * tf * tf + 45.0 * Math.pow(tf, 4.0)) /
			  360.0;
		tmp = tmp * yNf * yNf * (1.0 + nf * nf) * tf / 2.0;

		tmp = 1.0 - yNf * yNf * (1.0 + 2.0 * tf * tf + nf * nf) / 6.0;
		tmp = tmp +
			  Math.pow(yNf, 4.0) * (5.0 + 28.0 * tf * tf + 24.0 * Math.pow(tf, 4.0) +
									6.0 * nf * nf + 8.0 * nf * nf * tf * tf) / 120.0;
		earth_Long = (GaussY / (Nf * cosBf) * tmp) * 180.0 / 3.14159265;
		earth_Long += center_L;

		if (earth_Long < 0 || earth_Long > 180)
		{
			throw new Exception("com.pdager.pubclass.geoTransform.TranException\n" +
									"                at com.pdager.pubclass.geoTransform.getLong(geoTransform.java:101): invalid GaussX or GaussY");
		}

		return earth_Long;
	}

	public double getLat(double GaussX, double GaussY) throws Exception
	{
		double e2, e12, tmp, bf, nf, Nf, tf, fa, yNf;
		double cosBf;

		e2 = dE1 * dE1;
		e12 = dE2 * dE2;

		fa = 0.1570460641219e-6 * GaussX;
		bf = fa + 0.25184647783e-2 * Math.sin(2.0 * fa) +
			 0.36998873e-5 * Math.sin(4.0 * fa) +
			 0.74449e-8 * Math.sin(6.0 * fa) + 0.1828e-10 * Math.sin(8.0 * fa);

		tf = Math.sin(bf);
		cosBf = Math.cos(bf);
		if (cosBf + 1.0 == 1.0)
		{
			if (tf + 1.0 > 1.0)
			{
				earth_Lat = 90.0;
			}
			else
			{
				earth_Lat = -90.0;

			}
			if (earth_Lat < -90 || earth_Lat > 90)
			{
				throw new Exception(
					"com.pdager.pubclass.geoTransform.TranException\n" +
					"                at com.pdager.pubclass.geoTransform.getLat(geoTransform.java:154): invalid GaussX or GaussY");
			}

			return earth_Lat;
		}
		Nf = Math.sqrt(1.0 - e2 * tf * tf);
		Nf = dRa / Nf;
		tf = tf / cosBf;
		nf = Math.sqrt(e12) * cosBf;

		yNf = GaussY / Nf;
		tmp = 1.0 -
			  yNf * yNf * (5.0 + 3.0 * tf * tf + nf * nf - 9.0 * tf * tf * nf * nf) /
			  12.0;
		tmp = tmp +
			  Math.pow(yNf, 4.0) * (61.0 + 90.0 * tf * tf + 45.0 * Math.pow(tf, 4.0)) /
			  360.0;
		tmp = tmp * yNf * yNf * (1.0 + nf * nf) * tf / 2.0;
		earth_Lat = (bf - tmp) * 180.0 / 3.14159265;

		if (earth_Lat < -90 || earth_Lat > 90)
		{
			throw new Exception("com.pdager.pubclass.geoTransform.TranException\n" +
									"                at com.pdager.pubclass.geoTransform.getLat(geoTransform.java:154): invalid GaussX or GaussY");
		}

		return earth_Lat;
	}

	public double getGaussX(double Long, double Lat) throws Exception
	{
		if (Long > 180 || Long < 0 || Lat < -90 || Lat > 90)
		{
			throw new Exception("com.pdager.pubclass.geoTransform.TranException\n" +
									"                at com.pdager.pubclass.geoTransform.getGaussX(geoTransform.java:205): invalid longitude or latitude");
		}

		double ddL = Long - center_L;
		double E1, dSm, dN, AT, dTmp;
		double cosB, sinB, cosB2, sinB2;
		E1 = dE2 * dE2;
		Lat = Lat * 3.141592654 / 180.0;
		ddL = ddL * 3.141592654 / 180.0;
		sinB = Math.sin(Lat);
		cosB = Math.cos(Lat);
		sinB2 = sinB * sinB;
		cosB2 = 1.0 - sinB2;
		dN = dRa * cosB / Math.sqrt(1 - dE1 * dE1 * sinB2);
		dSm = 6367558.4971 * Lat - 16036.4803 * Math.sin(Lat * 2) +
			  16.8281 * Math.sin(Lat * 4) -
			  0.02198 * Math.sin(Lat * 6) + 0.000031 * Math.sin(Lat * 8);
		AT = E1 * cosB2;
		dTmp = 5.0 * cosB2 - sinB2 + AT * (9.0 + 4.0 * AT) * cosB2 +
			   ddL * ddL * (61.0 * cosB2 * cosB2 -
							sinB2 * (58.0 * cosB2 - sinB2)) / 30.0;
		gauss_X = dN * ddL * ddL * ddL * ddL / 24.0 * dTmp;
		gauss_X = gauss_X * sinB + dSm + dN * sinB * ddL * ddL / 2.0;

		return gauss_X;
	}

	public double getGaussY(double Long, double Lat) throws Exception
	{

		if (Long > 180 || Long < 0 || Lat < -90 || Lat > 90)
		{
			throw new Exception("com.pdager.pubclass.geoTransform.TranException\n" +
									"                at com.pdager.pubclass.geoTransform.getGaussX(geoTransform.java:205): invalid longitude or latitude");
		}

		double ddL = Long - center_L;
		@SuppressWarnings("unused")
		double E1, dSm, dN, AT, dTmp;
		double cosB, sinB, cosB2, sinB2;
		E1 = dE2 * dE2;
		Lat = Lat * 3.141592654 / 180.0;
		ddL = ddL * 3.141592654 / 180.0;
		sinB = Math.sin(Lat);
		cosB = Math.cos(Lat);
		sinB2 = sinB * sinB;
		cosB2 = 1.0 - sinB2;
		dN = dRa * cosB / Math.sqrt(1 - dE1 * dE1 * sinB2);
		dSm = 6367558.4971 * Lat - 16036.4803 * Math.sin(Lat * 2) +
			  16.8281 * Math.sin(Lat * 4) -
			  0.02198 * Math.sin(Lat * 6) + 0.000031 * Math.sin(Lat * 8);
		AT = E1 * cosB2;

		dTmp = cosB2 - sinB2 + AT * cosB2 + ddL * ddL / 20.0 * (5.0 * cosB2 * cosB2 -
			sinB2 * ( (18.0 + 58.0 * AT) * cosB2 - sinB2) +
			14.0 * AT * cosB2 * cosB2);
		gauss_Y = dN * ddL * ddL * ddL / 6.0 * dTmp;
		gauss_Y = gauss_Y + dN * ddL;
		return gauss_Y;
	}
}
