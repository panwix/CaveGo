package com.panwix.cavego;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.panwix.cavego2.R;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity {
	
	
	int highscore;
	MediaPlayer mediaPlayer;
	private View viewDraw;
	PreferencesService service ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		viewDraw = (View) findViewById(R.id.viewDraw);
		MyView v = new MyView(viewDraw.getContext(), null);

		setContentView(v);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mediaPlayer = new MediaPlayer();
		try{
		AssetManager assetManager = getAssets();
		AssetFileDescriptor descriptor = assetManager.openFd("feiji.mp3");
		mediaPlayer.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(),descriptor.getLength());
		mediaPlayer.prepare();
		mediaPlayer.setLooping(true);
		}catch(IOException e){
			mediaPlayer = null;
		}
		service = new PreferencesService(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mediaPlayer!=null){
			mediaPlayer.start();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mediaPlayer !=null){
			mediaPlayer.pause();
			if(isFinishing()){
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
	}
	
	class MyView extends View implements Runnable {
		static final int SPEED = 2;
		// ͼ�ε�ǰ����
		private int a, b, time, point, v;
		// pillarΪ�ϰ���
		Obstacle pillar;
		
		Feiji feiji;

		SoundPool soundPool;
		int explosionId = -1;

		boolean bInit, bCourse, bGame, bGameOver, highScoreScreen;

		RefreshHandler mRedrawHandler;
		FileIO file = new FileIO();
		String fileName = "cavego.txt";
		ScoreServer server = new ScoreServer(this.getContext());
		HighScores highscores = new HighScores();

		// ���췽��
		public MyView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
			// ��ý���
			setFocusable(true);
			bInit = false;
			bCourse = false;
			bGame = false;
			bGameOver = false;
			highScoreScreen = false;
			// �ϰ����ʼ��
			pillar = new Obstacle();
			 
			// �ɻ���ʼ��
			feiji = new Feiji();

			// ����ͼƬ
			Assets.startBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.other_start);
			Assets.planeUpBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.zhishengfeiji);
			Assets.planeDownBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.zhishengfeiji2);
			Assets.planeMiddleBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.zhishengfeiji3);
			Assets.groundUpBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ground_up);
			Assets.groundDownBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ground_down);
			
			
			Assets.stoneMiddleBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.sd03);
			Assets.gameOverBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.over_text);
			Assets.recordBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.record);
			Assets.playAgainBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.replay);

			Assets.startButtonBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.start_button);
			
			Assets.courseUpBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.intro);
			Assets.courseDownBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.intro2);
			Assets.courseMiddleBitmap = BitmapFactory.decodeResource(
					getResources(), R.drawable.intro3);
			Assets.exitBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.exit);
			Assets.homeBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.home);
			//Assets.gameBackgroundBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.beijing);

			// Settings.highscore=point;


			// ��Ϸ����ˢ��handler
			mRedrawHandler = new RefreshHandler();

			// �����߳�
			new Thread(this).start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (true) {
				// ͨ��������Ϣ���½���
				Message m = new Message();
				m.what = 0x101;
				mRedrawHandler.sendMessage(m);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// ��Ϸ�������
		void gameDraw(Canvas canvas) {

			if (!bInit) {
				Bitmap bm;
				bm = BitmapFactory.decodeResource(getResources(),
						R.drawable.other_load);
				canvas.drawBitmap(bm, 0, 0, null);
				Paint p1 = new Paint();

				p1.setAntiAlias(true);
				p1.setColor(Color.WHITE);
				p1.setTextSize(20);// ���������С

			} else if (bInit) {
				if (!bCourse) {
					a -= SPEED;
					if (a <= 0)
						a = 480;

					canvas.drawBitmap(Assets.startBitmap, 0, 0, null);
					canvas.drawBitmap(Assets.groundUpBitmap, a, 0, null);
					canvas.drawBitmap(Assets.groundUpBitmap, a - 480, 0, null);
					canvas.drawBitmap(Assets.groundDownBitmap, a, 700, null);
					canvas.drawBitmap(Assets.groundDownBitmap, a - 480, 700, null);
					canvas.drawBitmap(Assets.startButtonBitmap, 100, 500, null);
					canvas.drawBitmap(Assets.exitBitmap, 270, 500, null);

					b = a % 128;
					if (b >= 0 && b < 32) {
						canvas.drawBitmap(Assets.planeMiddleBitmap, 180, 300, null);
					}
					if (b >= 32 && b < 64) {
						canvas.drawBitmap(Assets.planeUpBitmap, 180, 296, null);
					}
					if (b >= 64 && b < 96) {
						canvas.drawBitmap(Assets.planeMiddleBitmap, 180, 300, null);
					}
					if (b >= 96 && b <= 128) {
						canvas.drawBitmap(Assets.planeDownBitmap, 180, 304, null);
					}
					if (highScoreScreen) {
						canvas.drawBitmap(Assets.high_scores_background, 0, 0, null);

					}
					// canvas.drawBitmap(groundBitmap, a, 448, null);
				} else if (bCourse) {
					if (!bGame) {
						time += 1;

						int temp = time % 64;
						if (temp >= 0 && temp < 16)
							canvas.drawBitmap(Assets.courseUpBitmap, 0, 0, null);
						if (temp >= 16 && temp < 32)
							canvas.drawBitmap(Assets.courseMiddleBitmap, 0, 0, null);
						if (temp >= 32 && temp < 48)
							canvas.drawBitmap(Assets.courseDownBitmap, 0, 0, null);
						if (temp >= 48 && temp < 64)
							canvas.drawBitmap(Assets.courseMiddleBitmap, 0, 0, null);
						a -= SPEED;
						if (a <= 0)
							a = 480;
						canvas.drawBitmap(Assets.groundUpBitmap, a, 0, null);
						canvas.drawBitmap(Assets.groundUpBitmap, a - 480, 0, null);
						canvas.drawBitmap(Assets.groundDownBitmap, a, 700, null);
						canvas.drawBitmap(Assets.groundDownBitmap, a - 480, 700,
								null);

					} else if (bGame) {

						if (!bGameOver) {
							time += 1;

							/*************** ����߶� ************************/
							v += 9.8;
							if (v > 120)
								v = 120;
							else if (v < -150)
								v = -150;
							if (v >= 0)
								feiji.h += ((v * 5.0) / 77);
							else if (v < 0)
								feiji.h += ((v * 4.5) / 77);
							if (feiji.h < 80)
								feiji.h = 80;
							else if (feiji.h > 660)
								feiji.h = 660;

							/*************** ʯͷ���ƶ� **********************/

							pillar.x -= SPEED+2;
							if (pillar.x <= -70) {
								pillar.x = 580;
								pillar.h = new Random().nextInt(430)+230;
							}
							
						
							
							
							

							/*************** ������� ************************/

							if ((pillar.x == feiji.x)) {
								point++;
							}
							



							/**************** �����ƶ� ***********************/
							a -= SPEED;

							/**************** �ж���ײ ***********************/
							if ((feiji.h - pillar.h < 105)
									&& (pillar.h - feiji.h < 70)
									&& (pillar.x - feiji.x < 0))
								bGameOver = true;
							
							
							if (feiji.h == 660 || feiji.h == 80)
								bGameOver = true;
							
							
						}

						/*************** ��ʾͼ�� ************************/
						// ��ʾ����
						//canvas.drawBitmap(Assets.gameBackgroundBitmap, 0, 0, null);
						
						a -= SPEED;
						if (a <= 0)
							a = 480;
						canvas.drawBitmap(Assets.gameBackgroundBitmap, a, 0, null);
						canvas.drawBitmap(Assets.gameBackgroundBitmap, a - 480, 0, null);
						

						// ��ʾ����

						canvas.drawBitmap(Assets.stoneMiddleBitmap, pillar.x,
								pillar.h, null);
					
						
						if (a <= 0)
							a = 480;
						canvas.drawBitmap(Assets.groundUpBitmap, a, 0, null);
						canvas.drawBitmap(Assets.groundUpBitmap, a - 480, 0, null);
						canvas.drawBitmap(Assets.groundDownBitmap, a, 700, null);
						canvas.drawBitmap(Assets.groundDownBitmap, a - 480, 700,
								null);

						int temp = time % 16;
						if (temp >= 0 && temp < 4)
							canvas.drawBitmap(Assets.planeMiddleBitmap, 100,
									feiji.h, null);
						if (temp >= 4 && temp < 8)
							canvas.drawBitmap(Assets.planeUpBitmap, 100, feiji.h,
									null);
						if (temp >= 8 && temp < 12)
							canvas.drawBitmap(Assets.planeMiddleBitmap, 100,
									feiji.h, null);
						if (temp >= 12 && temp < 16)
							canvas.drawBitmap(Assets.planeDownBitmap, 100, feiji.h,
									null);

						if (!bGameOver) {
							/******************* ��ʾ���� **********************/
							Paint p1 = new Paint();
							p1.setAntiAlias(true);
							p1.setColor(Color.WHITE);
							p1.setTextSize(20);// ���������С
							canvas.drawText("score:" + point, 171, 50, p1);
							canvas.drawText("acc:" + v, 171, 80, p1);
							canvas.drawText("H:" + feiji.h, 171, 110, p1);
						} else if (bGameOver) {
							canvas.drawBitmap(Assets.gameOverBitmap, 0, 100, null);
							canvas.drawBitmap(Assets.playAgainBitmap, 70, 550, null);
							canvas.drawBitmap(Assets.recordBitmap, 35, 300, null);
							canvas.drawBitmap(Assets.homeBitmap, 290, 550, null);
							Paint p1 = new Paint();
							p1.setAntiAlias(true);
							p1.setColor(Color.WHITE);
							p1.setTextSize(50);// ���������С
							canvas.drawText("" + point, 240, 375, p1);
							HighScores highscores = new HighScores();
							highscore = service.getPreferences(); 
							highscores.setScore(highscore);
							if(highscores.score<point){
								highscores.setScore(point);
								//server.update(highscores.score);
								service.save(point);
							}
							
							// ��߼�¼
							highscore = service.getPreferences(); 
							canvas.drawText(highscore+"", 240, 445, p1);
							if (highScoreScreen) {
								canvas.drawBitmap(Assets.high_scores_background, 0,
										0, null);

							}

						}

					}
				}
			}

			// ʵ��������
			Paint p = new Paint();
			p.setColor(Color.BLACK);
			p.setColor(Color.WHITE);
			canvas.drawText("init: " + bInit + "   course: " + bCourse
					+ "    game: " + bGame + "    over: " + bGameOver, 0, 10, p);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			canvas.drawColor(Color.BLACK);

			gameDraw(canvas);

		}

		// ���½��洦����
		class RefreshHandler extends Handler {
			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 0x101) {
					MyView.this.update();
					MyView.this.invalidate();
				}
				super.handleMessage(msg);
			}
		}

		// ��������
		private void update() {
		}

		// �����¼�
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				switch (event.getPointerCount()) {
				case 1:
					return onSingleTouchEvent(event);
				case 2:
					return onDoubleTouchEvent(event);
				default:
					return false;
				}
			}

			/*
			 * if ((!bCourse) && (x > 250) && (x < 400) && (y > 500) && (y < 600)) {
			 * Assets.high_scores_background = BitmapFactory
			 * .decodeResource(getResources(), R.drawable.high_scores_background);
			 * 
			 * highScoreScreen = true; }
			 */
			// ����ָ��������
			private boolean onSingleTouchEvent(MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					if (!bInit) {
						a = -2;
						bInit = true;
					} else if (bInit) {
						if ((!bCourse) && (x > 250) && (x < 400) && (y > 500)
								&& (y < 600)) {
							System.exit(0);
						}

						if ((!bCourse) && (x > 100) && (x < 200) && (y > 500)
								&& (y < 570)) {
							highScoreScreen = false;
							if ((new Random()).nextInt(3) == 0) {
								Assets.gameBackgroundBitmap = BitmapFactory
										.decodeResource(getResources(),
												R.drawable.background);
								Assets.courseDownBitmap = BitmapFactory.decodeResource(
										getResources(), R.drawable.intro);
								Assets.courseUpBitmap = BitmapFactory.decodeResource(
										getResources(), R.drawable.intro2);
								Assets.courseMiddleBitmap = BitmapFactory
										.decodeResource(getResources(),
												R.drawable.intro3);
							} else {
								Assets.gameBackgroundBitmap = BitmapFactory
										.decodeResource(getResources(),
												R.drawable.background);
								Assets.courseDownBitmap = BitmapFactory.decodeResource(
										getResources(), R.drawable.intro);
								Assets.courseUpBitmap = BitmapFactory.decodeResource(
										getResources(), R.drawable.intro2);
								Assets.courseMiddleBitmap = BitmapFactory
										.decodeResource(getResources(),
												R.drawable.intro3);
							}
							time = 0;
							a = 0;

							bCourse = true;
						}

						else if (bCourse) {
							if (!bGame) {
								time = 0;
								feiji.h = 150;
								point = 0;
								
								a = 0;
								b = 0;
								

								bGame = true;
							} else if (bGame) {
								if (!bGameOver)
									v -= 250;
								if (bGameOver && (x > 70) && (x < 170) && (y > 550)
										&& (y < 620)) {
									point = 0;
									pillar.x = 500;
									feiji.h = 100;
									bGameOver = false;
									bCourse = true;
									bGame = true;
								}
								/*
								 * if ((bGameOver) && (x > 250) && (x < 400) && (y >
								 * 500) && (y < 600)) { Assets.high_scores_background =
								 * BitmapFactory .decodeResource(getResources(),
								 * R.drawable.high_scores_background);
								 * if(!highScoreScreen){ highScoreScreen = true; }else{
								 * bGameOver = true; bCourse = true; bGame = true; }
								 * 
								 * }
								 */
								else if ((bGameOver) && (x > 250) && (x < 400)
										&& (y > 500) && (y < 600)) {
									
									pillar.x = 500;
									bGameOver = false;
									bCourse = false;
									bGame = false;
								}
							}
						}
					}
					break;

				case MotionEvent.ACTION_DOWN:

					break;
				case MotionEvent.ACTION_MOVE:

					break;
				}
				return true;
			}

			// ˫ָ��������
			private boolean onDoubleTouchEvent(MotionEvent event) {
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_UP:

					break;
				case MotionEvent.ACTION_POINTER_DOWN: {

					break;
				}
				case MotionEvent.ACTION_MOVE:

					postInvalidate();
					break;
				}
				return true;
			}
			
			

	}

	
}