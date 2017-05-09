package com.mxn.soul.flowingdrawer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/27.
 */
public class RoomListActivity extends AppCompatActivity {
    String meCookie = "";
    String errorMessage = "";
    //String localhost = "192.168.1.107:8080";
    String localhost = "115.159.203.174:80";//服务器ip
    boolean isFromService = false;
    //数据库类
    private MySQLiteOpenHelper mHelper;
    private SQLiteDatabase mDb;
    //相关map list 用于存储数据
    private Handler handler;
    private Map myMap = new LinkedHashMap();
    private List<String> teachNameitems = new ArrayList<>();
    private ArrayList<ClassInfo1> Info1List = new ArrayList<>();
    //学期及其相关控件
    private String[][] getxq = new String[][]{
            {"2016-2017学年第二学期", "20161"},
            {"2016-2017学年第一学期", "20160"},
            {"2015-2016学年第二学期", "20151"},
            {"2015-2016学年第一学期", "20150"},
            {"2014-2015学年第二学期", "20141"},
            {"2014-2015学年第一学期", "20140"},
    };
    String xqValue = "";
    Spinner spin_xqname = null;
    //教师姓名及其相关控件
    String techname_edit = "";
    EditText edit_teacherName = null;
    String technameValue = "";
    Spinner spin_techname = null;
    //验证码及其相关
    ImageView imgv_yzm = null;
    EditText edit_yzm = null;
    String yzm_edit = "isNULL";
    Bitmap bitmap_img = null;
    String yzmAddress = null;

    Toolbar mToolbar;
    FloatingActionButton mFabSearch;
    EditText mEditText;
    Spinner mSpinner;
    RecyclerView mRecyclerView;
    FloatingActionButton mFabUp;
    KProgressHUD loading;
    private CourseAdapter mAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        //获获取控件
        spin_xqname = (Spinner) findViewById(R.id.spinner_xq);
        edit_teacherName = (EditText) findViewById(R.id.edit_kcName);
        imgv_yzm = (ImageView) findViewById(R.id.imgv_yzm);
        spin_techname = (Spinner) findViewById(R.id.spiner_kcName);
        edit_yzm = (EditText) findViewById(R.id.edit_yzm);
        //第一次加载验证码部分不显示
        imgv_yzm.setVisibility(View.INVISIBLE);
        edit_yzm.setVisibility(View.INVISIBLE);

