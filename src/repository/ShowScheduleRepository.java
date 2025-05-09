package repository;

import pojo.Show;
import pojo.ShowSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ShowScheduleRepository {
    private final List<ShowSchedule> showSchedules = new ArrayList<>();

    public void addSchedule(ShowSchedule schedule) {
        showSchedules.add(schedule);
    }

    public List<ShowSchedule> getAllSchedules() {
        return showSchedules;
    }

    public Stream<ShowSchedule> stream() {
        return showSchedules.stream();
    }
}
