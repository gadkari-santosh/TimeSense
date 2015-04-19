package com.handyapps.timesense.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

public class Dialog {

	public static void show(final Context activity, 
							final String message, 
							final String title,
							final ICallback callback,
							final boolean exitOnYes) {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		// set title
		alertDialogBuilder.setTitle(Html.fromHtml("<font color='#33B5E5'>"+title+"</font>"));

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								// Do nothing.
							}
						})
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {

									callback.call(null);
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void show(final Context activity, 
			final String message, 
			final String title,
			final ICallback yesCallBack,
			final ICallback noCallBack,
			final boolean exitOnYes) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		
		// set title
		alertDialogBuilder.setTitle(Html.fromHtml("<font color='#33B5E5'>"+title+"</font>"));
		
		// set dialog message
		alertDialogBuilder
		.setMessage(message)
		.setCancelable(false)
		.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						noCallBack.call(null);
					}
				})
		.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
		
						yesCallBack.call(null);
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static void show(final Context context, 
			final String message, 
			final String title) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			
			// set title
			alertDialogBuilder.setTitle(Html.fromHtml("<font color='#33B5E5'>"+title+"</font>"));
			
			// set dialog message
			alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							// Do nothing.
						}
					});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
	}
}
