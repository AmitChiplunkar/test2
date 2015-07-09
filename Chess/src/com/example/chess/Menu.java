package com.example.chess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Menu extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);
		Button button3 = (Button) findViewById(R.id.button3);
		button1.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Menu.this, Player.class);
				startActivity(i);

			}
		}
		);
		button2.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Menu.this, Help.class);
				startActivity(i);

			}
		}
		);
		button3.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Menu.this, About.class);
				startActivity(i);

			}
		}
		);

	}
	
	public void onBackPressed() {
		AlertDialog alert_back = new AlertDialog.Builder(this).create();
		alert_back.setTitle("Exit?");
		alert_back.setMessage("Are you sure want to Exit?");

		alert_back.setButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert_back.setButton2("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Menu.this.finish();
			}
		});
		alert_back.show();
	}
}
