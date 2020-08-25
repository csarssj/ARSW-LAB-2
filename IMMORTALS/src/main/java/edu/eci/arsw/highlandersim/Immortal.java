package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
   
    private boolean pause = false;
    
    private boolean isAlive = true;

    private boolean stop = false;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (true) {
        	while(!stop && isAlive) {
                synchronized(this) {
                	if (pause) {
        				try {
        					wait(); // wait FOREVER for data
        				} catch (InterruptedException ex) {
        					ex.printStackTrace();
    		            }
    		        }	
                }
	            Immortal im;
	
	            int myIndex = immortalsPopulation.indexOf(this);
	
	            int nextFighterIndex = r.nextInt(immortalsPopulation.size());
	
	            //avoid self-fight
	            if (nextFighterIndex == myIndex) {
	                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
	            }
	
	            im = immortalsPopulation.get(nextFighterIndex);
	
	            if(isAlive) {this.fight(im);}
	
	            try {
	                Thread.sleep(1);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
        	}

        }

    }

    public void fight(Immortal i2) {
    	Immortal one ;
    	Immortal two ;
    	if(this.getId() < i2.getId()) {
    		one = i2;
    		two = this;
    	}
    	else{
    		one = this;
    		two = i2;
    	}
    	if(this.getHealth()<= 0) {
    		this.isAlive = false;
    		immortalsPopulation.remove(this);
    	}
    	synchronized(one) {
    		synchronized(two) {
		        if (i2.getHealth() > 0 && isAlive) {
		            i2.changeHealth(i2.getHealth() - defaultDamageValue);
		            this.health += defaultDamageValue;
		            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
		        } else {
		            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
		        }
    		}
    	}
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }
    
    public void setStop() {
    	this.stop = true;
    }
    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public synchronized void resumen() {
    	this.pause = false;
    	this.notifyAll();
    }
    public synchronized void pause() {
    	this.pause = true;
    	this.notifyAll();
    }
}