        //------
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_t);
        mFabUp = (FloatingActionButton) findViewById(R.id.fab_up_t);
        mEditText = (EditText) findViewById(R.id.edit_kcName);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFabSearch = (FloatingActionButton) findViewById(R.id.fab);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new CourseAdapter(RoomListActivity.this);

        mRecyclerView.setAdapter(mAdapter);
        mEditText.requestFocus();
        loading = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        // set mToolbar
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mFabUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
        mFabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //点击检索的事件
                //Info1List.clear();//每次点击检索时将之前的列表清空
                loading.show();
                if (isFromService) {
                    loading.dismiss();
                    mAdapter.setCourseData(Info1List);
                } else {
                    String yzmhere = edit_yzm.getText().toString();
                    if (yzmhere.trim() == "") {
                        Toast toast = Toast.makeText(getApplicationContext(), "请输入验证码！", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        yzm_edit = yzmhere;//获得输入的验证码
                        final String technameSelected = spin_techname.getSelectedItem().toString();//获得下拉列表选中的教师名字
                        //获得教师的编号value
                        for (Object s : myMap.keySet()) {
                            if (myMap.get(s).toString().equals(technameSelected))
                                technameValue = s.toString();
                        }
                        if (fetchDataFromSqlite()) {//说明数据库中有数据
                            loading.dismiss();
                            mAdapter.setCourseData(Info1List);
                        } else {//否则，开启线程从网络上获取
                            /***------------修改为：向服务器发送post请求,得到相关数据，之后再来绑定
                             *  难点在获得的json数据如何解析  目前不知道数据类型、格式***/
                            new Thread() {
                                @Override
                                public void run() {
                                    String netUrlgetJson = "http://" + localhost + "/room/queryClassByRoom";
                                    sendJsonToServer(netUrlgetJson);
                                    //handler.sendEmptyMessage(0x201);
                                    RoomListActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loading.dismiss();
                                            if(Info1List.size()==0){
                                                Toast toast = Toast.makeText(getApplicationContext(), "无数据！", Toast.LENGTH_SHORT);
                                                //显示toast信息
                                                toast.show();
                                            }else{
                                                mAdapter.setCourseData(Info1List);
                                                //界面显示之后存入数据库
                                                addInfoToSqlite();
                                            }
                                        }
                                    });

                                }
                            }.start();
                        }
                    }
                }
            }
        });
        //初始化 学期 教师姓名 验证码
        String xq_spin = spin_xqname.getSelectedItem().toString();
        for (int i = 0; i < getxq.length; i++) {
            if (xq_spin.equals(getxq[i][0]))
                xqValue = getxq[i][1];
        }
        techname_edit = edit_teacherName.getText().toString(); //获取输入的教师姓名
        getListName("1");

        //设置下拉列表的选中事件  我们要做的是 发送请求 判断服务器有没有数据，如果没有就返回验证码 有就返回数据存入list
        spin_techname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loading.show();
                Info1List.clear();
                List aarr=new ArrayList();
                mAdapter.setCourseData(aarr);
                isFromService = false;
                edit_yzm.setText("".toCharArray(),0,0);

                imgv_yzm.setVisibility(View.INVISIBLE);
                edit_yzm.setVisibility(View.INVISIBLE);
                //发送请求
                yzm_edit = "isNUll";
                String technameSelected = spin_techname.getSelectedItem().toString();//获得下拉列表选中的教师名字
                //获得教师的编号value
                for (Object s : myMap.keySet()) {
                    if (myMap.get(s).toString().equals(technameSelected))
                        technameValue = s.toString();
                }

                new Thread() {
                    @Override
                    public void run() {
                        yzmAddress=null;
                        String netUrlgetJson = "http://" + localhost + "/room/queryClassByRoom";
                        sendJsonToServer(netUrlgetJson);

                        if (yzmAddress != null || Info1List.size()==0) { //服务器本地没有数据，需要访问网络获取数据，这时候需要输入验证码 所以返回验证码id
                            bitmap_img = getBitmap_imgs("http://" + localhost + "/room/getVerImg");
                            //handler.sendEmptyMessage(0x202); //加载验证码
                            //代替handler
                            RoomListActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    imgv_yzm.setVisibility(View.VISIBLE);
                                    edit_yzm.setVisibility(View.VISIBLE);
                                    imgv_yzm.setImageBitmap(bitmap_img);
                                }
                            });

                        } else { //服务器本地有数据，取到了
                            isFromService = true;
                            loading.dismiss();
                        }
                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //refresh yzm
        imgv_yzm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getListName("1");
            }
        });

        //datebase
        mHelper = new MySQLiteOpenHelper(this, "course_table.db", null, 1);
        mDb = mHelper.getWritableDatabase();

        //handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 200) { //初始化下拉列表
                    loading.dismiss();
                    ArrayAdapter<String> kcAdapter = new ArrayAdapter<String>(RoomListActivity.this, android.R.layout.simple_spinner_item, teachNameitems);
                    kcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_techname.setAdapter(kcAdapter);//下拉列表显示
                }
                else if (msg.what == 5) {
                    loading.dismiss();
                    imgv_yzm.setVisibility(View.VISIBLE);
                    edit_yzm.setVisibility(View.VISIBLE);
                    imgv_yzm.setImageBitmap(bitmap_img);
                }
            }
        };
        //change edit search
        edit_teacherName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getListName("2");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }//oncreate over
    //获取教师教师列表名
    private void getListName(String id) {
        loading.show();
        final String courseId = id;
        new Thread() {
            @Override
            public void run() {
                if (courseId == "1") {
                    teachNameitems.clear();
                    techname_edit = "";
                    //意味着初始化页面，我要访问服务器 来获取下拉列表以及验证码
                    //采用url访问服务器 传递的参数为学期 xq  返回为json格式还是？ 我来解析
                    try {
                        String url = "http://" + localhost + "/room/" + xqValue + "/roomList";
                        String jsonString = getJsonString(url);
                        Log.i("aaaa", jsonString);
                        JSONArray jsonObject = new JSONArray(jsonString);
                        for (int i = 0; i < jsonObject.length(); i++) {
                            JSONObject jsonobj = jsonObject.getJSONObject(i);
                            String listName = jsonobj.getString("listName");
                            String listValue = jsonobj.getString("listValue");
                            myMap.put(listValue, listName);
                        }
                        for (Object s : myMap.keySet()) {
                            teachNameitems.add(myMap.get(s).toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    teachNameitems.clear();
                    techname_edit = edit_teacherName.getText().toString();
                    //本地搜索 课程名
                    for (Object s : myMap.keySet()) {
                        if (myMap.get(s).toString().contains(techname_edit))//如果存在与输入的课程名相关的课程
                            teachNameitems.add(myMap.get(s).toString());
                    }
                }
                handler.sendEmptyMessage(200); //去初始化下拉列表
            }
        }.start();
    }

    //访问url获得String类型的json数据
    protected String getJsonString(String urlPath) throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        //connection.setRequestProperty("Cookie","JSESSIONID="+meCookie); //设置cookie

        InputStream inputStream = connection.getInputStream();
        //对应的字符编码转换
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String str = null;
        StringBuffer sb = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            sb.append(str);
        }
        reader.close();
        inputStream.close();
        connection.disconnect();
        return sb.toString();
    }

    //访问url code and cookie ->String
    protected String getJsonAndSessionString(String urlPath) throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();


        InputStream inputStream1 = connection.getInputStream();
        //对应的字符编码转换
        Reader reader = new InputStreamReader(inputStream1, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String str = null;
        StringBuffer sb = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            sb.append(str);
        }
        reader.close();
        inputStream1.close();
        connection.disconnect();
        return sb.toString();
    }

    //访问 图片url 获取验证码
    public Bitmap getBitmap_imgs(String urlPath) {
        Bitmap bitmap_jb = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            bitmap_jb = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return bitmap_jb;
    }


    //向服务器发送请求 以post方式  传递json数据
    public void sendJsonToServer(String url) {
        URL myurl = null;
        try {
            JSONObject params = new JSONObject();
            params.put("term", xqValue);
            params.put("room", technameValue);
            params.put("yzm", yzm_edit);
            Log.i("jsonParam:", params.toString());
            // 把Json数据转换成String类型，使用输出流向服务器写
            final String str = String.valueOf(params);

            myurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);// 设置允许输出
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // 内容类型
            OutputStream os = conn.getOutputStream();
            os.write(str.getBytes());
            os.close();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                String json = readString(is);
                stringJsonToList(json);
            } else {
                errorMessage = "返回一个" + conn.getResponseCode() + "状态码";
                //handler.sendEmptyMessage(0x500);
                RoomListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                        //显示toast信息
                        toast.show();
                        //验证码错误之后  刷新验证码
                    }
                });
                //Log.i("fail","可能服务器忙"+conn.getResponseCode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //stringJsonToList里面用到的方法 把字节流以utf-8格式转换为string返回
    public String readString(InputStream is) throws IOException {
        if (is == null)
            return null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int len = 0;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) != -1) {
            bout.write(buf, 0, len);
        }
        is.close();
        return URLDecoder.decode(new String(bout.toByteArray()), "utf-8");
    }

    //把返回的json解析存放到list里面用以绑定到页面上
    public void stringJsonToList(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String success = jsonObject.getString("success");
            String error = jsonObject.getString("error");
            if (success.equals("true")) { //返回了data 也可能是返回了验证码
                if (error.equals("null")) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObjectSon = (JSONObject) data.opt(i);
                        String term = jsonObjectSon.getString("term");
                        String course = jsonObjectSon.getString("classname");
                        String week = jsonObjectSon.getString("week");
                        String lesson = jsonObjectSon.getString("lesson");
                        String info = jsonObjectSon.getString("info");

                        ClassInfo1 cou = new ClassInfo1();
                        cou.setWeek(week);
                        cou.setLesson(lesson);
                        cou.setInfo(info);

                        Info1List.add(cou);
                    }

                } else {
                    yzmAddress = error;
                }
            } else {
                errorMessage = error;
                //handler.sendEmptyMessage(0x500);
                RoomListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                        //显示toast信息
                        toast.show();
                        bitmap_img=null;
                        new Thread(){
                            @Override
                            public void run() {
                                bitmap_img = getBitmap_imgs("http://" + localhost + "/room/getVerImg");
                                handler.sendEmptyMessage(5);
                            }
                        }.start();
                        //handler.sendEmptyMessage(0x202); //加载验证码
                        loading.dismiss();
                        imgv_yzm.setVisibility(View.VISIBLE);
                        edit_yzm.setVisibility(View.VISIBLE);
                        imgv_yzm.setImageBitmap(bitmap_img);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //数据库的操作
    //添加数据进入数据库
    private long addInfoToSqlite() {
        //实例化ContentValues对象并进行数据组装
        ContentValues values = new ContentValues();
        mDb = mHelper.getWritableDatabase();
        long temp = 0;
        for (int i = 0; i < Info1List.size(); i++) {
            values.put("Room_xq", xqValue);
            values.put("Room_Name", technameValue);
            values.put("Room_week", Info1List.get(i).getWeek());
            values.put("Room_lesson", Info1List.get(i).getLesson());
            values.put("Room_info", Info1List.get(i).getInfo());
            //插入数据
            temp = mDb.insert("Room_table", null, values);
            values.clear();
        }
        mDb.close();
        return temp;
    }

    //从数据库中读取数据
    private boolean fetchDataFromSqlite() {
        boolean flag = false;
        try {
            mDb = mHelper.getReadableDatabase();
            Cursor cursor = mDb.query("Room_table", null, "Room_xq=? and Room_Name=?", new String[]{xqValue, technameValue}, null, null, null);
            if (cursor.moveToFirst()) { //从数据库中取到数据
                flag = true;
                do {
                    String kc_week = cursor.getString(cursor.getColumnIndex("Room_week"));
                    String kc_lesson = cursor.getString(cursor.getColumnIndex("Room_lesson"));
                    String kc_info = cursor.getString(cursor.getColumnIndex("Room_info"));
                    //取出了一条数据
                    ClassInfo1 cou = new ClassInfo1();
                    cou.setWeek(kc_week);
                    cou.setLesson(kc_lesson);
                    cou.setInfo(kc_info);
                    //Log.i("sqlite", cou.getInfo());
                    Info1List.add(cou);
                } while (cursor.moveToNext());
                cursor.close();
                mDb.close();
            }
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

}
