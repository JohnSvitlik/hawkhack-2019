import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;


public class testClass extends Application {
	final double WIDTH = 500;
	final double HEIGHT = 500;
	final static int AMOUNT_OF_MOVERS = 250;
	static int[] loc;
	static Vehicle veh;
	static server serv = new server();
	GraphicsContext gc;
	
	
	public void start(Stage theStage)
	{
		veh = new Vehicle();
		
		Group root = new Group();
		Scene scene = new Scene(root, WIDTH, HEIGHT);
		Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		theStage.setScene(scene);
		theStage.show();
		scene.setFill(Color.BLACK);
		
		Timeline tl = new Timeline();
		tl.setRate(1);
		tl.setCycleCount(Timeline.INDEFINITE);
		Mover[] movers = new Mover[AMOUNT_OF_MOVERS];
		Vector prev = new Vector(55, 55);
		point p = new point(prev);
		p.g = gc;
		for(int i = 0; i < movers.length; i++)
		{
			movers[i] = new Mover(scene);
			movers[i].setID(i);
		}
		KeyFrame key = new KeyFrame(Duration.millis(1000/40), e-> 
		{
			loc = serv.loc;
			gc.clearRect(0, 0, WIDTH, HEIGHT);
			
			
			
			
			
			try
			{
				veh.seek(new Vector(loc[0]*2, loc[1]*2));
				veh.update();
				//Vector next = new Vector(loc[0]*2, loc[1]*2);
				//p.next = next;
				//p.upd();
				gc.setFill(Color.DARKRED);
				gc.fillOval(veh.location.x, veh.location.y, 25, 25);
				for(int i = 0; i < movers.length; i++)
				{
					movers[i].update();
					movers[i].display(gc);
					//movers[i].checkBounds_infinite(scene);
					
					
					movers[i].processNeighbors();
					
					for(int j = 0; j < movers.length; j++)
					{
						
						movers[i].localGrav(movers[j].location);
						movers[i].calculateNeighbors(movers[j]);
						
						
					}
				}
			}
			catch(Throwable t)
			{
				gc.fillOval(prev.x, prev.y, 10, 10);
			}
			
			
			
		}); tl.getKeyFrames().add(key);
		tl.play();
		
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		
		Thread t = new Thread(serv);
		t.start();
		launch(args);
		
		
		
	  }
	
}

class Vector {
	double x;
	double y;
	static Vector mousePos;
	
	Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	void add(Vector v)
	{
		this.x += v.x;
		this.y += v.y;
	}
	void sub(Vector v)
	{
		this.x -= v.x;
		this.y -= v.y;
	}
	void mult(double n)
	{
		this.x *= n;
		this.y *= n;
	}
	void div(double n)
	{
		this.x = x / n;
		this.y = y / n;
	}
	double mag()
	{
		double c = Math.sqrt((x * x) + (y * y));
		return c;
	}
	void normalize()
	{ 
		if (mag() != 0)
		{
			div(mag());
		}
		
	}
	void limit(double max)
	{
		if(mag() > max)
		{
			normalize();
			mult(max);
		}
	}
	void Random2D()
	{
		double hypotenuse = 1;
		double x = Math.random();
		double y = Math.random();
		Vector rand = new Vector(Math.random(), Math.random());
		hypotenuse = Math.sqrt((rand.x * rand.x) + (rand.y * rand.y));
		rand.x = rand.x / hypotenuse;
		rand.y = rand.y / hypotenuse;
		if(Math.round(x) == 1)
		{
			rand.x *= -1;
		}
		if(Math.round(y) == 1)
		{
			rand.y *= -1;
		}
		this.x = rand.x;
		this.y = rand.y;
		
	}
	
	static Vector add(Vector v1, Vector v2)
	{
		Vector v3 = new Vector(v1.x + v2.x, v1.y + v2.y);
		return v3;
	}
	static Vector sub(Vector v1, Vector v2)
	{
		Vector v3 = new Vector(v1.x - v2.x, v1.y - v2.y);
		return v3;
	}
	static Vector div(Vector v1, double n)
	{
		Vector v3 = new Vector(v1.x / n, v1.y / n );
		return v3;
	}
	static Vector mult(Vector v1, double n)
	{
		Vector v3 = new Vector(v1.x * n, v1.y * n);
		return v3;
	}
	 Vector get()
	{
		Vector v = new Vector(x, y);
		return v;
	}
	 double dist(Vector v2)
	 {
		 double dist = Math.sqrt(((x - v2.x) * (x - v2.x)) + ((y - v2.y) * (y - v2.y)));
		 return Math.abs(dist);
	 }
	
	
	
	
}
class Mover {
	Vector location;
	Vector velocity;
	Vector acceleration;
	Vector dir;
	Vector mouse = new Vector(0, 0);
	double topspeed = 10; 
	Scene mainScene;
	Vehicle veh = testClass.veh;
	double mass;
	Paint color;
	int[] neighbor = new int[testClass.AMOUNT_OF_MOVERS];
	int ID;
	int neighbors;
	
