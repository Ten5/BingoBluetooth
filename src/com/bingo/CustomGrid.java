package com.bingo;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ServiceCast")
public class CustomGrid extends BaseAdapter {

	private String number[];
	private Context mcontext;
	public CustomGrid(Context c,String number[])
	{
		mcontext=c;
		this.number=number;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return number.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View grid;
		LayoutInflater inflater = (LayoutInflater) mcontext
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(arg1==null)
		{
			grid=new View(mcontext);
			grid=inflater.inflate(R.layout.grid_single, null);
			TextView textView = (TextView) grid.findViewById(R.id.grid_text);
			textView.setText(number[arg0]);
			/*textView.setOnClickListener(new OnClickListener() {
				
				

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					v.setBackgroundColor(Color.RED);
					v.setClickable(false);
					
				}
			});*/
		}
		else
		{
			grid=(View)arg1;
		}
		return grid;
		
	}

}

