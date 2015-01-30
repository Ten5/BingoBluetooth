package com.bingo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectMenu extends Activity {
	
	Button play_andy, play_peer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		play_andy = (Button)findViewById(R.id.play_andy);
		play_peer = (Button)findViewById(R.id.play_pvp);
		
		play_andy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectMenu.this, Bingo.class);
				startActivity(i);
			}
		});
		
		play_peer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelectMenu.this, BingoPvP.class);
				startActivity(i);
			}
		});
	}
}
