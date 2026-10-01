package com.eduquest.service;

import com.eduquest.domain.Activity;
import com.eduquest.domain.ActivityType;
import org.springframework.stereotype.Service;

@Service
public class ActivityCompletionService {

    public boolean isActivityCompleted(Activity activity, Integer score, boolean requestedCompleted) {
        if (activity == null) return false;

        if (activity.getActivityType() == ActivityType.LESSON) {
            return requestedCompleted;
        } else if (activity.getActivityType() == ActivityType.QUIZ) {
            // Quiz requires Score >= 50%
            return (score != null && score >= 50);
        } else {
            // Placeholder / Game types require score >= 50 or requested completion
            return requestedCompleted || (score != null && score >= 50);
        }
    }
}
