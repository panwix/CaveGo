package com.panwix.cavego;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;

public class FileIO extends Activity {
	private Context context;
	public void save(String name, String content) throws IOException {
		// TODO Auto-generated method stub
		//Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
		FileOutputStream outStream = context.openFileOutput(name,
				Context.MODE_PRIVATE);
		outStream.write(content.getBytes());
		outStream.close();
	}

	public String read(String fileName) throws IOException {
		FileInputStream inStream = context.openFileInput(fileName);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		return new String(data);

	}
}
