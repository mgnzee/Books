package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.entities.Follower;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.AlreadySubscribedException;
import ru.vladmz.books.exceptions.SelfSubscriptionException;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.FollowerRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.CurrentUserProvider;

import java.util.List;

@Service
@Transactional
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider provider;

    @Autowired
    public FollowerService(FollowerRepository followerRepository, UserRepository userRepository, CurrentUserProvider provider) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.provider = provider;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFollowersByUserId(Integer userId){
        if(!userRepository.existsById(userId)) throw new UserNotFoundException(userId);
        return userRepository.findAllFollowers(userId).stream().map(UserMapper::toResponse).toList();
    }

    public void subscribe(Integer userId){
        if(!userRepository.existsById(userId)) throw new UserNotFoundException(userId);
        User user = userRepository.getReferenceById(userId);
        User current = provider.get();
        if(userId.equals(current.getId())) throw new SelfSubscriptionException("You can't subscribe to yourself");
        if(followerRepository.existsByUserIdAndFollowerId(userId, current.getId())) throw new AlreadySubscribedException(current.getId(), userId);
        followerRepository.save(new Follower(user, current));
    }

    public void unsubscribe(Integer userId){
        if(!userRepository.existsById(userId)) throw new UserNotFoundException(userId);
        User current = provider.get();
        followerRepository.deleteByUserIdAndFollowerId(userId, current.getId());
    }
}
