package edu.ub.d2in.mtc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import edu.ub.d2in.mtc.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupStartButton();
	}

	private void setupStartButton() {
		Button startButton = (Button) findViewById(R.id.button_show_bt_devices);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
