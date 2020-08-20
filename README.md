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
