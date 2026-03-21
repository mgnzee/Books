package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.DTOs.CommentResponse;
import ru.vladmz.books.DTOs.CommentWithRepliesAmount;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
    Integer getRepliesCount(@Param("commentId") Integer commentId);

    @Query("SELECT c, (SELECT COUNT(r) FROM Comment r WHERE r.parentComment.id = c.id) " +
            "FROM Comment c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.targetType = :type AND c.targetId = :targetId AND c.parentComment IS NULL")
    List<Object[]> findCommentsWithRepliesAmount(TargetType type, Integer targetId);

    @Query("SELECT c, (SELECT COUNT(r) FROM Comment r WHERE r.parentComment.id = c.id) " +
            "FROM Comment c " +
            "WHERE c.targetType = :type AND c.targetId = :targetId AND c.parentComment.id = :parentCommentId")
    List<Object[]> findReplies(TargetType type, Integer targetId, Integer parentCommentId);

    /**
     * Finds comments by target and id of the comment.
     * Target is needed to prevent accessing comment from wrong path
     * @param commentId id of the comment
     * @param targetType TargetType value which can be BOOK, BOOKSHELF, COMMENT
     * @param targetId id of the target
     * @return Optional Comment
     * **/
    @Query("SELECT new ru.vladmz.books.DTOs.CommentResponse(c, " +
            "(SELECT COUNT(r) FROM Comment r WHERE r.parentComment.id = c.id)) " +
            "FROM Comment c " +
            "WHERE c.id = :commentId AND c.targetType = :targetType AND c.targetId = :targetId")
    Optional<CommentResponse> findByIdAndTarget(@Param("commentId") Integer commentId,
                                        @Param("targetType") TargetType targetType,
                                        @Param("targetId") Integer targetId);



}
