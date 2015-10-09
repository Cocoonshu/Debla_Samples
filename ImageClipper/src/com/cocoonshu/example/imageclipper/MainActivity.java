package com.cocoonshu.example.imageclipper;

import com.cocoonshu.example.imageclipper.view.ImageClipper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ImageView    mImgBackground   = null;
	private ImageClipper mIcpImageClipper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupViews();
		setupListeners();
	}

	private void setupViews() {
		mImgBackground   = (ImageView) findViewById(R.id.ImageViewBackground);
		mIcpImageClipper = (ImageClipper) findViewById(R.id.ImageClipper);
	}
	
	private void setupListeners() {
		// TODO Auto-generated method stub
		
	}
	
}
