package com.example.chess;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Player extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Button button = (Button)findViewById(R.id.button1);
		final EditText t1 = (EditText)findViewById(R.id.player1);
		final EditText t2 = (EditText)findViewById(R.id.player2);
		
		
		
		button.setOnClickListener(new View.OnClickListener() {
		
	
			@Override
			public void onClick(View v) {
			//	 TODO Auto-generated method stub
				String p1 = t1.getText().toString();
				String p2 = t2.getText().toString();
				Intent i = new Intent(getApplicationContext(),Controller.class);
				//String p1 = i.getStringExtra("p1");
   				i.putExtra("p1", p1);
   				i.putExtra("p2", p2);
				//Toast t1 = Toast.makeText(getApplicationContext(), p1, Toast.LENGTH_SHORT);
			    //Toast t2 = Toast.makeText(getApplicationContext(), p2, Toast.LENGTH_SHORT);
			    
			    //t1.show();
			    //t2.show();
			startActivity(i);	
				
			//Intent activityChangeIntent = new Intent(MainActivity.this,chess.class);	
		//	MainActivity.this.startActivity(activityChangeIntent);
			}
		});
	}

	


}
