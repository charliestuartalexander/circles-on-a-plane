/*
 * PROJECT I: Project1.java
 *
 * As in project 0, this file - and the others you downloaded - form a
 * template which should be modified to be fully functional.
 *
 * This file is the *last* file you should implement, as it depends on both
 * Point and Circle. Thus your tasks are to:
 *
 * 1) Make sure you have carefully read the project formulation. It contains
 *    the descriptions of all of the functions and variables below.
 * 2) Write the class Point.
 * 3) Write the class Circle
 * 4) Write this class, Project1. The results() method will perform the tasks
 *    laid out in the project formulation.
 */

import java.util.*;
import java.io.*;

public class Project1 {
    // -----------------------------------------------------------------------
    // Do not modify the names or types of the instance variables below! This 
    // is where you will store the results generated in the Results() function.
    // -----------------------------------------------------------------------
    public int      circleCounter; // Number of non-singular circles in the file.
    public double[] aabb;          // The bounding rectangle for the first and 
                                   // last circles
    public double   Smax;          // Area of the largest circle (by area).
    public double   Smin;          // Area of the smallest circle (by area).
    public double   areaAverage;   // Average area of the circles.
    public double   areaSD;        // Standard deviation of area of the circles.
    public double   areaMedian;    // Median of the area.
    public int      stamp = 220209;
    // -----------------------------------------------------------------------
    // You should implement - but *not* change the types, names or parameters of
    // the variables and functions below.
    // -----------------------------------------------------------------------

    /**
     * Default constructor for Project1. You should leave it empty.
     */
    public Project1() {
        // This method is complete.
    }

