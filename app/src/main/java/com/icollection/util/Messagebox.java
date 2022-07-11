package com.icollection.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.icollection.R;
import com.naa.data.UtilityAndroid;


public class Messagebox {

	private static ProgressDialog showProgresBar(Context context, String message) {
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(message);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		return mProgressDialog;
	}

	public static AlertDialog DialogBox(Activity context, String title, String message) {
		Builder dlg = new Builder(context);
		dlg.setTitle(title);
		dlg.setMessage(message);
		return dlg.create();
	}

	public interface DoubleRunnable {
		public void run();

		public void runUI();
	}

	public static void showProsesBar(Context context, DoubleRunnable run) {
		showProsesBar(context, new Runnable() {
			private DoubleRunnable run;

			public Runnable get(DoubleRunnable run) {
				this.run = run;
				return this;
			}

			public void run() {
				run.run();
			}
		}.get(run), new Runnable() {
			private DoubleRunnable run;

			public Runnable get(DoubleRunnable run) {
				this.run = run;
				return this;
			}

			public void run() {
				run.runUI();
			}
		}.get(run));
	}

	public static void newTask(final Runnable run) {
		newTask(new DoubleRunnable() {
			public void runUI() {
			}

			public void run() {
				run.run();
			}
		});
	}

	public static void newTask(DoubleRunnable run) {
		new AsyncTask<DoubleRunnable, Void, DoubleRunnable>() {
			protected DoubleRunnable doInBackground(DoubleRunnable... params) {
				params[0].run();
				return params[0];
			}

			protected void onPostExecute(DoubleRunnable result) {
				result.runUI();
			}

			protected void onPreExecute() {
			}

			protected void onProgressUpdate(Void... values) {
			}
		}.execute(run);
	}

	public static void showProsesBar(Context context, Runnable run, Runnable ui) {
		new AsyncTask<Runnable, Void, Runnable>() {
			protected Runnable doInBackground(Runnable... params) {
				params[0].run();
				return params[1];
			}

			protected void onPostExecute(Runnable result) {
				result.run();
			}

			protected void onPreExecute() {
			}

			protected void onProgressUpdate(Void... values) {
			}
		}.execute(run, new Runnable() {
			private Runnable ui;
			private ProgressDialog prb;

			public Runnable get(Runnable ui, ProgressDialog prb) {
				this.ui = ui;
				this.prb = prb;
				return this;
			}

			public void run() {
				prb.dismiss();
				this.ui.run();

			}
		}.get(ui, showProgresBar(context, "Please Wait . . . ")));
	}

