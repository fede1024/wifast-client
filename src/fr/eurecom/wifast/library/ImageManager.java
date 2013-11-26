package fr.eurecom.wifast.library;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;

public class ImageManager extends AsyncTask<String, Void, String> {
	private Callback callback;
	private Context context;
	
	public ImageManager(Callback cb, Context con){
		callback = cb;
		context = con;
	}
	
	@Override
	protected String doInBackground(String... imageCode) {
		InputStream is = null;
		String outFile = null;

		try {
	        System.out.println("ricevo " + imageCode);
			URL url = new URL("http://s3-eu-west-1.amazonaws.com/fede1024-app/pic/" + imageCode[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			System.out.println("Provo con " + url);
			// Starts the query
			conn.connect();
			is = conn.getInputStream();
			
			BufferedInputStream bufferinstream = new BufferedInputStream(is);

	        ByteArrayBuffer baf = new ByteArrayBuffer(5000);
	        int current = 0;
	        while((current = bufferinstream.read()) != -1){
	            baf.append((byte) current);
	        }

	        //String outFile = Environment.getExternalStorageDirectory() + "/" + imageCode[0];
	        outFile = context.getExternalFilesDir(null) + "/" + imageCode[0];
	        
	        FileOutputStream fos = new FileOutputStream(outFile);
	        fos.write(baf.toByteArray());
	        fos.flush();
	        fos.close();
	        
	        System.out.println("Completato " + outFile);
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return outFile;
	}
	
	public static String getCachedImage(String image, Context context){
		String path = context.getExternalFilesDir(null) + "/" + image;
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
		System.out.println("New image cached: " + result);
		this.callback.handleMessage(m);
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
