package edu.ub.d2in.mtc.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothStateListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import edu.ub.d2in.mtc.R;
import edu.ub.d2in.mtc.bluetooth.BTMessageBus;
import edu.ub.d2in.mtc.fragments.AboutFragment;
import edu.ub.d2in.mtc.fragments.LogFragment;
import edu.ub.d2in.mtc.fragments.dialogs.OkDialogFragment;
import edu.ub.d2in.mtc.fragments.dialogs.OkDialogFragment.OkDialogListener;

public class MainActivity extends ActionBarActivity implements OkDialogListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String DIALOG_NO_BT_ENABLED = "no_bt_enabled";
	
	private String [] navigationItems;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private String currentTitle;
	private BluetoothSPP bluetooth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//member initialiations
		navigationItems = getResources().getStringArray(R.array.nav_drawer_items);
		currentTitle = navigationItems[navigationItems.length - 1]; //first title
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.nav_drawer);
        
        //navigation drawer
        navigationDrawerInitialization();
        
        //action bar setup
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(currentTitle);
        
        //initial fragment init
        Fragment fragment = new AboutFragment();
		startFragment(fragment);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		bluetooth = new BluetoothSPP(this);
		//check for bluetooth
		//TODO user feedback
		if (bluetooth.isBluetoothAvailable()) {
			if (bluetooth.isBluetoothEnabled()) {
				Log.d(TAG, "BT available and enabled");
				bluetoothSetup();
		    }
			else {
		    	Log.e(TAG, "BT not enabled. Prompting the user to enable it.");
		    	promptBluetoothEnabling();
		    }
		}
		else {
			Log.e(TAG, "BT not available. Prompting the user to enable it.");
			promptBluetoothEnabling();
		}
	}
	
	private void promptBluetoothEnabling() {
		//warn user using a dialog
    	OkDialogFragment dialog = OkDialogFragment.newInstance(DIALOG_NO_BT_ENABLED, 
    			getString(R.string.dialog_no_bt_title), 
    			getString(R.string.dialog_no_bt_content), 
    			null);
    	dialog.show(getSupportFragmentManager(), DIALOG_NO_BT_ENABLED);
	}
	
	@Override
	public void onOkButtonClicked(DialogInterface dialog, String tag) {
		if (tag.equals(DIALOG_NO_BT_ENABLED)) {
			dialog.dismiss();
		}
	}
	
	private void bluetoothSetup() {
		Log.d(TAG, "Starting BT service...");
		
		//service initialization
		bluetooth.setupService();
		bluetooth.startService(BluetoothState.DEVICE_OTHER);
		
		//data listener
		bluetooth.setOnDataReceivedListener(BTMessageBus.getInstance());
		
		//connection state listener
		bluetooth.setBluetoothStateListener(new BluetoothStateListener() {                
			@Override
			public void onServiceStateChanged(int state) {
		        if(state == BluetoothState.STATE_CONNECTED)
		            Log.d(TAG, "Connected!");
		        else if(state == BluetoothState.STATE_CONNECTING)
		        	Log.d(TAG, "Connecting!");
		        else if(state == BluetoothState.STATE_LISTEN)
		        	Log.d(TAG, "Listening!");
		        else if(state == BluetoothState.STATE_NONE)
		            Log.d(TAG, "None!");
		    }
		});
		
		//device connection state listener
		bluetooth.setBluetoothConnectionListener(new BluetoothConnectionListener() {
			@Override
			public void onDeviceConnected(String name, String address) {
		        // Do something when successfully connected
		    }

			@Override
		    public void onDeviceDisconnected() {
		        // Do something when connection was disconnected
		    }

			@Override
		    public void onDeviceConnectionFailed() {
		        // Do something when connection failed
		    }
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() - Stopping BT service...");
		bluetooth.stopService();
	}
	
	private void navigationDrawerInitialization() {
		drawerList.setAdapter(
        		new ArrayAdapter<String>(this, 
        								 android.R.layout.simple_list_item_1, 
        								 navigationItems));
        drawerList.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				setFragment(position);
			}
		});
        
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.apptheme_ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
        	@Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(currentTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
        	@Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
	}
	
	private void setFragment(int position) {
		//TODO switch on position
		Fragment fragment;
	    switch (position) {
		case 0:
			//devices
			Intent intent = new Intent(this, DeviceList.class);
			startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
			drawerLayout.closeDrawer(drawerList);
			break;
		case 2:
			fragment = new LogFragment();
			setContentFragment(fragment, position);
			break;
		default:
			fragment = new AboutFragment();
			setContentFragment(fragment, position);
			break;
		}
	}
	
	private void setContentFragment(Fragment fragment, int position) {
		//set fragment
		startFragment(fragment);
		
		// Highlight the selected item, update the title, and close the drawer
	    drawerList.setItemChecked(position, true);
	    setTitle(navigationItems[position]);
	    drawerLayout.closeDrawer(drawerList);
	}
	
	private void startFragment(Fragment fragment) {
		// Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getSupportFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void setTitle(CharSequence title) {
	    currentTitle = title.toString();
	    getSupportActionBar().setTitle(currentTitle);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
	        if(resultCode == Activity.RESULT_OK) {
	        	Log.d(TAG, "Connecting to device with address: " 
	        			+ data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS)
	        			+ "...");
	        	bluetooth.connect(data);
	        }
	    }
	}
	
}
