package fun.billon.forum.service;

import fun.billon.common.model.ResultModel;
import fun.billon.forum.api.model.ForumMediaModel;
import fun.billon.forum.api.model.ForumPostModel;
import fun.billon.forum.api.model.ForumPostUnlockModel;
import fun.billon.forum.api.model.ForumReplyModel;

import java.util.List;

/**
 * 论坛服务接口
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
public interface IForumService {

    /**
     * 发表帖子
     *
     * @param forumPostModel forumPostModel.expiredTime   过期时间(yyyy-MM-dd HH:mm:ss)              选填
     *                       forumPostModel.uid           用户id                                    必填
     *                       forumPostModel.title         标题                                      必填
     *                       forumPostModel.content       内容                                      必填
     *                       forumPostModel.lat           纬度(gps)                                 选填
     *                       forumPostModel.lng           经度(gps)                                 选填
     *                       forumPostModel.address       位置信息                                   选填
     *                       forumPostModel.topicId       话题id                                    必填
     *                       forumPostModel.status        帖子状态(1:审核中;2:审核通过;3:审核未通过)     选填
     *                       forumPostModel.limit         限制条件(1:免费观看;2:分享后可见;3:付费后可见)  选填
     *                       forumPostModel.price         价格                                       选填
     *                       forumPostModel.payment       支付方式(1:金币;2:现金)                      选填
     *                       forumPostModel.mediaList     附件                                       必填
     * @return resultModel
     */
    ResultModel<Integer> addPost(ForumPostModel forumPostModel);

    /**
     * 发表帖子评论
     *
     * @param forumReplyModel forumReplyModel.postId  帖子id  必填
     *                        forumReplyModel.uid     用户id   必填
     *                        forumReplyModel.refId   回复评论id  选填
     *                        forumReplyModel.content 评论内容  必填
     * @return
     */
    ResultModel<Integer> addReply(ForumReplyModel forumReplyModel);

    /**
     * 解锁帖子
     *
     * @param forumPostUnlockModel forumPostUnlockModel.postId       帖子id   必填
     *                             forumPostUnlockModel.uid          用户id  必填
     *                             forumPostUnlockModel.unlockMethod 解锁方式(1:分享;2:付费)  必填
     * @return
     */
    ResultModel unlockPost(ForumPostUnlockModel forumPostUnlockModel);

    /**
     * 删除帖子
     *
     * @param forumPostModel forumPostModel.id 帖子id  必填
     *                       forumPostModel.uid    用户id  选填
     * @return
     */
    ResultModel deletePost(ForumPostModel forumPostModel);

    /**
     * 删除帖子评论
     *
     * @param forumReplyModel forumReplyModel.id     评论id   必填
     *                        forumReplyModel.postId  帖子id  必填
     *                        forumReplyModel.uid    用户id  选填
     * @return
     */
    ResultModel deleteReply(ForumReplyModel forumReplyModel);


    /**
     * 更新贴子
     *
     * @param forumPostModel forumPostModel.id              帖子id                                    必填
     *                       forumPostModel.title           标题                                      选填
     *                       forumPostModel.content         内容                                      选填
     *                       forumPostModel.statue          状态(1:审核中;2:审核通过;3:审核未通过)        选填
     *                       forumPostModel.limit           限制(1:免费观看;2:分享后可见;3:付费后可见)     选填
     *                       forumPostModel.price           价格                                      选填
     *                       forumPostModel.expiredTime     过期时间                                   选填
     *                       forumPostModel.payment         支付方式(1:金币;2:现金)                     选填
     * @return
     */
    ResultModel updatePost(ForumPostModel forumPostModel);

    /**
     * 更新媒体文件
     *
     * @param forumMediaModel forumMediaModel.id            附件id                                    必填
     *                        forumPostModel.type           文件类型(1:图片;2:视频)                     选填
     *                        forumPostModel.cover          视频封面                                   选填
     *                        forumPostModel.url            文件地址                                   选填
     *                        forumPostModel.desc           描述                                      选填
     *                        forumPostModel.sort           排序                                      选填
     *                        forumPostModel.visiable       是否可见(1:可见;2:不可见)                   选填
     * @return
     */
    ResultModel updateMedia(ForumMediaModel forumMediaModel);

    /**
     * 根据条件帖子数量
     *
     * @param forumPostModel forumPostModel.topicId 话题id 选填
     *                       forumPostModel.uid 用户id 选填
     * @return
     */
    ResultModel<Integer> postCount(ForumPostModel forumPostModel);

    /**
     * 获取多条件帖子信息
     *
     * @param forumPostModel forumPostModel.topicId 话题id 选填
     *                       forumPostModel.uid 用户id 选填
     *                       forumPostModel.pageSize 每页多少条 必填
     *                       forumPostModel.pageIndex 第几页 选填
     * @return
     */
    ResultModel<List<ForumPostModel>> posts(ForumPostModel forumPostModel);

    /**
     * 获取帖子信息
     *
     * @param forumPostModel forumPostModel.id 主键id 必填
     * @return
     */
    ResultModel<ForumPostModel> postDetail(ForumPostModel forumPostModel);

    /**
     * 获取帖子评论
     *
     * @param forumReplyModel forumReplyModel.postId 帖子id 必填
     *                        forumReplyModel.pageSize 每页多少条 必填
     *                        forumReplyModel.pageIndex 第几页 选填
     * @return
     */
    ResultModel<List<ForumReplyModel>> replies(ForumReplyModel forumReplyModel);

}
