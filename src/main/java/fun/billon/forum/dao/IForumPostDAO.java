package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumPostModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 论坛帖子DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumPostDAO {

    /**
     * 新增论坛帖子
     *
     * @param forumPostModel
     * @return
     */
    int insertForumPost(ForumPostModel forumPostModel);

    /**
     * 删除论坛帖子
     *
     * @param forumPostModel
     * @return
     */
    int deleteForumPostByPK(ForumPostModel forumPostModel);

    /**
     * 修改论坛帖子
     *
     * @param forumPostModel
     * @return
     */
    int updateForumPostByPK(ForumPostModel forumPostModel);

    /**
     * 根据特定条件查询论坛帖子主键集合
     *
     * @param forumPostModel
     * @return
     */
    int queryPKListCountByCriteria(ForumPostModel forumPostModel);

    /**
     * 根据条件查询记录总数
     *
     * @param forumPostModel
     * @return
     */
    List<Integer> queryPKListByCriteria(ForumPostModel forumPostModel);

    /**
     * 根据主键获取论坛帖子信息
     *
     * @param forumPostModel
     * @return
     */
    ForumPostModel queryForumPostByPK(ForumPostModel forumPostModel);
}
