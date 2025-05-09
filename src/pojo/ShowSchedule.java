package pojo;

import java.util.Objects;

public class ShowSchedule {
    private Show show;
    private BookingSlot slot;
    private Screen screen;

    public ShowSchedule(Show show, BookingSlot slot, Screen screen) {
        this.show = show;
        this.slot = slot;
        this.screen = screen;
    }

    public Show getShow() {
        return show;
    }

    public BookingSlot getSlot() {
        return slot;
    }

    public Screen getScreen() {
        return screen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowSchedule that = (ShowSchedule) o;
        return Objects.equals(show, that.show) &&
                Objects.equals(slot, that.slot) &&
                Objects.equals(screen, that.screen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(show, slot, screen);
    }

    @Override
    public String toString() {
        return "\"" + show.getShowName() + "\": " +
                "(" + slot.toString() + ") " +
                screen.getScreenNumber();
    }
}

