package fun.billon.forum.controller;

import com.alibaba.fastjson.JSONArray;
import fun.billon.common.constant.CommonStatusCode;
import fun.billon.common.exception.ParamException;
import fun.billon.common.model.ResultModel;
import fun.billon.common.util.DateUtils;
import fun.billon.common.util.StringUtils;
import fun.billon.forum.api.model.ForumMediaModel;
import fun.billon.forum.api.model.ForumPostModel;
import fun.billon.forum.api.model.ForumPostUnlockModel;
import fun.billon.forum.api.model.ForumReplyModel;
import fun.billon.forum.service.IForumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 论坛帖子controller
 *
 * @author billon
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/post")
public class PostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Resource
    private IForumService forumService;

    /**
     * 发表帖子
     *
     * @param paramMap paramMap.expiredTime   过期时间(yyyy-MM-dd HH:mm:ss)              选填
     *                 paramMap.uid           用户id                                    必填
     *                 paramMap.title         标题                                      必填
     *                 paramMap.content       内容                                      必填
     *                 paramMap.lat           纬度(gps)                                 选填
     *                 paramMap.lng           经度(gps)                                 选填
     *                 paramMap.address       位置信息                                   选填
     *                 paramMap.topicId       话题id                                    必填
     *                 paramMap.status        帖子状态(1:审核中;2:审核通过;3:审核未通过)     选填
     *                 paramMap.limit         限制条件(1:免费观看;2:分享后可见;3:付费后可见)  选填
     *                 paramMap.price         价格                                       选填
     *                 paramMap.payment       支付方式(1:金币;2:现金)                      选填
     *                 paramMap.mediaList     附件                                       必填
     * @return
     */
    @PostMapping
    public ResultModel<Integer> addPost(@RequestParam Map<String, String> paramMap) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"expiredTime", "uid", "title", "content", "lat", "lng", "address", "topicId",
                "status", "limit", "price", "payment", "mediaList"};
        boolean[] requiredArray = new boolean[]{false, true, true, true, false, false, false, true, false, false, false, false, true};
        Class[] classArray = new Class[]{Date.class, Integer.class, String.class, String.class, Double.class, Double.class,
                String.class, Integer.class, Integer.class, Integer.class, Float.class, Integer.class, String.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, new String[]{"yyyy-MM-dd HH:mm:ss"});
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        ForumPostModel forumPostModel = new ForumPostModel();
        if (!StringUtils.isEmpty(paramMap.get("expiredTime"))) {
            forumPostModel.setExpiredTime(DateUtils.parse(paramMap.get("expiredTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        forumPostModel.setUid(Integer.parseInt(paramMap.get("uid")));
        forumPostModel.setTitle(paramMap.get("title"));
        forumPostModel.setContent(paramMap.get("content"));
        if (!StringUtils.isEmpty(paramMap.get("lat"))) {
            forumPostModel.setLat(Double.parseDouble(paramMap.get("lat")));
        }
        if (!StringUtils.isEmpty(paramMap.get("lng"))) {
            forumPostModel.setLng(Double.parseDouble(paramMap.get("lng")));
        }
        forumPostModel.setAddress(paramMap.get("address"));
        forumPostModel.setTopicId(Integer.parseInt(paramMap.get("topicId")));

        int status = ForumPostModel.STATUS_VERIFY_WAITING;
        if (!StringUtils.isEmpty(paramMap.get("status"))) {
            status = Integer.parseInt(paramMap.get("status"));
        }
        forumPostModel.setStatus(status);

        int limit = ForumPostModel.LIMIT_FREE;
        if (!StringUtils.isEmpty(paramMap.get("limit"))) {
            limit = Integer.parseInt(paramMap.get("limit"));
        }
        forumPostModel.setLimit(limit);

        if (!StringUtils.isEmpty(paramMap.get("price"))) {
            forumPostModel.setPrice(Float.parseFloat(paramMap.get("price")));
        }
        if (!StringUtils.isEmpty(paramMap.get("payment"))) {
            forumPostModel.setPayment(Integer.parseInt(paramMap.get("payment")));
        }

        String mediaList = paramMap.get("mediaList");
        try {
            List<ForumMediaModel> forumMediaModels = JSONArray.parseArray(mediaList, ForumMediaModel.class);
            forumPostModel.setMediaList(forumMediaModels);
        } catch (Exception e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, "mediaList解析失败:" + e.getMessage());
            return resultModel;
        }
        return forumService.addPost(forumPostModel);
    }

    /**
     * 发表评论
     *
     * @param postId   帖子id
     * @param paramMap paramMap.uid         用户id        必填
     *                 paramMap.content     内容          必填
     *                 paramMap.refId       管理评论id     选填
     * @return
     */
    @PostMapping(value = "/{postId}/reply")
    public ResultModel<Integer> addReply(@PathVariable(value = "postId") Integer postId,
                                         @RequestParam Map<String, String> paramMap) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        try {
            StringUtils.checkParam(paramMap, new String[]{"uid", "content", "refId"},
                    new boolean[]{true, true, false}, new Class[]{Integer.class, String.class, Integer.class}, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        ForumReplyModel forumReplyModel = new ForumReplyModel();
        forumReplyModel.setPostId(postId);
        forumReplyModel.setUid(Integer.parseInt(paramMap.get("uid")));
        forumReplyModel.setContent(paramMap.get("content"));
        if (!StringUtils.isEmpty(paramMap.get("refId"))) {
            forumReplyModel.setRefId(Integer.parseInt(paramMap.get("refId")));
        }
        return forumService.addReply(forumReplyModel);
    }

    /**
     * 解锁帖子
     *
     * @param postId       帖子id                  必填
     * @param uid          用户id                  必填
     * @param unlockMethod 解锁方式(1:分享;2:付费)  必填
     * @return
     */
    @PostMapping(value = "/{postId}/unlock")
    public ResultModel unlockPost(@PathVariable(value = "postId") Integer postId,
                                  @RequestParam Integer uid,
                                  @RequestParam Integer unlockMethod) {
        ForumPostUnlockModel forumPostUnlockModel = new ForumPostUnlockModel();
        forumPostUnlockModel.setPostId(postId);
        forumPostUnlockModel.setUid(uid);
        forumPostUnlockModel.setUnlockMethod(unlockMethod);
        return forumService.unlockPost(forumPostUnlockModel);
    }

    /**
     * 删除帖子
     *
     * @param postId 帖子id    必填
     * @param uid    用户id    选填
     * @return
     */
    @DeleteMapping(value = "/{postId}")
    public ResultModel deletePost(@PathVariable(value = "postId") Integer postId,
                                  @RequestParam(required = false) Integer uid) {
        ForumPostModel forumPostModel = new ForumPostModel();
        forumPostModel.setId(postId);
        forumPostModel.setUid(uid);
        return forumService.deletePost(forumPostModel);
    }

    /**
     * 删除帖子评论
     *
     * @param postId  帖子id  必填
     * @param replyId 评论id  必填
     * @param uid     用户id  选填
     * @return
     */
    @DeleteMapping(value = "/{postId}/reply/{replyId}")
    public ResultModel deleteReply(@PathVariable(value = "postId") Integer postId,
                                   @PathVariable(value = "replyId") Integer replyId,
                                   @RequestParam(required = false) Integer uid) {
        ForumReplyModel forumReplyModel = new ForumReplyModel();
        forumReplyModel.setPostId(postId);
        forumReplyModel.setId(replyId);
        forumReplyModel.setUid(uid);
        return forumService.deleteReply(forumReplyModel);
    }

    /**
     * 更新帖子
     *
     * @param postId   帖子id
     * @param paramMap paramMap.expiredTime     过期时间                                   选填
     *                 paramMap.title           标题                                      选填
     *                 paramMap.content         内容                                      选填
     *                 paramMap.statue          状态(1:审核中;2:审核通过;3:审核未通过)        选填
     *                 paramMap.limit           限制(1:免费观看;2:分享后可见;3:付费后可见)     选填
     *                 paramMap.price           价格                                      选填
     *                 paramMap.payment         支付方式(1:金币;2:现金)                     选填
     * @return
     */
    @PutMapping(value = "/{postId}")
    public ResultModel updatePost(@PathVariable(value = "postId") Integer postId,
                                  @RequestParam Map<String, String> paramMap) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"expiredTime", "title", "content", "status",
                "limit", "price", "payment"};
        boolean[] requiredArray = new boolean[]{false, false, false, false, false, false, false};
        Class[] classArray = new Class[]{Date.class, String.class, String.class,
                Integer.class, Integer.class, Double.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, new String[]{"yyyy-MM-dd HH:mm:ss"});
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        ForumPostModel forumPostModel = new ForumPostModel();
        forumPostModel.setId(postId);
        if (!StringUtils.isEmpty(paramMap.get("expiredTime"))) {
            forumPostModel.setExpiredTime(DateUtils.parse(paramMap.get("expiredTime"), "yyyy-MM-dd HH:mm:ss"));
        }
        forumPostModel.setTitle(paramMap.get("title"));
        forumPostModel.setContent(paramMap.get("content"));
        if (!StringUtils.isEmpty(paramMap.get("status"))) {
            forumPostModel.setStatus(Integer.parseInt(paramMap.get("status")));
        }
        if (!StringUtils.isEmpty(paramMap.get("limit"))) {
            forumPostModel.setLimit(Integer.parseInt(paramMap.get("limit")));
        }
        if (!StringUtils.isEmpty(paramMap.get("price"))) {
            forumPostModel.setPrice(Float.parseFloat(paramMap.get("price")));
        }
        if (!StringUtils.isEmpty(paramMap.get("payment"))) {
            forumPostModel.setPayment(Integer.parseInt(paramMap.get("payment")));
        }

        return forumService.updatePost(forumPostModel);
    }

    /**
     * 更新媒体文件
     *
     * @param postId   帖子id
     * @param mediaId  附件id
     * @param paramMap paramMap.type           文件类型(1:图片;2:视频)                     选填
     *                 paramMap.cover          视频封面                                   选填
     *                 paramMap.url            文件地址                                   选填
     *                 paramMap.desc           描述                                      选填
     *                 paramMap.sort           排序                                      选填
     *                 paramMap.visiable       是否可见(1:可见;2:不可见)                   选填
     * @return
     */
    @PutMapping(value = "/{postId}/media/{mediaId}")
    public ResultModel updateMedia(@PathVariable(value = "postId") Integer postId,
                                   @PathVariable(value = "mediaId") Integer mediaId,
                                   @RequestParam Map<String, String> paramMap) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"type", "cover", "url", "desc", "sort", "visiable"};
        boolean[] requiredArray = new boolean[]{false, false, false, false, false, false};
        Class[] classArray = new Class[]{Integer.class, String.class, String.class,
                String.class, Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        ForumMediaModel forumMediaModel = new ForumMediaModel();
        forumMediaModel.setId(mediaId);
        forumMediaModel.setPostId(postId);
        if (!StringUtils.isEmpty(paramMap.get("type"))) {
            forumMediaModel.setType(Integer.parseInt(paramMap.get("type")));
        }
        forumMediaModel.setCover(paramMap.get("cover"));
        forumMediaModel.setUrl(paramMap.get("url"));
        forumMediaModel.setDesc(paramMap.get("desc"));
        if (!StringUtils.isEmpty(paramMap.get("sort"))) {
            forumMediaModel.setSort(Integer.parseInt(paramMap.get("sort")));
        }
        if (!StringUtils.isEmpty(paramMap.get("visiable"))) {
            forumMediaModel.setVisiable(Integer.parseInt(paramMap.get("visiable")));
        }

        return forumService.updateMedia(forumMediaModel);
    }

    /**
     * 根据条件帖子数量
     *
     * @param paramMap paramMap.topicId     话题id          选填
     *                 paramMap.uid         要查看的用户id    选填
     */
    @GetMapping(value = "/count")
    public ResultModel<Integer> postCount(@RequestParam Map<String, String> paramMap) {
        ResultModel<Integer> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"topicId", "uid"};
        boolean[] requiredArray = new boolean[]{false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        ForumPostModel forumPostModel = new ForumPostModel();
        String topicId = paramMap.get("topicId");
        if (!StringUtils.isEmpty(topicId)) {
            forumPostModel.setTopicId(Integer.parseInt(topicId));
        }
        String uid = paramMap.get("uid");
        if (!StringUtils.isEmpty(uid)) {
            forumPostModel.setUid(Integer.parseInt(uid));
        }
        return forumService.postCount(forumPostModel);
    }

    /**
     * 获取帖子列表
     *
     * @param paramMap paramMap.topicId     话题id          选填
     *                 paramMap.currentUid  当前用户id       选填
     *                 paramMap.uid         要查看的用户id    选填
     *                 paramMap.pageIndex   分页页码         选填
     *                 paramMap.pageSize    分页大小         选填
     */
    @GetMapping
    public ResultModel<List<ForumPostModel>> posts(@RequestParam Map<String, String> paramMap) {
        ResultModel<List<ForumPostModel>> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"topicId", "currentUid", "uid", "pageIndex", "pageSize"};
        boolean[] requiredArray = new boolean[]{false, false, false, false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class, Integer.class, Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }

        ForumPostModel forumPostModel = new ForumPostModel();
        String topicId = paramMap.get("topicId");
        if (!StringUtils.isEmpty(topicId)) {
            forumPostModel.setTopicId(Integer.parseInt(topicId));
        }
        String currentUid = paramMap.get("currentUid");
        if (!StringUtils.isEmpty(currentUid)) {
            forumPostModel.setCurrentUid(Integer.parseInt(currentUid));
        }
        String uid = paramMap.get("uid");
        if (!StringUtils.isEmpty(uid)) {
            forumPostModel.setUid(Integer.parseInt(uid));
        }
        String pageIndex = paramMap.get("pageIndex");
        if (!StringUtils.isEmpty(pageIndex)) {
            forumPostModel.setPageIndex(Integer.parseInt(pageIndex));
        }
        String pageSize = paramMap.get("pageSize");
        if (!StringUtils.isEmpty(pageSize)) {
            forumPostModel.setPageSize(Integer.parseInt(pageSize));
        }
        return forumService.posts(forumPostModel);
    }

    /**
     * 获取帖子详情
     *
     * @param postId   帖子id                                         必填
     * @param paramMap paramMap.currentUid      当前用户的id           选填
     *                 paramMap.requireExtend   是否需要帖子扩展信息     选填
     *                 paramMap.requireReplies  是否需要评论列表        选填
     *                 paramMap.requireUnlocks  是否需要解锁记录列表     选填
     * @return
     */
    @GetMapping(value = "/{postId}")
    public ResultModel<ForumPostModel> postDetail(@PathVariable(value = "postId") Integer postId,
                                                  @RequestParam Map<String, String> paramMap) {
        ResultModel<ForumPostModel> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"currentUid", "requireExtend", "requireReplies", "requireUnlocks"};
        boolean[] requiredArray = new boolean[]{false, false, false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class, Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        ForumPostModel forumPostModel = new ForumPostModel();
        forumPostModel.setId(postId);
        String currentUid = paramMap.get("currentUid");
        if (!StringUtils.isEmpty(currentUid)) {
            forumPostModel.setCurrentUid(Integer.parseInt(currentUid));
        }
        String requireExtend = paramMap.get("requireExtend");
        if (!StringUtils.isEmpty(requireExtend)) {
            if (Integer.parseInt(requireExtend) == 1) {
                forumPostModel.setRequireExtend(true);
            } else {
                forumPostModel.setRequireExtend(false);
            }
        }
        String requireReplies = paramMap.get("requireReplies");
        if (!StringUtils.isEmpty(requireReplies)) {
            if (Integer.parseInt(requireReplies) == 1) {
                forumPostModel.setRequireReplies(true);
            } else {
                forumPostModel.setRequireReplies(false);
            }
        }
        String requirePraises = paramMap.get("requireUnlocks");
        if (!StringUtils.isEmpty(requirePraises)) {
            if (Integer.parseInt(requirePraises) == 1) {
                forumPostModel.setRequireUnlocks(true);
            } else {
                forumPostModel.setRequireUnlocks(false);
            }
        }
        return forumService.postDetail(forumPostModel);
    }

    /**
     * 获取评论列表
     *
     * @param postId   帖子id                        必填
     * @param paramMap paramMap.pageIndex  分页页码   选填
     *                 paramMap.pageSize   分页大小   选填
     * @return
     */
    @GetMapping(value = "/{postId}/reply")
    public ResultModel<List<ForumReplyModel>> getPostReplyList(@PathVariable(value = "postId") Integer postId,
                                                               @RequestParam Map<String, String> paramMap) {
        ResultModel<List<ForumReplyModel>> resultModel = new ResultModel<>();
        String[] paramArray = new String[]{"pageIndex", "pageSize"};
        boolean[] requiredArray = new boolean[]{false, false};
        Class[] classArray = new Class[]{Integer.class, Integer.class};
        try {
            StringUtils.checkParam(paramMap, paramArray, requiredArray, classArray, null);
        } catch (ParamException e) {
            resultModel.setFailed(CommonStatusCode.PARAM_INVALID, e.getMessage());
            return resultModel;
        }
        ForumReplyModel forumReplyModel = new ForumReplyModel();
        forumReplyModel.setPostId(postId);
        String pageIndex = paramMap.get("pageIndex");
        if (!StringUtils.isEmpty(pageIndex)) {
            forumReplyModel.setPageIndex(Integer.parseInt(pageIndex));
        }
        String pageSize = paramMap.get("pageSize");
        if (!StringUtils.isEmpty(pageSize)) {
            forumReplyModel.setPageSize(Integer.parseInt(pageSize));
        }
        return forumService.replies(forumReplyModel);
    }

}