	public static void showDialog(Context context, String title, String[] data, DialogInterface.OnClickListener listener) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);
		Builder dlg = new Builder(context);
		if (title != null) {
			if (title.trim().length() >= 1) {
				dlg.setTitle(title);
			}
		}

		dlg.setAdapter(adapter, listener);
		dlg.create().show();
	}

	public static void showDialog(Context context, String title, String[] data, DialogInterface.OnClickListener listener, int layout) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, layout, data);
		Builder dlg = new Builder(context);
		if (title != null) {
			if (title.trim().length() >= 1) {
				dlg.setTitle(title);
			}
		}
		dlg.setAdapter(adapter, listener);
		dlg.create().show();
	}

	public static void showDialogSingleChoiceItems(Context context, String title, String[] data, int i, DialogInterface.OnClickListener listener, DialogInterface.OnKeyListener onKeyListener) {
		Builder dlg = new Builder(context);
		dlg.setSingleChoiceItems(data, i, listener);
		if (title != null) {
			if (title.trim().length() >= 1) {
				dlg.setTitle(title);
			}
		}

		dlg.create().show();
	}



	public static AlertDialog showDialogCustomA(Context context, String title, String[] data, int i, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener){
		Builder dlg = new Builder(context);
		//dlg.setSingleChoiceItems(data, i, listener);

		View v = UtilityAndroid.getInflater(context, R.layout.menuxactivity);
		dlg.setView(v);
		 final AlertDialog alertDialog = dlg.create();
		 //alertDialog.setCancelable(false);
		 alertDialog.setOnCancelListener(onCancelListener);
		//title ="0";//bypass
		v.findViewById(R.id.tblLihat).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				onClickListener.onClick(alertDialog, -2);
			}
		});
		if (title.startsWith("2")){
			v.findViewById(R.id.tblTagih).setVisibility(View.GONE);
		}else{
			v.findViewById(R.id.tblTagih).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					alertDialog.dismiss();
					showDialogCustomA(context,data, listener, onClickListener);
				}
			});
		}

		if (title.startsWith("0")){
			v.findViewById(R.id.tblTidakKunjungan).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					alertDialog.dismiss();
					showDialogCustomB(context,data, listener, onClickListener);
				}
			});
			v.findViewById(R.id.tblBHC).setVisibility(View.GONE);
		}else{
			v.findViewById(R.id.tblTidakKunjungan).setVisibility(View.GONE);

			if (title.contains(":EXIST")){
				v.findViewById(R.id.tblBHC).setVisibility(View.GONE);
			}else{
				v.findViewById(R.id.tblBHC).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						onClickListener.onClick(alertDialog, -1);
					}
				});
			}

		}
		if (title.startsWith("2")){
			v.findViewById(R.id.tblTidakKunjungan).setVisibility(View.GONE);
		}

		if (title.contains("TRUE")){
			v.findViewById(R.id.tblLihat).setVisibility(View.GONE);
			v.findViewById(R.id.tblTagih).setVisibility(View.GONE);
			v.findViewById(R.id.tblTidakKunjungan).setVisibility(View.GONE);
			v.findViewById(R.id.tblBHC).setVisibility(View.VISIBLE);
			v.findViewById(R.id.tblBHC).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onClickListener.onClick(alertDialog, -1);
				}
			});
		}
		//alertDialog.show();
		return alertDialog;
	}
	public static void showDialogCustomA(Context context, final  String[] data,  DialogInterface.OnClickListener listener, DialogInterface.OnClickListener onClickListener){
		Builder dlg = new Builder(context);

		View v = UtilityAndroid.getInflater(context, R.layout.menutagihxactivity);
		dlg.setView(v);
		final AlertDialog alertDialog = dlg.create();
		//alertDialog.setCancelable(false);

		v.findViewById(R.id.tblSudahBayar).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				listener.onClick(alertDialog, 9);
			}
		});

		if (data.length<=1){
			v.findViewById(R.id.tblSudahBayar).setVisibility(View.GONE);
		}
		if (data.length>=1){
			v.findViewById(R.id.tblTarik).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					listener.onClick(alertDialog, 0);
				}
			});
			((Button)v.findViewById(R.id.tblTarik)).setText(data[0]);
		}else{
			v.findViewById(R.id.tblTarik).setVisibility(View.GONE);
		}
		if (data.length>=1+1){
			v.findViewById(R.id.tblBayar).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					listener.onClick(alertDialog, 0+1);
				}
			});
			((Button)v.findViewById(R.id.tblBayar)).setText(data[0+1]);
		}else{
			v.findViewById(R.id.tblBayar).setVisibility(View.GONE);
		}

		if (data.length>=2+1){
			v.findViewById(R.id.tblJanji).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					listener.onClick(alertDialog, 1+1);
				}
			});
			((Button)v.findViewById(R.id.tblJanji)).setText(data[1+1]);
		}else{
			v.findViewById(R.id.tblJanji).setVisibility(View.GONE);
		}
		if (data.length>=3+1){
			v.findViewById(R.id.tblGagal).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					listener.onClick(alertDialog, 2+1);
				}
			});
			((Button)v.findViewById(R.id.tblGagal)).setText(data[2+1]);
		}else{
			v.findViewById(R.id.tblGagal).setVisibility(View.GONE);
		}

		if (data.length>=5){
			v.findViewById(R.id.tblBayarLain).setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					listener.onClick(alertDialog, 4);
				}
			});
			((Button)v.findViewById(R.id.tblBayarLain)).setText(data[4]);
		}else{
			v.findViewById(R.id.tblBayarLain).setVisibility(View.GONE);
		}
		alertDialog.show();
	}
	public static void showDialogCustomB(Context context, final  String[] data,  DialogInterface.OnClickListener listener, DialogInterface.OnClickListener onClickListener){
		Builder dlg = new Builder(context);

		View v = UtilityAndroid.getInflater(context, R.layout.menutkxactivity);
		dlg.setView(v);
		final AlertDialog alertDialog = dlg.create();
		//alertDialog.setCancelable(false);

		v.findViewById(R.id.tblSudahBayar).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickListener.onClick(alertDialog, 10);
			}
		});
		v.findViewById(R.id.tblJanji).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickListener.onClick(alertDialog, 11);
			}
		});
		v.findViewById(R.id.tblTidakSempat).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickListener.onClick(alertDialog, 12);
			}
		});
		alertDialog.show();
	}
	public static void showDialogCustomAA(Context context, String title, String[] data, int i, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener onClickListener){
		Builder dlg = new Builder(context);
		dlg.setSingleChoiceItems(data, i, listener);
		if (title!=null) {
			if (title.trim().length()>=1) {
				dlg.setTitle(title);
			}
		}
		View v = UtilityAndroid.getInflater(context, R.layout.menutagihxactivity);
		dlg.setView(v);
		if (onClickListener!=null){
			dlg.setPositiveButton(" Peta \r\n Survey ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onClickListener.onClick(dialog, 2);
				}
			});
		}
		if (onClickListener!=null){
			dlg.setNeutralButton(" Peta \r\n Alamat ",  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onClickListener.onClick(dialog, 0);
				}
			});
		}
		if (onClickListener!=null){
			dlg.setNegativeButton(" Peta \r\n Bayar ",  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onClickListener.onClick(dialog, 1);
				}
			});
		}
		dlg.create().show();
	}
}
