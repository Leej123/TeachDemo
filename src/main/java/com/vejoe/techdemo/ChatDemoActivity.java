package com.vejoe.techdemo;

import android.os.Bundle;

import com.vejoe.lib.voice.activity.ChatActivity;
import com.vejoe.lib.voice.faq.FaqSystem;
import com.vejoe.lib.voice.faq.FaqSystemBaseFile;
import com.vejoe.lib.voice.iflytek.IflyTekTokenizer;
import com.vejoe.lib.voice.keyword.KeywordExtractImp;
import com.vejoe.lib.voice.keyword.KeywordExtractor;
import com.vejoe.lib.voice.keyword.StopWords;
import com.vejoe.lib.voice.keyword.StopWordsWrapper;
import com.vejoe.lib.voice.tokenizer.Tokenizer;

public class ChatDemoActivity extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        iflyTekSettings = ((TechDemoApp) getApplication()).getIflyTekSettings();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("闲聊");

//        Tokenizer tokenizer = new IflyTekTokenizer(iflyTekSettings);
//        StopWords stopWords = new StopWordsWrapper(this);
//        KeywordExtractor extractor = new KeywordExtractImp(stopWords);
//        faqSystem = new FaqSystemBaseFile(tokenizer, extractor, "");
//        faqSystem.init(this);
    }

    @Override
    protected boolean onRecognizerResult(String result) {
        return false;
    }
}
