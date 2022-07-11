package com.naa.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;


public class Messagebox {
	public interface DoubleRunnable extends Runnable {
		void run();
		void runUI();
	}
	public static void showDialog(Context context, String title, String message, String button1, String button2,   DialogInterface.OnClickListener  listener1,   DialogInterface.OnClickListener  listener2){
		Builder dlg = new AlertDialog.Builder(context);
		if (title!=null  && !title.equals("")) {
			dlg.setTitle(title);
		}
		if (message!=null  && !message.equals("")) {
			dlg.setMessage(message);
		}

		if (button1!=null && !button1.equals("") && button1!=null && !button1.equals("")){
			dlg.setPositiveButton(button1, listener1);
			dlg.setNegativeButton(button2, listener2);
		}else if (button1!=null && !button1.equals("")  ){
			dlg.setNeutralButton(button1, listener1);
		}
		dlg.setCancelable(false);
		dlg.create().show();
	}
	public static void showDialog(Context context, View layout, boolean cancelable){
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setView(layout);

		dlg.setCancelable(cancelable);
		AlertDialog dialog = dlg.create();
		layout.setTag(dialog);





		dialog.show();
	}
}
