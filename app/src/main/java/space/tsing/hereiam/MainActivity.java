package space.tsing.hereiam;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button send;
    private ListView listView;
    private EditText input;
    private Handler handler = new Handler();
    private List<MsgEntity> dataArr = new ArrayList<MsgEntity>();
    private MsgAdapter adapter;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }
    private void initView() {
        send = (Button)findViewById(R.id.button);
        listView = (ListView)findViewById(R.id.listView);
        input = (EditText)findViewById(R.id.editText);
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String sendStr = input.getText().toString();
                if (sendStr.length() > 0) {
                    show(false, sendStr);
                    try {
                        new HttpThread("http://www.tsing.space/iamhere/iamhere.php?user=default&content=" + URLEncoder.encode(input.getText().toString(), "UTF-8"), handler).start();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    input.setText("");
                }
            }
        });
    }
    private void initData() {
        adapter = new MsgAdapter(this, dataArr);
        listView.setAdapter(adapter);
    }

    private void show(boolean left, String... strArr){
        for (String str :strArr) {
            if (str != null && str != ""){
                MsgEntity entity = new MsgEntity(str, left);
                dataArr.add(entity);
                adapter.notifyDataSetChanged();
                listView.setSelection(listView.getCount() - 1);
            }
        }
    }
    private class HttpThread extends Thread {

        private String url;
        private Handler handler;

        public HttpThread(String url, Handler handler){
            this.url = url;
            this.handler = handler;
        }

        @Override
        public void run(){
            try{
                URL httpUrl = new URL(url);
                try{
                    HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    final StringBuffer sb = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str;
                    while((str = reader.readLine()) != null)
                        sb.append(str);
                    handler.post(new Runnable(){
                        @Override
                        public void run() {
                            show(true, sb.toString().split("<br />"));
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
