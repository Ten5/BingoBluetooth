package com.bingo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class Computer extends Activity {
	ArrayList<Integer> computer_moves,player_moves;
	String pass[];
	GridView grid;
	@Override

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub 

		super.onCreate(savedInstanceState);
		setContentView(R.layout.computer);
		Bundle extras=getIntent().getExtras();
		int pass_computer[]=extras.getIntArray("Comp");
		computer_moves=new ArrayList<Integer>(extras.getIntegerArrayList("Cmoves"));

		player_moves=new ArrayList<Integer>(extras.getIntegerArrayList("Pmoves"));
		pass=new String[25];
		int i,j;
		for(i=0;i<25;i++)
		{
			pass[i]=Integer.toString(pass_computer[i]);
		}
		CustomGrid cadapter=new CustomGrid(Computer.this,pass);
		grid=(GridView)findViewById(R.id.gridView1);
		grid.setAdapter(cadapter);
		grid.setEnabled(false);
		


		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
		Intent i=new Intent(Computer.this,Bingo.class);
		finish();
		startActivity(i);
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home)
		{
			Intent i=new Intent(Computer.this,Bingo.class);
			finish();
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item =menu.add("View Moves");
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				int i,j;
				
				for(i=0;i<25;i++)
				{
					for(j=0;j<computer_moves.size();j++)
					{
						if(pass[i].equals(Integer.toString(computer_moves.get(j))))
						{
							
							grid.getChildAt(i).setBackgroundColor(Color.WHITE);
						}
					}
				}
				for(i=0;i<25;i++)
				{
					for(j=0;j<player_moves.size();j++)
					{
						if(pass[i].equals(Integer.toString(player_moves.get(j))))
						{
							View child;
							child=grid.getChildAt(i);
							child.setBackgroundColor(Color.parseColor("#4F0F65"));
							TextView tv=(TextView)child.findViewById(R.id.grid_text);
							tv.setTextColor(Color.WHITE);
						}
					}
				}
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}
}