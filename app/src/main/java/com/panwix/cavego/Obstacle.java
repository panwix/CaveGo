package com.panwix.cavego;

public class Obstacle {
	int x;
	int h;

	public Obstacle() {
		x = 480;
		//h = new Random().nextInt(70) + 350;
		h = (int)Math.random()*70+350;
	}
}
