interface Payable {
    double calculateSalary();
}

abstract class Employee implements Payable {
    private final String name;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void printSalary() {
        System.out.println(getName() + " - luong: " + calculateSalary());
    }
}

class FullTimeEmployee extends Employee {
    private final double baseSalary;
    private final double coefficient;

    public FullTimeEmployee(String name, double baseSalary, double coefficient) {
        super(name);
        this.baseSalary = baseSalary;
        this.coefficient = coefficient;
    }

    @Override
    public double calculateSalary() {
        return baseSalary * coefficient;
    }
}

class PartTimeEmployee extends Employee {
    private final int hours;
    private final double hourlySalary;

    public PartTimeEmployee(String name, int hours, double hourlySalary) {
        super(name);
        this.hours = hours;
        this.hourlySalary = hourlySalary;
    }

    @Override
    public double calculateSalary() {
        return hours * hourlySalary;
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }
}

public class OopDemo {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTimeEmployee("Nguyen Van A", 10000000, 1.5),
            new PartTimeEmployee("Tran Thi B", 100, 50000)
        };

        double total = 0;
        for (Employee employee : employees) {
            employee.printSalary();
            total += employee.calculateSalary();
        }

        Calculator calculator = new Calculator();

        System.out.println("Tong luong: " + total);
        System.out.println("Cong int: " + calculator.add(2, 3));
        System.out.println("Cong double: " + calculator.add(2.5, 3.5));
    }
}
