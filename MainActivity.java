package com.appnava.incomingcalllock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.appnava.incomingcalllock.app.App;
import com.appnava.incomingcalllock.app.ConnectionDetector;
import com.appnava.incomingcalllock.app.XmlDataParser1;
import com.bumptech.glide.Glide;


import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements OnClickListener
{

    private Toolbar toolbar;
    private int width, height;



    private RelativeLayout lyout_selected,
            lyout_all,
            lyout_unknown,
            lyout_onof,
            lyout_onof_displayname,
            lyout_paswd,
            lyout_changeback;
    static RelativeLayout lyout_manage;
    private ToggleButton chk_onof, chk_onof_displayname;
    private TextView txt_onof;
    private TextView txt_onof_displayname;
    private RadioButton rdr_selected, rdr_all, rdr_unknown;
    private TextView txt_select;
    private TextView txt_paswd;

    private MarginLayoutParams layoutParams;
    private ProgressBar pdialog;
    private ProgressDialog progress;
    private String title;

    private int request_code;
    //database
    private Cursor cur;
    private SQLiteDatabase db;
    private List<String> mobnumber;
    private List<String> name;
    private List<String> image;


    //ads loading
    public static ArrayList<App> appsList_Mian = new ArrayList<App>();
    public static ArrayList<App> appsList1 = new ArrayList<App>();
    public static ArrayList<App> appsList2 = new ArrayList<App>();


    private ConnectionDetector connectionDetector;
    private Dialog customDialog;
    private int dialogWidth, dialogHeight;
    private boolean isinternet;


    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler2 = new Handler();
    private ProgressDialog progressDialog;
    private int count = 0;
    private Animation animation;

    public static int dwidth, dheight;

    public static final int RequestPermissionCode = 1;
    private boolean permission=false;
    int currentapiVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();


    }

    @SuppressWarnings("deprecation")
    public void initViews() {


        setContentView(R.layout.activity_main);

        connectionDetector = new ConnectionDetector(MainActivity.this);
        isinternet = connectionDetector.isConnectingToInternet();

        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        if (currentapiVersion >= Build.VERSION_CODES.M)
        {

            if(checkPermission())
            {
                //Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }

        }
        else
        {


        }


        if (isinternet) {
            appsList1 = new ArrayList<App>();
            new LoadExitApps().execute("");
        } else {

        }

        animation = new ScaleAnimation(1.0F, 0.9F, 1.0F, 0.9F,
                Animation.RELATIVE_TO_SELF, (float) 0.5,
                Animation.RELATIVE_TO_SELF, (float) 0.5);
        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        DisplayMetrics size = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(size);
        dwidth = size.widthPixels;
        dheight = size.heightPixels;
        width = size.widthPixels;
        height = size.heightPixels;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle((CharSequence) "Incoming Call Lock");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setHomeButtonEnabled(true);


        create_database();
        initi();
    }

    private void create_database() {
        db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
        db.execSQL("create table if not exists tbl_contact(c_number STRING ,c_name STRING, c_image STRING);");
        db.execSQL("create table if not exists tbl_pasword(password STRING,question STRING,answer STRING);");
        db.close();
    }


    private void initi() {
        Editor editor;
        SharedPreferences call = getSharedPreferences(getPackageName(), 0);


        txt_onof = (TextView) findViewById(R.id.txt_onof);
        txt_onof_displayname = (TextView) findViewById(R.id.txt_onof_displayname);

        title = "Loading....";

        lyout_onof = (RelativeLayout) findViewById(R.id.lyout_onoff);
        lyout_onof.getLayoutParams().height = height / 12;

        chk_onof = (ToggleButton) findViewById(R.id.chk_onof);
        chk_onof.getLayoutParams().height = width / 10;
        chk_onof.getLayoutParams().width = width / 10;
        layoutParams = (MarginLayoutParams) chk_onof.getLayoutParams();
        layoutParams.rightMargin = width / 40;

        lyout_onof_displayname = (RelativeLayout) findViewById(R.id.lyout_onoff_displayname);
        lyout_onof_displayname.getLayoutParams().height = height / 12;

        chk_onof_displayname = (ToggleButton) findViewById(R.id.chk_onof_displayname);
        chk_onof_displayname.getLayoutParams().height = width / 10;
        chk_onof_displayname.getLayoutParams().width = width / 10;
        layoutParams = (MarginLayoutParams) chk_onof_displayname.getLayoutParams();
        layoutParams.rightMargin = width / 40;

        lyout_all = (RelativeLayout) findViewById(R.id.lyout_all);
        lyout_all.getLayoutParams().height = height / 12;
        rdr_all = (RadioButton) findViewById(R.id.radio0);
        rdr_all.getLayoutParams().height = width / 14;
        rdr_all.getLayoutParams().width = width / 14;
        layoutParams = (MarginLayoutParams) rdr_all.getLayoutParams();
        layoutParams.rightMargin = width / 40;

        lyout_unknown = (RelativeLayout) findViewById(R.id.lyout_unknown);
        lyout_unknown.getLayoutParams().height = height / 12;
        rdr_unknown = (RadioButton) findViewById(R.id.radio_unknown);
        rdr_unknown.getLayoutParams().height = width / 14;
        rdr_unknown.getLayoutParams().width = width / 14;
        layoutParams = (MarginLayoutParams) rdr_unknown.getLayoutParams();
        layoutParams.rightMargin = width / 40;

        lyout_selected = (RelativeLayout) findViewById(R.id.lyout_select);
        lyout_selected.getLayoutParams().height = height / 12;
        rdr_selected = (RadioButton) findViewById(R.id.radio1);
        rdr_selected.getLayoutParams().height = width / 14;
        rdr_selected.getLayoutParams().width = width / 14;
        layoutParams = (MarginLayoutParams) rdr_selected.getLayoutParams();
        layoutParams.rightMargin = width / 40;

        txt_select = (TextView) findViewById(R.id.txt_select);

        lyout_manage = (RelativeLayout) findViewById(R.id.lyout_edit);
        lyout_manage.getLayoutParams().height = height / 12;
        txt_paswd = (TextView) findViewById(R.id.txt_paswd);
        lyout_paswd = (RelativeLayout) findViewById(R.id.lyout_paswd);
        lyout_paswd.getLayoutParams().height = height / 12;
        lyout_changeback = (RelativeLayout) findViewById(R.id.lyout_changeback);
        lyout_changeback.getLayoutParams().height = height / 12;


        this.lyout_paswd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Set_PasswordActivity.class);
                i.putExtra("title", MainActivity.this.txt_paswd.getText().toString());
                MainActivity.this.startActivity(i);
            }
        });

        lyout_changeback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, ChangebackgroundActivity.class));
            }
        });

        if (call.getInt("onoff", 0) == 1) {
            chk_onof.setChecked(true);
            editor = call.edit();
            editor.putInt("onoff", 1);
            editor.commit();
            txt_onof.setText("Stop");
        } else {
            chk_onof.setChecked(false);
            editor = call.edit();
            editor.putInt("onoff", 0);
            editor.commit();
            txt_onof.setText("Start");
        }
        if (call.getInt("onoff_display", 0) == 1) {
            chk_onof_displayname.setChecked(false);
            editor = call.edit();
            editor.putInt("onoff_display", 1);
            editor.commit();
            txt_onof_displayname.setText("Show Contact Name");
        } else {
            chk_onof_displayname.setChecked(true);
            editor = call.edit();
            editor.putInt("onoff_display", 0);
            editor.commit();
            txt_onof_displayname.setText("Hide Contact Name");
        }
        if (call.getString("mode", "All").equalsIgnoreCase("Selected")) {
            lyout_manage.setVisibility(View.VISIBLE);
            rdr_selected.setChecked(true);
            txt_select.setText("Add Contact");
            rdr_all.setChecked(false);
            rdr_unknown.setChecked(false);
            editor = call.edit();
            editor.putString("mode", "Selected");
            editor.commit();
        } else if (call.getString("mode", "All").equalsIgnoreCase("All")) {
            lyout_manage.setVisibility(View.GONE);
            rdr_selected.setChecked(false);
            txt_select.setText("Selected Contact");
            rdr_all.setChecked(true);
            rdr_unknown.setChecked(false);
            editor = call.edit();
            editor.putString("mode", "All");
            editor.commit();
        } else {
            lyout_manage.setVisibility(View.GONE);
            rdr_selected.setChecked(false);
            txt_select.setText("Selected Contact");
            rdr_all.setChecked(false);
            rdr_unknown.setChecked(true);
            editor = call.edit();
            editor.putString("mode", "Unknown");
            editor.commit();
        }
        checkpaswd();


        lyout_onof.setOnClickListener(new OnOff(call));
        lyout_onof_displayname.setOnClickListener(new onoffDisplay(call));
        lyout_all.setOnClickListener(new Allcalls(call));
        lyout_unknown.setOnClickListener(new UnknownCall(call));
        lyout_selected.setOnClickListener(this);
        lyout_manage.setOnClickListener(this);


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


    public void no_internet() {
        AlertDialog.Builder builder;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }


        builder.setMessage("No Internet Connection");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more) {
            if (isinternet) {
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse("market://search?q=pub:App+Nava+Pvt+Ltd"));
                MainActivity.this.startActivity(localIntent);
            } else {
                no_internet();
            }

            return true;
        }
        if (id == R.id.like) {
            if (isinternet) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } else {
                no_internet();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            Drawable newIcon = overflowIcon.mutate();
            newIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            toolbar.setOverflowIcon(newIcon);
        }

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the navigation drawer is open, hide action items related to the
        // content
        // view

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            CharSequence menuTitle = menuItem.getTitle();
            SpannableString styledMenuTitle = new SpannableString(menuTitle);
            styledMenuTitle.setSpan(new ForegroundColorSpan(Color.BLACK), 0, menuTitle.length(), 0);
            menuItem.setTitle(styledMenuTitle);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    class Allcalls implements OnClickListener {
        final SharedPreferences val$call;

        Allcalls(SharedPreferences sharedPreferences) {
            val$call = sharedPreferences;
        }

        @Override
        public void onClick(View v) {
            Editor editor = val$call.edit();
            editor.putString("mode", "All");
            editor.commit();
            rdr_all.setChecked(true);
            rdr_selected.setChecked(false);
            rdr_unknown.setChecked(false);
            txt_select.setText("Selected Contact");
            lyout_manage.setVisibility(View.GONE);
        }
    }

    class UnknownCall implements OnClickListener {
        final SharedPreferences val$call;

        UnknownCall(SharedPreferences sharedPreferences) {
            val$call = sharedPreferences;
        }

        @Override
        public void onClick(View v) {
            Editor editor = val$call.edit();
            editor.putString("mode", "Unknown");
            editor.commit();
            rdr_unknown.setChecked(true);
            rdr_all.setChecked(false);
            rdr_selected.setChecked(false);
            txt_select.setText("Selected Contact");
            lyout_manage.setVisibility(View.GONE);
        }
    }


    class OnOff implements OnClickListener {
        final SharedPreferences val$call;
        Editor editor;

        OnOff(SharedPreferences sharedPreferences) {
            val$call = sharedPreferences;
        }

        public void onClick(View v) {
            if (MainActivity.this.checkpaswd() != 1) {
                return;
            }
            if (val$call.getInt("onoff", 0) == 1) {
                Editor editor = val$call.edit();
                editor.putInt("onoff", 0);
                editor.commit();
                chk_onof.setChecked(false);
                txt_onof.setText("Start");
                return;
            }
            editor = val$call.edit();
            editor.putInt("onoff", 1);
            editor.commit();
            chk_onof.setChecked(true);
            txt_onof.setText("Stop");
        }
    }

    class onoffDisplay implements OnClickListener {
        final SharedPreferences valcall;
        Editor editor;

        onoffDisplay(SharedPreferences sharedPreferences) {
            valcall = sharedPreferences;
        }

        public void onClick(View v) {
            if (MainActivity.this.checkpaswd() != 1) {
                return;
            }

            if (valcall.getInt("onoff_display", 0) == 1) {
                Editor editor = valcall.edit();
                editor.putInt("onoff_display", 0);
                editor.commit();
                chk_onof_displayname.setChecked(true);
                txt_onof_displayname.setText("Hide Contact Name");
                return;
            }
            editor = valcall.edit();
            editor.putInt("onoff_display", 1);
            editor.commit();
            chk_onof_displayname.setChecked(false);
            Toast.makeText(getApplicationContext(), "Contact Name OR Number Will Replace With Unknown text", 1).show();
            txt_onof_displayname.setText("Show Contact Name");
        }
    }

    private int checkpaswd() {
        if (db != null) {
            db.close();
        }
        db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
        cur = db.rawQuery("select * from tbl_pasword", null);
        if (cur.getCount() > 0) {
            Log.e("msg", "successfully saved password");
            txt_paswd.setText("Change Password");
            db.close();
            return 1;
        }
        txt_paswd.setText("Set Password");
        Intent i = new Intent(this, Set_PasswordActivity.class);
        i.putExtra("title", txt_paswd.getText().toString());
        startActivityForResult(i, 1011);
        db.close();
        return 0;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lyout_select:



                rdr_all.setChecked(false);
                rdr_unknown.setChecked(false);
                rdr_selected.setChecked(true);
                txt_select.setText("Add Contact");
                request_code = 1010;
                new backpro().execute(new Void[0]);

                break;

            case R.id.lyout_edit:

                startActivity(new Intent(MainActivity.this, Manage_contact.class));

                break;


            default:
                break;
        }

    }

    public class backpro extends AsyncTask<Void, Integer, Void> {

        class ContactLoding extends Thread {

            public void run() {
                MainActivity.this.startActivityForResult(new Intent(MainActivity.this, Add_contact.class), MainActivity.this.request_code);
            }
        }

        protected void onPreExecute() {
            MainActivity.this.progress = ProgressDialog.show(MainActivity.this, MainActivity.this.title, "Please wait contact loading", true);
            super.onPreExecute();
        }

        protected Void doInBackground(Void... params) {
            new ContactLoding().start();
            return null;
        }

        protected void onPostExecute(Void result) {
            MainActivity.this.progress.dismiss();
            super.onPostExecute(result);
        }

    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (resultCode == 1010) {
            Log.e("msg", "on activity");
            db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
            cur = db.rawQuery("select * from tbl_contact", null);
            int count = cur.getCount();
            Log.e("msg", "cur count: " + count);
            db.close();
            addnew();
        }
        if (resultCode == 1011) {
            Log.e("msg", "onActivityresult");
            SharedPreferences call = getSharedPreferences(getPackageName(), 0);
            Editor editor = call.edit();
            editor.putInt("onoff", 1);
            editor.commit();
            checkpaswd();
            if (call.getInt("onoff", 0) == 1) {
                chk_onof.setChecked(true);
                editor = call.edit();
                editor.putInt("onoff", 1);
                editor.commit();
                txt_onof.setText("Stop");
            } else {
                chk_onof.setChecked(false);
                editor = call.edit();
                editor.putInt("onoff", 0);
                editor.commit();
                txt_onof.setText("Start");
            }

        }
    }


    @SuppressWarnings("unused")
    private void addnew() {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        name = new ArrayList<String>();
        mobnumber = new ArrayList<String>();
        image = new ArrayList<String>();
        Iterator<PeopleObject> i$ = PeopleListClass.phoneList.iterator();
        while (i$.hasNext()) {
            PeopleObject bean = (PeopleObject) i$.next();
            if (bean.isSelected()) {
                sb.append(bean.getName());
                sb.append(",");
                name.add(bean.getName());
                mobnumber.add(bean.getNumber());
                image.add(bean.getImage());
                db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
                cur = db.rawQuery("select * from tbl_contact", null);
                Log.e("msg", "data base records" + cur.getCount());
                Editor editor;
                if (cur.getCount() == 0) {
                    db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
                    db.execSQL("insert into tbl_contact values('" + bean.getNumber() + "','" + bean.getName().replace("'", "''") + "','" + bean.getImage() + "');");
                    editor = getSharedPreferences(getPackageName(), 0).edit();
                    editor.putString("mode", "Selected");
                    editor.commit();
                    db.close();
                } else {
                    for (int j = 0; j < mobnumber.size(); j++) {
                        int check = checkdb((String) mobnumber.get(j));
                        if (check == 0) {
                            db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
                            db.execSQL("insert into tbl_contact values('" + bean.getNumber() + "','" + bean.getName().replace("'", "''") + "','" + bean.getImage() + "');");
                            editor = getSharedPreferences(getPackageName(), 0).edit();
                            editor.putString("mode", "Selected");
                            editor.commit();
                            db.close();
                        }
                    }
                }
                i++;
            }
        }
        db.close();
        lyout_manage.setVisibility(View.VISIBLE);
    }

    private int checkdb(String no) {
        if (db != null) {
            db.close();
        }
        db = openOrCreateDatabase("db_call", AccessibilityNodeInfoCompat.ACTION_PASTE, null);
        cur = db.rawQuery("select * from tbl_contact;", null);
        int flag = 0;
        if (cur.getCount() != 0) {
            while (cur.moveToNext()) {
                if (no.equalsIgnoreCase(cur.getString(cur.getColumnIndex("c_number")))) {
                    Log.i("msg", "if");
                    flag = 1;
                    break;
                }
                Log.i("msg", "else");
                flag = 0;
            }
        }

        db.close();

        return flag;
    }


    public static class LoadExitApps extends AsyncTask<String, Void, Boolean> {

        protected Boolean doInBackground(String... params) {
            try {

                new XmlDataParser1()
                        .parseXmlData("http://appnava.com/tools.xml");

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (appsList1 != null) {
                appsList_Mian.addAll(MainActivity.appsList1);


            }


        }

    }


    public void exit() {


        customDialog = new Dialog(MainActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.exit_layout);
        dwidth = getResources().getDisplayMetrics().widthPixels;
        dheight = getResources().getDisplayMetrics().heightPixels;
        dialogWidth = (width - (width / 10));
        dialogHeight = (height - height / 24);


        LinearLayout exitlinear = (LinearLayout) customDialog.findViewById(R.id.exitlinear);
        exitlinear.getLayoutParams().height = (int) (dialogHeight / 1.4);
        LinearLayout linear44 = (LinearLayout) customDialog
                .findViewById(R.id.linear442);
        linear44.getLayoutParams().width = dialogWidth;
        linear44.getLayoutParams().height = dialogHeight / 10
                + (int) ((dialogHeight / 12) * 4.5);


        LinearLayout linear2 = (LinearLayout) customDialog
                .findViewById(R.id.linear22);
        linear2.getLayoutParams().width = dialogWidth;
        linear2.getLayoutParams().height = (dialogHeight / 12) * 6;

        RelativeLayout real1 = (RelativeLayout) customDialog
                .findViewById(R.id.real12);
        real1.getLayoutParams().width = dialogWidth;
        real1.getLayoutParams().height = (int) ((dialogHeight / 15) * 1.5);
        //real1.getLayoutParams().height = (int) ((dialogHeight / 14));

        RelativeLayout real2 = (RelativeLayout) customDialog
                .findViewById(R.id.real22);
        real2.getLayoutParams().width = dialogWidth;
        real2.getLayoutParams().height = (int) ((dialogHeight / 15) * 1.5);

        View view = (View) customDialog.findViewById(R.id.view2);
        view.getLayoutParams().width = dialogWidth;
        view.getLayoutParams().height = (dialogHeight / 11);

        ImageView firstImage = (ImageView) customDialog
                .findViewById(R.id.firstImage);
        firstImage.getLayoutParams().width = dialogWidth / 3 - 20;
        firstImage.getLayoutParams().height = dialogWidth / 3 - 20;
        ImageView secondImage = (ImageView) customDialog
                .findViewById(R.id.secondImage);
        secondImage.getLayoutParams().width = dialogWidth / 3 - 20;
        secondImage.getLayoutParams().height = dialogWidth / 3 - 20;
        ImageView thirdImage = (ImageView) customDialog
                .findViewById(R.id.thirdImage);
        thirdImage.getLayoutParams().width = dialogWidth / 3 - 20;
        thirdImage.getLayoutParams().height = dialogWidth / 3 - 20;
        ImageView fourthImage = (ImageView) customDialog
                .findViewById(R.id.fourthImage);
        fourthImage.getLayoutParams().width = dialogWidth / 3 - 20;
        fourthImage.getLayoutParams().height = dialogWidth / 3 - 20;

        Button exitImage = (Button) customDialog.findViewById(R.id.exitImage);

        exitImage.getLayoutParams().width = (dialogWidth / 4);
        exitImage.getLayoutParams().height = dialogWidth / 6;

        Button cancelImage = (Button) customDialog.findViewById(R.id.cancelImage);

        cancelImage.getLayoutParams().width = (dialogWidth / 4);
        cancelImage.getLayoutParams().height = dialogWidth / 6;
        firstImage.startAnimation(animation);
        secondImage.startAnimation(animation);
        thirdImage.startAnimation(animation);
        fourthImage.startAnimation(animation);

        TextView firstText = (TextView) customDialog
                .findViewById(R.id.firstText);
        TextView secondText = (TextView) customDialog
                .findViewById(R.id.secondText);
        TextView thirdText = (TextView) customDialog
                .findViewById(R.id.thirdText);
        TextView fourthText = (TextView) customDialog
                .findViewById(R.id.fourthText);

        if (appsList_Mian != null && appsList_Mian.size() >= 4) {
            firstText.setText(appsList_Mian.get(0).getAppName());
            secondText.setText(appsList_Mian.get(1).getAppName());
            thirdText.setText(appsList_Mian.get(2).getAppName());
            fourthText.setText(appsList_Mian.get(3).getAppName());

        }

        if (appsList_Mian != null && appsList_Mian.size() >= 4) {
            Glide.with(getApplicationContext()).load(appsList_Mian.get(0).getImgUrl()).placeholder(R.drawable.ic_stub).error(R.drawable.ic_stub).into(firstImage);


            Glide.with(getApplicationContext()).load(appsList_Mian.get(1).getImgUrl()).placeholder(R.drawable.ic_stub).error(R.drawable.ic_stub).into(secondImage);
            Glide.with(getApplicationContext()).load(appsList_Mian.get(2).getImgUrl()).placeholder(R.drawable.ic_stub).error(R.drawable.ic_stub).into(thirdImage);
            Glide.with(getApplicationContext()).load(appsList_Mian.get(3).getImgUrl()).placeholder(R.drawable.ic_stub).error(R.drawable.ic_stub).into(fourthImage);


            firstImage.startAnimation(animation);
            secondImage.startAnimation(animation);
            thirdImage.startAnimation(animation);
            fourthImage.startAnimation(animation);

        }
        if (isinternet && appsList_Mian != null && appsList_Mian.size() >= 4) {
            firstImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (appsList_Mian != null && appsList_Mian.size() >= 1)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(appsList_Mian.get(0).getAppUrl())));

                }
            });

            secondImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (appsList_Mian != null && appsList_Mian.size() >= 2)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(appsList_Mian.get(1).getAppUrl())));

                }
            });

            thirdImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (appsList_Mian != null && appsList_Mian.size() >= 3)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(appsList_Mian.get(2).getAppUrl())));

                }
            });

            fourthImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (appsList_Mian != null && appsList_Mian.size() >= 4)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(appsList_Mian.get(3).getAppUrl())));

                }
            });

        }

        cancelImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        exitImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MainActivity.this.finish();
            }
        });
        customDialog.setCancelable(false);
        customDialog.show();

    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_PHONE_STATE,
                        READ_CONTACTS,
                        CALL_PHONE

                }, RequestPermissionCode);

    }

    public boolean checkPermission()
    {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThridPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int FivethPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThridPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FivethPermissionResult == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case RequestPermissionCode:
                if (grantResults.length > 0)
                {
                    boolean read_permission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean write_permission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean read_phone_permission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean read_contact_permission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean call_phone_permission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    if (read_permission && write_permission && read_phone_permission && read_contact_permission && call_phone_permission)
                    {
                        //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        //Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        permssiondialog();
                    }
                }
                break;
        }
    }

    private void permssiondialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("App requires Storage permissions to work perfectly..!");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                permission = true;
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainActivity.this.finish();
                    }
                });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (isinternet)
            exit();
        else
            finish();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (currentapiVersion >= Build.VERSION_CODES.M)
        {
            if (permission)
            {
                if (checkPermission())
                {
                }
                else
                {

                    permssiondialog();
                }
            }
        }

    }
}
