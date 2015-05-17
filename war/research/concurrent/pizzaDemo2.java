// Pizza Producer/Consumer Simulation to Demonstrate Threads
// By Greg Murray
// Date: version 2 completed 8/27/96
//

import java.awt.*;
import java.applet.Applet;


public class pizzaDemo2 extends Applet implements Runnable {
  // Declare varribles/objects
   protected MediaTracker tracker;  
   private Thread pizzaThread= null;
   private PizzaStorage pizzastorage;
   private int number, pizzasOnOrder, total;
   private boolean orderMade, deliveryMade, pizzaMade;
   Thread consumer, producer;
   Image house, pizza, man, man2, tree, tree2, sun, usagi;

//The following function was modified from the pause funciton presented
//in Java in 21 Days page 204 which was written by Laura Lemay 

void pause(int atime){
  try{
  pizzaThread.sleep(atime);
 } catch(InterruptedException e){}
}

public void paint(Graphics g){

   int loop;
   deliveryMade = pizzastorage.getDeliveryMade();
   this.setBackground(Color.white);
   g.drawImage(house, 400,150, this);
   g.drawImage(pizza, 10,10, this);
   g.drawImage(sun,420,20,this);
   g.drawImage(tree, 150,50,this);
   g.drawImage(usagi, 355,250,this);
   g.drawImage(tree2, 250,100,this);
  
   if (deliveryMade){
      // Set Color
      g.setColor(Color.white);
      g.fillRect(100,100,25,23);  // exit the window

      // down animation
      for(loop = 131; loop <= 200; loop++){
           g.drawImage(man,75,loop, this);
          }

      // across animation
      for(loop = 75; loop <= 375; loop++){
         g.drawImage(man,loop,200, this);
         g.fillRect(loop-1,200,1,50);
          }

      // text animation
      g.setColor(Color.red);
      g.drawString("Pizza",loop,180);
      pause(2000);
      g.setColor(Color.white);
      g.fillRect(loop,165,50,25);
      g.setColor(Color.red);
      g.drawString("Thank You",380,165);
      pause(2000);
      g.setColor(Color.white);
      g.fillRect(380,145,65,25);

      // return across animation
      for(loop = 375; loop >= 75; loop--){
         g.drawImage(man2,loop,200, this);
         g.fillRect(loop+30,200,1,50);
          }
      // retun up animation
      for(loop = 200; loop >= 131; loop--){
           g.drawImage(man2,75,loop, this);
         }
      g.fillRect(75,loop,40,50);
      g.drawImage(pizza, 10,10, this);
      }
 }

public void run(){   // pizzaThread

 while(pizzaThread!=null){

 try{
      pizzaThread.sleep(1000);
      number = pizzastorage.get();
      total = pizzastorage.getTotalPizzasSold();
      deliveryMade = pizzastorage.getDeliveryMade();
      orderMade = pizzastorage.getOrderMade();
      pizzasOnOrder = pizzastorage.getPizzasOnOrder();
      pizzaMade = pizzastorage.getPizzaMade();

      if (pizzaMade)    // notify that a Pizza was completed
         {
          getAppletContext().showStatus("A Pizza Is Finished");
          pause(1600);
          pizzastorage.handlePizzaMade(false);
         }

      if (orderMade)
         {
          getAppletContext().showStatus("An Order was Made");
          pause(1600);
          pizzastorage.handleOrderMade(false);
         }



      if (deliveryMade)  //Notify if delivery is made and do animation
         {
          repaint();  //go do the animation
          getAppletContext().showStatus("A Delivery is Made");
          pause(1600);
          pizzastorage.handleDeliveryMade(false); //delivery done
         }


      getAppletContext().showStatus("Unsold Pizzas " + this.number
            + "          Pizzas on Order  " + pizzasOnOrder
            + "          Total Pizzas Sold  " + total);
   
         
    } catch(InterruptedException e) {
               }
   }
}


public void start (){  // start up all the threads when pizzaThread is null
           
    if (pizzaThread==null)
      {
    	 tracker = new MediaTracker(this);
       PizzaStorage p= new PizzaStorage();
       pizzastorage = p;
       producer = new MyProducer(p);
       pizzaThread = new Thread(this);
       consumer = new MyConsumer(p);

       // load the images

       house = getImage(getCodeBase(), "images/house.gif");
       pizza = getImage(getCodeBase(), "images/pizza.gif");
       man = getImage(getCodeBase(), "images/man.gif");
       man2 = getImage(getCodeBase(),"images/man2.gif");
 
       // This should help those of you on Unix to see the men

       tracker.addImage(man, 1);
       tracker.addImage(man2,2);  
       tree = getImage(getCodeBase(), "images/tree.gif");
       tree2 = getImage(getCodeBase(), "images/tree2.gif");

	 try {
            tracker.waitForID(1);
           } 
	 catch (InterruptedException e){}
       try {
            tracker.waitForID(2);
          } 
	 catch (InterruptedException e){}
	 
	 sun = getImage(getCodeBase(), "images/sun.gif");
       usagi = getImage(getCodeBase(), "images/usagi.gif");

       // start the threads
       consumer.start();
       pizzaThread.start();
       producer.start();
      }
}


public void stop(){

  // shut down all the threads
  pizzaThread.stop();
  pizzaThread = null;
  producer.stop();
  producer = null;
  consumer.stop();
  consumer = null;
}
}// end PizzaDemo

