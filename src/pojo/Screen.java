package pojo;

public class Screen {
    int screenNumber;
    int capacity;

    public Screen(int screenNumber, int capacity){
        this.screenNumber=screenNumber;
        this.capacity=capacity;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return String.valueOf(screenNumber);
    }
}
