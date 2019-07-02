package fun.billon.forum.dao;

import fun.billon.forum.api.model.ForumTopicModel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 论坛话题DAO
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
public interface IForumTopicDAO {

    /**
     * 新增论坛话题
     *
     * @param forumTopicModel
     * @return
     */
    int insertForumTopic(ForumTopicModel forumTopicModel);

    /**
     * 删除论坛话题
     *
     * @param forumTopicModel
     * @return
     */
    int deleteForumTopicByPK(ForumTopicModel forumTopicModel);

    /**
     * 修改论坛话题
     *
     * @param forumTopicModel
     * @return
     */
    int updateForumTopicByPK(ForumTopicModel forumTopicModel);

    /**
     * 根据特定条件查询论坛话题主键集合
     *
     * @param forumTopicModel
     * @return
     */
    List<Integer> queryPKListByCriteria(ForumTopicModel forumTopicModel);

    /**
     * 根据主键获取论坛话题信息
     *
     * @param forumTopicModel
     * @return
     */
    ForumTopicModel queryForumTopicByPK(ForumTopicModel forumTopicModel);
}
