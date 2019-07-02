package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumPostUnlockModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子解锁记录DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumPostUnlockDAO {

    /**
     * 新增解锁记录
     *
     * @param forumPostUnlockModel
     * @return
     */
    int insertForumPostUnlock(ForumPostUnlockModel forumPostUnlockModel);

    /**
     * 根据条件查询帖子的解锁用户列表
     *
     * @param forumPostUnlockModel forumPostUnlockModel.post 必填
     * @return 帖子的解锁用户列表uidList
     */
    List<Integer> queryUidListByCriteria(ForumPostUnlockModel forumPostUnlockModel);

    /**
     * 根据条件查询用户解锁的帖子列表
     *
     * @param forumPostUnlockModel forumPostUnlockModel.uid 必填
     * @return 用户解锁的帖子列表postIdList
     */
    List<Integer> queryPostIdListByCriteria(ForumPostUnlockModel forumPostUnlockModel);

    /**
     * 当前用户是否解锁
     *
     * @param forumPostUnlockModel forumPostUnlockModel.uid           必填
     *                             forumPostUnlockModel.postId        必填
     *                             forumPostUnlockModel.unlockMethod  必填
     * @return 当前用户是否解锁
     */
    Integer queryPKByCriteria(ForumPostUnlockModel forumPostUnlockModel);
}
