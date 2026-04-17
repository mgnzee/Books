package ru.vladmz.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Deprecated
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
    Integer getRepliesCount(@Param("commentId") Integer commentId);

    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.targetType = :type AND c.targetId = :targetId AND c.parentComment IS NULL")
    Page<Comment> findAllByIdAndTargetId(TargetType type, Integer targetId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.parentComment " +
            "WHERE c.targetType = :type AND c.targetId = :targetId AND c.parentComment.id = :parentCommentId")
    Page<Comment> findReplies(TargetType type, Integer targetId, Integer parentCommentId, Pageable pageable);

    /**
     * Finds comments by target and id of the comment.
     * Target is needed to prevent accessing comment from wrong path
     * @param commentId id of the comment
     * @param targetType TargetType value which can be BOOK, BOOKSHELF, COMMENT
     * @param targetId id of the target
     * @return Optional Comment
     * **/
    @Query("SELECT c " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.id = :commentId AND c.targetType = :targetType AND c.targetId = :targetId")
    Optional<Comment> findByIdAndTarget(@Param("commentId") Integer commentId,
                                        @Param("type") TargetType targetType,
                                        @Param("id") Integer targetId);

    @Modifying
    @Query("UPDATE Comment c SET c.repliesCount = c.repliesCount + 1 WHERE c.id = :commentId")
    void incrementCommentCount(Integer commentId);

    @Modifying
    @Query("UPDATE Comment c SET c.repliesCount = greatest(c.repliesCount - 1, 0) WHERE c.id = :commentId")
    void decrementCommentCount(Integer commentId);

}
