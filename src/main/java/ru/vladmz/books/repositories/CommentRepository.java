package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Comment;
import ru.vladmz.books.etc.TargetType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {


    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
    Integer getRepliesCount(@Param("commentId") Integer commentId);

    List<Comment> findByTargetTypeAndTargetIdAndParentCommentIsNull(TargetType type, Integer targetId);

    List<Comment> findByTargetTypeAndTargetIdAndParentCommentId(TargetType type, Integer targetId, Integer parentCommentId);

    /**
     * Finds comments by target and id of the comment.
     * Target is needed to prevent accessing comment from wrong path
     * @param commentId Id of the comment
     * @param targetType TargetType value which can be BOOK, BOOKSHELF, COMMENT
     * @param targetId Id of the target
     * @return Optional Comment
     * **/
    @Query("SELECT c FROM Comment c WHERE c.id = :commentId " +
            "AND c.targetType = :targetType AND c.targetId = :targetId")
    Optional<Comment> findByIdAndTarget(@Param("commentId") Integer commentId,
                                        @Param("targetType") TargetType targetType,
                                        @Param("targetId") Integer targetId);



}
