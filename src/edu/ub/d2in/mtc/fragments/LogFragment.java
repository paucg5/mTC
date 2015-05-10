package edu.ub.d2in.mtc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.ub.d2in.mtc.R;
import edu.ub.d2in.mtc.bluetooth.BTDataListener;
import edu.ub.d2in.mtc.bluetooth.BTMessageBus;

public class LogFragment extends Fragment implements BTDataListener {
	
	private ListView listView;
	private ArrayAdapter<String> adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		//list init
		listView = (ListView) getActivity().findViewById(R.id.list_log);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		
		//register for messages
		BTMessageBus.getInstance().subscribeDataListener(this);
	}
	
	@Override
	public void onDestroy() {
		//deregister for messages
		BTMessageBus.getInstance().unsubscribeDataListener(this);
		super.onDestroy();
	}
	
	@Override
	public void onBTDataReceived(String stringData, float data) {
		String s = String.valueOf(data);
		adapter.add(s);
		adapter.notifyDataSetChanged();
	}
	
}