	Mover(Scene scene)
	{
		location = new Vector((Math.random() * scene.getWidth()), (Math.random() * scene.getHeight())); 
		//location = new Vector((scene.getWidth()/2) + ((Math.pow(-1, Math.round(Math.random()))) * (250 * Math.random())), (scene.getHeight()/2) + ((Math.pow(-1, Math.round(Math.random()))) * (250 * Math.random())) );
		//velocity = new Vector((ThreadLocalRandom.current().nextInt(-2, 3)), (ThreadLocalRandom.current().nextInt(-2, 3)));
		//velocity = new Vector(Math.pow(-1, Math.round(Math.random())) * Math.random() * 2, Math.pow(-1, Math.round(Math.random())) * Math.random() * 2);
		velocity = new Vector(0, 0);
		acceleration = new Vector(0.00, 0.00);
		mainScene = scene;
		mass = (Math.random() * 5);
		
		
		
		
		
		
		
		
		
	}
	
	void localGrav(Vector v)
	{
		Vector gravPull = Vector.sub(v, location);
		gravPull.normalize();
		gravPull.mult(mass);
		gravPull.mult(0.0001);
		
		applyForce(gravPull);
	}
	
	void setID(int i)
	{
		this.ID = i;
	}
	int getID()
	{
		return this.ID;
	}
	void update()
	{
		this.mouse.x = testClass.veh.location.x;
		this.mouse.y = testClass.veh.location.y;
		Vector dir = Vector.sub(mouse, location);
		Vector random_wind = new Vector((Math.pow(-1, Math.round(10 * Math.random())) * Math.random()), (Math.pow(-1, Math.round(10 * Math.random())) * Math.random()));
		Vector gravity = new Vector(0, 0.1 * mass);
		Vector wind = new Vector(0.1, 0);
		//System.out.println(dir.x + " " + dir.y);   //  ##  Mouselocation DEBUG  ##
		dir.normalize();
		dir.mult(1.5);
		applyForce(dir);		//applies force of mouse pointer attractor
		//applyForce(wind);
		
		//applyForce(random_wind);
		//applyForce(gravity);
		//applyForce(wind);
		//acceleration.Random2D();
		velocity.add(acceleration);
		velocity.limit(topspeed);
		location.add(velocity);
		acceleration.mult(0);
		
		
	}
	void display(GraphicsContext gc)
	{
		if(neighbors > 100)
		{
			this.color = Color.RED;
		}
		if(neighbors <= 100)
		{
			this.color = Color.BLUE;
		}
		gc.setFill(color);
		
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1.5);
		gc.fillOval(location.x, location.y, mass * 2, mass * 2);
		
		
		
	}
	
	void calculateNeighbors(Mover reference)
	{
		
		if(this.location.dist(reference.location) < 50)
		{
			this.neighbor[reference.getID()] = 1;
		}
		else if(this.location.dist(reference.location) >= 150)
		{
			this.neighbor[reference.getID()] = 0;
		}
		
		
		
	}
	void processNeighbors()
	{
		neighbors = 0;
		for(int i = 0; i < testClass.AMOUNT_OF_MOVERS; i++)
		{
			if(this.neighbor[i] == 1)
			{
				neighbors++;
			}
			
		}
	}
	void checkBounds_Bounce(Scene scene)
	{
		if(location.x > scene.getWidth() || location.x < 0)
		{
			velocity.x *= -1;
		}
		else if(location.y > scene.getHeight() || location.y < 0)
		{
			velocity.y *= -1;
		}
	}
	
	void checkBounds_infinite(Scene scene)
	{
		if(location.x > scene.getWidth())
		{
			location.x = 0;
		}else if (location.x < 0)
		{
			location.x = scene.getWidth();
		}
		if(location.y > scene.getHeight())
		{
			location.y = 0;
		}else if (location.y < 0)
		{
			location.y = scene.getHeight();
		}
	}
	void applyForce(Vector force)
	{
		Vector f = force.get();
		f.div(mass);
		acceleration.add(f);
		
	}
}
class point
{
	Vector prev;
	Vector next;
	GraphicsContext g;
	point(Vector v1)
	{
		this.prev = v1;
	}
	void next(Vector v2)
	{
		this.next = v2;
	}
	void upd()
	{
		for(int i = 0; i < 15; i++)
		{
			g.setFill(Color.DARKRED);
			g.fillOval(prev.x + ((next.x - prev.x)/15), prev.y + ((next.y - prev.y)/15), 25, 25);
		}
		this.prev = this.next;
	}
}
class Vehicle {
 Vector location = new Vector(55, 55);
 Vector velocity = new Vector(0, 0);
 Vector accel = new Vector(0, 0);
 float maxVel = 10;
 float maxForce = 3; 
 void seek(Vector target)
 {
	 Vector desire = Vector.sub(target, location);
	 desire.normalize();
	 desire.mult(maxVel);
	 Vector steer = Vector.sub(desire, velocity);
	 steer.limit(maxForce);
	 applyForce(steer);
	 
 }
 void applyForce(Vector force)
	{
		//Vector f = force.get();
		//f.div(1); //mass
		accel.add(force);
	}
 void update() {
	    velocity.add(accel);
	    velocity.limit(maxVel);
	    location.add(velocity);
	    accel.mult(0);
	  }
 

	
}