//*************************************************************************



class MyProducer extends Thread {
 
  private PizzaStorage pizzastorage;
  private int number, pizzasOnOrder, total;
 

public MyProducer(PizzaStorage p){
  pizzastorage=p;
}// end constructor

public void  run (){     //producer Thread
     
  while(this!=null)  
    {
      try{
            this.sleep(30000); // making a pizza
            this.number = pizzastorage.get();
            this.total = pizzastorage.getTotalPizzasSold();
            this.pizzasOnOrder = pizzastorage.getPizzasOnOrder();
            this.pizzastorage.handlePizzaMade(true);
            System.out.println("A Pizza is completed");

            // Handle those waiting for pizza

            if (pizzasOnOrder>=1){
                  pizzastorage.handleTotalPizzasSold(total + 1);
                  pizzastorage.handlePizzasOnOrder(pizzasOnOrder-1);
                  System.out.println("Consumer gets Ordered Pizza");
                  pizzastorage.handleDeliveryMade(true); //notify to deliver
              }
            else{
                  pizzastorage.put(this.number + 1);  // add pizza
                }
         } catch(InterruptedException e) {
               }
    }  //end while loop   
     
} // end Producer run

 
} // end Producer class

//************************************************************************

class MyConsumer extends Thread {

  int pizzaMade;
  private PizzaStorage pizzastorage;
  private int number, pizzasOnOrder, total;

  //Constructor
public MyConsumer (PizzaStorage p){
        pizzastorage = p;
       }

public void  run (){
      
    while(this!=null)  
      {
       try{
            this.sleep((int)(Math.random()*55000));// randomize orders
            this.number = pizzastorage.get();
            this.pizzasOnOrder = pizzastorage.getPizzasOnOrder();
            this.total = pizzastorage.getTotalPizzasSold();
            pizzastorage.handleOrderMade(true);

            //if there are pizza give them a pizza
            if (this.number>=1) 
               {
                  pizzastorage.handleTotalPizzasSold(total + 1);
                  pizzastorage.put(this.number-1); // reduce pizza in storage
                  pizzastorage.handleDeliveryMade(true); //notify for delivery
                  System.out.println("Consumer gets an Unsold pizza");
               }
            //if there are no pizzas put on order
            else if (this.number<=1){
               pizzastorage.handlePizzasOnOrder(this.pizzasOnOrder+1);//increase 
               System.out.println("No Pizzas -- Put On Order  ");
              }
          }   catch(InterruptedException e) {
               }
       } // end while loop
} // end Consumer run 
} // end class Consumer Class

//*************************************************************************

//This is the monitor
//The concept for this monitor was adapted from  an example in a tutorial
//Located on the internet at:
//http://w6.infosys.tuwien.ac.at/java...l/java/threads/sysnchronization.html
//The code was adapted here as a monitor for my two threads

class PizzaStorage {

  private int safePizza;
  private boolean orderMade = false;
  private boolean deliveryMade = false;
  private boolean pizzaMade = false;
  private int pizzasOnOrder;
  private int totalPizzasSold;
 
//manipulate pizzas available
public synchronized void put(int invalue) {
  safePizza = invalue;
  notify();
}

//retrieve pizzas that are in storage
public synchronized int get() {
  notify();
  return safePizza;
}

//handle the pizzas on order
public synchronized void handlePizzasOnOrder (int invalue) {
  pizzasOnOrder = invalue;
  notify(); 
}

//retrieve the pizzas on order
public synchronized int getPizzasOnOrder() {
  notify();
  return pizzasOnOrder;
}

//retrieve the total pizzas sold
public synchronized int getTotalPizzasSold() {
  notify();
  return totalPizzasSold;
}

//allow manipulation of the total pizzas sold
public synchronized void handleTotalPizzasSold (int invalue) {
  totalPizzasSold = invalue;
  notify();
}

//allow for the manipulation of order made for notification: true = order made
public synchronized void handleOrderMade (boolean invalue) {
  orderMade = invalue;
  notify();
}

//notify if an order has been made: true  = order to be notified
public synchronized boolean getOrderMade() {
  notify();
  return orderMade;
}

//all the delivery made to be manipulated: true = an order is made
public synchronized void handleDeliveryMade (boolean invalue) {
  deliveryMade = invalue;
  notify();
}

//notify if a delivery is to be made: true = delivery to be made
public synchronized boolean getDeliveryMade() {
  notify();
  return deliveryMade;
}

//allow the user to manipulate wether or not a pizza order is made: true = notify
public synchronized void handlePizzaMade (boolean invalue) {
  pizzaMade = invalue;
  notify();
}

//notify if a pizza made notification is needed: true = notify
public synchronized boolean getPizzaMade() {
  notify();
  return pizzaMade;
}
}// end PizzaStorage Class


