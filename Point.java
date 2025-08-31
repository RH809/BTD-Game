/**
 * Point class used to store x and y position of game objects
 */
public class Point {

    private double x;
    private double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public static double distance(Point a, Point b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public boolean equals(Object o){
        if(!(o instanceof Point)) return false;
        Point p = (Point)o;
        return p.x == x && p.y == y;
    }
}
