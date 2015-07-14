package com.panwix.cavego.framework.impl;

import android.content.Context;
import android.view.View;

import com.panwix.cavego.framework.Input;

import java.util.List;

public class AndroidInput implements Input {
	TouchHandler touchHandler;
	public AndroidInput(Context context, View view, float scaleX, float scaleY){
		touchHandler = new SingleTouchHandler(view,scaleX,scaleY);
	}
	@Override
	public boolean isKeyPressed(int Code) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTouchDown(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) {
		// TODO Auto-generated method stub
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public List<KeyEvent> getKeyEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		// TODO Auto-generated method stub
		return touchHandler.getTouchEvents();
	}

}

