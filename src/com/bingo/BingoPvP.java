package com.bingo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

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

public class BingoPvP extends Activity {

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final boolean D = true;
	private static final String TAG = "BluetoothChat";
	private static final int REQUEST_ENABLE_BT = 3;
	private static final int REQUEST_CONNECT_DEVICE = 2;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;
	private String mConnectedDeviceName = null;
	private StringBuffer mOutStringBuffer;
	private int game_over=0, num_to_send, mode=1, num_received, initial = 0, count = 0;

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
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",Toast.LENGTH_LONG).show();
			return;
		}

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setClickable(false);

				arg1.setEnabled(false);
				arg1.setClickable(false);

				if(mp!=null)
					mp.start();
				try {
					num_to_send=Integer.parseInt(number[arg2]);
					setupChat();
				}
				catch(Exception e) {
					Toast.makeText(BingoPvP.this, "Null", Toast.LENGTH_LONG).show();
				}

				if(game_over != 1) {
					int repeat_of_no_click=setTurn(Integer.parseInt(number[arg2]));
					if(repeat_of_no_click==1) {
						arg1.setBackgroundColor(Color.parseColor("#ffffff"));
						player_moves.add(Integer.parseInt(number[arg2]));
						game_over=check();
						if(game_over==1)
							Toast.makeText(BingoPvP.this, "Game Over!", Toast.LENGTH_LONG).show();
						/*if(mode==0) {
							if(game_over!=1) {
								int index=computerTurn();
								if(index==-1)
									Toast.makeText(BingoPvP.this, "Game Over!", Toast.LENGTH_LONG).show();
								else {
									computer_moves.add(index);
									setTurn(index);
									game_over=check();
								}
							}
						}*/
					}
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initialize();
		adapter = new CustomGrid(BingoPvP.this, number);
		grid.setAdapter(adapter);
		
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)					
				// Initialize the BluetoothChatService to perform bluetooth connections
				mChatService = new BluetoothChatService(this, mHandler);
				Toast.makeText(BingoPvP.this, "Setting up service!", Toast.LENGTH_SHORT).show();
				// Initialize the buffer for outgoing messages
				mOutStringBuffer = new StringBuffer("");
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		mp = MediaPlayer.create(BingoPvP.this,R.raw.click);
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
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
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if(mp != null)
			mp.stop();
		mp.release();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
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
			//Toast.makeText(BingoPvP.this, "Index: "+i, Toast.LENGTH_LONG).show();
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
		Toast.makeText(BingoPvP.this, "Game Over!", Toast.LENGTH_LONG).show();
		grid.setEnabled(false);
		if (((presult==cresult) && (presult>=4 && cresult>=4)) || ((presult>4 && cresult>4) && (presult!=cresult)))
			Toast.makeText(BingoPvP.this, "Draw!", Toast.LENGTH_LONG).show();
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
		//Toast.makeText(BingoPvP.this, "Number " +num, Toast.LENGTH_LONG).show();
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

		if(mode == 1) {
			j=0;index=0;
			index=0;
			comp_num=num_received;

			showCustomAlert("Player gave " +comp_num, 1); 
			for(i=0;i<25;i++) {
				if(number[i].equals(Integer.toString(comp_num)))
					index=i;
			}
			View item=grid.getChildAt(index);

			TextView tv=(TextView)item.findViewById(R.id.grid_text);
			tv.setTextColor(Color.WHITE);
			item.setBackgroundColor(Color.parseColor("#4F0F65"));
			game_over=check();
			if(game_over==1)
				Toast.makeText(BingoPvP.this, "Game Over!", Toast.LENGTH_LONG).show();
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent serverIntent = null;

		if(item.getItemId() == R.id.connect_scan) {
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		}
		
		if(item.getItemId() == R.id.discoverable) {
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}

		if(item.getItemId()==R.id.item1)
			Toast.makeText(BingoPvP.this,"Designed by Subhasree", Toast.LENGTH_LONG).show();
		
		if(item.getItemId()==R.id.item2) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(BingoPvP.this);
			builder1.setTitle("Viewing my grid will terminate this game.");
			builder1.setMessage("Are you sure?");
			builder1.setCancelable(true);
			builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent i=new Intent(BingoPvP.this,Computer.class);
					i.putExtra("Comp", pass_computer);
					i.putExtra("Cmoves", computer_moves);
					i.putExtra("Pmoves", player_moves);
					//finish();
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
		
		if(item.getItemId()==R.id.instruction) {
			Intent i=new Intent(BingoPvP.this,Instruction.class);
			startActivity(i);
		}
		
		if(item.getItemId()==R.id.start) {
			initial = 1;
			String text = "";
			for(int i=0; i<number.length; i++)
				text += number[i]+" ";
			text=text+',';
			sendMessage(text);			
		}

		if(item.getItemId()==R.id.sound) {
			builder1=new AlertDialog.Builder(BingoPvP.this);
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
						mp=MediaPlayer.create(BingoPvP.this,R.raw.click);
				}
			});
			builder1.create().show();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			AlertDialog.Builder builder=new AlertDialog.Builder(BingoPvP.this);
			builder.setTitle("Message");
			builder.setMessage("Are you sure to exit?");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					BingoPvP.this.finish();
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

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
				mode = 1;
				//type here for sending list
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session

				Toast.makeText(BingoPvP.this, "Activity Result is OK", Toast.LENGTH_SHORT).show();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");	
		// Send a message using content of the edit text widget
		String message = ""+num_to_send;
		sendMessage(message);
	}

	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
		}
	}

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
					//Toast.makeText(BluetoothBingoPvP.this, R.string.title_connected_to+" "+mConnectedDeviceName, Toast.LENGTH_LONG).show();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					//Toast.makeText(BluetoothBingoPvP.this, R.string.title_connecting, Toast.LENGTH_LONG).show();
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					//Toast.makeText(BluetoothBingoPvP.this, R.string.title_not_connected, Toast.LENGTH_LONG).show();
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);				
				writeMessage.concat("");
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
							
				if(readMessage.contains(",")) {
					//Toast.makeText(BingoPvP.this, readMessage, Toast.LENGTH_SHORT).show();
										
					initial = 0;
					int i=0, j=0, q=0;
					readMessage=readMessage.replace(",", "");
					Log.d("Peer's grid", readMessage);
					StringTokenizer dataPart = new StringTokenizer(readMessage);
					String str = dataPart.nextToken();
					while(str != null) {
						int num = Integer.parseInt(str);
						//Log.d("Strings", str);
						Log.d("i j num",i+" "+ j + " "+num);
						computer[i][j++] = num;

						
						pass_computer[q++]=num;
						if(j == 5) {
							i++;
							j=0;
						
						}
						try {
							str = dataPart.nextToken();
						} catch(Exception e) {
							str = null;
						}
					}
				}
				else {
					try
					{
					num_received = Integer.parseInt(readMessage);
					}
					catch(Exception e)
					{
						Log.e("NumberFormat", "Let's start");
					}
					int check_repeat = setTurn(num_received);
					if(check_repeat==1)
						computerTurn();
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
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
}
