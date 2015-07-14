package com.panwix.cavego;

public class Stone {
	
		int x;
		int h;

		public Stone() {
			x = 780;
			//h = new Random().nextInt(70) + 350;
			h = (int)Math.random()*70+350;
		}
	
}
