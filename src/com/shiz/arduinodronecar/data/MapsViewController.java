package com.shiz.arduinodronecar.data;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.app.Activity;

import com.shiz.arduinodronecar.R;
import com.shiz.arduinodronecar.connect.LocationToServerListener;


public class MapsViewController implements LocationToServerListener {
	private Activity mActivity;
	private MapView mMapView ;
	private OverlayManager mOverlayManager; 
	private MapController mMapController;
	public MapsViewController(Activity mActivity) {
		this.mActivity = mActivity;
	
	}

	public void init() {
		mMapView = (MapView) mActivity.findViewById(R.id.map);
		mMapController = mMapView.getMapController();
		// Получаем объект OverlayManager
		mOverlayManager = mMapController.getOverlayManager(); 
		mMapView.showZoomButtons(false);
		mMapView.showFindMeButton(true);
		mMapView.showJamsButton(false);
		mMapView.showBuiltInScreenButtons(true);
	}

	@Override
	public void changeLocation(Double x, Double y) {
		// TODO Auto-generated method stub
		//Создаем новый слой
		Overlay overlay = new Overlay(mMapController);
		// Создаем объект слоя
		OverlayItem yandex = new OverlayItem(new GeoPoint(x , y), mActivity.getResources().getDrawable(R.drawable.little_vko));
		// Добавляем объект на слой
		overlay.addOverlayItem(yandex);
		// Добавляем слой на карту
		mOverlayManager.addOverlay(overlay);
	}
}
