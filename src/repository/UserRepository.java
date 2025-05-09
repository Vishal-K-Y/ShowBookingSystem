package repository;

import pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class UserRepository {
    private final List<User> registeredUser = new ArrayList<>();

    public void addUser(User user) {
        registeredUser.add(user);
    }

    public List<User> getAllUsers() {
        return registeredUser;
    }

    public Optional<User> findByName(String name) {
        return registeredUser.stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Stream<User> stream() {
        return registeredUser.stream();
    }
}
