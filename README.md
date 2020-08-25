# ARSW-LAB-2

## Part I - Before finishing class
Thread control with wait/notify. Producer/consumer

1. Check the operation of the program and run it. While this occurs, run jVisualVM and check the CPU consumption of the corresponding process. Why is this consumption? Which is the responsible class? 
	
	- Este Consumo se debe a las clases producer y comsumer, mienstras producer tiene un limite de stock consumer siempre esta preguntando y pidiendo productos habiendo o no en la cola de produccion
	
	![image](https://github.com/csarssj/ARSW-LAB-2/blob/master/resources/1.png)
	
2. Make the necessary adjustments so that the solution uses the CPU more efficiently, taking into account that - for now - production is slow and consumption is fast. Verify with JVisualVM that the CPU consumption is reduced. 

	- Para mejorar la eficiencia y disminuir el consumo en la CPU se sincronizaron las clases por medio del queue que ellos comparte, mientras haya elementos que consumir el consumer lo hara, si no tendrá que esperar
	
	![image](https://github.com/csarssj/ARSW-LAB-2/blob/master/resources/2.png)
	
	- Consumer: 
	   
	   ```java
	   @Override
	   public void run() {
		while (true) {
			synchronized(queue) {
				if (queue.size() > 0) {
					int elem=queue.poll();
					System.out.println("Consumer consumes "+elem);                                
			    	}
			   	try {
					queue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		}
	    }
	   ```
	- Producer: 
	  
	  ```java
	  @Override
	  public void run() {
	  	while (true) {
			dataSeed = dataSeed + rand.nextInt(100);
		    	System.out.println("Producer added " + dataSeed);
		    	synchronized(queue) {
				queue.add(dataSeed);
				queue.notifyAll();
		    	}
		    	try {
				Thread.sleep(1000);
		    	} catch (InterruptedException ex) {
				Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
		    	}
		}
	    }		
	  ```

3. Make the producer now produce very fast, and the consumer consumes slow. Taking into account that the producer knows a Stock limit (how many elements he should have, at most in the queue), make that limit be respected. Review the API of the collection used as a queue to see how to ensure that this limit is not exceeded. Verify that, by setting a small limit for the 'stock', there is no high CPU consumption or errors.

	- Se modifico el numero de "stocks" a 5 y agregando un condicional en el Producer para que no agregue más si se alcanza el límite. Además se ralentizo la velocidad   		del consumer, dando como resulado un consumo mínimo de la CPU y sin presentarse nigún error.
	
	![image](https://github.com/csarssj/ARSW-LAB-2/blob/master/resources/1.png)
	
	![image](https://github.com/csarssj/ARSW-LAB-2/blob/master/resources/2.png)
	- Producer: 
	  
	  ```java
	  @Override
	  public void run() {
	  	while (true) {
			if(queue.size() < stockLimit){
				dataSeed = dataSeed + rand.nextInt(100);
		    		System.out.println("Producer added " + dataSeed);
		    		synchronized(queue) {
					queue.add(dataSeed);
					queue.notifyAll();
			    	}
			}
		    	try {
				Thread.sleep(500);
		    	} catch (InterruptedException ex) {
				Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
		    	}
		}
	    }  	
	  ```
## Part II - Synchronization and Dead-Locks.

1. Review the “highlander-simulator” program, provided in the edu.eci.arsw.highlandersim package. This is a game in which:
    - You have N immortal players. 
    - Each player knows the remaining N-1 player.
    - Each player permanently attacks some other immortal. The one who first attacks subtracts M life points from his opponent, and increases his own life points by the same amount. 
    - The game could never have a single winner. Most likely, in the end there are only two left, fighting indefinitely by removing and adding life points. 
2. Review the code and identify how the functionality indicated above was implemented. Given the intention of the game, an invariant
should be that the sum of the life points of all
players is always the same (of course, in an instant
of time in which a time increase / reduction operation is not in
process ). For this case, for N players, what 
should this value be?
    
    El invariante tendria que ser N(Número de inmortales)*100 (DEFAULT_IMMORTAL_HEALTH)
    
3. Run the application and verify how the ‘pause and check’ option works. 
Is the invariant fulfilled?
    
    El invariante no se cumple, se altera mediante pasa el tiempo.
     
4. A first hypothesis that the race condition for this function 
(pause and check) is presented is that the program consults the list whose 
values ​​it will print, while other threads modify their values. 
To correct this, do whatever is necessary so that, before printing the current
results, all other threads are paused. Additionally, implement the ‘resume’
option.

    ```java
	JButton btnResume = new JButton("Resume");

        btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (Immortal im : immortals) {
                    im.resumen();
                }

            }
        });
	```
	
    ```java
	JButton btnPauseAndCheck = new JButton("Pause and check");
        btnPauseAndCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int sum = 0;
                for (Immortal im : immortals) {
                    sum += im.getHealth();
                    im.pause();
                }

                statisticsLabel.setText("<html>"+immortals.toString()+"<br>Health sum:"+ sum);
             
            }
        });
	```

5. Check the operation again 
(click the button many times). Is the invariant fulfilled or not ?.

    No, no se cumple todavia


6. Identify possible critical regions in regards to the fight of the immortals.
Implement a blocking strategy that avoids race conditions. Remember that if you 
need to use two or more ‘locks’ simultaneously, you can use nested synchronized
blocks:

7. After implementing your strategy, start running your program, and pay attention to whether it comes to a halt.
If so, use the jps and jstack programs to identify why the program stopped.

8. Consider a strategy to correct the problem identified above 
(you can review Chapter 15 of Java Concurrency in Practice again)
      
      ```java
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
	```


9. Once the problem is corrected, rectify that the program continues to 
function consistently when 100, 1000 or 10000 immortals are executed.
If in these large cases the invariant begins to be breached again, 
you must analyze what was done in step 4.
    
    No cambio el invariante en ninguna de las ejecuciones y se mantuvo.
   
10. An annoying element for the simulation is that at a certain point in it there are few living
 'immortals' making failed fights with 'immortals' already dead.
 It is necessary to suppress the immortal dead of the simulation as they die.*
    1. Analyzing the simulation operation scheme, could this create a race condition? Implement the functionality, run the simulation and see what problem arises when there are many 'immortals' in it. Write your conclusions about it in the file ANSWERS.txt. 
    2. Correct the previous problem WITHOUT using synchronization, since making access to the shared list of immortals sequential would make simulation extremely slow.

11.  To finish, implement the STOP option.

    ```java
        JButton btnStop = new JButton("STOP");
        btnStop.setForeground(Color.RED);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for (Immortal im : immortals) {
                    im.setStop();
                }

            }
        });
	```
