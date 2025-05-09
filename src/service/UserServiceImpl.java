package service;

import pojo.User;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserServiceImpl {
//    private final UserRepository repository;
//
//    public UserServiceImpl(UserRepository repository) {
//        this.repository = repository;
//    }
//
//    public User getOrCreateUser(String name) {
//        return repository.findByName(name).orElseGet(() -> {
//            User user = new User(name);
//            repository.addUser(user);
//            return user;
//        });
//    }
//
//    private Optional<User> checkIfUserExists(String name) {
//        List<User> allUsers=repository.getAllUsers();
//        return allUsers.stream()
//                .filter(x -> x.getName().equals(name))
//                .findFirst();
//    }

}
