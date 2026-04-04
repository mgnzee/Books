package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Follower;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Integer> {


    long countByUserId(Integer userId);

    boolean existsByUserIdAndFollowerId(Integer userId, Integer followerId);

    @Modifying
    @Query("delete from Follower f where f.user.id = :userId and f.follower.id = :followerId")
    void deleteByUserIdAndFollowerId(Integer userId, Integer followerId);
}
