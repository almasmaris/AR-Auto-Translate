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

import android.app.Activity;

/**
 * Delegates translation requests to the appropriate translation service.
 */
public class Translator {

  public static final String BAD_TRANSLATION_MSG = "[Translation unavailable]";
  
  private Translator(Activity activity) {  
    // Private constructor to enforce noninstantiability
  }
  
  static String translate(Activity activity, String sourceLanguageCode, String targetLanguageCode, String sourceText) {   
    
    // Check preferences to determine which translation API to use--Google, or Bing.
    String api = "Google Translate"; //google doang
    
    // Delegate the translation based on the user's preference. Langsung google
    if (api.equals("Google Translate")) {
      
      // Get the correct code for the source language for this translation service.
      sourceLanguageCode = TranslatorGoogle.toLanguage("Indonesian");      
      
      return TranslatorGoogle.translate(sourceLanguageCode, targetLanguageCode, sourceText);
    }
    return BAD_TRANSLATION_MSG;
  }
}