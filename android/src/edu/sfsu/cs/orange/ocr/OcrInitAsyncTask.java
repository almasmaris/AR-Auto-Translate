/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.cs.orange.ocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
/**
 * Installs the language data required for OCR, and initializes the OCR engine using a background 
 * thread.
 */
final class OcrInitAsyncTask extends AsyncTask<String, String, Boolean> {
  private static final String TAG = OcrInitAsyncTask.class.getSimpleName();

  public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/OCRTest/";

  private CaptureActivity activity;
  private Context context;
  private TessBaseAPI baseApi;
  private ProgressDialog dialog;
  private ProgressDialog indeterminateDialog;
  private final String languageCode;

  /**
   * AsyncTask to asynchronously download data and initialize Tesseract.
   * 
   * @param activity
   *          The calling activity
   * @param baseApi
   *          API to the OCR engine
   * @param dialog
   *          Dialog box with thermometer progress indicator
   * @param indeterminateDialog
   *          Dialog box with indeterminate progress indicator
   * @param languageCode
   *          ISO 639-2 OCR language code
   * @param languageName
   *          Name of the OCR language, for example, "English"
   * @param ocrEngineMode
   *          Whether to use Tesseract, Cube, or both
   */
  OcrInitAsyncTask(CaptureActivity activity, TessBaseAPI baseApi, ProgressDialog dialog, 
      ProgressDialog indeterminateDialog, String languageCode, String languageName) {
    this.activity = activity;
    this.context = activity.getBaseContext();
    this.baseApi = baseApi;
    this.dialog = dialog;
    this.indeterminateDialog = indeterminateDialog;
    this.languageCode = languageCode;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    dialog.setTitle("Please wait");
    dialog.setMessage("Checking for data installation...");
    dialog.setIndeterminate(false);
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setCancelable(false);
    dialog.show();
    activity.setButtonVisibility(false);
  }

  /**
   * In background thread, perform required setup, and request initialization of
   * the OCR engine.
   * 
   * @param params
   *          [0] Pathname for the directory for storing language data files to the SD card
   */
  protected Boolean doInBackground(String... params) {
    //testing
    String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

	for (String path : paths) {
		File dir = new File(path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
				return false;
			} else {
				Log.v(TAG, "Created directory " + path + " on sdcard");
			}
		}

	}
	
	// languageCode.traineddata file with the app (in assets folder)
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	// This area needs work and optimization
	// If language data files are not present, install them
    boolean installSuccess = false;
	if (!(new File(DATA_PATH + "tessdata/" + languageCode + ".traineddata")).exists()) {
		try {

			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open("tessdata/" + languageCode + ".traineddata");
			//GZIPInputStream gin = new GZIPInputStream(in);
			OutputStream out = new FileOutputStream(DATA_PATH
					+ "tessdata/" + languageCode + ".traineddata");

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			//while ((lenf = gin.read(buff)) > 0) {
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			//gin.close();
			out.close();
			
			Log.v(TAG, "Copied " + languageCode + " traineddata");
			Log.v(TAG, "Language data for " + languageCode + " already installed");
			installSuccess = true;
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + languageCode + " traineddata " + e.toString());
		}
	}else {
		installSuccess = true;
		Log.v(TAG, "Language data for " + languageCode + " already installed");
	}

    
    
    // Dismiss the progress dialog box, revealing the indeterminate dialog box behind it
    try {
      dialog.dismiss();
    } catch (IllegalArgumentException e) {
      // Catch "View not attached to window manager" error, and continue
    }

    // Initialize the OCR engine
    if (baseApi.init(DATA_PATH, languageCode)) {
      return installSuccess;
    }
    return false;
  }



  /**
   * Update the dialog box with the latest incremental progress.
   * 
   * @param message
   *          [0] Text to be displayed
   * @param message
   *          [1] Numeric value for the progress
   */
  @Override
  protected void onProgressUpdate(String... message) {
    super.onProgressUpdate(message);
    int percentComplete = 0;

    percentComplete = Integer.parseInt(message[1]);
    dialog.setMessage(message[0]);
    dialog.setProgress(percentComplete);
    dialog.show();
  }

  @Override
  protected void onPostExecute(Boolean result) {
    super.onPostExecute(result);
    
    try {
      indeterminateDialog.dismiss();
    } catch (IllegalArgumentException e) {
      // Catch "View not attached to window manager" error, and continue
    }

    if (result) {
      // Restart recognition
      activity.resumeOCR();
    } else {
      activity.showErrorMessage("Error", "cannot install data");
    }
  }
}