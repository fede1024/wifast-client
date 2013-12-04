package fr.eurecom.wifast.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler.Callback;
import android.os.Message;

//Uses AsyncTask to create a task away from the main UI thread. This task takes a 
// URL string and uses it to create an HttpUrlConnection. Once the connection
// has been established, the AsyncTask downloads the contents of the webpage as
// an InputStream. Finally, the InputStream is converted into a string, which is
// displayed in the UI by the AsyncTask's onPostExecute method.
public class JSONDownload extends AsyncTask<String, Void, String> {
	private Callback callback;
	private JSONArray resultsArray;
	private JSONObject resultObject;

	public JSONDownload(Callback callback) {
		this.callback = callback;
		this.resultObject = null;
		this.resultsArray = null;
	}

	@Override
	protected String doInBackground(String... urls) {

		// params comes from the execute() call: params[0] is the url.
		try {
			getJSON(urls[0], urls[1], urls[2]);
			return "OK";
		} catch (IOException e) {
			return "Unable to retrieve web page. URL may be invalid.";
		}
	}
	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		Message m = new Message();
		if (this.resultObject != null)
			m.obj = this.resultObject;
		else if (this.resultsArray != null)
			m.obj = this.resultsArray;
		this.callback.handleMessage(m);
	}

	private Boolean getJSON(String HTTPRequest, String type, String myurl) throws IOException {
		InputStream is = null;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000 /* milliseconds */);
			conn.setConnectTimeout(45000 /* milliseconds */);
			conn.setRequestMethod(HTTPRequest);
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("response code: "+response);
			is = conn.getInputStream();

			BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			StringBuilder responseStrBuilder = new StringBuilder();

			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);
			if (type.equalsIgnoreCase("jsonarray"))
				this.resultsArray = new JSONArray(responseStrBuilder.toString());
			else if (type.equalsIgnoreCase("jsonobject"))
				this.resultObject = new JSONObject(responseStrBuilder.toString());
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch (JSONException e) {
			System.err.println("JsonException");
			return false;
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return false;
	}
}
