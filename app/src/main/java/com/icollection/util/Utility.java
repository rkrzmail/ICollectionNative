package com.icollection.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.icollection.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;


public class Utility {
	private static Context defContext;
	private static Context sevContext;

	public static int parseVersion(String s) {
		s = s.trim();
		if (s.contains(" ")) {
			s = s.substring(0, s.indexOf(" "));
		}
		try {
			String[] ver = Utility.split(s + "..", ".");
			int i = Utility.getInt(ver[0]) * 1000000 + Utility.getInt(ver[1]) * 1000 + Utility.getInt(ver[2]);
			return i;
		} catch (Exception e) {
		}

		return 0;
	}
	public static String getHttpConnectionPost(String stringURL, Map args) {
		// TODO Auto-generated method stub
		URL object;
		try {
			object = new URL(stringURL);
			HttpsURLConnection urlConnection;
			HttpURLConnection con;
			try {
				con = (HttpURLConnection) object.openConnection();

				con.setDoOutput(true);
				con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("POST");

				StringBuilder builder = new StringBuilder();
				List<String> keys = new ArrayList<String>();
					Iterator iterator = args.keySet().iterator();
					while (iterator.hasNext()) {
						String key = String.valueOf(iterator.next()) ;
						keys.add(key);
					}

				for (int i = 0; i < keys.size(); i++) {
					String key = keys.get(i);
					if (i > 0){
						builder.append("&");
					}
					builder.append((Utility.urlEncode(key)+"="));
					builder.append(Utility.urlEncode( String.valueOf(args.get(key)) ));
				}


				OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
				wr.write(builder.toString());
				wr.flush();
				wr.close();

				//display what returns the POST request

				StringBuilder sb = new StringBuilder();
				int HttpResult = con.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(con.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();

					return sb.toString();
				} else {
					return null;
				}
			} catch (IOException e) {

				return null;
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return null;
		}

	}

	public static String getHttpConnectionX(String stringURL) {
		URL object;
		try {
			object = new URL(stringURL);

			HttpURLConnection con;
			try {

 				con = (HttpURLConnection) object.openConnection();

				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("GET");
				con.setConnectTimeout(30000);


				//display what returns the POST request

				StringBuilder sb = new StringBuilder();
				int HttpResult = con.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {

					BufferedReader br = new BufferedReader(
							new InputStreamReader(con.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();

					return sb.toString();
				} else {
					System.out.println(con.getResponseMessage());
				}
			} catch (IOException e) {
				return e.getMessage();
			} catch (Exception e) {
				return e.getMessage();
			}

		} catch (MalformedURLException e) {
			return e.getMessage();
		}
		return "";
	}
	public interface NikitaParse {
		public Object getVirtual(String name);
	}

	public static String nhtmlNikitaParse(String html, NikitaParse np) {
		StringBuilder sb = new StringBuilder();
		StringTokenizer stringTokenizer = new StringTokenizer(html, "{}", true);
		while (stringTokenizer.hasMoreElements()) {
			String nhtml = String.valueOf(stringTokenizer.nextElement());
			String vArgs = nhtml.trim();
			if ((vArgs.startsWith("@") || vArgs.startsWith("$") || vArgs.startsWith("!"))) {
				if ((vArgs.contains("(") || vArgs.contains(")") || vArgs.contains("[") || vArgs.contains("]"))) {
					if (vArgs.endsWith(")") || vArgs.endsWith("]")) {
						vArgs = "";
					} else {
						//salah
						vArgs = "error";
					}
				} else {
					vArgs = "";
				}
				if (vArgs.equals("") && sb.toString().endsWith("{")) {
					if (stringTokenizer.hasMoreElements()) {
						String vhtml = String.valueOf(stringTokenizer.nextElement());
						if (vhtml.equals("}")) {
							if (np != null) {
								sb.deleteCharAt(sb.length() - 1);
								sb.append(String.valueOf(np.getVirtual(nhtml)));
							} else {
								sb.append("");
							}

						} else {
							sb.append(nhtml);
							sb.append(vhtml);
						}
					} else {
						sb.append(nhtml);
					}
				} else {
					sb.append(nhtml);
				}
			} else {
				sb.append(nhtml);
			}
		}
		return sb.toString();
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String readInputStreamAsString(String inputStreamPath) throws IOException {
		//InputStream inputStream = new DataInputStream(new FileInputStream(inputStreamPath));
		return readInputStreamAsString(new FileInputStream(inputStreamPath));
	}

	public static String readInputStreamAsString(InputStream inputStream) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		inputStream.close();
		return baos.toString();
	}

	public static Boolean isTablet(Context context) {
		if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			return true;
		}
		return false;
	}

	public static String[] getDataArray(Vector nodes) {
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++) {
				result[loop] = String.valueOf(nodes.elementAt(loop));
			}
		}
		return result;
	}

