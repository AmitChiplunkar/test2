package com.example.chess;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class Controller extends Activity {
	private Chess core;
	private Board board;
	
	private String user;

	public void createUsername(View v)
	{
		setContentView(board);
		board.requestFocus();
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chess);
		Intent j = getIntent();
		Toast t1;
		board = new Board(this);
		core = new Chess();
		board.setCore(core);
		setContentView(board);
		Intent k = getIntent();
		String player1=k.getStringExtra("p1");
		String player2=k.getStringExtra("p2");
		Toast toast = Toast.makeText(getApplicationContext(), player1+" vs "+player2, Toast.LENGTH_SHORT);
		//Toast t2 = Toast.makeText(getApplicationContext(), player2, Toast.LENGTH_SHORT);
    
		toast.show();
		//String player1=i.getStringExtra("p1");
		//String player2=i.getStringExtra("p2");
    
		//Toast t1 = Toast.makeText(getApplicationContext(), player1, Toast.LENGTH_SHORT);
		//Toast t2 = Toast.makeText(getApplicationContext(), player2, Toast.LENGTH_SHORT);
    
		//t1.show();
		//t2.show();

	}

}