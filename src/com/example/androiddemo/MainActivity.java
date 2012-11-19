package com.example.androiddemo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnScrollListener{

	private ArrayList<String> mArrayList = new ArrayList<String>();
	private ListView mListView;
	private int number = 1;
	private final int MAX_ITEMS_PER_PAGE = 10;
	private boolean isloading = false;
	private MyAdapter adapter;
	private MyTask task;
	private TextView footer;
	private int TOTAL_ITEMS = 100;
	private TextView header;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		header = (TextView) findViewById(R.id.header);
		mListView = (ListView) findViewById(R.id.myListView);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer = (TextView) inflater.inflate(R.layout.footer, null);
		mListView.addFooterView(footer);
		
		adapter = new MyAdapter(this, R.layout.row);
		mListView.setAdapter(adapter);
		mListView.setOnScrollListener(this);
		
		task = new MyTask();
		task.execute();
	}

	class MyAdapter extends ArrayAdapter<String> {
		LayoutInflater inflater;

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public String getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.row, null);
			TextView mTextView = (TextView) convertView.findViewById(R.id.item);
			mTextView.setText(mArrayList.get(position).toString());

			return convertView;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int loadedItems = firstVisibleItem + visibleItemCount;
		if((loadedItems == totalItemCount) && !isloading){
			if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)){
				task = new MyTask();
				task.execute();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
	
	class MyTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			if(TOTAL_ITEMS > number){
				SystemClock.sleep(1000);
				isloading = true;
				for (int i = 1; i <= MAX_ITEMS_PER_PAGE; i++) {
					mArrayList.add("Item "+number);
					number += 1;
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			isloading = false;
			
			if(adapter.getCount() == TOTAL_ITEMS){
				header.setText("All "+adapter.getCount()+" Items are loaded.");
				mListView.removeFooterView(footer);
			}
			else{
				header.setText("Loaded items - "+adapter.getCount()+" out of "+TOTAL_ITEMS);
			}
		}
	}
}