	public static String[] getObjectKeys(Hashtable masterdata) {
		if (masterdata instanceof Hashtable) {
			Enumeration hdata = ((Hashtable) masterdata).keys();
			int i = 0;
			while (hdata.hasMoreElements()) {
				hdata.nextElement();
				i++;
			}
			hdata = ((Hashtable) masterdata).keys();
			String[] rString = new String[i];
			i = 0;
			while (hdata.hasMoreElements()) {
				rString[i] = (String) hdata.nextElement();
				i++;
			}
			return rString;
		}
		return new String[]{};
	}

	public static double getDisplaySizeInchi(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;

		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);

		return Math.sqrt(x + y);
	}

	public static String getNumberPointOnly(String s) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if ("01234567890.".indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		return buf.toString();
	}

	public static String getNumberOnly(String s) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if ("01234567890".indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		return buf.toString();
	}

	public static int getNumberOnlyInt(String s) {
		return getInt(getNumberOnly(s));
	}

	public static int getInt(String s) {
		return getNumber(s).intValue();
	}

	public static long getLong(String s) {
		return getNumber(s).longValue();
	}

	public static double getDouble(Object n) {
		return getNumber(n).doubleValue();
	}

	public static float getFloat(String s) {
		return getNumber(s).floatValue();
	}

	public static Number getNumber(Object n) {
		if (n instanceof Number) {
			return ((Number) n);

		} else if (isDecimalNumber(String.valueOf(n))) {
			try {
				return Double.valueOf(String.valueOf(n));
			} catch (Exception e) {
			}

		} else if (isLongIntegerNumber(String.valueOf(n))) {
			try {
				return Long.valueOf(String.valueOf(n));
			} catch (Exception e) {
			}

		}
		return 0;
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

	public static boolean isDecimalNumber(String str) {
		return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
	}

	public static boolean isLongIntegerNumber(String str) {
		return str.matches("-?\\d+");
	}


	public static MarginLayoutParams getMarginLayoutParams(View v) {
		return getMarginLayoutParams(v, MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
	}

	public static MarginLayoutParams getMarginLayoutParams(View v, int width, int heigh) {
		if (v.getLayoutParams() instanceof MarginLayoutParams) {
			MarginLayoutParams marginLayoutParams = (MarginLayoutParams) v.getLayoutParams();
			return marginLayoutParams;
		} else if (v.getLayoutParams() == null) {
			return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
		} else {
			LayoutParams marginLayoutParams = v.getLayoutParams();
			return new MarginLayoutParams(marginLayoutParams.width, marginLayoutParams.height);
		}
	}

	public static float ConvertPxtoDp(Context context, float px) {
		return px / context.getResources().getDisplayMetrics().density;
	}

	public static float ConvertDptoPx(Context context, float dp) {
		return dp * context.getResources().getDisplayMetrics().density;
	}

	public static int convertDptoPx(Context context, int dp) {
		return new Float(dp * context.getResources().getDisplayMetrics().density).intValue();
	}

	public static int convertPixel(Context context, String val) {
		val = val.trim().toLowerCase();
		if (val.endsWith("dp") || val.endsWith("sp")) {
			return Math.round(Utility.ConvertDptoPx(context, Utility.getNumberOnlyInt(val)));
		} else if (val.endsWith("px")) {
			return Utility.getNumberOnlyInt(val);
		} else {
			return Utility.getInt(val);
		}
	}

	public static String replace(String _text, String _searchStr, String _replacementStr) {
		StringBuffer sb = new StringBuffer();
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}
		sb.append(_text.substring(startPos, _text.length()));
		return sb.toString();
	}

	public static long getDateTime(String date) {
		if (isNumeric(date)) {
			return getLong(date);
		}
		try {
			//dd/mm/yyyy|dd-mm-yyyy|yyyy-mm-dd
			String sd = "-";
			String time = "";
			if (date.contains(".")) {
				date = date.substring(0, date.indexOf("."));
			}
			if (date.contains(":") && date.length() >= 18) {
				time = " HH:mm:ss";
			}
			if (date.contains("-")) {
				sd = "-";
			} else if (date.contains("/")) {
				sd = "/";
			}
			if (date.length() >= 10) {
				if (isNumeric(date.substring(0, 4))) {
					//yyyy-mm-dd
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy" + sd + "MM" + sd + "dd" + time);
					return simpleDateFormat.parse(date).getTime();
				} else if (isNumeric(date.substring(6, 10))) {
					//dd/mm/yyyy
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd" + sd + "MM" + sd + "yyyy" + time);
					return simpleDateFormat.parse(date).getTime();
				}
			} else {
				//???
			}
		} catch (Exception e) {
		}
		return 0;
	}

	public static long getTime(String time) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			return simpleDateFormat.parse(time).getTime();
		} catch (Exception e) {
			return 0;
		}
	}

	public static long getDate(String date) {
		if (isNumeric(date)) {
			return getLong(date);
		}
		if (date.trim().length() >= 10) {
			return getDateTime(date.trim().substring(0, 10));
		}
		return getDateTime(date);
	}

	public static String formatDate(long currdate, String format) {
		return new SimpleDateFormat(format).format(new Date(currdate));
	}

	public static Context getAppContext() {
		return defContext != null ? defContext : AppController.getInstanceA();
	}

	private static void setServContext(Context appContext) {
		Utility.sevContext = appContext;
	}

	public static void setAppContext(Context appContext) {
		Utility.defContext = appContext;
	}

	public static String getExternalPath() {
		try {
			if (getAppContext().getExternalFilesDir(null).getPath().length() >= 3) {
				return getAppContext().getExternalFilesDir(null).getPath() + "/";
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDefaultPath() {
		String defpath = getExternalPath();
		if (defpath != null) {
			return defpath;
		}
		return getAppContext().getFilesDir().getPath() + "/";
	}

	public static String getDefaultTempPath(String fname) {
		return getDefaultPath("temp/" + fname);
	}

	public static String getDefaultImagePath(String fname) {
		return getDefaultPath("image/" + fname);
	}

	public static String getDefaultPath(String fname) {
		return getDefaultPath() + fname;
	}

	public static void deleteAllFileOnFolder(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteAllFileFolder(files[i]);
					;
				} else if (files[i].isFile()) {
					files[i].delete();
				}
			}
		}
	}

	public static void deleteAllFileFolder(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteAllFileFolder(files[i]);
					;
				} else if (files[i].isFile()) {
					files[i].delete();
				}
			}
			file.delete();
		} else if (file.isFile()) {
			file.delete();
		}
	}

	public static Vector<Vector<String>> splitVector(String original, String separatorcol, String separatorrow) {
		Vector<Vector<String>> nodes = new Vector<Vector<String>>();
		int index = original.indexOf(separatorrow);
		while (index >= 0) {
			nodes.addElement(splitVector(original.substring(0, index), separatorcol));
			original = original.substring(index + separatorrow.length());
			index = original.indexOf(separatorrow);
		}
		nodes.addElement(splitVector(original, separatorcol));
		return nodes;
	}

	public static List<String> splitList(String original, String separator) {
		List<String> nodes = new ArrayList<String>();
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.add(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		nodes.add(original);
		return nodes;
	}

	public static Vector<String> splitVector(String original, String separator) {
		Vector<String> nodes = new Vector<String>();
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		nodes.addElement(original);
		return nodes;
	}

	public static String[] split(String original, String separator) {
		List<String> nodes = splitList(original, separator);
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++) {
				result[loop] = (String) nodes.get(loop);
			}
		}
		return result;
	}


	public static HttpResponse doGet(String url) {
		try {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

			HttpParams params = new BasicHttpParams();
			params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
			params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);


			params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
			HttpConnectionParams.setConnectionTimeout(params, 0);
			HttpConnectionParams.setSoTimeout(params, 0);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

			ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
			DefaultHttpClient httpClient = new DefaultHttpClient(cm, params);


			HttpGet httpget = new HttpGet(url);
			httpget.setParams(params);
			HttpResponse response;
			response = httpClient.execute(httpget);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}

	private static long iRandom = 1;//System.currentTimeMillis()

	public static String nikitaUrl(String url) {
		iRandom = iRandom + 1;
		StringBuffer result = new StringBuffer();
		StringBuffer addParam = new StringBuffer();
		try {
			PackageInfo pInfo = Utility.getAppContext().getPackageManager().getPackageInfo(Utility.getAppContext().getPackageName(), 0);
			String version = URLEncoder.encode(pInfo.versionName).trim();


			TelephonyManager tm = (TelephonyManager) Utility.getAppContext().getSystemService(android.content.Context.TELEPHONY_SERVICE);

			String imei = "";

				addParam.append("i=").append(imei).append("&u=").append(Utility.getSetting(Utility.getAppContext(), "USERNAME","")).append("&v=").append(version).append("&r=").append(iRandom);
			} catch (Exception e) {
				addParam = new StringBuffer();
				addParam.append("i=").append("imei").append("&u=").append("user").append("&v=").append("versi").append("&r=").append(iRandom);
			}


			if (url.contains("?")) {
				result.append(url.substring(0,url.indexOf("?")+1)).append(addParam.toString()).append("&").append(url.substring(url.indexOf("?")+1));
			}else if (url.contains("&")) {
				result.append(url.substring(0,url.indexOf("&"))).append("?").append(addParam.toString()).append(url.substring(url.indexOf("&")));
			}else{
				result.append(url).append("?").append(addParam.toString());
			}

			return result.toString();
		}
	public static String urlEncode(String stringURL) {
		return URLEncoder.encode(stringURL);
	}


	public static String repeat(String sString, int iTimes) {
		String output = "";
		for (int i = 0; i < iTimes; i++)
			output = output + sString;
		return output;
	}

	@SuppressWarnings("deprecation")
	public static String getURLenc(String... get) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < get.length; i++) {
			buffer.append((i % 2) == 0 ? get[i] : URLEncoder.encode(get[i]));
		}
		return buffer.toString();
	}




	public static int getSettingInt(Context context, String key, int def) {
		return Utility.getInt(getSetting(context, key, def + ""));
	}

	public static String getSetting(Context context, String key, String def) {// baca
																				// data
																				// yang
																				// disimpan(string)
		if (context!=null) {
			SharedPreferences settings = context.getApplicationContext().getSharedPreferences("NikitaMobile", 0);
			String silent = settings.getString(key, def);
			return silent;
		}else{
			return def;
		}
	}
	public static void delSetting(Context context, String key ) {// Simpat
		// data
		// string
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("NikitaMobile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove(key);
		editor.commit();
	}
	public static void setSetting(Context context, String key, String val) {// Simpat
																			// data
																			// string
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("NikitaMobile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, val);
		editor.commit();
	}
	public static void removeSettingAll(Context context) {
		SharedPreferences settings = context.getApplicationContext().getSharedPreferences("NikitaMobile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
	public static byte[] DownloadFromUrl(String surl) {

		try {
			URL url = new URL(surl);
			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			return baf.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	public static final String MD5(final String s) {
		if (s.equals("")) {
			return "";
		}
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}
	public static final String SHA1(final String s) {
        if (!s.equals("")) {
            try {
                MessageDigest digest = java.security.MessageDigest.getInstance("SHA1");
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();

                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++) {
                    hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) { }
        }
        return "";
    }
	public static void openPDF(Context context, String filename) {
		File pdfFile = new File(filename);
		if (pdfFile.exists()) {
			Uri path = Uri.fromFile(pdfFile);
			Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			pdfIntent.setDataAndType(path, "application/pdf");
			pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				context.startActivity(pdfIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static void OpenImage(ImageView context, String path) {
		OpenImage(context, path, 0);
	}

	public static void OpenImage(ImageView context, String path, int defImage) {
		try {


			if (path.trim().equals("")) {
				context.setImageResource(defImage);
				return;
			} else if (!new File(path).exists()) {
				context.setImageResource(defImage);
				return;
			}
			int scale = 1;// int height=1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			if (options.outWidth > context.getResources().getDisplayMetrics().widthPixels || options.outHeight > context.getResources().getDisplayMetrics().heightPixels) {
				scale = options.outWidth / 960 ;// 960px
				scale = scale + (((options.outWidth - 960 * scale) >= 1) ? 1 : 0);
				scale = (scale % 2 != 0 ? (scale + 1) : scale);
				// height=options.outHeight*context.getResources().getDisplayMetrics().widthPixels/options.outWidth;
			} else {
				// height=context.getResources().getDisplayMetrics().heightPixels;
			}
			// ((LayoutParams)context.getLayoutParams()).height=height;
			// ((LayoutParams)context.getLayoutParams()).width=getResources().getDisplayMetrics().widthPixels;
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			context.setImageBitmap(BitmapFactory.decodeFile(path, options));
		} catch (Exception e) {
			context.setImageResource(defImage);
		}
	}

	public static void getSplash(Context context) {
		try {
			byte[] data = Utility.DownloadFromUrl(Utility.getURLenc("http://www.takalen.com/splash.php", ""));
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bmp.getWidth() >= 10) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < data.length; i++) {
					buf.append((char) data[i]);
				}
				Utility.setSetting(context, "SPLASH", buf.toString());
			}
		} catch (Exception e) {
		}
	}




	public static View getInflater(Context context, int layout) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layout, null, false);
	}

	public static void showDialogSingleChoiceItems(Context context, String title, String[] data, int i, DialogInterface.OnClickListener listener) {
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setSingleChoiceItems(data, i, listener);
		if (title != null) {
			if (title.trim().length() >= 1) {
				dlg.setTitle(title);
			}
		}
		dlg.create().show();
	}
	public static boolean extrackZip(InputStream in, String path){
        try{     
            path = path + ((path.endsWith("/")||path.endsWith("\\"))?"": System.getProperty("file.separator"));
            ZipInputStream zis =   new ZipInputStream(in);
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){

                byte[] buffer = new byte[1024];
                int length; 

                //create folder
                String sfzip = path+ze.getName();
                if (ze.isDirectory()) {
                    new File(sfzip).mkdirs();
                }else if (ze.getName().contains("/")) {
                    String fname = ze.getName().substring(0, ze.getName().lastIndexOf("/"));
                    fname = fname.replace("/", System.getProperty("file.separator"));
                    sfzip = path+ze.getName().replace("/", System.getProperty("file.separator"));

                   
                    new File(path+fname).mkdirs();
                    OutputStream out = new FileOutputStream(sfzip);
                    while ((length = zis.read(buffer)) > 0) {
                        out.write( buffer, 0, length );
                    } 
                    out.flush();
                    out.close();
                }else{
                     OutputStream out = new FileOutputStream(sfzip);
                     while ((length = zis.read(buffer)) > 0) {
                         out.write( buffer, 0, length );
                     } 
                     out.flush();
                     out.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            return true;
        }catch(IOException ex){
        	String
        	eString = ex.getMessage();
        	ex.getMessage();
    	}
        return false;
    }
	public static void createFolder(String folder) {
		buildResource(new String[] { "mkdir", folder });
	}

	private static void buildResource(String[] str) {
		try {
			Process ps = Runtime.getRuntime().exec(str);
			try {
				ps.waitFor();
				Log.v("Directory", "Directory");
			} catch (InterruptedException e) {
				Log.v("Directory", "Directory ." + e.getMessage());
			}
		} catch (IOException e) {
			Log.v("Directory", "Directory .." + e.getMessage());
		}
	}

	public static void removeFolder(String folder) {
		try {
			Process ps = Runtime.getRuntime().exec("rm -rf " + folder);
			try {
				ps.waitFor();
				Log.v("rDirectory", "Directory " + folder);
			} catch (InterruptedException e) {
				Log.v("rDirectory", "Directory ." + e.getMessage());
			}
		} catch (Exception e) {
		}

	}

	public static void deleteFileAll(String folder) {
		deleteFolderAll(new File(folder));
	}

	public static void deleteFolderAll(File dir) {
		try {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					file.delete();
				} else {
					deleteFolderAll(file);
					file.delete();
				}
			}
		} catch (Exception e) {
		}
		dir.delete();
	}

	public static void copyFile(String origin, String destination) {
		try {
			copyFile(new FileInputStream(origin), destination);
		} catch (Exception e) {
		}
	}

	public static void copyFile(InputStream is, String destination) {
		try {
			OutputStream os = new FileOutputStream(destination);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
		}
	}
	public static void writeFile(String destinationm, String str) {
		try {
			OutputStream os = new FileOutputStream( destinationm);

			os.write(str.getBytes());
			os.flush();
			os.close();
		} catch (Exception e) {
		}
	}
	public static String insertString(String str, int len) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static String insertString(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {
			if ((i % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}
	public static String Date() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}
	public static String Now() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
	public static String formatCurrency(long original) {
        return formatCurrency(String.valueOf(original));
    }
	public static String formatCurrency(String original) {
		if (original.contains(".")) {
            StringBuilder stringBuilder = new StringBuilder();
            int il = original.indexOf(".");
            stringBuilder.append(insertStringRev(original.substring(0, il), ",", 3));
            stringBuilder.append(original.substring(il));                
            return  stringBuilder.toString();
        }                    
        return insertStringRev(original, ",", 3);
	}
	public static String formatNumber(String original) {
        return original.replace(",", ""); 
    }
	public static String insertStringMax(String original, String sInsert, int max) {
		StringBuffer sb = new StringBuffer();
		for (int i = original.length(); i < max; i++) {
			sb.append(sInsert);
		}
		sb.append(original);
		return sb.toString();
	}

	public static String insertStringRev(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {

			if (((original.length() - i) % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}

	public static String notNull(String s) {
		if (s != null) {
			return s;
		}
		return "";
	}

	public static boolean isExit;

	public static void setExit(boolean isExit) {
		Utility.isExit = isExit;
	}

	public static boolean isExit() {
		return isExit;
	}

	public static long converDateToLong(String date) {

		SimpleDateFormat df = null;

		if (date.length() == 10) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		} else if (date.length() == 16) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			}
		} else if (date.length() == 19) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}
		} else {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		}

		try {
			Date dt = df.parse(date);
			long hasil = dt.getTime();
			return hasil;
		} catch (Exception e) {
		}

		return 0;
	}

	@SuppressLint("DefaultLocale")
	public static boolean containsChar(String kalimat, char character) {
		boolean isContain = false;
		String charLower = kalimat.toLowerCase();
		char[] charArray = charLower.toCharArray();
		for (int i = 0; i < charLower.length(); i++) {
			if (charArray[i] == character) {
				isContain = true;
				return isContain;
			}
		}
		return isContain;
	}

	// untuk mendapatkan font dari asset
	public static Typeface getFonttype(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "BentonSansComp-Bold.ttf");
	}

	// untuk mendapatkan path dari images
	public static String getPathTheme(String nameFile) {
		return getDefaultPath() + "themes/" + nameFile;
	}

	public static Bitmap LoadImage(String url) {
		Bitmap bitmap = null;
		try {

			// mendapatkan nama file gambar
			String nameFile = url;
			if (nameFile.contains("/")) {
				for (int i = 0; i < nameFile.length(); i++) {
					if (nameFile.contains("/")) {
						nameFile = nameFile.substring(nameFile.indexOf("/") + 1);
					}
				}

				if (nameFile.contains(".")) {
					nameFile = nameFile.substring(0, nameFile.indexOf(".") + 1);
				}
			}

			String path = Utility.getDefaultPath() + "themes/" + nameFile;

			if (isContainImages(path)) {
				bitmap = BitmapFactory.decodeFile(path);
			} else {
				URL urlx = new URL(url);
				HttpURLConnection httpConn = (HttpURLConnection) urlx.openConnection();
				InputStream is = httpConn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);

				// save bitmap to file
				SaveBitmap(bitmap, nameFile);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	// untuk menyimpan bitmap kedalam file di sdcard
	public static void SaveBitmap(Bitmap bitmap, String nameFile) {
		String path = Utility.getDefaultPath() + "themes";
		OutputStream os = null;
		File f = new File(path);

		if (!f.exists()) {
			f.mkdirs();
		}

		File file = new File(path, nameFile);
		if (!file.exists()) {
			try {
				os = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
				os.flush();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isContainImages(String path) {
		boolean isThere = false;
		File file = new File(path);
		if (file.exists()) {
			isThere = true;
		} else {
			isThere = false;
		}
		return isThere;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	/**
	 * checking contex application if null application close by system
	 * 
	 * @param activity
	 */
	public static void checkingAppContex(Activity activity) {
 
	}

	public static boolean isHaveExternalStorage() {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		return false;
	}

	public static void setImageTouch(final ImageView img) {
		img.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//img.setBackgroundResource(R.drawable.select);
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					img.setBackgroundResource(0);
				}
				return false;
			}
		});
	}

	public static void imagePost(String URL, String fileName) {

	}
 


	public static Vector<LinkedHashMap<String, String>> getRecordWithSeparate(Vector<String> data, String separate) {
		Vector<LinkedHashMap<String, String>> v = new Vector<LinkedHashMap<String, String>>();
		for (int i = 0; i < data.size(); i++) {
			if (data.elementAt(i).contains(separate)) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(data.elementAt(i).substring(0, data.elementAt(i).indexOf(separate)), data.elementAt(i).substring(data.elementAt(i).indexOf(separate) + 1));
				v.add(map);
			}
		}
		return v;
	}

	public static void LoadImageDirect(ImageView img, String url) {
		Bitmap bitmap = null;
		try {
			// mendapatkan nama file gambar
			if (url.contains("http")) {
				String nameFile = url;
				if (nameFile.contains("/")) {
					for (int i = 0; i < nameFile.length(); i++) {
						if (nameFile.contains("/")) {
							nameFile = nameFile.substring(nameFile.indexOf("/") + 1);
						}
					}

					if (nameFile.contains(".")) {
						nameFile = nameFile.substring(0, nameFile.indexOf(".") + 1);
					}
				}

				String path = Utility.getDefaultPath() + "themes/" + nameFile;

				if (isContainImages(path)) {
					bitmap = BitmapFactory.decodeFile(path);
				} else {
					URL urlx = new URL(url);
					HttpURLConnection httpConn = (HttpURLConnection) urlx.openConnection();
					InputStream is = httpConn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);

					// save bitmap to file
					SaveBitmap(bitmap, nameFile);
				}

				img.setImageBitmap(bitmap);
			} else {
				if (isContainImages(url))
					img.setImageBitmap(BitmapFactory.decodeFile(url));
				else
					img.setImageResource(0);

			}

		} catch (MalformedURLException e) {
			img.setImageResource(0);
		} catch (IOException e) {
			img.setImageResource(0);
		}
	}
	public static int pxToDp(Context context, int px) {
		return px/context.getResources().getDisplayMetrics().densityDpi;
	}
	public static int dpToPx(Context context, int dp) {
		return dp*context.getResources().getDisplayMetrics().densityDpi;
	}

	public static String[] argumentsQueryNF(Object...param){
        if (param!=null) {
            String[]  args = new String[param.length];
            for (int i = 0; i < args.length; i++) {
                String strparam = String.valueOf(param[i]);
                if (param[i] instanceof Date || param[i] instanceof java.sql.Timestamp) {
                    strparam = "dt|"+param[i];
                }else if (param[i] instanceof java.sql.Date) {
                    strparam = "date|"+param[i];
                }else if (param[i] instanceof java.sql.Time) {    
                    strparam = "time|"+param[i];
                }else if (param[i] instanceof Long) {
                    strparam = "l|"+param[i];
                }else if (param[i] instanceof Integer) {
                    strparam = "i|"+param[i];
                }else if (param[i] instanceof Boolean) {
                    strparam = "b|"+param[i];
                }else if (param[i] instanceof Double) {
                    strparam = "d|"+param[i];
                }else if (param[i] instanceof Float) {
                    strparam = "f|"+param[i];
                }else  if (param[i] instanceof String) {
                    strparam = "s|"+param[i];
                }
                args[i] = strparam;
            }
            return args;      
        }
        return null;
    }
	public static String toHexString(byte[] ba) {
	    StringBuilder str = new StringBuilder();
	    for(int i = 0; i < ba.length; i++)
	        str.append(String.format("%x", ba[i]));
	    return str.toString();
	}

	public static String fromHexString(String hex) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < hex.length(); i+=2) {
	        str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
	    }
	    return str.toString();
	}
}
