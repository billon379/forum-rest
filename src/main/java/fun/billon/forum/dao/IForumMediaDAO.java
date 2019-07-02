package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumMediaModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子相关的媒体文件DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumMediaDAO {

    /**
     * 批量新增帖子相关的媒体文件
     *
     * @param list
     * @return
     */
    int insertForumMedias(List<ForumMediaModel> list);

    /**
     * 删除帖子相关的媒体文件
     *
     * @param forumMediaModel
     * @return
     */
    int deleteForumMediaByPK(ForumMediaModel forumMediaModel);

    /**
     * 修帖子相关的媒体文件
     *
     * @param forumMediaModel
     * @return
     */
    int updateForumMediaByPK(ForumMediaModel forumMediaModel);

    /**
     * 根据特定条件查询论坛帖子主键集合
     *
     * @param forumMediaModel
     * @return
     */
    List<Integer> queryPKListByCriteria(ForumMediaModel forumMediaModel);

    /**
     * 根据主键获取论坛帖子信息
     *
     * @param forumMediaModel
     * @return
     */
    ForumMediaModel queryForumMediaByPK(ForumMediaModel forumMediaModel);
}
