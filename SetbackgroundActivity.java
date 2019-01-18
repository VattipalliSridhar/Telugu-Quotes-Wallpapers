package com.appnava.incomingcalllock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetbackgroundActivity extends Activity
{
	RelativeLayout toolbarlyout;
	TextView toolbartitle;
	Button toolbarback1;
	int width,height;
	
	Button btn_change;
    Button btn_set;
    Cursor cursor;
    RelativeLayout img_back;
    ImageView img_gal;
    String path;
    Integer[] pics_large=new Integer[]{Integer.valueOf(R.drawable.ic_launcher), 
    		Integer.valueOf(R.drawable.gradient1), Integer.valueOf(R.drawable.gradient2), 
  		   Integer.valueOf(R.drawable.gradient4), Integer.valueOf(R.drawable.gradient3), 
  		   Integer.valueOf(R.drawable.gradient5), Integer.valueOf(R.drawable.gradient6), 
  		   Integer.valueOf(R.drawable.gradient7), Integer.valueOf(R.drawable.gradient8)};
    int pos;
    Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setbackground);
		
		DisplayMetrics displayMetrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width=displayMetrics.widthPixels;
		height=displayMetrics.heightPixels;
		
		toolbarlyout=(RelativeLayout)findViewById(R.id.setbacktoolbar);
		toolbarlyout.getLayoutParams().height=height/12;
		
		toolbarback1=(Button)findViewById(R.id.settoolbarback);
		toolbarback1.getLayoutParams().height=width/12;
		toolbarback1.getLayoutParams().width=width/12;
		toolbarback1.setBackgroundResource(R.drawable.back);
		MarginLayoutParams param1=(MarginLayoutParams)toolbarback1.getLayoutParams();
		param1.leftMargin=width/20;
		
		toolbarback1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				SetbackgroundActivity.this.finish();
			}
		});
		
		toolbartitle = (TextView) findViewById(R.id.settoolbartitle);
        if (toolbartitle != null)
        {
        	toolbartitle.setText("Set Background");
        }
        MarginLayoutParams param=(MarginLayoutParams)toolbartitle.getLayoutParams();
		param.leftMargin=width/6;
		
		
		
		img_back = (RelativeLayout) findViewById(R.id.img_main);
        btn_set = (Button) findViewById(R.id.btn_set);
        btn_set.getLayoutParams().height=width/7;
        btn_set.getLayoutParams().width=width/7;
        btn_change = (Button) findViewById(R.id.btn_change);
        btn_change.getLayoutParams().height=width/7;
        btn_change.getLayoutParams().width=width/7;
        img_gal = (ImageView) findViewById(R.id.img_main_large);
        pos = getIntent().getIntExtra("pos", 1);
        path = getIntent().getStringExtra("path");
        
        
        if (path.equalsIgnoreCase("default"))
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), pics_large[pos].intValue());
            img_back.setBackgroundResource(pics_large[pos].intValue());
            btn_change.setVisibility(View.GONE);
        }
        else
        {
            Bitmap my_btmp = BitmapFactory.decodeFile(path);
            btn_change.setVisibility(View.VISIBLE);
            img_back.setVisibility(View.GONE);
            img_gal.setImageBitmap(my_btmp);
        }
        btn_set.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Editor editor = getSharedPreferences("back", 0).edit();
	            editor.putString("path", SetbackgroundActivity.this.path);
	            editor.putInt("pos", SetbackgroundActivity.this.pos);
	            editor.commit();
	            SetbackgroundActivity.this.finish();
			}
		});
        btn_change.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				if (ContextCompat.checkSelfPermission(SetbackgroundActivity.this, "android.permission.READ_EXTERNAL_STORAGE") == 0 
						&& ContextCompat.checkSelfPermission(SetbackgroundActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0)
				{
	                Intent intent = new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI);
	                intent.setType("image/*");
	                intent.putExtra("return-data", false);
	                SetbackgroundActivity.this.startActivityForResult(Intent.createChooser(intent, "Complete action using"), 100);
	                return;
	            }
	            ActivityCompat.requestPermissions(SetbackgroundActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
	        
			}
		});
	}
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        if (resultCode == -1)
        {
            switch (requestCode)
            {
                case 100:
                    this.uri = data.getData();
                    try
                    {
                        Options options = new Options();
                        String[] filePath = new String[]{"_data"};
                        this.cursor = getContentResolver().query(data.getData(), filePath, null, null, null);
                        this.cursor.moveToFirst();
                        this.path = this.cursor.getString(this.cursor.getColumnIndex(filePath[0]));
                        this.cursor.close();
                        Bitmap my_btmp = BitmapFactory.decodeFile(this.path);
                        if (my_btmp.getHeight() > 1248 || my_btmp.getWidth() > 1248)
                        {
                            my_btmp = resizeImageForImageView(my_btmp);
                        }
                        this.img_back.setVisibility(8);
                        this.img_gal.setImageBitmap(my_btmp);
                    }
                    catch (Exception e)
                    {
                    	
                    }
                default:
            }
        }
    }
	
	
    public Bitmap resizeImageForImageView(Bitmap bitmap)
    {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        if (originalHeight > originalWidth)
        {
            newHeight = 1248;
            newWidth = (int) (((float) 1248) * (((float) originalWidth) / ((float) originalHeight)));
        }
        else if (originalWidth > originalHeight)
        {
            newWidth = 1220;
            newHeight = (int) (((float) 1220) * (((float) originalHeight) / ((float) originalWidth)));
        } 
        else if (originalHeight == originalWidth)
        {
            newHeight = 1248;
            newWidth = 1248;
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

	
}
