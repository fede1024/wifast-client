package fr.eurecom.wifast.library;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import fr.eurecom.wifast.WiFastApp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler.Callback;
import android.os.Message;

public class ImageManager extends AsyncTask<String, Void, String> {
	private Callback callback;
	private Context context;
	private String imagesUrl;
	
	public ImageManager(Callback cb, Context con){
		callback = cb;
		context = con;
		imagesUrl = WiFastApp.getProperty("images_url");
	}
	
	@Override
	protected String doInBackground(String... imageCode) {
		InputStream is = null;
		String outFile = null;

		try {
			URL url = new URL(imagesUrl + imageCode[0] + ".png");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			// Starts the query
			conn.connect();
			is = conn.getInputStream();
			
			BufferedInputStream bufferinstream = new BufferedInputStream(is);

	        ByteArrayBuffer baf = new ByteArrayBuffer(5000);
	        int current = 0;
	        while((current = bufferinstream.read()) != -1){
	            baf.append((byte) current);
	        }

	        outFile = context.getExternalFilesDir(null) + "/" + imageCode[0] + ".png";
	        
	        FileOutputStream fos = new FileOutputStream(outFile);
	        fos.write(baf.toByteArray());
	        fos.flush();
	        fos.close();
		} catch(IOException e){
			System.out.println("ImageManager error!" + imageCode[0]);
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch(IOException e){
					System.out.println("ImageManager error on close!");
					e.printStackTrace();
					return null;
				}
			}
		}
		return outFile;
	}
	
	public static String getCachedImage(String image, Context context){
		String path = context.getExternalFilesDir(null) + "/" + image + ".png";
		File f = new File(path);
		if (f.exists())
			return path;
		else
			return null;
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		Message m = new Message();
		m.obj = result;
		if(result != null)
			System.out.println("New image cached: " + result);
		else
			System.out.println("Error downloading image.");
		this.callback.handleMessage(m);
	}
	
}
