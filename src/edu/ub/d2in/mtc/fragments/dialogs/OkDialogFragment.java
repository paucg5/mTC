package edu.ub.d2in.mtc.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class OkDialogFragment extends DialogFragment {
	
	public interface OkDialogListener {
		public void onOkButtonClicked(DialogInterface dialog, String tag);
	}

	private static final String DIALOG_TAG = "dialog_tag";
	private static final String DIALOG_TITLE = "dialog_title";
	private static final String DIALOG_MESSAGE = "dialog_message";
	private static final String DIALOG_BUTTON = "dialog_button";
	
	private String tag;
	private OkDialogListener listener;
	private String title = null;
	private String message = null;
	private String dismissButton = null;
	
	public OkDialogFragment() {
		//empty constructor
	}
	
	/**
	 * Initializes an 'ok'-type dialog fragment, which is capable of showing
	 * a title, a message and just one button, which dismisses the dialog.
	 * Use this kind of dialog to show important information to the users 
	 * or to warn them about something. The activity calling this dialog MUST
	 * implement the {@code OkDialogListener} interface.
	 * 
	 * @param tag <b>required</b> a tag identifying which OkDialog is this one
	 * @param title the title of the dialog or {@code null} to leave it empty
	 * @param message the message of the dialog or {@code null} to leave it empty
	 * @param dismissButton the title of the dismiss button or {@code null} (defaults to "Ok")
	 */
	public static final OkDialogFragment newInstance(String tag, String title, String message, String dismissButton) {
		OkDialogFragment dialog = new OkDialogFragment();
		Bundle bundle = new Bundle();
		
		if (tag != null) {
			bundle.putString(DIALOG_TAG, tag);
		}
		else {
			throw new IllegalArgumentException("The tag argument must not be null");
		}
		
		if (title != null) bundle.putString(DIALOG_TITLE, title);
		if (message != null) bundle.putString(DIALOG_MESSAGE, message);
		if (dismissButton != null) bundle.putString(DIALOG_BUTTON, dismissButton);
		
		dialog.setArguments(bundle);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		tag = args.getString(DIALOG_TAG);
		title = args.getString(DIALOG_TITLE);
		message = args.getString(DIALOG_MESSAGE);
		dismissButton = args.getString(DIALOG_BUTTON);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new Builder(getActivity());
		if (title != null) builder.setTitle(title);
		if (message != null) builder.setMessage(message);
		builder.setPositiveButton(dismissButton == null ? getString(android.R.string.ok) : dismissButton, 
				new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				listener.onOkButtonClicked(dialog, tag);
			}
		});
		return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OkDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement " 
					+ OkDialogListener.class.getSimpleName());
		}
	}
}
