package edu.ub.d2in.mtc.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import edu.ub.d2in.mtc.R;
import edu.ub.d2in.mtc.bluetooth.BTDataListener;
import edu.ub.d2in.mtc.bluetooth.BTMessageBus;

public class ChartFragment extends Fragment implements BTDataListener {

	private LineGraphSeries<DataPoint> series;
	private double x = 0.0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		//graph init
		setupGraph();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//register for messages
		BTMessageBus.getInstance().subscribeDataListener(this);
	}
	
	@Override
	public void onPause() {
		//deregister for messages
		BTMessageBus.getInstance().unsubscribeDataListener(this);
		super.onPause();
	}
	
	private void setupGraph() {
		GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);
		setupGridRenderer(graph);
		setupAxisBounds(graph);
		setupDataSeries(graph);
	}
	
	private void setupGridRenderer(GraphView graph) {
		GridLabelRenderer grid = graph.getGridLabelRenderer();
		
		grid.setNumHorizontalLabels(8);
		grid.setNumVerticalLabels(6);
		
		grid.setPadding(30);
		grid.setLabelsSpace(10);
		
		//label formatter
		grid.setLabelFormatter(new DefaultLabelFormatter(){
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
			        double x = value / 160.0;
			        return super.formatLabel(x, isValueX);
			    }
				else {
			       return super.formatLabel(value, isValueX);
			    }
			}
		});
	}
	
	private void setupAxisBounds(GraphView graph) {
		graph.getViewport().setYAxisBoundsManual(true);
		graph.getViewport().setMaxY(3.0);
		graph.getViewport().setMinY(0.0);
		
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMaxX(500.0);
		graph.getViewport().setMinX(0.0);
	}
	
	private void setupDataSeries(GraphView graph) {
		series = new LineGraphSeries<DataPoint>();
		graph.addSeries(series);
	}

	@Override
	public void onBTDataReceived(String stringData, float data) {
		DataPoint point = new DataPoint(x, data);
		x += 1.0;
		series.appendData(point, true, 500);
	}
	
}
