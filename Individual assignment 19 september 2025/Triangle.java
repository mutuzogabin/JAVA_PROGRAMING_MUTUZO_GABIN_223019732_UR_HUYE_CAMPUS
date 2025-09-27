package individual_assisgnment.entities;

public class Triangle {
    private final double height;
    private final double base;
    private final double hypotenuse;
    public Triangle(double height, double base) {
        this.height = height;
        this.base = base;
        this.hypotenuse = Math.sqrt(Math.pow(height, 2) + Math.pow(base, 2));
    }
    public double calculatePerimeter() {
        return hypotenuse + base + height;
    }
    public double calculateArea() {
        return base * height / 2;
    }
    public double getHeight() {
        return height;
    }

    public double getBase() {
        return base;
    }

    public double getHypotenuse() {
        return hypotenuse;
    }
}
