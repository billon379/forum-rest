package fun.billon.forum.service.impl;

import fun.billon.common.model.ResultModel;
import fun.billon.forum.api.constant.ForumStatusCode;
import fun.billon.forum.api.model.*;
import fun.billon.forum.dao.*;
import fun.billon.forum.service.IForumService;
import fun.billon.member.api.feign.IMemberService;
import fun.billon.member.api.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 论坛服务接口实现
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class ForumServiceImpl implements IForumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumServiceImpl.class);

    /**
     * 内部服务id(sid)
     */
    @Value("${billon.forum.sid}")
    private String sid;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private IForumPostDAO forumPostDAO;

    @Autowired
    private IForumPostExtendDAO forumPostExtendDAO;

    @Autowired
    private IForumMediaDAO forumMediaDAO;

    @Autowired
    private IForumReplyDAO forumReplyDAO;

    @Autowired
    private IForumPostUnlockDAO forumPostUnlockDAO;

    @Autowired
    private IMemberService memberService;

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
    @Override
    public ResultModel<Integer> addPost(ForumPostModel forumPostModel) {
        ResultModel result = new ResultModel();
        /*
         * 拷贝对象
         */
        ForumPostModel forumPostModelCopy = new ForumPostModel();
        BeanUtils.copyProperties(forumPostModel, forumPostModelCopy);

        /*
         * 声明事物
         */
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            //插入帖子内容
            forumPostDAO.insertForumPost(forumPostModelCopy);
            //新增扩展信息记录
            forumPostExtendDAO.insertForumPostExtend(new ForumPostExtendModel(forumPostModelCopy.getId()));
            List<ForumMediaModel> medias = forumPostModel.getMediaList();
            if (null != medias && medias.size() > 0) {
                //有附件则插入附件
                List<ForumMediaModel> mediaModelList = new ArrayList<>();
                for (ForumMediaModel mediaModel : medias) {
                    mediaModel.setPostId(forumPostModelCopy.getId());
                    mediaModelList.add(mediaModel);
                }
                forumMediaDAO.insertForumMedias(mediaModelList);
            }
            transactionManager.commit(status);
            result.setData(forumPostModelCopy.getId());
        } catch (Exception e) {
            LOGGER.error("============ service insertForumPost Exception ==============" + e.getMessage(), e);
            result.setFailed(ForumStatusCode.FORUM_STATUS_OFFSET, e.getMessage());
            transactionManager.rollback(status);
        }
        return result;
    }

    /**
     * 发表帖子评论
     *
     * @param forumReplyModel forumReplyModel.postId  帖子id  必填
     *                        forumReplyModel.uid     用户id   必填
     *                        forumReplyModel.refId   回复评论id  选填
     *                        forumReplyModel.content 评论内容  必填
     * @return
     */
    @Override
    public ResultModel<Integer> addReply(ForumReplyModel forumReplyModel) {
        ResultModel result = new ResultModel();
        /*
         * 声明事物
         */
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            //插入评论内容
            forumReplyDAO.insertForumReply(forumReplyModel);
            /*
             * 更新评论数量
             */
            ForumPostExtendModel forumPostExtendModel = new ForumPostExtendModel();
            forumPostExtendModel.setPostId(forumReplyModel.getPostId());
            forumPostExtendModel.setReplyNum(1);
            forumPostExtendDAO.updateForumPostExtendNumStepedByPK(forumPostExtendModel);
            transactionManager.commit(status);
            result.setData(forumReplyModel.getId());
        } catch (Exception e) {
            LOGGER.error("============ service insertForumReplay Exception ==============" + e.getMessage(), e);
            result.setFailed(ForumStatusCode.FORUM_STATUS_OFFSET, e.getMessage());
            transactionManager.rollback(status);
        }
        return result;
    }

    /**
     * 解锁帖子
     *
     * @param forumPostUnlockModel forumPostUnlockModel.postId       帖子id   必填
     *                             forumPostUnlockModel.uid          用户id  必填
     *                             forumPostUnlockModel.unlockMethod 解锁方式(1:分享;2:付费)  必填
     * @return
     */
    @Override
    public ResultModel unlockPost(ForumPostUnlockModel forumPostUnlockModel) {
        ResultModel result = new ResultModel();
        if (null == forumPostUnlockDAO.queryPKByCriteria(forumPostUnlockModel)) {
            forumPostUnlockDAO.insertForumPostUnlock(forumPostUnlockModel);
        }
        return result;
    }

    /**
     * 删除帖子
     *
     * @param forumPostModel forumPostModel.id 帖子id  必填
     *                       forumPostModel.uid    用户id  选填
     * @return
     */
    @Override
    public ResultModel deletePost(ForumPostModel forumPostModel) {
        ResultModel result = new ResultModel();
        forumPostDAO.deleteForumPostByPK(forumPostModel);
        return result;
    }

    /**
     * 删除帖子评论
     *
     * @param forumReplyModel forumReplyModel.id     评论id   必填
     *                        forumReplyModel.postId  帖子id  必填
     *                        forumReplyModel.uid    用户id  选填
     * @return
     */
    @Override
    public ResultModel deleteReply(ForumReplyModel forumReplyModel) {
        ResultModel result = new ResultModel();
        /*
         * 声明事物
         */
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            //删除评论
            if (forumReplyDAO.deleteForumReplyByPK(forumReplyModel) > 0) {
                /*
                 * 更新评论数量
                 */
                ForumPostExtendModel forumPostExtendModel = new ForumPostExtendModel();
                forumPostExtendModel.setPostId(forumReplyModel.getPostId());
                forumPostExtendModel.setReplyNum(-1);
                forumPostExtendDAO.updateForumPostExtendNumStepedByPK(forumPostExtendModel);
            }

            transactionManager.commit(status);
        } catch (Exception e) {
            LOGGER.error("============ service deleteForumReplay Exception ==============" + e.getMessage(), e);
            result.setFailed(ForumStatusCode.FORUM_STATUS_OFFSET, e.getMessage());
            transactionManager.rollback(status);
        }
        return result;
    }

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
    @Override
    public ResultModel updatePost(ForumPostModel forumPostModel) {
        ResultModel resultModel = new ResultModel();
        forumPostDAO.updateForumPostByPK(forumPostModel);
        return resultModel;
    }

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
    @Override
    public ResultModel updateMedia(ForumMediaModel forumMediaModel) {
        ResultModel resultModel = new ResultModel();
        forumMediaDAO.updateForumMediaByPK(forumMediaModel);
        return resultModel;
    }

    /**
     * 根据条件帖子数量
     *
     * @param forumPostModel forumPostModel.topicId 话题id 选填
     *                       forumPostModel.uid 用户id 选填
     * @return
     */
    @Override
    public ResultModel<Integer> postCount(ForumPostModel forumPostModel) {
        ResultModel<Integer> resultModel = new ResultModel();
        resultModel.setData(forumPostDAO.queryPKListCountByCriteria(forumPostModel));
        return resultModel;
    }

    /**
     * 获取多条件帖子信息
     *
     * @param forumPostModel forumPostModel.topicId     话题id        选填
     *                       forumPostModel.uid         用户id        选填
     *                       forumPostModel.currentUid  当前用户id     选填
     *                       forumPostModel.pageSize    分页大小       必填
     *                       forumPostModel.pageIndex   分页页码       选填
     * @return
     */
    @Override
    public ResultModel<List<ForumPostModel>> posts(ForumPostModel forumPostModel) {
        ResultModel<List<ForumPostModel>> result = new ResultModel<>();

        ForumPostModel forumPostModelCopy = new ForumPostModel();
        BeanUtils.copyProperties(forumPostModel, forumPostModelCopy);

        List<ForumPostModel> forumPosts = new ArrayList<>();
        List<Integer> postIdList = forumPostDAO.queryPKListByCriteria(forumPostModelCopy);
        if (null != postIdList && postIdList.size() > 0) {
            StringBuffer uidBuffer = new StringBuffer();
            for (Integer postId : postIdList) {
                forumPostModelCopy.setId(postId);
                ForumPostModel forumPost = getPost(forumPostModelCopy);
                uidBuffer.append("," + forumPost.getUid());
                forumPosts.add(forumPost);
            }
            /*
             * 获取发帖用户信息
             */
            String uids = uidBuffer.substring(1);
            ResultModel<Map<Integer, UserModel>> userInfoResult = memberService.getUserByIds(sid, sid, uids);
            if (userInfoResult.getCode() == ResultModel.RESULT_SUCCESS) {
                Map<Integer, UserModel> userInfoMap = userInfoResult.getData();
                for (ForumPostModel post : forumPosts) {
                    UserModel userInfo = userInfoMap.get(post.getUid());
                    post.setUser(userInfo);
                }
            }
        }
        result.setData(forumPosts);
        return result;
    }

    /**
     * 获取帖子信息
     *
     * @param forumPostModel forumPostModel.id 主键id 必填
     *                       forumPostModel.uid 用户id 选填
     * @return
     */
    @Override
    public ResultModel<ForumPostModel> postDetail(ForumPostModel forumPostModel) {
        ResultModel<ForumPostModel> result = new ResultModel<>();
        ForumPostModel forumPost = getPost(forumPostModel);
        result.setData(forumPost);
        return result;
    }

    /**
     * 获取帖子评论
     *
     * @param forumReplyModel forumReplyModel.postId 帖子id 必填
     *                        forumReplyModel.pageSize 分页大小 必填
     *                        forumReplyModel.pageIndex 分页页码 选填
     * @return
     */
    @Override
    public ResultModel<List<ForumReplyModel>> replies(ForumReplyModel forumReplyModel) {
        ResultModel<List<ForumReplyModel>> result = new ResultModel<>();
        result.setData(getReplyList(forumReplyModel));
        return result;
    }

    /**
     * 获取帖子基本信息
     *
     * @param forumPostModel forumPostModel.id             帖子id            必填
     *                       forumPostModel.uid             用户id            选填
     *                       forumPostModel.currentUid      当前用户id         选填
     *                       forumPostModel.requireMedias   需要附件信息       选填
     *                       forumPostModel.requireExtend   需要扩展信息       选填
     *                       forumPostModel.requireReplies  需要评论信息        选填
     *                       forumPostModel.requireUnlocks  需要解锁用户信息    选填
     * @return
     */
    private ForumPostModel getPost(ForumPostModel forumPostModel) {
        ForumPostModel forumPost = null;
        if (null != forumPostModel && null != forumPostModel.getId()) {
            forumPost = forumPostDAO.queryForumPostByPK(forumPostModel);
            /*
             * 获取帖子附件信息
             */
            if (forumPostModel.isRequireMedias()) {
                ForumMediaModel forumMediaModel = new ForumMediaModel(forumPostModel.getId());
                List<ForumMediaModel> medias = getPostMediaList(forumMediaModel);
                forumPost.setMediaList(medias);
            }
            /*
             * 获取帖子扩展信息
             */
            if (forumPostModel.isRequireExtend()) {
                ForumPostExtendModel extend = getPostExtend(forumPostModel.getId());
                forumPost.setExtend(extend);
            }
            /*
             * 获取解锁用户列表
             */
            if (forumPostModel.isRequireUnlocks()) {
                ForumPostUnlockModel forumPostUnlockModel = new ForumPostUnlockModel(forumPostModel.getId());
                List<UserModel> unlockList = getUnlockList(forumPostUnlockModel);
                forumPost.setUnlockList(unlockList);
            }
            /*
             * 获取帖子的评论
             */
            if (forumPostModel.isRequireReplies()) {
                ForumReplyModel forumReplyModel = new ForumReplyModel(forumPostModel.getId());
                List<ForumReplyModel> replies = getReplyList(forumReplyModel);
                forumPost.setReplyList(replies);
            }
            /*
             * 当前用户是否已经解锁该帖子
             */
            if (null != forumPostModel.getCurrentUid()
                    && forumPostModel.getCurrentUid() > 0) {
                if (forumPost.getLimit() == ForumPostModel.LIMIT_FREE
                        || forumPost.getUid() == forumPostModel.getCurrentUid()) {
                    // 免费帖或者用户自己发的帖子不需要解锁
                    forumPost.setUnlock(ForumPostModel.UNLOCKED);
                } else {
                    // 分享、支付后可看且非用户自己发的帖子,需要查看用户解锁状态
                    Integer unlockId = forumPostUnlockDAO.queryPKByCriteria(
                            new ForumPostUnlockModel(forumPostModel.getId(),
                                    forumPostModel.getCurrentUid(),
                                    forumPost.getLimit()));
                    if (unlockId != null) {
                        forumPost.setUnlock(ForumPostModel.UNLOCKED);
                    }
                }
            }
        }
        return forumPost;
    }

    /**
     * 获取某论坛帖子的附件信息
     *
     * @param forumMediaModel forumMediaModel.postId 帖子id 必填
     * @return
     */
    private List<ForumMediaModel> getPostMediaList(ForumMediaModel forumMediaModel) {
        List<ForumMediaModel> medias = new ArrayList<>();
        List<Integer> mediaIdList = forumMediaDAO.queryPKListByCriteria(forumMediaModel);
        if (null != mediaIdList && mediaIdList.size() > 0) {
            for (Integer mediaId : mediaIdList) {
                forumMediaModel.setId(mediaId);
                ForumMediaModel forumMedia = forumMediaDAO.queryForumMediaByPK(forumMediaModel);
                medias.add(forumMedia);
            }
        }
        return medias;
    }

    /**
     * 获取某论坛帖子的扩展信息
     *
     * @param postId
     * @return
     */
    private ForumPostExtendModel getPostExtend(int postId) {
        ForumPostExtendModel postExtend = new ForumPostExtendModel();
        postExtend.setPostId(postId);
        return forumPostExtendDAO.queryForumPostExtendByPK(postExtend);
    }

    /**
     * 获取解锁用户信息
     *
     * @param forumPostUnlockModel forumPostUnlockModel.postId 帖子id 必填
     * @return
     */
    private List<UserModel> getUnlockList(ForumPostUnlockModel forumPostUnlockModel) {
        List<UserModel> unlockList = new ArrayList<>();
        List<Integer> unlockIdList = forumPostUnlockDAO.queryUidListByCriteria(forumPostUnlockModel);
        if (null != unlockIdList && unlockIdList.size() > 0) {
            StringBuffer uidBuffer = new StringBuffer();
            for (Integer uid : unlockIdList) {
                uidBuffer.append("," + uid);
            }
            /**
             * 获取用户信息
             */
            String uids = uidBuffer.substring(1);
            ResultModel<Map<Integer, UserModel>> userResult = memberService.getUserByIds(sid, sid, uids);
            if (userResult.getCode() == ResultModel.RESULT_SUCCESS) {
                Map<Integer, UserModel> userMap = userResult.getData();
                unlockList.addAll(userMap.values());
            }
        }
        return unlockList;
    }

    /**
     * 获取帖子评论
     *
     * @param forumReplyModel forumReplyModel.postId  帖子id  必填
     *                        forumReplyModel.pageSize  分页大小  选填
     *                        forumReplyModel.pageIndex  分页页码  选填
     * @return
     */
    private List<ForumReplyModel> getReplyList(ForumReplyModel forumReplyModel) {
        ForumReplyModel forumReplyModelCopy = new ForumReplyModel();
        BeanUtils.copyProperties(forumReplyModel, forumReplyModelCopy);
        List<ForumReplyModel> replies = new ArrayList<>();
        List<Integer> replyIdList = forumReplyDAO.queryPKListByCriteria(forumReplyModelCopy);
        if (null != replyIdList && replyIdList.size() > 0) {
            StringBuffer sbUids = new StringBuffer();
            for (Integer replyId : replyIdList) {
                forumReplyModelCopy.setId(replyId);
                ForumReplyModel forumReply = forumReplyDAO.queryForumReplyByPK(forumReplyModelCopy);
                Integer refId = forumReply.getRefId();
                if (null != refId && refId > 0L) {
                    forumReplyModelCopy.setId(refId);
                    ForumReplyModel refForumReply = forumReplyDAO.queryForumReplyByPK(forumReplyModelCopy);
                    forumReply.setReplyRef(refForumReply);
                    sbUids.append("," + refForumReply.getUid());
                }
                sbUids.append("," + forumReply.getUid());
                replies.add(forumReply);
            }
            /**
             * 获取用户信息
             */
            String uids = sbUids.substring(1);
            ResultModel<Map<Integer, UserModel>> userResult = memberService.getUserByIds(sid, sid, uids);
            if (userResult.getCode() == ResultModel.RESULT_SUCCESS) {
                Map<Integer, UserModel> userMap = userResult.getData();
                for (ForumReplyModel reply : replies) {
                    UserModel um = userMap.get(reply.getUid());
                    reply.setUser(um);
                    ForumReplyModel replyRef = reply.getReplyRef();
                    if (replyRef != null) {
                        replyRef.setUser(userMap.get(replyRef.getUid()));
                    }
                }
            }
        }
        return replies;
    }

}