    /**
     * Results function. It should open the file called fileName (using
     * Scanner), and from it generate the statistics outlined in the project
     * formulation. These are then placed in the instance variables above.
     *
     * @param fileName  The name of the file containing the circle data.
     */
    public void results(String fileName){
      // Initialise line counter, coords of centre and radius
      double x, y, rad;
      int lineCount = 0;
    
      // Initialise the max and minimum radius to something sensible
      double maxRAD = 0;
      double minRAD = 100000000;

      // Initialise index for max, min and median radius
      int imax = 0;
      int imin = 0;
      double radMedian = 0;

      //Create Array list for circles including sungular, rads not incl singular and circles not incl singular
      ArrayList<Circle> ALcircles = new ArrayList<Circle>();
      List<Double> ALrads = new ArrayList<Double>();
      ArrayList<Circle> ALnoscircles = new ArrayList<Circle>();
    
      try (
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)))
        ) {
    
        while(scanner.hasNext()) {
      
          //Read the three values on each line of the file
          x = scanner.nextDouble();
          y = scanner.nextDouble();
          rad = scanner.nextDouble();
          Circle circ = new Circle(x,y,rad); 

          // Adding circles to Array List
          ALcircles.add(circ);
      
          // Find max, min for radius
          if (rad > maxRAD) {
            maxRAD = rad;
            imax = lineCount;
          }

          if ((rad < minRAD) && (rad>Point.GEOMTOL)) {
            minRAD = rad;
            imin = lineCount;
          }
          if (rad > Point.GEOMTOL) {
            circleCounter ++;
            ALrads.add(rad);
            ALnoscircles.add(circ);
          }
        
          lineCount++;
      }
      
      } catch(Exception e) {
        System.err.println("An error has occured. See below for details");
        e.printStackTrace();
      } 
    
      // circArray is the array including singular circles
      Circle[] circArray = ALcircles.toArray(new Circle[ALcircles.size()]);
      // radArray is the array of radii not including singular cirles
      Double[] radArray = ALrads.toArray(new Double[ALrads.size()]);
      // nosArray is the array not including singular circles
      Circle[] nosArray = ALnoscircles.toArray(new Circle[ALcircles.size()]);

      // sort array of radii
      Arrays.sort(radArray);

      Circle maxRadcirc = circArray[imax];
      Circle minRadcirc = circArray[imin];

      // Storing results in instance variables
      Smax=maxRadcirc.area();
      Smin=minRadcirc.area();
      areaAverage = averageCircleArea(circArray);
      areaSD = areaStandardDeviation(circArray);

      // Even and odd cases considered to calculate median radius and area
      if ((radArray.length % 2) == 0){
        radMedian=(radArray[(radArray.length/2)-1]+radArray[(radArray.length/2)])/2;
      }
      else {
        radMedian=radArray[(radArray.length-1)/2];
      }
      areaMedian=(Math.PI*Math.pow(radMedian,2));

      // Take 10th and 20th circle in none singular array
      // Then calculate x and y coords of edges of rectangle
      Circle[] aabbcirc= {nosArray[9],nosArray[19]};
      aabb = calculateAABB(aabbcirc);

      }

    /**
     * A function to calculate the avarage area of circles in the array provided. 
     * This array may contain 0 or more circles.
     *
     * @param circles  An array of Circles
     */
    public double averageCircleArea(Circle[] circles) {
      double a = 0;
      int k = circles.length;
      for(int i = 0;i<circles.length;i++){
        double radnow = circles[i].getRadius();
        if (radnow<=Point.GEOMTOL){
          k=k-1;
        }
        else{        
        a=a+circles[i].area();
        }
      }
      double AvArea = a/k;
      return AvArea;
    }
    
    /**
     * A function to calculate the standard deviation of areas in the circles in the array provided. 
     * This array may contain 0 or more circles.
     * 
     * @param circles  An array of Circles
     */
    public double areaStandardDeviation(Circle[] circles) {
      double a = 0;
      int k = circles.length;
      for(int i = 0;i<circles.length;i++){
        double radnow = circles[i].getRadius();
        if (radnow<=Point.GEOMTOL){
          k=k-1;
        }
        else{        
        a=a+Math.pow(((circles[i].area())-averageCircleArea(circles)),2);
        }
      }
      double SDArea = Math.sqrt(a/k);
      return SDArea;
    }
    
    /**
     * Returns 4 values in an array [X1,Y1,X2,Y2] that define the rectangle
     * that surrounds the array of circles given.
     * This array may contain 0 or more circles.
     *
     * @param circles  An array of Circles
     * @return An array of doubles [X1,Y1,X2,Y2] that define the bounding rectangle with
     *         the origin (bottom left) at [X1,Y1] and opposite corner (top right)
     *         at [X2,Y2]
     */
    public double[] calculateAABB(Circle[] circles) 
    {   // Initialise Max's and Min's
        double maxX = -100000000;
        double maxY = -100000000;
        double minX = 100000000;
        double minY = 100000000;
        // initialise count for singular to return 0
        int scount = 0;
        for(int i = 0;i < circles.length;i++){
          double circx = (circles[i].getCentre()).getX();
          double circy = (circles[i].getCentre()).getY();
          double circrad = circles[i].getRadius();
          if (circles[i].getRadius() > Point.GEOMTOL){
            if ((circx+circrad) > maxX){
              maxX=(circx+circrad);
            }
            if ((circy+circrad) > maxY){
              maxY=(circy+circrad); 
            }
            if ((circx-circrad)<minX){
              minX=(circx-circrad);
            }
            if ((circy-circrad)<minY){
              minY=(circy-circrad);
            }
          }
          else {
            scount ++;

          }
        }
        // For case where they are all singular
        if (scount==circles.length){
          maxX = 0;
          maxY = 0;
          minX = 0;
          minY = 0;
        }

        return new double[]{minX,minY,maxX,maxY};
    }

  
    // =======================================================
    // Tester - tests methods defined in this class
    // =======================================================

    /**
     * Your tester function should go here (see week 14 lecture notes if
     * you're confused). It is not tested by BOSS, but you will receive extra
     * credit if it is implemented in a sensible fashion.
     */
    public static void main(String args[]){
        // Tests
        // Testing student data
        Project1 TestObj = new Project1();
        TestObj.results("student.data");
        System.out.println("Smax and min are: \n"+TestObj.Smax+"\n"+TestObj.Smin);
        System.out.println("Num of non-singular circles are: "+TestObj.circleCounter);
        System.out.println("Average Circle Area is: "+TestObj.areaAverage);
        System.out.println("Area SD is: "+TestObj.areaSD);
        System.out.println("Median area is: "+TestObj.areaMedian);
        System.out.println("Rectangle coords as string are: "+Arrays.toString(TestObj.aabb));

        // Testing methods with 4 random circles 
        Project1 Testobj2 = new Project1();
        Circle[] TestArray = {new Circle(4,5,1.2), new Circle(6,-3,0.00000000000002), new Circle(3.44455,5.6,4.3), new Circle(0.45,2,8.6456)};
        System.out.println("Average circle Area is: "+Testobj2.averageCircleArea(TestArray));
        System.out.println("SDev Area is: "+Testobj2.areaStandardDeviation(TestArray));
        System.out.println("Rectangle coords as string are: "+Arrays.toString(Testobj2.calculateAABB(TestArray)));

        // Testing self created data file
        Project1 TestObj3 = new Project1();
        TestObj3.results("newTest.data");
        System.out.println("Smax and min are: \n"+TestObj3.Smax+"\n"+TestObj3.Smin);
        System.out.println("Num of non-singular circles are: "+TestObj3.circleCounter);
        System.out.println("Average Circle Area is: "+TestObj3.areaAverage);
        System.out.println("Area SD is: "+TestObj3.areaSD);
        System.out.println("Median area is: "+TestObj3.areaMedian);
        System.out.println("Rectangle coords as string are: "+Arrays.toString(TestObj3.aabb));

       
    }
}