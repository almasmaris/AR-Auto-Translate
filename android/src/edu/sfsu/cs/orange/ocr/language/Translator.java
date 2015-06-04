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
package edu.sfsu.cs.orange.ocr.language;

import edu.sfsu.cs.orange.ocr.CaptureActivity;
import android.app.Activity;

/**
 * 
 */
public class Translator {

  public static final String BAD_TRANSLATION_MSG = "[Translation unavailable]";
  //private static DatabaseHelper dbHelper;
  private final CaptureActivity activity;
  
  private Translator(CaptureActivity activity) {  
    // Private constructor to enforce noninstantiability
	  this.activity = activity;
	  
	  //dbHelper = DatabaseHelper.getInstance(this.activity.getBaseContext());
	  
  }
  
  static String translate(Activity activity, DatabaseHelper dbHelper, String sourceText) {   
   
	  	String Translation;
    	
    	Translation = dbHelper.findTranslation(sourceText);
    	
    	if(Translation != null){
    		return Translation;
    	}
    
    
    return BAD_TRANSLATION_MSG;
  }
}