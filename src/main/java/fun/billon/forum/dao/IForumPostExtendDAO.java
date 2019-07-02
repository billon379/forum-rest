package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumPostExtendModel;
import org.springframework.stereotype.Repository;

/**
 * 帖子数量统计DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumPostExtendDAO {

    /**
     * 新增帖子数量统计
     *
     * @param forumPostExtendModel
     * @return
     */
    int insertForumPostExtend(ForumPostExtendModel forumPostExtendModel);

    /**
     * 修改帖子数量统计
     *
     * @param forumPostExtendModel
     * @return
     */
    int updateForumPostExtendByPK(ForumPostExtendModel forumPostExtendModel);

    /**
     * 修改帖子数量统计 数字加减
     *
     * @param forumPostExtendModel
     * @return
     */
    int updateForumPostExtendNumStepedByPK(ForumPostExtendModel forumPostExtendModel);

    /**
     * 根据主键获取帖子数量统计
     *
     * @param forumPostExtendModel
     * @return
     */
    ForumPostExtendModel queryForumPostExtendByPK(ForumPostExtendModel forumPostExtendModel);

}
