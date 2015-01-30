package com.bingo;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Bingo extends Activity {

	private static final boolean D = true;
	private static final String TAG = "BingoAI";

	private int game_over=0, num_to_send;

	GridView grid;
	AlertDialog.Builder builder1;
	ArrayList<Integer>comp_numbers;
	int comp_num;
	String[] number = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25"};
	int player[][], computer[][], pass_computer[];
	CustomGrid adapter;
	int cscore[], pscore[], presult=-1, cresult=-1;
	String result[]={"B","BI","BIN","BING","BINGO"};
	ArrayList<Integer> computer_moves=new ArrayList<Integer>();
	ArrayList<Integer> player_moves=new ArrayList<Integer>();
	TextView andy,you;
	MediaPlayer mp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.activity_bingo);
		
		andy=(TextView)findViewById(R.id.textView1);
		you=(TextView)findViewById(R.id.textView2);		
		grid=(GridView)findViewById(R.id.gridView1);
		initialize();
		adapter = new CustomGrid(Bingo.this, number);
		grid.setAdapter(adapter);

		//mp.start();

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				//arg0.setEnabled(false);
				arg0.setClickable(false);
				//arg1.setBackgroundColor(Color.parseColor("#9E99A0"));

				arg1.setEnabled(false);
				arg1.setClickable(false);

				if(mp!=null)
					mp.start();

				//grid.getChildAt(arg2).setEnabled(false);
				//grid.getChildAt(arg2).setClickable(false);


				//Toast.makeText(Bingo.this, "Clicked at"+ number[arg2], Toast.LENGTH_LONG).show();
				try {
					num_to_send=Integer.parseInt(number[arg2]);
				}
				catch(Exception e) {
					Toast.makeText(Bingo.this, "Null", Toast.LENGTH_LONG).show();
				}

				if(game_over!=1) {
					int repeat_of_no_click=setTurn(Integer.parseInt(number[arg2]));
					if(repeat_of_no_click==1) {
						arg1.setBackgroundColor(Color.parseColor("#ffffff"));
						player_moves.add(Integer.parseInt(number[arg2]));
						game_over=check();
						if(game_over!=1) {
							int index=computerTurn();
							if(index==-1)
								Toast.makeText(Bingo.this, "Game Over!", Toast.LENGTH_LONG).show();
							else {
								computer_moves.add(index);
								setTurn(index);
								game_over=check();
							}
						}
					}
				}
			}
		});
	}

	private int checkLeftDiagonal() {
		int i, cflag=0, pflag=0;
		for(i=0;i<5;i++) {
			if(player[i][i]==0)
				pflag++;
			if(computer[i][i]==0)
				cflag++;
		}

		if(pflag==5 && pscore[10]!=1) {
			pscore[10]=1;
			presult++;
			if(presult<5)
				you.setText("You: "+result[presult]);
			if(presult==4)
				return checkScore();
		}
		if(cflag==5 && cscore[10]!=1) {
			cscore[10]=1;
			cresult++;
			if(cresult<5)
				andy.setText("Andy: "+result[cresult]);
			if(cresult==4)
				return checkScore();
		}
		return 0;
	}

	private int checkRightDiagonal() {
		int i, cflag=0, pflag=0;
		for(i=0;i<5;i++) {
			//Toast.makeText(Bingo.this, "Index: "+i, Toast.LENGTH_LONG).show();
			if(player[i][4-i]==0)
				pflag++;
			if(computer[i][4-i]==0)
				cflag++;
		}
		if(pflag==5 && pscore[11]!=1) {
			pscore[11]=1;
			presult++;
			if(presult<5)
				you.setText("You: "+result[presult]);
			if(presult==4)
				return checkScore();
		}
		if(cflag==5 && cscore[11]!=1) {
			cscore[11]=1;
			cresult++;
			if(cresult<5)
				andy.setText("Andy: "+result[cresult]);
			if(cresult==4)
				return checkScore();
		}
		return 0;
	}

	private int checkRows() {
		int i,j,cflag=0,pflag=0;
		for(i=0;i<5;i++) {
			for(j=0;j<5;j++) {
				if(player[i][j]==0)
					pflag++;
				if(computer[i][j]==0)
					cflag++;
			}

			if(pflag==5 && pscore[i]!=1) {
				pscore[i]=1;
				presult++;
				if(presult<5)
					you.setText("You: "+result[presult]);
				if(presult==4)
					return checkScore();
			}
			if(cflag==5 && cscore[i]!=1) {
				cscore[i]=1;
				cresult++;
				if(cresult<5)
					andy.setText("Andy: "+result[cresult]);
				if(cresult==4)
					return checkScore();
			}
			pflag=0;
			cflag=0;
		}
		return 0;
	}

	private int  checkCols() {
		int i,j,cflag=0,pflag=0;
		for(j=0;j<5;j++) {
			for(i=0;i<5;i++) {
				if(player[i][j]==0)
					pflag++;
				if(computer[i][j]==0)
					cflag++;
			}
			if(pflag==5) {
				if(pscore[5+j]!=1) {
					pscore[5+j]=1;
					presult++;
					if(presult<5)
						you.setText("You: "+result[presult]);
					if(presult==4)
						return checkScore();
				}
			}
			if(cflag==5) {
				if(cscore[5+j]!=1) {
					cscore[5+j]=1;
					cresult++;
					if(cresult<5)
						andy.setText("Andy: "+result[presult]);
					if(cresult==4)
						return checkScore();
				}
			}
			pflag=0;
			cflag=0;
		}
		return 0;
	}

	private int checkScore() {
		Toast.makeText(Bingo.this, "Game Over!", Toast.LENGTH_LONG).show();
		grid.setEnabled(false);
		if (((presult==cresult) && (presult>=4 && cresult>=4)) || ((presult>4 && cresult>4) && (presult!=cresult)))
			Toast.makeText(Bingo.this, "Draw!", Toast.LENGTH_LONG).show();
		if(presult==4 && cresult<4)
			showCustomAlert("You win", 2);
		if(cresult==4 && presult<4)
			showCustomAlert("Andy wins", 3);
		return 1;
	}

	public int check() {
		if(checkLeftDiagonal()==1)
			return 1;
		if(checkRightDiagonal()==1)
			return 1;
		if(checkRows()==1)
			return 1;
		if(checkCols()==1)
			return 1;
		return 0;
	}

	public int setTurn(int num) {
		int i, j, flag=1, pi=0, pj=0, ci=0, cj=0;
		//Toast.makeText(Bingo.this, "Number " +num, Toast.LENGTH_LONG).show();
		for(i=0;i<5;i++) {
			for(j=0;j<5;j++) {
				if(player[i][j]==num) {
					flag++;
					pi=i;
					pj=j;
				}
				if(computer[i][j]==num) {
					flag++;
					ci=i;
					cj=j;
				}
			}
		}

		if(flag==3) {
			player[pi][pj]=0;
			computer[ci][cj]=0;
			//check();
		}
		else
			return 0;
		return 1;
	}

	public int computerTurn() {

		int i,j=0,index=0;
		comp_numbers = new ArrayList<Integer>();
		for(i=0;i<5;i++) {
			for(j=0;j<5;j++) {
				if(player[i][j]!=0)
					comp_numbers.add(player[i][j]);
			}
		}

		if(comp_numbers.isEmpty()==true)
			return -1;
		//Toast.makeText(Bingo.this, "Size " +numbers.size(), Toast.LENGTH_LONG).show();
		Collections.shuffle(comp_numbers);

		final Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
			int i,index=0;
			@Override
			public void run() {
				comp_num=comp_numbers.get(0);
				showCustomAlert("Andy gave " +comp_num,1); 
				for(i=0;i<25;i++) {
					if(number[i].equals(Integer.toString(comp_num)))
						index=i;
				}
				View item=grid.getChildAt(index);

				TextView tv=(TextView)item.findViewById(R.id.grid_text);
				tv.setTextColor(Color.WHITE);
				item.setBackgroundColor(Color.parseColor("#4F0F65"));
			}
		}, 3000);
		//showCustomAlert("Andy gave " + num, 1); 
		//Toast.makeText(Bingo.this, "Computer gave " +num, Toast.LENGTH_SHORT).show();
		return comp_num;
	}

	private void initialize () {
		int i,j,q=0;
		player=new int[5][5];
		computer=new int[5][5];
		pass_computer=new int[25];
		cscore=new int[12];
		pscore=new int[12];

		ArrayList<Integer> numbers=new ArrayList<Integer>();
		for(i=0;i<25;i++)
			numbers.add(i+1);
		Collections.shuffle(numbers);
		for(i=0;i<5;i++) {
			for(j=0;j<5;j++) {
				computer[i][j] = numbers.get(q);
				pass_computer[q]=computer[i][j];
				q++;
			}
		}
		Collections.shuffle(numbers);
		for(i=0;i<25;i++)
			number[i]=Integer.toString(numbers.get(i));
		q=0;
		for(i=0;i<5;i++) {
			for(j=0;j<5;j++) {
				player[i][j]=numbers.get(q);
				q++;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			AlertDialog.Builder builder=new AlertDialog.Builder(Bingo.this);
			builder.setTitle("Message");
			builder.setMessage("Are you sure to exit?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Bingo.this.finish();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId()==R.id.item1)
			Toast.makeText(Bingo.this,"Designed by Subhasree", Toast.LENGTH_LONG).show();
		if(item.getItemId()==R.id.item2) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(Bingo.this);
			builder1.setTitle("Viewing my grid will terminate this game.");
			builder1.setMessage("Are you sure?");
			builder1.setCancelable(true);
			builder1.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent i=new Intent(Bingo.this,Computer.class);
					i.putExtra("Comp", pass_computer);
					i.putExtra("Cmoves", computer_moves);
					i.putExtra("Pmoves", player_moves);
					finish();
					startActivity(i);

				}
			});
			builder1.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder1.setIcon(R.drawable.andy);
			AlertDialog alert11 = builder1.create();
			alert11.show();
		}
		if(item.getItemId()==R.id.instruction) {
			Intent i=new Intent(Bingo.this,Instruction.class);
			startActivity(i);
		}

		if(item.getItemId()==R.id.restart) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(Bingo.this);
			builder1.setTitle("Andy says");
			builder1.setMessage("Are you sure?");
			builder1.setCancelable(true);
			builder1.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent i=new Intent(Bingo.this,Bingo.class);
					finish();
					startActivity(i); 
				}
			});
			builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder1.setIcon(R.drawable.andy);
			AlertDialog alert11 = builder1.create();
			alert11.show();
		}

		if(item.getItemId()==R.id.sound) {
			builder1=new AlertDialog.Builder(Bingo.this);
			builder1.setTitle("Sound");
			builder1.setMessage("Turn on/off sounds?");
			builder1.setPositiveButton("Off",new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mp!=null) {
						try {
							mp.stop();
							mp.release();
						}
						finally {
							mp = null;
							builder1.setMessage("Turn on sounds?");
						}
					}
				}
			} );

			builder1.setNegativeButton("On", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mp==null)
						mp=MediaPlayer.create(Bingo.this,R.raw.click);
				}
			});
			builder1.create().show();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("InflateParams")
	public void showCustomAlert(String msg,int show_type) {
		Context context = getApplicationContext();
		// Create layout inflator object to inflate toast.xml file
		LayoutInflater inflater = getLayoutInflater();

		// Call toast.xml file for toast layout 
		View toastRoot = inflater.inflate(R.layout.toast, null);

		TextView text=(TextView)toastRoot.findViewById(R.id.tv);
		ImageView img=(ImageView)toastRoot.findViewById(R.id.image);

		text.setText(msg);
		if(show_type==1)
			img.setBackgroundResource(R.drawable.image0);
		else if(show_type==2)
			img.setBackgroundResource(R.drawable.lost);
		else
			img.setBackgroundResource(R.drawable.win);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		mp = MediaPlayer.create(Bingo.this,R.raw.click);
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		mp.stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mp != null)
			mp.stop();
		mp.release();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}
}
