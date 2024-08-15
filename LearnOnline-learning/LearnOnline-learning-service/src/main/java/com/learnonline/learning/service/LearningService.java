package com.learnonline.learning.service;

import com.learnonline.base.model.RestResponse;

public interface LearningService {
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
