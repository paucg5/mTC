package edu.ub.d2in.mtc.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;

public class BTMessageBus implements OnDataReceivedListener {

	private static final String TAG = BTMessageBus.class.getSimpleName();
	
	private List<BTDataListener> listeners;
	
	private BTMessageBus(){
		listeners = new ArrayList<BTDataListener>();
	}
	
	private static BTMessageBus instance = null;
	
	public static BTMessageBus getInstance() {
		if (instance == null) {
			instance = new BTMessageBus();
		}
		return instance;
	}
	
	@Override
	public void onDataReceived(byte[] rawData, String message) {
		try {
    		String processedMessage = message.substring(1, message.length());
    		float number = Float.parseFloat(processedMessage);
        	for (BTDataListener listener : listeners) {
	        	listener.onBTDataReceived(processedMessage, number);
	        }
        } catch (Exception e) {
        	//do nothing
        	Log.e(TAG, "Received: " + message + ", but could not be parsed into a number");
        }
	}

	
	public void subscribeDataListener(BTDataListener listener) {
		listeners.add(listener);
	}

	
	public void unsubscribeDataListener(BTDataListener listener) {
		listeners.remove(listener);
	}

}
