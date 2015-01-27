package com.bingo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spash);

		final Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent i=new Intent(Splash.this,Bingo.class);
				finish();
				startActivity(i);
				
			}
		}, 5000);
		
	}
}
