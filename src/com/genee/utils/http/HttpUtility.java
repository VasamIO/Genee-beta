package com.genee.utils.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.genee.exception.InvalidResponse;
import com.genee.utils.Constants;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class HttpUtility {

	public static Scanner getHTTPSConnection(String urlStr) throws InvalidResponse {
		int resCode = -1;
		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setConnectTimeout(Constants.DEFAULT_CONNECTION_TIMEOUT);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod(Constants.LABEL_GET);
			httpConn.connect();
			resCode = httpConn.getResponseCode();
			if (resCode == HttpsURLConnection.HTTP_OK) {
				return new Scanner(httpConn.getInputStream(), Constants.TYPE_UTF);
			} else {
				throw new InvalidResponse(resCode, null);
			}
		} catch (MalformedURLException e) {
			Logger.log(TYPE.SERVERE, "MalformedURLException while secureliveConnection, " + e.getMessage());
		} catch (IOException e) {
			Logger.log(TYPE.SERVERE, "IOException while secureliveConnection, " + e.getMessage());
		}
		return null;
	}

	public static HttpsURLConnection postHTTPSConnection(String urlStr, Map<String, String> requestProperties) throws IOException {
		URL url = new URL(urlStr);
		URLConnection urlConn = url.openConnection();
		HttpsURLConnection httpConn = (HttpsURLConnection) urlConn;
		httpConn.setAllowUserInteraction(false);
		httpConn.setInstanceFollowRedirects(true);
		httpConn.setRequestMethod(Constants.LABEL_POST);
		httpConn.setDoOutput(true);
		httpConn.setChunkedStreamingMode(0);
		if (requestProperties != null) {
			Iterator<String> iterator = requestProperties.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = requestProperties.get(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		httpConn.connect();
		return httpConn;
	}

}
