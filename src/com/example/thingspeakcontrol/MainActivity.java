package com.example.thingspeakcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button button1, button2, button3;
	EditText et1, et2, et3;
	TextView tv1;

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				tv1.setText((String) msg.obj);
				break;
			case 2:
				String str = "";
				ThingSpeakChannelJson json = (ThingSpeakChannelJson) msg.obj;
				str += "name = " + json.channel_name + "\n";
				str += "field1 = " + json.channel_field1 + "\n";
				str += "field2 = " + json.channel_field2 + "\n";
				str += "entry_id = " + json.channel_last_entry_id + "\n";
				for(int i = 0; i<json.feeds.size(); i++){
					str += "Item = " + json.feeds.get(i).entry_id + "\n";
					str += "field1 = " + json.feeds.get(i).channel_field1 + "\n";
					str += "field2 = " + json.feeds.get(i).channel_field2 + "\n";
				}
				tv1.setText(str);				
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
	}

	public void findViews() {
		tv1 = (TextView) findViewById(R.id.textView1);
		et1 = (EditText) findViewById(R.id.editText1);
		et2 = (EditText) findViewById(R.id.editText2);
		et3 = (EditText) findViewById(R.id.editText3);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new HttpJsonTask(ThingSpeakUrl.feedJson())).start();
			}
		});
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new HttpUpdateTask(ThingSpeakUrl.updata(
						Integer.parseInt(et1.getText().toString()),
						Integer.parseInt(et2.getText().toString())))).start();
			}
		});
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new HttpUpdateTask(ThingSpeakUrl.getLast(Integer
						.parseInt(et3.getText().toString())))).start();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class HttpUpdateTask implements Runnable {

		String ip_addr;

		public HttpUpdateTask(String ip_addr) {
			this.ip_addr = ip_addr;
			Log.d("ip_addr", ip_addr);
		}

		@Override
		public void run() {
			URL url;
			try {
				StringBuilder sb = new StringBuilder();
				url = new URL(ip_addr);
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String data = br.readLine();
				while (data != null) {
					sb.append(data);
					data = br.readLine();
				}
				Log.d("data", sb.toString());
				sendMessage(1, sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class HttpJsonTask implements Runnable {

		String ip_addr;

		public HttpJsonTask(String ip_addr) {
			this.ip_addr = ip_addr;
			Log.d("ip_addr", ip_addr);
		}

		@Override
		public void run() {
			URL url;
			try {
				StringBuilder sb = new StringBuilder();
				url = new URL(ip_addr);
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String data = br.readLine();
				while (data != null) {
					sb.append(data);
					data = br.readLine();
				}
				Log.d("data", sb.toString());
				ThingSpeakChannelJson json = ThingSpeakUrl.parserJson(sb
						.toString());
				sendMessage(2, json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void sendMessage(int what, Object obj) {
		Message msg = new Message();
		msg = mHandler.obtainMessage(what, obj);
		mHandler.sendMessage(msg);
	}
}
