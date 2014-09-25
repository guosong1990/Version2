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
	//���ֱ���
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
                
        //�����ǹ�����
		// ��ʼ��ͳ��������Ҫ��AndroidManifest��ע��APP_ID��APP_PIDֵ
		AppConnect.getInstance(this);
		AppConnect.getInstance(this).getPoints(this);
		value = AppConnect.getInstance(this).getConfig("showad_meizu", "yes");
		Log.e("Value��ֵΪ��", value);
		
		
		if(value.equals("yes")){
			//���ÿ��нӿ�
			//MyKAM.getInstance().showKuguoSprite(this, MyKAM.STYLE_KUZAI);
			
			/*���ֹ��*/
	        myAdonContainerView = (RelativeLayout)findViewById(R.id.adonContainerView);
	        myAdView = new AdBanner(this); //new AdBanner��this,480, 800��; ������õĿ�Ⱦ��ǹ������ʾ�Ŀ�ȣ��߶ȸ��ݹ�����Ŀ�ߵȱ����ţ������������Ĭ��ʹ����Ļ�Ŀ��

	        myAdonContainerView.addView(myAdView);
			myAdView.setAppKey(appKey);
			RecevieAdListener adListener = new RecevieAdListener() {
				@Override
				public void onSucessedRecevieAd(AdBanner adView) {
					//����ȡ�ɹ�����ʾ���
					myAdonContainerView.setVisibility(View.VISIBLE);
				}
				@Override
				public void onFailedToRecevieAd(AdBanner adView) {
					//����ȡʧ�ܣ����ع��
					myAdonContainerView.setVisibility(View.GONE);
				}
			};
			myAdView.setRecevieAdListener(adListener);
		}

		

		
    }  
      /*
       * ��д�ֻ���system�µ�build.prop�ļ��ı����е��������±��浽�ⲿ����������һ����sd����
       */
	public void readBuildprop(){
		File build = new File("/system/build.prop");
        String string,model,string2="",string3="";
        String[] strings = null;
        StringBuffer buffer = new StringBuffer();
        try {
        	
        	//�ļ��޸�
        	if(isEditModle.equals("")){
            	model = getModel();  //�õ�ˮ���ֻ��ͺŸ�ֵ
        	}else{
        		model = isEditModle;
        	}
			FileReader reader = new FileReader(build);
			BufferedReader br = new BufferedReader(reader);
			while ((string = br.readLine())!=null) {	
				if(string.contains("ro.product.model")){
					strings = string.split("=");
					textView1.setText("��ǰ�ֻ��ͺ�Ϊ��"+strings[1]);

					editText.setText(model);
					
					//���´����ǰ��ֻ��ͺŵĿո�ȥ�����滻build�ļ���û�пո���ֻ��ͺ�
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
			
			//�ļ�д��
			File sd = Environment.getExternalStorageDirectory();
			path = sd.getPath()+"/build.prop";
            File file = new File(path);
        	if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			Log.e(tag, "û�б�ִ��");
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
       * ��ȡ�ֻ�ģ���ļ����ֻ��ͺţ�������ɣ�
       */
    private String getModel() {
		// TODO Auto-generated method stub
    	String string = "huawei p6";
    	InputStream inputStream = getResources().openRawResource(R.raw.telmodel);
    	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
    	BufferedReader reader = new BufferedReader(inputStreamReader);
    	Random random = new Random();
    	int temp = Math.abs(random.nextInt())%567;//����567���ֻ�ģ��
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
	 * �õ�system�µĶ�дȨ�޲���������д��build.prop�ļ����ⲿ�������ģ�������system�ļ�����
	 * ע����ô��û���޸��ļ��Ķ�д���ԣ���Ϊ��д���Ե��޸Ļᵽ�����ֻ��޷�������
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
	 *�����ť�¼�
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
			if(!isEditModle.equals("")){//�༭����������������ٴ��滻
				readBuildprop();
			}
			AppConnect.getInstance(this).getPoints(this);
			Log.e("value", value);
			
			if(score<30&&value.equals("yes")){
				Toast.makeText(this, "���ػ��ֳ���30�������ʹ��", 3).show();
				//��ʾ���ջ���ǽ
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
	 * ��������е����ֽ��м���(non-Javadoc)
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