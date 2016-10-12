package com.dengshizhen.bird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BirdGame extends JPanel {
	Bird bird;
	Column column1,column2;
	Ground ground;
    BufferedImage background;
   // boolean gameOver;
    int state;
    public static final int START=0;
    public static final int RUNNING=1;
    public static final int GAME_OVER=2;
    BufferedImage startImage;
    BufferedImage gameOverImage;
    int score;//分数
	
	//初始化BirdGame的属性变量
	public BirdGame()throws Exception{
		state=START;
		startImage=ImageIO.read(getClass().getResource("start.png"));
		//gameOver=false;
		gameOverImage=ImageIO.read(getClass().getResource("gameover.png"));
        score=0;
		bird =new Bird();
		column1=new Column(1);
		column2=new Column(2);
		ground=new Ground();
		background=ImageIO.read(getClass().getResource("bg.png"));
		
	}
	//重写paint方法实现绘制
	public void paint(Graphics g){
		g.drawImage(background, 0, 0, null);
		g.drawImage(column1.image, column1.x-column1.width/2, column1.y-column1.height/2, null);
		g.drawImage(column2.image, column2.x-column2.width/2, column2.y-column2.height/2, null);
	    g.drawImage(ground.image, ground.x, ground.y, null);
	    g.drawImage(bird.image, bird.x-bird.width/2, bird.y-bird.height/2, null);
	    Graphics2D g2=(Graphics2D) g;
	    g2.rotate(-bird.alpha,bird.x,bird.y);
	    g.drawImage(bird.image, bird.x-bird.width/2, bird.y-bird.height/2, null);
	    g2.rotate(bird.alpha, bird.x, bird.y);
	    //添加绘制分数方法
	    Font f=new Font(Font.SANS_SERIF,Font.BOLD,40);
	    g.setFont(f);
	    g.drawString(""+score,40,60);
	    g.setColor(Color.white);
	    g.drawString(""+score, 40-3, 60-3);
	   // if(gameOver){
	    //	g.drawImage(gameOverImage, 0, 0, null);
	    //}
	    //在paint添加游戏结束状态代码
	    switch(state){
	    case GAME_OVER:
	    	g.drawImage(gameOverImage, 70, 300, null);
	    	break;
	    case START:
	    	g.drawImage(startImage, 70, 300, null);
	    	break;
	    }
	}
	//BirdGame中添加action()
	public void action()throws Exception{
		MouseListener l=new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				//bird.flappy();
				try{
					switch(state){
					case GAME_OVER:
						column1=new Column(1);
						column2=new Column(2);
						bird=new Bird();
						score=0;
						state=START;
						break;
					case START:
						state=RUNNING;
					case RUNNING:
						//鸟向上飞
						bird.flappy();
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		addMouseListener(l);
		while(true){
			//if(!gameOver){
			//ground.step();
			//column1.step();
			//column2.step();
			//bird.step();
			//bird.fly();
			//}
			//if(bird.hit(ground)||bird.hit(column1)||bird.hit(column2)){
			//	gameOver=true;
			//}
			//if(bird.x==column1.x||bird.x==column2.x){
			//	score++;
			//}
			switch(state){
			case START:
				bird.fly();
				ground.step();
				break;
			case RUNNING:
				column1.step();
				column2.step();
				bird.step();
				bird.fly();
				ground.step();
				//计分逻辑
				if(bird.x==column1.x||bird.x==column2.x){
					score++;
				}
				//如果鸟撞地上游戏结束
				if(bird.hit(ground)||bird.hit(column1)||bird.hit(column2)){
					state=GAME_OVER;
				}
				break;
				}
			repaint();
			Thread.sleep(1000/60);
		}
}
//启动软件的方法
	public static void main(String[] args)throws Exception{
		JFrame frame=new JFrame();
		BirdGame game=new BirdGame();
		frame.add(game);
		frame.setSize(300, 650);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		game.action();
	}
}
//地面
class Ground{
	BufferedImage image;
	int x,y;
	int width;
	int height;
	
	public Ground() throws Exception{
		image=ImageIO.read(getClass().getResource("ground.png"));
		width=image.getWidth();
		height=image.getHeight();
		x=0;
		y=500;
	}
	public void step(){
		x--;
		if(x==-50){
			x=0;
		}
	}
}
//柱子
class Column{
	BufferedImage image;
	int x,y;
	int width,height;
	//柱子中间间隙
	int gap;
	int distance;//距离
	
	Random random=new Random();
	//构造器：初始化数据，n代表第几个柱子
	public Column(int n) throws Exception{
		image=ImageIO.read(getClass().getResource("column.png"));
		width=image.getWidth();
		height=image.getHeight();
		gap=144;
		distance=245;
		x=550+(n-1)*distance;
		y=random.nextInt(218)+132;
	}
	public void step(){
		x--;
		if(x==-width/2){
			x=distance*2-width/2;
			y=random.nextInt(218)+132;
		}
	}
}
//鸟类
class Bird{
	BufferedImage image;
	int x,y;
	int width,height;
	int size;//用于鸟碰撞
	//在Bird类中增加属性，用于计算鸟的位置
	double g;
	double t;
	double v0;
	double speed;
	double s;
	double alpha;
	BufferedImage[] images;
	int index;
	
	public Bird() throws Exception{
		image=ImageIO.read(getClass().getResource("0.png"));
		width=image.getWidth();
		height=image.getHeight();
		x=132;
		y=280;
		size=40;
		g=4;
		v0=20;
		t=0.25;
		speed=v0;
		s=0;
		alpha=0;
		images=new BufferedImage[3];
		for(int i=0;i<3;i++){
			images[i]=ImageIO.read(getClass().getResource(i+".png"));
		}
		index=0;
	}
	//飞翔的代码
	public void fly(){
		index++;
		image=images[(index/12)%3];
	}
	//移动方法
	public void step(){
		double v0=speed;
		s=v0*t+g*t*t/2;//上抛运动位移
		y=y-(int)s;//计算鸟的坐标
		double v=v0-g*t;//计算下次速度
		speed=v;
		alpha=Math.atan(s/8);
	}
	public void flappy(){
		//重新设置初始速度，重新向上飞
		speed=v0;
	}
	
	public boolean hit(Ground ground){
		boolean hit=y+size/2>ground.y;
		if(hit){
			y=ground.y-size/2;
			alpha=-3.1415926538979323/2;
		}
		return hit;
	}
	public boolean hit(Column column){
		if(x>column.x-column.width/2-size/2&&x<column.x+column.width/2+size/2){
			if(y>column.y-column.gap/2+size/2&&y<column.y+column.gap/2-size/2){
				return false;
			}
			return true;
		}
		return false;
	}
	
	
}
