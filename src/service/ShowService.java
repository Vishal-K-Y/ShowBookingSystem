package service;

import pojo.Show;
import repository.ShowRepository;

import java.util.Optional;

public class ShowService {
    private ShowRepository repository;

    public ShowService(ShowRepository repository) {
        this.repository = repository;
    }

    public void registerShow(String showName, String genre) {
        repository.addShow(new Show(showName, genre));
        System.out.println(showName + " show is registered!");
    }

    public Optional<Show> getShowByName(String name) {
        return repository.stream()
                .filter(show -> show.getShowName().equals(name))
                .findFirst();
    }
}
