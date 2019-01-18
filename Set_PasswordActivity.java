package com.appnava.incomingcalllock;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Set_PasswordActivity extends Activity
{
	RelativeLayout viewplace,toolbarlyout;
	TextView toolbartitle;
	Button toolbarback1;
	int width,height;
	
	public static String[] permissions;
    Button btn_submite;
    List<String> categories;
    Cursor cur;
    SQLiteDatabase db;
    EditText edit_ans1;
    EditText edit_cpasswd;
    EditText edit_oldpass;
    EditText edit_passwd;
    ProgressDialog progress;
    String question;
    Spinner spnr_question;
    String title;
    TextView txt_forget;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set__password);
		this.categories = new ArrayList<String>();
		DisplayMetrics displayMetrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		width=displayMetrics.widthPixels;
		height=displayMetrics.heightPixels;
		
		toolbarlyout=(RelativeLayout)findViewById(R.id.toolbarlyout);
		toolbarlyout.getLayoutParams().height=height/12;
		
		toolbarback1=(Button)findViewById(R.id.toolbarback1);
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
				Set_PasswordActivity.this.finish();
			}
		});
		
		toolbartitle = (TextView) findViewById(R.id.toolbartitle);
        if (toolbartitle != null)
        {
        	toolbartitle.setText(getIntent().getStringExtra("title"));/*setTitle(getIntent().getStringExtra("title"));*/
        }
        MarginLayoutParams param=(MarginLayoutParams)toolbartitle.getLayoutParams();
		param.leftMargin=width/6;
		
		viewplace=(RelativeLayout)findViewById(R.id.viewplace);
		viewplace.getLayoutParams().height=height/6;
		
		
		main();
		
	}
	
	private void main()
	{
		edit_ans1 = (EditText) findViewById(R.id.edit_ans1);
        edit_cpasswd = (EditText) findViewById(R.id.edit_cpasswd);
        edit_oldpass = (EditText) findViewById(R.id.edit_oldpass);
        edit_passwd = (EditText) findViewById(R.id.edit_passwd);
        spnr_question = (Spinner) findViewById(R.id.spn_que1);
        txt_forget = (TextView) findViewById(R.id.txt_forget);
        btn_submite = (Button) findViewById(R.id.btn_submit);
        title = getIntent().getStringExtra("title");
        
        if (this.title.equalsIgnoreCase("Change Password"))
        {
            this.edit_ans1.setVisibility(View.GONE);
            this.edit_oldpass.setVisibility(View.VISIBLE);
            this.spnr_question.setVisibility(View.GONE);
            this.txt_forget.setVisibility(View.VISIBLE);
        }
        else
        {
        	Log.e("msg", "set password screen");
            this.edit_ans1.setVisibility(View.VISIBLE);
            this.edit_oldpass.setVisibility(View.GONE);
            this.spnr_question.setVisibility(View.VISIBLE);
            this.txt_forget.setVisibility(View.GONE);
        }
        
        fillspinner();
        
        this.spnr_question.setOnItemSelectedListener(new OnItemSelectedListener()
        {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				question = parent.getItemAtPosition(position).toString();
				Log.e("msg", "que  :"+question);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
		});
        this.btn_submite.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				if (VERSION.SDK_INT >= 23)
				{
	                /*try {
	                    ArrayList<String> stringArrayList = new ArrayList<String>();
	                    for (String permission : Set_PasswordActivity.permissions)
	                    {
	                        if (getApplication().checkSelfPermission(permission) != 0)
	                        {
	                            stringArrayList.add(permission);
	                        }
	                    }
	                    if (stringArrayList.isEmpty())
	                    {
	                        setPassword();
	                    }
	                    requestPermissions((String[]) stringArrayList.toArray(new String[stringArrayList.size()]), 1);
	                    return;
	                }
	                catch (Exception e)
	                {
	                    return;
	                }*/
					setPassword();
	            }
				else
				{
					setPassword();
				}
	            
	            
			}
		});
        this.txt_forget.setOnClickListener(new OnClickListener()
        {
			
			@Override
			public void onClick(View v)
			{
				Set_PasswordActivity.this.startActivity(new Intent(Set_PasswordActivity.this, ForgotActivity.class));
			}
		});
	}
	
	
    private void fillspinner()
    {
        this.categories.clear();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Set_PasswordActivity.this, R.layout.support_simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(17367049);
        this.spnr_question.setAdapter(dataAdapter);
        this.categories.add(new String("Select security question"));
        this.categories.add(new String("Whats your favourite food?"));
        this.categories.add(new String("Whats your nick name?"));
        this.categories.add(new String("Whats your childhood teacher name?"));
        this.categories.add(new String("Whats your dream job?"));
        this.categories.add(new String("Whats your favourite game?"));
        dataAdapter = new ArrayAdapter<String>(Set_PasswordActivity.this,R.layout.support_simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(17367049);
        this.spnr_question.setAdapter(dataAdapter);
    }
    
    void setPassword()
    {
        if (this.title.equalsIgnoreCase("Change password"))
        {
            if (this.edit_oldpass.getText().toString().length() ==0
            		|| this.edit_cpasswd.getText().toString().length()  ==0 ||
            		this.edit_passwd.getText().toString().length()  ==0)
            {
                Toast.makeText(Set_PasswordActivity.this, "Fields can not be empty", 0).show();
            }
            else if (this.edit_oldpass.getText().toString().length() < 4 
            		|| this.edit_cpasswd.getText().toString().length() < 4 ||
            		this.edit_passwd.getText().toString().length() < 4)
            {
                Toast.makeText(Set_PasswordActivity.this, "Please enter password 4 digits", 0).show();
            }
            else if (this.edit_passwd.getText().toString().equals(this.edit_cpasswd.getText().toString()))
            {
                new CallWebService().execute(new Void[0]);
            }
            else
            {
                Toast.makeText(Set_PasswordActivity.this, "Password not match with confirm password", 0).show();
            }
        }
        else if (this.edit_ans1.getText().toString().length() == 0 || 
        		this.question.equalsIgnoreCase("Select security question"))
        {
            Toast.makeText(Set_PasswordActivity.this, "Fields can not be empty", 0).show();
        }
        else if ( this.edit_cpasswd.getText().toString().length() < 4
        		|| this.edit_passwd.getText().toString().length() < 4 )
        {
            Toast.makeText(Set_PasswordActivity.this, "Please enter password 4 digits", 0).show();
        }
        else if (this.edit_passwd.getText().toString().equals(this.edit_cpasswd.getText().toString()))
        {
            new CallWebService().execute(new Void[0]);
        }
        else
        {
            Toast.makeText(Set_PasswordActivity.this, "Password not match with confirm password", 0).show();
        }
    }
    
    public class CallWebService extends AsyncTask<Void, Void, Void>
    {

		
		protected Void doInBackground(Void... params)
		{
			if (title.equalsIgnoreCase("Change password"))
			{
                db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
                cur = db.rawQuery("select * from tbl_pasword;", null);
                if (cur.getCount() != 0)
                {
                    while (cur.moveToNext())
                    {
                        if (cur.getString(cur.getColumnIndex("password")).equals(edit_oldpass.getText().toString()))
                        {
                            db.execSQL("update tbl_pasword set  password='" + edit_passwd.getText().toString() + "' where password='" + edit_oldpass.getText().toString() + "'");
                            db.close();
                            Set_PasswordActivity.this.runOnUiThread(new Runnable()
                            {
								
								@Override
								public void run()
								{
									Toast.makeText(Set_PasswordActivity.this.getApplicationContext(), "Password Changed", 0).show();
								}
							});
                            Set_PasswordActivity.this.finish();
                        }
                        else
                        {
                        	Set_PasswordActivity.this.runOnUiThread(new Runnable()
                        	{
								
								@Override
								public void run()
								{
									Toast.makeText(Set_PasswordActivity.this.getApplicationContext(), "Password Not match", 0).show();
								}
							});
                        }
                    }
                }
            }
			else
			{
                db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
                db.execSQL("insert into tbl_pasword values('" + edit_passwd.getText().toString() + "','" + question + "','" + edit_ans1.getText().toString().replace("'", "''") + "');");
                Set_PasswordActivity.this.runOnUiThread(new Runnable()
                {
					
					@Override
					public void run()
					{
						SharedPreferences call = Set_PasswordActivity.this. getSharedPreferences(getPackageName(), 0);
		                Toast.makeText(Set_PasswordActivity.this.getApplicationContext(), "Password set successfully", 0).show();
		                Editor editor = call.edit();
		                editor.putInt("onoff", 1);
		                editor.commit();
					}
				});
                setResult(1011);
                Set_PasswordActivity.this.finish();
            }
            return null;
		}
		
		protected void onPostExecute(Void result)
		{
            Set_PasswordActivity.this.progress.dismiss();
        }

        protected void onPreExecute()
        {
            Set_PasswordActivity.this.progress = ProgressDialog.show(Set_PasswordActivity.this, Set_PasswordActivity.this.title, "Please wait while sending data", true);
            super.onPreExecute();
        }
    	
    }
}
