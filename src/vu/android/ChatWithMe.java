package vu.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import vu.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatWithMe extends Activity {
	private vu.android.DiscussArrayAdapter adapter;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private ListView lv, lv_speak;
	private EditText editText1;
	private Button bt_send, bt_speak, bt_send_real;
	private String url1 = "http://tech.fpt.com.vn/AIML/api/bots/53acca0de4b0e9c80338a8ae/chat?request=";
	private String url2 = "&token=7cb14cc6-80db-4e2a-bda3-f5fd8939daf1";
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//bo thanh title
		getActionBar().setTitle("Chat with me!");  
		setContentView(R.layout.activity_discuss);
		
	    if (!NetworkAvailable()) {
	    	
	    	showToastMessage("Network Unavailable!");
	    }

		lv = (ListView) findViewById(R.id.listView1);
		lv_speak = (ListView)findViewById(R.id.listView2);
		bt_send = (Button)findViewById(R.id.button1);
		bt_send_real = (Button)findViewById(R.id.button2);
		
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

		lv.setAdapter(adapter);
		adapter.add(new OneComment(true, "hello"));
		
		editText1 = (EditText) findViewById(R.id.editText1);
		
		
		editText1.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.length() > 0){
					bt_send.setVisibility(View.INVISIBLE);
					bt_send_real.setVisibility(View.VISIBLE);
				} else {
					bt_send.setVisibility(View.VISIBLE);
					bt_send_real.setVisibility(View.INVISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		
		editText1.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				lv_speak.setVisibility(View.INVISIBLE);
				lv.setVisibility(View.VISIBLE);
				scrollMyListViewToBottom();
				return false;
			}
			
		});
		
		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) &&(editText1.getText().toString().length() > 0)) {
					// Perform action on key press
					if (!NetworkAvailable()) {
				    	showToastMessage("Network Unavailable!");
				    	return false;
				    }
					adapter.add(new OneComment(false, editText1.getText().toString()));
					answer();
					scrollMyListViewToBottom();
					return true;
				}
				return false;
			}
		});

		
		bt_send_real.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//long startTime = System.currentTimeMillis();
				if (!NetworkAvailable()) {
			    	showToastMessage("Network Unavailable!");
			    	return;
			    } 
				
				adapter.add(new OneComment(false, editText1.getText().toString()));
				
				answer();
				
				scrollMyListViewToBottom();
			}
		});
		
		lv_speak.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				editText1.setText((String) lv_speak.getItemAtPosition(position));
				lv_speak.setVisibility(View.INVISIBLE);
				lv.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void answer(){
		String url = editText1.getText().toString();
		editText1.setText("");
		try {
			String encodedURL = URLEncoder.encode(url, "UTF-8");
			String finalUrl = url1 + encodedURL + url2;			
			new HttpAsyncTask().execute(finalUrl);//sendn request
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    // scroll den cuoi thanh chat
	private void scrollMyListViewToBottom() {
	    lv.post(new Runnable() {
	        @Override
	        public void run() {
	            lv.setSelection(adapter.getCount() - 1);
	        }
	    });
	}
	
	
	// Kiem tra thiet bi cho phep nhan dang giong noi hay ko
	public void checkVoiceRecognition() {
		Log.v("", "checkVoiceRecognition checkVoiceRecognition");
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			bt_speak.setEnabled(false);
			Toast.makeText(this, "Voice recognizer not present", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void speak(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// xac nhan ung dung muon gui yeu cau
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

		// goi y nhan dang nhung gi nguoi dung se noi
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

		int noOfMatches = 5;
		
		// Xac dinh ban muon bao nhieu ket qua gan dung duoc tra ve
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

		// Gui yeu cau di
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	// Su kien nhan lai ket qua
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			// Truong hop co gia tri tra ve
			if(resultCode == RESULT_OK) {
				ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				
				if (!textMatchList.isEmpty()) {
					// Hien thi ket qua
					lv_speak.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, textMatchList));
					lv_speak.setVisibility(View.VISIBLE);
					lv.setVisibility(View.INVISIBLE);
				}
			// Cac truong hop loi
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
				showToastMessage("Audio Error");
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
				showToastMessage("Client Error");
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
				showToastMessage("Network Error");
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
				showToastMessage("No Match");
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
				showToastMessage("Server Error");
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	void showToastMessage(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	public boolean NetworkAvailable(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	return true;
	    } else {
	    	return false;
	    	//finish();
	    }
	}
	
	public static String GET(String url){
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

   
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
        	JSONObject json;
			try {
				json = new JSONObject(result);
				adapter.add(new OneComment(true, json.getString("response")));
				bt_send.setVisibility(View.VISIBLE);
				bt_send_real.setVisibility(View.INVISIBLE);
				scrollMyListViewToBottom();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
       }
    }
	
}