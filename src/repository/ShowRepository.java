package repository;

import pojo.Show;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ShowRepository {
        private final List<Show> registeredShows = new ArrayList<>();

        public void addShow(Show show) {
            registeredShows.add(show);
        }

        public List<Show> getAllShows() {
            return registeredShows;
        }

        public Stream<Show> stream() {
            return registeredShows.stream();
        }
    }
