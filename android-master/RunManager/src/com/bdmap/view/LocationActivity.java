package com.bdmap.view;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;


public class LocationActivity extends Activity implements OnClickListener {

	private MapView mapview;
	private BaiduMap bdMap;

	private LocationClient locationClient;
	private BDLocationListener locationListener;
	private BDNotifyListener notifyListener;

	private double longitude;// ����
	private double latitude;// γ��
	private float radius;// ��λ���Ȱ뾶����λ����
	private String addrStr;// ���������
	private String province;// ʡ����Ϣ
	private String city;// ������Ϣ
	private String district;// ������Ϣ
	private float direction;// �ֻ�������Ϣ
	private int locType;//��λģʽ

	private TextView speedInfo;
	// ��λ��ť
	private Button locateBtn;
	// ��λģʽ ����ͨ-����-���̣�
	private MyLocationConfiguration.LocationMode currentMode;
	// ��λͼ������
	private BitmapDescriptor currentMarker = null;
	// ��¼�Ƿ��һ�ζ�λ
	private boolean isFirstLoc = true;
	
	//�������ڻ��켣
	List<LatLng> pointstwo = new ArrayList<LatLng>();
	LatLng p1;
	LatLng p2;
	
	public double length=0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		mapview = (MapView) findViewById(R.id.bd_mapview);
		bdMap = mapview.getMap();
		locateBtn = (Button) findViewById(R.id.locate_btn);
		locateBtn.setOnClickListener(this);
		currentMode = MyLocationConfiguration.LocationMode.NORMAL;
		locateBtn.setText("��ͨ");
		init();
		
	}

	/**
	 * 
	 */
	private void init() {
		bdMap.setMyLocationEnabled(true);
		// 1. ��ʼ��LocationClient��
		locationClient = new LocationClient(getApplicationContext());
		// 2. ����LocationListener��
		locationListener = new MyLocationListener();
		// 3. ע���������
		locationClient.registerLocationListener(locationListener);
		// 4. ���ò���
		LocationClientOption locOption = new LocationClientOption();
		locOption.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ
		locOption.setCoorType("bd09ll");// ���ö�λ�������
		locOption.setScanSpan(2000);// ���÷���λ����ļ��ʱ��,ms
		locOption.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		locOption.setNeedDeviceDirect(true);// ���÷��ؽ�������ֻ��ķ���

		locationClient.setLocOption(locOption);
		locationClient.registerNotify(notifyListener);
		// �ر� ��λSDK
		locationClient.start();
	}

	/**
	 * 
	 * @author ys
	 *
	 */
	class MyLocationListener implements BDLocationListener {
		// �첽���صĶ�λ���
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			locType = location.getLocType();
			//Toast.makeText(LocationActivity.this, "��ǰ��λ�ķ���ֵ�ǣ�"+locType, Toast.LENGTH_SHORT).show();
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			if (location.hasRadius()) {// �ж��Ƿ��ж�λ���Ȱ뾶
				radius = location.getRadius();
			}
			if (locType == BDLocation.TypeGpsLocation) {
				//ʹ��GPS��λʱ��ʾ�˶��ٶ�
				speedInfo = (TextView)findViewById(R.id.speedInfo);
				speedInfo.setText("�˶��ٶȣ�" + location.getSpeed());
				//Toast.makeText(LocationActivity.this,"�ٶȣ�" + location.getSpeed() + " ��������" + location.getSatelliteNumber(),Toast.LENGTH_SHORT).show();
			} else if (locType == BDLocation.TypeNetWorkLocation) {
				addrStr = location.getAddrStr();// ��ȡ���������(���������ĵ�ַ)
				//Toast.makeText(LocationActivity.this, addrStr,Toast.LENGTH_SHORT).show();
			}
			direction = location.getDirection();// ��ȡ�ֻ����򣬡�0~360�㡿,�ֻ��������泯��Ϊ0��
			province = location.getProvince();// ʡ��
			city = location.getCity();// ����
			district = location.getDistrict();// ����
			//Toast.makeText(LocationActivity.this,province + " " + city + " " + district, Toast.LENGTH_SHORT).show();
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(radius)//
					.direction(direction)// ����
					.latitude(latitude)//
					.longitude(longitude)//
					.build();
			// ���ö�λ����
			bdMap.setMyLocationData(locData);
			
			//����GPS�仯��
			if (isFirstLoc) {
				//���Ƶ�һ����
				isFirstLoc = false;
				p1 = p2 = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p2);
				OverlayOptions pointFirst = new DotOptions().center(p1).radius(10)
						.color(0xFF0000FF);
				bdMap.addOverlay(pointFirst);
				bdMap.animateMapStatus(u);
			} else {
				//���������
				p2 = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(p2);
				OverlayOptions point = new DotOptions().center(p2).radius(6)
						.color(0xAAFF0000);
				bdMap.addOverlay(point);
				
				double distance = DistanceUtil.getDistance(p1, p2);
				length += distance;
				//�����ƶ�����distance������
				if(distance>2){					
					//speedInfo.setText("����"+distance);  //��֪Ϊ�δ˾����ɳ������
					pointstwo.add(p1);
					pointstwo.add(p2);
					OverlayOptions ooPolyline = new PolylineOptions().width(4)
							.color(0xAAFF0000).points(pointstwo);
					bdMap.addOverlay(ooPolyline);
					p1 = p2;
					//bdMap.animateMapStatus(u);
				}

			}
			

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.locate_btn:// ��λ
			switch (currentMode) {
			case NORMAL:
				locateBtn.setText("����");
				currentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
				break;
			case FOLLOWING:
				locateBtn.setText("����");
				currentMode = MyLocationConfiguration.LocationMode.COMPASS;
				break;
			case COMPASS:
				locateBtn.setText("��ͨ");
				currentMode = MyLocationConfiguration.LocationMode.NORMAL;
				break;
			}
			bdMap.setMyLocationConfigeration(new MyLocationConfiguration(
					currentMode, true, currentMarker));
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
		locationClient.unRegisterLocationListener(locationListener);
		locationClient.stop();
	}
}
