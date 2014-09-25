package com.songg.version2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import com.fw.bn.AdBanner;
import com.fw.bn.RecevieAdListener;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;
import android.app.Activity;  
import android.content.SharedPreferences;
import android.os.Bundle;  
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
  
public class MainActivity extends Activity implements OnClickListener,TextWatcher,UpdatePointsNotifier{  
  
	private String tag = "Version"; 
	private String path = "";
	private TextView textView1;
	private EditText editText;
	private String isEditModle = "";
	private int score = 0;
	
	private String value = "yes";
	//飞沃变量
	private RelativeLayout myAdonContainerView;
	private String appKey = "bpTyu7N4YwAP1uAwUmgXO077";
    private AdBanner myAdView;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);   
        textView1 = (TextView)findViewById(R.id.text1);
        editText = (EditText)findViewById(R.id.editText);
        Button restart = (Button)findViewById(R.id.open);
        Button again = (Button)findViewById(R.id.again);
        restart.setOnClickListener(this);
        again.setOnClickListener(this);
        
        readBuildprop();
        
        editText.addTextChangedListener(this);
                
        //以下是广告代码
		// 初始化统计器，需要在AndroidManifest中注册APP_ID和APP_PID值
		AppConnect.getInstance(this);
		AppConnect.getInstance(this).getPoints(this);
		value = AppConnect.getInstance(this).getConfig("showad_meizu", "yes");
		Log.e("Value的值为：", value);
		
		
		if(value.equals("yes")){
			//调用酷仔接口
			//MyKAM.getInstance().showKuguoSprite(this, MyKAM.STYLE_KUZAI);
			
			/*飞沃广告*/
	        myAdonContainerView = (RelativeLayout)findViewById(R.id.adonContainerView);
	        myAdView = new AdBanner(this); //new AdBanner（this,480, 800）; 这个设置的宽度就是广告条显示的宽度，高度根据广告条的宽高等比缩放，如果不设置则默认使用屏幕的宽高

	        myAdonContainerView.addView(myAdView);
			myAdView.setAppKey(appKey);
			RecevieAdListener adListener = new RecevieAdListener() {
				@Override
				public void onSucessedRecevieAd(AdBanner adView) {
					//广告获取成功，显示广告
					myAdonContainerView.setVisibility(View.VISIBLE);
				}
				@Override
				public void onFailedToRecevieAd(AdBanner adView) {
					//广告获取失败，隐藏广告
					myAdonContainerView.setVisibility(View.GONE);
				}
			};
			myAdView.setRecevieAdListener(adListener);
		}

		

		
    }  
      /*
       * 读写手机中system下的build.prop文件改变其中的内容重新保存到外部储存器（不一定是sd卡）
       */
	public void readBuildprop(){
		File build = new File("/system/build.prop");
        String string,model,string2="",string3="";
        String[] strings = null;
        StringBuffer buffer = new StringBuffer();
        try {
        	
        	//文件修改
        	if(isEditModle.equals("")){
            	model = getModel();  //得到水机手机型号赋值
        	}else{
        		model = isEditModle;
        	}
			FileReader reader = new FileReader(build);
			BufferedReader br = new BufferedReader(reader);
			while ((string = br.readLine())!=null) {	
				if(string.contains("ro.product.model")){
					strings = string.split("=");
					textView1.setText("当前手机型号为："+strings[1]);

					editText.setText(model);
					
					//以下代码是把手机型号的空格去掉，替换build文件中没有空格的手机型号
					String[] strings2 = strings[1].split(" ");
					int i = 0;
					while (strings2.length>i) {
						string2+=strings2[i];
						i++;
					}
					
					String[] strings3 = model.split(" ");
					int j = 0;
					while (strings3.length>j) {
						string3+=strings3[j];
						j++;
					}
					
					buffer.append(strings[0]+"="+model);
					buffer.append("\n");
				}else {
					buffer.append(string);
					buffer.append("\n");
				}
				
			}
			
			String myBuild = buffer.toString();
			
			String newBulid =  myBuild.replace(string2, string3);
			br.close();
			
			//文件写入
			File sd = Environment.getExternalStorageDirectory();
			path = sd.getPath()+"/build.prop";
            File file = new File(path);
        	if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			Log.e(tag, "没有被执行");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(newBulid);
			
			bw.flush();
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(tag, "error1");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(tag, "error2");
			e.printStackTrace();
		}
	}
      /*
       * 获取手机模板文件的手机型号（随机生成）
       */
    private String getModel() {
		// TODO Auto-generated method stub
    	String string = "huawei p6";
    	InputStream inputStream = getResources().openRawResource(R.raw.telmodel);
    	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    	BufferedReader reader = new BufferedReader(inputStreamReader);
    	Random random = new Random();
    	int temp = Math.abs(random.nextInt())%567;//共有567个手机模板
    	while(temp-->=0){
    		try {
				string = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return string;
	}

	/*
	 * 得到system下的读写权限并把上面重写的build.prop文件（外部储存器的）拷贝的system文件夹下
	 * 注意这么做没有修改文件的读写属性，因为读写属性的修改会到导致手机无法启动了
	 */
	private void getRoot()  
    {  

        try {  
            Process process = Runtime.getRuntime().exec("su");  
            DataOutputStream out = new DataOutputStream(process.getOutputStream());  
            out.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system\n");  
            out.writeBytes("cat "+path+" > /system/build.prop\n");  
            out.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system\n");  
            out.writeBytes("reboot\n");    
            out.flush(); 
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }


	/*
	 *点击按钮事件
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.again:
			isEditModle="";
			readBuildprop();
			break;
		case R.id.open:
			if(!isEditModle.equals("")){//编辑框被重新输入过，就再次替换
				readBuildprop();
			}
			AppConnect.getInstance(this).getPoints(this);
			Log.e("value", value);
			
			if(score<30&&value.equals("yes")){
				Toast.makeText(this, "下载积分超过30即可免费使用", 3).show();
				//显示万普积分墙
				AppConnect.getInstance(this).showOffers(this);
			}else {
				Log.e("123", "123");
				getRoot();
			}

			break;
		default:
			break;
		}
	}
	
	/*
	 * 对输入框中的文字进行监听(non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		isEditModle = s.toString();
		
	}
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	}  
    
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		// TODO Auto-generated method stub
		score = arg1;
		Log.e("my score", score+"");
	}
	@Override
	public void getUpdatePointsFailed(String arg0) {
		// TODO Auto-generated method stub
		
	}
}  