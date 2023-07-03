package com.naa.utils;


import com.icollection.AppController;
import com.icollection.util.AppUtil;
import com.icollection.util.EasySSLSocketFactory;
import com.naa.data.Nson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;


import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InternetX {

    public static String multipartHttp(String url, Map<String, String> arg, String imagename, InputStream file) {
        HashtableMulti<String, String, InputStream> inps = new HashtableMulti<String, String, InputStream>  ();
        inps.put("image", imagename, file);
        return multipartHttp(url, arg, inps);
    }

    public static String multipartHttp(String url, Map<String, String> arg, HashtableMulti<String, String, InputStream> file) {
        Nson args = new Nson(arg);
        URL object;
        final String LINE_FEED = "\r\n";
        try {
            object = new URL(nikitaYToken(url));//nikitaYToken(stringURL); tidak perlu

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) object.openConnection();
                // creates a unique boundary based on time stamp
                String boundary = "===" + System.currentTimeMillis() + "===";

                con.setDoOutput(true);
                con.setDoInput(true);
                //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.setConnectTimeout(30000);

                OutputStream outputStream = con.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);

                args.set("ytoken", getSetting("TOKEN"));
                args.set("yuserid", getSetting("ID_USER"));

                Nson keys = args.getObjectKeys();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < keys.size(); i++) {

                    builder.append((urlEncode(keys.get(i).asString())+"="));
                    builder.append(urlEncode(args.get(keys.get(i).asString()).asString()));

                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + keys.get(i).asString() + "\"")
                            .append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=UTF-8" ).append(
                            LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(  args.get(keys.get(i).asString()).asString()  ).append(LINE_FEED);
                    writer.flush();
                }

                Nson nson = new Nson(file) ;
                keys =  nson.getObjectKeys();
                for (int i = 0; i < keys.size(); i++) {
                    String fieldName = "image";
                    String fileName = file.get(keys.get(i).asString());
                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append(
                            "Content-Disposition: form-data; name=\"" + fieldName
                                    + "\"; filename=\"" + fileName + "\"")
                            .append(LINE_FEED);
                    writer.append(
                            "Content-Type: "
                                    + URLConnection.guessContentTypeFromName(fileName))
                            .append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    InputStream inputStream = file.getData(keys.get(i).asString());
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                    inputStream.close();

                    writer.append(LINE_FEED);
                    writer.flush();
                }

                writer.append(LINE_FEED).flush();
                writer.append("--" + boundary + "--").append(LINE_FEED);
                writer.close();
                //display what returns the POST request

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    //Utility.sessionExpired(con.getHeaderFields());
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
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }

        } catch (MalformedURLException e) {
            //Utility.nikitaErrorConn();
            // TODO Auto-generated catch block
            e.printStackTrace();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

        return "";
    }
    public static String urlEncode(String s){
        try {
            return URLEncoder.encode(s,"UTF-8");
        } catch (Exception e) { }
        return  "";
    }
    public static String postHttpConnection(String stringURL, Map args) {
        return  postHttpConnection(stringURL, new Nson(args));
    }

    public static String postHttpConnection(String stringURL, Nson args) {
        URL object;
        try {
            object = new URL(stringURL);//nikitaYToken(stringURL); tidak perlu
            HttpURLConnection con;
            try {


                HttpPost post = new HttpPost(stringURL);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);


                Nson keys = args.getObjectKeys();

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < keys.size(); i++) {
                    nameValuePairs.add(new BasicNameValuePair(keys.get(i).asString(), args.get(keys.get(i).asString()).asString() ));
                }
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));


                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
                HttpParams params = new BasicHttpParams();
                params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
                params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
                params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
                ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
                HttpClient client =  new DefaultHttpClient(cm, params);//new DefaultHttpClient();
                HttpResponse response = client.execute(post);


                StringBuilder sb = new StringBuilder();
                int HttpResult = response.getStatusLine().getStatusCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = response.getEntity().getContent();

                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();

                     //return Utility.readFile(inputStream);
                } else {
                    InputStream inputStream = response.getEntity().getContent();


                    return Utility.readFile(inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                e.printStackTrace();
               // Utility.nikitaErrorConn();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }

        } catch (MalformedURLException e) {
            //Utility.nikitaErrorConn();
            // TODO Auto-generated catch block
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }

        //return "";
    }

    public static String nikitaYToken(String url) {
        StringBuffer result = new StringBuffer();
        StringBuffer addParam = new StringBuffer();
       /* addParam.append(("ytoken="));
        addParam.append(Utility.urlEncode(getSetting("TOKEN")));
        addParam.append(("&yuserid="));
        addParam.append(Utility.urlEncode(getSetting("ID_USER")));*/

        if (url.contains("?")) {
            result.append(url.substring(0,url.indexOf("?")+1)).append(addParam.toString()).append("&").append(url.substring(url.indexOf("?")+1));
        }else if (url.contains("&")) {
            result.append(url.substring(0, url.indexOf("&"))).append("?").append(addParam.toString()).append(url.substring(url.indexOf("&")));
        }else{
            result.append(url).append("?").append(addParam.toString());
        }

        return result.toString();
    }
    public static String getSetting(String key){
        return UtilityAndroid.getSetting(AppController.getInstance(), key, "");
    }
    public static void setSetting(String key, String value){
        UtilityAndroid.setSetting(AppController.getInstance(), key, value);
    }
    public static String getHttpConnectionX(String stringURL, Map<String, String> args) {
        Nson nset = new Nson(args) ;
        Nson keys =  nset.getObjectKeys();

        String[] strings = new String[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            strings[i] = nset.get(keys.get(i).asString()).asString();
        }
        return getHttpConnectionX(stringURL, strings);
    }
    public static String getHttpConnectionX(String stringURL, String...paramvalue) {
        URL object;
        try {
            stringURL = nikitaYToken(stringURL);
            if (paramvalue!=null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");String sdata = urlEncode(paramvalue[i].substring(split+1));
                        stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                    }
                }
                stringURL =  stringURL+(stringURL.contains("?")?"&":"?")+stringBuffer.toString();
            }
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
                    //Utility.sessionExpired(con.getHeaderFields());
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
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
                //Utility.nikitaErrorConn();
                // TODO Auto-generated catch block
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            } catch (Exception e) {
                //Utility.nikitaErrorConn();
                e.printStackTrace();
                Nson nson = Nson.newObject();
                nson.set("STATUS", "ERROR");
                nson.set("ERROR", e.getMessage());
                return nson.toJson();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Utility.nikitaErrorConn();
            Nson nson = Nson.newObject();
            nson.set("STATUS", "ERROR");
            nson.set("ERROR", e.getMessage());
            return nson.toJson();
        }
        return "";
    }
    public static String getString(String str){
        return str;
    }
}
