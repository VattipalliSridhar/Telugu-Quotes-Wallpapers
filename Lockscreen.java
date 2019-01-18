package com.appnava.incomingcalllock;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Lockscreen extends Service
{
	int count;
	Cursor cur;
    SQLiteDatabase db;
    
	LayoutParams mLayoutParams;
	WindowManager wm;
	View root;
	BroadcastReceiver disable_screen;
	boolean stopped;
	TextView txt_caller_name;
	String name;
    String number;
    String image;
    
    ImageView img_hang;
    RelativeLayout img_background;
    ImageView img_clear;
    ImageView img_gal;
    

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    TextView img_0;
    TextView img_1;
    TextView img_2;
    TextView img_3;
    TextView img_4;
    TextView img_5;
    TextView img_6;
    TextView img_7;
    TextView img_8;
    TextView img_9;
    
    String password;
    
    Integer[] pics_large=new Integer[]{Integer.valueOf(R.drawable.ic_launcher), 
    		Integer.valueOf(R.drawable.gradient1), Integer.valueOf(R.drawable.gradient2), 
   		   Integer.valueOf(R.drawable.gradient4), Integer.valueOf(R.drawable.gradient3), 
   		   Integer.valueOf(R.drawable.gradient5), Integer.valueOf(R.drawable.gradient6), 
   		   Integer.valueOf(R.drawable.gradient7), Integer.valueOf(R.drawable.gradient8)};
    
	public Lockscreen()
	{
		this.disable_screen = new stops();
    }
	
	class stops extends BroadcastReceiver
	{
		public void onReceive(Context context, Intent intent)
        {
            wm.removeView(root);
            stopSelf();
            Process.killProcess(Process.myPid());
            stopService(new Intent(Lockscreen.this, Lockscreen.class));
        }
    }
    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
    	try
		{
			number = intent.getStringExtra("number");
            name = intent.getStringExtra("name");
            image = intent.getStringExtra("image");
        }
		catch (Exception e)
		{
        }
        
        init();
    	
    	 return super.onStartCommand(intent, flags, startId);
    	
	}
    private void init()
	{
    	Log.e("msg", "msg3");
		//this.mLayoutParams = new LayoutParams(-1, -1, 2010, 6948224, -3); 
		this.mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT, 
				LayoutParams.TYPE_SYSTEM_ERROR, 
				LayoutParams.FLAG_TRANSLUCENT_STATUS, 
				PixelFormat.OPAQUE);
	    this.wm = (WindowManager) getSystemService("window");
        root = LayoutInflater.from(this).inflate(R.layout.lockscreen_layout, null);
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        wm.addView(root, mLayoutParams);
        
        
        this.img_background = (RelativeLayout) this.root.findViewById(R.id.img_background);
        this.img_clear = (ImageView) this.root.findViewById(R.id.img_clear);
        this.img_gal = (ImageView) this.root.findViewById(R.id.img_main_large);
        
        this.img_clear = (ImageView) this.root.findViewById(R.id.img_clear);
        
        this.count=1;
        
        this.img1 = (ImageView) this.root.findViewById(R.id.img1);
        this.img2 = (ImageView) this.root.findViewById(R.id.img2);
        this.img3 = (ImageView) this.root.findViewById(R.id.img3);
        this.img4 = (ImageView) this.root.findViewById(R.id.img4);
        
        this.img_1 = (TextView) this.root.findViewById(R.id.img_1);
        this.img_2 = (TextView) this.root.findViewById(R.id.img_2);
        this.img_3 = (TextView) this.root.findViewById(R.id.img_3);
        this.img_4 = (TextView) this.root.findViewById(R.id.img_4);
        this.img_5 = (TextView) this.root.findViewById(R.id.img_5);
        this.img_6 = (TextView) this.root.findViewById(R.id.img_6);
        this.img_7 = (TextView) this.root.findViewById(R.id.img_7);
        this.img_8 = (TextView) this.root.findViewById(R.id.img_8);
        this.img_9 = (TextView) this.root.findViewById(R.id.img_9);
        this.img_0 = (TextView) this.root.findViewById(R.id.img_0);
        
        this.img_0.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(0);
			}
		});
        this.img_1.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(1);
			}
		});
        this.img_2.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(2);
			}
		});
        this.img_3.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(3);
			}
		});
        this.img_4.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(4);
			}
		});
        this.img_5.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(5);
			}
		});
        this.img_6.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(6);
			}
		});
        this.img_7.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(7);
			}
		});
        this.img_8.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(8);
			}
		});
        this.img_9.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Lockscreen.this.checkpass(9);
			}
		});
        
        
        registerReceiver(disable_screen, new IntentFilter("disable_screen"));
        txt_caller_name = (TextView)root.findViewById(R.id.txt_caller_name);
        
        if (getSharedPreferences(getPackageName(), 0).getInt("onoff_display", 0) != 0)
        {
            txt_caller_name.setText("Unknown");
        }
        else if (name == null)
        {
            txt_caller_name.setText(number);
        }
        else
        {
            txt_caller_name.setText(name);
        }
        /*txt_caller_name.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				wm.removeView(root);
	            stopSelf();
			}
		});*/
        
        
        SharedPreferences back = getSharedPreferences("back", 0);
        String path = back.getString("path", "default");
        int pos = back.getInt("pos", 1);
        Log.e("msg", "Background:   " + pos);
        if (path.equalsIgnoreCase("default"))
        {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), this.pics_large[pos].intValue());
            this.img_background.setBackgroundResource(this.pics_large[pos].intValue());
            this.img_gal.setVisibility(View.GONE);
        }
        else
        {
            this.img_gal.setImageBitmap(BitmapFactory.decodeFile(path));
            this.img_background.setVisibility(View.GONE);
        }
        
        this.img_hang=(ImageView)root.findViewById(R.id.img_hang);
        this.img_hang.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
            	//call reject code
                try
                {
                	String serviceManagerName = "android.os.ServiceManager";
                    String serviceManagerNativeName = "android.os.ServiceManagerNative";
                    String telephonyName = "com.android.internal.telephony.ITelephony";
                    Class<?> telephonyClass;
                    Class<?> telephonyStubClass;
                    Class<?> serviceManagerClass;
                    Class<?> serviceManagerNativeClass;
                    Method telephonyEndCall;
                    Object telephonyObject;
                    Object serviceManagerObject;
                    telephonyClass = Class.forName(telephonyName);
                    telephonyStubClass = telephonyClass.getClasses()[0];
                    serviceManagerClass = Class.forName(serviceManagerName);
                    serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                    Method getService = serviceManagerClass.getMethod("getService", String.class);
                    Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                    Binder tmpBinder = new Binder();
                    tmpBinder.attachInterface(null, "fake");
                    serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
                    IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                    Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                    telephonyObject = serviceMethod.invoke(null, retbinder);
                    telephonyEndCall = telephonyClass.getMethod("endCall");
                    telephonyEndCall.invoke(telephonyObject);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("unable", "msg cant dissconect call....");

                }
            }
        });
        
        this.img_clear.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Lockscreen.this.img1.setImageResource(R.drawable.star);
                Lockscreen.this.img2.setImageResource(R.drawable.star);
                Lockscreen.this.img3.setImageResource(R.drawable.star);
                Lockscreen.this.img4.setImageResource(R.drawable.star);
                Lockscreen.this.count = 1;
                Lockscreen.this.password = null;
            }
        });
	}
    
    protected void checkpass(int i)
    {
		switch (this.count)
		{
		case 1:
			this.img1.setImageResource(R.drawable.star_select);
			this.password = String.valueOf(i);
            this.count++;
			
			break;
		case 2:
			this.img2.setImageResource(R.drawable.star_select);
			this.password += String.valueOf(i);
            this.count++;
			
			break;
		case 3:
			this.img3.setImageResource(R.drawable.star_select);
			this.password += String.valueOf(i);
            this.count++;
			
			break;
			
		case 4:
			this.img4.setImageResource(R.drawable.star_select);
            this.password += String.valueOf(i);
            this.count++;
            this.db = openOrCreateDatabase("db_call", 0, null);
            this.cur = this.db.rawQuery("select * from tbl_pasword;", null);
            int coun = this.cur.getCount();
            Log.i("msg", "data count" + coun);
            if (coun != 0)
            {
                while (this.cur.moveToNext())
                {
                    Log.i(" ", "local");
                    String password = this.cur.getString(this.cur.getColumnIndex("password"));
                    Log.e("msg ", "enter password " + password);
                    Log.e("msg ", "secure password "+ this.password);
                    if (this.password.equalsIgnoreCase(password))
                    {
                    	Log.e("msg ", "password matched ");
                        new Handler().postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                ((WindowManager) Lockscreen.this.getSystemService("window")).removeView(Lockscreen.this.root);
                                Lockscreen.this.stopSelf();
                                Process.killProcess(Process.myPid());
                            }
                        }, 700);
                        stopService(new Intent(this, Lockscreen.class));
                    }
                    else
                    {
                    	Log.e("msg ", "password not matched ");
                    	
                        Lockscreen.this.img1.setImageResource(R.drawable.star);
                        Lockscreen.this.img2.setImageResource(R.drawable.star);
                        Lockscreen.this.img3.setImageResource(R.drawable.star);
                        Lockscreen.this.img4.setImageResource(R.drawable.star);
                        this.count = 1;
                        this.password = null;
                    }
                }
            }
            break;

		default:
			break;
		}
	}

	public void onDestroy()
    {
        super.onDestroy();
        if (this.disable_screen != null)
        {
            unregisterReceiver(this.disable_screen);
        }
        setStopped(true);
    }
    
    public void setStopped(boolean stopped)
    {
        this.stopped = stopped;
    }
    public boolean isStopped()
    {
        return this.stopped;
    }
    
   

	@Override
	public IBinder onBind(Intent intent)
	{
		
		return null;
	}

}
