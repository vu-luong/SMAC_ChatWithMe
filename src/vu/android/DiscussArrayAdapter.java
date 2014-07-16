package vu.android;

import java.util.ArrayList;
import java.util.List;

import vu.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

	private TextView countryName;
	private List<OneComment> countries = new ArrayList<OneComment>();
	private LinearLayout wrapper;
	ImageView iv_android, iv_nguoi;

	@Override
	public void add(OneComment oneComment) {
		countries.add(oneComment);
		super.add(oneComment);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.countries.size();
	}

	public OneComment getItem(int index) {
		return this.countries.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_discuss, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
		iv_android = (ImageView)row.findViewById(R.id.imageView1);
		//iv_nguoi = (ImageView)row.findViewById(R.id.imageView2);
		
		OneComment coment = getItem(position);

		countryName = (TextView) row.findViewById(R.id.comment);

		countryName.setText(coment.comment);
		countryName.setTextSize(16);

		countryName.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		
		wrapper.setGravity(coment.left ? (Gravity.LEFT) : (Gravity.RIGHT));
		if (!coment.left) {
			countryName.setTextColor(Color.WHITE);
			iv_android.setVisibility(View.INVISIBLE);	
			//iv_nguoi.setVisibility(View.VISIBLE);
		} else {
			iv_android.setVisibility(View.VISIBLE);
			//iv_nguoi.setVisibility(View.INVISIBLE);
			countryName.setTextColor(Color.BLACK);
		}

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}