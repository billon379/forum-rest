package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumReplyModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 论坛评论记录DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumReplyDAO {

    /**
     * 新增论坛回复记录
     *
     * @param forumReplyModel
     * @return
     */
    int insertForumReply(ForumReplyModel forumReplyModel);

    /**
     * 删除论坛回复记录
     *
     * @param forumReplyModel
     * @return
     */
    int deleteForumReplyByPK(ForumReplyModel forumReplyModel);

    /**
     * 根据特定条件查询评论坛回复记录主键集合
     *
     * @param forumReplyModel
     * @return
     */
    List<Integer> queryPKListByCriteria(ForumReplyModel forumReplyModel);

    /**
     * 根据主键获取论坛回复记录
     *
     * @param forumReplyModel
     * @return
     */
    ForumReplyModel queryForumReplyByPK(ForumReplyModel forumReplyModel);
}
